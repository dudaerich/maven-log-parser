#!/bin/bash

./gradlew installApp

echo "Copy binary file to /usr/local/bin."
cp build/install/maven-log-parser/bin/maven-log-parser /usr/local/bin
echo "Done."
echo "Copy library files to /usr/local/lib"
cp build/install/maven-log-parser/lib/* /usr/local/lib
echo "Done."
echo "Installation is successfully done."
