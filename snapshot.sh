#!/bin/bash
# SNAPSHOT will deploy a working version to the repository
set -e
TARGET_REPOSITORY=origin
if [ ! -z "$1" ]; then
    TARGET_REPOSITORY=$1
fi
# Configuration
POM_FILE="pom.xml"
DRY_RUN=false

# Function to execute a command or echo it if in dry-run mode
execute() {
    if $DRY_RUN; then
        echo "DRY-RUN: $*"
    else
        echo "Executing: $*"
        "$@"
        if [ $? -ne 0 ]; then
            echo "Error executing command: '$*'" >&2
            exit 1
        fi
    fi
}

# --- Main Script Logic ---

# Check for dry-run argument
if [[ "$1" == "--dry-run" ]]; then
    DRY_RUN=true
    echo "--- Running in DRY-RUN mode. No changes will be made to files or Git. ---"
fi

# 1. Check for prerequisite tools
if ! command -v git &> /dev/null || ! command -v sed &> /dev/null; then
    echo "Error: 'git' and 'sed' are required but not found." >&2
    exit 1
fi

# 2. Check for the POM file
if [ ! -f "$POM_FILE" ]; then
    echo "Error: Maven POM file not found at $POM_FILE." >&2
    exit 1
fi

# 3. Check for uncommitted changes (prevents tagging a dirty tree)
if [ -n "$(git status --porcelain)" ]; then
    echo "Error: You have uncommitted changes. Please commit or stash them before running." >&2
    exit 1
fi

# SNAPSHOT VERSION is a fix one
NEW_VERSION=1.0.0-SNAPSHOT

if [ $? -ne 0 ]; then
    exit 1 # Exit if bump_version failed
fi

echo "New Version: $NEW_VERSION"

BRANCH_SNAPSHOT=snapshot/$(date +%Y%m%d-%H%M%S)
## 切换到主分支
git checkout master
git checkout -b $BRANCH_SNAPSHOT


# 6. Update the version in pom.xml (Parent)
echo "Updating $POM_FILE to version $NEW_VERSION..."
# This targets the main project version (as already in your script)
execute sed -i.bak "0,/<version>.*<\/version>/s/<version>.*<\/version>/<version>${NEW_VERSION}<\/version>/" "$POM_FILE"
execute rm -f "${POM_FILE}.bak"

# NEW STEP: Update the version in sub-module pom.xml files
SUB_MODULE_POMS=(
"mapway-geo/pom.xml"
"mapway-gwt-ace/pom.xml"
"mapway-gwt-common/pom.xml"
"mapway-gwt-echart/pom.xml"
"mapway-gwt-geo/pom.xml"
"mapway-gwt-map/pom.xml"
"mapway-gwt-mqtt/pom.xml"
"mapway-gwt-openapi/pom.xml"
"mapway-gwt-test/pom.xml"
"mapway-gwt-xterm/pom.xml"
"mapway-module-rbac/pom.xml"
"mapway-spring-tools/pom.xml"
)

echo "Synchronizing parent version in sub-module POMs..."
for  SUB_POM in  ${SUB_MODULE_POMS[@]}; do
  echo "  -> Updating $SUB_POM"
  execute sed -i.bak "/<parent>/,/<\\/parent>/ { s|<version>.*</version>|<version>${NEW_VERSION}</version>|; t; }" "$SUB_POM"
  execute rm -f "${SUB_POM}.bak"
done

execute rm -f "pom.xml.bak"
# 7. Commit the version change
COMMIT_MSG="Snapshot to ${NEW_VERSION}"
execute git add "$POM_FILE"
# Add all sub-module POMs to the commit
execute git add "${SUB_MODULE_POMS[@]}"
execute git commit -m "$COMMIT_MSG"
execute git push $TARGET_REPOSITORY
execute git checkout -

echo ""
if $DRY_RUN; then
    echo "--- DRY-RUN complete. Review the above commands. ---"
    echo "To execute for real, run: ./version_bumper.sh"
else
    echo "✅ Success! Version updated to ${NEW_VERSION}, committed, and tag ${TAG} pushed."
    echo "You may now continue with your deployment or push the main branch changes."
fi
