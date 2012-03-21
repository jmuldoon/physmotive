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

    private int userId;
    protected Button addDiary;
    protected ListView diary_lv;
    private DiaryDBM DBM;
    private Cursor cursor;
    private ListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.diary_list);
	Bundle b = getIntent().getExtras();
	userId = b.getInt(USERID);

	DBM = new DiaryDBM(this);
	diary_lv = (ListView) this.getListView();

	setupButton();
	registerForContextMenu(diary_lv);

	// Move DB info Into List View
	updateList();
    }

    @Override
    protected void onResume()
    {
	super.onResume();
	// Restore state here
	updateList();
    }

    @Override
    protected void onPause()
    {
	super.onPause();
	cursor.close();
	DBM.close();
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
	menu.add("Settings");
	menu.add("Delete Multiple");

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
	Intent myIntent = new Intent(v.getContext(), DiaryView.class);
	// The information being passed to the new activity
	Bundle bundle = new Bundle();

	// Preparing the data
	bundle.putLong(USERID, userId);
	bundle.putLong(DIARYID, item_id);

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
	    Intent myIntent = new Intent(this, DiaryView.class);

	    bundle.putLong(USERID, userId);
	    bundle.putInt(DIARYID, item_id);

	    myIntent.putExtras(bundle);
	    startActivity(myIntent);
	} else if (item.getTitle().equals("Edit"))
	{
	    bundle.putInt(DIARYID, item_id);

	    showDialog(1, bundle);

	} else if (item.getTitle().equals("Delete"))
	{
	    delete(item_id, userId);
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
	cursor = DBM.getList(userId);
	DBM.close();

	startManagingCursor(cursor);
	adapter = new SimpleCursorAdapter(this, R.layout.diary_list_item, cursor, new String[] { "_id", "name" }, new int[] {
		R.id.D_ID, R.id.D_Name });
	setListAdapter(adapter);

    }

    private long insert(String _name, int _ht, int _wt, int _age, int _gender, String _note, long _usr)
    {
	DBM.open();
	long id = DBM.insert(_name, _ht, _wt, _age, _gender, _note, _usr);
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
