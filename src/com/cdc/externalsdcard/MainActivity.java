package com.cdc.externalsdcard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cdc.tools.Tools;

public class MainActivity extends Activity {
	
	
	
	private List<String> lists=new ArrayList<String>();
	
	private File file;
	
	private String path;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		Tools.execute(new String[]{"mount"}, "mount_new.txt");
//		Tools.execute(new String[]{"ls","-l","/storage/sdcard1"},"ls-l-sdcard-storage.txt");
		
//		Tools.execute(new String[]{"ls","-l"},"ls-l.txt");
//		Tools.execute(new String[]{"ls","-l","storage"},"ls-l-storage.txt");
//		Tools.readFile("/proc/mounts", "proc_mount.txt");
//		Tools.readFile("/system/etc/permissions/platform.xml", "platform.txt");
		
		//Tools.getStorages();
		lists=Tools.getStorages();
		Log.i(Tools.TAG, lists.toString());
		path=lists.get(1);
		file=new File(path);
		if(file.exists()){
			if(file.canWrite() && file.canRead()){
				Toast.makeText(this, path+",是可读写的!", Toast.LENGTH_SHORT).show();
				//copyFileFromAssets("ls.txt", "ls_copy.txt");
			}else if(file.canRead()){
				Toast.makeText(this, path+",是可读不可写的!", Toast.LENGTH_SHORT).show();
			}
		}
		
		
		//Tools.execute(new String[]{"who",lists.get(0)},"who.txt");
	//	Tools.execute(new String[]{"w",lists.get(0)},"w.txt");
//		Tools.execute(new String[]{"ls","-l",lists.get(1)},"ls外.txt");
		
		
		
	}
	/**
	 * 在外置SD卡上写入文件
	 * @param view
	 */
	@SuppressLint("InlinedApi")
	public void sdcard(View view){
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
	    startActivityForResult(intent, 42);
	}
	
	
	
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
		Toast.makeText(this, "requestCode="+requestCode+",resultCode="+resultCode, Toast.LENGTH_LONG).show();
	    if (resultCode == RESULT_OK) {
	        Uri treeUri = resultData.getData();
	        //content://com.android.externalstorage.documents/tree/F685-F357%3A
	        Log.i(Tools.TAG,treeUri.toString());
	        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

	        // List all existing files inside picked directory
	        for (DocumentFile file : pickedDir.listFiles()) {
	            
				Log.i(Tools.TAG, "Found file " + file.getName() + " with size " + file.length());
	        }

	        // Create a new file and write into it
	       // DocumentFile newFile = pickedDir.createFile("text/plain", "My Novel");
	        DocumentFile newFile = pickedDir.createFile("text/plain", "My Novel");
	        DocumentFile newDir=pickedDir.createDirectory("ifaboo");
	        if(newDir==null){
	        	return;
	        }else if(newDir.exists()){
	        	//return;
	        }
	        if(newFile==null){
	        	return;
	        }else if(newFile.exists()){
	        	//return;
	        }
	        //content://com.android.externalstorage.documents/tree/F685-F357%3A/document/F685-F357%3Aifaboo
	        Log.i(Tools.TAG, "uri="+newDir.getUri()+",name="+newDir.getName()+",type="+newDir.getType()+",p="+newDir.getParentFile().getName());
	        DocumentFile newFile2=newDir.createFile("text/plain", "ls_copy");
	        //vnd.android.document/directory
	        OutputStream out=null;
	        try {
	        	out = getContentResolver().openOutputStream(newFile2.getUri());
				out.write("A long time ago...测试".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	        
	    }
	}
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			File dir=new File(path);

			if(dir.exists() && dir.isDirectory() && dir.canWrite() && dir.canRead()){
				Toast.makeText(this, "文件"+path+"是存在的,可读写的", Toast.LENGTH_SHORT).show();
				//copyFileFromAssets("ls.txt", "ls_copy.txt");
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private void copyFileFromAssets(String sourcePath,String targetPath){
		try {
			InputStream inputStream=getAssets().open(sourcePath);
			Log.i(Tools.TAG,inputStream.available()+",文件长度");
			File targetFile=new File(path+"/"+targetPath);
			Log.i(Tools.TAG,targetFile.getAbsolutePath());
			Log.i(Tools.TAG,targetFile.exists()+",存在吗?");
			if(targetFile.exists()){
				Log.i(Tools.TAG,targetFile.exists()+",存在吗2?");
			}else{
				//无法在外置SD卡上创建文件  权限不允许，只能通过Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);的方法创建
				//java.io.IOException: open failed: EACCES (Permission denied)
				boolean bl=targetFile.createNewFile();
				Log.i(Tools.TAG,"文件创建"+bl);
				
			}
			
			Tools.byteStreamCopyFile(inputStream, targetFile);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
