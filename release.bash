#!/usr/bin/env bash

if [[ debug == "$1" ]]; then
  set -xv   # this line will enable debug
fi

RELEASE_VERSION="$(date -u +%Y.%m.%d_%H.%M.%S)"
DEVELOPMENT_VERSION="$(date -u +%Y.%m.%d_%H.%M)-NEXT-SNAPSHOT"
RELEASE_NOTES_FILE="RELEASE-NOTES-NEXT.md"
RELEASE_NOTES_HISTORY_FILE="RELEASE-NOTES.md"
RELEASE_NOTES_TEMP_HISTORY_FILE="RELEASE-NOTES.md.tmp"

# Displays the specified error message and exits with status code=1.
error() {
    local BRIGHT_RED='\033[0;91m'
    local NC='\033[0m' # No Color
    echo -e "${BRIGHT_RED}Error: ${1:-Something went wrong}${NC}"
    exit 1
}

echo "Releasing v${RELEASE_VERSION}"

echo "Checking for uncommitted changes..."
if ! mvn scm:check-local-modification; then
    error "There are uncommitted changes. Please revert or commit them before releasing."
fi
echo "... OK. The workspace is clean."

echo "Checking Release Notes for this release (RELEASE-NOTES-NEXT.md)..."
read -r -d '' RELEASE_NOTES_TEMPLATE << EOD
## Summary

This is a template and should be replaced by actual release notes...

## Changes in this Release
- First Change
- Second Change
EOD

# Read actual release notes (convert CRLF to LF in case file is checked out on Windows)
ACTUAL_RELEASE_NOTES=$(tr -d '\r' < "${RELEASE_NOTES_FILE}")

# Verify that `RELEASE-NOTES-NEXT.md` has release notes describing this release.
if [ -z "${ACTUAL_RELEASE_NOTES}" ] ; then
    error "The Release Notes are empty. Please update ${RELEASE_NOTES_FILE}."
fi

#echo -n "${ACTUAL_RELEASE_NOTES}" | md5sum
#echo -n "${RELEASE_NOTES_TEMPLATE}" | md5sum

# Verify that `RELEASE-NOTES-NEXT.md` has actual release notes and not just the template.
if [ "${ACTUAL_RELEASE_NOTES}" == "${RELEASE_NOTES_TEMPLATE}" ]; then
    error "The Release Notes are just the template. Please update ${RELEASE_NOTES_FILE}."
fi
echo "... OK. Release Notes exist."

echo "Prepending ${RELEASE_NOTES_FILE} to ${RELEASE_NOTES_HISTORY_FILE}..."
# Copy the contents of `RELEASE-NOTES-NEXT.md` to the top of `RELEASE-NOTES.md`, with the tag name as a level-1 header.
echo "# ami v${RELEASE_VERSION}"     > "${RELEASE_NOTES_TEMP_HISTORY_FILE}"
echo "${ACTUAL_RELEASE_NOTES}"      >> "${RELEASE_NOTES_TEMP_HISTORY_FILE}"
echo ""                             >> "${RELEASE_NOTES_TEMP_HISTORY_FILE}"
echo ""                             >> "${RELEASE_NOTES_TEMP_HISTORY_FILE}"
cat "${RELEASE_NOTES_HISTORY_FILE}" >> "${RELEASE_NOTES_TEMP_HISTORY_FILE}"
mv "${RELEASE_NOTES_TEMP_HISTORY_FILE}" "${RELEASE_NOTES_HISTORY_FILE}"
echo "... Updated ${RELEASE_NOTES_HISTORY_FILE} OK."

echo "Updating release version in pom.xml to ${RELEASE_VERSION}..."
# This `sed` syntax works on both GNU and BSD/macOS, due to a *non-empty* option-argument:
# Create a backup file *temporarily* and remove it on success.
sed -i.bak "s/<version>[0-9][0-9][0-9][0-9]\.[0-9][0-9]\.[0-9][0-9]_[0-9][0-9].*</<version>${RELEASE_VERSION}</g" pom.xml && rm pom.xml.bak

echo "Committing pom.xml and RELEASE-NOTES.md..."
if ! git commit --quiet -m "Release ami version ${RELEASE_VERSION}" pom.xml RELEASE-NOTES.md
then
    error "Unable to commit pom.xml and RELEASE-NOTES.md"
fi
echo "... Committed OK."

echo "Tagging last commit with v${RELEASE_VERSION}..."
if ! git tag -m "Release ami version ${RELEASE_VERSION}" "v${RELEASE_VERSION}"
then
    error "Unable to tag the last commit"
fi
echo "... Tagged last commit OK."

# If we wanted to run `mvn deploy` to publish to GitHub Packages, this is where we would do it.
# see https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages
#mvn -Darguments=-DskipTests -DskipTests -Dmaven.test.skip=true clean package  deploy

echo ""
echo "Preparing for next development cycle..."

echo "Resetting ${RELEASE_NOTES_FILE} to template..."
echo "${RELEASE_NOTES_TEMPLATE}" > "${RELEASE_NOTES_FILE}"

echo "Updating version in pom.xml to v${DEVELOPMENT_VERSION}..."
sed -i.bak "s/<version>${RELEASE_VERSION}</<version>${DEVELOPMENT_VERSION}</g" pom.xml && rm pom.xml.bak

echo "Committing pom.xml and ${RELEASE_NOTES_FILE}..."
if ! git commit --quiet -m "Preparing for next development cycle" pom.xml "${RELEASE_NOTES_FILE}"
then
    error "Unable to commit pom.xml and ${RELEASE_NOTES_FILE}"
fi
echo "... Committed SNAPSHOT version and reset Release Notes OK."

echo "Pushing changes..."
if ! git push --tags --progress origin master
then
    error "Unable push changes to master branch in origin repository"
fi
echo "... Pushed changes OK."
echo ""

echo "Release ${RELEASE_VERSION} completed successfully."
echo "See https://github.com/petermr/ami3/actions to monitor the GitHub Actions triggered by this tag."

