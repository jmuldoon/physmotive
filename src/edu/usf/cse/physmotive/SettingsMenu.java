package edu.usf.cse.physmotive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import edu.usf.cse.physmotive.db.UserDBM;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SettingsMenu extends Activity{
	protected Button _updateButton;
	protected Button _exportButton;
	
	protected EditText diaryEntryEditText;
    protected EditText heightEditText;
    protected EditText weightEditText;
    protected EditText ageEditText;
    protected ToggleButton genderToggleButton;
    protected ToggleButton unitToggleButton;
    protected ToggleButton orientationToggleButton;
    protected RadioButton radioButtonCSV;
    protected RadioButton radioButtonTXT;
    
	
	private int diaryID;
    private int usrID;
	
    private UserDBM dbuManager;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_menu);    
        
        // Connect interface elements to properties
        _updateButton =(Button)findViewById(R.id.updateButton);
        _exportButton =(Button)findViewById(R.id.exportButton);
        diaryEntryEditText = (EditText) findViewById(R.id.diaryEntryEditText);
		heightEditText = (EditText) findViewById(R.id.heightEditText);
		weightEditText = (EditText) findViewById(R.id.weightEditText);
		ageEditText = (EditText) findViewById(R.id.ageEditText);
		genderToggleButton = (ToggleButton) findViewById(R.id.genderToggleButton);
		unitToggleButton = (ToggleButton)findViewById(R.id.unitToggleButton);
		orientationToggleButton = (ToggleButton)findViewById(R.id.orientationToggleButton);
		radioButtonCSV = (RadioButton)findViewById(R.id.radioButtonCSV);
		radioButtonTXT = (RadioButton)findViewById(R.id.radioButtonTXT);
        
		dbuManager = new UserDBM(this);
		
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
		int gend, uni, ori;
    	if(genderToggleButton.isChecked())
    		gend = 1;
    	else
    		gend = 0;
    	if(unitToggleButton.isChecked())
    		uni = 1;
    	else
    		uni = 0;
    	if(orientationToggleButton.isChecked())
    		ori = 1;
    	else
    		ori = 0;
    	dbuManager.open();
    	dbuManager.update(diaryID, Integer.valueOf(heightEditText.getText().toString()), Integer.valueOf(weightEditText.getText().toString()),
		    	Integer.valueOf(ageEditText.getText().toString()), gend, uni, ori, 0, usrID);
    	dbuManager.close();
    }
    private void onButtonClickExport(View w)
    {
        String state = Environment.getExternalStorageState();
        String fileName;
        String fileExtension;
        String cOrNl; //comma or new line, for CSV or TXT
        if(radioButtonCSV.isChecked()) { 
            fileExtension = ".csv";
            cOrNl = ","; 
        } else { 
            fileExtension = ".txt"; 
            cOrNl = "\n";
        }
        fileName = "test" + fileExtension;
        
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            String unit;
            if (unitToggleButton.isChecked()) { unit = "Imperial"; } else { unit = "Metric"; }
            String orientation;
            if (orientationToggleButton.isChecked()) { orientation = "Portrait";} else { orientation = "Landscape"; }
            String gender;
            if (genderToggleButton.isChecked()) { gender = "Male"; } else { gender = "Female"; }
            String height = heightEditText.getText().toString();
            String weight = weightEditText.getText().toString();
            String age = ageEditText.getText().toString();
            String writeString = unit + "\n" + orientation + "\n" + gender
                    + "\n" + height + "\n" + weight + "\n" + age + "\n";
            try {
                OutputStream outFile = new FileOutputStream(file);
                outFile.write(writeString.getBytes());
                outFile.close();
                Toast.makeText(this, "It worked " + fileName, Toast.LENGTH_SHORT).show();
            } 
            
            catch (FileNotFoundException ex) {} 
            catch (IOException ex) {}
        }
         
    }
	
}
