ASK - Android Starter Kit
=====

Android Starter Kit is meant to start developing app **simply** and **quickly**.  
In this project we offer some `Android` modules that are most commonly used imported using command line.

This `AndroidModule` contains the following modules:

1. RecyclerView
2. ListView
3. SlidingTabLayout 
4. SlidingIconTabLayout 
4. ~~DrawerLayout~~ (coming soon, unsupported)
5. ~~FloatingActionButton~~ (unsupported)
6. ~~etc (Image Loader(glide), famous samples supported by Google)~~ (unsupported)

### Usage: AndroidStater [options] [dir]

#### Options

```bash
* -h, --help                  output usage information
* -w, --widget <view>         add <view> support (RecyclerView, ListView) (defaults to RecyclerView)
```

#### Dir

```bash
* -p, --path                  source project path (defaults to new project)
```

### Run

```bash
# Make your new project

$ ./ask -w <widget> -p your_project_path 

```

> Examples  
1. ./ask -w ListView -p /Users/kevin/Documents/AndroidStarterKit/AndroidSample   
2. ./ask -w RecyclerView -p /Users/kevin/Documents/AndroidStarterKit/AndroidSample 

### License

Copyright (c) 2013 “KimKevin” Yongjun Kim  
Licensed under the MIT license.

