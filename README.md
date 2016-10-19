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
5. Glide - Image Loader

### Usage: ./ask [options] [dir]
					 ./ask [-w <widget>] [dir]
					 ./ask [-t <tab>] [dir] [args...]

#### Options
```bash
* -h, --help           output usage information
* -w, --widget <view>  add <view> support (RecyclerView | ListView) 
* -t, --tab <tab>      add <tab> support (SlidingTabLayout | SlidingIconTabLayout)
                       (defaults to <tab> which has two fragment)

* args...              arguments should be <view> for adding to <tab> 
                       use - for default <view>
```

#### Dir
```bash
* -p, --path				   sample project path 
                       (defaults to local path for ask-sample in root project)

```

### Run

```bash
# Make your new project

$ ./ask -w <widget> -p your_project_path 

```

> Examples  
1. ./ask -w ListView 
2. ./ask -w RecyclerView -p /Users/kevin/Documents/AndroidStarterKit/AndroidSample 
3. ./ask -t SlidingTabLayout -p /Users/kevin/Documents/AndroidStarterKit/AndroidSample ListView,-,ListView

### License

Copyright (c) 2013 “KimKevin” Yongjun Kim  
Licensed under the MIT license.

