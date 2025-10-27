#!/bin/bash
# 每一个新的版本 都会产生一个新的分支 在新的分支上打上标签 然后推送到中央仓库
# master
#    |---> v1.0.12
#    |        |-> TAG v1.0.12
#  version.txt is current released version
set -e
# Configuration
POM_FILE="pom.xml"
DRY_RUN=false
VERSION_FILE=version.txt
# --- Utility Functions ---

# Function to parse and bump the version number (patch version)
# E.g., 1.2.3-SNAPSHOT becomes 1.2.4
bump_version() {
    local current_version=$1
    # Strip any trailing -SNAPSHOT or other qualifiers
    local base_version=$(echo "$current_version" | sed 's/-.*//')

    # Split version into major, minor, patch
    if [[ "$base_version" =~ ([0-9]+)\.([0-9]+)\.([0-9]+) ]]; then
        local major=${BASH_REMATCH[1]}
        local minor=${BASH_REMATCH[2]}
        local patch=${BASH_REMATCH[3]}

        # Increment the patch number
        local new_patch=$((patch + 1))
        echo "${major}.${minor}.${new_patch}"
    else
        echo "Error: Version format ($current_version) is not recognized (expected X.Y.Z)." >&2
        return 1
    fi
}

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

# 4. Extract current version from pom.xml
echo "Reading current version from $VERSION_FILE..."
# Use grep and sed to safely extract the version from the main <version> tag
#CURRENT_VERSION=$(grep -m 1 -A 3 '<groupId' "$POM_FILE" | grep -A 3 '<artifactId' | grep -A 1 '<version' | grep '<version' | sed -e 's/.*<version>//' -e 's/<\/version>.*//' | head -n 1 | sed 's/ //g')
CURRENT_VERSION=$(cat "$VERSION_FILE")
if [ -z "$CURRENT_VERSION" ]; then
    echo "Error: Could not extract version from $VERSION_FILE. Ensure version.txt tag is present." >&2
    exit 1
fi

echo "Current Version: $CURRENT_VERSION"

# 5. Calculate new version
NEW_VERSION=$(bump_version "$CURRENT_VERSION")

if [ $? -ne 0 ]; then
    exit 1 # Exit if bump_version failed
fi

echo "New Version: $NEW_VERSION"

#  commit new version info to version.txt
cat "$NEW_VERSION" > version.txt
git add version
git commit -m "change to new version"



TAG="v${NEW_VERSION}"
NEW_BRANCH=release/${TAG}
echo "New Tag: $TAG"

git push origin
git checkout -b $NEW_BRANCH


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

SUB_POM=README.md
echo "  -> Updating $SUB_POM"
execute sed -i.bak "{ s|<version>.*</version>|<version>${NEW_VERSION}</version>|; t; }" "$SUB_POM"
execute rm -f "${SUB_POM}.bak"


execute rm -f "pom.xml.bak"
# 7. Commit the version change
COMMIT_MSG="Release: bump version to ${NEW_VERSION}"
execute git add "$POM_FILE"
# Add all sub-module POMs to the commit
execute git add "${SUB_MODULE_POMS[@]}"
execute git add "README.md"
execute git commit -m "$COMMIT_MSG"


# 8. Tag the commit
echo "Creating tag ${TAG}..."
execute git tag -a "$TAG" -m "Version ${NEW_VERSION}"

# 9. Push the tag to origin
echo "Pushing tag ${TAG} to origin..."
execute git push origin $NEW_BRANCH
execute git push origin "$TAG"
execute git checkout master

echo ""
if $DRY_RUN; then
    echo "--- DRY-RUN complete. Review the above commands. ---"
    echo "To execute for real, run: ./version_bumper.sh"
else
    echo "✅ Success! Version updated to ${NEW_VERSION}, committed, and tag ${TAG} pushed."
    echo "You may now continue with your deployment or push the main branch changes."
fi
