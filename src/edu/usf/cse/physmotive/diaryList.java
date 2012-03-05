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

public class DiaryList extends ListActivity
{
    private long Usr = 1;
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

	DBM = new DiaryDBM(this);
	diary_lv = (ListView) this.getListView();

	setupButton();
	registerForContextMenu(diary_lv);

	// Move DB info Into List View
	updateList();
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
	bundle.putLong("User_Id", Usr);
	bundle.putInt("Coll_Id", item_id);

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

	    bundle.putLong("User_Id", Usr);
	    bundle.putInt("Coll_Id", item_id);

	    myIntent.putExtras(bundle);
	    startActivity(myIntent);
	} else if (item.getTitle().equals("Edit"))
	{
	    bundle.putInt("Coll_Id", item_id);

	    showDialog(1, bundle);

	} else if (item.getTitle().equals("Delete"))
	{
	    delete(DBM, item_id);
	    Toast.makeText(this, " Collection Deleted", Toast.LENGTH_LONG).show();
	} else
	{
	    return false;
	}
	return true;
    }

    @Override
    protected Dialog onCreateDialog(int i, Bundle args)
    {
	LayoutInflater factory = LayoutInflater.from(this);
	final View textEntryView = factory.inflate(R.layout.new_diary, null);

	switch (i) {

	// ///////////////////////////////////////////////////////
	// This dialog is for the creation of a new diary entry //
	// ///////////////////////////////////////////////////////
	case 0:
	    return new AlertDialog.Builder(DiaryList.this)
		    // .setIconAttribute(android.R.attr.alertDialogIcon)
		    .setTitle(R.string.diaryTitle).setView(textEntryView)
		    .setPositiveButton(R.string.btnSave, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
			    EditText collName = (EditText) textEntryView.findViewById(R.id.diaryName);

			    /* User clicked OK so do some stuff */
			    if (collName.getText().toString().equals("") || collName.getText().toString().trim().equals(""))
			    {
				Toast.makeText(DiaryList.this, "Collections must have a name.", Toast.LENGTH_LONG).show();
			    } else
			    {
				Toast.makeText(DiaryList.this, "The collection is saving...", Toast.LENGTH_SHORT).show();
				insert(DBM, collName.getText().toString());
				collName.setText("");

			    }
			}
		    }).setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
			    /* User clicked cancel, close dialog */
			    EditText collName = (EditText) textEntryView.findViewById(R.id.diaryName);
			    collName.setText("");
			}
		    }).create();
	case 1:

	    final int item_id = args.getInt("Coll_Id");

	    return new AlertDialog.Builder(DiaryList.this)
		    // .setIconAttribute(android.R.attr.alertDialogIcon)
		    .setTitle(R.string.diaryTitle).setView(textEntryView)
		    .setPositiveButton(R.string.btnUpdate, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
			    EditText collName = (EditText) textEntryView.findViewById(R.id.diaryName);

			    /* User clicked OK so do some stuff */
			    if (collName.getText().toString().equals("") || collName.getText().toString().trim().equals(""))
			    {
				Toast.makeText(DiaryList.this, "Collections must have a name.", Toast.LENGTH_LONG).show();
			    } else
			    {
				Toast.makeText(DiaryList.this, "The collection is updating...", Toast.LENGTH_SHORT).show();

				update(DBM, item_id, collName.getText().toString());
				collName.setText("");
			    }
			}
		    }).setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
			    /* User clicked cancel, close dialog */
			    EditText collName = (EditText) textEntryView.findViewById(R.id.diaryName);
			    collName.setText("");
			}
		    }).create();

	}
	return null;
    }

    @Override
    protected void onPrepareDialog(final int id, final Dialog dialog, Bundle args)
    {
	EditText collName = (EditText) dialog.findViewById(R.id.diaryName);

	switch (id) {
	case 0:
	    break;
	case 1:
	    final int item_id = args.getInt("Coll_Id");
	    final AlertDialog ad = (AlertDialog) dialog;
	    ad.setButton(AlertDialog.BUTTON_POSITIVE, "test", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton)
		{
		    EditText collName = (EditText) ad.findViewById(R.id.diaryName);

		    /* User clicked OK so do some stuff */
		    if (collName.getText().toString().equals("") || collName.getText().toString().trim().equals(""))
		    {
			Toast.makeText(DiaryList.this, "Collections must have a name.", Toast.LENGTH_LONG).show();
		    } else
		    {
			Toast.makeText(DiaryList.this, "The collection is updating...", Toast.LENGTH_SHORT).show();

			update(DBM, item_id, collName.getText().toString());
			collName.setText("");
		    }
		}
	    });
	    Cursor cursor = get(DBM, item_id);
	    collName.setText(cursor.getString(1));

	}

    }

    private void updateList()
    {
	cursor = DBM.getList(1);
	startManagingCursor(cursor);
	adapter = new SimpleCursorAdapter(this, R.layout.diary_list_item, cursor, new String[] { "_id", "name" }, new int[] {
		R.id.D_ID, R.id.D_Name });
	setListAdapter(adapter);
    }

    private long insert(DiaryDBM dbm, String diaryName)
    {

	dbm.open();
	// long id = dbm.insert(diaryName, Usr);
	dbm.close();
	updateList();

	return 1; // id
    }

    private void update(DiaryDBM dbm, int item_id, String diary_name)
    {
	dbm.open();
	// dbm.update(item_id, dairy_name, Usr);
	dbm.close();
	updateList();
    }

    private void delete(DiaryDBM dbm, int item_id)
    {
	dbm.open();
	// dbm.delete(item_id, Usr);
	dbm.close();
	updateList();
    }

    private Cursor get(DiaryDBM dbm, int item_id)
    {
	dbm.open();
	Cursor c = dbm.get(item_id);
	startManagingCursor(c);
	dbm.close();
	if (c.moveToFirst())
	    return c;
	else
	{
	    Toast.makeText(this, "No Collection found.", Toast.LENGTH_LONG).show();
	    return null;
	}

    }

}
