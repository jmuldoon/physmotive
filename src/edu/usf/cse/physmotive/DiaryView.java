package edu.usf.cse.physmotive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
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
    static final String CHECK = "checked";

    protected EditText diaryEntryEditText;
    protected EditText heightEditText;
    protected EditText weightEditText;
    protected EditText ageEditText;
    protected EditText notesEditText;
    protected ToggleButton genderToggleButton;
    protected ListView boundListView;
    protected Button bindRacesButton;
    protected Button cancelButton;
    protected Button saveButton;

    protected boolean[] _selections;

    private int diaryId;
    private int userId;
    private ActivityDBM activityDBM;
    private DiaryDBM diaryDBM;
    private UserDBM userDBM;
    private Cursor cur, checkCur, raceCur, diaryCur, userCur;

    private ListAdapter adapter;

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
        bindRacesButton = (Button) findViewById(R.id.bindRacesButton);
        diaryEntryEditText = (EditText) findViewById(R.id.diaryEntryEditText);
        heightEditText = (EditText) findViewById(R.id.heightEditText);
        weightEditText = (EditText) findViewById(R.id.weightEditText);
        ageEditText = (EditText) findViewById(R.id.ageEditText);
        notesEditText = (EditText) findViewById(R.id.notesEditText);
        boundListView = (ListView) findViewById(R.id.racesListView);
        genderToggleButton = (ToggleButton) findViewById(R.id.genderToggleButton);

        // pulling in bundle information
        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);
        diaryId = b.getInt(DIARYID);

        setOnClickListeners();

        // TODO: Get Check boxes to update properly.
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
        cur = activityDBM.getBindingList(diaryId);
        activityDBM.close();

        return new AlertDialog.Builder(this).setTitle("Races")
        // set list of races
                .setMultiChoiceItems(cur, CHECK, EDATE, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean checked)
                    {
                        activityDBM.open();
                        AlertDialog AD = (AlertDialog) dialog;
                        ListView list = AD.getListView();
                        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                        // gets the cursor of the selected row
                        checkCur = (Cursor) list.getItemAtPosition(position);

                        if (checked)
                        {
                            // Set checked(bound)
                            // updates the database with selection
                            activityDBM.setChecked(checkCur.getInt(checkCur.getColumnIndex(ID)), diaryId, userId);

                            // this is SUPPOSED to check the check box
                            list.setItemChecked(position, true);
                        } else
                        {
                            // Uncheck (unbind)
                            activityDBM.setUnChecked(checkCur.getInt(checkCur.getColumnIndex(ID)), userId);
                            list.setItemChecked(position, false);
                        }
                        activityDBM.close();
                    }
                }).setPositiveButton("OK", new DialogButtonClickHandler()).setOnCancelListener(new OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        updateBindList();
                    }
                }).create();
    }

    public class DialogButtonClickHandler implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int clicked)
        {
            // updates bind list on diary_view
            updateBindList();
        }
    }

    private void setupTextEdits()
    {
        diaryDBM.open();
        diaryCur = diaryDBM.get(diaryId);
        diaryDBM.close();
        startManagingCursor(diaryCur);

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
        activityDBM.open();
        raceCur = activityDBM.getBoundList(diaryId);
        activityDBM.close();
        startManagingCursor(raceCur);
        adapter = new SimpleCursorAdapter(this, R.layout.activity_list_item, raceCur, new String[] { ID, EDATE }, new int[] {
                R.id.A_ID, R.id.A_Name });

        boundListView.setAdapter(adapter);
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
