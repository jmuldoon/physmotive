package edu.usf.cse.physmotive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
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
    static final String ACTIVITYID = "activityId";
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
    private SimpleCursorAdapter listAdapter;
    private ListView bind_lv;

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
        updateBoundList();
    }

    @Override
    public void onPause()
    {
        super.onPause();

    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        LayoutInflater factory = LayoutInflater.from(this);

        // Setup of the view for the dialog
        final View bindListDialog = factory.inflate(R.layout.bind_list, null);
        bind_lv = (ListView) bindListDialog.findViewById(R.id.bindList);
        activityDBM.open();
        Cursor bindCursor = activityDBM.getBindingList(diaryId);
        activityDBM.close();

        startManagingCursor(bindCursor);
        listAdapter = new SimpleCursorAdapter(this, R.layout.check_list_item, bindCursor, new String[] { ID, EDATE, CHECK },
                new int[] { R.id.itemId, R.id.itemName, R.id.itemCheck });

        listAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex)
            {
                int nCheckedIndex = cursor.getColumnIndex(CHECK);
                if (columnIndex == nCheckedIndex)
                {
                    CheckBox cb = (CheckBox) view;
                    boolean bChecked = (cursor.getInt(nCheckedIndex) != 0);
                    cb.setChecked(bChecked);
                    return true;
                }

                return false;
            }
        });

        bind_lv.setAdapter(listAdapter);

        return new AlertDialog.Builder(DiaryView.this).setTitle(R.string.multiRaceTitle).setView(bindListDialog)
                .setPositiveButton(R.string.btnSave, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        updateBoundList();
                    }
                }).create();
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

    // /////////////////////////////////////////////////////
    // Dead code for updating the DB with checkbox values //
    // breaks when accessing view out of screen
    // /////////////////////////////////////////////////////
    //
    // private void updateBindList()
    // {
    // activityDBM.open();
    // for (int i = 0; i < bind_lv.getCount(); i++)
    // {
    // int raceId = Integer.valueOf(((TextView)
    // bind_lv.getChildAt(i).findViewById(R.id.itemId)).getText().toString());
    // if (((CheckBox)
    // bind_lv.getChildAt(i).findViewById(R.id.itemCheck)).isChecked())
    // activityDBM.setChecked(raceId, diaryId, userId);
    // else
    // activityDBM.setUnChecked(raceId, userId);
    // }
    // activityDBM.close();
    // }
    // /////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////

    private void updateBoundList()
    {
        activityDBM.open();
        raceCur = activityDBM.getBoundList(diaryId);
        activityDBM.close();
        startManagingCursor(raceCur);
        adapter = new SimpleCursorAdapter(this, R.layout.plain_list_item, raceCur, new String[] { ID, EDATE, CHECK },
                new int[] { R.id.itemId, R.id.itemName, R.id.itemCheck });

        boundListView.setAdapter(adapter);
        boundListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
            {
                // Gets the cursor from the entry selected

                Cursor item = (Cursor) boundListView.getAdapter().getItem(position);
                // Gets the entry _id of the cursor
                int itemId = item.getInt(item.getColumnIndex(ID));

                // the new activity being started
                Intent myIntent = new Intent(v.getContext(), ActivityView.class);
                // The information being passed to the new activity
                Bundle bundle = new Bundle();

                // Preparing the data
                bundle.putInt(USERID, userId);
                bundle.putInt(ACTIVITYID, itemId);

                // Attaching info and starting new activity
                myIntent.putExtras(bundle);
                startActivity(myIntent);
            }

        });
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
