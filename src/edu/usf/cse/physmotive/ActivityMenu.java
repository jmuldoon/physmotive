package edu.usf.cse.physmotive;

import java.util.Arrays;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.usf.cse.physmotive.db.ActivityDBM;
import edu.usf.cse.physmotive.logic.ChartData;
import edu.usf.cse.physmotive.ui.ImageAdapter;

public class ActivityMenu extends Activity
{
    // CONST string variables
    public static final String USERID = "userId";
    public static final String UNITVALUE = "unitValue";
    public static final String UNITTYPE = "unitType";
    public static final String STARTTYPE = "startType";
    public static final String STARTTYPE_MAN = "manual";
    public static final String STARTTYPE_AUTO = "automatic";
    public static final String ACTIVITYID = "activityId";

    protected Button manualButton;
    protected Button automaticButton;
    protected Gallery gallery;
    protected EditText minuteEditText;
    protected EditText secondEditText;
    protected ToggleButton distanceOrTimeToggleButton;

    private ActivityDBM activityDBM;

    private int userId, activityId, activitySelection = 0;
    private String startType;
    
    private XYPlot mySimpleXYPlot;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // pulling in bundle information
        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);

        activityDBM = new ActivityDBM(this);

        // Connect interface elements to properties
        manualButton = (Button) findViewById(R.id.manualButton);
        automaticButton = (Button) findViewById(R.id.automaticButton);
        minuteEditText = (EditText) findViewById(R.id.minuteEditText);
        secondEditText = (EditText) findViewById(R.id.secondEditText);
        distanceOrTimeToggleButton = (ToggleButton) findViewById(R.id.distanceOrTimeToggleButton);

        // Setting up gallery information.
        gallery = (Gallery) findViewById(R.id.activityGallery);
        gallery.setAdapter(new ImageAdapter(this));

        setOnClickListeners();
        
        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        
        mySimpleXYPlot.setDomainLabel("Time");
        mySimpleXYPlot.setRangeLabel("Distance");
 
        // Reduce the number of range labels
        mySimpleXYPlot.setTicksPerRangeLabel(3);

        // By default, AndroidPlot displays developer guides to aid in laying
        // out your plot.
        // To get rid of them call disableAllMarkup():
        mySimpleXYPlot.disableAllMarkup();
        
        //do some db stuff
        activityDBM.open();
        Cursor cursor = activityDBM.getRaceStats(userId);
        activityDBM.close();
        
        ChartData chartData = new ChartData(cursor, 1, 2);
        Number x[] = chartData.getX();
        Number y[] = chartData.getY();
        
        XYSeries series1 = new SimpleXYSeries(
              Arrays.asList(x),          // SimpleXYSeries takes a List so turn our array into a List
              Arrays.asList(y), // Y_VALS_ONLY means use the element index as the x value
              "Distance vs. Time");                             // Set the display title of the series
        
     // Create a formatter to use for drawing a series using LineAndPointRenderer:
      LineAndPointFormatter series1Format = new LineAndPointFormatter(
              Color.rgb(0, 200, 0),                   // line color
              Color.rgb(0, 100, 0),                   // point color
              null);              // fill color (optional)
      
      mySimpleXYPlot.addSeries(series1, series1Format);

        // TODO: Fix Pictures
    }

    private void bundleUserInformation(Intent mIntent)
    {
        int unitValue = 0;
        int unitType = 0;

        Bundle b = new Bundle();
        b.putInt(USERID, userId);

        if (distanceOrTimeToggleButton.isChecked())
        {
            unitValue = Integer.valueOf(minuteEditText.getText().toString());
            unitType = 1;
        } else
        {
            unitValue = Integer.valueOf(minuteEditText.getText().toString()) * 60;
            unitValue += Integer.valueOf(secondEditText.getText().toString());
            unitType = 0;
        }

        b.putInt(UNITTYPE, unitType);
        b.putInt(UNITVALUE, unitValue);
        b.putInt(ACTIVITYID, activityId);
        b.putString(STARTTYPE, startType);
        mIntent.putExtras(b);
    }

    private void invokeActiveActivity(View arg0)
    {
        Intent myIntent = new Intent(arg0.getContext(), ActiveActivity.class);
        // TODO: Insert New activity with activity details from screen
        activityDBM.open();
        activityId = activityDBM.insert(userId, activitySelection);
        activityDBM.close();

        bundleUserInformation(myIntent);
        startActivity(myIntent);
    }

    private void setOnClickListeners()
    {
        manualButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                onButtonClickManualStart(v);
            }
        });
        automaticButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                onButtonClickAutomaticStart(v);
            }
        });
        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id)
            {
                activitySelection = position;
                Toast.makeText(ActivityMenu.this, "Only running is available for Beta release.", Toast.LENGTH_LONG).show();
            }
        });
        distanceOrTimeToggleButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                onButtonClickDistanceOrTimeToggleButton(v);
            }
        });
    }

    private boolean validateData()
    {
        // Verifies the fields have information in them
        if (!distanceOrTimeToggleButton.isChecked())
        {
            if (minuteEditText.getText().toString().equals("") || minuteEditText.getText().toString().trim().equals("")
                    || secondEditText.getText().toString().equals("")
                    || secondEditText.getText().toString().trim().equals(""))
            {
                Toast.makeText(ActivityMenu.this, "Please enter minutes and seconds.", Toast.LENGTH_LONG).show();
                minuteEditText.setText("");
                secondEditText.setText("");
                return false;
            }
        } else
        {
            if (minuteEditText.getText().toString().equals("") || minuteEditText.getText().toString().trim().equals(""))
            {
                Toast.makeText(ActivityMenu.this, "Please enter a distance.", Toast.LENGTH_LONG).show();
                minuteEditText.setText("");
                return false;
            }
        }
        return true;
    }

    private void onButtonClickDistanceOrTimeToggleButton(View v)
    {
        if (distanceOrTimeToggleButton.isChecked())
        {
            secondEditText.setVisibility(View.GONE);
            minuteEditText.setHint("meters");
        } else
            secondEditText.setVisibility(View.VISIBLE);
    }

    private void onButtonClickManualStart(View w)
    {
        if (validateData())
        {
            Intent myIntent = new Intent(w.getContext(), ActiveActivity.class);
            bundleUserInformation(myIntent);
            startType = STARTTYPE_MAN;
            invokeActiveActivity(w);
        }
    }

    private void onButtonClickAutomaticStart(View w)
    {

        if (validateData())
        {
            Intent myIntent = new Intent(w.getContext(), ActiveActivity.class);
            bundleUserInformation(myIntent);
            startType = STARTTYPE_AUTO;
            invokeActiveActivity(w);
        }
    }
}
