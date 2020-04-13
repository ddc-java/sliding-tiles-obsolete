## What is it?

**Sliding tiles** is an Android implementation of the classic [_sliding puzzle_](https://en.wikipedia.org/wiki/Sliding_puzzle), or [_15 puzzle_](https://en.wikipedia.org/wiki/15_puzzle). It extends the basic forms of those puzzles in 3 main areas:

* The number of tiles can be set by the user at 8, 15, 24, or 25 (3x3, 4x4, 5x5, or 6x6, respectively, with 1 open space).

* The user can select 1 of 3 included images, or any image from the Android image gallery (e.g. downloaded images, or images captured by the device camera). The puzzle tiles are formed by slices of this image; when the puzzle is solved, the complete image will be shown.

* A timer and move counter are updated as the puzzle is being solved. Thus, the user may take as their objective simply solving the puzzle, or solving the puzzle in as few moves as possible, or solving it in the least amount of time possible.  

## Who is it for?

A 3x3 or 4x4 puzzle can usually be completed in a few minutes; thus, _Sliding Tiles_ is perfect for anyone needing a quick break or distraction. Because it also exercises logical and spatial reasoning, as well as pattern recognition, it can be challenging and intellectually rewarding for those looking to practice those abilities.

Apart from our intent that _Sliding Tiles_ should be a fun and interesting puzzle, the app is primarily intended for use in a curriculum unit of the [Deep Dive Coding](https://deepdivecoding.com/) [Java + Android bootcamp](https://deepdivecoding.com/java-android/). Through development and refinement of this app, students will explore the use of activities, fragments, navigation &amp; options menus, adapter views &amp; adapters, settings/preferences, viewmodels &amp; LiveData, persistence via SQLite with Room, etc.

## How do I use it?

* ![launcher](images/icon.png)Start the game by launching the **Sliding Tiles** icon. 

* ![screen-permission](images/permission-request.png)If this is the first time the app is launched after installation, a request for permission to access images on the device appears.  


The purpose of the game is to solve and complete the image in the least amount of time in the least amount of moves possible.

Sign-in
Either skip the sign in altogether or use the Google Sign In to create a profile, here a user will be able to keep stats in the future.

To Begin game
- Click on any tile to begin game.
- Click on 'New' to begin new game.
- Click on 'Reset' to reset a new game.

Game Play
- Click on tile adjacent to empty spot to move a tile.
- If tile is unable to be moved the game will notify you. 
- Try to slide the tiles in the least amount of moves to create the puzzle image.
- Time and moves will be kept track of as part of the game stats. 
- Progress bar will keep track of far along the puzzle you have gone. 
- Game can be played in Portrait or Landscape mode.

Settings
- Top right section of main game display.
- Click on the three vertical dots to access.
- Sign in and Sign out options can be accessed here.
- License info can be accessed here. 
- Tile animation can be adjusted.
- Puzzle size can be adjusted here.
- Image for the puzzle can be chosen here or uploaded from user library.

## Credits

_Sliding Tiles_ was written by Nick Bennett, with Chris Hughes and Steven Z&uacute;&ntilde;iga.

Copyright 2020 Deep Dive Coding/CNM Ingenuity, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

For copyright &amp; license information on the libraries incorporated into _Sliding Tiles_, see [Notice](notice.md).