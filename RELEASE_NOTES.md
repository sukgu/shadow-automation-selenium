# Release Notes

## Version 0.1.6 (February 2026)

### 🚀 New Features
- **Enhanced Shadow DOM Support**: Improved support for deeply nested shadow DOM elements
- **Comprehensive Test Suite**: Added extensive test coverage with new test classes:
  - `ComprehensiveShadowTest` - Complete functional testing
  - `ShadowBrowserCompatibilityTest` - Cross-browser compatibility testing
  - `ShadowEdgeCaseTest` - Edge case handling verification
  - `ShadowIntegrationTest` - Integration testing scenarios
  - `ShadowSupportClassesTest` - Support class validation

### 🔧 Improvements
- **CI/CD Pipeline Enhancement**: 
  - Added comprehensive GitHub Actions workflows
  - Implemented SonarQube code quality analysis
  - Added security scanning workflows  
  - Automated release management
  - Code coverage reporting integration

- **Testing Infrastructure**:
  - Added 4-layer nested shadow DOM test case (`testShadowNested`)
  - New test resources for complex scenarios:
    - `nested.html` - Multi-layer shadow DOM structure
    - `complex-selectors.html` - Advanced selector testing
    - `shadow_test.html` - Comprehensive shadow DOM testing
    - `workflow.html` - Workflow testing scenarios
    - `dropdown_with_complex_options.html` - Complex dropdown interactions

### 📦 Dependency Updates
- **Selenium**: Updated to version 4.40.0
- **WebDriverManager**: Upgraded to 6.3.3
- **Maven Surefire Plugin**: Updated to 3.5.4
- **Maven Javadoc Plugin**: Updated to 3.12.0
- **JUnit Jupiter**: Updated to 5.14.2

### 🐛 Bug Fixes
- Fixed multiple shadow DOM interaction issues
- Improved querySelector.js functionality
- Enhanced element detection and interaction reliability
- Resolved workflow and build pipeline issues

### 🔍 Code Quality
- Added SonarQube integration for continuous code quality monitoring
- Implemented comprehensive test coverage reporting
- Enhanced static code analysis
- Improved documentation and code comments

### 🛠️ Technical Improvements
- Enhanced JavaScript querySelector implementation
- Better error handling and logging
- Improved browser compatibility
- Optimized performance for nested shadow DOM operations

### 📋 Testing
- Added comprehensive browser compatibility testing
- Enhanced edge case coverage
- Improved integration test scenarios
- Better test resource management

---

## Previous Versions

### Version 0.1.5 and Earlier
See Git history for detailed changes in previous versions.

---

## Contributors
- **Sushil Gupta** - Primary maintainer and core development
- **Kazım Birol Çağlar** - Nested shadow DOM enhancements and testing improvements

## Links
- [GitHub Repository](https://github.com/sukgu/shadow-automation-selenium)
- [Maven Central](https://search.maven.org/search?q=g:%22io.github.sukgu%22%20AND%20a:%22automation%22)
- [Documentation](https://github.com/sukgu/shadow-automation-selenium#readme)