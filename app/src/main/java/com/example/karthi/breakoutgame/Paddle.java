package com.example.karthi.breakoutgame;
/*
 * Author : Aishwarya & Vel
 * Date : 24/Nov/2015
 * Purpose : Creating the Paddle
*/
import android.graphics.RectF;
import android.util.Log;
/*
* Author : Aishwarya & Vel
* Date : 24/Nov/2015
* Purpose : Class to Create the Paddle
*/
public class Paddle {

    // RectF is an object that holds four coordinates - just what we need
    private RectF rect;

    // How long and high our paddle will be
    private float length;
    private float height;
    private int screenWidth;
    private int screenHeight;

    // X is the far left of the rectangle which forms our paddle
    private float x;

    // Y is the top coordinate
    private float y;

    // This will hold the pixels per second speedthat the paddle will move
    private float paddleSpeed;

    // Which ways can the paddle move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    // Is the paddle moving and in which direction
    private int paddleMoving = STOPPED;

    // This the the constructor method
    // When we create an object from this class we will pass
    // in the screen width and height
    public Paddle(int screenX, int screenY) {

        screenWidth = screenX;
        screenHeight = screenY;

        // length is 1/6th of the screen width
        length = screenWidth / 6;
        height = 20;

        // How fast is the paddle in pixels per second
        paddleSpeed = 600;

        reset();
    }
    /*
* Author : Aishwarya & Vel
* Date : 24/Nov/2015
* Purpose : Creating the Paddle
*/
    public float getHeight() {
        return height;
    }
    /*
	* Author : Aishwarya & Vel
	* Date : 24/Nov/2015
	* Purpose : Creating the Paddle
	*/
    // Restore the paddle to its original place i.e. centre of the screen
    public void reset() {
        x = (screenWidth / 2) - (length/2);
        y = screenHeight - height;

        rect = new RectF(x, y, x + length, y + height);
    }

    // This is a getter method to make the rectangle that
    // defines our paddle available in BreakoutView class
    public RectF getRect(){
        return rect;
    }

    // This method will be used to change/set if the paddle is going left, right or nowhere
    public void setMovementState(int state){
        paddleMoving = state;
    }

    /*
    * Author : Aishwarya & Vel
    * Date : 24/Nov/2015
    * Purpose : This update method will be called from update in BreakoutView.
    * It determines if the paddle needs to move and changes the coordinates// contained in rect if necessary
    */
    public void update(long fps) {
        //Log.i("paddle", "Movt: " + paddleMoving + " b4 x:" + rect.left + ", y:" + rect.right + " fps:" + fps);
        float deltaX = 0f;
        if(paddleMoving == LEFT) {
            deltaX = x - paddleSpeed / fps;
        }

        if(paddleMoving == RIGHT) {
            deltaX = x + paddleSpeed / fps;
        }

        //Log.i("paddle", "\tafter deltax:" + deltaX + ", deltay:" + (Math.round(deltaX) + length));
        // Don't let the paddle move beyond the side walls.
        if(deltaX < (screenWidth - length) && deltaX > 0) {
            x = Math.round(deltaX);
            rect.left = x;
            rect.right = x + length;
        }
    }
}
