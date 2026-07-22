param(
    [string]$GradlePropertiesPath = "gradle.properties",
    [switch]$NoStage
)

$resolvedPath = Resolve-Path -LiteralPath $GradlePropertiesPath -ErrorAction Stop
$content = Get-Content -LiteralPath $resolvedPath
$versionLineIndex = -1

for ($i = 0; $i -lt $content.Length; $i++) {
    if ($content[$i] -match '^mod_version=(.+)$') {
        $versionLineIndex = $i
        break
    }
}

if ($versionLineIndex -lt 0) {
    throw "mod_version entry was not found in gradle.properties."
}

$currentVersion = $Matches[1]
if ($currentVersion -notmatch '^(\d+)\.(\d+)\.(\d+)(-.+)?$') {
    throw "mod_version format is not supported: $currentVersion"
}

$major = [int]$Matches[1]
$minor = [int]$Matches[2]
$patch = [int]$Matches[3] + 1
$suffix = $Matches[4]

if ($null -eq $suffix) {
    $suffix = ""
}

$nextVersion = "$major.$minor.$patch$suffix"
$content[$versionLineIndex] = "mod_version=$nextVersion"
Set-Content -LiteralPath $resolvedPath -Value $content

if (-not $NoStage) {
    git add -- $resolvedPath | Out-Null
}

Write-Host "mod_version: $currentVersion -> $nextVersion"
