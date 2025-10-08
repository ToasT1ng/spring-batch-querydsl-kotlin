---
description: Generate or update tests for a specific class
---

Generate comprehensive test code for the specified class following the project's testing standards:

1. Read the target class implementation
2. Identify all public methods and behaviors to test
3. Create or update test file using:
   - Kotest FunSpec style
   - MockK for mocking
   - No SpringBootTest (pure unit tests)
   - Same package structure in test directory

4. Test coverage should include:
   - Happy path scenarios
   - Edge cases (empty, null, boundary conditions)
   - Error handling
   - Mock verification for important interactions

5. Follow patterns from existing tests in `spring-batch-querydsl-kotlin-reader/src/test`
6. Use `.claude/test-template.md` for reference
7. Run tests after generation to ensure they pass

The class to test: {{prompt}}
