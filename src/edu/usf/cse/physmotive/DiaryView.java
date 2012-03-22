package edu.usf.cse.physmotive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;
import edu.usf.cse.physmotive.db.ActivityDBM;
import edu.usf.cse.physmotive.db.DiaryDBM;

public class DiaryView extends Activity
{
    public static final String DIARYID = "diaryId";
    public static final String USERID = "userId";

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
    protected boolean[] _selections;

    private int diaryId;
    private int userId;

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

        // pulling in bundle information
        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);
        diaryId = b.getInt(DIARYID);

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
        // cur.close();
        this.finish();
    }

    private void onButtonClickSave(View w)
    {
        long gend;
        if (genderToggleButton.isChecked())
            gend = 1;
        else
            gend = 0;

        if (((Integer) diaryId).intValue() == 0)
            dbdManager.insert(diaryEntryEditText.getText().toString(), Long.valueOf(heightEditText.getText().toString()),
                    Long.valueOf(weightEditText.getText().toString()), Long.valueOf(ageEditText.getText().toString()), gend,
                    notesEditText.getText().toString(), 0);
        else
            dbdManager.update(diaryId, diaryEntryEditText.getText().toString(),
                    Long.valueOf(heightEditText.getText().toString()), Long.valueOf(weightEditText.getText().toString()),
                    Long.valueOf(ageEditText.getText().toString()), gend, notesEditText.getText().toString(), userId);
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
    protected Dialog onCreateDialog(int id){
        dbaManager.open();
        cur = dbaManager.getBindingList(userId, diaryId);
        dbaManager.close();
        
        _selections = new boolean[cur.getCount()];
        return new AlertDialog.Builder(this).setTitle("Races")
                .setMultiChoiceItems(cur, "checked", "entryDate", new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int clicked,
                            boolean selected) {
                    	_selections[clicked] = selected;
                    	Log.d( "MultiSelect", cur.moveToPosition(clicked) + " selected: " + selected );
                    }
                })
                .setPositiveButton("OK", new DialogButtonClickHandler()).create();
    }

    public class DialogButtonClickHandler implements DialogInterface.OnClickListener
    {
    	@Override
        public void onClick(DialogInterface dialog, int clicked)
        {
            switch (clicked) {
            case DialogInterface.BUTTON_POSITIVE:
            	 for(int i = 0; i < cur.getCount(); i++){
            		 Log.d( "MultiSelectOK", cur.moveToPosition(i) + " selected: " + _selections[i] );
                 }
                break;
            }
        }
    }
}
