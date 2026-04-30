#!/usr/bin/env bash

set -euo pipefail

variant="release"
skip_gradle=0

usage() {
    cat <<'EOF'
Usage: export-native-debug-symbols.sh [--variant debug|release] [--skip-gradle]

Creates app/build/outputs/native-debug-symbols/<variant>/native-debug-symbols-manual.zip
from the merged native libraries packaged into the Android app.
EOF
}

capitalize_variant() {
    local input="$1"
    local first_char rest

    first_char=$(printf '%s' "${input%${input#?}}" | tr '[:lower:]' '[:upper:]')
    rest=${input#?}
    printf '%s%s' "$first_char" "$rest"
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --variant)
            if [[ $# -lt 2 ]]; then
                echo "Missing value for --variant" >&2
                exit 1
            fi
            variant="$2"
            shift 2
            ;;
        --skip-gradle)
            skip_gradle=1
            shift
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
            echo "Unknown argument: $1" >&2
            usage >&2
            exit 1
            ;;
    esac
done

case "$variant" in
    debug|release)
        ;;
    *)
        echo "Variant must be 'debug' or 'release'." >&2
        exit 1
        ;;
esac

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
android_root="$(cd "$script_dir/.." && pwd)"
task_variant="$(capitalize_variant "$variant")"
gradle_wrapper="$android_root/gradlew"
native_lib_root="$android_root/app/build/intermediates/merged_native_libs/$variant/merge${task_variant}NativeLibs/out/lib"
output_dir="$android_root/app/build/outputs/native-debug-symbols/$variant"
output_zip="$output_dir/native-debug-symbols-manual.zip"
file_list="$(mktemp "${TMPDIR:-/tmp}/erick-native-symbols.XXXXXX")"

cleanup() {
    rm -f "$file_list"
}

trap cleanup EXIT

if [[ "$skip_gradle" -eq 0 ]]; then
    if [[ ! -f "$gradle_wrapper" ]]; then
        echo "Could not find Gradle wrapper at $gradle_wrapper" >&2
        exit 1
    fi

    echo "Refreshing merged native libraries for '$variant'..."
    (
        cd "$android_root"
        "$gradle_wrapper" ":app:merge${task_variant}NativeLibs"
    )
fi

if [[ ! -d "$native_lib_root" ]]; then
    echo "Merged native library directory not found: $native_lib_root" >&2
    exit 1
fi

if ! command -v zip >/dev/null 2>&1; then
    echo "The 'zip' command is required to create the archive." >&2
    exit 1
fi

(
    cd "$native_lib_root"
    find . -type f -name '*.so' | sed 's#^\./##'
) | LC_ALL=C sort > "$file_list"

if [[ ! -s "$file_list" ]]; then
    echo "No native libraries were found under $native_lib_root" >&2
    exit 1
fi

mkdir -p "$output_dir"
rm -f "$output_zip"

(
    cd "$native_lib_root"
    zip -q -X "$output_zip" -@ < "$file_list"
)

echo "Created native symbols zip: $output_zip"
echo "Included libraries:"

while IFS= read -r native_library; do
    abi=${native_library%%/*}
    name=${native_library##*/}
    size=$(wc -c < "$native_lib_root/$native_library" | tr -d '[:space:]')
    printf '%-11s %-30s %s\n' "$abi" "$name" "$size"
done < "$file_list"