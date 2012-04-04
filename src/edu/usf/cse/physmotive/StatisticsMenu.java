package edu.usf.cse.physmotive;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;
import edu.usf.cse.physmotive.db.ActivityDBM;
import edu.usf.cse.physmotive.db.UserDBM;
import edu.usf.cse.physmotive.logic.Statistics;
import edu.usf.cse.physmotive.ui.ImageAdapter;

public class StatisticsMenu extends Activity
{
    public static final String USERID = "userId";

    protected Gallery gallery;
    protected DatePicker filterDatePicker;
    protected TextView NumberOfRacesTextView;
    protected TextView averageTimeTextView;
    protected TextView averageDistanceTextView;
    protected TextView totalTimeTextView;
    protected TextView totalDistanceTextView;
    protected TextView bmiTextView;
    protected Statistics statsLocationAll;
    protected Statistics statsUser;
    protected Statistics statsActivity;

    protected ActivityDBM dbaManager;
    protected UserDBM dbuManager;

    private int userId;
    private int activityId;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic_menu);

        // Pulling in bundle information
        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);
        activityId = 0;

        // Setting up gallery information.
        gallery = (Gallery) findViewById(R.id.activityGallery);
        gallery.setAdapter(new ImageAdapter(this));

        // Setting up TextViews
        NumberOfRacesTextView = (TextView) findViewById(R.id.NumberOfRacesTextView);
        averageTimeTextView = (TextView) findViewById(R.id.averageTimeTextView);
        averageDistanceTextView = (TextView) findViewById(R.id.averageDistanceTextView);
        totalTimeTextView = (TextView) findViewById(R.id.totalTimeTextView);
        totalDistanceTextView = (TextView) findViewById(R.id.totalDistanceTextView);
        bmiTextView = (TextView) findViewById(R.id.BMITextView);

        // Setting up Radio Buttons
        filterDatePicker = (DatePicker) findViewById(R.id.filterDatePicker);

        // Database Managers
        dbaManager = new ActivityDBM(this);
        dbuManager = new UserDBM(this);

        // Update the Stats for All
        updateStatistics();

        // Setup On Click Listeners
        setOnClickListeners();

        // TODO: get data
        // TODO: Graph??
        // TODO: create stats, live data
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateStatistics();
    }

    private void setOnClickListeners()
    {
        filterDatePicker.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                updateStatistics();
            }
        });
        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id)
            {
                activityId = position;
                updateStatistics();
            }
        });
    }

    private void updateStatistics()
    {
        dbaManager.open();

        Cursor cur1 = dbaManager.getStatisticsList(userId, activityId, filterDatePicker.getDayOfMonth(),
                filterDatePicker.getMonth() + 1, filterDatePicker.getYear());
        startManagingCursor(cur1);
        statsActivity = new Statistics(cur1);

        dbaManager.close();

        Toast.makeText(this, Integer.toString(cur1.getCount()), Toast.LENGTH_SHORT).show();

        NumberOfRacesTextView.setText("Total Activities Completed: " + statsActivity.getTotalNumberActivities());
        averageTimeTextView.setText("Average Time: " + statsActivity.getAverageTime());
        averageDistanceTextView.setText("Average Distance: " + statsActivity.getAverageDistance());
        totalTimeTextView.setText("Total Time: " + statsActivity.getActivityTotalTime());
        totalDistanceTextView.setText("Total Distance: " + statsActivity.getActivityTotalDistance());

        dbuManager.open();
        Cursor cur2 = dbuManager.getList(userId);
        startManagingCursor(cur2);
        statsUser = new Statistics(cur2);
        dbuManager.close();
        bmiTextView.setText("Body Mass Index: " + statsUser.getBMI());
    }

}
