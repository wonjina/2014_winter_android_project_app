package com.example.sf;

import java.util.ArrayList;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Search_recipe_Activity extends Activity implements OnItemClickListener {
	
	private ArrayList<String> arrayList;
	private ArrayAdapter<String> adapter;
	
	private ListView list;
	
	TextView tv2;
	
	private boolean mBound;
	NetService nService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_recipe_search);
	    
	    initList_search_recipe();
	    	
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		nService.startThread("62"+ ( (String) parent.getItemAtPosition(position) )   );
		if( nService.getConnect() )
		{
			Intent intent = new Intent(Search_recipe_Activity.this, Info_search_recipe_Activity.class);
			intent.putExtra("recipe_detail", nService.getString());
			startActivityForResult(intent, 6);
		}
		else
		{
			showToast();
			finish();
		}
	}
	

	private void initList_search_recipe()
	{
		findViewById();
		mBound = false;
	    arrayList = new ArrayList<String>();
	    init_SetTexts();
		setAdapters(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
		list.setOnItemClickListener(this);
		
		tv2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	}
	private void init_SetTexts()
	{
		Intent search_recipe = getIntent();
		StringTokenizer st1 = new StringTokenizer(search_recipe.getStringExtra("recipe_name"), ",");
	    while(st1.hasMoreTokens())
	    {
	    	arrayList.add(st1.nextToken());
	    }
	}
	private void findViewById()
	{
		list = (ListView)findViewById(R.id.listView1);
		
		tv2 = (TextView)findViewById(R.id.textView2);
	}
	private void setAdapters(ArrayAdapter<String> adapter)
	{
		this.adapter = adapter;
		list.setAdapter(this.adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);	
	}
	public void btn_home_ser(View v)
	{
		finish();
	}

	private ServiceConnection nConnection = new ServiceConnection()
	{
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			Log.e("onStart", "search_recipe_onServiceConnected");
			nService = ((LocalBinder) service).getService();
			mBound = true;
			Log.e("onStart", "search_recipe_onServiceConnected2");
				
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
		Log.e("onStart", "search_recipe_onStart");
		Intent intent = new Intent(this, NetService.class);
		intent.putExtra("EventType", "0");
		bindService(intent, nConnection, Context.BIND_AUTO_CREATE);
		Log.e("onStart", "search_recipe_onStart2");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(mBound)
		{
			Log.e("onStart", "search_recipe_onStop");
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
