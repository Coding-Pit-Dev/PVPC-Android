# Manual Setup Steps

These steps must be performed **once** by a human with the appropriate access.
They are prerequisites for the automated release pipeline.
After completing them, all future releases are fully automated.

---

## Step 1 — Create the Release Keystore

The release keystore signs your APK and AAB. **Store it and its passwords
securely in a password manager** — losing the keystore means you can never
update your Play Store listing again.

Run this on a secure machine (not a shared CI runner):

```bash
keytool -genkey -v \
  -keystore release.keystore \
  -alias pvpc-release-key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -dname "CN=CodingPit, OU=Android, O=CodingPit, L=Spain, ST=Spain, C=ES"
```

You will be prompted for:
- **Keystore password** — save as `STORE_PASSWORD`
- **Key password** — save as `KEY_PASSWORD`

The alias used above is `pvpc-release-key` — save as `KEY_ALIAS`.

> **Keep `release.keystore` out of git.** It is listed in `.gitignore`.
> Back it up to a secure location (e.g. a password manager attachment or
> encrypted cloud storage).

---

## Step 2 — Add GitHub Secrets

Navigate to:
**GitHub → Coding-Pit-Dev/PVPC-Android → Settings → Secrets and variables → Actions → New repository secret**

Add these secrets:

### Signing secrets

```bash
# Encode the keystore as base64 (macOS)
base64 -i release.keystore | pbcopy
# Paste as: KEYSTORE_BASE64

# Add the values you set during Step 1:
# KEY_ALIAS     = pvpc-release-key
# KEY_PASSWORD  = <key password from Step 1>
# STORE_PASSWORD = <keystore password from Step 1>
```

| Secret name | Value |
|-------------|-------|
| `KEYSTORE_BASE64` | Output of `base64 -i release.keystore` |
| `KEY_ALIAS` | `pvpc-release-key` (or the alias you used) |
| `KEY_PASSWORD` | Key password from Step 1 |
| `STORE_PASSWORD` | Keystore password from Step 1 |

---

## Step 3 — Set Up Google Play Console API

This allows Fastlane to upload AABs to the Play Store automatically.

1. Go to [Google Play Console](https://play.google.com/console) → **Setup → API access**
2. Click **Link to a Google Cloud project** (create a new one if needed)
3. In the linked Google Cloud project, go to **IAM & Admin → Service Accounts**
4. Create a new service account:
   - Name: `fastlane-release`
   - Role: **Service Account User** (at the project level)
5. Back in Play Console, find the service account and grant it the **Release Manager** role
6. In Google Cloud, create a JSON key for the service account:
   - Service Accounts → `fastlane-release` → Keys → Add Key → JSON
   - Download the JSON file
7. Encode and add as GitHub Secret:

```bash
# macOS
base64 -i play-store-key.json | pbcopy
# Paste as: PLAY_STORE_JSON_KEY_BASE64
```

> Delete the local `play-store-key.json` after encoding — it contains
> credentials and must not be committed.

---

## Step 4 — Set Up Firebase App Distribution

This distributes pre-release APKs to internal testers before Play Store publication.

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a project (or use an existing one) — e.g. `pvpc-planner`
3. **Add Android app**: click the Android icon, enter package name `com.codingpit.pvpcplanner`
4. Skip downloading `google-services.json` (not needed for App Distribution only)
5. In the Firebase Console sidebar, go to **App Distribution**
6. Note the **App ID** from **Project Settings → Your apps → App ID**
   - Format: `1:XXXXXXXXXXXX:android:XXXXXXXXXXXXXXXX`
   - Add as GitHub Secret: `FIREBASE_APP_ID`
7. Generate a CI token:

```bash
# Install Firebase CLI if not already installed
npm install -g firebase-tools

# Generate a long-lived CI token
firebase login:ci
# Copy the printed token
```

   Add the token as GitHub Secret: `FIREBASE_TOKEN`

8. Create a tester group:
   - Firebase Console → App Distribution → **Testers & Groups** → **Add group**
   - Name: `internal-testers`
   - Add tester email addresses

> **Firebase token expiry**: CI tokens can expire. If distribution fails with
> an auth error, regenerate the token with `firebase login:ci` and update the
> `FIREBASE_TOKEN` secret. For a more robust setup, replace the token with a
> Google service account — see the
> [Firebase App Distribution docs](https://firebase.google.com/docs/app-distribution/authenticate-service-account).

---

## Step 5 — First-Time Play Store Upload (REQUIRED)

> **This step is mandatory before any automated Play Store upload will work.**
> The Play Store API (`supply`) cannot create a new app listing — it can only
> update an existing one.

1. Build a signed release AAB locally:

```bash
# Ensure signing env vars are set first
export KEYSTORE_PATH=/path/to/release.keystore
export STORE_PASSWORD=<your password>
export KEY_ALIAS=pvpc-release-key
export KEY_PASSWORD=<your password>

./gradlew bundleRelease
# Output: app/build/outputs/bundle/release/app-release.aab
```

2. Go to [Google Play Console](https://play.google.com/console)
3. Create a new app (or open existing)
4. Navigate to **Testing → Internal testing → Create new release**
5. Upload the `app-release.aab`
6. Fill in **all required fields**:
   - Release name
   - Release notes (at least one language)
7. **Save and review**, then **Start rollout to internal testing**

After this first upload, all subsequent AAB uploads from the automated
pipeline will succeed.

---

## Step 6 — Verify the Setup

Once all secrets are in place, test the full pipeline end-to-end:

```bash
# Create the first release branch
git checkout develop && git pull
git checkout -b release/$(date +%Y.%m).1
git push -u origin release/$(date +%Y.%m).1
```

Then:
1. **Check the `Release — Version Bump` workflow** fires and commits a version bump
2. Pull the bump: `git pull`
3. Open a PR from the release branch to `main`
4. Merge the PR
5. **Check the `Release — Deploy` workflow** completes all steps:
   - ✅ Tests pass
   - ✅ APK and AAB are built
   - ✅ GitHub Release created with APK attached
   - ✅ AAB appears in Play Store internal track as draft
   - ✅ Testers receive APK via Firebase email notification

---

## Security Checklist

- [ ] `release.keystore` is backed up to a secure location and **not in git**
- [ ] Keystore passwords are stored in a team password manager
- [ ] `play-store-key.json` is deleted from local disk after encoding
- [ ] GitHub Secrets are set at the **repository** level (not environment level unless needed)
- [ ] The `internal-testers` Firebase group contains only authorized testers
- [ ] Firebase CI token is noted so it can be regenerated when it expires
