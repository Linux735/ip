param(
    [string]$PlanPath = "test/ui-test-plan.md"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Get-CodeBlock {
    param(
        [string]$CaseText,
        [string]$Heading
    )

    $pattern = '(?ms)^### ' + [regex]::Escape($Heading) + '\s*\r?\n```(?:text)?\r?\n(.*?)\r?\n```'
    $match = [regex]::Match($CaseText, $pattern)
    if (-not $match.Success) {
        throw "Missing '$Heading' code block in a test case."
    }
    return $match.Groups[1].Value
}

function Normalize-Output {
    param([string]$Text)

    $lines = ($Text -replace "`r`n", "`n") -split "`n"
    $normalizedLines = foreach ($line in $lines) {
        $line -replace '^ ', ''
    }
    return ($normalizedLines -join "`n").TrimEnd("`n")
}

if (-not (Test-Path -LiteralPath $PlanPath)) {
    throw "UI test plan not found: $PlanPath"
}

$javacVersion = (& javac -version 2>&1 | Out-String)
if ($javacVersion -notmatch "javac 25\.") {
    throw "Java 25 is required, but found: $javacVersion"
}

$plan = Get-Content -Raw -LiteralPath $PlanPath
$caseMatches = [regex]::Matches($plan, "(?ms)^## Test case: (.+?)\r?$\r?\n(.*?)(?=^## Test case:|\z)")
if ($caseMatches.Count -eq 0) {
    throw "The UI test plan contains no test cases."
}

$classDirectory = Join-Path ([System.IO.Path]::GetTempPath()) ("alzara-ui-" + [guid]::NewGuid())
New-Item -ItemType Directory -Path $classDirectory | Out-Null

try {
    $sources = Get-ChildItem -Path "src/main/java" -Filter "*.java" -File | Select-Object -ExpandProperty FullName
    $compilerOutput = & javac -d $classDirectory $sources 2>&1 | Out-String
    if ($LASTEXITCODE -ne 0) {
        throw "Compilation failed:`n$compilerOutput"
    }

    foreach ($caseMatch in $caseMatches) {
        $name = $caseMatch.Groups[1].Value.Trim()
        $caseText = $caseMatch.Groups[2].Value
        $inputs = Get-CodeBlock $caseText "Inputs"
        $expected = Get-CodeBlock $caseText "Expected output"

        $processInfo = [System.Diagnostics.ProcessStartInfo]::new()
        $processInfo.FileName = "java"
        $processInfo.Arguments = "-cp `"$classDirectory`" Alzara"
        $processInfo.UseShellExecute = $false
        $processInfo.RedirectStandardInput = $true
        $processInfo.RedirectStandardOutput = $true
        $processInfo.RedirectStandardError = $true

        $process = [System.Diagnostics.Process]::new()
        $process.StartInfo = $processInfo
        [void]$process.Start()
        $process.StandardInput.Write($inputs + "`n")
        $process.StandardInput.Close()
        $actual = $process.StandardOutput.ReadToEnd()
        $standardError = $process.StandardError.ReadToEnd()
        $process.WaitForExit()

        Write-Output "=== Test case: $name ==="
        Write-Output "Console input:"
        Write-Output $inputs
        Write-Output "Console output:"
        Write-Output $actual

        if ($process.ExitCode -ne 0 -or (Normalize-Output $actual) -cne (Normalize-Output $expected)) {
            Write-Output "FAILED: $name"
            Write-Output "Expected output:"
            Write-Output $expected
            Write-Output "Actual output:"
            Write-Output $actual
            if ($standardError) {
                Write-Output "Standard error:"
                Write-Output $standardError
            }
            exit 1
        }

        Write-Output "PASSED: $name"
    }
} finally {
    Remove-Item -LiteralPath $classDirectory -Recurse -Force -ErrorAction SilentlyContinue
}
