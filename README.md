ASK - Android Starter Kit
=====

Android Starter Kit is meant to start developing app **simply** and **quickly**.  
In this project we offer some `Android` modules that are most commonly used imported using command line.

![](https://github.com/kimkevin/AndroidStarterKit/blob/master/assets/ask_00.gif)

This `ask-module` contains the following modules:

1. RecyclerView
2. ListView
3. SlidingTabLayout 
4. SlidingIconTabLayout 

## Getting Started

```bash
$ git clone git@github.com:kimkevin/AndroidStarterKit.git
$ cd AndroidStarterKit

# Adding ask path to bash_profile
$ pwd
/kevin/git/AndroidStarterKit
$ echo 'export PATH=$PATH:/kevin/git/AndroidStarterKit' >> ~/.bash_profile
```

## Usage
```bash
Usage: ask [options] [dir]

Options:
First option must be a layout specifier
  -l -layout <widget>...   add <widget> support : rv(RecyclerView), lv(ListView), sv(ScrollView), -(Default)

  -h, --help               output usage information
  -i, --icon               tab icon instead of text more than 2 widgets
```

## Run

```bash
# 1. Make your new project

# 2. If you just want one fragment
$ ask -l <widget> your_project_path 
# or more than 2 fragments
$ ask -l <widget>... your_project_path
```

> **Examples**
```bash
$ ask -l rv 
$ ask -l lv,lv /AndroidStarterKit/AndroidSample 
$ ask -l rv,-,rv /AndroidStarterKit/AndroidSample
$ ask -l lv,lv,- -i /AndroidStarterKit/AndroidSample
```

## License

Copyright (c) 2013 “KimKevin” Yongjun Kim  
Licensed under the MIT license.

