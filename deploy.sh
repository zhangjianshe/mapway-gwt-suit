#/bin/bash
export REPO_ID=cangling
export REPO_URL=https://nexus.cangling.io/repository/maven-releases/

mvn clean compile package install deploy -DREPO_URL=$REPO_URL -DREPO_ID=$REPO_ID