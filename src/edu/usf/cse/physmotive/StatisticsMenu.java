package edu.usf.cse.physmotive;

import edu.usf.cse.physmotive.ui.ImageAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.Toast;

public class StatisticsMenu extends Activity
{
    public static final String USERID = "userId";
    protected Gallery gallery;
    
    private int userId;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic_menu);

        // pulling in bundle information
        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);
        
        // Setting up gallery information.
        gallery = (Gallery) findViewById(R.id.activityGallery);
        gallery.setAdapter(new ImageAdapter(this));

        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Toast.makeText(StatisticsMenu.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
