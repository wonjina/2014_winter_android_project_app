package com.example.sf;

import java.util.Calendar;

import cnetService.NetService;
import cnetService.NetService.LocalBinder;

import com.example.sf.R;
import com.example.sf.R.array;
import com.example.sf.R.id;
import com.example.sf.R.layout;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Add_fridge_Activity extends Activity {
	
	private Button mPickDate;		//마지막 유통기한 날짜 저장하는 변수
	private int mYear;
	private int mMonth;
	private int mDay;
	private String inyear;
	private String inmonth;
	private String indays;
	
	EditText ed_name;				// 품목이름 저장하는 변수
	TextView tx_date, tx_result, tv3, tv8;
	Button btn6, btn7;
	
	RadioButton r_freezer;
	
	String fre;
	String str;
	String inDate;
	String date;
	int result;						// 남은일수 저장하는 변수
	
	static final int DATE_DIALOG_ID = 0; //날짜 초기화
	
	private boolean mBound = false;
	NetService nService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_fridge_add);
	    
	    initList_add_fridge();
	    
	    mPickDate = (Button)findViewById(R.id.datepick);
	    
		mPickDate.setOnClickListener(new View.OnClickListener() {
	         
	        @SuppressWarnings("deprecation")
			@Override
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	            showDialog(DATE_DIALOG_ID);
	        }
			});
		final Calendar c = Calendar.getInstance();
	     
	    mYear = c.get(Calendar.YEAR);
	    mMonth = c.get(Calendar.MONTH);
	    mDay = c.get(Calendar.DAY_OF_MONTH);
	     
	    updateDisplay();
		
		    // TODO Auto-generated method stub
		}

 	//저장버튼 눌럿을 떄
	public void btn_save(View v)
	{
		if(!ed_name.getText().toString().equals(""))
		{
			Intent int_back = new Intent();
			if( r_freezer.isChecked() ) fre = "Freezer";
			else 						fre = "Fridge";
			nService.startThread("74" + ed_name.getText().toString()+"," + mPickDate.getText().toString()+","+fre);
			if( nService.getConnect() )
			{
				nService.getString();
				int_back.putExtra("name_data", ed_name.getText().toString());
				int_back.putExtra("res_day_data", Integer.toString(result));
				int_back.putExtra("loca_data", fre);
				setResult(107, int_back);
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
	// pick 버튼에 text 동기화 시키기 위한 함수
	private void updateDisplay()
	{
		mPickDate.setText(new StringBuilder()
	            .append(mYear).append(",")
	            .append(mMonth+1).append(",")
	            .append(mDay));
		date = mPickDate.getText().toString();
	}
	//DatePicker 눌렷을때 사용되는 함수
	private DatePickerDialog.OnDateSetListener mDateSetListener = 
	        new DatePickerDialog.OnDateSetListener() {
	             
	            @Override
	            public void onDateSet(DatePicker view, int year, int monthOfYear,
	                    int dayOfMonth) {
	                // TODO Auto-generated method stub
	                mYear = year;
	                mMonth = monthOfYear;
	                mDay = dayOfMonth;
	                updateDisplay();
	                math_date();
	            }
	        };
	
	//back 버튼
	public void btn_cancel(View v)
	{
		finish();
	}

	//초기화
	private void initList_add_fridge()
	{
		findViewById();
				
		inDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
		inyear = new java.text.SimpleDateFormat("yyyy").format(new java.util.Date());
		inmonth = new java.text.SimpleDateFormat("MM").format(new java.util.Date());
		indays = new java.text.SimpleDateFormat("dd").format(new java.util.Date());
		tx_date.setText(inDate);
		
		tv3.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
		tv8.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
		
		btn6.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
		btn7.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	}
	
	
	private void findViewById()
	{
	    ed_name = (EditText)findViewById(R.id.editText1);
	    tx_date = (TextView)findViewById(R.id.textView6);
	    tx_result = (TextView)findViewById(R.id.textView5);
	    mPickDate = (Button)findViewById(R.id.datepick);
	    r_freezer = (RadioButton)findViewById(R.id.radio0);
	    
	    tv3 = (TextView)findViewById(R.id.tv3);
	    tv8 = (TextView)findViewById(R.id.textView8);
	    
	    btn6 = (Button)findViewById(R.id.button6);
	    btn7 = (Button)findViewById(R.id.button7);
	}
	
	
	//다이얼로그 생성
	@Override
	protected Dialog onCreateDialog(int id)
	{
	    switch(id)
	    {
	    	case DATE_DIALOG_ID:
	    		return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
	    }
	    return null;
	}
	
	
	// 남은날짜 계산하는 함수
	public void math_date()
	{
		result = totalday(mYear,mMonth, mDay) - totalday(Integer.parseInt(inyear), Integer.parseInt(inmonth)-1, Integer.parseInt(indays));
		tx_result.setText(result+" days Remain");
	}
	
	
	//날짜 계산하기위한 함수
	public int totalday(int y, int m, int d)
	{
	    int months[]={31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	    long total=0L;
	    
	    total=(y-1)*365L+(y-1)/4-(y-1)/100+(y-1)/400;
	    
	    if( 0==(y%4) && 0!=y%100 || 0==(y%400))
	        months[1]++;
	    
	    for(int i=0; i < m-1; i++)
	    		total += months[i];
	    
	    total+=d;
	    return (int)total;
	}
	

	private ServiceConnection nConnection = new ServiceConnection()
	{
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			Log.e("onStart", "add_fridge_onServiceConnected");
			nService = ((LocalBinder) service).getService();
			mBound = true;
			Log.e("onStart", "add_fridge_onServiceConnected2");
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
		Log.e("onStart", "add_fridge_onStart");
		Intent intent = new Intent(this, NetService.class);
		intent.putExtra("EventType", "0");
		bindService(intent, nConnection, Context.BIND_AUTO_CREATE);
		Log.e("onStart", "add_fridge_onStart2");
	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(mBound)
		{
			Log.e("onStart", "add_fridge_onStop");
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
