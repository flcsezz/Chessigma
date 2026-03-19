# Contributing to Chessigma

## Conventions
- **Branch Naming**: `feature/name`, `fix/description`, `chore/task`
- **Commit Messages**: [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) - `feat:`, `fix:`, `chore:`

## Documentation Updates (Mandatory)

After completing any task, update all relevant files:

| File | When | What |
| :--- | :--- | :--- |
| `docs/PROGRESS.md` | Every milestone | `- **YYYY-MM-DD**: Task (Agent) - Description` |
| `docs/CURRENT_TASK.md` | Task changes | Mark `[x]` objectives, update next steps |
| `docs/PLAN.md` | Phase complete | Check `[x]` items, add new sub-tasks |
| `docs/DECISIONS.md` | Architecture choice | New `## ADR N:` section |
| `README.md` | User-facing change | Update features/setup |
| `GEMINI.md` | Structure change | Update file tree/notes |

### Checklist
- [ ] `PROGRESS.md` updated
- [ ] `CURRENT_TASK.md` reflects new state
- [ ] `PLAN.md` checkboxes updated
- [ ] `DECISIONS.md` has new ADR if applicable
- [ ] `README.md` updated if user-facing
- [ ] `GEMINI.md` updated if structure changed

## Development Workflow
