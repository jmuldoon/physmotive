package edu.usf.cse.physmotive;

import android.app.Activity;
import android.os.Bundle;

public class StatisticsMenu extends Activity
{
    public static final String USERID = "userId";

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
    }
}
