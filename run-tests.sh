#!/bin/bash

set -e

./gradlew testDebugUnitTest
./gradlew connectedAndroidTest

# print green message 'success' if both commands succeeds
echo -e "\e[32mTests passed!\e[0m"
