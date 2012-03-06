package edu.usf.cse.physmotive;

import android.app.Activity;
import android.os.Bundle;

public class ActiveActivity extends MapActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
