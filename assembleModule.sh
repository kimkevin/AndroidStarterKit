#!/bin/bash

showprogress() {
  PROC_ID=$!
  delay=0.1
  sp="/-\|"
  count=0

  while kill -0 "$PROC_ID" >/dev/null 2>&1; do
    printf "\r[${sp:count++:1}] %s" "$1"
    ((count==${#sp})) && count=0
    sleep $delay
  done

  printf "\r%s                                      \n" "Build Finished"
}

APP_HOME=`dirname "$0"`
cd "$APP_HOME" >/dev/null

./gradlew :$1:build >/dev/null 2>&1 &
showprogress "Building with your command options..."
