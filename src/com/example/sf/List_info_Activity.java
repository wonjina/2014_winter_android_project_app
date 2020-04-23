package com.example.sf;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cnetService.NetService;
import cnetService.NetService.LocalBinder;

public class List_info_Activity extends ListActivity implements OnItemClickListener {
	
	TextView tx1;
	
	Button btn_home, btn_add;
	ListView My_list;

	String str;
	String list_result;
	String pass_Str;		//���� ȭ������ �Ѱ��� ��Ʈ�� ������ ���� ����� null���� ���ϵǴµ� �̸� �˻��ϱ� ���� ���
	
	private boolean mBound = false;
	NetService nService;
	
	private ArrayList<String> arrayList;
	private ArrayAdapter<String> adapter;

	private boolean del_state; //������ư �������� �ȴ��������� ��Ÿ���� ���º���
	
	int sel_list=0;	//����ƮŬ���� ���°�� Ŭ���ƴ����� ������ ����

	@Override
	//������ ����Ʈ �ߴ� ȭ��
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_info_list);
	    Log.e("onStart", "list_info  onCreate");
	    initList_info();
	    Log.e("onStart", "list_info  onCreate");
	}
	
	//�߰� ��ư ������
	public void btn_add1(View v) throws SocketException
	{
		nService.startThread("41");
		setAdapters(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
		del_state =false;
		if( nService.getConnect() )
		{
			Intent int_add = new  Intent(List_info_Activity.this, Add_info_Activity.class);
			int_add.putExtra("disease_name", nService.getString());
			startActivityForResult(int_add, 1);
		}
		else 
		{
			showToast();
		}
	}

	//���� ��ư ��������
	public void click_btndle(View v) throws SocketException
	{
		str = "";
		if(!del_state)
		{
			setAdapters(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, arrayList));
			del_state = true;
		}
		else 
		{
			SparseBooleanArray checkArr = My_list.getCheckedItemPositions();
			if(checkArr.size()!= 0)
			{
				for(int i = My_list.getCount()-1; i>-1; i--)
				{
					if(checkArr.get(i)){
						str +=",";
						str += arrayList.get(i);
						arrayList.remove(i);
					}
				}
			}
			
			if( !str.equals("") )
			{
				//������ ���� ��� �κ�
				nService.startThread("93"+str);
				
				if( nService.getConnect() )
				{
					nService.getString();
					adapter.notifyDataSetChanged();
					setAdapters(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
					del_state = false;
				}
				else 
				{
					showToast();
				}
			}
			
		}
	}

	public void btn_refresh(View v) throws SocketException
	{
		nService.startThread("31");
		if( nService.getConnect() )
		{
			arrayList.removeAll(arrayList);
			init_Settexts(nService.getString());
		}
		else 
		{
			showToast();
		}
	}
	
	public void btn_home(View v)
	{
		Log.e("onStart", "list_info  btn_home");
		finish();
	}
	
	//����Ʈ �޴� ������ �� ��Ƽ��Ƽ�� �޾ƿ��� �Լ�
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) { 

	 // TODO Auto-generated method stub
		 //����Ʈ �߰� Ŭ���� ���ƿ���
		 if(requestCode == 1) 
		 { 
			 if(resultCode==100)
			 {
				 arrayList.add(data.getStringExtra("name_data"));
				 adapter.notifyDataSetChanged(); 
			 }
		 }
		 //����ƮŬ���� �󼼸���Ʈ�� �߰� �ű⼭ ���ƿ���
		 if(requestCode == 6) 
		 { 
			 if(resultCode==101)
			 {
				 arrayList.set(sel_list, data.getStringExtra("name_data1"));
				 adapter.notifyDataSetChanged(); 
			 }
		 }
		 super.onActivityResult(requestCode, resultCode, data); 
	}

	//����Ʈ �� �������� Ŭ���� �ۿ��ϴ� �Լ�
		@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(!del_state)
		{
			nService.startThread("32");
			if( nService.getConnect() )
			{
				sel_list= arg2;
				Intent my_info_detail = new Intent(this, Info_info_Activity.class);
				my_info_detail.putExtra("name_data", (String) arg0.getItemAtPosition(arg2));
				my_info_detail.putExtra("my_disease", nService.getString());
				startActivityForResult(my_info_detail, 6);
			}
			else 
			{
				showToast();
			}
		}
	}
	
	private void setAdapters(ArrayAdapter<String> adapter)
	{
		//�̰��ؾ� onActivityResult�Լ����� adapter.notifyDataSetChanged(); �� ����Ǳ� ������ 
		this.adapter =adapter;
		My_list.setAdapter(this.adapter);
		My_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
	
	private void initList_info()
	{
	    findViewsById();
	    del_state=false;	//������ư �ȴ������� �ʱⰪ
	    arrayList = new ArrayList<String>();
	    mBound = false;
	    sel_list=0;	
	    setAdapters(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
		My_list.setOnItemClickListener(this);
		
		Intent list_info = getIntent();
		init_Settexts(list_info.getStringExtra("list_name"));
		

		tx1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
		btn_add.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	}
	private void init_Settexts(String str)
	{
	    StringTokenizer st1 = new StringTokenizer(str, ",");
	    while(st1.hasMoreTokens())
	    	arrayList.add(st1.nextToken());
		adapter.notifyDataSetChanged();
	}
	private void findViewsById() {
		 btn_home = (Button)findViewById(R.id.datepick);
		 btn_add = (Button)findViewById(R.id.button01);
		 My_list = (ListView)findViewById(android.R.id.list);
		 
		 tx1 = (TextView)findViewById(R.id.tv3);
    }
	

	 //���񽺿����ϴ� �Լ�
	private ServiceConnection nConnection = new ServiceConnection()
	{
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			Log.e("onStart", "list_lifo_onServiceConnected");
			nService = ((LocalBinder) service).getService();
			mBound = true;	
		}
		//���μ��� ų�Ǵ� ��찰�� ���� ȣ��ȴ�
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBound = false;
			onStop() ;
			onDestroy();
		}
	};
	
	@Override
	protected void onStart() 
	{
		// TODO Auto-generated method stub
		super.onStart();
		Log.e("onStart", "list_lifo_scond_onStart");
		Intent intent = new Intent(List_info_Activity.this, NetService.class);
		intent.putExtra("EventType", "0");
		bindService(intent, nConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e("onStart", "list_info_onStop , " +mBound);
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
		finish();
	}
	
}


