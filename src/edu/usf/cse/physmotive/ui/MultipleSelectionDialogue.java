package edu.usf.cse.physmotive.ui;

import edu.usf.cse.physmotive.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MultipleSelectionDialogue extends Activity {
	protected CharSequence[] _options = { "Race 1", "Race 2", "Race 3", "Race 4" };
	protected boolean[] _selections =  new boolean[ _options.length ];
	
	protected Button _optionsButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.diary_view);
        
        _optionsButton =(Button)findViewById(R.id.bindRacesButton);
        _optionsButton.setOnClickListener(new ButtonClickHandler());
    }
    
    public class ButtonClickHandler implements View.OnClickListener {
		public void onClick( View view ) {
			showDialog( 0 );
		}
	}
    
	@Override
	protected Dialog onCreateDialog( int id ) 
	{
		return 
		new AlertDialog.Builder( this )
        	.setTitle( "Races" )
        	.setMultiChoiceItems( _options, _selections, new DialogSelectionClickHandler() )
        	.setPositiveButton( "OK", new DialogButtonClickHandler() )
        	.create();
	}
	
	
	public class DialogSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener
	{
		public void onClick( DialogInterface dialog, int clicked, boolean selected )
		{
			Log.i( "ME", _options[ clicked ] + " selected: " + selected );
		}
	}
	

	public class DialogButtonClickHandler implements DialogInterface.OnClickListener
	{
		public void onClick( DialogInterface dialog, int clicked )
		{
			switch( clicked )
			{
				case DialogInterface.BUTTON_POSITIVE:
					printSelectedRaces();
					break;
			}
		}
	}
	
	protected void printSelectedRaces(){
		for( int i = 0; i < _options.length; i++ ){
			Log.i( "ME", _options[ i ] + " selected: " + _selections[i] );
		}
	}
}
