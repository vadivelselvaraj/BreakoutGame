package com.example.karthi.breakoutgame;
/*
 * Author : Aishwarya & Vel
 * Date : 24/Nov/2015
 * Purpose : Creating the Brick
*/
import android.graphics.Color;
import android.graphics.RectF;
/*
 * Author - Aishu
 * Date : 24/Nov/2015
 * Purpose : Class Brick with all the required variables
 */
public class Brick {

    private RectF rect;

    private boolean isVisible;
    private int brickColor;
    private int numberOfHits;
    private int maxHits;
    /*
     * Author - Aishu
     * Date : 24/Nov/2015
     * Purpose : Parameterized constructor of Brick class.
     */
    public Brick(int row, int column, int width, int height, int brickColor){

        isVisible = true;
        this.brickColor = brickColor;
        numberOfHits = 0;
        switch (brickColor) {
            case Color.WHITE:
                maxHits = 1;
                break;
            case Color.BLUE:
                maxHits = 2;
                break;
            case Color.GREEN:
                maxHits = 3;
                break;
            case Color.RED:
                maxHits = 4;
                break;
            case Color.BLACK:
                maxHits = 5;
                break;
        }
        int padding = 1;

        rect = new RectF(column * width + padding,
                row * height + padding,
                column * width + width - padding,
                row * height + height - padding);
    }
    /*
     * Author - Vel
     * Date : 24/Nov/2015
     * Purpose : Check the maximum hits made by each brick.
     */
    public boolean hasReachedMaxHits() {
        return maxHits == numberOfHits;
    }
    /*
     * Author - Vel
     * Date : 24/Nov/2015
     * Purpose : Update the number of hits by brick.
     */
    public void updateNumberOfHits() {
        if(!hasReachedMaxHits()) {
            numberOfHits++;
        }
    }
    /*
     * Author - Aishu & Vel
     * Date : 24/Nov/2015
     * Purpose : Setters and Getters for Rect, Visibility and Invisibility of Brick, Brick Color.
     */
    public RectF getRect(){
        return this.rect;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }

    public int getBrickColor() {
        return this.brickColor;
    }
}
// This is the end of the Brick class