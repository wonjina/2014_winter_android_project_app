package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

import android.util.Log;

public class NetClient 
{

	private static final String HOST = "220.149.119.118";
	private static final int PORT = 9730;
	
	private Selector selector = null;
	private SocketChannel sc = null;
	
	private Charset charset = null;
	private CharsetDecoder decoder = null;
	
	private ByteBuffer buffer;
	
	private static NetClient instance = new NetClient();
	
	private NetClient()
	{
		charset = Charset.forName("EUC-KR");
		decoder = charset.newDecoder();
		this.buffer = ByteBuffer.allocateDirect(1024);
	}
	
	public static NetClient getInstance()
	{
		return instance;
	}
	

	public void initServer(){
		
		Log.e("onStart", "initServer");
		try{
			selector = Selector.open();
			sc = SocketChannel.open();
			sc.socket().connect(new InetSocketAddress(HOST, PORT),3000);
			sc.finishConnect();
			sc.configureBlocking(false);
			sc.register(selector, SelectionKey.OP_READ);
		}catch(IOException e)
		{ 
			stoped();
		}
		
	}
	
	public boolean getConnectToServer() 
	{
		Log.i("onStart", selector.isOpen()+"");
		return selector.isOpen();
	}
	

	public void startWriter(String str) {
		// TODO Auto-generated method stub		
		Log.e("onStart", "startWriter");
		try{			
			
			buffer.put(str.getBytes("EUC_KR"));
			buffer.flip();
			sc.write(buffer);
		}
		catch(Exception e)	
		{
			stoped();
		} finally	{	 clearBuffer(buffer); 	}
		
		Log.e("onStart", "startWriter2");
	}

	public String startReader(char type) {
		// TODO Auto-generated method stub
		Log.e("onStart", "startReader");
		String str="";
		try{
			
			if( type == '7' || type == '8' || type == '9') selector.select(100);
			else 							selector.select();
			
			Iterator it = selector.selectedKeys().iterator();
			
			while(it.hasNext())
			{
				SelectionKey key = (SelectionKey)it.next();
				if(key.isReadable())
				{
					str = read(key);
				}
				it.remove();
			}
			
		}catch(Exception e)	{
			stoped();
		}
		
		return str;
	}
	
	private String read(SelectionKey key)
	{
		// SelectionKey로 부터 소켓 채널을 얻어 옴
		SocketChannel sc = (SocketChannel)key.channel();
		// ByteBuffer를 생성함
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
		int read = 0;
		
		try{
			// 요청한 클라이언트의 소켓 채널로 부터 데이터를 읽어 들임
			for(int i=0; i<2; i++)
			{
				read = sc.read(buffer);
				Log.w("onStart", read+"");
				if( read == -1)
				{
					stoped();
					return "";
				}
			}
		}catch(IOException e){
			stoped();
		}
		
		buffer.flip();
		String data = "";
		try
		{
			data = decoder.decode(buffer).toString();
		}catch(CharacterCodingException e){ e.printStackTrace(); }		
		
		// 버퍼 메모리를 해제함 
		clearBuffer(buffer);
		return data;
	}
	
	public void stoped()
	{
		try 
		{
			sc.close();
			selector.close();
		} catch (IOException e) { e.printStackTrace();	}
	}
	
	private void clearBuffer(ByteBuffer buffer)
	{
		if(buffer != null)
		{
			buffer.clear();
		}
	}
	
}
