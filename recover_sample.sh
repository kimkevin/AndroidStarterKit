#!/bin/bash

# rm -rf AndroidSample/app/*
# cp -R AndroidSample.bak/app/* AndroidSample/app

rm -rf AndroidSample/app/build.gradle
cp -R AndroidSample.bak/app/build.gradle AndroidSample/app/build.gradle

rm -rf AndroidSample/app/src/main/res/layout/*
cp -R AndroidSample.bak/app/src/main/res/layout/* AndroidSample/app/src/main/res/layout

rm -rf AndroidSample/app/src/main/res/values/*
cp -R AndroidSample.bak/app/src/main/res/values/* AndroidSample/app/src/main/res/values

rm -rf AndroidSample/app/src/main/java/com/kimkevin/sample/*
cp -R AndroidSample.bak/app/src/main/java/com/kimkevin/sample/* AndroidSample/app/src/main/java/com/kimkevin/sample

echo 'Success to Recover Sample Project for AndroidStarter Developer'
