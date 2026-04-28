from __future__ import annotations

from collections import defaultdict
from dataclasses import dataclass
import os
from pathlib import Path
import re
import sys

import numpy as np


ROOT = Path(__file__).resolve().parents[4]
RESEARCH_DIR = Path(__file__).resolve().parent
KOTLIN_PATH = ROOT / "android" / "shared" / "src" / "commonMain" / "kotlin" / "KeyboardLogic.kt"
V5_OUTPUT_PATH = RESEARCH_DIR / "v5_output.txt"
BRANCH3_PATH = RESEARCH_DIR / "results_and_logs" / "optimization_results_6section_shipped_toggle_pair_2026-04-26.md"
BENCHMARK_PACK_DIR = RESEARCH_DIR / "benchmark_packs"

os.environ.setdefault("ERICK6_UTILITY_MODEL", "shipped")
os.environ.setdefault("ERICK6_CORPUS_PROFILE", "mixed_shortform")
os.environ.setdefault("ERICK6_SYMBOL_COST_MODEL", "toggle_pair")

sys.path.insert(0, str(RESEARCH_DIR))

import erick_v5_6section as opt6  # noqa: E402


DIRS8 = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"]
DIRS6 = ["N", "NE", "SE", "S", "SW", "NW"]
IDX8 = {direction: index for index, direction in enumerate(DIRS8)}
LETTERS = list("abcdefghijklmnopqrstuvwxyz")
DIGITS = list("0123456789")
ALL_POS8 = [(left, right) for left in range(8) for right in range(8)]
POSITION_INDEX8 = {(left, right): index for index, (left, right) in enumerate(ALL_POS8)}
BENCHMARK_FILES = [
    "messaging-shortform.txt",
    "accessibility-supportive.txt",
    "controller-tv-query.txt",
    "punctuation-mixed.txt",
]
CANONICAL_SYMBOL_ORDER8 = LETTERS + DIGITS + ["'", "-", "/", ";", "\\", "[", "]", "=", "`", "!", "?", ":", '"', "(", ")", "@", "#"]

UTILITY8 = {
    "SHIFT": "N",
    "SPACE": "E",
    "BACKSPACE": "W",
    "ENTER": "S",
    "CAPSLOCK": "NE",
    "TAB": "SE",
    ".": "SW",
    ",": "NW",
}
UTILITY_KEYS8 = list(UTILITY8.keys())
UTILITY_RIGHT8 = {key: IDX8[direction] for key, direction in UTILITY8.items()}

L_EFF8 = np.array([0.95, 0.98, 1.00, 1.08, 1.18, 1.30, 1.15, 1.03])
R_EFF8 = np.array([0.88, 0.92, 0.95, 1.02, 1.12, 1.20, 1.05, 0.98])
SEP8 = np.array([0.5, 0.8, 1.2, 1.7, 2.4])
ANG8 = np.array([[min(abs(i - j), 8 - abs(i - j)) for j in range(8)] for i in range(8)], dtype=int)

BASE_KEY_TIME = 0.08
EFFORT_TIME_SCALE = 0.12
TRANSITION_TIME_SCALE = 0.04
ALT_THUMB_BONUS = 0.90
ROLLING_BONUS = 0.92
BIGRAM_WEIGHT = 0.6
TRIGRAM_WEIGHT = 0.3


@dataclass(frozen=True)
class EvaluationResult:
    name: str
    score: float
    predicted_wpm: float
    slot_matches: int | None = None
    total_slots: int | None = None


@dataclass(frozen=True)
class EightSectionModel:
    symbols: list[str]
    uni_si: np.ndarray
    uni_f: np.ndarray
    cc_si1: np.ndarray
    cc_si2: np.ndarray
    cc_f: np.ndarray
    uc_ui: np.ndarray
    uc_si: np.ndarray
    uc_f: np.ndarray
    cu_si: np.ndarray
    cu_ui: np.ndarray
    cu_f: np.ndarray
    tri: dict[str, tuple[np.ndarray, np.ndarray, np.ndarray, np.ndarray]]
    chord_diff: np.ndarray
    trans: np.ndarray
    util_to_chord: np.ndarray
    chord_to_util: np.ndarray
    r_pos: np.ndarray
    l_pos: np.ndarray
    char_bi: dict[tuple[str, str], float]


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
    return rows


def parse_v5_layout() -> dict[str, list[str]]:
    rows: dict[str, list[str]] = {}
    for line in V5_OUTPUT_PATH.read_text(encoding="utf-8").splitlines():
        if "|" not in line:
            continue
        left, right = line.split("|", maxsplit=1)
        label = left.strip().split()
        if not label:
            continue
        direction = label[0]
        if direction not in DIRS8:
            continue
        cells = ["" if cell == "·" else cell for cell in right.split()]
        if len(cells) == 8:
            rows[direction] = cells
    missing = [direction for direction in DIRS8 if direction not in rows]
    if missing:
        raise ValueError(f"Missing v5 rows: {missing}")
    return rows


def count_slot_matches(left: dict[str, list[str]], right: dict[str, list[str]], directions: list[str]) -> int:
    matches = 0
    for direction in directions:
        matches += sum(1 for left_value, right_value in zip(left[direction], right[direction]) if left_value == right_value)
    return matches


def collect_symbols(row_map: dict[str, list[str]], directions: list[str]) -> list[str]:
    symbols = []
    for direction in directions:
        for value in row_map[direction]:
            if value:
                symbols.append(value)
    return symbols


def build_layout_array_generic(
    row_map: dict[str, list[str]],
    directions: list[str],
    symbols: list[str],
    position_index: dict[tuple[int, int], int],
) -> np.ndarray:
    layout = [0] * len(symbols)
    for symbol_index, symbol in enumerate(symbols):
        match = None
        for left_index, left_direction in enumerate(directions):
            row = row_map[left_direction]
            for right_index, value in enumerate(row):
                if value == symbol:
                    match = position_index[(left_index, right_index)]
                    break
            if match is not None:
                break
        if match is None:
            raise ValueError(f"Missing symbol {symbol!r}")
        layout[symbol_index] = match
    return np.array(layout, dtype=int)


def tokenize_mixed_shortform_8_with_inventory(text: str, available_symbols: set[str]) -> list[str]:
    tokens: list[str] = []
    previous_space = False
    for char in text.lower():
        if char in available_symbols:
            tokens.append(char)
            previous_space = False
        elif char == ".":
            tokens.append(".")
            previous_space = False
        elif char == ",":
            tokens.append(",")
            previous_space = False
        elif char.isspace():
            if tokens and not previous_space:
                tokens.append("SPACE")
                previous_space = True
        else:
            previous_space = False
    if tokens and tokens[-1] == "SPACE":
        tokens.pop()
    return tokens


def add_token_sequence(tokens: list[str], weight: float, uni, bi, tri) -> None:
    if not tokens:
        return
    for index, token in enumerate(tokens):
        uni[token] += weight
        if index < len(tokens) - 1:
            bi[(tokens[index], tokens[index + 1])] += weight
        if index < len(tokens) - 2:
            tri[(tokens[index], tokens[index + 1], tokens[index + 2])] += weight


def build_corpus_8_for_symbols(symbols: list[str]):
    uni = defaultdict(float)
    bi = defaultdict(float)
    tri = defaultdict(float)
    available_symbols = set(symbols)
    for filename in BENCHMARK_FILES:
        path = BENCHMARK_PACK_DIR / filename
        for raw_line in path.read_text(encoding="utf-8").splitlines():
            line = raw_line.strip()
            if line:
                add_token_sequence(tokenize_mixed_shortform_8_with_inventory(line, available_symbols), 1.0, uni, bi, tri)

    def normalize(values: dict) -> dict:
        total = sum(values.values())
        return {key: value / total for key, value in values.items()}

    return normalize(uni), normalize(bi), normalize(tri)


def build_model_8_for_symbols(symbols: list[str]) -> EightSectionModel:
    char_uni, char_bi, char_tri = build_corpus_8_for_symbols(symbols)

    chord_diff = np.array(
        [SEP8[ANG8[left, right]] * (L_EFF8[left] + R_EFF8[right]) / 2 for left, right in ALL_POS8],
        dtype=np.float64,
    )

    def transition_cost(position_left: int, position_right: int) -> float:
        left_direction, right_direction = ALL_POS8[position_left]
        next_left_direction, next_right_direction = ALL_POS8[position_right]
        delta_left = ANG8[left_direction, next_left_direction]
        delta_right = ANG8[right_direction, next_right_direction]
        return (delta_left + delta_right) / 8.0 * (ALT_THUMB_BONUS if delta_left != delta_right else 1.0)

    trans = np.array(
        [[transition_cost(position_left, position_right) for position_right in range(len(ALL_POS8))] for position_left in range(len(ALL_POS8))],
        dtype=np.float64,
    )
    r_pos = np.array([right for _, right in ALL_POS8], dtype=int)
    l_pos = np.array([left for left, _ in ALL_POS8], dtype=int)

    util_right_indices = np.array([UTILITY_RIGHT8[key] for key in UTILITY_KEYS8], dtype=int)
    util_to_chord = ANG8[util_right_indices[:, None], r_pos[None, :]] / 8.0
    chord_to_util = util_to_chord.T

    def symbol_index(char: str):
        return symbols.index(char) if char in symbols else None

    def utility_index(char: str):
        return UTILITY_KEYS8.index(char) if char in UTILITY_KEYS8 else None

    uni_si, uni_f = [], []
    for symbol in symbols:
        frequency = char_uni.get(symbol, 0.0)
        if frequency > 0:
            uni_si.append(symbols.index(symbol))
            uni_f.append(frequency)

    cc_si1, cc_si2, cc_f = [], [], []
    uc_ui, uc_si, uc_f = [], [], []
    cu_si, cu_ui, cu_f = [], [], []
    for (first, second), frequency in char_bi.items():
        first_symbol, first_utility = symbol_index(first), utility_index(first)
        second_symbol, second_utility = symbol_index(second), utility_index(second)
        weighted = frequency * BIGRAM_WEIGHT
        if first_symbol is not None and second_symbol is not None:
            cc_si1.append(first_symbol)
            cc_si2.append(second_symbol)
            cc_f.append(weighted)
        elif first_utility is not None and second_symbol is not None:
            uc_ui.append(first_utility)
            uc_si.append(second_symbol)
            uc_f.append(weighted)
        elif first_symbol is not None and second_utility is not None:
            cu_si.append(first_symbol)
            cu_ui.append(second_utility)
            cu_f.append(weighted)

    tri_types = {"CCC": ([], [], [], []), "UCC": ([], [], [], []), "CCU": ([], [], [], []), "CUC": ([], [], [], [])}
    for (first, second, third), frequency in char_tri.items():
        first_symbol, first_utility = symbol_index(first), utility_index(first)
        second_symbol, second_utility = symbol_index(second), utility_index(second)
        third_symbol, third_utility = symbol_index(third), utility_index(third)
        weighted = frequency * TRIGRAM_WEIGHT
        if first_symbol is not None and second_symbol is not None and third_symbol is not None:
            tri_types["CCC"][0].append(first_symbol)
            tri_types["CCC"][1].append(second_symbol)
            tri_types["CCC"][2].append(third_symbol)
            tri_types["CCC"][3].append(weighted)
        elif first_utility is not None and second_symbol is not None and third_symbol is not None:
            tri_types["UCC"][0].append(first_utility)
            tri_types["UCC"][1].append(second_symbol)
            tri_types["UCC"][2].append(third_symbol)
            tri_types["UCC"][3].append(weighted)
        elif first_symbol is not None and second_symbol is not None and third_utility is not None:
            tri_types["CCU"][0].append(first_symbol)
            tri_types["CCU"][1].append(second_symbol)
            tri_types["CCU"][2].append(third_utility)
            tri_types["CCU"][3].append(weighted)
        elif first_symbol is not None and second_utility is not None and third_symbol is not None:
            tri_types["CUC"][0].append(first_symbol)
            tri_types["CUC"][1].append(second_utility)
            tri_types["CUC"][2].append(third_symbol)
            tri_types["CUC"][3].append(weighted)

    tri = {}
    for key, (index0, index1, index2, weights) in tri_types.items():
        if index0:
            tri[key] = (
                np.array(index0, dtype=int),
                np.array(index1, dtype=int),
                np.array(index2, dtype=int),
                np.array(weights, dtype=np.float64),
            )

    return EightSectionModel(
        symbols=symbols,
        uni_si=np.array(uni_si, dtype=int),
        uni_f=np.array(uni_f, dtype=np.float64),
        cc_si1=np.array(cc_si1, dtype=int),
        cc_si2=np.array(cc_si2, dtype=int),
        cc_f=np.array(cc_f, dtype=np.float64),
        uc_ui=np.array(uc_ui, dtype=int),
        uc_si=np.array(uc_si, dtype=int),
        uc_f=np.array(uc_f, dtype=np.float64),
        cu_si=np.array(cu_si, dtype=int),
        cu_ui=np.array(cu_ui, dtype=int),
        cu_f=np.array(cu_f, dtype=np.float64),
        tri=tri,
        chord_diff=chord_diff,
        trans=trans,
        util_to_chord=util_to_chord,
        chord_to_util=chord_to_util,
        r_pos=r_pos,
        l_pos=l_pos,
        char_bi=char_bi,
    )


def total_cost_8(layout: np.ndarray, model: EightSectionModel) -> float:
    cost1 = float(np.dot(model.uni_f, model.chord_diff[layout[model.uni_si]]))
    cost2 = float(np.dot(model.cc_f, model.trans[layout[model.cc_si1], layout[model.cc_si2]]))
    cost2 += float(np.dot(model.uc_f, model.util_to_chord[model.uc_ui, layout[model.uc_si]]))
    cost2 += float(np.dot(model.cu_f, model.chord_to_util[layout[model.cu_si], model.cu_ui]))
    cost3 = 0.0

    if "CCC" in model.tri:
        index0, index1, index2, weights = model.tri["CCC"]
        transition12 = model.trans[layout[index0], layout[index1]]
        transition23 = model.trans[layout[index1], layout[index2]]
        right0 = model.r_pos[layout[index0]]
        right1 = model.r_pos[layout[index1]]
        right2 = model.r_pos[layout[index2]]
        left0 = model.l_pos[layout[index0]]
        left1 = model.l_pos[layout[index1]]
        left2 = model.l_pos[layout[index2]]
        rolling = ((ANG8[left0, left1] > 0) & (ANG8[left1, left2] > 0)) | ((ANG8[right0, right1] > 0) & (ANG8[right1, right2] > 0))
        cost3 += float(np.dot(weights, transition12 + transition23 * np.where(rolling, ROLLING_BONUS, 1.0)))

    if "UCC" in model.tri:
        index0, index1, index2, weights = model.tri["UCC"]
        cost3 += float(np.dot(weights, model.util_to_chord[index0, layout[index1]] + model.trans[layout[index1], layout[index2]]))
    if "CCU" in model.tri:
        index0, index1, index2, weights = model.tri["CCU"]
        cost3 += float(np.dot(weights, model.trans[layout[index0], layout[index1]] + model.chord_to_util[layout[index1], index2]))
    if "CUC" in model.tri:
        index0, index1, index2, weights = model.tri["CUC"]
        cost3 += float(np.dot(weights, model.chord_to_util[layout[index0], index1] + model.util_to_chord[index1, layout[index2]]))

    return cost1 + cost2 + cost3


def estimate_wpm_8(layout: np.ndarray, model: EightSectionModel, row_map: dict[str, list[str]]) -> float:
    positions = {}
    for left_direction in DIRS8:
        for right_direction, value in zip(DIRS8, row_map[left_direction]):
            if value:
                positions[value] = (left_direction, right_direction)
    for key, direction in UTILITY8.items():
        positions[key] = (None, direction)

    total_time = 0.0
    for (first, second), probability in model.char_bi.items():
        first_position = positions.get(first)
        second_position = positions.get(second)
        if first_position is None or second_position is None:
            continue
        left_first, right_first = first_position
        left_second, right_second = second_position
        if left_first is None:
            effort = 0.25 * R_EFF8[IDX8[right_first]]
        else:
            effort = SEP8[ANG8[IDX8[left_first], IDX8[right_first]]] * (L_EFF8[IDX8[left_first]] + R_EFF8[IDX8[right_first]]) / 2
        if left_first is None or left_second is None:
            transition = ANG8[IDX8[right_first], IDX8[right_second]] / 8.0
        else:
            delta_left = ANG8[IDX8[left_first], IDX8[left_second]]
            delta_right = ANG8[IDX8[right_first], IDX8[right_second]]
            transition = (delta_left + delta_right) / 8.0 * (ALT_THUMB_BONUS if delta_left != delta_right else 1.0)
        total_time += probability * (BASE_KEY_TIME + EFFORT_TIME_SCALE * effort + TRANSITION_TIME_SCALE * transition)
    return (1.0 / total_time) * 60 / 5 if total_time > 0 else 0.0


def evaluate_6section() -> list[EvaluationResult]:
    current_map = parse_kotlin_map("efficiencyNormalMap6")
    branch3_map = parse_branch3_layout()
    position_index6 = {(left, right): index for index, (left, right) in enumerate(opt6.ALL_POS)}
    current_layout = build_layout_array_generic(current_map, DIRS6, list(opt6.SYMBOLS), position_index6)
    branch3_layout = build_layout_array_generic(branch3_map, DIRS6, list(opt6.SYMBOLS), position_index6)
    slot_matches = count_slot_matches(current_map, branch3_map, DIRS6)
    total_slots = len(DIRS6) * len(DIRS6)
    return [
        EvaluationResult(
            name="current_shipped_efficiency_6",
            score=float(opt6.total_cost(current_layout)),
            predicted_wpm=float(opt6.estimate_wpm(current_layout)),
            slot_matches=slot_matches,
            total_slots=total_slots,
        ),
        EvaluationResult(
            name="branch3_toggle_pair_winner_6",
            score=float(opt6.total_cost(branch3_layout)),
            predicted_wpm=float(opt6.estimate_wpm(branch3_layout)),
            slot_matches=slot_matches,
            total_slots=total_slots,
        ),
    ]


def evaluate_8section() -> list[EvaluationResult]:
    current_map = parse_kotlin_map("efficiencyNormalMap")
    v5_map = parse_v5_layout()
    current_symbols = set(collect_symbols(current_map, DIRS8))
    v5_symbols = set(collect_symbols(v5_map, DIRS8))
    shared_symbols = [symbol for symbol in CANONICAL_SYMBOL_ORDER8 if symbol in current_symbols and symbol in v5_symbols]
    model = build_model_8_for_symbols(shared_symbols)
    current_layout = build_layout_array_generic(current_map, DIRS8, shared_symbols, POSITION_INDEX8)
    v5_layout = build_layout_array_generic(v5_map, DIRS8, shared_symbols, POSITION_INDEX8)
    slot_matches = count_slot_matches(current_map, v5_map, DIRS8)
    total_slots = len(DIRS8) * len(DIRS8)
    return [
        EvaluationResult(
            name=f"current_shipped_efficiency_8_shared{len(shared_symbols)}",
            score=total_cost_8(current_layout, model),
            predicted_wpm=estimate_wpm_8(current_layout, model, current_map),
            slot_matches=slot_matches,
            total_slots=total_slots,
        ),
        EvaluationResult(
            name=f"v5_logged_winner_8_shared{len(shared_symbols)}",
            score=total_cost_8(v5_layout, model),
            predicted_wpm=estimate_wpm_8(v5_layout, model, v5_map),
            slot_matches=slot_matches,
            total_slots=total_slots,
        ),
    ]


def print_results(title: str, results: list[EvaluationResult]) -> None:
    print(title)
    print("layout|score|predicted_wpm|slot_matches")
    for result in results:
        slot_summary = "n/a"
        if result.slot_matches is not None and result.total_slots is not None:
            slot_summary = f"{result.slot_matches}/{result.total_slots}"
        print(f"{result.name}|{result.score:.5f}|{result.predicted_wpm:.1f}|{slot_summary}")
    print()


def main() -> None:
    print("Branch 1 Baseline Probe")
    print("6-section family: shipped-mixed-shortform replay via current 6-section optimizer")
    print("8-section family: benchmark-pack replay against the current shipped map and the v5 logged winner")
    print()
    print_results("6-section shipped baseline replay", evaluate_6section())
    print_results("8-section mixed-shortform replay", evaluate_8section())


if __name__ == "__main__":
    main()