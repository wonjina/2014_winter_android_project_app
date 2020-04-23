package com.example.sf;


import com.example.sf.R;
import com.example.sf.R.id;
import com.example.sf.R.layout;
import com.example.sf.R.menu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Intro_Activity extends Activity {

	TextView tx1;
	TextView tx2;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro_);
       	  
        customdelay();
        initList_intro();
        
       
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.intro_, menu);
        return true;
    }
    private void customdelay()
    {
        Handler handler = new Handler(){
        	public void handleMessage(Message msg)
        	{
        		startActivity(new Intent(Intro_Activity.this, Menu_Activity.class));
        		finish();
        	}
        };
        handler.sendEmptyMessageDelayed(0, 2000);
    }
	private void initList_intro()
	{
	    //아이디부여하는 함수
	    findViewsById();
	    tx1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anothershabby_trial.ttf"));
        tx2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anothershabby_trial.ttf"));
 
	}
	
	
	private void findViewsById()
	{
		tx1 = (TextView)findViewById(R.id.tv3);
		tx2 = (TextView)findViewById(R.id.textView2);
    }

    
    
}
