# B21_AI_Group_06
Project for Level 4 IT Quality Assurance module

## Links

- [Test Case Document](https://docs.google.com/spreadsheets/d/16ZdDdVzM8Bu1Xdfkisf543dGNmaPUD_AMswfmfRjRZY/edit?gid=1924560121#gid=1924560121)
- [Defect Report](https://docs.google.com/document/d/1QZfpvXcbmoHj__fLtoOFaSlClM1j-gfvMj7cnmU24Z4/edit?pli=1&tab=t.0)

> NOTE: Request access to the above documents if you do not have it already.

## Guidelines
- [Assignment Guidelines](https://drive.google.com/file/d/1JRnsofbYWJRmvVcJjGeMyjn5jlCHmy5m/view?usp=drivesdk)
- [SRS](https://drive.google.com/file/d/1H4Fjcm0fhMJtv9Oahn2v6En9zrnVE-6u/view?usp=drivesdk)
- [Deployment & Access](https://drive.google.com/file/d/1YvHVMOHSltwft_CWrMzuVun-Gr1Db1Af/view?usp=drivesdk)

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
