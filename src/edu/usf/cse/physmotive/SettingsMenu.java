package edu.usf.cse.physmotive;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingsMenu extends Activity{
	protected Button _updateButton;
	protected Button _exportButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_menu);    
        
        // Connect interface elements to properties
        _updateButton =(Button)findViewById(R.id.updateButton);
        _exportButton =(Button)findViewById(R.id.exportButton);
        
        setOnClickListeners();
	}
	
	private void setOnClickListeners()
    {
		_updateButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onButtonClickUpdate(v);
			}
		});
		_exportButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onButtonClickExport(v);
			}
		});
    }
	
	private void onButtonClickUpdate(View w)
    {
//		if (diaryID == 0)
//		    dbManager.insert(diaryEntryEditText.getText().toString(), Long.valueOf(heightEditText.getText().toString()),
//			    Long.valueOf(weightEditText.getText().toString()), Long.valueOf(ageEditText.getText().toString()),
//			    Long.valueOf(genderToggleButton.getText().toString()), notesEditText.getText().toString(), 0);
//		else
//		    dbManager.update(diaryID, diaryEntryEditText.getText().toString(),
//			    Long.valueOf(heightEditText.getText().toString()), Long.valueOf(weightEditText.getText().toString()),
//			    Long.valueOf(ageEditText.getText().toString()), Long.valueOf(genderToggleButton.getText().toString()),
//			    notesEditText.getText().toString(), Usr);
//	    }
    }
    private void onButtonClickExport(View w)
    {
    	//nothing as of right now
    }
	
}
