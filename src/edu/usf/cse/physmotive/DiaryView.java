package edu.usf.cse.physmotive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;
import edu.usf.cse.physmotive.db.ActivityDBM;
import edu.usf.cse.physmotive.db.DiaryDBM;

public class DiaryView extends Activity
{
    protected EditText diaryEntryEditText;
    protected EditText heightEditText;
    protected EditText weightEditText;
    protected EditText ageEditText;
    protected ToggleButton genderToggleButton;
    protected EditText notesEditText;
    protected Button bindRacesButton;
    protected Button cancelButton;
    protected Button saveButton;
    private DiaryDBM dbdManager;
    private ActivityDBM dbaManager;
    private Cursor cur;

    // protected CharSequence[] _options = { "Race 1", "Race 2", "Race 3",
    // "Race 4" };
    // protected boolean[] _selections = new boolean[ _options.length ];

    private int diaryID;
    private int usrID;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.diary_view);

	// Creating DBM object
	dbdManager = new DiaryDBM(this);
	dbaManager = new ActivityDBM(this);

	// Connect interface elements to properties
	cancelButton = (Button) findViewById(R.id.cancelButton);
	saveButton = (Button) findViewById(R.id.saveButton);
	diaryEntryEditText = (EditText) findViewById(R.id.diaryEntryEditText);
	heightEditText = (EditText) findViewById(R.id.heightEditText);
	weightEditText = (EditText) findViewById(R.id.weightEditText);
	ageEditText = (EditText) findViewById(R.id.ageEditText);
	notesEditText = (EditText) findViewById(R.id.notesEditText);
	genderToggleButton = (ToggleButton) findViewById(R.id.genderToggleButton);
	bindRacesButton = (Button) findViewById(R.id.bindRacesButton);

	Bundle b = getIntent().getExtras();
	if (b != null)
	    diaryID = b.getInt("Coll_Id");

	setOnClickListeners();
    }

    private void setOnClickListeners()
    {
	cancelButton.setOnClickListener(new OnClickListener() {
	    public void onClick(View v)
	    {
		onButtonClickCancel(v);
	    }
	});
	saveButton.setOnClickListener(new OnClickListener() {
	    public void onClick(View v)
	    {
		onButtonClickSave(v);
	    }
	});

	bindRacesButton.setOnClickListener(new ButtonClickHandler());
    }

    private void onButtonClickCancel(View w)
    {
	cur.close();
	this.finish();
    }

    private void onButtonClickSave(View w)
    {
	int gend;
	if (genderToggleButton.isChecked())
	    gend = 1;
	else
	    gend = 0;

	if (diaryID == 0)
	    dbdManager.insert(diaryEntryEditText.getText().toString(), Integer.valueOf(heightEditText.getText().toString()),
		    Integer.valueOf(weightEditText.getText().toString()), Integer.valueOf(ageEditText.getText().toString()),
		    gend, notesEditText.getText().toString(), 0);
	else
	    dbdManager.update(diaryID, diaryEntryEditText.getText().toString(),
		    Integer.valueOf(heightEditText.getText().toString()),
		    Integer.valueOf(weightEditText.getText().toString()), Integer.valueOf(ageEditText.getText().toString()),
		    gend, notesEditText.getText().toString(), usrID);
	cur.close();
	invokeActivityDiaryList(w);
    }

    private void invokeActivityDiaryList(View w)
    {
	Intent myIntent = new Intent(w.getContext(), DiaryList.class);
	startActivityForResult(myIntent, 0);
    }

    public class ButtonClickHandler implements View.OnClickListener
    {
	public void onClick(View view)
	{
	    showDialog(0);
	}
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
	dbaManager.open();
	cur = dbaManager.getBindingList(usrID, diaryID);
	dbaManager.close();
	return new AlertDialog.Builder(this).setTitle("Races")
		.setMultiChoiceItems(cur, "checked", "entryDate", new DialogSelectionClickHandler())
		.setPositiveButton("OK", new DialogButtonClickHandler()).create();
    }

    public class DialogSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener
    {
	public void onClick(DialogInterface dialog, int clicked, boolean selected)
	{
	    // Log.i( "ME", _options[ clicked ] + " selected: " + selected );
	}
    }

    public class DialogButtonClickHandler implements DialogInterface.OnClickListener
    {
	public void onClick(DialogInterface dialog, int clicked)
	{
	    switch (clicked) {
	    case DialogInterface.BUTTON_POSITIVE:
		// printSelectedRaces();
		break;
	    }
	}
    }

    // protected void printSelectedRaces(){
    // for( int i = 0; i < _options.length; i++ ){
    // Log.i( "ME", _options[ i ] + " selected: " + _selections[i] );
    // }
    // }
}