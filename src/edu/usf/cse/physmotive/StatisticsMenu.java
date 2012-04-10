package edu.usf.cse.physmotive;

import java.util.Arrays;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import edu.usf.cse.physmotive.db.ActivityDBM;
import edu.usf.cse.physmotive.db.UserDBM;
import edu.usf.cse.physmotive.logic.ChartData;
import edu.usf.cse.physmotive.logic.Statistics;
import edu.usf.cse.physmotive.ui.ImageAdapter;

public class StatisticsMenu extends Activity
{
	static final String TOTALTIME = "totalTime";
	
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
    private XYPlot mySimpleXYPlot;

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
        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

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

 
        // Create two arrays of y-values to plot:
        //Number[] series1Numbers = {};
 
        // Turn the above arrays into XYSeries:
//        XYSeries series1 = new SimpleXYSeries(
//                Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
//                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
//                "Distance");                             // Set the display title of the series
// 
        // Create a formatter to use for drawing a series using LineAndPointRenderer:
//        LineAndPointFormatter series1Format = new LineAndPointFormatter(
//                Color.rgb(0, 200, 0),                   // line color
//                Color.rgb(0, 100, 0),                   // point color
//                Color.rgb(150, 190, 150));              // fill color (optional)
 
        // Add series1 to the xyplot:
        //mySimpleXYPlot.addSeries(series1, series1Format);
        
        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
 
        mySimpleXYPlot.setDomainLabel("Time");
        mySimpleXYPlot.setRangeLabel("Distance");
 
        // Reduce the number of range labels
        mySimpleXYPlot.setTicksPerRangeLabel(1);
        mySimpleXYPlot.setTicksPerDomainLabel(1);
        mySimpleXYPlot.setDomainLeftMin(0);

        // By default, AndroidPlot displays developer guides to aid in laying
        // out your plot.
        // To get rid of them call disableAllMarkup():
        mySimpleXYPlot.disableAllMarkup();
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
                Toast.makeText(StatisticsMenu.this, "Only running is available for Beta release.", Toast.LENGTH_LONG).show();
                updateStatistics();
            }
        });
    }

    private void updateStatistics()
    {
        ChartData chartData;
        dbaManager.open();
        
        Cursor cur1 = dbaManager.getStatisticsList(userId, activityId, filterDatePicker.getDayOfMonth(),
                filterDatePicker.getMonth() + 1, filterDatePicker.getYear());
        startManagingCursor(cur1);
        statsActivity = new Statistics(cur1);

        dbaManager.close();
        

        chartData = new ChartData(cur1, 1, 2);
        Number x[] = chartData.getX();
        Number y[] = chartData.getY();
        
        XYSeries series1 = new SimpleXYSeries(
              Arrays.asList(x),          // SimpleXYSeries takes a List so turn our array into a List
              Arrays.asList(y),         // Y_VALS_ONLY means use the element index as the x value
              //SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED,
              "Distance");                             // Set the display title of the series
        
     // Create a formatter to use for drawing a series using LineAndPointRenderer:
      LineAndPointFormatter series1Format = new LineAndPointFormatter(
              Color.rgb(0, 200, 0),                   // line color
              Color.rgb(0, 100, 0),                   // point color
              //Color.rgb(150, 190, 150));              // fill color (optional)
              null);
      
      mySimpleXYPlot.addSeries(series1, series1Format);
        
        

        Toast.makeText(this, Integer.toString(cur1.getCount()), Toast.LENGTH_SHORT).show();

        NumberOfRacesTextView.setText("Total Activities Completed: " + statsActivity.getTotalNumberActivities());
        averageTimeTextView.setText("Average Time: " + Statistics.roundTwoDecimals(statsActivity.getAverageTime()) + " s");
        averageDistanceTextView.setText("Average Distance: " + Statistics.roundTwoDecimals(statsActivity.getAverageDistance()) + " m");
        totalTimeTextView.setText("Total Time: " + Statistics.roundTwoDecimals(statsActivity.getActivityTotalTime())+ " s");
        totalDistanceTextView.setText("Total Distance: " + Statistics.roundTwoDecimals(statsActivity.getActivityTotalDistance()) + " m");

        dbuManager.open();
        Cursor cur2 = dbuManager.getList(userId);
        startManagingCursor(cur2);
        statsUser = new Statistics(cur2);
        dbuManager.close();
        bmiTextView.setText("BMI: " + Statistics.roundTwoDecimals(statsUser.getBMI()));
    }

}
