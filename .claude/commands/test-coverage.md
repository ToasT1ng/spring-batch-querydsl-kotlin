---
description: Check test coverage and suggest improvements
---

Analyze test coverage for the reader module:

1. List all production classes in `spring-batch-querydsl-kotlin-reader/src/main/kotlin`
2. List all test classes in `spring-batch-querydsl-kotlin-reader/src/test/kotlin`
3. Identify classes without tests or with incomplete coverage
4. For each untested/undertested class:
   - Identify public methods
   - Identify critical paths
   - Suggest test cases to add

5. Report:
   - Coverage summary (classes with/without tests)
   - Recommended test additions
   - Priority (critical/important/nice-to-have)
