#!/usr/bin/env bash
set -euo pipefail
cd -- "$(dirname -- "$0")"
mkdir -p out
javac --release 17 -d out $(find src/main/java -name '*.java')
cp -R src/main/resources/. out/
exec java -cp out com.vehicleservice.Main "$@"
