package com.example.karthi.breakoutgame;
/*
 * Author : Aishwarya & Vel
 * Date : 24/Nov/2015
 * Purpose : Creating the GameArea of the Breakout Game
*/
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ScoreAdapter extends ArrayAdapter<Score>{
    int layoutResourceId;
    Context context;
    List<Score> scoresList = null;
    Typeface fontHeading;
    Typeface fontText;

    // Constructor
    // Context - we can pass the reference of the activity in which we will use our class
    // resource id - of the layout file we want to use for displaying each ListView item. eg: listview_item_row.xml
    // Score[] - array of Score class objects that will be used by the Adapter to display data.
    /*
     * Author : Vadivel
     * Date : 27/Nov/2015
     */
    public ScoreAdapter(Context context, int layoutResourceId, List<Score> list, Typeface fontHeading, Typeface fontText) {
        super(context, layoutResourceId, list);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.scoresList = list;
        this.fontHeading = fontHeading;
        this.fontText = fontText;
    }

    /*
     * Author : Vadivel
     * Date : 02/Nov/2015
     * Method will be called for every item in the ListView to create views with their properties set as we want.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ScoreHolder holder = null;

        if(row == null) {
            // Uses the Android built in Layout Inflater to inflate (parse) the xml layout file.
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ScoreHolder();
            holder.rank = (TextView) row.findViewById(R.id.txtRank);
            holder.userName = (TextView) row.findViewById(R.id.txtUserName);
            holder.score = (TextView) row.findViewById(R.id.txtScore);
            holder.time = (TextView) row.findViewById(R.id.txtTime);

            holder.userName.setId(position);
            holder.userName.setTypeface(fontText);
            holder.score.setTypeface(fontText);

            row.setTag(holder);
        }
        else {
            holder = (ScoreHolder) row.getTag();
        }

        holder.userName.setText(scoresList.get(position).getUserName() );
        holder.rank.setText( String.valueOf(scoresList.get(position).getRank()) );
        holder.score.setText( String.valueOf(scoresList.get(position).getScore()));
        holder.time.setText( String.valueOf(scoresList.get(position).getTime()));

        return row;
    }

    /*
     * Author : Vadivel
     * Date : 22/Nov/2015
     */
    static class ScoreHolder
    {
        TextView rank;
        TextView userName;
        TextView score;
        TextView time;
    }
}
