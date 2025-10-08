---
description: Fix failing tests after code changes
---

Analyze and fix failing tests in the project:

1. Run tests to identify failures:
   ```bash
   ./gradlew :spring-batch-querydsl-kotlin-reader:test
   ```

2. For each failing test:
   - Read the test file
   - Read the implementation being tested
   - Identify what changed that caused the failure
   - Update test expectations/mocks to match new behavior
   - Ensure test still validates the correct behavior

3. If new functionality was added:
   - Add new test cases for the new behavior
   - Follow existing test patterns

4. Re-run tests to verify fixes

5. Report:
   - What was broken
   - What was fixed
   - New test coverage added (if any)
