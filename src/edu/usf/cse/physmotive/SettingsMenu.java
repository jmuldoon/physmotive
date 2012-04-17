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
    static final String USERID = "userId";
    static final String NAME = "name";
    static final String HEIGHT = "height";
    static final String WEIGHT = "weight";
    static final String AGE = "age";
    static final String GENDER = "gender";
    static final String ORI = "orientation";
    static final String UNITS = "units";

    protected Button update_btn;
    protected Button export_btn;
    protected EditText heightEditText;
    protected EditText weightEditText;
    protected EditText ageEditText;
    protected ToggleButton genderToggleButton;
    protected ToggleButton unitToggleButton;
    protected ToggleButton orientationToggleButton;
    protected RadioButton radioButtonCSV;
    protected RadioButton radioButtonTXT;

    // Cursors for exporting databases
    protected Cursor activityCur;
    protected Cursor diaryCur;
    protected Cursor locationCur;
    protected Cursor userCur;

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
        update_btn = (Button) findViewById(R.id.updateButton);
        export_btn = (Button) findViewById(R.id.exportButton);
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

        setOnClickListeners();

        // TODO: import??
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setupEditTexts();
        setupToggleButtons();
    }

    private void setupEditTexts()
    {
        dbmUser.open();
        userCur = dbmUser.get(userId);
        dbmUser.close();
        startManagingCursor(userCur);

        heightEditText.setText(userCur.getString(userCur.getColumnIndex(HEIGHT)));
        weightEditText.setText(userCur.getString(userCur.getColumnIndex(WEIGHT)));
        ageEditText.setText(userCur.getString(userCur.getColumnIndex(AGE)));
    }

    private void setupToggleButtons()
    {
        dbmUser.open();
        userCur = dbmUser.get(userId);
        dbmUser.close();
        startManagingCursor(userCur);

        genderToggleButton.setChecked(userCur.getInt(userCur.getColumnIndex(GENDER)) == 1);
        unitToggleButton.setChecked(userCur.getInt(userCur.getColumnIndex(UNITS)) == 1);
        orientationToggleButton.setChecked(userCur.getInt(userCur.getColumnIndex(ORI)) == 1);
    }

    private void setOnClickListeners()
    {
        update_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                onButtonClickUpdate(v);
            }
        });
        export_btn.setOnClickListener(new OnClickListener() {
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
        dbmUser.update(userId, Integer.valueOf(heightEditText.getText().toString()),
                Integer.valueOf(weightEditText.getText().toString()), Integer.valueOf(ageEditText.getText().toString()),
                gend, uni, ori, 0, userId);
        Toast.makeText(this, Integer.toString(userId), Toast.LENGTH_SHORT).show();
        dbmUser.close();
    }

    private void onButtonClickExport(View w)
    {
        String state = Environment.getExternalStorageState();
        String fileName;
        String fileExtension;

        if (radioButtonCSV.isChecked())
        {
            fileExtension = ".csv";
        } else
        {
            fileExtension = ".txt";
        }
        fileName = "test" + fileExtension;

        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);

            OutputStream outFile = null;
            try
            {
                outFile = new FileOutputStream(file);
                if (radioButtonCSV.isChecked())
                {
                    writeCSV(outFile);
                } else
                {
                    writeTXT(outFile);
                }
                outFile.close();
            } catch (FileNotFoundException ex)
            {
            } catch (IOException ex)
            {
            }
        }

    }

    private void writeCSV(OutputStream outFile)
    {
        // Write general settings
        String generalString = unitToggleButton.getText().toString();
        generalString = generalString.concat(",");
        generalString = generalString.concat(orientationToggleButton.getText().toString());
        generalString = generalString.concat(",");
        generalString = generalString.concat(genderToggleButton.getText().toString());
        generalString = generalString.concat(",");
        generalString = generalString.concat(heightEditText.getText().toString());
        generalString = generalString.concat(",");
        generalString = generalString.concat(weightEditText.getText().toString());
        generalString = generalString.concat(",");
        generalString = generalString.concat(ageEditText.getText().toString());
        generalString = generalString.concat("\n");
        try
        {
            outFile.write(generalString.getBytes());
        } catch (IOException ex)
        {
        }

        // Write Activity table
        dbmActivity.open();
        activityCur = dbmActivity.getForExport(userId);
        dbmActivity.close();
        String activityString = activityCur.getColumnName(0);
        for (int i = 1; i < activityCur.getColumnCount(); ++i) // Write column
                                                               // names
        {
            activityString = activityString.concat(",");
            activityString = activityString.concat(activityCur.getColumnName(i));
        }
        activityString = activityString.concat("\n");
        try
        {
            outFile.write(activityString.getBytes());
        } catch (IOException ex)
        {
        }
        if (activityCur.moveToFirst())
        {
            do
            {
                // have to reset string to first column, for needs to 1
                if (activityCur.isNull(0))
                {
                    activityString = "null";
                } else
                {
                    activityString = activityCur.getString(0);
                }
                for (int i = 1; i < activityCur.getColumnCount(); ++i)
                {
                    activityString = activityString.concat(",");
                    if (activityCur.isNull(i))
                    {
                        activityString = activityString.concat("null");
                    } else
                    {
                        activityString = activityString.concat(activityCur.getString(i));
                    }
                }
                activityString = activityString.concat("\n");
                try
                {
                    outFile.write(activityString.getBytes());
                } catch (IOException ex)
                {
                }
            } while (activityCur.moveToNext());
        }

        // Write Diary table
        dbmDiary.open();
        diaryCur = dbmDiary.getForExport(userId);
        dbmDiary.close();
        String diaryString = diaryCur.getColumnName(0);
        for (int i = 1; i < diaryCur.getColumnCount(); ++i) // Write column
                                                            // names
        {
            diaryString = diaryString.concat(",");
            diaryString = diaryString.concat(diaryCur.getColumnName(i));
        }
        diaryString = diaryString.concat("\n");
        try
        {
            outFile.write(diaryString.getBytes());
        } catch (IOException ex)
        {
        }
        if (diaryCur.moveToFirst())
        {
            do
            {
                // have to reset string to first column, for needs to 1
                if (diaryCur.isNull(0))
                {
                    diaryString = "null";
                } else
                {
                    diaryString = diaryCur.getString(0);
                }
                for (int i = 1; i < diaryCur.getColumnCount(); ++i)
                {
                    diaryString = diaryString.concat(",");
                    if (diaryCur.isNull(i))
                    {
                        diaryString = diaryString.concat("null");
                    } else
                    {
                        diaryString = diaryString.concat(diaryCur.getString(i));
                    }
                }
                diaryString = diaryString.concat("\n");
                try
                {
                    outFile.write(diaryString.getBytes());
                } catch (IOException ex)
                {
                }
            } while (diaryCur.moveToNext());
        }

        // Write Location table
        dbmLocation.open();
        locationCur = dbmLocation.getForExport(userId);
        dbmLocation.close();
        String locString = locationCur.getColumnName(0);
        for (int i = 1; i < locationCur.getColumnCount(); ++i) // Write column
                                                               // names
        {
            locString = locString.concat(",");
            locString = locString.concat(locationCur.getColumnName(i));
        }
        locString = locString.concat("\n");
        try
        {
            outFile.write(locString.getBytes());
        } catch (IOException ex)
        {
        }
        if (locationCur.moveToFirst())
        {
            do
            {
                // have to reset string to first column, for needs to 1
                if (locationCur.isNull(0))
                {
                    locString = "null";
                } else
                {
                    locString = locationCur.getString(0);
                }
                for (int i = 1; i < locationCur.getColumnCount(); ++i)
                {
                    locString = locString.concat(",");
                    if (locationCur.isNull(i))
                    {
                        locString = locString.concat("null");
                    } else
                    {
                        locString = locString.concat(locationCur.getString(i));
                    }
                }
                locString = locString.concat("\n");
                try
                {
                    outFile.write(locString.getBytes());
                } catch (IOException ex)
                {
                }
            } while (locationCur.moveToNext());
        }

        // Write User table
        dbmUser.open();
        userCur = dbmUser.getForExport(userId);
        dbmUser.close();
        String userString = userCur.getColumnName(0);
        for (int i = 1; i < userCur.getColumnCount(); ++i) // Write column names
        {
            userString = userString.concat(",");
            userString = userString.concat(userCur.getColumnName(i));
        }
        userString = userString.concat("\n");
        try
        {
            outFile.write(userString.getBytes());
        } catch (IOException ex)
        {
        }
        if (userCur.moveToFirst())
        {
            do
            {
                // have to reset string to first column, for needs to 1
                if (userCur.isNull(0))
                {
                    userString = "null";
                } else
                {
                    userString = userCur.getString(0);
                }
                for (int i = 1; i < userCur.getColumnCount(); ++i)
                {
                    userString = userString.concat(",");
                    if (userCur.isNull(i))
                    {
                        userString = userString.concat("null");
                    } else
                    {
                        userString = userString.concat(userCur.getString(i));
                    }
                }
                userString = userString.concat("\n");
                try
                {
                    outFile.write(userString.getBytes());
                } catch (IOException ex)
                {
                }
            } while (userCur.moveToNext());
        }
    }

    private void writeTXT(OutputStream outFile)
    {
        // Write general settings
        String genString = unitToggleButton.getText().toString();
        genString = genString.concat("\t");
        genString = genString.concat(orientationToggleButton.getText().toString());
        genString = genString.concat("\t");
        genString = genString.concat(genderToggleButton.getText().toString());
        genString = genString.concat("\t");
        genString = genString.concat(heightEditText.getText().toString());
        genString = genString.concat("\t");
        genString = genString.concat(weightEditText.getText().toString());
        genString = genString.concat("\t");
        genString = genString.concat(ageEditText.getText().toString());
        genString = genString.concat("\n");
        try
        {
            outFile.write(genString.getBytes());
        } catch (IOException ex)
        {
        }

        // Write Activity table
        dbmActivity.open();
        activityCur = dbmActivity.getForExport(userId);
        dbmActivity.close();
        String activityString = activityCur.getColumnName(0);
        for (int i = 1; i < activityCur.getColumnCount(); ++i) // Write column
                                                               // names
        {
            activityString = activityString.concat("\t");
            activityString = activityString.concat(activityCur.getColumnName(i));
        }
        activityString = activityString.concat("\n");
        try
        {
            outFile.write(activityString.getBytes());
        } catch (IOException ex)
        {
        }
        if (activityCur.moveToFirst())
        {
            do
            {
                // have to reset string to first column, for needs to 1
                if (activityCur.isNull(0))
                {
                    activityString = "null";
                } else
                {
                    activityString = activityCur.getString(0);
                }
                for (int i = 1; i < activityCur.getColumnCount(); ++i)
                {
                    activityString = activityString.concat(",");
                    if (activityCur.isNull(i))
                    {
                        activityString = activityString.concat("null");
                    } else
                    {
                        activityString = activityString.concat(activityCur.getString(i));
                    }
                }
                activityString = activityString.concat("\n");
                try
                {
                    outFile.write(activityString.getBytes());
                } catch (IOException ex)
                {
                }
            } while (activityCur.moveToNext());
        }

        // Write Diary table
        dbmDiary.open();
        diaryCur = dbmDiary.getForExport(userId);
        dbmDiary.close();
        String diaryString = diaryCur.getColumnName(0);
        for (int i = 1; i < diaryCur.getColumnCount(); ++i) // Write column
                                                            // names
        {
            diaryString = diaryString.concat("\t");
            diaryString = diaryString.concat(diaryCur.getColumnName(i));
        }
        diaryString = diaryString.concat("\n");
        try
        {
            outFile.write(diaryString.getBytes());
        } catch (IOException ex)
        {
        }
        if (diaryCur.moveToFirst())
        {
            do
            {
                // have to reset string to first column, for needs to 1
                if (diaryCur.isNull(0))
                {
                    diaryString = "null";
                } else
                {
                    diaryString = diaryCur.getString(0);
                }
                for (int i = 1; i < diaryCur.getColumnCount(); ++i)
                {
                    diaryString = diaryString.concat(",");
                    if (diaryCur.isNull(i))
                    {
                        diaryString = diaryString.concat("null");
                    } else
                    {
                        diaryString = diaryString.concat(diaryCur.getString(i));
                    }
                }
                diaryString = diaryString.concat("\n");
                try
                {
                    outFile.write(diaryString.getBytes());
                } catch (IOException ex)
                {
                }
            } while (diaryCur.moveToNext());
        }

        // Write Location table
        dbmLocation.open();
        locationCur = dbmLocation.getForExport(userId);
        dbmLocation.close();
        String locString = locationCur.getColumnName(0);
        for (int i = 1; i < locationCur.getColumnCount(); ++i) // Write column
                                                               // names
        {
            locString = locString.concat("\t");
            locString = locString.concat(locationCur.getColumnName(i));
        }
        locString = locString.concat("\n");
        try
        {
            outFile.write(locString.getBytes());
        } catch (IOException ex)
        {
        }
        if (locationCur.moveToFirst())
        {
            do
            {
                // have to reset string to first column, for needs to 1
                if (locationCur.isNull(0))
                {
                    locString = "null";
                } else
                {
                    locString = locationCur.getString(0);
                }
                for (int i = 1; i < locationCur.getColumnCount(); ++i)
                {
                    locString = locString.concat(",");
                    if (locationCur.isNull(i))
                    {
                        locString = locString.concat("null");
                    } else
                    {
                        locString = locString.concat(locationCur.getString(i));
                    }
                }
                locString = locString.concat("\n");
                try
                {
                    outFile.write(locString.getBytes());
                } catch (IOException ex)
                {
                }
            } while (locationCur.moveToNext());
        }

        // Write User table
        dbmUser.open();
        userCur = dbmUser.getForExport(userId);
        dbmUser.close();
        String userString = userCur.getColumnName(0);
        for (int i = 1; i < userCur.getColumnCount(); ++i) // Write column names
        {
            userString = userString.concat("\t");
            userString = userString.concat(userCur.getColumnName(i));
        }
        userString = userString.concat("\n");
        try
        {
            outFile.write(userString.getBytes());
        } catch (IOException ex)
        {
        }
        if (userCur.moveToFirst())
        {
            do
            {
                // have to reset string to first column, for needs to 1
                if (userCur.isNull(0))
                {
                    userString = "null";
                } else
                {
                    userString = userCur.getString(0);
                }
                for (int i = 1; i < userCur.getColumnCount(); ++i)
                {
                    userString = userString.concat(",");
                    if (userCur.isNull(i))
                    {
                        userString = userString.concat("null");
                    } else
                    {
                        userString = userString.concat(userCur.getString(i));
                    }
                }
                userString = userString.concat("\n");
                try
                {
                    outFile.write(userString.getBytes());
                } catch (IOException ex)
                {
                }
            } while (userCur.moveToNext());
        }
    }
}
