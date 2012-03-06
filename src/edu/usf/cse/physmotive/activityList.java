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
    private long Usr = 1;
    protected Button addActivity;
    protected ListView activity_lv;
    private ActivityDBM DBM;
    private Cursor cursor;
    private ListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_list);

	DBM = new ActivityDBM(this);
	activity_lv = (ListView) this.getListView();

	setupButton();
	registerForContextMenu(activity_lv);

	// Move DB info Into List View
	updateList();
    }

    private void setupButton()
    {
	addActivity = (Button) findViewById(R.id.btnAddActivity);
	addActivity.setOnClickListener(new OnClickListener() {
	    public void onClick(View v)
	    {
		insert(Usr);
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
	menu.add("Settings");

	return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
	if (!(item.hasSubMenu()))
	    Toast.makeText(this, "Hi", Toast.LENGTH_LONG).show();

	return true;
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
	int item_id = item.getInt(0);

	// the new activity being started
	Intent myIntent = new Intent(v.getContext(), ActivityView.class);
	// The information being passed to the new activity
	Bundle bundle = new Bundle();

	// Preparing the data
	bundle.putLong("userId", Usr);
	bundle.putInt("activityId", item_id);

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
	    int item_id = item.getInt(0);
	    String item_name = item.getString(1);
	    // sets dialog's header to name of selection
	    menu.setHeaderTitle(item_name);
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
	int item_id = item.getItemId();
	Bundle bundle = new Bundle();

	if (item.getTitle().equals("Select"))
	{
	    Intent myIntent = new Intent(this, ActivityView.class);

	    bundle.putLong("UserId", Usr);
	    bundle.putInt("activityId", item_id);

	    myIntent.putExtras(bundle);
	    startActivity(myIntent);
	} else if (item.getTitle().equals("Edit"))
	{
	    bundle.putInt("activityId", item_id);

	    showDialog(1, bundle);

	} else if (item.getTitle().equals("Delete"))
	{
	    delete(item_id, Usr);
	    Toast.makeText(this, "Entry Deleted", Toast.LENGTH_LONG).show();
	} else
	{
	    return false;
	}
	return true;
    }

    private void updateList()
    {
	DBM.open();
	cursor = DBM.getList(Usr);

	startManagingCursor(cursor);
	adapter = new SimpleCursorAdapter(this, R.layout.activity_list_item, cursor, new String[] { "_id", "_id" },
		new int[] { R.id.A_ID, R.id.A_Name });
	setListAdapter(adapter);
    }

    private long insert(long _usr)
    {
	DBM.open();
	long id = DBM.insert(_usr);
	DBM.close();
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

    private void delete(int item_id, long _usr)
    {
	DBM.open();
	DBM.delete(item_id, _usr);
	DBM.close();
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
