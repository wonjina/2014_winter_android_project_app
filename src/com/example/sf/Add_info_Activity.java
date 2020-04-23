package com.example.sf;


import java.net.SocketException;
import java.util.StringTokenizer;

import cnetService.NetService;
import cnetService.NetService.LocalBinder;

import com.example.sf.R;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Add_info_Activity extends Activity {
	
	EditText edit1;
	Button btn, btn_del, btn2;
	LinearLayout line;									//동적으로 뷰 생성해서 추가할 곳임
	LinearLayout line_serv, serv_add_layout;	//첫번째것은 메인에 붙일 리니어레이아웃, 두번째는 첫번째에 붙이는 체크박스
	ImageButton img_btn_add, img_btn_del;
	
	LinearLayout.LayoutParams layparam;
	int check_Count;									//체크박스 갯수 현황 
	
	private boolean mBound = false;
	NetService nService;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_info_add);
	
	    // TODO Auto-generated method stub
	    
	    initList_info();
	    
	}
	
	//질병추가 - 다이얼로그
	public void click_img_addBtn(View v)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(5);
		input.setFilters(FilterArray);
		alert.setTitle("Add Disease").setMessage("Disease Name Insert").setCancelable(false).setView(input)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String value = input.getText().toString();
				if( !value.equals(""))	cb_creat(value);
				
			}})
	
		.setNegativeButton("Cancel",	new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {	}	})
		.show();
		
	}
	
	//체크 박스 생성
	public void cb_creat(String value)
	{
		CheckBox checkB = new CheckBox(this);
		checkB.setText(""+value);
		
		if( check_Count == 0 || check_Count%3 ==0)
		{
			line_serv = (LinearLayout) View.inflate(Add_info_Activity.this, R.layout.dynamic_creat_view, null);
			line_serv.addView(checkB);
			line.addView(line_serv, layparam);
		}
		else ((LinearLayout) line.getChildAt(line.getChildCount()-1)).addView(checkB);
		
		check_Count++;
	}
	
	//체크박스 삭제
	public void click_img_delBtn(View v)
	{
		//현재 뷰의 위치
		  int num = (line.getChildCount()-1)*3 + ( (LinearLayout) line.getChildAt(line.getChildCount()-1) ).getChildCount();		
		  int abc , def;
			
		  //3개의 채크박스를 가질수 있는 레이아웃의 갯수만큼 반복
		  for(int index = line.getChildCount()-1 ; index >= 0 ; index--)
		  {
		   //그 레이아웃이 가진 뷰의 갯수만큼 반복
			   for(int index2 = ((LinearLayout) line.getChildAt(index)).getChildCount()-1; index2 >= 0 ; index2 --)
			   {
				   CheckBox chile_View = (CheckBox) ((LinearLayout) line.getChildAt(index)).getChildAt(index2);
			    //체크되있다면 마지막 뷰의 텍스트를 가져와 셋 해주자
					if( chile_View.isChecked() ) 
					{
						//마지막놈의 인덱스 정보
						abc = line.getChildCount()-1;
						if( check_Count%3-1 <0 ) def=2;
						else def=check_Count%3-1;
						//마지막놈하고 현재 뷰하고 다른놈이라면 즉 현재 뷰가 체크되어 삭제해야되는데 뒤에 다른놈이 잇다면
						if( num != (check_Count) )
						{
							chile_View.setText(  ( (CheckBox) ((LinearLayout) line.getChildAt(abc)).getChildAt(def) ).getText().toString()  );
						}
						if( ((LinearLayout) line.getChildAt(abc)).getChildCount() > 1 ) ((LinearLayout) line.getChildAt(abc)).removeViewAt(def);
						else line.removeViewAt(abc);
						check_Count--;
						chile_View.setChecked(false);
					}
					//num을 다시 초기화 1단계
					num --;
			   }
			   //num을 다시 초기화 2단계
			   num --;
		  }
	
	} 
	public void btn_ok(View v) throws SocketException
	{	
		//이름을 입력해야 됨
		if( !edit1.getText().toString().equals(""))
		{
			nService.startThread("91"+edit1.getText().toString()+"&"+click_img_checked(""));
			if( nService.getConnect() )
			{
				Intent int_back = new Intent();	
				int_back.putExtra("name_data", edit1.getText().toString());
				nService.getString();
				setResult(100, int_back);		
			}
			else 
			{
				showToast();
			}
			finish();
		}
		else 
		{
			Toast toast = Toast.makeText(this, "이름을 입력하세요.", Toast.LENGTH_SHORT); 
			toast.show(); 
		}
	}
	
	//채크된 것만 str에 붙이는 함수
	public String click_img_checked(String str_checked)
	{
		  //3개의 채크박스를 가질수 있는 레이아웃의 갯수만큼 반복
		  for(int index = line.getChildCount()-1 ; index >= 0 ; index--)
		  {
		   //그 레이아웃이 가진 뷰의 갯수만큼 반복
		   for(int index2 = ((LinearLayout) line.getChildAt(index)).getChildCount()-1; index2 >= 0 ; index2 --)
		   {
			   CheckBox chile_View = (CheckBox) ((LinearLayout) line.getChildAt(index)).getChildAt(index2);
		    //체크되있다면 마지막 뷰의 텍스트를 가져와 셋 해주자
					if( chile_View.isChecked() ) 
					{
						str_checked += ",";
						str_checked += chile_View.getText().toString();
					}
			   }
		  }
		  return str_checked;
	}
	
	private ServiceConnection nConnection = new ServiceConnection()
	{
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			Log.v("onStart", "add_info_onServiceConnected");
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
		Intent intent = new Intent(this, NetService.class);
		intent.putExtra("EventType", "0");
		bindService(intent, nConnection, Context.BIND_AUTO_CREATE);
		Log.e("onStart", "add_info_onStart");
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
	
	
	public void btn_cancel(View v)
	{
		finish();
	}
	private void initList_info()
	{
	    //아이디부여하는 함수
	    findViewsById();
	    check_Count = 0;
	    mBound = false;
		layparam = new LinearLayout.LayoutParams(layparam.WRAP_CONTENT, layparam.WRAP_CONTENT);
		layparam.gravity = Gravity.CENTER;
		init_Settexts();
		
		btn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
		btn_del.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	}
	private void init_Settexts()
	{
		Intent add_Info = getIntent();
		StringTokenizer st1 = new StringTokenizer(add_Info.getStringExtra("disease_name"), ",");
		while(st1.hasMoreTokens())
			cb_creat(st1.nextToken());
	}
	private void findViewsById()
	{
		btn = (Button)findViewById(R.id.button01);
		btn_del = (Button)findViewById(R.id.button2);
		line = (LinearLayout)findViewById(R.id.dymanicArea);
		img_btn_add = (ImageButton)findViewById(R.id.imageButton1);
		img_btn_del = (ImageButton)findViewById(R.id.imageButton2);
		edit1 = (EditText)findViewById(R.id.editText1);
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
