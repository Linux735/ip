---
name: test-ui
description: Run exact console UI regression tests for this Alzara Java project. Use when code changes can affect commands, console messages, task display, parsing, or program flow, and when adding or updating UI test cases in test/ui-test-plan.md.
---

# Test UI

Maintain [`test/ui-test-plan.md`](../../../test/ui-test-plan.md) before testing. Add or revise a test case whenever a code change affects observable console behaviour.

Each test case must use this exact structure:

````markdown
## Test case: descriptive name

**Aim:** What behaviour this test proves.

### Inputs
```text
commands sent to the program
```

### Expected output
```text
complete expected console output
```
````

Run the plan with Java 25:

```powershell
& .\.codex\skills\test-ui\scripts\run-ui-tests.ps1
```

The runner compiles the program, runs one fresh program session per test case, compares output exactly apart from the final newline and one optional leading space on each line, prints that session's input and output, and stops at the first failure. Do not continue testing after a failure; report its expected and actual output.
