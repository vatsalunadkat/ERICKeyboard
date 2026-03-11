# ERICK Keyboard Layout & Research Report

## 1. Literature Review: Chorded Keyboards & Accessibility

Our review of the provided research papers and external literature highlighted several key findings that inform the design of the ERICK keyboard:

*   **Motor Control and Chorded Keyboards:** According to Gopher and Raij (1988), learning to touch-type on a traditional QWERTY keyboard involves severe cognitive and motor demands. Chorded keyboards significantly reduce the required motor trajectories since the fingers (or joysticks) can rest in a home position. Wu & Shi (2018) note that applying Fitts’ Law to keyboard design means reducing the distance fingers must travel, which a chorded, multiple-characters-per-key design naturally achieves.
*   **Accessibility for the Physically Disabled:** Kirschenbaum et al. (1986) found that for individuals with cerebral palsy, muscular dystrophy, or spasticity, a chordic device requiring minimal physical exertion and stable hand positioning drastically improves transcription ability. The OrbiTouch studies also demonstrate that relying on gross hand/arm movements rather than fine finger dexterity mitigates common motor skill obstacles (McAlindon, 1994).
*   **Cognitive Load and Mental Disabilities:** For users with mental disabilities such as autism, literature (e.g., OrbiTouch whitepapers) emphasizes the importance of visual consistency and reduced distraction. A logical, predictable layout like alphabetical order, paired with consistent color coding, significantly eases the cognitive burden of letter searching.
*   **Efficiency and Frequency Optimization:** For expert normal users, efficiency is maximized when the cognitive structure of the skill is simplified. Gopher and Raij (1988) emphasize that the highest frequency letters should be mapped onto the easiest chord combinations.

## 2. Best Colors for the Right Wheel & Colorblind Accessibility

While the standard design uses a rainbow progression (Red, Orange, Yellow, Green, Blue, Indigo, Violet, Black), this combination can be problematic for individuals with color vision deficiencies (such as Red-Green color blindness). 

**Recommended Colorblind-Safe Palette (The Wong Palette):**
To ensure maximum inclusivity for the 8 sections of the right wheel, we highly recommend using a scientifically verified colorblind-safe palette. The widely accepted Wong 8-Color Palette provides high contrast across various types of color blindness:
1.  **Black** (`#000000`)
2.  **Orange** (`#E69F00`)
3.  **Sky Blue / Light Blue** (`#56B4E9`)
4.  **Bluish Green / Teal** (`#009E73`)
5.  **Yellow** (`#F0E442`)
6.  **Dark Blue** (`#0072B2`)
7.  **Vermillion / Red-Orange** (`#D55E00`)
8.  **Reddish Purple / Pink** (`#CC79A7`)

*Alternative pairing idea:* Use consistent geometry/symbols (like increasing dots or specific shapes) alongside the colors so color isn't the sole identifying factor.

## 3. ERICK Keyboard Full Layout Suggestions

Below are the comprehensive 64-chord layout suggestions for the 3 target user groups. To read the tables, select the Left Joystick Direction (Row) and the Right Joystick Direction (Column).

### Disability Mode: Mental (Autism)
| Left \ Right | N (1st) | NE (2nd) | E (3rd) | SE (4th) | S (5th) | SW (6th) | W (7th) | NW (8th) |
|---|---|---|---|---|---|---|---|---|
| **N** | a | b | c | d | e | ' | ? | ! |
| **NE** | f | g | h | i | j | Space | Bksp | Enter |
| **E** | k | l | m | n | o | Shift | Caps | Tab |
| **SE** | p | q | r | s | t | 1 | 2 | 3 |
| **S** | u | v | w | x | y | 4 | 5 | 6 |
| **SW** | z | 0 | . | , | @ | 7 | 8 | 9 |
| **W** | - | _ | = | + | * | / | \ | |
| **NW** | ( | ) | [ | ] | { | } | < | > |


> *Design rationale: Maintains strict alphabetical/numerical logic. Users simply follow a visual progression, minimizing the need to memorize distinct patterns.*

### Disability Mode: Physical
| Left \ Right | N (1st) | NE (2nd) | E (3rd) | SE (4th) | S (5th) | SW (6th) | W (7th) | NW (8th) |
|---|---|---|---|---|---|---|---|---|
| **N** | Space | u | e | m | t | w | a | f |
| **NE** | g | - | y | _ | p | = | b | + |
| **E** | o | v | Enter | k | i | j | n | x |
| **SE** | q | * | z | / | Caps | @ | Tab | ( |
| **S** | s | 0 | r | 1 | Bksp | 2 | h | 3 |
| **SW** | 4 | ) | 5 | [ | 6 | ] | 7 | { |
| **W** | d | 8 | l | 9 | c | . | Shift | , |
| **NW** | ? | } | ! | < | ' | > | " | |


> *Design rationale: Cardinal directions (N, E, S, W) require less precise muscle isolation than diagonals. Thus, the 16 most frequently used keys (Space, Enter, Backspace, Shift, and top letters) are located exclusively on cardinal-cardinal intersections.*

### Efficiency Mode (Normal Users)
| Left \ Right | N (1st) | NE (2nd) | E (3rd) | SE (4th) | S (5th) | SW (6th) | W (7th) | NW (8th) |
|---|---|---|---|---|---|---|---|---|
| **N** | e | w | i | f | Space | g | n | y |
| **NE** | p | - | b | _ | v | = | k | + |
| **E** | s | j | a | x | r | q | h | z |
| **SE** | Enter | * | Shift | / | Caps | @ | Tab | ( |
| **S** | Bksp | 0 | d | 1 | t | 2 | l | 3 |
| **SW** | 4 | ) | 5 | [ | 6 | ] | 7 | { |
| **W** | c | 8 | u | 9 | m | . | o | , |
| **NW** | ? | } | ! | < | ' | > | " | |


> *Design rationale: Designed strictly for speed akin to Dvorak/Colemak. The absolute most frequent characters reside on identical actions (N+N, S+S) or exact opposites (N+S) for rapid, reflexive chording. Consonants and vowels are geographically separated to encourage rhythmic alternating strokes.*
