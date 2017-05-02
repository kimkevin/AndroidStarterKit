ASK - Android Starter Kit
=====

[![Build Status](https://travis-ci.org/kimkevin/AndroidStarterKit.svg?branch=master)](https://travis-ci.org/kimkevin/AndroidStarterKit)

![](https://github.com/kimkevin/AndroidStarterKit/blob/master/assets/ask_logo_100.png)

Use the application generator command-line tool for a new android project to create an application skeleton simply and quickly. For more detail, please visit to [the website](http://androidstarterkit.com/)

## Download and Run

```bash
$ git clone git@github.com:kimkevin/AndroidStarterKit.git
$ cd AndroidStarterKit

# Add new path
$ echo 'export PATH=$PATH:/path/to/AndroidStarterKit'
```

For example, the following command generate the Android project of the following path which included in the GridView and RecyclerView for layouts and the Firebase Analytics and Crash Reporting. Use the following command to do so:

```bash
$ ask -l gv,rv -m fa,fc /path/to/android/project
```

If the command-line doesn’t have the project path for [dir] option, the new project will created in an output folder.

```bash
$ ask -l gv,rv -m fa,fc
```

Sync it with Gradle and run it.

## How it works

![](https://github.com/kimkevin/AndroidStarterKit/blob/master/assets/ask_demo.gif)


## License

[MIT](https://opensource.org/licenses/MIT)  

Copyright (c) 2016-present, “KimKevin” Yongjun Kim  
