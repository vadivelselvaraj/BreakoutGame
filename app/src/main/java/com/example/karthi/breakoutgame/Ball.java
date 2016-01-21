package com.example.karthi.breakoutgame;
/*
 * Author : Aishwarya & Vel
 * Date : 21/Nov/2015
 * Purpose : Creating the Ball
 */

import android.graphics.RectF;
import android.util.Log;

import java.util.Random;
    /*
     * Author - Aishwarya
     * Date : 21/Nov/2015
     * Purpose : Class Ball with all the required variables
     */
public class Ball {
    RectF rect;
    private double xVelocity, yVelocity;
    int screenWidth;
    int screenHeight;
    int speed;
    float radius;
    double angle;

    public Ball(int width, int height) {
        // Start the ball travelling straight up at 200 pixels per second
        xVelocity = 200;
        yVelocity = -400;

        speed = 200;
        //setAngle(45);

        screenHeight = height;
        screenWidth = width;

        // Ball should be 1/12th of the screen width i.e radius = 1/24
        radius = screenWidth / 24;

        rect = new RectF();
    }

    /*
     * Author - Vel
     * Date : 21/Nov/2015
     * Purpose : To set the angle of the ball
     */
    public void setAngle(float angle) {
        this.angle = angle;
        /*
        xVelocity = Math.cos(Math.toRadians(angle));
        yVelocity = Math.sin(Math.toRadians(angle));
        */
    }
    /*
     * Author - Vel
     * Date : 21/Nov/2015
     * Purpose : Returns a random cosine value of an angle between 173 to 187.
     * To mimic + or - 7 degrees between the vertical and the angle of incidence
     * and angle of reflection
     */
    public double getRandomAngle() {
        Random rand = new Random();

        // Pre-computed values for cos(183..187)
        double cosineAngles[] = { -0.992546151641322, -0.9945218953682733, -0.9961946980917455,
                -0.9975640502598242, -0.9986295347545738, -0.9993908270190958,
                -0.9998476951563913, -1.0, -0.9998476951563913, -0.9993908270190958,
                -0.9986295347545738, -0.9975640502598242, -0.9961946980917455,
                -0.9945218953682733, -0.9925461516413221 };

        return cosineAngles[ rand.nextInt(15) ];
    }

    /*
     * Author - Aishu
     * Date : 21/Nov/2015
     * Purpose : Setters and Getters for xVelocity,yVelocity,Rect and Radius
     */

    public double getXVelocity() {
        return xVelocity;
    }

    public double getYVelocity() {
        return yVelocity;
    }

    public void setXVelocity(float xVelocity) {
        this.xVelocity = xVelocity;
    }

    public void setYVelocity(float yVelocity) {
        this.yVelocity = yVelocity;
    }

    public RectF getRect(){
        return rect;
    }

    public float getRadius() {
        return radius;
    }

    /*
     * Author - Vel
     * Date : 21/Nov/2015
     * Purpose : Update the co-ordinates of ball based on the speed and fps
     */
    public void update(long fps) {
        //rect.left = (float) (rect.left + xVelocity * speed / fps);
        //rect.top = (float) (rect.top + yVelocity * speed / fps);
        rect.left = (float) (rect.left + (xVelocity / fps));
        rect.top = (float) (rect.top + (yVelocity / fps));
        //Log.i("ball", "update factor x:" + rect.left + " y:" + rect.top + " fps:" + fps);
        rect.right = rect.left + (2 * radius);
        rect.bottom = rect.top + (2 * radius);
    }

    /*
     * Author - Aishu
     * Date : 21/Nov/2015
     * Purpose : Update the co-ordinates of the rectangle that holds the circle.
     */
    public void updateRect(float sensorX) {
        rect.left = rect.left + sensorX;
        rect.right = rect.left + (2 * radius);
    }

    /*
     * Author - Vel
     * Date : 21/Nov/2015
     * Purpose : Setters for Reverse X & Y velocities..
     */
    public void reverseYVelocity(){
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity(){
        xVelocity = -xVelocity;
    }

    public void setRandomXVelocity() {
        xVelocity = getRandomAngle() * xVelocity;
    }

    public void setRandomYVelocity() {
        yVelocity = getRandomAngle() * yVelocity;
    }

    /*
     * Author - Vel
     * Date : 21/Nov/2015
     * Purpose : Set Random X Velocity.
     */
    public void setRandomVelocity() {
        Random generator = new Random();
        int result = generator.nextInt(2);

        if(result == 0) {
            Log.i("ball", "reversing X velocity");
            reverseXVelocity();
        }
    }

    /*
     * Author - Aishu
     * Date : 21/Nov/2015
     * Purpose : Clear the obstacles of X and Y Co-ordinates.
     */
    public void clearObstacleY(float y) {
        //Log.i("ball", "obstacleY b4 left: " + rect.left + " top: " + rect.top + " right: " + rect.right + " bottom: " + rect.bottom + " centerx:" + rect.centerX() + " centery:" + rect.centerY());
        rect.bottom = y;
        rect.top = y - (2 * radius);
        //Log.i("ball", "obstacleY a4 left: " + rect.left + " top: " + rect.top + " right: " + rect.right + " bottom: " + rect.bottom + " centerx:" + rect.centerX() + " centery:" + rect.centerY());
    }

    public void clearObstacleX(float x) {
        rect.left = x;
        rect.right = x + (2 * radius);
    }

    /*
     * Author - Aishu
     * Date : 21/Nov/2015
     * Purpose : Set the ball to the center of the screen and 20pixels from the bottom wall,so that it sits at the paddle.
     */
    public void reset() {
        // Set the ball to the center of the screen and 20pixels from the bottom wall
        // so that it sits at the paddle.
        rect.left = (screenWidth / 2) - radius;
        rect.top = (screenHeight - 20) - (2*radius);
        rect.right = (screenWidth / 2) + radius;
        rect.bottom = (screenHeight - 20); // 20 is the paddle height
    }
}
