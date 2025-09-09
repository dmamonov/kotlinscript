#!/bin/zsh
set -e

./gradlew build

cd build/distributions
rm -rf ansi5-1.0-SNAPSHOT
unzip ansi5-1.0-SNAPSHOT.zip

cd ansi5-1.0-SNAPSHOT/bin
./ansi5