package edu.usf.cse.physmotive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityView extends Activity
{
    static final String USERID = "userId";
    static final String ACTIVITYID = "activityId";
    
    protected Button statisticsButton;
    protected Button diaryButton;

    private int userId, activityId;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        // Connect interface elements to properties
        diaryButton = (Button) findViewById(R.id.toDiaryButton);
        statisticsButton = (Button) findViewById(R.id.toStatisticsButton);

        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);
        activityId = b.getInt(ACTIVITYID);
        
        setOnClickListeners();
    }

    private void setOnClickListeners()
    {
        statisticsButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                onButtonClickStatistics(v);
            }
        });
        diaryButton.setOnClickListener(new OnClickListener() {
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

    private void bundleUserInformation(Intent mIntent){
    	Bundle b = new Bundle();
        b.putInt(USERID, userId);
        b.putInt(ACTIVITYID ,activityId);
        mIntent.putExtras(b);
    }
}
