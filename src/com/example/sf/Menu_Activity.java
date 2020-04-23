package com.example.sf;

import java.net.SocketException;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cnetService.NetService;
import cnetService.NetService.LocalBinder;

public class Menu_Activity extends Activity {
	
	Button btn_info, btn_recipe, btn_fridge;
	Button btn_barcode_insert, btn_barcode_del, refresh_server;
	Button plus, miners;
	TextView text;
	
	private boolean mBound = false;
	NetService nService;

	//바코드 화면에서 리턴된 값 저장
	String contents;
	
	int c= 5;
	
	private static final int DIALOG_1 = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_menu);
	    
	    initList_menu();
	    
	}
	public void btn_C_add(View v)
	{
		nService.startThread("81");
		if( nService.getConnect() )
		{
			nService.getString();
		}
		else	showToast();
		c++;
		text.setText(""+ c );
	}
	public void btn_C_del(View v)
	{
		nService.startThread("82");
		if( nService.getConnect() )
		{	
			nService.getString();
		}
		else	showToast();
		c--;
		text.setText(""+ c );
	}
	public void btn_connect(View v)
	{
		nService.re_Connect();
	}
	public void btn_Bacord_Add(View v)
	{
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		startActivityForResult(intent, 5);
	}
	public void btn_Bacord_Del(View v)
	{
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		startActivityForResult(intent, 6);
	}
	
    
	//바코드 화면에서 돌아올때
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{		
		//바코드 추가
		if (requestCode == 5)
		{
			if (resultCode == RESULT_OK)
			{
				showDialog(DIALOG_1);
				
				StringTokenizer st = new StringTokenizer(data.getStringExtra("SCAN_RESULT"),".");
				while(st.hasMoreTokens())	contents = ","+st.nextToken();
			}
			 
		}
		//바코드 삭제
		else if( requestCode == 6 )
		{
			if (resultCode == RESULT_OK)
			{
				contents = data.getStringExtra("SCAN_RESULT");
				StringTokenizer st1 = new StringTokenizer(contents, ".");			    	
				nService.startThread("74,"+st1.nextToken());
				nService.getString();
				Toast toast = Toast.makeText(this, contents+"  75, ", Toast.LENGTH_SHORT); 
				toast.show();
			}	 
		}
	}
	
	protected Dialog onCreateDialog(int id)
	{
		return alertDial();
	}
	
	private Dialog alertDial()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Choose where to save.").setCancelable(false)
		.setPositiveButton("fridge", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				nService.startThread("74"+contents+",fridge");
				nService.getString();
				Toast toast = Toast.makeText(Menu_Activity.this, contents+" , "+" fridge", Toast.LENGTH_SHORT); 
				toast.show();
				dialog.cancel();
			}
		})
		.setNegativeButton("Freezer", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				nService.startThread("74"+contents+",Freezer");
				nService.getString();
				Toast toast = Toast.makeText(Menu_Activity.this, contents+" , "+" Freezer", Toast.LENGTH_SHORT); 
				toast.show();
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		return alert;
	}
	
	//내정보 버튼 누를때
	public void btn_information(View v)
	{
		nService.startThread("31");
		Intent int_info = new  Intent(this, List_info_Activity.class);
	
		if( nService.getConnect() )
		{
			int_info.putExtra("list_name", nService.getString());
			startActivity(int_info);
			Log.e("onStart", "menu_btn_information");
		}
		else	showToast();
	}
	
	//레시피 버튼 누를때
	public void btn_recipe(View v) 
	{
		nService.startThread("31");
		Intent int_recipe = new  Intent(this, Recipe_Activity.class);
	
		if( nService.getConnect() )
		{
			int_recipe.putExtra("my_info_name", nService.getString());
		}
		else	showToast();
		
		nService.startThread("51");
		
		if( nService.getConnect() )
		{
			int_recipe.putExtra("fridge_name", nService.getString());
			startActivity(int_recipe);
		}
		else	showToast();
	}

	//냉장고 버튼 누를때
	public void btn_fridge(View v) throws SocketException
	{
		nService.startThread("52");		
		Intent int_fridge = new  Intent(this, Fridge_Activity.class);
		
		if( nService.getConnect() )
		{
			int_fridge.putExtra("fridge_detail", nService.getString());
			startActivity(int_fridge);
			Log.e("onStart", "menu_btn_fridge");
		}
		else	showToast();
	}
	

	private ServiceConnection nConnection = new ServiceConnection()
	{
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			Log.e("onStart", "menu_onServiceConnected");
			nService = ((LocalBinder) service).getService();
			mBound = true;
			Log.e("onStart", "menu_onServiceConnected2");
				
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.i("onStart", "menu_onServiceDisconnected");
			mBound = false;
			onStop() ;
			onDestroy();
		}
	};

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.e("onStart", "menu_onStart");
		Intent intent = new Intent(Menu_Activity.this, NetService.class);
		intent.putExtra("EventType", "0");
		bindService(intent, nConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("onStart", "menu_onDestroy");
		if(mBound)
		{
			Log.e("onStart", "menu_onStop");
			unbindService(nConnection);
			mBound = false;
		}
		stopService(new Intent(this, NetService.class));
	}
	private void initList_menu()
	{
		finViewById();
		text.setText(""+c);
		mBound = false;
		btn_info.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anothershabby_trial.ttf"));
		btn_recipe.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anothershabby_trial.ttf"));
		btn_fridge.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anothershabby_trial.ttf"));
		
		btn_barcode_insert.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anothershabby_trial.ttf"));
		btn_barcode_del.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anothershabby_trial.ttf"));
		refresh_server.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anothershabby_trial.ttf"));
		
		plus.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anothershabby_trial.ttf"));
		miners.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anothershabby_trial.ttf"));
	}
	
	private void finViewById()
	{
	    btn_info = (Button)findViewById(R.id.button_home);
	    btn_recipe = (Button)findViewById(R.id.Button2);
	    btn_fridge = (Button)findViewById(R.id.Button3);
	    
	    btn_barcode_insert = (Button)findViewById(R.id.button1);
	    btn_barcode_del = (Button)findViewById(R.id.button5);
	    refresh_server = (Button)findViewById(R.id.button4);
	    text = (TextView)findViewById(R.id.textView1);
	    plus = (Button)findViewById(R.id.button6);
	    miners = (Button)findViewById(R.id.button7);
	}
	private void showToast()
	{
		Toast toast = Toast.makeText(this, "<error> 서버와 연결이 끊겼습니다.", Toast.LENGTH_SHORT); 
		toast.show(); 
	}
	
}
