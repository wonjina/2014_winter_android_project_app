package com.example.sf;

import java.util.StringTokenizer;

import com.example.sf.R;
import com.example.sf.R.id;
import com.example.sf.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Change_info_info_Activity extends Activity {

	EditText ed_name;
	Intent intent;
	ImageButton img_btn_add, img_btn_del;
	
	Button btn1, btn2;
	
	int check_Count;		//üũ�ڽ� ���� ��Ȳ
	int a=0;	//�׳� �����̸��� �� �ӽ� ����
	
	LinearLayout line;							//�������� �� �����ؼ� �߰��� ����
	LinearLayout line_serv, serv_add_layout;	//ù��°���� ���ο� ���� ���Ͼ�̾ƿ�, �ι�°�� ù��°�� ���̴� üũ�ڽ�
	
	LinearLayout.LayoutParams layparam;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_info_info_change);
	    
	    initList_info();
	    
	}
	

	//�����߰� - ���̾�α�
	public void click_img_addBtn_change(View v)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(10);
		input.setFilters(FilterArray);
		alert.setTitle("Add Disease").setMessage("Disease Name Insert").setCancelable(false).setView(input)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String value = input.getText().toString();
				if( !value.equals("") ) cb_creat(value);			
				
			}})
	
		.setNegativeButton("Cancel",	new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {	}	})
		.show();
		
	}
	
	//üũ �ڽ� ����
	public void cb_creat(String value)
	{
		CheckBox checkB = new CheckBox(this);
		checkB.setText(""+value);
		
		if( check_Count == 0 || check_Count%3 == 0)
		{
			line_serv = (LinearLayout) View.inflate(Change_info_info_Activity.this, R.layout.dynamic_creat_view, null);
			line_serv.addView(checkB);
			line.addView(line_serv, layparam);
		}
		else ((LinearLayout) line.getChildAt(line.getChildCount()-1)).addView(checkB);
		
		check_Count++;
	}
	
	//üũ�ڽ� ����
	public void click_img_delBtn_change(View v)
	{
		//���� ���� ��ġ
		  int num = (line.getChildCount()-1)*3 + ( (LinearLayout) line.getChildAt(line.getChildCount()-1) ).getChildCount();		
		  int abc , def;
			
		  //3���� äũ�ڽ��� ������ �ִ� ���̾ƿ��� ������ŭ �ݺ�
		  for(int index = line.getChildCount()-1 ; index >= 0 ; index--)
		  {
		   //�� ���̾ƿ��� ���� ���� ������ŭ �ݺ�
			   for(int index2 = ((LinearLayout) line.getChildAt(index)).getChildCount()-1; index2 >= 0 ; index2 --)
			   {
				   CheckBox chile_View = (CheckBox) ((LinearLayout) line.getChildAt(index)).getChildAt(index2);
			    //üũ���ִٸ� ������ ���� �ؽ�Ʈ�� ������ �� ������
					if( chile_View.isChecked() ) 
					{
						//���������� �ε��� ����
						abc = line.getChildCount()-1;
						if( check_Count%3-1 <0 ) def=2;
						else def=check_Count%3-1;
						//���������ϰ� ���� ���ϰ� �ٸ����̶�� �� ���� �䰡 üũ�Ǿ� �����ؾߵǴµ� �ڿ� �ٸ����� �մٸ�
						if( num != (check_Count) )
						{
							chile_View.setText(  ( (CheckBox) ((LinearLayout) line.getChildAt(abc)).getChildAt(def) ).getText().toString()  );
						}
						if( ((LinearLayout) line.getChildAt(abc)).getChildCount() > 1 ) ((LinearLayout) line.getChildAt(abc)).removeViewAt(def);
						else line.removeViewAt(abc);
						check_Count--;
						chile_View.setChecked(false);
					}
					//num�� �ٽ� �ʱ�ȭ 1�ܰ�
					num --;
			   }
			   //num�� �ٽ� �ʱ�ȭ 2�ܰ�
			   num --;
		  }
	} 
	
	//äũ�� �͸� str�� ���̴� �Լ�
	public String click_img_checked(String str_checked)
	{
		  //3���� äũ�ڽ��� ������ �ִ� ���̾ƿ��� ������ŭ �ݺ�
		  for(int index = line.getChildCount()-1 ; index >= 0 ; index--)
		  {
		   //�� ���̾ƿ��� ���� ���� ������ŭ �ݺ�
		   for(int index2 = ((LinearLayout) line.getChildAt(index)).getChildCount()-1; index2 >= 0 ; index2 --)
		   {
			   CheckBox chile_View = (CheckBox) ((LinearLayout) line.getChildAt(index)).getChildAt(index2);
		    //üũ���ִٸ� ������ ���� �ؽ�Ʈ�� ������ �� ������
					if( chile_View.isChecked() ) 
					{
						str_checked += ",";
						str_checked += chile_View.getText().toString();
					}
			   }
		  }
		  return str_checked;
	}
	
	public void btn_save(View v)
	{
		if( !ed_name.getText().toString().equals("") )
		{
			Log.d("onStart", "change_info_info_click_save");
			intent = new Intent();
			intent.putExtra("name_data1", ed_name.getText().toString());
			intent.putExtra("name_data2", click_img_checked(""));
			setResult(100, intent);
			finish();
		}
		else 
		{
			Toast toast = Toast.makeText(this, "�̸��� �Է��ϼ���.", Toast.LENGTH_SHORT); 
			toast.show(); 
		}
	}

	public void btn_cancel(View v)
	{
		Log.d("onStart", "change_info_info_click_save");
		finish();
	}
	
	
	private void initList_info()
	{
	    //���̵�ο��ϴ� �Լ�
	    findViewsById();
	    check_Count = 0;
	    layparam = new LinearLayout.LayoutParams(layparam.WRAP_CONTENT, layparam.WRAP_CONTENT);
		layparam.gravity = Gravity.CENTER;
		
		init_Settexts();
		
		btn1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
		btn2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Cinzel-Bold.otf"));
	}
	private void init_Settexts()
	{
		intent = getIntent();		
		ed_name.setText(intent.getStringExtra("name_data"));
		StringTokenizer st1 = new StringTokenizer(intent.getStringExtra("my_disease"), ",");
		while(st1.hasMoreTokens())
			cb_creat(st1.nextToken());
	}
	private void findViewsById() 
	{
		 ed_name = (EditText)findViewById(R.id.editText1);
		 line = (LinearLayout)findViewById(R.id.dymanicArea);
		 img_btn_add = (ImageButton)findViewById(R.id.imageButton1);
		 img_btn_del = (ImageButton)findViewById(R.id.imageButton2);
		 
		 btn1 = (Button)findViewById(R.id.button01);
		 btn2 = (Button)findViewById(R.id.button2);
    }

	
}