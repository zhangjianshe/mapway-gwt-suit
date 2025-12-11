#!/bin/bash
# DEPLOY_SNAPSHOT: Creates a temporary branch, pushes it to trigger a snapshot build, and deletes the branch.
set -e

# Configuration
TARGET_REPOSITORY=cental
# For simplicity, we assume 'main' is the branch that holds the X.Y.Z-SNAPSHOT version
BASE_BRANCH="master"
SNAPSHOT_VERSION_IN_POM="1.0.0-SNAPSHOT" # Expected version in pom.xml

POM_FILE="pom.xml"
# 1. Check prerequisites and uncommitted changes (as in your script)
# ...

# 2. Check the current POM version (Crucial sanity check)
CURRENT_VERSION=$(grep -m 1 '<version>' "$POM_FILE" | sed -E 's/.*<version>(.*)<\/version>.*/\1/')
if [[ "$CURRENT_VERSION" != *"-SNAPSHOT" ]]; then
    echo "Error: Current POM version ($CURRENT_VERSION) is not a SNAPSHOT. Run a release first." >&2
    exit 1
fi

echo "Current Snapshot Version: $CURRENT_VERSION"

# 3. Create a unique snapshot branch for CI
BRANCH_SNAPSHOT="snapshot/$(date +%Y%m%d-%H%M%S)"

echo "Creating branch: $BRANCH_SNAPSHOT"

# Ensure we are on the base branch and up to date
git checkout  "$BASE_BRANCH"
git pull "$TARGET_REPOSITORY" "$BASE_BRANCH"

# Create the temporary branch
git checkout -b "$BRANCH_SNAPSHOT"

# 4. Push the branch to trigger the GitHub Action
echo "Pushing $BRANCH_SNAPSHOT to trigger snapshot deployment..."
git push "$TARGET_REPOSITORY" "$BRANCH_SNAPSHOT"

# 5. Clean up: Switch back and delete the temporary branch locally and remotely
git checkout "$BASE_BRANCH"
echo "Cleaning up local branch $BRANCH_SNAPSHOT..."
git branch -D "$BRANCH_SNAPSHOT"
# If you don't want to clutter the remote, you can delete the remote branch after deployment is finished:
# execute git push "$TARGET_REPOSITORY" --delete "$BRANCH_SNAPSHOT"

echo "✅ Success! Snapshot deployment for $CURRENT_VERSION triggered on $BRANCH_SNAPSHOT."