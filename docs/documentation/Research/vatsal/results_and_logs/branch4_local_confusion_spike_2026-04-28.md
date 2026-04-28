# Branch 4 - Local Controller Confusion Spike

Date: 2026-04-28

## What Was Implemented

The first Branch 4 instrumentation spike is now checked into the Android diagnostics surface.

### Shared additions

- `android/shared/src/commonMain/kotlin/ControllerConfusionMetrics.kt`
- `android/shared/src/commonTest/kotlin/ControllerConfusionMetricsTest.kt`

The shared layer now exposes:

- `ControllerConfusionAnalyzer.directionsForMode(...)`
- `ControllerConfusionAnalyzer.classifyDrillSample(...)`
- `ControllerConfusionAnalyzer.detectSnapBackReversal(...)`
- `ControllerConfusionAnalyzer.deadZoneBand(...)`

Those helpers classify the first local-only bucket family:

- exact match
- adjacent slip
- mirror slip
- dead-zone jitter
- other mismatch
- passive snap-back reversal on release

### Android diagnostics additions

- `android/app/src/main/java/com/vatoo/erick/ControllerDiagnosticsActivity.kt`

The diagnostics screen now includes a local **Confusion Drill** card that:

- lets the user choose the left or right stick
- cycles a target direction for the active dial geometry
- records the currently resolved direction against that target
- stores only aggregate bucket counts on-device for the current diagnostics session
- shows hot expected-to-resolved pairs and passive snap-back counts

The spike deliberately does **not** store raw axis traces or typed text.

## Why This Is Enough For ERICK-150

ERICK-150 is a research spike, not a production telemetry rollout. The goal for Branch 4 was to prove that privacy-safe local evidence collection is possible and to anchor the first confusion buckets in a checked-in surface.

This spike does that without touching the shipping typing path:

- it reuses the shared controller normalization path
- it keeps all collection local to diagnostics
- it records only aggregate buckets and dead-zone bands
- it provides a concrete surface for calibrating the proposed confusion matrix later

## Validation

| Validation | Result |
|---|---|
| `cd android && .\gradlew.bat :shared:testAndroidHostTest` | passed |
| Editor diagnostics for shared classifier, tests, and diagnostics screen | passed |

No controller device run was performed in this environment, so the spike is compile- and test-validated rather than hardware-validated.

## Recommendation

- Treat Branch 4 as completed inside ERICK-150 for the spike phase.
- If later work wants calibrated probabilities instead of ordinal seed weights, split that into a follow-up using this diagnostics drill or a practice-driven local aggregate surface.
- Do not add exported event logs or raw trace storage unless a later ticket explicitly revisits the privacy boundary.