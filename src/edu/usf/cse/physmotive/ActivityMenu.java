package edu.usf.cse.physmotive;

import android.app.Activity;
import android.content.Intent;
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

    private int userId, activityId;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // pulling in bundle information
        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);

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

        // TODO: Fix Pictures
        // TODO: Setup user stats
    }

    private void bundleUserInformation(Intent mIntent)
    {
        int unitValue = 0;
        int unitType = 0;

        Bundle b = new Bundle();
        b.putInt(USERID, userId);

        if (distanceOrTimeToggleButton.isChecked())
        {
            unitValue = Integer.valueOf(minuteEditText.getText().toString()) * 60;
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
        mIntent.putExtras(b);
    }

    private void invokeActiveActivity(View arg0, String type)
    {
        Intent myIntent = new Intent(arg0.getContext(), ActiveActivity.class);
        // TODO: Insert New activity with activity details from screen
        activityId = activityDBM.insert(userId);

        bundleUserInformation(myIntent);
        myIntent.getExtras().putString(STARTTYPE, type);
        startActivityForResult(myIntent, 0);
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
                Toast.makeText(ActivityMenu.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
        distanceOrTimeToggleButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                onButtonClickDistanceOrTimeToggleButton(v);
            }
        });
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
        Intent myIntent = new Intent(w.getContext(), ActiveActivity.class);
        bundleUserInformation(myIntent);
        invokeActiveActivity(w, STARTTYPE_MAN);
    }

    private void onButtonClickAutomaticStart(View w)
    {
        Intent myIntent = new Intent(w.getContext(), ActiveActivity.class);
        bundleUserInformation(myIntent);
        invokeActiveActivity(w, STARTTYPE_AUTO);
    }
}
