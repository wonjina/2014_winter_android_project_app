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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Recipe_Activity extends Activity  {
	 
    LinearLayout line1, line2;
	String str_checked;
	
	TextView tv2, tv3;
	
	Button btn;
	
	private boolean mBound;
	NetService nService;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_recipe);
	    
	    initList_recipe();
	    
	    // TODO Auto-generated method stub
	}

	//채크된것 스트링만드는함수를 호출하는 함수
	private void click_img_checked()
	{
		str_checked = "";
		str_checked += click_check(line1);
		str_checked += "&";
		str_checked += click_check(line2);
	}
	
	//체크된것들을 스트링으로 만들어주는 함수
	private String click_check(LinearLayout line)
	{
		String check_Str="";
		for(int index = 0 ; index < line.getChildCount() ; index++)
		  {
			   CheckBox chile_View = (CheckBox) line.getChildAt(index);
		    //체크되있다면 마지막 뷰의 텍스트를 가져와 셋 해주자
				if( chile_View.isChecked() ) 
				{
					
					check_Str +=",";
					check_Str += chile_View.getText().toString();
					chile_View.setChecked(false);
				}
		  }
		return check_Str;
	}
	
	//찾기 버튼 눌렀을ㄸ
	public void btn_search(View v)
	{
		click_img_checked();
		nService.startThread("61"+str_checked);
		if( nService.getConnect() )
		{
			Intent int_search = new Intent(this, Search_recipe_Activity.class);
			int_search.putExtra("recipe_name", nService.getString());
			startActivity(int_search);
		}
		else
		{
			showToast();
			finish();
		}
	}
	
	
	//내정보체크박스 생성
	public void cb_creat_men(String value)
	{
		StringTokenizer st1 = new StringTokenizer(value, ",");
	    while(st1.hasMoreTokens())
	    {
			CheckBox my_checkb = new CheckBox(this);
			my_checkb.setText(st1.nextToken());
			line1.addView(my_checkb);
	    }
	}
	
	//재료 체크박스 생성
	public void cb_creat_thing(String value)
	{
		StringTokenizer st1 = new StringTokenizer(value, ",");
	    while(st1.hasMoreTokens())
	    {
			CheckBox thing_checkb = new CheckBox(this);
			thing_checkb.setText(st1.nextToken());
			line2.addView(thing_checkb);
	    }
	}
	
	//뒤로가기 버튼
	public void btn_home(View v)
	{
		finish();
	}
		
	private void initList_recipe()
	{
		findViewById();
		str_checked= "";
		mBound = false;

		Intent recipe = getIntent();
		cb_creat_men(recipe.getStringExtra("my_info_name"));
		cb_creat_thing(recipe.getStringExtra("fridge_name"));
		
		tv2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
		tv3.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
		
		btn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	}
	private void findViewById()
	{		
		line1 = (LinearLayout)findViewById(R.id.LinearLayout_recipe);
		line2 = (LinearLayout)findViewById(R.id.LinearLayout_recipe2);
		
		tv2 = (TextView)findViewById(R.id.textView2);
		tv3 = (TextView)findViewById(R.id.tv3);
		
		btn = (Button)findViewById(R.id.button_Search);
	}

	private ServiceConnection nConnection = new ServiceConnection()
	{
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			Log.e("onStart", "recipe_onServiceConnected");
			nService = ((LocalBinder) service).getService();
			mBound = true;
			Log.e("onStart", "recipe_onServiceConnected2");
				
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
		Log.e("onStart", "recipe_onStart");
		Intent intent = new Intent(this, NetService.class);
		intent.putExtra("EventType", "0");
		bindService(intent, nConnection, Context.BIND_AUTO_CREATE);
		Log.e("onStart", "recipe_onStart2");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(mBound)
		{
			Log.e("onStart", "recipe_onStop");
			unbindService(nConnection);
			mBound = false;
		}
	}
	private void showToast()
	{
		Toast toast = Toast.makeText(this, "<error> 서버와 연결이 끊겼습니다.", Toast.LENGTH_SHORT); 
		toast.show(); 
	}
}
