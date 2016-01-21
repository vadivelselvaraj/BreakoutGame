package com.example.karthi.breakoutgame;
/*
 * Author : Aishwarya & Vel
 * Date : 24/Nov/2015
 * Purpose : Creating the GetUserName to update the scores
*/

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
/*
 * Author : Aishwarya & Vel
 * Date : 24/Nov/2015
 * Purpose : Class to GetUserName to update the scores
*/

public class GetUserName extends Activity {
    public static final int MAX_HIGH_SCORERS = 10;
    private EditText username;
    private Button save;
    private FileIO storage;
    private int insertAtPosition;
    private int score;
    private int time;
    public static boolean RUNNING = false;

    /*
    * Author : Aishwarya
    * Date : 24/Nov/2015
    * Purpose : Creating the activity
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RUNNING = true;
        setContentView(R.layout.activity_get_user_name);

        Bundle bundle = getIntent().getExtras();
        insertAtPosition = Integer.parseInt(bundle.getString("insertAtPosition"));
        score = Integer.parseInt(bundle.getString("score"));
        time = Integer.parseInt(bundle.getString("time"));

        username = (EditText) findViewById(R.id.editUserName);
        save = (Button) findViewById(R.id.buttonSave);

        storage = new FileIO();
        storage.initIO();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserScore();
            }
        });
    }

    public GetUserName() {
        time = 0;
        score = 0;
    }

    /*
    * Author : Vel
    * Date : 24/Nov/2015
    * Purpose : Save User Score
    */
    private void saveUserScore() {
        String user = username.getText().toString().trim();
        // Skip save if the username text wasn't entered.
        if(user.length() == 0 ||  time == 0 || score == 0) {
            Log.i("scoresave", "Skipping save coz of null data in username:" + user + " time:" + time + " score: " + score);
            this.finish();
            return;
        }

        List<Score> oldScoreList = storage.readFile();
        List<Score> newScoreList = new ArrayList<>();
        int size = oldScoreList.size();
        Log.i("Score", "insertAtPosition: " + insertAtPosition + " oldscorelist size:" + size);
        if(insertAtPosition > size || insertAtPosition < 0 || insertAtPosition > MAX_HIGH_SCORERS) {
            Log.i("Score", "insertAtPosition is invalid" + insertAtPosition);
            return;
        }

        Score s = new Score(insertAtPosition + 1, user, score, time);
        int i = 0;
        // Insert items before the insertAtPosition into the new list
        while( (i < insertAtPosition) && (i < MAX_HIGH_SCORERS) ) {
            newScoreList.add(oldScoreList.get(i));
            i++;
        }
        // Add the new one
        newScoreList.add(s);
        // Add the rest after incrementing their rank by 1
        while( (i < size) && ( (i+1) < MAX_HIGH_SCORERS) ) {
            Score t = oldScoreList.get(i);
            t.setRank( t.getRank() + 1 );
            newScoreList.add(t);
            i++;
        }
        Log.i("score", "writing newscorelist size:" + newScoreList.size());

        // Store the modified list
        storage.writeList(newScoreList);

        // Return to the game screen
        this.finish();
        // Needed for activity to finish properly
        return;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RUNNING = false;
    }
}
