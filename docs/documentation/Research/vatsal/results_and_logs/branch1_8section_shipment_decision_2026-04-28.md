# Branch 1 - 8-Section Shipment Decision (2026-04-28)

## Decision

`No-Go` for replacing the current shipped 8-section `Efficiency` layout in place.

Keep the current shipped 8-section map on the product path for now, and treat the exact rerun winner as a research candidate rather than an immediate shipping change.

## Why This Is The Decision

### The exact rerun winner is real

Under the explicit shipped-symbol policy:

- current shipped `efficiencyNormalMap`: `0.95690`, `70.5` predicted WPM
- exact rerun winner: `0.90377`, `71.7` predicted WPM

That is a meaningful optimizer win: about `5.5%` better score and `1.2` predicted WPM.

### The continuity cost is too high for an in-place swap

- the current shipped map and the exact rerun winner match in only `5 / 64` slots
- that means `59 / 64` chord assignments would change under the same built-in `Efficiency` preset name
- for existing users, this would behave like replacing the layout almost completely rather than tuning it

This is too large to treat as a silent improvement.

### The learnability evidence is still missing

The exact rerun is a `replay-core` result, not a `behavioral-core` result under the Branch 0 scorecard.

That means the branch currently proves:

- the new layout is stronger under the optimizer objective

It does not yet prove:

- how hard it is to learn versus the current shipped 8-section layout
- how much it hurts continuity for returning users
- whether the `1.2` predicted WPM gain survives real onboarding, practice, and correction cost

Branch 6 already showed on the 6-section side that raw efficiency wins alone are not enough to settle product decisions when learning cost is unresolved. The same caution applies here.

## Product Outcome

### Ship now

- keep the current shipped 8-section `Efficiency` layout unchanged

### Preserve as research artifact

- keep the exact rerun winner recorded in `branch1_exact_8section_rerun_2026-04-28.md`
- keep the raw optimizer log and comparison artifacts checked in

### If revisited later

- do not replace the current 8-section `Efficiency` preset in place without a dedicated migration decision
- if the winner is ever tested on-product, prefer a separate opt-in layout variant or a focused migration experiment instead of silently mutating the existing preset

## Short Rationale

Branch 1 cleaned the baseline successfully:

- 6-section: direct implementation approved and shipped in shared code
- 8-section: exact rerun completed, but immediate product replacement rejected

That split is defensible because the 6-section change converged the product onto the already measured winner, while the 8-section change would replace almost the entire learned map for a relatively modest predicted speed gain.