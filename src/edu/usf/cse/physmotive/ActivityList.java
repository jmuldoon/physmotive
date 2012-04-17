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
import edu.usf.cse.physmotive.db.ActivityDBM;

public class ActivityList extends ListActivity
{
    // Internal Strings
    static final String SETTINGS = "Settings";
    static final String USERID = "userId";
    static final String ACTIVITYID = "activityId";
    static final String ID = "_id";
    static final String ENTRYDATE = "entryDate";
    static final String UPDATEDATE = "updateDate";
    static final String SELECT = "Select";
    static final String EDIT = "Edit";
    static final String DELETE = "Delete";

    private int userId;
    protected Button addActivityBtn;
    protected ListView activity_lv;
    private ActivityDBM activityDBM;
    private Cursor cursor;
    private ListAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        activityDBM = new ActivityDBM(this);
        activity_lv = (ListView) this.getListView();

        // pulling in bundle information
        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);

        setupButton();
        registerForContextMenu(activity_lv);
    }

    // Also runs with onCreate
    @Override
    protected void onResume()
    {
        super.onResume();
        // Inserts info into listview
        updateList();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        cursor.close();
        activityDBM.close();
    }

    private void setupButton()
    {
        addActivityBtn = (Button) findViewById(R.id.btnAddActivity);
        addActivityBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                Intent myIntent = new Intent(ActivityList.this, ActivityMenu.class);
                Bundle b = new Bundle();
                b.putInt(USERID, userId);

                myIntent.putExtras(b);
                startActivity(myIntent);
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
            Bundle b = new Bundle();
            b.putInt(USERID, userId);

            myIntent.putExtras(b);
            startActivity(myIntent);
            return true;
        } else
            return false;
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
        int item_id = item.getInt(item.getColumnIndex(ID));

        // the new activity being started
        Intent myIntent = new Intent(v.getContext(), ActivityView.class);
        // The information being passed to the new activity
        Bundle bundle = new Bundle();

        // Preparing the data
        bundle.putInt(USERID, userId);
        bundle.putInt(ACTIVITYID, item_id);

        // Attaching info and starting new activity
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    // /////////////////////////////////////////////////
    // This is the Long Click event for the list view //
    // /////////////////////////////////////////////////
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        if (v.getId() == getListView().getId())
        {
            // gets the menu selection
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            // gets the cursor for the item selected
            Cursor item = (Cursor) getListAdapter().getItem(info.position);
            // gets info from cursor
            int item_id = item.getInt(item.getColumnIndex(ID));
            String itemName = item.getString(item.getColumnIndex(ENTRYDATE));
            // sets dialog's header to name of selection
            menu.setHeaderTitle(itemName);
            // creates the buttons in the menu
            String[] menuItems = getResources().getStringArray(R.array.SDC_menu);
            for (int i = 0; i < menuItems.length; i++)
            {
                menu.add(Menu.NONE, item_id, i, menuItems[i]);
            }
        }
    }

    // ///////////////////////////////////////////
    // Actions available to the long click menu //
    // ///////////////////////////////////////////
    public boolean onContextItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putInt(USERID, userId);
        bundle.putInt(ACTIVITYID, itemId);

        if (item.getTitle().equals(SELECT))
        {
            Intent myIntent = new Intent(this, ActivityView.class);
            myIntent.putExtras(bundle);
            startActivity(myIntent);
        } else if (item.getTitle().equals(DELETE))
        {
            delete(itemId, userId);
            Toast.makeText(this, "Entry Deleted", Toast.LENGTH_LONG).show();
        } else
        {
            return false;
        }
        return true;
    }

    private void updateList()
    {
        activityDBM.open();
        cursor = activityDBM.getList(userId);

        startManagingCursor(cursor);
        listAdapter = new SimpleCursorAdapter(this, R.layout.plain_list_item, cursor, new String[] { ID, ENTRYDATE },
                new int[] { R.id.itemId, R.id.itemName });
        setListAdapter(listAdapter);
        activityDBM.close();
    }

    // private long insert(int usr)
    // {
    // activityDBM.open();
    // long id = activityDBM.insert(usr);
    // activityDBM.close();
    // updateList();
    //
    // return id;
    // }

    // private void update(DiaryDBM dbm, int item_id, String _name, int _ht, int
    // _wt, int _age, int _gender, String _note, int _usr)
    // {
    // dbm.open();
    // dbm.update(item_id, _name, _ht, _wt, _age, _gender, _note, _usr);
    // dbm.close();
    // updateList();
    // }

    private void delete(int itemId, int usr)
    {
        activityDBM.open();
        activityDBM.delete(itemId);
        activityDBM.close();
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
