package edu.usf.cse.physmotive;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import edu.usf.cse.physmotive.db.DiaryDBM;

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

    private int userId;
    private int diaryId;
    protected Button addDiary;
    protected ListView diary_lv;
    private DiaryDBM diaryDBM;
    private Cursor cursor;
    private ListAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_list);
        retrieveBundleInfo();

        diaryDBM = new DiaryDBM(this);
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
                insert("Temp Entry", 120, 185, 25, 1, "This is a note.", userId);
            }
        });
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
        listAdapter = new SimpleCursorAdapter(this, R.layout.diary_list_item, cursor, new String[] { ID, NAME }, new int[] {
                R.id.D_ID, R.id.D_Name });
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
    private int insert(String _name, int _ht, int _wt, int _age, int _gender, String _note, int _usr)
    {
        diaryDBM.open();
        int id = diaryDBM.insert(_name, _ht, _wt, _age, _gender, _note, _usr);
        diaryDBM.close();
        updateList();

        return id;
    }

    // private void update(DiaryDBM dbm, int item_id, String _name, int _ht, int
    // _wt, int _age, int _gender, String _note, int _usr)
    // {
    // dbm.open();
    // dbm.update(item_id, _name, _ht, _wt, _age, _gender, _note, _usr);
    // dbm.close();
    // updateList();
    // }

    private void delete(int item_id, int _usr)
    {
        diaryDBM.open();
        diaryDBM.delete(item_id, _usr);
        diaryDBM.close();
        updateList();
    }

    // Currently unused
    // private Cursor get(DiaryDBM dbm, int item_id)
    // {
    // dbm.open();
    // Cursor c = dbm.get(item_id);
    // startManagingCursor(c);
    // dbm.close();
    // if (c.moveToFirst())
    // return c;
    // else
    // {
    // Toast.makeText(this, "No Collection found.", Toast.LENGTH_LONG).show();
    // return null;
    // }
    //
    // }

}
