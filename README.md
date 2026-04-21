# B21_AI_Group_06
Project for Level 4 IT Quality Assurance module

## Repository Governance and QA
To ensure code quality and project integrity, this repository utilizes a two-stage branching strategy. The 'main' branch represents stable production code, while the 'dev' branch serves as the integration and testing environment.

### Branch Protection Rules
The following rules apply to both 'main' and 'dev' branches to maintain high quality standards.


| Rule | Requirement | Goal |
| :--- | :--- | :--- |
| Pull Requests | Required for all changes | Prevent direct pushes and unreviewed code. |
| Peer Review | 1 Mandatory Approval | Ensure a second pair of eyes on all logic. |
| Block Force Pushes | Enabled | Protect the integrity of the git history. |
| Conversation Resolution | All comments must be resolved | Ensure all reviewer feedback is addressed. |
| Admin Bypass | Disabled | Standardize quality across all team members. |

### Development Workflow
1. Feature branches are created from 'dev'.
2. Pull Requests are opened from feature branches into 'dev'.
3. Once 'dev' reaches a stable milestone, a final PR is made from 'dev' into 'main'.
