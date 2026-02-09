---
allowed-tools: Bash(grep:*), Bash(git describe:*), Bash(git branch:*), Bash(git log:*), Bash(git diff:*), Bash(git status:*), Bash(git tag:*), Bash(git add:*), Bash(git commit:*), Bash(git reset:*), Bash(gh repo view:*), Bash(gh release view:*), Bash(gh run:*), Bash(gh run list:*), Bash(gh run watch:*), Bash(gh run rerun:*), Bash(gh workflow run:*), Bash(./gradlew:*), Edit, Read
description: Release a new version - bump version, commit, tag, push, publish to Maven Central, create GitHub release
argument-hint: "[major|minor|patch|<explicit-version>]"
---

# Release

## Current State

- Current version: !`grep '^library' gradle/libs.versions.toml`
- Latest git tag: !`git describe --tags --abbrev=0 2>/dev/null || echo "(no tags)"`
- Current branch: !`git branch --show-current`
- GitHub repo: !`gh repo view --json nameWithOwner -q .nameWithOwner`

## Instructions

Follow these steps precisely to create a new release.

### Step 1: Preflight checks

1. Verify we are on the `master` branch. If not, **stop** and warn the user.
2. Verify the working tree is clean (`git status --porcelain`). If not, **stop** and warn the user.
3. Pull latest: `git pull --ff-only`. If this fails, **stop** and warn the user.

### Step 2: Determine version bump

The current version uses pre-release identifiers (e.g., `1.0.0-alpha03`). Version bumping rules:

If `$ARGUMENTS` is one of `major`, `minor`, or `patch`:
- **patch**: increment the last numeric segment (e.g., `1.0.0-alpha03` → `1.0.0-alpha04`, or `1.2.3` → `1.2.4`)
- **minor**: `X.Y.Z` → `X.Y+1.0` (drops any pre-release suffix)
- **major**: `X.Y.Z` → `X+1.0.0` (drops any pre-release suffix) — always confirm with user first

If `$ARGUMENTS` is an explicit version string (e.g., `1.0.0-beta01` or `1.0.0`), use it directly.

Otherwise, analyze the commits since the last tag and suggest an appropriate bump. Present the proposed new version to the user and ask them to confirm using `AskUserQuestion`.

### Step 3: Summarize changes

List the commits since the last tag using `git log <last-tag>..HEAD --oneline`. Write concise, user-facing bullet points for each meaningful change. These will be used in the GitHub release body alongside the auto-generated changelog.

### Step 4: Execute the release

Do all of the following in order:

1. Edit `gradle/libs.versions.toml` to update the `library` version on line 9 to the new version
2. Build to verify nothing is broken:
   ```
   ./gradlew apiDump
   ./gradlew :core:build
   ```
   If the build fails, **stop** and help the user fix the issue before continuing.
3. Stage and commit:
   ```
   git add gradle/libs.versions.toml core/api/
   git commit -m "release: vX.Y.Z"
   ```
4. Tag: `git tag vX.Y.Z`
5. Push: `git push && git push --tags`

### Step 5: Wait for CI

After pushing, monitor the CI workflows triggered by the push:

1. Wait a moment for workflows to trigger, then use `gh run list --branch master --limit 5` to find the runs
2. Use `gh run watch <run-id>` on the test workflow run

If all checks pass, proceed to Step 6.

If any check fails:
- **Flaky/random failure**: Rerun with `gh run rerun <run-id> --failed`, then wait again
- **Real failure**: We need to roll back and fix the issue:
  1. Delete the remote tag: `git push --delete origin vX.Y.Z`
  2. Delete the local tag: `git tag -d vX.Y.Z`
  3. Revert the release commit: `git reset --hard HEAD~1 && git push --force`
  4. Help the user fix the issue, then re-release

### Step 6: Create GitHub release

Create the release on GitHub using the auto-generated changelog:

```
gh release create vX.Y.Z --title "vX.Y.Z" --generate-notes
```

This will:
- Create a GitHub release with auto-generated PR-based changelog
- Automatically trigger the `apple-binaries.yml` workflow (attaches XCFramework archives to the release)

### Step 7: Trigger Maven Central publish

Trigger the publish workflow manually:

```
gh workflow run publish.yml
```

### Step 8: Monitor post-release workflows

1. Use `gh run list --limit 5` to find the publish and apple-binaries workflow runs
2. Watch both with `gh run watch <run-id>`
3. If the publish workflow fails, inform the user — they may need to check Maven Central / Sonatype credentials
4. If apple-binaries fails, it can be re-triggered: `gh workflow run apple-binaries.yml`

Once all workflows complete successfully, report:
- Link to the GitHub release
- Confirm Maven Central publish status
- Confirm Apple XCFramework archives attached to release
