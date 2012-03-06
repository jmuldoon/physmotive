package edu.usf.cse.physmotive;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
	protected Button newUserButton;
	
	// Called when the activity is first created.
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
        newUserButton =(Button)findViewById(R.id.newUserButton);
        
        setOnClickListeners();
    }
    
    @Override
    protected Dialog onCreateDialog(int i, Bundle args){
    	LayoutInflater factory = LayoutInflater.from(this);
    	final View textEntryView = factory.inflate(R.layout.new_user, null);

    	switch (i) {
    		case 0:
    		default: break;
    	}
    	return null;
    }
    
    private void invokeActivityMenu(View arg0){
    	Intent myIntent = new Intent(arg0.getContext(), ActivityMenu.class);
        startActivityForResult(myIntent, 0);
	}
    private void invokeActivityView(View arg0){
    	Intent myIntent = new Intent(arg0.getContext(), ActivityList.class);
        startActivityForResult(myIntent, 0);
	}
    private void invokeDiaryList(View arg0){
    	Intent myIntent = new Intent(arg0.getContext(), DiaryList.class);
        startActivityForResult(myIntent, 0);
	}
    private void invokeMapView(View arg0){
    	Intent myIntent = new Intent(arg0.getContext(), MapView.class);
        startActivityForResult(myIntent, 0);
	}
    private void invokeStatisticsMenu(View arg0){
    	Intent myIntent = new Intent(arg0.getContext(), StatisticsMenu.class);
        startActivityForResult(myIntent, 0);
	}
    private void invokeSettingsMenu(View arg0){
    	Intent myIntent = new Intent(arg0.getContext(), SettingsMenu.class);
        startActivityForResult(myIntent, 0);
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
    	newUserButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onButtonClickNewUser(v);
			}
		});
    }
        
    private void onButtonClickNewActivity(View w)
    {
    	invokeActivityMenu(w);
    }
    private void onButtonClickViewActivity(View w)
    {
    	invokeActivityView(w);
    }
    private void onButtonClickMap(View w)
    {
    	invokeMapView(w);
    }
    private void onButtonClickDiary(View w)
    {
    	invokeDiaryList(w);
    }
    private void onButtonClickStatistics(View w)
    {
    	invokeStatisticsMenu(w);
    }
    private void onButtonClickSettings(View w)
    {
    	invokeSettingsMenu(w);
    }
    private void onButtonClickNewUser(View w){
    	showDialog(0, null);
    }
}