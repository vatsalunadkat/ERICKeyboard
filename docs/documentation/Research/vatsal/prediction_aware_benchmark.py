#!/usr/bin/env python3

from __future__ import annotations

import argparse
import re
from collections import Counter, defaultdict
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable


SCRIPT_DIR = Path(__file__).resolve().parent
RESULTS_DIR = SCRIPT_DIR / "results_and_logs"
BENCHMARK_PACK_DIR = SCRIPT_DIR / "benchmark_packs"
REPO_ROOT = SCRIPT_DIR.parents[3]
WORD_ENGINE_PATH = REPO_ROOT / "android/shared/src/commonMain/kotlin/WordPredictionEngine.kt"

WORD_RE = re.compile(r"[A-Za-z0-9']+")
ROW_RE = re.compile(r"^\s*(N|NE|E|SE|S|SW|W|NW)\s+\|\s+(.+)$")

DEFAULT_LAYOUTS = [
    ("6_default", RESULTS_DIR / "optimization_results_6section_branch2_default_probe_2026-04-26.txt"),
    ("6_bigram_up", RESULTS_DIR / "optimization_results_6section_branch2_bigram_up_probe_2026-04-26.txt"),
    ("6_trigram_up", RESULTS_DIR / "optimization_results_6section_branch2_trigram_up_probe_2026-04-26.txt"),
    ("8_default", RESULTS_DIR / "optimization_results_8section_branch2_default_probe_2026-04-26.txt"),
    ("8_bigram_up", RESULTS_DIR / "optimization_results_8section_branch2_bigram_up_probe_2026-04-26.txt"),
    ("8_trigram_up", RESULTS_DIR / "optimization_results_8section_branch2_trigram_up_probe_2026-04-26.txt"),
]


@dataclass
class WordOccurrence:
    word: str
    preceding_delimiter: str
    following_delimiter: str
    previous_word: str | None


@dataclass
class LayoutResult:
    name: str
    mode: str
    raw_cost: float
    adjusted_cost: float
    savings_percent: float
    prefix_hit_rate: float
    prefix_safe_rate: float
    mean_prefix_depth: float
    next_word_hit_rate: float
    strategy_counts: Counter


class PredictorModel:
    def __init__(self, word_scores: dict[str, int], next_word_scores: dict[str, dict[str, int]]):
        self.word_scores = word_scores
        self.next_word_scores = next_word_scores

    @classmethod
    def from_kotlin(cls, path: Path) -> "PredictorModel":
        source = path.read_text(encoding="utf-8")
        word_scores: dict[str, int] = {}

        tiers = {
            "tier1": lambda words: {word: len(words) + 1 - index + 900 for index, word in enumerate(words)},
            "tier2": lambda words: {word: 500 for word in words},
            "tier3": lambda words: {word: 200 for word in words},
            "tier4": lambda words: {word: 100 for word in words},
        }
        for tier_name, score_builder in tiers.items():
            words = cls._parse_kotlin_list(source, tier_name)
            word_scores.update(score_builder(words))

        next_word_scores: dict[str, dict[str, int]] = defaultdict(dict)
        bigram_block = re.search(
            r"val bigramData = mapOf\((.*?)\n\s*\)\n\n\s*for \(\(word, nextWords\) in bigramData\)",
            source,
            re.DOTALL,
        )
        if not bigram_block:
            raise ValueError("Could not parse bigramData from WordPredictionEngine.kt")
        for line in bigram_block.group(1).splitlines():
            match = re.match(r'\s*"([^"]+)" to listOf\((.*)\),?\s*$', line)
            if not match:
                continue
            previous_word = match.group(1)
            pairs = re.findall(r'"([^"]+)" to (\d+)', match.group(2))
            next_word_scores[previous_word] = {word: int(score) for word, score in pairs}

        return cls(word_scores, next_word_scores)

    @staticmethod
    def _parse_kotlin_list(source: str, name: str) -> list[str]:
        match = re.search(rf"val {name} = listOf\((.*?)\n\s*\)", source, re.DOTALL)
        if not match:
            raise ValueError(f"Could not parse {name} from WordPredictionEngine.kt")
        return re.findall(r'"([^"]+)"', match.group(1))

    @staticmethod
    def _score_word_candidate(word: str, base_frequency: int, exact_match: bool) -> int:
        return (20_000 if exact_match else 0) + base_frequency

    def get_suggestions(self, prefix: str, limit: int = 3) -> list[str]:
        if not prefix:
            return []
        normalized = prefix.lower().strip()
        candidates = [
            word for word in self.word_scores
            if word.startswith(normalized)
        ]
        candidates.sort(
            key=lambda word: (
                -self._score_word_candidate(word, self.word_scores[word], exact_match=word == normalized),
                len(word),
                word,
            )
        )
        return candidates[:limit]

    def get_next_word_suggestions(self, previous_word: str, limit: int = 3) -> list[str]:
        normalized = previous_word.lower().strip()
        candidates = self.next_word_scores.get(normalized, {})
        return [
            word for word, _ in sorted(candidates.items(), key=lambda item: (-item[1], item[0]))[:limit]
        ]


class LayoutModel:
    def __init__(
        self,
        name: str,
        mode: str,
        row_order: list[str],
        grid_rows: dict[str, list[str]],
    ):
        self.name = name
        self.mode = mode
        self.row_order = row_order
        self.grid_rows = grid_rows

        if mode == "6-section":
            self.dirs = ["N", "NE", "SE", "S", "SW", "NW"]
            self.left_eff = [0.95, 0.98, 1.08, 1.18, 1.30, 1.03]
            self.right_eff = [0.88, 0.92, 1.02, 1.12, 1.20, 0.98]
            self.sep = [0.5, 0.8, 1.2, 1.7]
            self.utility = {
                "TOGGLE_SYMBOLS": "N",
                "TOGGLE_SHIFT": "NE",
                "SPACE": "SE",
                ".": "S",
                "ENTER": "SW",
                "BACKSPACE": "NW",
            }
            self.symbol_cluster_tokens = ["TOGGLE_SYMBOLS", "TOGGLE_SYMBOLS"]
        else:
            self.dirs = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"]
            self.left_eff = [0.95, 0.98, 1.00, 1.08, 1.18, 1.30, 1.15, 1.03]
            self.right_eff = [0.88, 0.92, 0.95, 1.02, 1.12, 1.20, 1.05, 0.98]
            self.sep = [0.5, 0.8, 1.2, 1.7, 2.4]
            self.utility = {
                "SHIFT": "N",
                "SPACE": "E",
                "BACKSPACE": "W",
                "ENTER": "S",
                "CAPSLOCK": "NE",
                "TAB": "SE",
                ".": "SW",
                ",": "NW",
            }
            self.symbol_cluster_tokens = []

        self.idx = {direction: index for index, direction in enumerate(self.dirs)}
        self.nd = len(self.dirs)
        self.divisor = (self.nd // 2) * 2.0
        self.base_key_time = 0.08
        self.effort_time_scale = 0.12
        self.transition_time_scale = 0.04
        self.single_thumb_penalty = 0.25
        self.dual_thumb_penalty = 1.0
        self.alt_thumb_bonus = 0.90
        self.rolling_bonus = 0.92
        self.ang = [
            [min(abs(i - j), self.nd - abs(i - j)) for j in range(self.nd)]
            for i in range(self.nd)
        ]

        self.char_positions: dict[str, tuple[str, str]] = {}
        for left_direction in self.row_order:
            for right_index, symbol in enumerate(self.grid_rows[left_direction]):
                if symbol == "┬╖":
                    continue
                self.char_positions[symbol.lower()] = (left_direction, self.dirs[right_index])

        self.utility_positions = {token: self.idx[direction] for token, direction in self.utility.items()}
        self.tap_cost = self.base_key_time + self.effort_time_scale * min(
            self._chord_effort(left_direction, right_direction)
            for left_direction, row in self.grid_rows.items()
            for right_direction in self.dirs[: len(row)]
        )

    @classmethod
    def from_log(cls, name: str, path: Path) -> "LayoutModel":
        row_order: list[str] = []
        grid_rows: dict[str, list[str]] = {}
        in_table = False
        for line in path.read_text(encoding="utf-8").splitlines():
            if "L \\ R" in line:
                in_table = True
                continue
            if in_table and "UTILITY (right-dial single-swipe)" in line:
                break
            if not in_table:
                continue
            match = ROW_RE.match(line)
            if not match:
                continue
            direction = match.group(1)
            entries = match.group(2).split()
            row_order.append(direction)
            grid_rows[direction] = entries

        if not row_order:
            raise ValueError(f"Could not parse layout table from {path}")

        mode = "6-section" if len(row_order) == 6 else "8-section"
        return cls(name=name, mode=mode, row_order=row_order, grid_rows=grid_rows)

    def _chord_effort(self, left_direction: str, right_direction: str) -> float:
        li = self.idx[left_direction]
        ri = self.idx[right_direction]
        ang = self.ang[li][ri]
        return self.dual_thumb_penalty * self.sep[ang] * (self.left_eff[li] + self.right_eff[ri]) / 2.0

    def _token_position(self, token: str) -> tuple[str | None, str]:
        if token in self.utility_positions:
            return None, self.dirs[self.utility_positions[token]]
        if token not in self.char_positions:
            raise KeyError(f"Token {token!r} is not available in {self.mode} layout {self.name}")
        return self.char_positions[token]

    def _token_effort(self, token: str) -> float:
        left_direction, right_direction = self._token_position(token)
        if left_direction is None:
            return self.single_thumb_penalty * self.right_eff[self.idx[right_direction]]
        return self._chord_effort(left_direction, right_direction)

    def _transition_cost(self, previous_token: str, current_token: str) -> float:
        prev_left, prev_right = self._token_position(previous_token)
        curr_left, curr_right = self._token_position(current_token)
        if prev_left is None or curr_left is None:
            return self.ang[self.idx[prev_right]][self.idx[curr_right]] / self.divisor
        dl = self.ang[self.idx[prev_left]][self.idx[curr_left]]
        dr = self.ang[self.idx[prev_right]][self.idx[curr_right]]
        value = (dl + dr) / self.divisor
        return value * (self.alt_thumb_bonus if dl != dr else 1.0)

    def _is_roll(self, token_a: str, token_b: str, token_c: str) -> bool:
        left_a, right_a = self._token_position(token_a)
        left_b, right_b = self._token_position(token_b)
        left_c, right_c = self._token_position(token_c)
        if left_a is None or left_b is None or left_c is None:
            return False
        left_roll = self.ang[self.idx[left_a]][self.idx[left_b]] > 0 and self.ang[self.idx[left_b]][self.idx[left_c]] > 0
        right_roll = self.ang[self.idx[right_a]][self.idx[right_b]] > 0 and self.ang[self.idx[right_b]][self.idx[right_c]] > 0
        return left_roll or right_roll

    def sequence_cost(self, tokens: Iterable[str]) -> float:
        token_list = [token for token in tokens if token]
        total = 0.0
        previous: str | None = None
        previous_previous: str | None = None
        for token in token_list:
            transition = 0.0
            if previous is not None:
                transition = self._transition_cost(previous, token)
                if previous_previous is not None and self._is_roll(previous_previous, previous, token):
                    transition *= self.rolling_bonus
            total += (
                self.base_key_time
                + self.effort_time_scale * self._token_effort(token)
                + self.transition_time_scale * transition
            )
            previous_previous, previous = previous, token
        return total

    def tokenize_word(self, word: str) -> list[str]:
        if self.mode == "6-section":
            return self._tokenize_6(word)
        return self._tokenize_8(word)

    def boundary_token(self, delimiter: str) -> str | None:
        if not delimiter:
            return None
        tokens = self._tokenize_6(delimiter) if self.mode == "6-section" else self._tokenize_8(delimiter)
        return tokens[0] if tokens else None

    @staticmethod
    def is_prefix_safe(following_delimiter: str) -> bool:
        return following_delimiter.startswith(" ") or following_delimiter.startswith("\t")

    def _tokenize_8(self, text: str) -> list[str]:
        tokens: list[str] = []
        for char in text:
            if char == "\r":
                continue
            lower = char.lower()
            if char == "\n":
                tokens.append("ENTER")
            elif char.isspace():
                tokens.append("SPACE")
            elif lower == ".":
                tokens.append(".")
            elif lower == ",":
                tokens.append(",")
            elif lower in self.char_positions:
                tokens.append(lower)
        return tokens

    def _tokenize_6(self, text: str) -> list[str]:
        tokens: list[str] = []
        prev_space = False
        index = 0
        lowered = text.lower()
        while index < len(lowered):
            char = lowered[index]
            if char.isalnum():
                tokens.append(char)
                prev_space = False
                index += 1
            elif char.isspace():
                if tokens and not prev_space:
                    tokens.append("SPACE")
                    prev_space = True
                index += 1
            elif char == ".":
                tokens.append(".")
                prev_space = False
                index += 1
            else:
                while index < len(lowered):
                    pending = lowered[index]
                    if pending.isalnum() or pending.isspace() or pending == ".":
                        break
                    index += 1
                tokens.extend(self.symbol_cluster_tokens)
                prev_space = False
        if tokens and tokens[-1] == "SPACE":
            tokens.pop()
        return tokens


def parse_layout_argument(raw: str) -> tuple[str, Path]:
    if "=" not in raw:
        path = Path(raw)
        return path.stem, path
    name, value = raw.split("=", 1)
    return name, Path(value)


def extract_word_occurrences(text: str) -> list[WordOccurrence]:
    matches = list(WORD_RE.finditer(text))
    occurrences: list[WordOccurrence] = []
    previous_word: str | None = None
    previous_end = 0
    for index, match in enumerate(matches):
        current_word = match.group(0).lower()
        preceding = text[previous_end:match.start()]
        next_start = matches[index + 1].start() if index + 1 < len(matches) else len(text)
        following = text[match.end():next_start]
        occurrences.append(
            WordOccurrence(
                word=current_word,
                preceding_delimiter=preceding,
                following_delimiter=following,
                previous_word=previous_word,
            )
        )
        previous_word = current_word
        previous_end = match.end()
    return occurrences


def shortest_useful_prefix_depth(predictor: PredictorModel, word: str) -> int | None:
    for depth in range(1, len(word)):
        prefix = word[:depth]
        if word in predictor.get_suggestions(prefix, limit=3):
            return depth
    return None


def evaluate_layout(layout: LayoutModel, predictor: PredictorModel, benchmark_files: list[Path]) -> LayoutResult:
    occurrences: list[WordOccurrence] = []
    for path in benchmark_files:
        occurrences.extend(extract_word_occurrences(path.read_text(encoding="utf-8")))

    if not occurrences:
        raise ValueError("No benchmark words found to evaluate")

    raw_total = 0.0
    adjusted_total = 0.0
    prefix_hits = 0
    prefix_safe_hits = 0
    prefix_depths: list[int] = []
    next_hits = 0
    next_eligible = 0
    strategy_counts: Counter = Counter()

    for occurrence in occurrences:
        raw_cost = layout.sequence_cost(layout.tokenize_word(occurrence.word))
        raw_total += raw_cost
        best_cost = raw_cost
        best_strategy = "raw"

        prefix_depth = shortest_useful_prefix_depth(predictor, occurrence.word)
        if prefix_depth is not None:
            prefix_hits += 1
            if layout.is_prefix_safe(occurrence.following_delimiter):
                prefix_safe_hits += 1
                prefix_depths.append(prefix_depth)
                prefix_cost = layout.sequence_cost(layout.tokenize_word(occurrence.word[:prefix_depth])) + layout.tap_cost
                if prefix_cost < best_cost:
                    best_cost = prefix_cost
                    best_strategy = "prefix"

        if occurrence.previous_word:
            next_eligible += 1
            if occurrence.word in predictor.get_next_word_suggestions(occurrence.previous_word, limit=3):
                boundary_token = layout.boundary_token(occurrence.preceding_delimiter)
                if boundary_token is not None:
                    next_hits += 1
                    next_cost = layout.sequence_cost([boundary_token]) + layout.tap_cost
                    if next_cost < best_cost:
                        best_cost = next_cost
                        best_strategy = "next_word"

        adjusted_total += best_cost
        strategy_counts[best_strategy] += 1

    return LayoutResult(
        name=layout.name,
        mode=layout.mode,
        raw_cost=raw_total,
        adjusted_cost=adjusted_total,
        savings_percent=((raw_total - adjusted_total) / raw_total * 100.0) if raw_total else 0.0,
        prefix_hit_rate=(prefix_hits / len(occurrences)) * 100.0,
        prefix_safe_rate=(prefix_safe_hits / prefix_hits * 100.0) if prefix_hits else 0.0,
        mean_prefix_depth=(sum(prefix_depths) / len(prefix_depths)) if prefix_depths else 0.0,
        next_word_hit_rate=(next_hits / next_eligible * 100.0) if next_eligible else 0.0,
        strategy_counts=strategy_counts,
    )


def print_mode_summary(mode: str, results: list[LayoutResult]) -> None:
    print(f"\n=== {mode} ===")
    print(
        "name | raw_cost | adjusted_cost | savings% | prefix_hit% | prefix_safe% | "
        "mean_prefix_depth | next_word_hit% | chosen(raw/prefix/next)"
    )
    for result in sorted(results, key=lambda item: item.name):
        print(
            f"{result.name} | {result.raw_cost:.4f} | {result.adjusted_cost:.4f} | "
            f"{result.savings_percent:.2f} | {result.prefix_hit_rate:.2f} | {result.prefix_safe_rate:.2f} | "
            f"{result.mean_prefix_depth:.2f} | {result.next_word_hit_rate:.2f} | "
            f"{result.strategy_counts.get('raw', 0)}/{result.strategy_counts.get('prefix', 0)}/{result.strategy_counts.get('next_word', 0)}"
        )

    raw_ranking = ", ".join(result.name for result in sorted(results, key=lambda item: item.raw_cost))
    adjusted_ranking = ", ".join(result.name for result in sorted(results, key=lambda item: item.adjusted_cost))
    print(f"raw ranking: {raw_ranking}")
    print(f"prediction-adjusted ranking: {adjusted_ranking}")


def main() -> None:
    parser = argparse.ArgumentParser(description="Post-hoc prediction-aware benchmark for ERICK research layouts")
    parser.add_argument(
        "--layout",
        action="append",
        default=[],
        help="Layout log path, optionally as name=path. Defaults to the current Branch 2 probe logs.",
    )
    parser.add_argument(
        "--benchmark-dir",
        default=str(BENCHMARK_PACK_DIR),
        help="Directory containing the benchmark pack text files to score.",
    )
    args = parser.parse_args()

    predictor = PredictorModel.from_kotlin(WORD_ENGINE_PATH)
    benchmark_dir = Path(args.benchmark_dir)
    benchmark_files = sorted(benchmark_dir.glob("*.txt"))
    if not benchmark_files:
        raise ValueError(f"No benchmark packs found under {benchmark_dir}")

    layout_specs = [parse_layout_argument(item) for item in args.layout] if args.layout else DEFAULT_LAYOUTS
    layouts = [LayoutModel.from_log(name, path) for name, path in layout_specs]

    print("Prediction-aware benchmark assumptions:")
    print("- predictor source: android/shared/src/commonMain/kotlin/WordPredictionEngine.kt")
    print("- suggestion mode: top-3 prefix completions plus top-3 next-word predictions")
    print("- suggestion tap cost: one easiest-chord equivalent tap in the active dial mode")
    print("- prefix acceptance: only counted as safe when the target word is followed by whitespace")
    print("- benchmark packs:", ", ".join(path.stem for path in benchmark_files))

    grouped_results: dict[str, list[LayoutResult]] = defaultdict(list)
    for layout in layouts:
        grouped_results[layout.mode].append(evaluate_layout(layout, predictor, benchmark_files))

    for mode in sorted(grouped_results):
        print_mode_summary(mode, grouped_results[mode])


if __name__ == "__main__":
    main()