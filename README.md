ASK - Android Starter Kit
=====

Android Starter Kit is meant to start developing app **simply** and **quickly**.  
In this project we offer some `Android` modules that are most commonly used imported using command line.

> **Creating Android project which has `GridView`, `ListView` and Default `ScrollView` as the multiple sequentially fragments in `ViewPager`**

![](https://github.com/kimkevin/AndroidStarterKit/blob/master/assets/ask_demo.gif)

This `ask-module` contains the following modules:

**Layout**

1. SlidingTabLayout 
2. SlidingIconTabLayout 

**Widget**

1. RecyclerView
2. ListView
3. GridView

## Getting Started

```bash
$ git clone git@github.com:kimkevin/AndroidStarterKit.git
$ cd AndroidStarterKit

# Adding ask path to bash_profile
$ pwd
/local/AndroidStarterKit/ask-app/build/classes/main
$ echo 'export PATH=$PATH:/local/AndroidStarterKit/ask-app/build/classes/main' >> ~/.bash_profile
```

## Usage
```bash
Usage: ask [options] [dir]

Options:
First option must be a layout specifier
  -l -layout <widget>...   add <widget> support: sv(ScrollView), rv(RecyclerView), lv(ListView), gv(GridView)
													 defaults to ScrollView

  -h, --help               output usage information
  -i, --icon               tab icon instead of text more than 2 widgets
```

## Run

```bash
# 1. Make your new project

# 2. If you just want to create one fragment
$ ask -l <widget> your_project_path 
# or more than 2 fragments
$ ask -l <widget>... your_project_path
```

> **Examples**
```bash
$ ask -l rv /samples/MyApplication
$ ask -l gv,lv /samples/MyApplication
$ ask -l gv,lv --icon /samples/MyApplication
```

## License

Copyright (c) 2013 “KimKevin” Yongjun Kim  
Licensed under the MIT license.

