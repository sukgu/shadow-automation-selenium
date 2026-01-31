# GitHub Workflows

This directory contains GitHub Actions workflows for automated CI/CD, code quality, and dependency management.

## Workflows

### 1. CI/CD Pipeline (`ci-cd.yml`)
**Triggers:** Push to main branches, tags, pull requests, manual dispatch

**Jobs:**
- **Test**: Runs tests across multiple Java versions (11, 17, 21) and browsers (Chrome, Firefox)
- **Build**: Compiles and packages the project
- **Publish Snapshot**: Publishes snapshot versions to OSSRH for main branch pushes
- **Publish Release**: Publishes releases to Maven Central for version tags
- **Publish GitHub Packages**: Publishes to GitHub Packages

### 2. Code Quality (`code-quality.yml`)
**Triggers:** Push to main branches, pull requests

**Jobs:**
- **Code Quality Analysis**: SonarCloud analysis, test coverage with Codecov
- **SpotBugs**: Static analysis for bug detection

### 3. Security (`security.yml`)
**Triggers:** Weekly schedule, manual dispatch

**Jobs:**
- **Dependency Check**: OWASP dependency vulnerability scanning
- **Dependabot Auto-merge**: Automatically merges minor dependency updates

### 4. Release (`release.yml`)
**Triggers:** Manual dispatch with version input

**Jobs:**
- **Create Release**: Updates version, creates tag, deploys to Maven Central, creates GitHub release

## Required Secrets

Configure these secrets in your GitHub repository settings:

### Maven Central Publishing
- `OSSRH_USERNAME`: Your OSSRH/Sonatype username
- `OSSRH_PASSWORD`: Your OSSRH/Sonatype password
- `GPG_PRIVATE_KEY`: Your GPG private key for artifact signing
- `GPG_PASSPHRASE`: Your GPG key passphrase

### Code Quality (Optional)
- `SONAR_TOKEN`: SonarCloud authentication token

## Setup Instructions

### 1. OSSRH/Maven Central Setup
1. Create an account at https://issues.sonatype.org/
2. Follow the guide: https://central.sonatype.org/publish/publish-guide/
3. Generate GPG keys for artifact signing
4. Add secrets to your GitHub repository

### 2. SonarCloud Setup (Optional)
1. Go to https://sonarcloud.io/
2. Import your GitHub repository
3. Generate a token and add it as `SONAR_TOKEN` secret
4. Update the `sonar.projectKey` and `sonar.organization` in the workflow

### 3. Enable Dependabot
The `dependabot.yml` configuration is already set up to:
- Update Maven dependencies weekly
- Update GitHub Actions weekly
- Auto-assign PRs to the repository owner

## Usage

### Automatic Releases
1. Go to Actions → Release workflow
2. Click "Run workflow"
3. Enter the version number (e.g., "1.2.3")
4. Optionally add release notes
5. The workflow will:
   - Update the version in `pom.xml`
   - Create and push a git tag
   - Deploy to Maven Central
   - Create a GitHub release
   - Prepare the next development version

### Manual Testing
Push to any main branch or create a pull request to trigger the CI/CD pipeline.

### Monitoring
- Check the Actions tab for workflow runs
- Review dependency update PRs from Dependabot
- Monitor code quality reports in SonarCloud
- Check security reports from OWASP Dependency Check

## Browser Testing
The CI pipeline runs tests with different browser profiles:
- Chrome (default)
- Firefox
- Edge (disabled on Linux runners)

Tests use WebDriverManager for automatic driver management.