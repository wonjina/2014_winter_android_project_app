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
	
	String no_Change_Name;	// ������ �̸� db�����ҽ� ���� �̸��� �˾ƾ� ���̸��� ã�Ƽ� ������ �� �մ�.
	String change_Name;		//�ٲ��̸�
	String rec_Disease;	//��ȭ�����κ��� ���� ���� ��Ʈ�� �Ǵ� ü���� ȭ�鰡�� ������ ������Ʈ������ ���� �����Ͽ� �ٽ� ��ȭ������ ��������.
	
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
	
	//�ʱ� ���� �̿��� �����ϴ� �κ�
	public void setTexts(Intent data)
	{
		//��ū �߰� ����--------------------------------------------------------------------------------------------
		 //�ٲ��̸� ������ ����
		 change_Name =  data.getStringExtra("name_data1");
		 nametx.setText(change_Name);	
		 //�ٲ����� ������ ���� 
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
	    //���̵�ο��ϴ� �Լ�
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
	//�ʱ� �ؽ�Ʈ�� ����
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
		Toast toast = Toast.makeText(this, "<error> ������ ������ ������ϴ�.", Toast.LENGTH_SHORT); 
		toast.show();
		Intent intentHome = new Intent(this, Menu_Activity.class);
		intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intentHome);
	}
}
