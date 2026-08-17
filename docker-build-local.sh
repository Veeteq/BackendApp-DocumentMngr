#!/bin/bash

set -e

AUTH_JWKS_URI=http://authorization-app:8080/.well-known/jwks.json
APP_NAME="documentmngr-app"
IMAGE_NAME="documentmngr-app"
NETWORK_NAME="backend-network"
HOST_NAME="localhost"
HOST_PORT="8181"
CONTAINER_PORT="8080"

echo "Configuring app network"
if ! docker network inspect "${NETWORK_NAME}" >/dev/null 2>&1; then
  echo "Docker network '${NETWORK_NAME}' does not exist. Creating it..."
  docker network create "${NETWORK_NAME}"
else
  echo "Docker network '${NETWORK_NAME}' already exists."
fi

echo "Building Docker image: ${IMAGE_NAME}"

docker build \
  --progress=plain \
  -t "${IMAGE_NAME}" \
  -f Dockerfile.local .

echo "Stopping old container if exists"

docker rm -f "${APP_NAME}" 2>/dev/null || true

echo "Starting container: ${APP_NAME}"

docker run -d \
  --name "${APP_NAME}" \
  --network "${NETWORK_NAME}" \
  -p "${HOST_PORT}:${CONTAINER_PORT}" \
  -e SERVER_PORT="${CONTAINER_PORT}" \
  -e AUTH_JWKS_URI="${AUTH_JWKS_URI}" \
  "${IMAGE_NAME}"

echo "Container started"
echo "Health check URL:"
echo "http://${HOST_NAME}:${HOST_PORT}/actuator/health"