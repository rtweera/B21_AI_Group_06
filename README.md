# B21_AI_Group_06
Project for Level 4 IT Quality Assurance module

## Links

| Resource | URL |
| :--- | :--- |
| Test Case Document | [Google Sheets](https://docs.google.com/spreadsheets/d/16ZdDdVzM8Bu1Xdfkisf543dGNmaPUD_AMswfmfRjRZY/edit?gid=1924560121#gid=1924560121) |
| Defect Report | [Google Docs](https://docs.google.com/document/d/1QZfpvXcbmoHj__fLtoOFaSlClM1j-gfvMj7cnmU24Z4/edit?pli=1&tab=t.0) |
| Allure Test Report | [GitHub Pages](https://rtweera.github.io/B21_AI_Group_06/) |
| Assignment Guidelines | [Drive](https://drive.google.com/file/d/1JRnsofbYWJRmvVcJjGeMyjn5jlCHmy5m/view?usp=drivesdk) |
| SRS | [Drive](https://drive.google.com/file/d/1H4Fjcm0fhMJtv9Oahn2v6En9zrnVE-6u/view?usp=drivesdk) |
| Deployment & Access | [Drive](https://drive.google.com/file/d/1YvHVMOHSltwft_CWrMzuVun-Gr1Db1Af/view?usp=drivesdk) |

> Request access to Google documents if you do not have it already.

---

## Tech Stack

| Tool | Version |
| :--- | :--- |
| Java | 21 (Temurin) |
| Maven | 3.x |
| Cucumber | 7.34.3 |
| Playwright (Java) | 1.44.0 |
| TestNG | 7.9.0 |
| Allure | 2.27.0 (JVM) / 3.x (CLI) |

---

## Prerequisites

- **JDK 21** or higher
- **Maven** (on `PATH`)
- **Git**
- **Node.js / npm** — only required for generating Allure reports locally (`npm install -g allure`)

---

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/rtweera/B21_AI_Group_06.git
cd B21_AI_Group_06
```

### 2. Configure the application

Copy the example properties file and fill in your database credentials:

```bash
cp app/application.properties.example app/application.properties
```

Edit `app/application.properties` and set your MySQL root password and port (`8088` by default).

### 3. Install Playwright browsers

```bash
mvn exec:java -e -D"exec.mainClass=com.microsoft.playwright.CLI" -D"exec.args=install --with-deps"
```

---

## Running the Application

The backend **must be running** before you execute any tests.

```bash
cd app && java -jar qa-training-app.jar
```

> The `cd app` is required — the jar reads `application.properties` relative to its working directory.

The app starts on **http://localhost:8088**.

---

## Running the Tests

### Run the full suite

```bash
mvn clean test
```

`mvn clean` also wipes `allure-results/` and `allure-report/` so you always get a fresh report.

### Run by student tag

```bash
mvn test -Dcucumber.filter.tags="@215552U"
mvn test -Dcucumber.filter.tags="@215565L"
mvn test -Dcucumber.filter.tags="@215527A"
mvn test -Dcucumber.filter.tags="@215564H"
```

### Run multiple tags

```bash
# Any of these tags
mvn test -Dcucumber.filter.tags="@215552U or @215565L"

# Only scenarios tagged with both
mvn test -Dcucumber.filter.tags="@UI and @215565L"

# Exclude a tag
mvn test -Dcucumber.filter.tags="@UI and not @215564H"
```

### Run API or UI tests only

```bash
mvn test -Dcucumber.filter.tags="@API"
mvn test -Dcucumber.filter.tags="@UI"
```

### Run in headless mode (no browser window)

```bash
mvn test -Dheadless=true
```

The default in `config.properties` is `headless=false` (browser visible). CI always passes `-Dheadless=true`.

### Override the base URL

```bash
mvn test -Dbase.url=http://localhost:8088
```

Useful if the app is running on a different port or host.

---

## Allure Reports

### Local — generate and open

After running tests, an `allure-results/` directory is created.

**Option A — static report** (opens `allure-report/index.html` in your browser):

```bash
mvn allure:report
# Then open: allure-report/index.html
```

**Option B — live server** (starts a local web server and opens the report automatically):

```bash
mvn allure:serve
```

> Both options require the Allure Maven plugin already declared in `pom.xml` — no extra install needed for these two commands.

### Local — Allure CLI (optional)

If you have the Allure CLI installed (`npm install -g allure`):

```bash
allure generate allure-results -o allure-report
allure open allure-report
```

### CI — GitHub Pages

Every CI run publishes the latest report to GitHub Pages automatically.
View it at: **https://rtweera.github.io/B21_AI_Group_06/**

---

## CI/CD — GitHub Actions

The workflow file is at [`.github/workflows/tests.yml`](.github/workflows/tests.yml).

### Triggers

| Trigger | When |
| :--- | :--- |
| **Push** | Automatically on every push to the `test` branch |
| **Manual** | Via the **Actions** tab → select workflow → **Run workflow** |

### Manual run with custom tags

When triggering manually you can set a Cucumber tag expression in the input field. The default is:

```
@215552U or @215565L or @215527A or @215564H
```

### What the workflow does

1. Spins up a MySQL 8.0 service container
2. Checks out code and sets up JDK 21
3. Installs Playwright browsers and system dependencies
4. Configures `application.properties` from the example file and starts the backend jar
5. Runs `mvn clean test` with the selected tag filter in headless mode
6. Generates an Allure 3 report from `allure-results/`
7. Deploys the report to the `gh-pages` branch (visible at the GitHub Pages URL above)

Test failures do **not** stop the workflow — the report is always generated and deployed even if tests fail.

---

## Test Configuration

`src/test/resources/config/config.properties` controls local test behaviour:

| Property | Default | Description |
| :--- | :--- | :--- |
| `base.url` | `http://localhost:8088` | Backend URL for UI and API tests |
| `admin.username` | `admin` | Admin credentials |
| `admin.password` | `admin123` | |
| `user.username` | `testuser` | Normal user credentials |
| `user.password` | `test123` | |
| `headless` | `false` | Set to `true` to suppress the browser window |

---

## Repository Governance

To ensure code quality and project integrity, this repository uses a two-stage branching strategy. `main` represents stable production code; `dev` is the integration and testing environment.

### Branch Protection Rules

| Rule | Requirement | Goal |
| :--- | :--- | :--- |
| Pull Requests | Required for all changes | Prevent direct pushes and unreviewed code |
| Peer Review | 1 mandatory approval | Ensure a second pair of eyes on all logic |
| Block Force Pushes | Enabled | Protect the integrity of git history |
| Conversation Resolution | All comments must be resolved | Ensure all reviewer feedback is addressed |
| Admin Bypass | Disabled | Standardize quality across all team members |

### Development Workflow

1. Create a feature branch from `dev`.
2. Open a Pull Request from your feature branch into `dev`.
3. Once `dev` reaches a stable milestone, open a final PR from `dev` into `main`.
4. To trigger CI, merge or push to the `test` branch, or run the workflow manually from the Actions tab.
