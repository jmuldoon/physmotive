package edu.usf.cse.physmotive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.usf.cse.physmotive.db.ActivityDBM;
import edu.usf.cse.physmotive.db.DiaryDBM;
import edu.usf.cse.physmotive.db.LocationDBM;
import edu.usf.cse.physmotive.db.UserDBM;

public class SettingsMenu extends Activity
{
    public static final String USERID = "userId";

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
    
    //Cursors for exporting databases
    protected Cursor activityCur;
    protected Cursor diaryCur; 
    protected Cursor locationCur;
    protected Cursor userCur;

    private int diaryId;
    private int userId;

    private UserDBM dbmUser;
    private ActivityDBM dbmActivity;
    private DiaryDBM dbmDiary;
    private LocationDBM dbmLocation;
    

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_menu);

        // pulling in bundle information
        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);

        // Connect interface elements to properties
        _updateButton = (Button) findViewById(R.id.updateButton);
        _exportButton = (Button) findViewById(R.id.exportButton);
        diaryEntryEditText = (EditText) findViewById(R.id.diaryEntryEditText);
        heightEditText = (EditText) findViewById(R.id.heightEditText);
        weightEditText = (EditText) findViewById(R.id.weightEditText);
        ageEditText = (EditText) findViewById(R.id.ageEditText);
        genderToggleButton = (ToggleButton) findViewById(R.id.genderToggleButton);
        unitToggleButton = (ToggleButton) findViewById(R.id.unitToggleButton);
        orientationToggleButton = (ToggleButton) findViewById(R.id.orientationToggleButton);
        radioButtonCSV = (RadioButton) findViewById(R.id.radioButtonCSV);
        radioButtonTXT = (RadioButton) findViewById(R.id.radioButtonTXT);
        
        dbmActivity = new ActivityDBM(this);
        dbmDiary = new DiaryDBM(this);
        dbmLocation = new LocationDBM(this);
        dbmUser = new UserDBM(this); 
        
        //dbmUser.open();
        //userCur = dbmUser.getList(userId)
        
        

        setOnClickListeners();
    }

    private void setOnClickListeners()
    {
        _updateButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                onButtonClickUpdate(v);
            }
        });
        _exportButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                onButtonClickExport(v);
            }
        });
    }

    private void onButtonClickUpdate(View w)
    {
        int gend, uni, ori;
        if (genderToggleButton.isChecked())
            gend = 1;
        else
            gend = 0;
        if (unitToggleButton.isChecked())
            uni = 1;
        else
            uni = 0;
        if (orientationToggleButton.isChecked())
            ori = 1;
        else
            ori = 0;
        dbmUser.open();
        dbmUser.update(diaryId, Integer.valueOf(heightEditText.getText().toString()),
                Integer.valueOf(weightEditText.getText().toString()), Integer.valueOf(ageEditText.getText().toString()),
                gend, uni, ori, 0, userId);
        dbmUser.close();
    }

    private void onButtonClickExport(View w)
    {
        String state = Environment.getExternalStorageState();
        String fileName;
        String fileExtension;
        String cOrNl; // comma or new line, for CSV or TXT
        
        //Open databases, create cursors and close databases
        dbmActivity.open();
        dbmDiary.open();
        dbmLocation.open();
        dbmUser.open();
        
        //Get cursors
        activityCur = dbmActivity.getForExport(userId);
        diaryCur = dbmDiary.getForExport();
        locationCur = dbmLocation.getForExport();
        userCur = dbmUser.getForExport();
        
        //Close Databases
        dbmActivity.close();
        dbmDiary.close();
        dbmLocation.close();
        dbmUser.close();

        if (radioButtonCSV.isChecked()) { fileExtension = ".csv"; } else { fileExtension = ".txt"; }
        
        fileName = userId + fileExtension; //will take user name in future, for testing purposes currently

        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            //need to make function for this to be prettier
            
            String writeString;
            if(radioButtonCSV.isChecked()) { writeString = writeCSV(); } else { writeString=writeTXT(); }
            
            OutputStream outFile = null;
            try
            {
                outFile = new FileOutputStream(file);
                outFile.write(writeString.getBytes());
                outFile.close();
                //Toast.makeText(this, "It worked " + fileName, Toast.LENGTH_SHORT).show();
            }
            catch (FileNotFoundException ex) {}
            catch (IOException ex) {}         
        }

    }
    
    private String writeCSV() {
        String csvStr = unitToggleButton.getText().toString();
        csvStr.concat(",");
        csvStr.concat(orientationToggleButton.getText().toString());
        csvStr.concat(",");
        csvStr.concat(genderToggleButton.getText().toString());
        csvStr.concat(",");
        csvStr.concat(heightEditText.getText().toString());
        csvStr.concat(",");
        csvStr.concat(weightEditText.getText().toString());
        csvStr.concat(",");
        csvStr.concat(ageEditText.getText().toString());
        return csvStr;
    }
    
    private String writeTXT() {
        String txtStr = unitToggleButton.getText().toString();
        txtStr.concat("\t");
        txtStr.concat(orientationToggleButton.getText().toString());
        txtStr.concat("\t");
        txtStr.concat(genderToggleButton.getText().toString());
        txtStr.concat("\t");
        txtStr.concat(heightEditText.getText().toString());
        txtStr.concat("\t");
        txtStr.concat(weightEditText.getText().toString());
        txtStr.concat("\t");
        txtStr.concat(ageEditText.getText().toString());
        return txtStr;
    }
}


