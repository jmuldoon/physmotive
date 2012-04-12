package edu.usf.cse.physmotive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import edu.usf.cse.physmotive.db.DiaryDBM;
import edu.usf.cse.physmotive.db.UserDBM;

public class DiaryList extends ListActivity
{
    // Internal Strings
    static final String USERID = "userId";
    static final String DIARYID = "diaryId";
    static final String ID = "_id";
    static final String NAME = "name";
    static final String SETTINGS = "Settings";
    static final String SELECT = "Select";
    static final String EDIT = "Edit";
    static final String DELETE = "Delete";
    static final String WEIGHT = "weight";
    static final String HEIGHT = "height";
    static final String AGE = "age";
    static final String GENDER = "gender";

    private int userId;
    private int diaryId;
    protected Button addDiary;
    protected ListView diary_lv;
    private DiaryDBM diaryDBM;
    private UserDBM userDBM;
    private Cursor cursor, userCur;
    private ListAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_list);
        retrieveBundleInfo();

        diaryDBM = new DiaryDBM(this);
        userDBM = new UserDBM(this);
        diary_lv = (ListView) this.getListView();

        setupButton();
        registerForContextMenu(diary_lv);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Restore state here
        // Move DB info Into List View
        updateList();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        cursor.close();
        diaryDBM.close();
    }

    private void setupButton()
    {
        addDiary = (Button) findViewById(R.id.btnAddDiary);
        addDiary.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                showDialog(0);
            }
        });
    }

    // //////////////////////////////////////////////
    // This is the beginning of the dialog windows //
    // //////////////////////////////////////////////
    @Override
    protected Dialog onCreateDialog(int i, Bundle args)
    {
        LayoutInflater factory = LayoutInflater.from(this);
        switch (i) {
        // This case is for the New User Dialog
        case 0:
            // Setup of the view for the dialog
            final View textEntryView = factory.inflate(R.layout.new_diary, null);

            return new AlertDialog.Builder(DiaryList.this)
                    // .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle(R.string.newUserTitle).setView(textEntryView)
                    .setPositiveButton(R.string.btnSave, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            // Gets text fields from dialog
                            EditText diaryName = (EditText) textEntryView.findViewById(R.id.diaryName);
                            diaryName.requestFocus();

                            // Verifies the fields have information in
                            // them
                            if (diaryName.getText().toString().equals("")
                                    || diaryName.getText().toString().trim().equals(""))
                            {
                                Toast.makeText(DiaryList.this, "Diary must have a name.", Toast.LENGTH_LONG).show();
                                diaryName.setText("");
                            } else
                            {
                                Toast.makeText(DiaryList.this, "The diary is saving...", Toast.LENGTH_SHORT).show();
                                diaryDBM.open();
                                userDBM.open();
                                userCur = userDBM.get(userId);
                                userDBM.close();
                                startManagingCursor(userCur);
                                diaryDBM.insert(diaryName.getText().toString(),
                                        userCur.getInt(userCur.getColumnIndex(HEIGHT)),
                                        userCur.getInt(userCur.getColumnIndex(WEIGHT)),
                                        userCur.getInt(userCur.getColumnIndex(AGE)),
                                        userCur.getInt(userCur.getColumnIndex(GENDER)), "", userId);
                                diaryDBM.close();
                                diaryName.setText("");
                                updateList();
                            }
                        }
                    }).setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            // clears the fields and closes the dialog
                            EditText diaryName = (EditText) textEntryView.findViewById(R.id.diaryName);
                            diaryName.requestFocus();
                            diaryName.setText("");
                        }
                    }).create();
        default:
            break;
        }
        return null;
    }

    // ////////////////////////////////////////
    // This is the start of the Options Menu //
    // ////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(SETTINGS);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getTitle().equals(SETTINGS))
        {
            Intent myIntent = new Intent(this, SettingsMenu.class);
            bundleUserInformation(myIntent);

            startActivity(myIntent);
            return true;
        } else
        {
            return false;
        }
    }

    // ///////////////////////////////////////////////
    // This is the on click event for the list view //
    // ///////////////////////////////////////////////
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {

        // Gets the cursor from the entry selected
        Cursor item = (Cursor) getListAdapter().getItem(position);
        // Gets the entry _id of the cursor
        diaryId = item.getInt(item.getColumnIndex(ID));

        // the new activity being started
        Intent myIntent = new Intent(v.getContext(), DiaryView.class);
        // The information being passed to the new activity
        bundleUserInformation(myIntent);

        startActivity(myIntent);
    }

    // /////////////////////////////////////////////////
    // This is the Long Click event for the list view //
    // /////////////////////////////////////////////////
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        // makes sure selection is from the list view
        if (v.getId() == getListView().getId())
        {
            // gets the menu selection
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            // gets the cursor for the item selected
            Cursor item = (Cursor) getListAdapter().getItem(info.position);
            // gets info from cursor
            diaryId = item.getInt(item.getColumnIndex(ID));
            String itemName = item.getString(item.getColumnIndex(NAME));
            // sets dialog's header to name of selection
            menu.setHeaderTitle(itemName);
            // creates the buttons in the menu
            String[] menuItems = getResources().getStringArray(R.array.SDC_menu);
            for (int i = 0; i < menuItems.length; i++)
            {
                menu.add(Menu.NONE, diaryId, i, menuItems[i]);
            }
        }
    }

    // ///////////////////////////////////////////
    // Actions available to the long click menu //
    // ///////////////////////////////////////////
    public boolean onContextItemSelected(MenuItem item)
    {
        diaryId = item.getItemId();

        if (item.getTitle().equals(SELECT))
        {
            Intent myIntent = new Intent(this, DiaryView.class);

            bundleUserInformation(myIntent);
            startActivity(myIntent);
        } else if (item.getTitle().equals(EDIT))
        {
            Bundle b = new Bundle();
            b.putInt(DIARYID, diaryId);

            showDialog(1, b);

        } else if (item.getTitle().equals(DELETE))
        {
            delete(diaryId, userId);
            Toast.makeText(this, "Entry Deleted", Toast.LENGTH_LONG).show();
        } else
        {
            return false;
        }
        return true;
    }

    private void updateList()
    {
        diaryDBM.open();
        cursor = diaryDBM.getList(userId);
        diaryDBM.close();

        startManagingCursor(cursor);
        listAdapter = new SimpleCursorAdapter(this, R.layout.plain_list_item, cursor, new String[] { ID, NAME }, new int[] {
                R.id.itemId, R.id.itemName });
        setListAdapter(listAdapter);

    }

    private void bundleUserInformation(Intent mIntent)
    {
        Bundle b = new Bundle();
        b.putInt(USERID, userId);
        b.putInt(DIARYID, diaryId);
        mIntent.putExtras(b);
    }

    private void retrieveBundleInfo()
    {
        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);
    }

    // /////////////////////
    // Database Functions //
    // /////////////////////
    private void delete(int item_id, int _usr)
    {
        diaryDBM.open();
        diaryDBM.delete(item_id, _usr);
        diaryDBM.close();
        updateList();
    }
}
