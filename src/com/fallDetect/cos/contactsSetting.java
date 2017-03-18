package com.fallDetect.cos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.R.string;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class contactsSetting extends Activity{
	private Button contactsSaveBT,contactsChooseBT;
	private EditText name1,name2,name3,name4;
	private EditText phone1,phone2,phone3,phone4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);
		//联系人姓名和电话
		name1=(EditText)findViewById(R.id.editText1);
		phone1=(EditText)findViewById(R.id.editText2);
		name2=(EditText)findViewById(R.id.editText3);
		phone2=(EditText)findViewById(R.id.editText4);
		name3=(EditText)findViewById(R.id.editText5);
		phone3=(EditText)findViewById(R.id.editText6);
		name4=(EditText)findViewById(R.id.editText7);
		phone4=(EditText)findViewById(R.id.editText8);
		//更新，取消按钮
		contactsSaveBT=(Button)findViewById(R.id.saveContacts);
		contactsChooseBT=(Button)findViewById(R.id.chooseContacts);
		ShowContacts();
		
		//更新按钮功能实现
		contactsChooseBT.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivityForResult(new Intent(
		                Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI), 0);	
			}
		});
		contactsSaveBT.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String path=Environment.getExternalStorageDirectory().getPath();
			    String sDir=path+"/Fall_Detection/";
			    String contacts_nameDir=sDir+"contacts_name.txt";
				String contacts_phoneDir=sDir+"contacts_phone.txt";
			  	try{
					File destDir = new File(sDir);
					if (!destDir.exists()) 
					{
					   destDir.mkdirs();
					}
					else {
						File file_name=new File(contacts_nameDir);
						File file_phone=new File(contacts_phoneDir);
						file_name.delete();
						file_phone.delete();
					}
					File file_name=new File(contacts_nameDir);
					File file_phone=new File(contacts_phoneDir);
					file_name.delete();
					FileOutputStream fos1=new FileOutputStream(file_name,true);
					FileOutputStream fos2=new FileOutputStream(file_phone,true);
					if(name1.getText()!=null&&phone1.getText()!=null)
					{
						fos1.write((name1.getText().toString()+"\n").getBytes());
						fos2.write((phone1.getText().toString()+"\n").getBytes());
					}
					if(name2.getText()!=null&&phone2.getText()!=null)
					{
						fos1.write((name2.getText().toString()+"\n").getBytes());
						fos2.write((phone2.getText().toString()+"\n").getBytes());
					}
					if(name3.getText()!=null&&phone3.getText()!=null)
					{
						fos1.write((name3.getText().toString()+"\n").getBytes());
						fos2.write((phone3.getText().toString()+"\n").getBytes());
					}
					if(name4.getText()!=null&&phone4.getText()!=null)
					{
						fos1.write((name4.getText().toString()+"\n").getBytes());
						fos2.write((phone4.getText().toString()+"\n").getBytes());
					}
					fos1.close();
					fos2.close();
					Toast.makeText(getApplicationContext(), "紧急联系人更新成功！",
							Toast.LENGTH_SHORT).show();
			    }catch (Exception e) {
			      	// TODO: handle exception
			      		  e.printStackTrace();
			      		Toast.makeText(getApplicationContext(), "更新失败！",
			      				Toast.LENGTH_SHORT).show();
			    }  
			}
		});
		
		//取消按钮功能实现
	}
	private void ShowContacts() {
		// TODO Auto-generated method stub
		ArrayList<String> list_name=new ArrayList<String>();
		ArrayList<String> list_phone=new ArrayList<String>();
		try {
            //String encoding="GBK";
			String path=Environment.getExternalStorageDirectory().getPath();
			String contacts_nameDir=path+"/Fall_Detection/contacts_name.txt";
			File file_name=new File(contacts_nameDir);
			String contacts_phoneDir=path+"/Fall_Detection/contacts_phone.txt";
			File file_phone=new File(contacts_phoneDir);
            
			if(file_name.isFile() && file_name.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file_name));//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	list_name.add(lineTxt);
                }                
                read.close();
		    }
			
			if(file_phone.isFile() && file_phone.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file_phone));//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	list_phone.add(lineTxt);
                }                
                read.close();
		    }			
			
		} catch (Exception e) 
		{
			e.printStackTrace();
        }
		
		if(list_name.size()>=1 && list_phone.size()>=1)
		{
			name1.setText(list_name.get(0));
			phone1.setText(list_phone.get(0));
			if(list_name.size()>=2 && list_phone.size()>=2)
			{
				name2.setText(list_name.get(1));
				phone2.setText(list_phone.get(1));
				if(list_name.size()>=3 && list_phone.size()>=3)
				{
					name3.setText(list_name.get(2));
					phone3.setText(list_phone.get(2));
					if(list_name.size()>=4 && list_phone.size()>=4)
					{
						name4.setText(list_name.get(3));
						phone4.setText(list_phone.get(3));
					}
				}
			}
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver reContentResolverol = getContentResolver();
             Uri contactData = data.getData();
             @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(contactData, null, null, null, null);
             cursor.moveToFirst();
             String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
                     null, 
                     ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, 
                     null, 
                     null);
             while (phone.moveToNext()) {
                 String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                 phone1.setText(usernumber);
                 name1.setText(username);
             }

         }
    }
	
}
