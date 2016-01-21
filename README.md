# BreakoutGame

This is an Android coursework project I and my teammate Aishwarya created as part of our 'User Interface Design and Mobile App Development' course in Fall'15.

This is an Android Studio project and you could directly import this project in Android SDK to test it.

##About the game:
The game play is same as the arcade game 'Breakout' with the below variations.
- Hitting on the side walls is allowed. You could tilt the mobile to avoid the same as well(Uses Accelerometer to do this).
- Only when all the bricks are hit, the game ends.

##Implementational details:
- Uses two fragments: one for the game control and another for the actual game.
- Game control fragment lets you control the ball speed, start the game and view high scores.
- Uses accelerometer sensor for controlling the ball movement and the touch sensor for controlling the paddle(allows touch/ drag movements).
- Tracks 10 high scores using a text file at the backend.

##Screenshots:
![Main screen](https://cloud.githubusercontent.com/assets/4311177/12473551/bf2263fe-bfd8-11e5-87d7-85e86a0e9106.png)
![High scores screen](https://cloud.githubusercontent.com/assets/4311177/12473540/b8ad77ca-bfd8-11e5-8b94-edcb597fa5bd.png)
