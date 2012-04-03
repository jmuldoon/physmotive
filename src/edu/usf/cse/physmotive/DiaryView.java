package edu.usf.cse.physmotive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.usf.cse.physmotive.db.ActivityDBM;
import edu.usf.cse.physmotive.db.DiaryDBM;
import edu.usf.cse.physmotive.db.UserDBM;

public class DiaryView extends Activity
{
    static final String ID = "_id";
    static final String DIARYID = "diaryId";
    static final String USERID = "userId";
    static final String NAME = "name";
    static final String HEIGHT = "height";
    static final String WEIGHT = "weight";
    static final String AGE = "age";
    static final String EDATE = "entryDate";
    static final String NOTE = "note";
    static final String GENDER = "gender";

    protected EditText diaryEntryEditText;
    protected EditText heightEditText;
    protected EditText weightEditText;
    protected EditText ageEditText;
    protected ToggleButton genderToggleButton;
    protected EditText notesEditText;
    protected Button bindRacesButton;
    protected Button cancelButton;
    protected Button saveButton;
    private Cursor cur;
    private Cursor checkCur;
    protected boolean[] _selections;

    private int diaryId;
    private int userId;
    private ActivityDBM activityDBM;
    private DiaryDBM diaryDBM;
    private UserDBM userDBM;
    private Cursor diaryCur;
    private Cursor userCur;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_view);

        // Creating DBM object
        activityDBM = new ActivityDBM(this);
        diaryDBM = new DiaryDBM(this);
        userDBM = new UserDBM(this);

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

        // TODO: GET Multi-select working properly.
        // TODO: GET Vertical Scrolling for Activities that are bound to the
        // diary. And have it link to Activity View.
        // TODO: Dialog on update fix

    }

    @Override
    public void onResume()
    {
        super.onResume();
        setupTextEdits();
        setupToggleButtons();
        updateBindList();
    }

    @Override
    public void onPause()
    {
        super.onPause();

    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        activityDBM.open();
        cur = activityDBM.getBindingList(userId, diaryId);
        activityDBM.close();

        _selections = new boolean[cur.getCount()];
        return new AlertDialog.Builder(this).setTitle("Races")
                .setMultiChoiceItems(cur, "checked", "entryDate", new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int clicked, boolean selected)
                    {
                        // activityDBM.open();
                        // AlertDialog AD = (AlertDialog) dialog;
                        // checkCur = (Cursor)
                        // AD.getListView().getItemAtPosition(clicked);
                        // Toast.makeText(DiaryView.this,
                        // Integer.toString(checkCur.getInt(checkCur.getColumnIndex(ID))),
                        // Toast.LENGTH_SHORT).show();
                        // activityDBM.setChecked(checkCur.getInt(checkCur.getColumnIndex(ID)),
                        // diaryId, userId, 1);
                        // activityDBM.close();
                        // // _selections[clicked] = selected;
                        // Log.d("MultiSelect", cur.moveToPosition(clicked) +
                        // " selected: " + selected);
                    }
                }).setPositiveButton("OK", new DialogButtonClickHandler()).create();
    }

    // @Override
    // protected void onPrepareDialog(int id, Dialog dialog)
    // {
    // dialog = new AlertDialog.Builder(this).setTitle("Races")
    // .setMultiChoiceItems(cur, "checked", "entryDate", new
    // DialogInterface.OnMultiChoiceClickListener() {
    // @Override
    // public void onClick(DialogInterface dialog, int clicked, boolean
    // selected)
    // {
    // AlertDialog AD = (AlertDialog) dialog;
    // checkCur = (Cursor) AD.getListView().getItemAtPosition(clicked);
    // Toast.makeText(DiaryView.this,
    // Integer.toString(checkCur.getInt(checkCur.getColumnIndex(ID))),
    // Toast.LENGTH_SHORT).show();
    // activityDBM.open();
    // activityDBM.setChecked(clicked, diaryId, userId, 1);
    // activityDBM.close();
    // // _selections[clicked] = selected;
    // Log.d("MultiSelect", cur.moveToPosition(clicked) + " selected: " +
    // selected);
    // }
    // }).setPositiveButton("OK", new DialogButtonClickHandler()).create();
    // }

    public class DialogButtonClickHandler implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int clicked)
        {
            switch (clicked) {
            case DialogInterface.BUTTON_POSITIVE:
                for (int i = 0; i < cur.getCount(); i++)
                {
                    Log.d("MultiSelectOK", cur.moveToPosition(i) + " selected: " + _selections[i]);
                }
                break;
            }
        }
    }

    private void setupTextEdits()
    {
        diaryDBM.open();
        diaryCur = diaryDBM.get(diaryId);
        diaryDBM.close();
        startManagingCursor(diaryCur);

        Toast.makeText(this, diaryCur.getString(diaryCur.getColumnIndex(NAME)), Toast.LENGTH_SHORT).show();

        diaryEntryEditText.setText(diaryCur.getString(diaryCur.getColumnIndex(NAME)));
        notesEditText.setText(diaryCur.getString(diaryCur.getColumnIndex(NOTE)));
        heightEditText.setText(diaryCur.getString(diaryCur.getColumnIndex(HEIGHT)));
        weightEditText.setText(diaryCur.getString(diaryCur.getColumnIndex(WEIGHT)));
        ageEditText.setText(diaryCur.getString(diaryCur.getColumnIndex(AGE)));
    }

    private void setupToggleButtons()
    {
        diaryDBM.open();
        diaryCur = diaryDBM.get(diaryId);
        diaryDBM.close();
        startManagingCursor(diaryCur);

        genderToggleButton.setChecked(diaryCur.getInt(diaryCur.getColumnIndex(GENDER)) == 1);
    }

    private void updateBindList()
    {
        // TODO: Setup List View
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
        this.finish();
    }

    private void onButtonClickSave(View w)
    {
        int gend;
        if (genderToggleButton.isChecked())
            // male
            gend = 1;
        else
            // female
            gend = 0;
        diaryDBM.open();
        if (((Integer) diaryId).intValue() == 0)
        {
            diaryDBM.insert(diaryEntryEditText.getText().toString(), Integer.valueOf(heightEditText.getText().toString()),
                    Integer.valueOf(weightEditText.getText().toString()), Integer.valueOf(ageEditText.getText().toString()),
                    gend, notesEditText.getText().toString(), 0);
            Log.d("insert", "insert");
        } else
        {
            diaryDBM.update(diaryId, diaryEntryEditText.getText().toString(),
                    Integer.valueOf(heightEditText.getText().toString()),
                    Integer.valueOf(weightEditText.getText().toString()), Integer.valueOf(ageEditText.getText().toString()),
                    gend, notesEditText.getText().toString(), userId);
            Log.d("update", "update");
        }
        diaryDBM.close();

        // Exits the activity and goes back
        finish();
    }

    public class ButtonClickHandler implements View.OnClickListener
    {
        public void onClick(View view)
        {
            showDialog(0);
        }
    }
}
