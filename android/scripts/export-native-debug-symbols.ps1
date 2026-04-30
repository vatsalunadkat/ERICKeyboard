param(
    [ValidateSet("debug", "release")]
    [string]$Variant = "release",

    [switch]$SkipGradle
)

$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.IO.Compression.FileSystem

function Get-VariantTaskName {
    param([string]$Name)

    if ([string]::IsNullOrWhiteSpace($Name)) {
        throw "Variant name must not be empty."
    }

    return $Name.Substring(0, 1).ToUpperInvariant() + $Name.Substring(1)
}

$androidRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$taskVariant = Get-VariantTaskName -Name $Variant
$gradleWrapper = Join-Path $androidRoot "gradlew.bat"
$nativeLibRoot = Join-Path $androidRoot "app\build\intermediates\merged_native_libs\$Variant\merge${taskVariant}NativeLibs\out\lib"
$outputDir = Join-Path $androidRoot "app\build\outputs\native-debug-symbols\$Variant"
$outputZip = Join-Path $outputDir "native-debug-symbols-manual.zip"

if (-not $SkipGradle) {
    if (-not (Test-Path $gradleWrapper)) {
        throw "Could not find Gradle wrapper at $gradleWrapper"
    }

    Write-Host "Refreshing merged native libraries for '$Variant'..."
    & $gradleWrapper ":app:merge${taskVariant}NativeLibs"
}

if (-not (Test-Path $nativeLibRoot)) {
    throw "Merged native library directory not found: $nativeLibRoot"
}

$nativeLibraries = Get-ChildItem -Path $nativeLibRoot -Recurse -File -Filter *.so
if (-not $nativeLibraries) {
    throw "No native libraries were found under $nativeLibRoot"
}

New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
if (Test-Path $outputZip) {
    Remove-Item $outputZip -Force
}

try {
    $zipArchive = [System.IO.Compression.ZipFile]::Open(
        $outputZip,
        [System.IO.Compression.ZipArchiveMode]::Create
    )

    try {
        foreach ($nativeLibrary in ($nativeLibraries | Sort-Object FullName)) {
            $relativePath = $nativeLibrary.FullName.Substring($nativeLibRoot.Length).TrimStart([char[]]@('\', '/'))
            $entryName = $relativePath -replace '\\', '/'

            [System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile(
                $zipArchive,
                $nativeLibrary.FullName,
                $entryName,
                [System.IO.Compression.CompressionLevel]::Optimal
            ) | Out-Null
        }
    }
    finally {
        $zipArchive.Dispose()
    }

    Write-Host "Created native symbols zip: $outputZip"
    Write-Host "Included libraries:"

    $nativeLibraries |
        Sort-Object FullName |
        Select-Object @{Name = "Abi"; Expression = { $_.Directory.Name }}, Name, Length |
        Format-Table -AutoSize
}
finally {
    if ($zipArchive) {
        $zipArchive.Dispose()
    }
}