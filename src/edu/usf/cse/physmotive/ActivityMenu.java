package edu.usf.cse.physmotive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityMenu extends Activity
{
    // CONST string variables
    static final String USERID = "userId";

    protected Button manualButton;
    protected Button automaticButton;
    // Called when the activity is first created.

    private int userId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_menu);

	// Connect interface elements to properties
	manualButton = (Button) findViewById(R.id.manualButton);
	automaticButton = (Button) findViewById(R.id.automaticButton);

	setOnClickListeners();
    }

    private void invokeActiveActivity(View arg0)
    {
	Intent myIntent = new Intent(arg0.getContext(), ActiveActivity.class);
	Bundle b = new Bundle();
	b.putInt(USERID, userId);
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
	invokeActiveActivity(w);
    }

    private void onButtonClickAutomaticStart(View w)
    {
	invokeActiveActivity(w);
    }
}
