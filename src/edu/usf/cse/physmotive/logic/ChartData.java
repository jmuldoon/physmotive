package edu.usf.cse.physmotive.logic;

import android.database.Cursor;

public class ChartData
{
    private Number x[];
    private Number y[];
    
    public ChartData(Cursor cursor, int colX, int colY)
    {
        x = new Number[cursor.getCount()];
        y = new Number[cursor.getCount()];
        //String colXName = cursor.getColumnName(colX);
        //String colYName = cursor.getColumnName(colY);
        if(cursor.moveToFirst())
        {
            do
            {
                x[cursor.getPosition()] = cursor.getDouble(colX);
                y[cursor.getPosition()] = cursor.getDouble(colY);
            }while(cursor.moveToNext()); 
        }    
    }
    
    public Number[] getX()
    {
        return x;
    }
    
    public Number[] getY()
    {
        return y;
    }
    
}