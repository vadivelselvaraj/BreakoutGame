package com.example.karthi.breakoutgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.io.IOException;
import java.util.List;
import java.util.Random;


/*
 * Author : Vel
 * Date : 24/Nov/2015
 * Purpose : Creating the GameArea as a part of BreakoutView fragment
 */
public class GameArea extends Fragment {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    BreakoutView breakoutView;

    //The size of the screen in pixels
    int fragmentWidth;
    int fragmentHeight;
    int screenWidth;
    int screenHeight;

    // The players paddle
    Paddle paddle;

    // A ball
    Ball ball;

    // Up to 200 bricks
    Brick[] bricks = new Brick[200];
    int numBricks = 0;

    // For sound FX
    SoundPool soundPool;
    int beep1ID = -1;
    int beep2ID = -1;
    int beep3ID = -1;
    int loseLifeID = -1;
    int explodeID = -1;

    // The score
    int score = 0;
    int time = 0;
    int maxScore = 0;
    // Lives
    int lives = 0;

    int currentProgress = 0;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    /**
     * Factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Game.
     */
    public static GameArea newInstance(String param1, String param2) {
        GameArea fragment = new GameArea();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /*
     * Author : Vel
     * Date : 24/Nov/2015
     * Purpose : Default Constructor for GameArea
     */
    public GameArea() {
        // Required empty public constructor
    }

    /*
	* Author : Aishwarya
	* Date : 24/Nov/2015
	* Purpose : To create an activity
	*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /*
     * Author : Vel
     * Date : 24/Nov/2015
     * Purpose : Creating a view for the GameArea
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        breakoutView = new BreakoutView(getActivity());
        return breakoutView;
    }

    /*
    * Author : Aishwarya
    * Date : 24/Nov/2015
    * Purpose : Exposes the startGame subroutine for controlArea interaction.
    */
    public void startPlay() {
        breakoutView.startGame();
    }

    /*
    * Author : Vel
    * Date : 24/Nov/2015
    * Purpose : Exposes the updateBallSpeed subroutine for controlArea interaction.
    */
    public void updateBallSpeed(int progress) {
        currentProgress = progress;
        breakoutView.updateBallSpeed(progress);
    }

    /*
    * Author : Aishwarya
    * Date : 24/Nov/2015
    * Purpose : Exposes the updateTimer subroutine for controlArea interaction.
    */
    public void updateTime(int updatedTime) {
        breakoutView.updateTimer(updatedTime);
    }

    /*
    * Author : Aishwarya
    * Date : 24/Nov/2015
    * Purpose :OnButton Pressed UI event
    */
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View fragmentView = getView();
        fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fragmentWidth = getView().getWidth();
                fragmentHeight = getView().getHeight();
                if(fragmentWidth > 0) {
                    Log.i("fragment", "width > 0 w: " + fragmentWidth + " h:" + fragmentHeight);
                    getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    breakoutView.initGameLayer();
                }
            }
        });

        // Initialize interface object
        mListener = (OnFragmentInteractionListener) getActivity();
    }

    /*
	* Author : Aishwarya
	* Date : 24/Nov/2015
	* Purpose : Activity has to Start
	*/
    @Override
    public void onStart() {
        super.onStart();

        // If the view is visible, resume the play.
        if(this.getUserVisibleHint()) {
            Log.i("sensor", "breakoutview start called");
            breakoutView.resume();
        }
    }

    /*
    * Author : Aishwarya
    * Date : 24/Nov/2015
    * Purpose : Activity is stopped
    */
    @Override
    public void onStop() {
        super.onStop();
        Log.i("sensor", "breakoutview stop called");
        breakoutView.pause();
    }

    /*
     * Author : Vel
     * Date : 24/Nov/2015
     * Purpose : To Detach an activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    /*
    * Author : Aishwarya
    * Date : 24/Nov/2015
    * Purpose : Creating a Breakout View class to run Multiple threads and hold an accelometer sensor
    */
    private class BreakoutView extends SurfaceView implements Runnable, SensorEventListener {

        // This is our thread
        Thread gameThread = null;

        // This is new. We need a SurfaceHolder
        // When we use Paint and Canvas in a thread
        // We will see it in action in the draw method soon.
        SurfaceHolder ourHolder;

        // A boolean which we will set and unset
        // when the game is running or not.
        volatile boolean playing;

        // Game is paused at the start
        boolean paused = true;

        final int BOUNCEBACK = 50;

        // A Canvas and a Paint object
        Canvas canvas;
        Paint paint;

        // This variable tracks the game frame rate
        long fps;

        // Declare variables for accelerometer
        float sensorX, sensorY, sensorZ;
        Sensor accelerometerSensor;
        SensorManager sm;
        final float FACTOR_FRICTION = 0.5f; // imaginary friction on the screen
        final float GRAVITY = 9.8f; // acceleration of gravity
        float mxCenter;

        // This is used to help calculate the fps
        private long timeThisFrame;
        private long lastUpdate;

        // Storage stuff
        FileIO storage;
        List<Score> scoreList;
        // When we initialize (call new()) on gameView
        // This special constructor method runs
        public BreakoutView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            // How kind.
            super(context);

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            // Initialize and register accelerometer sensor.
            sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            if(sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
                accelerometerSensor = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
                sm.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
            sensorX = sensorY = 0f;

            // Get a Display object to access screen details
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            // Load the resolution into a Point object
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;
            Log.i("screen", "w:" + screenWidth + " h:" + screenHeight);

            // Initialize storage
            storage = new FileIO();
            storage.initIO();

            // Load the sounds
            // This SoundPool is deprecated but don't worry
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

            try {
                // Create objects of the 2 required classes
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;
                // Load our fx in memory ready for use
                descriptor = assetManager.openFd("soundfiles/beep1.ogg");
                beep1ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("soundfiles/beep2.ogg");
                beep2ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("soundfiles/beep3.ogg");
                beep3ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("soundfiles/loseLife.ogg");
                loseLifeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("soundfiles/explode.ogg");
                explodeID = soundPool.load(descriptor, 0);

            } catch(IOException e) {
                // Print an error message to the console
                Log.e("error", "failed to load sound files");
            }
        }

        /*
        * Author : Vel
        * Date : 24/Nov/2015
        * Purpose : Creates the paddle, ball and the bricks.
        */
        public void initGameLayer() {

            //Create a paddle
            paddle = new Paddle(fragmentWidth, fragmentHeight);

            // Create a ball
            ball = new Ball(fragmentWidth, fragmentHeight);

            //start the game
            createBricksAndRestart();
        }

        /*
        * Author : Vel
        * Date : 24/Nov/2015
        * Purpose : Put the Bricks and Ball to restart
        */
        public void createBricksAndRestart(){
            // Put the ball back to the start
            ball.reset();
            paddle.reset();

            int brickWidth = fragmentWidth / 8;
            int brickHeight = fragmentHeight / 10;
            // Build a wall of bricks
            numBricks = 0;

            int colorArray[] = { Color.WHITE, Color.BLUE, Color.GREEN, Color.RED, Color.BLACK };
            Random rand = new Random();

            brickHeight = fragmentWidth / 12;
            int noColumns;
            for(int row = 0; row < 3; row ++ ) {
                switch(row) {
                    case 0:
                        brickWidth = fragmentWidth / 9;
                        break;
                    case 1:
                        brickWidth = fragmentWidth / 8;
                        break;
                    case 2:
                        brickWidth = fragmentWidth / 7;
                        break;
                }
                noColumns = Math.round(fragmentWidth / brickWidth);
                int prevRandNumber = -1, currentRandomNumber;
                for(int column = 0; column < noColumns; column ++ ) {
                    // Make sure not to color the adjacent bricks with the same color
                    currentRandomNumber = rand.nextInt(5);
                    while (prevRandNumber == currentRandomNumber) {
                        currentRandomNumber = rand.nextInt(5);
                    }
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight, colorArray[currentRandomNumber]);
                    numBricks++;
                    prevRandNumber = currentRandomNumber;
                }
            }

            // Reset scores and lives
            score = 0;
            lives = 1;
            time = 0;

            // Re-read the scores from the file as they could have changed.
            scoreList = storage.readFile();
        }

        /*
        * Author : Vel
        * Date : 24/Nov/2015
        * Purpose : Runs the actual game thread
        */
        @Override
        public void run() {
            while (playing) {

                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();

                // Update the frame
                // Update the frame
                if(!paused) {
                    update();
                }

                // Draw the frame
                draw();

                // Calculate the fps this frame
                // We can then use the result to
                // time animations and more.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }
            }

        }

        /*
        * Author : Aishwarya
        * Date : 24/Nov/2015
        * Purpose : Everything that needs to be updated goes in here,Movement, collision detection etc.
        */
        public void update() {
            // Move the paddle if required
            paddle.update(fps);

            //Move the ball
            ball.update(fps);

            // Respond to the accelerometer if sensor has some o/p
            if(sensorX != 0f) {
                //Log.i("sensorX", "x: " + sensorX);
                // Only respond if the ball is moving towards the bricks
                //if(ball.getYVelocity() < 0) {
                    ball.setXVelocity((float) ball.getXVelocity() + sensorX);
                    ball.updateRect(sensorX);
                    // Let's reset the x velocity if it reaches too low.
                    if(Math.abs(ball.getXVelocity()) < 200)
                        ball.setXVelocity((float) 200 + (2 * currentProgress));
                //}
            }

            // Check for ball colliding with a brick
            boolean brickBroken =false;
            for(int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()) {
                    if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                        bricks[i].updateNumberOfHits();

                        // Make the brick invisible if the max hits have been reached.
                        if(bricks[i].hasReachedMaxHits()) {
                            bricks[i].setInvisible();
                            score += 10;

                            // Update the score text in the control area
                            mListener.respond("updatescore", String.valueOf(score));
                            brickBroken = true;
                        }

                        // Reverse yVelocity so that the ball bounces back.
                        //ball.reverseYVelocity();
                        ball.setRandomYVelocity();
                        ball.clearObstacleY(bricks[i].getRect().bottom + BOUNCEBACK);

                        //ball.setRandomAngle();
                        soundPool.play(explodeID, 1, 1, 0, 0, 1);
                    }
                }
                if(brickBroken)
                    break;
            }

            // Check for ball colliding with paddle
            if(RectF.intersects(paddle.getRect(), ball.getRect())) {
                ball.setRandomVelocity();
                //ball.reverseYVelocity();
                ball.setRandomYVelocity();
                ball.clearObstacleY(paddle.getRect().top - BOUNCEBACK);
                Log.i("ball", "paddle collision bottom:" + ball.getRect().bottom + " right:" + ball.getRect().right);
                soundPool.play(beep1ID, 1, 1, 0, 0, 1);
            }

            // Bounce the ball back when it hits the bottom of screen
            // And deduct a life
            if(ball.getRect().bottom > fragmentHeight) {
                ball.reverseYVelocity();
                //ball.setAngle(90);
                Log.i("ball", "bottom collision bottom:" + ball.getRect().bottom + " right:" + ball.getRect().right);
                ball.clearObstacleY(fragmentHeight - BOUNCEBACK);

                // Lose a life
                lives--;
                soundPool.play(loseLifeID, 1, 1, 0, 0, 1);

                if(lives == 0) {
                    paused = true;
                    mListener.respond("stopTimer", "");
                    Log.i("hiscore", "lives eq 0");
                    checkHighScore();
                    createBricksAndRestart();
                }
            }

            // Bounce the ball back when it hits the top of screen
            if(ball.getRect().top < 0) {
                ball.reverseYVelocity();
                //ball.setAngle(270);
                ball.clearObstacleY(BOUNCEBACK);
                soundPool.play(beep2ID, 1, 1, 0, 0, 1);
            }

            // If the ball hits left wall bounce
            if(ball.getRect().left < 0) {
                ball.reverseXVelocity();
                //ball.setAngle(0);
                ball.clearObstacleX(BOUNCEBACK);
                //soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            // If the ball hits right wall bounce
            if(ball.getRect().right > fragmentWidth - 10) {
                ball.reverseXVelocity();
                //ball.setAngle(180);
                ball.clearObstacleX(fragmentWidth - BOUNCEBACK);
                //soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            // Pause if cleared screen
            if(score == numBricks * 10) {
                paused = true;
                mListener.respond("stopTimer", "");
                Log.i("hiscore", "cleared all bricks(update)");
                checkHighScore();
                createBricksAndRestart();
            }
        }
        /*
        * Author : Aishwarya
        * Date : 24/Nov/2015
        * Purpose : draws the newly updated scene for each iteration of the game thread.
        */
        public void draw() {
            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();
                if(canvas == null)
                    return;

                // Draw the background color
                canvas.drawColor(Color.argb(255, 26, 128, 182));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));

                // Draw the paddle
                canvas.drawRect(paddle.getRect(), paint);

                // Draw the ball
                paint.setColor(Color.WHITE);
                canvas.drawOval(ball.getRect(), paint);

                //canvas.drawCircle(ballRect.centerX(), ballRect.centerY(), ball.getRadius(), paint);
                // Draw the bricks
                for(int i = 0; i < numBricks; i++) {
                    if(bricks[i].getVisibility()) {
                        paint.setColor(bricks[i].getBrickColor());
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                // Draw the HUD
                // Choose the brush color for drawing
                paint.setColor(Color.argb(255,  255, 255, 255));
                // Draw the score
                paint.setTextSize(40);

                // Has the player cleared the screen?
                if (score == numBricks * 10) {
                    paint.setTextSize(60);
                    canvas.drawText("YOU HAVE WON!", 10, fragmentHeight / 2, paint);
                    //checkHighScore();
                }

                // Has the player lost?
                else if(lives == 0) {
                    paint.setTextSize(60);
                    canvas.drawText("YOU HAVE LOST!", 10, fragmentHeight / 2, paint);
                    //checkHighScore();
                }
                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
                //this.invalidate();
            }
        }

        /*
        * Author : Vel
        * Date : 24/Nov/2015
        * Purpose :// Checks if the current score is among the top 10// score and initiates save if so.
        */
        private void checkHighScore() {
            // If score is zero, return.
            if(score == 0)
                return;
            int i;
            for(i = 0; i < scoreList.size(); i++) {
                Score p = scoreList.get(i);
                // If score is greater than the current score
                // or if score is equal and time is less, we should
                // initiate save.
                if (
                    p.getScore() < score
                    || (p.getScore() == score && p.getTime() < time)
                ) {
                    promptForUserName(i);
                    return;
                }
            }
            if(i > scoreList.size())
                i = scoreList.size();
            // If we still have the top 10 list to be filled out, ask for username.
            if(i < GetUserName.MAX_HIGH_SCORERS) {
                promptForUserName(i);
            }
        }

        /*
        * Author : Aishwarya
        * Date : 24/Nov/2015
        * Purpose : Starts the GetUserName activity to get the user name and store it.
        */
        private void promptForUserName(int position) {
            Intent userIntent = new Intent(getActivity(), GetUserName.class);

            Bundle info = new Bundle();
            info.putString("insertAtPosition", String.valueOf(position));
            info.putString("score", String.valueOf(score));
            info.putString("time", String.valueOf(time));
            Log.i("timer", "Passing time via intent. val:" + time);
            userIntent.putExtras(info);

            startActivity(userIntent);
        }

        /*
        * Author : Vel
        * Date : 24/Nov/2015
        * Purpose : If game is paused/stopped, shutdown our thread and unregister the accelerometer sensor.
        */
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "suspending thread error" +  e.getMessage());
            }
            sm.unregisterListener(this);
            mListener.respond("stopTimer", "");
        }

        public void updateBallSpeed(int progress) {
            ball.setXVelocity((float) 200 + (2 * progress) );
            ball.setYVelocity((float) -400 - (2 * progress) );
        }

        /*
        * Author : Aishwarya
        * Date : 24/Nov/2015
        * Purpose : If game is resumed, restart the thread and register the accelerometer sensor.
        * And if the game isn't in paused status, restart the timer.
        */
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();

            sm.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            if(!paused) {
                Log.i("timer", "resuming timer");
                mListener.respond("startTimer", "");
            }
        }

        /*
        * Author : Aishwarya
        * Date : 24/Nov/2015
        * Purpose : Starts the game(only if it's in paused state).
        */
        public void startGame() {
            if(paused && !GetUserName.RUNNING) {
                paused = false;
                // Initialize game score to be '0' for a new game.
                Log.i("timer", "starting the timer from startGame method.");
                mListener.respond("updatescore", "0");
                mListener.respond("startTimer", "");
            }
        }

        /*
        * Author : Aishwarya
        * Date : 24/Nov/2015
        * Purpose : Updates the time variable.
        */
        public void updateTimer(int updatedTime) {
            time = updatedTime;
        }

        /*
        * Author : Aishwarya
        * Date : 24/Nov/2015
        * Purpose : The SurfaceView class implements onTouchListener So we can override this method and detect screen touches.
        */
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                // Player has touched the screen
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_DOWN:
                    // Start the game once an user action is detected in the game area
                    startGame();
                    //Log.i("paddle", "x:" + motionEvent.getX());
                    if(motionEvent.getX() > fragmentWidth /2) {
                        //Log.i("paddle", "\t taking right");
                        paddle.setMovementState(paddle.RIGHT);
                    }
                    else {
                        //Log.i("paddle", "\t taking left");
                        paddle.setMovementState(paddle.LEFT);
                    }
                    break;
                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:
                    paddle.setMovementState(paddle.STOPPED);
                    break;
            }
            return true;
        }

        /*
        * Author : Vel
        * Date : 24/Nov/2015
        * Purpose : Accelerometer Sensor Changed
        */
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && !paused) {
                // Let's get sensor values every 100ms so that the ball speed doesn't vary too much
                long actualTime = System.currentTimeMillis();
                if ((actualTime - lastUpdate) > 100) {
                    long diffTime = (actualTime - lastUpdate);
                    lastUpdate = actualTime;

                    float mX = event.values[0];
                    float mZ = event.values[2];

                    float dt = (System.nanoTime() - event.timestamp) / 1000000000.0f;
                    sensorX = Math.signum(mX) * Math.abs(mX) * (1 - FACTOR_FRICTION * Math.abs(mZ) / GRAVITY);
                    // Threshold for shake
                    if (sensorX < -.16 || sensorX < .16) {
                        //Log.i("sensor", "stopped");
                        sensorX = 0f;
                    } else {
                        sensorX = -sensorX * dt * dt;
                        if(sensorX > 0)
                            sensorX += 3;
                        else
                            sensorX -= 3;
                        Log.i("sensor", "mx: " + mX + " x: " + sensorX + " dt:" + dt);
                    }

                    //Toast.makeText(this.getContext(), "Right shake detected", Toast.LENGTH_SHORT).show();
                    /*
                    if(mX < 1 || mX < -1 || mX < -6 || mX > 6) {
                        sensorX = 0f;
                    }
                    else {
                        sensorX += Math.signum(mX) * .5f;
                    }
                    */
                    //Log.i("sensor", "x:" + sensorX + "y:" + sensorY);

                    this.invalidate();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

}
