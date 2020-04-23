package com.example.sf;

import java.util.StringTokenizer;

import cnetService.NetService;
import cnetService.NetService.LocalBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Info_search_recipe_Activity extends Activity {
	
	TextView tv1;
	TextView tv2, tv4, tv6, tv01;	//제목
	TextView tv3, tv5, tv7, tv02;	//내용
	Button btn;
	
	private boolean mBound;
	NetService nService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_recipe_search_info);
	    
	    initList_info_search_recipe();
	   
	    // TODO Auto-generated method stub
	}
	public void btn_ok(View v)
	{
		finish();
	}
	private void initList_info_search_recipe()
	{
	    findViewsById();
	    mBound = false;
	    tv7.setText("");
	    init_Settexts();
	}
	private void init_Settexts()
	{
	    Intent info_search_recipe = getIntent();
	    StringTokenizer st1 = new StringTokenizer(info_search_recipe.getStringExtra("recipe_detail"), ",");
	    
	    tv1.setText(st1.nextToken());
	    tv3.setText(st1.nextToken());
	    tv5.setText(st1.nextToken());
	    tv02.setText(st1.nextToken());
	    while(st1.hasMoreTokens())
	    	tv7.append(st1.nextToken());	
	    
	    tv2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	    tv4.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	    tv6.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	    tv01.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	    btn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	}
	private void findViewsById() 
	{		
		tv1 = (TextView)findViewById(R.id.tv3); 
		tv2 = (TextView)findViewById(R.id.textView2); 
		tv4 = (TextView)findViewById(R.id.textView4);
		tv6 = (TextView)findViewById(R.id.textView6);
		tv01 = (TextView)findViewById(R.id.TextView01);

		tv3 = (TextView)findViewById(R.id.textView3); 
		tv5 = (TextView)findViewById(R.id.textView5);
		tv7 = (TextView)findViewById(R.id.textView7);
		tv02 = (TextView)findViewById(R.id.TextView02);
		
		btn = (Button)findViewById(R.id.button1);
    }
	
	private ServiceConnection nConnection = new ServiceConnection()
	{
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			Log.e("onStart", "info_search_recipe_onServiceConnected");
			nService = ((LocalBinder) service).getService();
			mBound = true;
			Log.e("onStart", "info_search_recipe_onServiceConnected2");
				
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBound = false;
			onStop() ;
			onDestroy();
		}
	};

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.e("onStart", "info_search_recipe_onStart");
		Intent intent = new Intent(this, NetService.class);
		intent.putExtra("EventType", "0");
		bindService(intent, nConnection, Context.BIND_AUTO_CREATE);
		Log.e("onStart", "info_search_recipe_onStart2");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(mBound)
		{
			Log.e("onStart", "info_search_recipe_onStop");
			unbindService(nConnection);
			mBound = false;
		}
	}


}
