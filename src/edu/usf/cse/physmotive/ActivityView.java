package edu.usf.cse.physmotive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityView extends Activity
{
	protected Button statisticsButton;
	protected Button diaryButton;
	
	// Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        
        // Connect interface elements to properties
        diaryButton =(Button)findViewById(R.id.toDiaryButton);
        statisticsButton =(Button)findViewById(R.id.toStatisticsButton);
        
        setOnClickListeners();
    }
    
    private void setOnClickListeners()
    {
    	statisticsButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onButtonClickStatistics(v);
			}
		});
    	diaryButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onButtonClickDiary(v);
			}
		});
    }
    
    private void onButtonClickStatistics(View w)
    {
    	Intent myIntent = new Intent(w.getContext(), StatisticsMenu.class);
        startActivityForResult(myIntent, 0);	
    }
    
    private void onButtonClickDiary(View w)
    {
    	Intent myIntent = new Intent(w.getContext(), DiaryView.class);
    	startActivityForResult(myIntent, 0);
    }
}
