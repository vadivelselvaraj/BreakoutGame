package com.example.karthi.breakoutgame;
/*
* Author : Aishwarya & Vel
* Date : 24/Nov/2015
* Purpose : Creating the BreakoutGame
*/

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/*
 * Author - Vel
 * Date : 24/Nov/2015
 * Purpose : Activity BreakoutGame holding a view of controls and GameArea in separate fragments.
 */
public class BreakoutGame extends Activity implements OnFragmentInteractionListener {
     /*
      * Author - Aishu
      * Date : 24/Nov/2015
      * Purpose : Creates control and gameArea fragments by inflating the XML.
      */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakout_game);
    }
    /*
      * Author - Aishu
      * Date : 24/Nov/2015
      * Purpose : To resume an activity
      */
    @Override
    protected void onResume() {
        super.onResume();
    }
    /*
      * Author - Vel
      * Date : 24/Nov/2015
      * Purpose : To Pause an activity.
      */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /*
      * Author - Vel
      * Date : 24/Nov/2015
      * Purpose : For Fragment interaction in the layout.
      */
    @Override
    public void respond(String action, String data) {
        FragmentManager manager = getFragmentManager();
        GameArea gameArea = (GameArea) manager.findFragmentById(R.id.fragmentGameArea);
        ControlArea controlArea = (ControlArea) manager.findFragmentById(R.id.fragmentControlArea);

        if(action.equalsIgnoreCase("start")) {
            gameArea.startPlay();
        }
        else if(action.equalsIgnoreCase("updatescore")) {
            controlArea.updateScore(data);
        }
        else if(action.equalsIgnoreCase("starttimer")) {
            Log.i("interface", "calling start timer from respond method");
            controlArea.startTimer();
        }
        else if(action.equalsIgnoreCase("stoptimer")) {
            controlArea.stopTimer();
        }
        else if(action.equalsIgnoreCase("updatetime")) {
            gameArea.updateTime(Integer.parseInt(data));
        }
        else if(action.equalsIgnoreCase("updateballspeed")) {
            gameArea.updateBallSpeed(Integer.parseInt(data));
        }
    }
}
// This is the end of the BreakoutGame class