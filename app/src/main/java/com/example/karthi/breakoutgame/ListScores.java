package com.example.karthi.breakoutgame;
/*
 * Author : Aishwarya & Vel
 * Date : 24/Nov/2015
 * Purpose : To List the Top 10 High Scores
*/

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * Author : Aishwarya & Vel
 * Date : 24/Nov/2015
 * Purpose : Class to List the Top 10 High Scores
*/
public class ListScores extends Activity {
    TextView userNameHeading;
    TextView scoreHeading;
    ListView highScoresList;
    FileIO storage;
    ScoreAdapter adapter;

    /*
	* Author : Aishwarya
	* Date : 24/Nov/2015
	* Purpose : Create an activity
	*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_scores);

        userNameHeading = (TextView)findViewById(R.id.userName);
        scoreHeading = (TextView)findViewById(R.id.score);
        highScoresList = (ListView) findViewById(R.id.highScoreListView);

        // Set font for the headings
        Typeface t = Typeface.createFromAsset(getAssets(),"fonts/Segoe-UI-Symbol.ttf");
        userNameHeading.setTypeface(t);
        scoreHeading.setTypeface(t);

        // Initialize file IO
        storage = new FileIO();
        storage.initIO();
        makeList();
    }
    /*
    * Author : Vel
    * Date : 24/Nov/2015
    * Purpose : List of the Top 10 High Scores
    */
    private void makeList() {
        //For setting the font
        Typeface fontHeading = Typeface.createFromAsset(getAssets(), "fonts/Segoe-UI-Symbol.ttf");
        Typeface fontBody = Typeface.createFromAsset(getAssets(), "fonts/Segoe-Regular.ttf");

        // read list form the disk
        final List<Score> scoreList = storage.readFile();

        // Call the Person Adaptor, Person Adaptor is our Customized ArrayAdaptor!
        adapter = new ScoreAdapter(this, R.layout.listview_item_row, scoreList, fontHeading, fontBody);
        highScoresList.setAdapter(adapter);

        // Sort the adapter by score and then by time.
        adapter.sort(new Comparator<Score>() {
            @Override
            public int compare(Score lhs, Score rhs) {
                int lhsScore = lhs.getScore();
                int rhsScore = rhs.getScore();

                // If scores aren't equal, check for their respective scores.
                if (lhsScore != rhsScore) {
                    if (lhsScore > rhsScore)
                        return -1;
                    else if (lhsScore < rhsScore)
                        return 1;
                } else {
                    // If scores are equal, check for their time.
                    // Least time wins.
                    if (lhs.getTime() < rhs.getTime())
                        return -1;
                    else if (lhs.getTime() > rhs.getTime())
                        return 1;
                }

                return 0;
            }
        });
    }

}
