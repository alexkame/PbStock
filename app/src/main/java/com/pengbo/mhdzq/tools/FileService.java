package com.pengbo.mhdzq.tools;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.os.Environment;

/**
 * 文件的业务类 saveToFile（String name, byte[] data, int size）保存文件的方法 
 * readFile（Stringname, byte[] data）读取文件的方法
 * getFileSize（String name） 获取文件的方法 返回值是文件的大小
 * 
 * @author pobo
 * 
 */
public class FileService {
	public static final String UTF8 = "UTF-8";

	public static final String ENTER = "\n";
	
	private Context context;
	private FileOutputStream mOutstream;

	public FileService(Context context) {
		this.context = context;
	}
	
	public static File getSDCardDirectory() {
		return Environment.getExternalStorageDirectory();
	}
	
	
	public static  String getInternalPath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

	public static void makeDir(String dir) {
		File absDir = new File(dir);
		if (!absDir.exists()) {
			absDir.mkdirs();
		}
	}
	
	/**
	 * 保存文件
	 * 
	 * @param name
	 * @param data
	 * @param size
	 */
	public void saveToFile(String name, byte[] data, int offset, int size) {
		
		FileOutputStream outstream;
		try {
			outstream = context.openFileOutput(name, Context.MODE_PRIVATE);

			DataOutputStream dos = new DataOutputStream(outstream);
			dos.write(data, offset, size);

			dos.flush();
			dos.close();

			// 关闭流
			outstream.close();
		} catch (FileNotFoundException e) {
			L.e("FileService____", "saveToFile Error! -- FileNotFoundException");
		} catch (IOException e) {
			L.e("FileService____", "saveToFile Error! -- IOException");
		}
	}

	/**
	 * 保存文件
	 * 
	 * @param name
	 * @param data
	 * @param size
	 */
	public void saveToFile(String name, byte[] data, int size) {
		
		FileOutputStream outstream;
		try {
			outstream = context.openFileOutput(name, Context.MODE_PRIVATE);

			DataOutputStream dos = new DataOutputStream(outstream);
			dos.write(data, 0, size);

			dos.flush();
			dos.close();

			// 关闭流
			outstream.close();
		} catch (FileNotFoundException e) {
			L.e("FileService____", "saveToFile Error! -- FileNotFoundException");
		} catch (IOException e) {
			L.e("FileService____", "saveToFile Error! -- IOException");
		}
	}
	
	/**
	 * 保存文件不覆盖旧数据
	 * 
	 * @param name
	 * @param data
	 * @param size
	 */
	public void saveToFileAppend(String name, byte[] data, int size) {
		
		FileOutputStream outstream;
		try {
			outstream = context.openFileOutput(name, Context.MODE_PRIVATE+Context.MODE_APPEND);
			DataOutputStream dos = new DataOutputStream(outstream);
			dos.write(data, 0, size);

			dos.flush();
			dos.close();

			// 关闭流
			outstream.close();
		} catch (FileNotFoundException e) {
			L.e("FileService____", "saveToFile Error! -- FileNotFoundException");
		} catch (IOException e) {
			L.e("FileService____", "saveToFile Error! -- IOException");
		}
	}
	
	public DataOutputStream openFile(String name, int mode)
	{
		DataOutputStream dos = null;
		try {
			mOutstream = context.openFileOutput(name, mode);
			dos = new DataOutputStream(mOutstream);
			
		} catch (FileNotFoundException e) {
			L.e("FileService____", "openFile Error! -- FileNotFoundException");
		} 
		return dos;
	}
	
	public void writeFile(DataOutputStream dos, byte[] data, int size)
	{
		if (dos == null) return;

		try {
			dos.write(data, 0, size);
		} catch (IOException e) {
			L.e("FileService____", "writeFile Error! -- IOException");
		}
	}
	
	public void closeFile(DataOutputStream dos)
	{
		if(dos == null) return;
		try {

			dos.flush();
			dos.close();

			// 关闭流
			if(mOutstream != null)
				mOutstream.close();
		} catch (FileNotFoundException e) {
			L.e("FileService____", "closeFile Error! -- FileNotFoundException");
		} catch (IOException e) {
			L.e("FileService____", "closeFile Error! -- IOException");
		}
	}

	/**
	 * 读取文件, 返回-1文件不存在
	 * 
	 * @param name
	 * @param data
	 * @return
	 */
	public int readFile(String name, byte[] data) {
		FileInputStream instream;
		int len = 0;
		byte[] buffer;
		ByteArrayOutputStream stream;

		try {
			instream = context.openFileInput(name);
			buffer = new byte[1024];
			stream = new ByteArrayOutputStream();

			while ((len = instream.read(buffer)) != -1) {
				stream.write(buffer, 0, len);
			}

			stream.close();

			int size = stream.size();
			STD.memcpy(data, stream.toByteArray(), size);

			return size;

		} catch (FileNotFoundException e) {
			L.e("FileService____", "readFile Error! -- FileNotFoundException");
			return -1;
		} catch (IOException e) {
			L.e("FileService____", "readFile Error! -- IOException");
			return -2;
		}
	}

	/**
	 * 获取文件大小，文件不存在返回0
	 * 
	 * @param name
	 * @return
	 */
	public int getFileSize(String name) {
		int size = 0;
		try {			
			FileInputStream instream = context.openFileInput(name);
			size = instream.available();
		} catch (FileNotFoundException e) {
			return -1;
		} catch (IOException e) {
			return -2;
		}
		return size;
	}
	
	/**
	 * copy file
	 * 
	 * @param src,des.
	 * @return true-success;false-fail
	 */
	public boolean copyFile(InputStream inputStream, String des) {

		int len = 0;
		byte[] buffer;
		ByteArrayOutputStream stream;

		try {
			buffer = new byte[1024];
			stream = new ByteArrayOutputStream();

			while ((len = inputStream.read(buffer)) != -1) {
				stream.write(buffer, 0, len);
			}

			stream.close();

			int size = stream.size();
			saveToFile(des, stream.toByteArray(), size);
			return true;
			
		}catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * delete file
	 * 
	 * @param fileName.
	 * @return true-success;false-fail
	 */
	public boolean deleteFile(String fileName) {
		boolean ret = true;
		ret = context.deleteFile(fileName);
		return ret;
		
	}
	
	public static void saveDataToFile(File fileToWrite, String data) {
		saveDataToFile(fileToWrite, data, false);
	}

	public static synchronized void saveDataToFile(File fileToWrite, String data, boolean appand) {
		FileOutputStream fOut = null;
		OutputStreamWriter myOutWriter = null;
		data = EncodingUtils.getString(data.getBytes(), UTF8);
		try {
			if (!fileToWrite.exists()) {
				fileToWrite.createNewFile();
			}

			fOut = new FileOutputStream(fileToWrite, appand);
			myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(data);
			myOutWriter.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			close(fOut);
			close(myOutWriter);
		}
	}

	public static void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
			}
		}
	}

}
