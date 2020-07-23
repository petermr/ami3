#!/usr/bin/env bash

if [[ debug == "$1" ]]; then
  set -xv   # this line will enable debug
fi

RELEASE_VERSION="$(date -u +%Y.%m.%d_%H.%M.%S)"
DEVELOPMENT_VERSION="$(date -u +%Y.%m.%d_%H.%M)-NEXT-SNAPSHOT"
echo "Releasing v${RELEASE_VERSION}"

sed -i '' "s/<version>[0-9][0-9][0-9][0-9]\.[0-9][0-9]\.[0-9][0-9]_[0-9][0-9].*</<version>${RELEASE_VERSION}</g" pom.xml

# The maven scm:checkin goal commits ALL modified files, not just pom.xml...
# see https://stackoverflow.com/questions/48947392/commit-multiple-files-with-maven-scm-plugin
# see https://maven.apache.org/scm/maven-scm-plugin/checkin-mojo.html
# WAS: if mvn scm:checkin -Dmessage="Bump ami version to ${RELEASE_VERSION}" -Dincludes=pom.xml -DpushChanges=false
if git commit -m "Release ami version ${RELEASE_VERSION}" --verbose pom.xml
then

    #git tag -m "Release ami version ${VERSION}"
    # Use scm:check-local-modification to ensure all changes are committed before we tag the commit.
    # see https://maven.apache.org/scm/maven-scm-plugin/tag-mojo.html
    # and https://maven.apache.org/scm/maven-scm-plugin/check-local-modification-mojo.html
    if mvn scm:check-local-modification scm:tag -Dmessage="Release ami version ${RELEASE_VERSION}" -DpushChanges=false
    then
        # mvn deploy to upload our distribution artifacts to GitHub Packages
        # see https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages
        #mvn -Darguments=-DskipTests -DskipTests -Dmaven.test.skip=true clean package  deploy

        echo "Updating to v${DEVELOPMENT_VERSION} for next development cycle..."
        sed -i'' "s/<version>${RELEASE_VERSION}</<version>${DEVELOPMENT_VERSION}</g" pom.xml
        # mvn scm:checkin -Dmessage="Updating ami version to ${DEVELOPMENT_VERSION} for next development cycle" -Dincludes=pom.xml -DpushChanges=true
        git commit -m "Setting ami version to ${DEVELOPMENT_VERSION} for next development cycle" --verbose pom.xml
        git push --tags --progress --porcelain origin master
    fi
fi
echo "Done."

