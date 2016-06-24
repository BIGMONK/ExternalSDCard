package com.cdc.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import android.os.Environment;
import android.util.Log;

public class Tools {

	public static final String TAG = "ExternalSDCard";
	
	
	/**
	 * 
	 * @param source
	 *            源文件
	 * @param target
	 *            目标文件
	 */
	public static void byteStreamCopyFile(InputStream in, File target) {
		BufferedInputStream bin = null;
		OutputStream fos = null;
		try {
			bin = new BufferedInputStream(in);
			fos = new BufferedOutputStream(new FileOutputStream(target));
			byte[] buffer = new byte[2048];
			int i;
			while ((i = bin.read(buffer)) != -1) {
				Log.i(Tools.TAG,i+"");
				fos.write(buffer, 0, i);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bin.close();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// 其中第一列表示挂载的设备，
	// 第二列表示在当前目录树中的挂载点，
	// 第三点表示当前文件系统的类型，
	// 第四列表示挂载属性（ro或者rw），
	// 第五列和第六列用来匹配/etc/mtab文件中的转储（dump）属性
	/**
	 * 获取内置和外置SD卡的路径
	 * @return
	 */
	public static ArrayList<String> getStorages() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(Environment.getExternalStorageDirectory().getPath());
		List<String> typeWL = Arrays.asList("vfat", "exfat", "sdcardfs",
				"fuse", "ntfs", "fat32", "ext3", "ext4");
		// tmpfs是一种虚拟内存文件系统，而不是块设备。
		List<String> typeBL = Arrays.asList("tmpfs");
		String[] mountWL = { "/mnt", "/Removable" };
		//UICC：Universal Integrated Circuit Card。 UICC卡是一种可移动智能卡，包含USIM和SIM两个模块
		String[] mountBL = { "/mnt/secure", "/mnt/shell", "/mnt/asec",
				"/mnt/obb", "/mnt/media_rw/extSdCard", "/mnt/media_rw/sdcard",
				"/storage/emulated", "/storage/uicc0", "/storage/uicc1",
				"/mnt/media_rw/sda1" };
		String[] deviceWL = { "/dev/block/vold", "/dev/fuse",
				"/mnt/media_rw/extSdCard" };
		BufferedReader bufferedReader = null;
		try {
			String line;
			bufferedReader = new BufferedReader(new FileReader("/proc/mounts"));
			while ((line = bufferedReader.readLine()) != null) {
				StringTokenizer tokens = new StringTokenizer(line, " ");
				String device = tokens.nextToken();
				String mountpoint = tokens.nextToken();
				String type = tokens.nextToken();
				// skip if already in list or if type/mountpoint is blacklisted
				if (list.contains(mountpoint) || typeBL.contains(type)
						|| StartsWith(mountBL, mountpoint)) {
					continue;
				}
				// check that device is in whitelist, and either type or
				// mountpoint is in a whitelist
				if (StartsWith(deviceWL, device)
						&& (typeWL.contains(type) || StartsWith(mountWL,
								mountpoint))) {
					list.add(mountpoint);
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				bufferedReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	private static boolean StartsWith(String[] array, String text) {
		for (String item : array) {
			if (text.startsWith(item)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 执行linux命令,并将结果保存在内置的sd卡上
	 * 
	 * @param commond
	 * @param fileName
	 */
	public static void execute(String[] commond, String fileName) {
		Runtime runtime = Runtime.getRuntime();
		Process proc;
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/" + fileName);
		OutputStreamWriter fileWriter = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			proc = runtime.exec(commond);
			fileWriter = new OutputStreamWriter(new FileOutputStream(file),
					"UTF-8");
			is = proc.getInputStream();
			isr = new InputStreamReader(is);
			String line;
			br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				fileWriter.write(line + "\r\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
				isr.close();
				br.close();
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 读取系统上的文件,并保存在内置的sd卡上面
	 * 
	 * @param fileSourcePath
	 * @param fileTargetName
	 */
	public static void readFile(String fileSourcePath, String fileTargetName) {
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/" + fileTargetName);
		OutputStreamWriter fileWriter = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileSourcePath));
			fileWriter = new OutputStreamWriter(new FileOutputStream(file),
					"UTF-8");
			String line;
			while ((line = br.readLine()) != null) {
				fileWriter.write(line + "\r\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
}
