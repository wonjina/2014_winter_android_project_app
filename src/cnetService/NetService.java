package cnetService;

import java.net.SocketException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import client.NetClient;

public class NetService extends Service{

	private NetClient nc;

	String str_my_info ="";		//�����κ��� ������ �޴º���
	private String str_EventType;	//������ ���� �̺�Ʈ Ÿ�� 
	private final IBinder ib = new LocalBinder();
	
	private Object my_moniter = new Object();
	private boolean moniter_State = false;
	
	//ó���� ���� - ���� ����
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		moniter_State = false;
		nc = NetClient.getInstance();
		Nc_init_Thread nc_Thread = new Nc_init_Thread();
		nc_Thread.start();
		Log.d("onStart", "NetService_onCreate_���ϻ���_������ ����");
	}

	public void re_Connect()
	{
		if( !nc.getConnectToServer() )
		{
			moniter_State = false;
			Nc_init_Thread nc_Thread = new Nc_init_Thread();
			nc_Thread.start();
		}
	}
	class Nc_init_Thread extends Thread{
		
		public void run()
		{
			Log.d("onStart", "Nc_init_Thread");
			nc.initServer();
		}
	}
	////////////////////
	
	public class LocalBinder extends Binder
	{
		public NetService getService()
		{
			Log.d("onStart", "NetService_LocalBinder");
			return NetService.this;
		}
	}
	
	//����ϴ� ������ ���� �Լ�
	public void startThread(String event_Type)
	{
		Log.d("onStart", "NetService_startThread");
		str_EventType = event_Type;
		Nc_work_Thread nct = new Nc_work_Thread();
		nct.start();
	}
	
	class Nc_work_Thread extends Thread{
		
		public void run()
		{
			if( str_EventType.charAt(0) != '0' )
			{
				Log.d("onStart", "NetService_startThread_run");
				synchronized(my_moniter)
				{
					Log.d("onStart", "NetService_startThread_run_rock");
					nc.startWriter(str_EventType);
					str_my_info = nc.startReader(str_EventType.charAt(0));	
					moniter_State = true;
					my_moniter.notify();
				}
				Log.d("onStart", "NetService_startThread_����ϴ� ������ run ->"+str_my_info);
			}
		}
	}
	
///////////////////////////

	public String getString()
	{	
		Log.d("onStart", "NetService_getString");
		synchronized(my_moniter)
		{
			if(!moniter_State)
			{
				try {
					my_moniter.wait();
				} catch (InterruptedException e) {	e.printStackTrace(); }
			}
		}
		moniter_State = false;
		
		return str_my_info;
	}
	
	public boolean getConnect()
	{		
		Log.d("onStart", "NetService_getConnect");
		synchronized(my_moniter)
		{
			if(!moniter_State)
			{
				try {
					my_moniter.wait();
				} catch (InterruptedException e) {	e.printStackTrace(); }
			}
		}
		
		return nc.getConnectToServer();
	}
	/////////////////////////////////

	//������Ʈ�� ���񽺶� ����� ȣ��
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.d("onStart", "NetService_onBind");
		return ib;
	}
	
	////////////////////////////////////
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d("onStart", "NetService_onUnbind");
		return super.onUnbind(intent);
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("onStart", "NetService_onDestroy");
		nc.stoped();
	}

}
