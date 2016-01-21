package com.example.karthi.breakoutgame;
/*
 * Author : Aishwarya & Vel
 * Date : 24/Nov/2015
 * Purpose : Creating the ControlsArea to add every control
*/

import android.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/*
 * Author - Vel
 * Date : 24/Nov/2015
 * Purpose : Class ControlArea with all the required variables
 */
public class ControlArea extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    TextView timer;
    TextView score;
    TextView userName;
    SeekBar ballSpeedController;
    Button start;
    Button viewHighScores;

    // Variables for timer
    private long startTime, finalTime, timeSwap, timeInMillies, secondsSoFar;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Handler myHandler;

    /*
    * Author - Aishu
    * Date : 24/Nov/2015
    * Purpose : parameterized Constructor of ControlArea
    */
    public static ControlArea newInstance(String param1, String param2) {
        ControlArea fragment = new ControlArea();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    /*
     * Author - Aishu
     * Date : 24/Nov/2015
     * Purpose : Deafult Constructor of ControlArea
     */
    public ControlArea() {
        // Required empty public constructor
    }
    /*
     * Author - Vel
     * Date : 24/Nov/2015
     * Purpose : To Create an activity
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
     * Author - Aishu
     * Date : 24/Nov/2015
     * Purpose : View creation for the activity
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control, container, false);
    }

    /*
     * Author - Vel
     * Date : 24/Nov/2015
     * Purpose : On Creating the activity
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity a = getActivity();

        timer = (TextView) a.findViewById(R.id.timer);
        score = (TextView) a.findViewById(R.id.scoreText);
        userName = (TextView) a.findViewById(R.id.displayUserName);
        ballSpeedController = (SeekBar) a.findViewById(R.id.ballSpeedController);
        start = (Button) a.findViewById(R.id.btnStart);;
        viewHighScores = (Button) a.findViewById(R.id.btnViewHighScore);

        // Initialize onclick listeners of start and viewHighScores to class object
        // since they'd be implemented in Click subroutine as part of the
        // View.OnClickListener interface implementation.
        start.setOnClickListener(this);
        viewHighScores.setOnClickListener(this);
        // Also initialize onSeekBarChangeListener for the ball speed controller
        ballSpeedController.setOnSeekBarChangeListener(this);

        // Initialize interface object
        mListener = (OnFragmentInteractionListener) a;
        myHandler = new Handler();
    }

    /*
     * Author - Vel
     * Date : 24/Nov/2015
     * Purpose : To Detach the Fragment Controls to the BreakoutGame
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*
     * Author - Aishu
     * Date : 24/Nov/2015
     * Purpose : OnClick UI event
     */
    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if(viewID == R.id.btnStart) {
            Log.i("btnclick", "start button was clicked.");
            mListener.respond("start", "");
        }
        else if(viewID == R.id.btnViewHighScore) {
            Log.i("btnclick", "HiScores button was clicked. Initiating stoptimer()");
            stopTimer();
            // Invoke the ListScores
            Intent scoresDisplayIntent = new Intent(getActivity(), ListScores.class);
            getActivity().startActivity(scoresDisplayIntent);
        }
    }

    /*
     * Author - Vel
     * Date : 29/Nov/2015
     * Purpose : Updates the score text view with the new one.
     */
    public void updateScore(final String updatedScore) {
        //Update score UI element by invoking the UI thread since
        // accessing inside the gameThread(the one in BreakoutView)
        // throws errors.
        new Thread(){
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(100);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                score.setText(updatedScore);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.e("updateScore", "thread exception: " + e.getMessage());
                }
            };
        }.start();
    }

    /*
     * Author - Vel
     * Date : 29/Nov/2015
     * Purpose : Updates the username text view with the new one.
     */
    public void updateUserName(final String user) {
        new Thread(){
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(100);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userName.setText(user);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.e("updateUserName", "thread exception: " + e.getMessage());
                }};
        }.start();
    }

    public void startTimer() {
        startTime = SystemClock.uptimeMillis();
        finalTime = timeInMillies = timeSwap = 0L;
        Log.i("timer", "starting the timer");
        myHandler.postDelayed(updateTimerMethod, 0);
    }

    public void stopTimer() {
        timeSwap += timeInMillies;
        Log.i("timer", "sending an update time of " + secondsSoFar);
        mListener.respond("updateTime", String.valueOf(secondsSoFar));
        myHandler.removeCallbacks(updateTimerMethod);
    }

    private Runnable updateTimerMethod = new Runnable() {
        public void run() {
            timeInMillies = SystemClock.uptimeMillis() - startTime;
            finalTime = timeSwap + timeInMillies;

            int seconds = (int) (finalTime / 1000);
            secondsSoFar = seconds;
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timer.setText("" + minutes + ":" + String.format("%02d", seconds));
            myHandler.postDelayed(this, 0);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mListener.respond("updateBallSpeed", String.valueOf(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
