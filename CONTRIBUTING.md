# Contributing to Spring Batch QueryDSL Kotlin

Thank you for your interest in contributing! This document provides guidelines for contributing to this project.

## Development Setup

### Prerequisites

- JDK 21
- Gradle 8.x (wrapper included)
- Git

### Getting Started

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/YOUR_USERNAME/spring-batch-querydsl-kotlin.git
   cd spring-batch-querydsl-kotlin
   ```

3. Build the project:
   ```bash
   ./gradlew build
   ```

4. Run tests:
   ```bash
   ./gradlew test
   ```

## Development Workflow

### Creating a Branch

Create a feature branch from `main`:

```bash
git checkout -b feature/your-feature-name
```

Branch naming conventions:
- `feature/` - New features
- `fix/` - Bug fixes
- `test/` - Test additions/improvements
- `docs/` - Documentation updates
- `refactor/` - Code refactoring

### Making Changes

1. Write your code following the project's coding standards
2. Add tests for your changes
3. Ensure all tests pass:
   ```bash
   ./gradlew test
   ```

4. Check test coverage:
   ```bash
   ./gradlew jacocoTestReport
   ```
   Open: `spring-batch-querydsl-kotlin-reader/build/reports/jacoco/test/html/index.html`

### Testing Guidelines

- Use **Kotest** for all tests (FunSpec style)
- Use **MockK** for mocking
- Follow existing test patterns in the codebase
- Aim for high test coverage (70%+ overall, 60%+ per class)
- Test edge cases and error conditions

Example test structure:

```kotlin
class MyFeatureTest : FunSpec({
    test("should do something when condition is met") {
        // Given
        val input = mockk<Input>()

        // When
        val result = myFeature.process(input)

        // Then
        result shouldBe expectedOutput
    }
})
```

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions small and focused
- Avoid code duplication

### Committing Changes

Write clear, descriptive commit messages:

```
Add support for string-based no-offset pagination

- Implement BaseNoOffsetStringOptions
- Add tests for string field pagination
- Update documentation
```

## Pull Request Process

### Before Submitting

1. Ensure all tests pass locally:
   ```bash
   ./gradlew test
   ```

2. Run the build:
   ```bash
   ./gradlew build
   ```

3. Update documentation if needed

4. Commit your changes with clear messages

### Submitting a PR

1. Push your branch to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```

2. Create a Pull Request on GitHub

3. Fill out the PR template completely

4. Wait for CI checks to complete

### CI Checks

All PRs must pass the following automated checks:

- ✅ **All tests pass** (both modules)
- ✅ **Test coverage meets minimum thresholds**
- ✅ **Code compiles without errors**
- ✅ **No dependency vulnerabilities**

The CI pipeline runs:
- Unit tests in parallel for both modules
- JaCoCo coverage reports
- Kotlin compilation checks
- Dependency verification

### Review Process

1. Maintainers will review your PR
2. Address any requested changes
3. Once approved, your PR will be merged

## Project Structure

```
spring-batch-querydsl-kotlin/
├── spring-batch-querydsl-kotlin-reader/    # Core library
│   ├── src/main/kotlin/                     # Production code
│   └── src/test/kotlin/                     # Test code
└── spring-batch-querydsl-kotlin-sample/    # Sample application
    ├── src/main/kotlin/                     # Sample code
    └── src/test/kotlin/                     # Sample tests
```

## Testing Strategy

### Unit Tests

- Test individual classes in isolation
- Use MockK for dependencies
- Focus on edge cases and error handling

### Integration Tests (Sample Module)

- Test complete workflows
- Use H2 in-memory database
- Verify batch job execution

## Code Coverage Goals

- **Overall**: 70% minimum
- **Per Class**: 60% minimum
- **Critical paths**: 100% (ItemReaders, Options)

Excluded from coverage:
- QueryDSL generated Q-classes
- Configuration classes

## Getting Help

- Check existing issues on GitHub
- Read the project documentation
- Ask questions in issue comments

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on the code, not the person

## License

By contributing, you agree that your contributions will be licensed under the same license as the original project.
