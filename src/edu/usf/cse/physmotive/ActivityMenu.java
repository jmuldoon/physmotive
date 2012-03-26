package edu.usf.cse.physmotive;

import edu.usf.cse.physmotive.ui.ImageAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.Toast;

public class ActivityMenu extends Activity
{
    // CONST string variables
    public static final String USERID = "userId";
    public static final String STARTTYPE = "startType";
    public static final String STARTTYPE_MAN = "manual";
    public static final String STARTTYPE_AUTO = "automatic";

    protected Button manualButton;
    protected Button automaticButton;
    protected Gallery gallery;
    
    // Called when the activity is first created.
    private int userId;

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

        // Setting up gallery information.
        gallery = (Gallery) findViewById(R.id.activityGallery);
        gallery.setAdapter(new ImageAdapter(this));

        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Toast.makeText(ActivityMenu.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
        
        setOnClickListeners();
    }

    private void invokeActiveActivity(View arg0, String type)
    {
        Intent myIntent = new Intent(arg0.getContext(), ActiveActivity.class);
        Bundle b = new Bundle();
        b.putInt(USERID, userId);
        b.putString(STARTTYPE, type);
        myIntent.putExtras(b);
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
    }

    private void onButtonClickManualStart(View w)
    {
        invokeActiveActivity(w, STARTTYPE_MAN);
    }

    private void onButtonClickAutomaticStart(View w)
    {
        invokeActiveActivity(w, STARTTYPE_AUTO);
    }
}
