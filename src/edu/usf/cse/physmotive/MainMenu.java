package edu.usf.cse.physmotive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import edu.usf.cse.physmotive.db.UserDBM;

public class MainMenu extends Activity
{
    protected Button newActivityButton;
    protected Button viewActivityButton;
    protected Button mapButton;
    protected Button diaryButton;
    protected Button statisticsButton;
    protected Button settingsButton;
    protected Button newUserButton;
    protected Spinner userSpinner;

    private long Usr;
    private UserDBM uDBM;
    private Cursor userCursor;
    private SimpleCursorAdapter adapter;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	uDBM = new UserDBM(this);

	// Connect interface elements to properties
	newActivityButton = (Button) findViewById(R.id.newActivityButton);
	viewActivityButton = (Button) findViewById(R.id.viewActivityButton);
	mapButton = (Button) findViewById(R.id.mapButton);
	diaryButton = (Button) findViewById(R.id.diaryButton);
	statisticsButton = (Button) findViewById(R.id.statisticsButton);
	settingsButton = (Button) findViewById(R.id.settingsButton);
	newUserButton = (Button) findViewById(R.id.newUserButton);
	userSpinner = (Spinner) findViewById(R.id.userSpinner);

	setOnClickListeners();
	updateSpinner();
    }

    @Override
    protected void onResume()
    {
	super.onResume();
	// Restore state here
	updateSpinner();
    }

    @Override
    protected void onPause()
    {
	super.onPause();
	userCursor.close();
	uDBM.close();
    }

    @Override
    protected Dialog onCreateDialog(int i, Bundle args)
    {
	LayoutInflater factory = LayoutInflater.from(this);

	switch (i) {

	case 0:
	    final View textEntryView = factory.inflate(R.layout.new_user, null);

	    return new AlertDialog.Builder(MainMenu.this)
		    // .setIconAttribute(android.R.attr.alertDialogIcon)
		    .setTitle(R.string.newUserTitle).setView(textEntryView)
		    .setPositiveButton(R.string.btnSave, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
			    EditText firstName = (EditText) textEntryView.findViewById(R.id.userFirstNameEditText);
			    EditText lastName = (EditText) textEntryView.findViewById(R.id.userLastNameEditText);
			    firstName.requestFocus();

			    /* User clicked OK so do some stuff */
			    if (firstName.getText().toString().equals("")
				    || firstName.getText().toString().trim().equals("")
				    || lastName.getText().toString().equals("")
				    || lastName.getText().toString().trim().equals(""))
			    {
				Toast.makeText(MainMenu.this, "User must have first and last name.", Toast.LENGTH_LONG)
					.show();
				firstName.setText("");
				lastName.setText("");
			    } else
			    {
				Toast.makeText(MainMenu.this, "The user is saving...", Toast.LENGTH_SHORT).show();
				uDBM.open();
				uDBM.insert(firstName.getText().toString(), lastName.getText().toString(), 0, 0, 0, 1, 1, 1,
					1, Usr);
				uDBM.close();
				firstName.setText("");
				lastName.setText("");
				updateSpinner();
			    }
			}
		    }).setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
			    EditText firstName = (EditText) textEntryView.findViewById(R.id.userFirstNameEditText);
			    EditText lastName = (EditText) textEntryView.findViewById(R.id.userLastNameEditText);
			    firstName.requestFocus();
			    /* User clicked cancel, close dialog */
			    firstName.setText("");
			    lastName.setText("");
			}
		    }).create();
	default:
	    break;
	}
	return null;
    }

    private void updateSpinner()
    {
	uDBM.open();
	userCursor = uDBM.getList(Usr);
	startManagingCursor(userCursor);

	String[] from = new String[] { "_id", "firstName", "lastName" };
	int[] to = new int[] { R.id.userIdTV, R.id.userFirstNameTV, R.id.userLastNameTV };

	adapter = new SimpleCursorAdapter(this, R.layout.user_list_item, userCursor, from, to);
	userSpinner.setAdapter(adapter);
	uDBM.close();
    }

    private void invokeActivityMenu(View arg0)
    {
	Intent myIntent = new Intent(arg0.getContext(), ActivityMenu.class);
	startActivityForResult(myIntent, 0);
    }

    private void invokeActivityView(View arg0)
    {
	Intent myIntent = new Intent(arg0.getContext(), ActivityList.class);
	startActivityForResult(myIntent, 0);
    }

    private void invokeDiaryList(View arg0)
    {
	Intent myIntent = new Intent(arg0.getContext(), DiaryList.class);
	startActivityForResult(myIntent, 0);
    }

    private void invokeMapView(View arg0)
    {
	Intent myIntent = new Intent(arg0.getContext(), MapMenu.class);
	startActivityForResult(myIntent, 0);
    }

    private void invokeStatisticsMenu(View arg0)
    {
	Intent myIntent = new Intent(arg0.getContext(), StatisticsMenu.class);
	startActivityForResult(myIntent, 0);
    }

    private void invokeSettingsMenu(View arg0)
    {
	Intent myIntent = new Intent(arg0.getContext(), SettingsMenu.class);
	startActivityForResult(myIntent, 0);
    }

    private void setOnClickListeners()
    {
	newActivityButton.setOnClickListener(new OnClickListener() {
	    public void onClick(View v)
	    {
		onButtonClickNewActivity(v);
	    }
	});
	viewActivityButton.setOnClickListener(new OnClickListener() {
	    public void onClick(View v)
	    {
		onButtonClickViewActivity(v);
	    }
	});
	mapButton.setOnClickListener(new OnClickListener() {
	    public void onClick(View v)
	    {
		onButtonClickMap(v);
	    }
	});
	diaryButton.setOnClickListener(new OnClickListener() {
	    public void onClick(View v)
	    {
		onButtonClickDiary(v);
	    }
	});
	statisticsButton.setOnClickListener(new OnClickListener() {
	    public void onClick(View v)
	    {
		onButtonClickStatistics(v);
	    }
	});
	settingsButton.setOnClickListener(new OnClickListener() {
	    public void onClick(View v)
	    {
		onButtonClickSettings(v);
	    }
	});
	newUserButton.setOnClickListener(new OnClickListener() {
	    public void onClick(View v)
	    {
		onButtonClickNewUser(v);
	    }
	});
    }

    private void onButtonClickNewActivity(View w)
    {
	invokeActivityMenu(w);
    }

    private void onButtonClickViewActivity(View w)
    {
	invokeActivityView(w);
    }

    private void onButtonClickMap(View w)
    {
	invokeMapView(w);
    }

    private void onButtonClickDiary(View w)
    {
	invokeDiaryList(w);
    }

    private void onButtonClickStatistics(View w)
    {
	invokeStatisticsMenu(w);
    }

    private void onButtonClickSettings(View w)
    {
	invokeSettingsMenu(w);
    }

    private void onButtonClickNewUser(View w)
    {
	showDialog(0, null);
    }
}