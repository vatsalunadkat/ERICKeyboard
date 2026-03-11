# ERICK Keyboard Layout Optimization Results

Based on the merged corpus from Common Crawl, Wikipedia, and Google Books N-grams, the combined Simulated Annealing and Genetic Algorithm successfully converged to the following optimal layout.

## 1. Best 8x8 Chord Table
The following matrix assigns a character to the combination of a Left-dial direction (row) and a Right-dial direction (column):

| Left \ Right | N | NE | E | SE | S | SW | W | NW |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| **N**  | `a` | `m` | `b` | `;` | `:` | `#` | `3` | `v` |
| **NE** | `l` | `e` | `s` | `5` | - | - | - | - |
| **E**  | `1` | `y` | `t` | `w` | `8` | - | `/` | - |
| **SE** | `!` | - | `d` | `o` | `c` | `7` | - | - |
| **S**  | - | `(` | `'` | `f` | `n` | `u` | `4` | `"` |
| **SW** | `)` | - | - | `q` | `g` | `i` | `0` | - |
| **W**  | `9` | `@` | - | - | `2` | `k` | `r` | `j` |
| **NW** | `p` | `z` | - | `?` | - | `6` | `x` | `h` |

## 2. Right-Dial Single Swipe Mapping
The following macros are mapped to a pure right-dial push (while the left dial is neutral):

*   **N**: SHIFT
*   **E**: SPACE
*   **W**: BACKSPACE
*   **S**: ENTER
*   **NE**: CAPSLOCK
*   **SE**: TAB
*   **SW**: `.`
*   **NW**: `,`

## 3. Objective Scores
*   **Total Score**: 0.7541
*   **Unigram Contribution**: 0.6207
*   **Bigram Contribution**: 0.1334

## 4. Improvement vs. Random Baseline
*   **Random Baseline Average**: 1.2576
*   **Improvement**: **40.04% reduction in typing cost**

## 5. Top 20 Letters by Difficulty (Chord Index)
*Sorted by highest geometric/chord physical difficulty penalty in the chosen mapping:*
1.  **b**: 1.2
2.  **q**: 1.2
3.  **z**: 1.2
4.  **c**: 0.9
5.  **d**: 0.9
6.  **f**: 0.9
7.  **g**: 0.9
8.  **j**: 0.9
9.  **k**: 0.9
10. **l**: 0.9
11. **m**: 0.9
12. **p**: 0.9
13. **s**: 0.9
14. **u**: 0.9
15. **v**: 0.9
16. **w**: 0.9
17. **x**: 0.9
18. **y**: 0.9
19. **a**: 0.6
20. **e**: 0.6

## 6. Transition Heatmap Summary
*Top optimal bigram transition distance penalties measured (Distance × Frequency × 0.5):*
1.  **e → SPACE**: 0.013783
2.  **SPACE → a**: 0.010258
3.  **n → SPACE**: 0.008964
4.  **SPACE → t**: 0.008523
5.  **SPACE → i**: 0.007788
6.  **SPACE → h**: 0.007553
7.  **r → SPACE**: 0.006728
8.  **SPACE → o**: 0.005749
9.  **o → SPACE**: 0.005084
10. **f → SPACE**: 0.004253
