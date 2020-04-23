package com.example.sf;

import java.net.SocketException;
import java.util.StringTokenizer;

import cnetService.NetService;
import cnetService.NetService.LocalBinder;

import com.example.sf.R;
import com.example.sf.R.id;
import com.example.sf.R.layout;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Info_info_Activity extends Activity {

	/** Called when the activity is first created. */
	TextView nametx, text_dise;
	Intent intent;
	
	Button btn1, btn2;
	
	String no_Change_Name;	// 수정전 이름 db수정할시 원래 이름을 알아야 그이름을 찾아서 수정할 수 잇다.
	String change_Name;		//바뀐이름
	String rec_Disease;	//전화면으로부터 받은 질병 스트링 또는 체인지 화면가서 수정된 질병스트링으로 따로 저장하여 다시 전화면으로 돌려주자.
	
	private boolean mBound = false;
	NetService nService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_info_info);
	    
	    initList_info();
	    
	    // TODO Auto-generated method stub
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 

	 // TODO Auto-generated method stub 
			 if(requestCode == 5) 
			 { 
				 if(resultCode==100)
				 {
					 setTexts(data);
				 }
			 } 
		 super.onActivityResult(requestCode, resultCode, data); 
	}
	
	//초기 셋팅 이외의 수정하는 부분
	public void setTexts(Intent data)
	{
		//토큰 추가 하자--------------------------------------------------------------------------------------------
		 //바뀐이름 가져와 셋팅
		 change_Name =  data.getStringExtra("name_data1");
		 nametx.setText(change_Name);	
		 //바뀐질병 가져와 셋팅 
		 rec_Disease = data.getStringExtra("name_data2");
		 StringTokenizer st1 = new StringTokenizer(rec_Disease, ",");
		 text_dise.setText("");
		 while(st1.hasMoreTokens())
		    	text_dise.append(st1.nextToken()+"\n");
			
	}
	
	
	public  void btn_change(View v)
	{
		Log.d("onStart", "info_info_btn_change");
		Intent intent = new Intent(this, Change_info_info_Activity.class);
		intent.putExtra("name_data", nametx.getText().toString());
		
		intent.putExtra("my_disease", rec_Disease);
		startActivityForResult(intent, 5);
	}
	
	
	public void btn_ok(View v) throws SocketException
	{
		rec_Disease.replace("\n", ",");
		nService.startThread("92"+no_Change_Name+"&"+change_Name+"&"+rec_Disease); 	
		
		if( nService.getConnect() )
		{
			nService.getString();
			intent = new Intent();
			intent.putExtra("name_data1", nametx.getText().toString());
			setResult(101, intent);
		}
		else 
		{
			Intent intentHome = new Intent(this, Menu_Activity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intentHome);
			showToast();
		}
		finish();
	}
	
	private void initList_info()
	{
	    //아이디부여하는 함수
	    findViewsById();
	    text_dise.setText("");
	    mBound = false;
		no_Change_Name = "";
		change_Name = "";
		rec_Disease = "";
	    init_setTexts();
	    
	    btn1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	    btn2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	}
	//초기 텍스트뷰 셋팅
	public void init_setTexts()
	{
		intent = getIntent();
		no_Change_Name = "";
		change_Name = "";
		rec_Disease = "";
				
		no_Change_Name = intent.getStringExtra("name_data");
		change_Name = no_Change_Name;
		nametx.setText(no_Change_Name);		
		
		rec_Disease = intent.getStringExtra("my_disease");

		StringTokenizer st1 = new StringTokenizer(rec_Disease, ",");
	    while(st1.hasMoreTokens())
	    	text_dise.append(st1.nextToken()+"\n");
		
	}
	
	private void findViewsById()
	{
		 nametx = (TextView)findViewById(R.id.textView6);
		 text_dise = (TextView)findViewById(R.id.textView4); 
		 
		 btn1 = (Button)findViewById(R.id.button01);
		 btn2 = (Button)findViewById(R.id.datepick);
    }

	private ServiceConnection nConnection = new ServiceConnection()
	{
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			Log.v("onStart", "info_info__onServiceConnected");
			nService = ((LocalBinder) service).getService();
			mBound = true;
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
		Log.e("onStart", "info_info__onStart");
		Intent intent = new Intent(this, NetService.class);
		intent.putExtra("EventType", "0");
		bindService(intent, nConnection, Context.BIND_AUTO_CREATE);
		Log.e("onStart", "info_info__onStart2");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(mBound)
		{
			unbindService(nConnection);
			mBound = false;
		}
	}
	
	private void showToast()
	{
		Toast toast = Toast.makeText(this, "<error> 서버와 연결이 끊겼습니다.", Toast.LENGTH_SHORT); 
		toast.show();
		Intent intentHome = new Intent(this, Menu_Activity.class);
		intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intentHome);
	}
}
