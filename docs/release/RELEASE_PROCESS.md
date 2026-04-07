# Automated Release Process

This document describes the end-to-end release lifecycle for PVPC-Android.
All steps after branch creation are automated via Fastlane and GitHub Actions.

> **Prerequisites**: One-time manual setup must be completed before the first
> release. See [MANUAL_STEPS.md](MANUAL_STEPS.md).

---

## Version Format — CalVer

Versions follow the **CalVer** `YYYY.MM.PATCH` format:

| Component | Description | Example |
|-----------|-------------|---------|
| `YYYY` | Four-digit year | `2026` |
| `MM` | Zero-padded month | `04` |
| `PATCH` | Sequential number within the month, starting at `1` | `1`, `2`, `3` |

Examples: `2026.04.1`, `2026.04.2`, `2026.05.1` (resets at new month)

### versionCode Formula

Android's `versionCode` is derived from the CalVer components:

```
versionCode = YYYY × 10,000 + MM × 100 + PATCH
```

| versionName | versionCode |
|-------------|-------------|
| `2026.04.1` | `20260401` |
| `2026.04.2` | `20260402` |
| `2026.05.1` | `20260501` |

This formula is always increasing as time progresses and stays well within
Android's 2,100,000,000 limit.

---

## Branch Strategy

```
develop  ──────────────────────────────────────────▶  (feature development)
             │
             ├── release/2026.04.1  ──▶  PR  ──▶  main  ──▶  (tagged release)
             │                                        │
             └────────────────────────────────────────┘
                        (merge main back to develop)
```

| Branch | Purpose |
|--------|---------|
| `develop` | Active development, target for feature PRs |
| `release/YYYY.MM.PATCH` | Release preparation; auto-bumps version on push |
| `main` | Production-ready code only; populated via merged release PRs |

---

## Starting a Release

### Step 1 — Determine the next version number

Check the latest release tag for the current month:

```bash
git fetch --tags
git tag -l "v$(date +%Y.%m).*" | sort -V | tail -1 | sed 's/^v//'
```

> **Note:** Tags are created with a `v` prefix (e.g. `v2026.04.1`) by the `deploy_github` Fastlane lane. The command above strips the prefix so the output matches the CalVer format used in branch names.

- If the output is `2026.04.2`, the next patch is `3` → create `release/2026.04.3`
- If no tags exist for this month, start at `1` → create `release/2026.04.1`

### Step 2 — Create and push the release branch

```bash
git checkout develop
git pull origin develop

# Replace X with the patch number from Step 1
git checkout -b release/$(date +%Y.%m).X
git push -u origin release/$(date +%Y.%m).X
```

### Step 3 — Automatic version bump

Within seconds, the `Release — Version Bump` GitHub Actions workflow fires and:

1. Parses the CalVer from the branch name
2. Writes the new `versionCode` and `versionName` into `app/build.gradle.kts`
3. Commits with message: `chore: bump version to YYYY.MM.PATCH (versionCode) [skip ci]`
4. Pushes the commit back to the release branch

Pull the bump locally before making further changes:

```bash
git pull
```

> **Idempotency**: The version bump is idempotent. Subsequent pushes to the
> same release branch re-run the workflow but skip the commit if the version
> is already correct.

### Step 4 — Release-specific changes (optional)

Make any changes needed for the release (e.g. last-minute bug fixes, string
freeze). Push normally — the version bump workflow will not re-commit since
the version is already correct.

### Step 5 — Open a Pull Request

Open a PR from `release/YYYY.MM.PATCH` → `main`.

- Request review from at least one team member
- The regular CI does not run on `release/**` branches (handled by the
  release workflows instead)

### Step 6 — Merge triggers full deployment

Once the PR is approved and merged into `main`, the `Release — Deploy`
workflow fires automatically and runs the full pipeline:

| Step | Tool | Output |
|------|------|--------|
| Unit tests | Gradle | Pass/fail |
| Signed APK | Gradle + keystore | `app-release.apk` |
| Signed AAB | Gradle + keystore | `app-release.aab` |
| GitHub Release | `set_github_release` | Tag `vYYYY.MM.PATCH` + APK asset |
| Play Store | `upload_to_play_store` | Internal track draft |
| Firebase | `firebase_app_distribution` | Distributed to `internal-testers` group |

> The Play Store upload lands as a **draft** in the internal testing track.
> Promote it to higher tracks (alpha, beta, production) manually via the
> Play Console after QA sign-off.

### Step 7 — Merge main back to develop

After the release succeeds, sync the version bump back to `develop`:

```bash
git checkout develop
git pull origin develop
git merge main
git push origin develop
```

---

## Fastlane Lanes Reference

All lanes are defined in `fastlane/Fastfile`.

| Lane | Description | When to run |
|------|-------------|-------------|
| `increment_calver` | Bumps version from branch name (idempotent) | Automatically via `release.yml` |
| `build_release` | Builds signed APK + AAB | Automatically via `deploy` lane |
| `deploy_github` | Creates GitHub Release with APK | Automatically via `deploy` lane |
| `deploy_play` | Uploads AAB to Play Store internal track | Automatically via `deploy` lane |
| `deploy_firebase` | Distributes APK to Firebase testers | Automatically via `deploy` lane |
| `deploy` | Full pipeline: test → build → all channels | Automatically via `release_deploy.yml` |

To run a lane locally (requires signing credentials in env):

```bash
bundle exec fastlane <lane_name>
```

---

## GitHub Actions Workflows

| Workflow | Trigger | Job |
|----------|---------|-----|
| `ci.yml` | Push to any branch except `release/**` and `main` | Build + test |
| `release.yml` | Push to `release/**` | Version bump only |
| `release_deploy.yml` | `release/*` PR merged into `main` | Full deploy pipeline |

---

## Required GitHub Secrets

These must be configured in **Settings → Secrets and variables → Actions**
before the first automated release. See [MANUAL_STEPS.md](MANUAL_STEPS.md)
for how to obtain each value.

| Secret | Used by |
|--------|---------|
| `KEYSTORE_BASE64` | Signing APK + AAB |
| `KEY_ALIAS` | Signing APK + AAB |
| `KEY_PASSWORD` | Signing APK + AAB |
| `STORE_PASSWORD` | Signing APK + AAB |
| `PLAY_STORE_JSON_KEY_BASE64` | Play Store upload |
| `FIREBASE_APP_ID` | Firebase distribution |
| `FIREBASE_TOKEN` | Firebase distribution |

`GITHUB_TOKEN` is provided automatically by Actions — no manual setup needed.

---

## Troubleshooting

### Version bump workflow did not run
- Confirm the branch name matches exactly `release/YYYY.MM.PATCH` (zero-padded month)
- Check the Actions tab for workflow run errors

### versionCode regression error
- The new versionCode is lower than the current one in `build.gradle.kts`
- This means you used a patch number lower than a previous release in the same month
- Fix: use a higher patch number in the branch name

### Play Store upload fails with "App not found"
- The first upload must be done manually via the Play Console web UI
- See [MANUAL_STEPS.md — Step 5](MANUAL_STEPS.md#step-5--first-time-play-store-upload-required)

### Firebase token expired
- Regenerate: `firebase login:ci`
- Update the `FIREBASE_TOKEN` secret in GitHub Settings
- Consider migrating to a service account for longer-lived credentials
