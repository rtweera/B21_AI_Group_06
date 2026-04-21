# Contributing Guidelines

## Branching Convention
We use a short-lived feature branch strategy. All development must occur in feature branches.

**Format:** `category/description-in-kebab-case`


| Category | Usage |
| :--- | :--- |
| feat/ | New features or enhancements |
| fix/ | Bug fixes |
| docs/ | Documentation updates |
| test/ | Adding or modifying tests |
| refactor/ | Code cleanup (no functional change) |

**Process:**
1. Branch from `dev`: `git checkout dev && git pull && git checkout -b feat/task-name`
2. Commit changes using the convention below.
3. Push branch: `git push origin feat/task-name`
4. Open a Pull Request into the `dev` branch.
5. Delete the feature branch after a successful merge.

## Commit Message Convention
We follow Conventional Commits to maintain a readable project history.

**Format:** `<type>: <description>`


| Type | Meaning |
| :--- | :--- |
| feat | A new feature |
| fix | A bug fix |
| docs | Documentation changes |
| refactor | Code change that neither fixes a bug nor adds a feature |
| test | Adding or updating tests |

**Note:** Use the imperative mood (e.g., "add" instead of "added").
