ASK - Android Stater Kit [beta]
=====

Android Stater Kit is meant to start developing app **simply** and **quickly**.  
In this project we offer some `Android` modules that are most commonly used imported using command line.

This `AndroidModule` contains the following modules:

1. RecyclerView
2. ListView
3. SlidingTabLayout (unsupported)
4. DrawerLayout (unsupported)
5. FloatingActionButton (unsupported)
6. etc (Image Loader(glide, picasso), famous samples) (unsupported)

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

### Run (only working RecyclerView with no option)

```bash
# Make your new project

$ ./ask -p your_project_path -w ListView 

# or

$ cd bin/production/AndroidStater
$ java AndroidStarter -p your_project_path -w RecyclerView
```

> Examples  
1. ./ask -p /Users/kevin/Documents/AndroidStarterKit/AndroidSample -w ListView 
2. java AndroidStarter -p /Users/kevin/Documents/AndroidStarterKit/AndroidSample -w RecyclerView

### License

Copyright (c) 2013 “KimKevin” Yongjun Kim  
Licensed under the MIT license.
