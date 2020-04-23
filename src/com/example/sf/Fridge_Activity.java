package com.example.sf;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cnetService.NetService;
import cnetService.NetService.LocalBinder;

public class Fridge_Activity extends Activity {
	
	private ArrayAdapter<CData> adapter;
	private ArrayList<CData> alist;
	private TextView title;
	private TextView text;
	private TextView text2;
	private CheckBox ch;
	
	//일단 서버로 부터 받은 데이터 확인용
	TextView tv;
	
	Button btn;
	
	String material_info;
	String list_material;
	
	String name;
	String days;
	String loca;
	private String inyear;
	private String inmonth;
	private String indays;
	
			
	ListView Men_list;
	
	String str;

	private boolean mBound;
	NetService nService;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e("onStart", "fridge_oncreate5");
	    super.onCreate(savedInstanceState);
	    Log.e("onStart", "fridge_oncreate4");
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    Log.e("onStart", "fridge_oncreate3");
	    setContentView(R.layout.activity_fridge);
	    
	    Log.e("onStart", "fridge_oncreate");
	    initList_fridge();
	    Log.e("onStart", "fridge_oncreate2");
	}
	
	//add함수 호출시 실행된다고 보면됨
	private class DataAdapter extends ArrayAdapter<CData> {
		private LayoutInflater mInflater;
		
		public DataAdapter(Context context, ArrayList<CData> object) {
			super(context, 0, object);	
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View v, ViewGroup parent) {
			View view = null;
			if (v == null) {
				view = mInflater.inflate(R.layout.list_position, null);
			} 
			else view = v;
			
			final CData data = this.getItem(position);
			if (data != null) {
				title = (TextView) view.findViewById(R.id.tv1);
				text = (TextView) view.findViewById(R.id.tv2);
				text2 = (TextView) view.findViewById(R.id.tv3);
				ch = (CheckBox) view.findViewById(R.id.checkBox1);
				title.setText(data.getTitle());
				text.setText(data.getText());
				text2.setText(data.getText2());
			}
			return view;
		}
	}
	
	private class CData {
		private String Title;
		private String Text;
		private String Text2;
	
		public CData(Context context, String mTitle, String mText, String mText2) {		
			Title = mTitle;
			Text = mText;
			Text2 = mText2;
		}	

		public String getTitle() {	return Title;	}
		public String getText()  {	return Text;	}
		public String getText2() {	return Text2;	}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
	 // TODO Auto-generated method stub 
			 if(requestCode == 7) 
			 { 
				 if(resultCode==107)
				 {
					 name = data.getStringExtra("name_data");
					 days = data.getStringExtra("res_day_data");
					 loca = data.getStringExtra("loca_data");
					 
					 adapter.add(new CData(this, name, days, loca));
					 adapter.notifyDataSetChanged(); 
				 }
			 }
			 super.onActivityResult(requestCode, resultCode, data); 
	 }
	
	
	//재료 추가 버튼
	public void btn_add(View v)
	{
		Intent intent = new Intent(this, Add_fridge_Activity.class);
		startActivityForResult(intent, 7);		
	}
	
	//삭제 버튼 눌럿을때
	public void click_btndle(View v)
	{
		String del_fridge = "";
		for(int index = Men_list.getChildCount()-1 ; index >= 0 ; index--)
		{
			CheckBox cb_fridge  = (CheckBox) ((LinearLayout) Men_list.getChildAt(index)).getChildAt(3);
						
			if( cb_fridge.isChecked() )
			{
				del_fridge += ",";
				del_fridge += ( (TextView)((LinearLayout) Men_list.getChildAt(index)).getChildAt(0) ).getText().toString();
				adapter.remove( (CData) Men_list.getAdapter().getItem(index) );
				cb_fridge.setChecked(false);
			}
		}
		
		//삭제 할것이 있따면
		if( !del_fridge.equals(""))
		{
			adapter.notifyDataSetChanged(); 
			nService.startThread("75"+del_fridge);
			if( !nService.getConnect() )
			{
				showToast();
				finish();
			}
			else nService.getString();
		}
	}

	public void btn_home (View v)  {	finish();	}
	public void btn_refresh(View v)
	{
		nService.startThread("52");	
		if( nService.getConnect() )
		{
			alist.removeAll(alist);
			init_Settexts(nService.getString());
			adapter.notifyDataSetChanged(); 
		}
		else
		{
			showToast();
			finish();
		}
	}
	

	private void initList_fridge()
	{
		findViewById();
		inyear = new java.text.SimpleDateFormat("yyyy").format(new java.util.Date());
		inmonth = new java.text.SimpleDateFormat("MM").format(new java.util.Date());
		indays = new java.text.SimpleDateFormat("dd").format(new java.util.Date());
		mBound = false;
	    setAdapters(new ArrayAdapter<CData>(this, android.R.layout.simple_list_item_1));
		alist = new ArrayList<CData>();
		adapter = new DataAdapter(this,alist);
		Men_list.setAdapter(adapter);
		
		Intent fridge = getIntent();
	    init_Settexts(fridge.getStringExtra("fridge_detail"));
	    
	    tv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	    
	    btn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	}
	private void init_Settexts(String str)
	{
		StringTokenizer st1 = new StringTokenizer(str, "&");
	    while(st1.hasMoreTokens())
	    {
			StringTokenizer st2 = new StringTokenizer(st1.nextToken(), ",");
			name = st2.nextToken();
			days = Integer.toString(math_date(st2.nextToken(),st2.nextToken(),st2.nextToken()));
			loca = st2.nextToken();
			
			adapter.add(new CData(this, name, days, loca));
			adapter.notifyDataSetChanged();
		}	
	}
	
	private void findViewById()
	{
	    Men_list = (ListView)findViewById(R.id.listView1);
	    tv = (TextView)findViewById(R.id.tv3);
	    
	    btn = (Button)findViewById(R.id.button01);
	}
	private void setAdapters(ArrayAdapter<CData> adapter)
	{
		this.adapter = adapter;
		Men_list.setAdapter(this.adapter);
		Men_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
	

	// 남은날짜 계산하는 함수
	public int math_date(String mYear, String mMonth, String mDay)
	{
		return totalday(Integer.parseInt(mYear), Integer.parseInt(mMonth) , Integer.parseInt(mDay) ) - 
						totalday(Integer.parseInt(inyear), Integer.parseInt(inmonth)-1, Integer.parseInt(indays));
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
			Log.e("onStart", "fridge_onServiceConnected");
			LocalBinder binder = (LocalBinder) service;
			nService = binder.getService();
			mBound = true;
			Log.e("onStart", "fridge_onServiceConnected2");
				
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
		Log.e("onStart", "fridge_onStart");
		Intent intent = new Intent(this, NetService.class);
		intent.putExtra("EventType", "0");
		bindService(intent, nConnection, Context.BIND_AUTO_CREATE);
		Log.e("onStart", "fridge_onStart2");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(mBound)
		{
			Log.e("onStart", "fridge_onStop");
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
