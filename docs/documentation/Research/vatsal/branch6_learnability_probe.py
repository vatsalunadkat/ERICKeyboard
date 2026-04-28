from __future__ import annotations

import os
import re
import sys
from dataclasses import dataclass
from pathlib import Path


ROOT = Path(__file__).resolve().parents[4]
KOTLIN_PATH = ROOT / "android" / "shared" / "src" / "commonMain" / "kotlin" / "KeyboardLogic.kt"
BRANCH3_PATH = ROOT / "docs" / "documentation" / "Research" / "vatsal" / "results_and_logs" / "optimization_results_6section_shipped_toggle_pair_2026-04-26.md"

os.environ.setdefault("ERICK6_UTILITY_MODEL", "shipped")
os.environ.setdefault("ERICK6_CORPUS_PROFILE", "mixed_shortform")
os.environ.setdefault("ERICK6_SYMBOL_COST_MODEL", "toggle_pair")
os.environ.setdefault("ERICK6_EFFORT_PROFILE", "shared_derived")
os.environ.setdefault("ERICK6_BIGRAM_WEIGHT", "0.6")
os.environ.setdefault("ERICK6_TRIGRAM_WEIGHT", "0.3")

sys.path.insert(0, str(Path(__file__).resolve().parent))

import erick_v5_6section as optimizer  # noqa: E402


DIRS6 = ["N", "NE", "SE", "S", "SW", "NW"]
PREVIEW_DIRS6 = ["NE", "SE", "S", "SW", "NW", "N"]
LESSON_TARGETS = [
    "face",
    "907",
    "?",
    "go on",
    "go.",
    "?",
    "be",
    "12",
    "go.",
]
POSITION_INDEX = {(left, right): index for index, (left, right) in enumerate(optimizer.ALL_POS)}


@dataclass(frozen=True)
class LayoutMeasurement:
    name: str
    score: float
    predicted_wpm: float
    row_dispersion: float
    preview_jump_penalty: float
    lesson_span_penalty: float

    @property
    def learnability_proxy(self) -> float:
        return self.row_dispersion + self.preview_jump_penalty + self.lesson_span_penalty


def parse_kotlin_map(map_name: str) -> dict[str, list[str]]:
    lines = KOTLIN_PATH.read_text(encoding="utf-8").splitlines()
    start = next(index for index, line in enumerate(lines) if f"private val {map_name} = mapOf(" in line)
    rows: dict[str, list[str]] = {}
    for line in lines[start + 1 :]:
        if line.strip() == ")":
            break
        match = re.search(r"Direction\.(\w+)\s+to listOf\((.*)\)", line)
        if not match:
            continue
        rows[match.group(1)] = re.findall(r'"([^"]*)"', match.group(2))
    missing = [direction for direction in DIRS6 if direction not in rows]
    if missing:
        raise ValueError(f"Missing directions in {map_name}: {missing}")
    return rows


def parse_branch3_layout() -> dict[str, list[str]]:
    lines = BRANCH3_PATH.read_text(encoding="utf-8").splitlines()
    start = next(index for index, line in enumerate(lines) if line.strip() == "## Best Normal-Layer Layout")
    rows: dict[str, list[str]] = {}
    for line in lines[start + 1 :]:
        if line.startswith("## ") and line.strip() != "## Best Normal-Layer Layout":
            break
        if not line.startswith("| "):
            continue
        parts = [part.strip() for part in line.split("|")[1:-1]]
        if len(parts) == 7 and parts[0] in DIRS6:
            rows[parts[0]] = parts[1:]
    missing = [direction for direction in DIRS6 if direction not in rows]
    if missing:
        raise ValueError(f"Missing Branch 3 rows: {missing}")
    return rows


def build_layout_array(row_map: dict[str, list[str]]):
    layout = [0] * len(optimizer.SYMBOLS)
    for symbol_index, symbol in enumerate(optimizer.SYMBOLS):
        match = None
        for left_index, left_direction in enumerate(DIRS6):
            row = row_map[left_direction]
            for right_index, value in enumerate(row):
                if value == symbol:
                    match = POSITION_INDEX[(left_index, right_index)]
                    break
            if match is not None:
                break
        if match is None:
            raise ValueError(f"Missing symbol {symbol!r}")
        layout[symbol_index] = match
    return optimizer.np.array(layout, dtype=int)


def token_kind(value: str) -> str:
    if value.isalpha():
        return "letter"
    if value.isdigit():
        return "digit"
    return "other"


def adjacency_penalty(left: str, right: str) -> float:
    if not left or not right:
        return 2.0
    left_kind = token_kind(left)
    right_kind = token_kind(right)
    if left_kind != right_kind:
        return 3.0
    if left_kind == "letter":
        return float(max(abs(ord(left) - ord(right)) - 1, 0))
    if left_kind == "digit":
        return float(max(abs(int(left) - int(right)) - 1, 0))
    return 2.0 if left != right else 0.0


def row_dispersion(row_map: dict[str, list[str]]) -> float:
    penalties: list[float] = []
    for direction in DIRS6:
        row = row_map[direction]
        penalties.extend(adjacency_penalty(left, right) for left, right in zip(row, row[1:]))
    return sum(penalties) / len(penalties)


def preview_jump_penalty(row_map: dict[str, list[str]]) -> float:
    starts = [row_map[direction][0] for direction in PREVIEW_DIRS6]
    penalties = [adjacency_penalty(left, right) for left, right in zip(starts, starts[1:])]
    return sum(penalties) / len(penalties)


def lesson_span_penalty(row_map: dict[str, list[str]]) -> float:
    rows_by_symbol: dict[str, str] = {}
    for direction in DIRS6:
        for value in row_map[direction]:
            rows_by_symbol[value] = direction

    distinct_rows = set()
    utility_tokens = 0
    symbol_toggles = 0

    for target in LESSON_TARGETS:
        for token in optimizer._tokenize_shortform_line(target):
            if token in rows_by_symbol:
                distinct_rows.add(rows_by_symbol[token])
            elif token in optimizer.UTIL_KEYS:
                utility_tokens += 1
                if token == "TOGGLE_SYMBOLS":
                    symbol_toggles += 1

    return float(len(distinct_rows)) + 0.5 * utility_tokens + 0.5 * symbol_toggles


def measure_layout(name: str, row_map: dict[str, list[str]]) -> LayoutMeasurement:
    layout = build_layout_array(row_map)
    return LayoutMeasurement(
        name=name,
        score=float(optimizer.total_cost(layout)),
        predicted_wpm=float(optimizer.estimate_wpm(layout)),
        row_dispersion=row_dispersion(row_map),
        preview_jump_penalty=preview_jump_penalty(row_map),
        lesson_span_penalty=lesson_span_penalty(row_map),
    )


def percentage_delta(value: float, baseline: float, lower_is_better: bool) -> float:
    if lower_is_better:
        return ((baseline - value) / baseline) * 100.0
    return ((value - baseline) / baseline) * 100.0


def print_scorecard(measurements: list[LayoutMeasurement]) -> None:
    best_efficiency = min(measurements, key=lambda measurement: measurement.score)
    best_learnability = min(measurements, key=lambda measurement: measurement.learnability_proxy)

    print("Branch 6 Learnability Probe")
    print("Utility model: shipped")
    print("Corpus profile: mixed_shortform")
    print("Symbol cost model: toggle_pair")
    print("Learnability proxy: row_dispersion + preview_jump_penalty + lesson_span_penalty")
    print("Utility anchor stability is held constant across candidates and is not part of the combined score.")
    print()
    print(
        "layout|score|predicted_wpm|row_dispersion|preview_jump_penalty|lesson_span_penalty|learnability_proxy|"
        "score_delta_vs_best|learnability_delta_vs_best"
    )
    for measurement in measurements:
        score_delta = percentage_delta(measurement.score, best_efficiency.score, lower_is_better=True)
        learnability_delta = percentage_delta(
            measurement.learnability_proxy,
            best_learnability.learnability_proxy,
            lower_is_better=True,
        )
        print(
            f"{measurement.name}|{measurement.score:.5f}|{measurement.predicted_wpm:.1f}|"
            f"{measurement.row_dispersion:.2f}|{measurement.preview_jump_penalty:.2f}|"
            f"{measurement.lesson_span_penalty:.2f}|{measurement.learnability_proxy:.2f}|"
            f"{score_delta:+.1f}%|{learnability_delta:+.1f}%"
        )

    print()
    print(
        f"Best pure-efficiency candidate: {best_efficiency.name} "
        f"({best_efficiency.score:.5f}, {best_efficiency.predicted_wpm:.1f} WPM)"
    )
    print(
        f"Best learnability proxy: {best_learnability.name} "
        f"({best_learnability.learnability_proxy:.2f})"
    )
    print("Hybrid-interest gate: within 3% of the best efficiency score and at least 15% learnability-proxy improvement.")
    for measurement in measurements:
        if measurement is best_efficiency:
            continue
        within_score_gate = measurement.score <= best_efficiency.score * 1.03
        learnability_gain = percentage_delta(
            measurement.learnability_proxy,
            best_efficiency.learnability_proxy,
            lower_is_better=True,
        )
        print(
            f"{measurement.name}: within_score_gate={within_score_gate} "
            f"learnability_gain_vs_best_efficiency={learnability_gain:+.1f}%"
        )


def main() -> None:
    measurements = [
        measure_layout("logical_6", parse_kotlin_map("normalMap6")),
        measure_layout("efficiency_placeholder_6", parse_kotlin_map("efficiencyNormalMap6")),
        measure_layout("branch3_toggle_pair", parse_branch3_layout()),
    ]
    print_scorecard(measurements)


if __name__ == "__main__":
    main()