package edu.usf.cse.physmotive;

import edu.usf.cse.physmotive.db.DiaryDBM;
import edu.usf.cse.physmotive.ui.MultipleSelectionDialogue;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

public class DiaryView extends Activity{
	protected EditText diaryEntryEditText;
	protected EditText heightEditText;
	protected EditText weightEditText;
	protected EditText ageEditText;
	protected ToggleButton genderToggleButton;
	protected EditText notesEditText;
	protected Button bindRacesButton;
	protected Button cancelButton;
	protected Button saveButton;
	private DiaryDBM dbManager;
	private MultipleSelectionDialogue msdBox;
	
	private int diaryID; 
	
	// Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_view);

        // Creating DBM object
        dbManager = new DiaryDBM(this);
        
        // Connect interface elements to properties
        cancelButton =(Button)findViewById(R.id.cancelButton);
        saveButton =(Button)findViewById(R.id.saveButton);
        diaryEntryEditText = (EditText)findViewById(R.id.diaryEntryEditText);
        heightEditText = (EditText)findViewById(R.id.heightEditText);
        weightEditText = (EditText)findViewById(R.id.weightEditText);
        ageEditText = (EditText)findViewById(R.id.ageEditText);
        notesEditText = (EditText)findViewById(R.id.notesEditText);
        genderToggleButton = (ToggleButton)findViewById(R.id.genderToggleButton);
        bindRacesButton = (Button)findViewById(R.id.bindRacesButton);
        
        Bundle b = getIntent().getExtras();
        if(b != null)
        	diaryID = b.getInt("Coll_Id");
        msdBox = new MultipleSelectionDialogue();
        
        setOnClickListeners();
    }
    
    private void setOnClickListeners()
    {
    	cancelButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onButtonClickCancel(v);
			}
		});
    	saveButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onButtonClickSave(v);
			}
		});
    	bindRacesButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onButtonClickBindRaces(v);
			}
		});
    }
    
    private void onButtonClickBindRaces(View w)
    {
    	msdBox.showDialog(0);
    }
    private void onButtonClickCancel(View w)
    {
    	this.finish();
    }
    private void onButtonClickSave(View w)
    {
    	//if (diaryID == 0)
    		//dbManager.insert(diaryEntryEditText.getText(), heightEditText.getText(), weightEditText.getText(), ageEditText.getText(), genderToggleButton.getText(), notesEditText.getText(), diaryID);
    	//else
    		//dbManager.update(diaryEntryEditText.getText(), heightEditText.getText(), weightEditText.getText(), ageEditText.getText(), genderToggleButton.getText(), notesEditText.getText(), diaryID);
    }
}
