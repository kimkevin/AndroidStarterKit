ASK - Android Stater Kit [beta]
=====

Android Stater Kit is meant to start developing app **simply** and **quickly**.  
In this project we offer some `Android` modules that are most commonly used imported using command line.

This `AndroidModule` contains the following modules:

1. RecyclerView
2. ListView (unsupported)
3. SlidingTabLayout (unsupported)
4. DrawerLayout (unsupported)
5. FloatingActionButton (unsupported)
6. etc (Image Loader(glide, picasso), famous samples) (unsupported)

### Usage: AndroidStater [options], [dir]

#### Options

```bash
* -h, --help                  output usage information
* -w, --widget <view>         add <view> support (RecyclerView, ListView, SlidingTabLayout) (defaults to RecyclerView)
* -o, --options <view>        add <view> optional support (DrawerLayout(drawer), FloatingActionButton(fab))
* -i, --imageLoader <engine>  add image loader <engine> support (Glide|Picasso)
```

#### Dir

```bash
* -p, --path                  source project path (defaults to new project)
```

### Run (only working RecyclerView with no option)

```bash
# Make your new project

$ ./ask your_project_path

# or

$ cd bin/production/AndroidStater
$ java AndroidStater your_project_path
```

> Examples  
1. java AndroidStater /Users/kimkevin/android/AndroidSample -l recyclerview -o drawerlayout  
2. java AndroidStater /Users/kimkevin/android/AndroidSample -l listview -i glide

### License

Copyright (c) 2013 “KimKevin” Yongjun Kim  
Licensed under the MIT license.
