#!/bin/bash
set -e
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ARCH=$(uname -m)
export BROWSER=${1:-chrome}

export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)
export BROWSER=${1:-chrome}
export BUILD_MODE=${2:-nobuild}

docker compose down
docker_containers=$(docker ps -a -q)
docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'rococo' || true)
required_docker_images=$(docker compose config --images)
need_build=false

echo "Browser: $BROWSER"
echo "Build mode: $BUILD_MODE"

if [ "$BUILD_MODE" = "build" ]; then
          need_build=true
fi

echo '### Java version ###'
java --version
for image in $required_docker_images; do
  if ! docker image inspect "$image" >/dev/null 2>&1; then
        echo "Missing: $image"
        need_build=true
      fi
done

if [ "$need_build" = true ]; then
  if [ ! -z "$docker_images" ]; then
    echo "### Remove images: $docker_images ###"
    docker rmi $docker_images
  fi
  echo "### Build images ###"
  bash ./gradlew clean
  bash ./gradlew jibDockerBuild -x :rococo-e-2-e-tests:test -Duser.timezone=UTC
else
  echo "### Images already exist, skip build ###"
fi
if [ "$BROWSER" = "firefox" ]; then
  docker image inspect selenoid/vnc_firefox:125.0 >/dev/null 2>&1 || docker pull selenoid/vnc_firefox:125.0
else
  docker image inspect twilio/selenoid:chrome_stable_140 >/dev/null 2>&1 || docker pull twilio/selenoid:chrome_stable_140
fi
docker compose up -d
docker ps -a
