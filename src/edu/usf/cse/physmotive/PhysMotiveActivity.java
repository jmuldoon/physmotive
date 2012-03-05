package edu.usf.cse.physmotive;

import edu.usf.cse.physmotive.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PhysMotiveActivity extends Activity {
	protected Button newActivityButton;
	protected Button viewActivityButton;
	protected Button mapButton;
	protected Button diaryButton;
	protected Button statisticsButton;
	protected Button settingsButton;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Connect interface elements to properties
        newActivityButton =(Button)findViewById(R.id.newActivityButton);
        viewActivityButton =(Button)findViewById(R.id.viewActivityButton);
        mapButton =(Button)findViewById(R.id.mapButton);
        diaryButton =(Button)findViewById(R.id.diaryButton);
        statisticsButton =(Button)findViewById(R.id.statisticsButton);
        settingsButton =(Button)findViewById(R.id.settingsButton);
        
        setOnClickListeners();
    }
    
    private void setOnClickListeners()
    {
    	newActivityButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onButtonClickNewActivity(v);
			}
		});
    	viewActivityButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onButtonClickViewActivity(v);
			}
		});
    	mapButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onButtonClickMap(v);
			}
		});
    	diaryButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onButtonClickDiary(v);
			}
		});
    	statisticsButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onButtonClickStatistics(v);
			}
		});
    	settingsButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onButtonClickSettings(v);
			}
		});
    }
        
    private void onButtonClickNewActivity(View w)
    {
    	Log.d("onButtonClick", newActivityButton.getText()+": works");
    }
    private void onButtonClickViewActivity(View w)
    {
    	Log.d("onButtonClick", viewActivityButton.getText()+": works");
    }
    private void onButtonClickMap(View w)
    {
    	Log.d("onButtonClick", mapButton.getText()+": works");
    }
    private void onButtonClickDiary(View w)
    {
    	Log.d("onButtonClick", diaryButton.getText()+": works");
    }
    private void onButtonClickStatistics(View w)
    {
    	Log.d("onButtonClick", statisticsButton.getText()+": works");
    }
    private void onButtonClickSettings(View w)
    {
    	Log.d("onButtonClick", settingsButton.getText()+": works");
    }
}