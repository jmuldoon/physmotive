package edu.usf.cse.physmotive;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import edu.usf.cse.physmotive.db.ActivityDBM;

public class ActivityView extends Activity
{
    static final String USERID = "userId";
    static final String ACTIVITYID = "activityId";
    static final String ID = "_id";
    static final String EDATE = "entryDate";

    protected Button statistics_btn;
    protected Button diary_btn;
    protected TextView raceId_tv;
    protected TextView raceDate_tv;
    protected TextView raceTotPace_tv;
    protected TextView raceTotTime_tv;
    protected TextView raceTotDist_tv;
    // Future implementation
    // protected TextView raceTarPace_tv;
    // protected TextView raceTarTime_tv;
    // protected TextView raceTarDist_tv;

    private int userId, activityId;
    private Cursor activityInfo;
    private ActivityDBM activityDBM;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        retrieveBundleInfo();

        // Connect interface elements to properties
        diary_btn = (Button) findViewById(R.id.toDiaryButton);
        statistics_btn = (Button) findViewById(R.id.toStatisticsButton);
        raceId_tv = (TextView) findViewById(R.id.raceIdTextView);
        raceDate_tv = (TextView) findViewById(R.id.raceDateTextView);
        raceTotPace_tv = (TextView) findViewById(R.id.paceTextView);
        raceTotTime_tv = (TextView) findViewById(R.id.timeTextView);
        raceTotDist_tv = (TextView) findViewById(R.id.distanceTextView);

        activityDBM = new ActivityDBM(this);

        setOnClickListeners();

        // TODO: Make Jimmy do stats
        // TODO: Make sure buttons work properly
        // TODO: Make Map Work, add geo from DB
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setupTextViews();
    }

    private void setupTextViews()
    {
        // Variables we will need at some point
        // float time, dist, pace;

        activityDBM.open();
        activityInfo = activityDBM.get(activityId);
        activityDBM.close();

        raceId_tv.setText("Race Id #" + activityInfo.getString(activityInfo.getColumnIndex(ID)));
        raceDate_tv.setText(activityInfo.getString(activityInfo.getColumnIndex(EDATE)));

        raceTotTime_tv.setText("Total Time: ##:##:##");
        raceTotDist_tv.setText("Total Distance: ##.#");
        raceTotPace_tv.setText("Speed: ##.#");
        // TODO: Figure out stats and insert into fields
    }

    private void bundleUserInformation(Intent mIntent)
    {
        Bundle b = new Bundle();
        b.putInt(USERID, userId);
        b.putInt(ACTIVITYID, activityId);
        mIntent.putExtras(b);
    }

    private void retrieveBundleInfo()
    {
        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);
        activityId = b.getInt(ACTIVITYID);
    }

    private void setOnClickListeners()
    {
        statistics_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                onButtonClickStatistics(v);
            }
        });
        diary_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                onButtonClickDiary(v);
            }
        });
    }

    private void onButtonClickStatistics(View w)
    {
        Intent myIntent = new Intent(w.getContext(), StatisticsMenu.class);
        bundleUserInformation(myIntent);
        startActivity(myIntent);
    }

    private void onButtonClickDiary(View w)
    {
        Intent myIntent = new Intent(w.getContext(), DiaryView.class);
        bundleUserInformation(myIntent);
        startActivity(myIntent);
    }
}
