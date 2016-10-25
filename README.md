ASK - Android Starter Kit
=====

Android Starter Kit is meant to start developing app **simply** and **quickly**.  
In this project we offer some `Android` modules that are most commonly used imported using command line.

![](https://github.com/kimkevin/AndroidStarterKit/blob/master/assets/ask.gif)

This `AndroidModule` contains the following modules:

1. RecyclerView
2. ListView
3. SlidingTabLayout 
4. SlidingIconTabLayout 

## Usage
```bash
Usage: ask [options] [dir]

Options:

First option must be a layout specifier
  -l -layout <widget>...   add <widget> support : rv(RecyclerView), lv(ListView), sv(ScrollView), -(Default View)

  -h, --help               output usage information
  -i, --icon               tab icon instead of text more than 2 widgets
```

## Run

```bash
# 1. Make your new project

# 2. If you just want one fragment
$ ./ask -l <widget> your_project_path 
# or more than 2 fragments
$ ./ask -l <widget>... your_project_path
```

> **Examples**
```bash
$ ./ask -l rv 
$ ./ask -l lv,lv /AndroidStarterKit/AndroidSample 
$ ./ask -l rv,-,rv /AndroidStarterKit/AndroidSample
$ ./ask -l lv,lv,- -i /AndroidStarterKit/AndroidSample
```

## License

Copyright (c) 2013 “KimKevin” Yongjun Kim  
Licensed under the MIT license.

