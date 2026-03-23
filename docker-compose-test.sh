#!/bin/bash
set -e
source ./docker.properties

export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ARCH=$(uname -m)
export BROWSER=${1:-chrome}

echo '### Java version ###'
java --version
echo "Browser: $BROWSER"

./gradlew clean

echo '### Build images ###'
docker compose -f docker-compose.yaml build

if [ "$1" = "push" ]; then
  echo '### Push images ###'
  docker compose -f docker-compose.yaml push
fi

if [ "$BROWSER" = "firefox" ]; then
  docker image inspect selenoid/vnc_firefox:125.0 >/dev/null 2>&1 || docker pull selenoid/vnc_firefox:125.0
else
  docker image inspect twilio/selenoid:chrome_stable_140 >/dev/null 2>&1 || docker pull twilio/selenoid:chrome_stable_140
fi

echo "### Build images ###"
bash ./gradlew clean
bash ./gradlew jibDockerBuild
docker compose up -d
docker ps -a