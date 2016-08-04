package com.pengbo.mhdzq.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public final class L {
	public static final boolean DEVELOPEMENT=false;
	public static final boolean enableLog=false;
	public static void i(String MSG){
		if(DEVELOPEMENT){
			Log.i("TTTTT", MSG);
			if (logToFile != null && enableLog) {
				logToFile.writeLogToFile(Log.INFO, "TTTTT", MSG);
			}
		}
	}
	public static void d(String MSG){
		if(DEVELOPEMENT){
			Log.d("TTTTT", MSG);
			if (logToFile != null && enableLog) {
				logToFile.writeLogToFile(Log.DEBUG, "TTTTT", MSG);
			}
		}
	}
	public static void w(String MSG){
		if(DEVELOPEMENT){
			Log.w("TTTTT", MSG);
			if (logToFile != null && enableLog) {
				logToFile.writeLogToFile(Log.WARN, "TTTTT", MSG);
			}
		}
	}
	public static void e(String MSG){
		if(DEVELOPEMENT){
			Log.e("TTTTT", MSG);
			if (logToFile != null && enableLog) {
				logToFile.writeLogToFile(Log.ERROR, "TTTTT", MSG);
			}
		}
	}
	public static void v(String MSG){
		if(DEVELOPEMENT){
			Log.v("TTTTT", MSG);
			if (logToFile != null && enableLog) {
				logToFile.writeLogToFile(Log.VERBOSE, "TTTTT", MSG);
			}
		}
	}
	
	public static void i(String TAG, String MSG){
		if(DEVELOPEMENT){
			Log.i(TAG, MSG);
			if (logToFile != null && enableLog) {
				logToFile.writeLogToFile(Log.INFO, TAG, MSG);
			}
		}
	}
	public static void d(String TAG, String MSG){
		if(DEVELOPEMENT){
			Log.d(TAG, MSG);
			if (logToFile != null && enableLog) {
				logToFile.writeLogToFile(Log.DEBUG, TAG, MSG);
			}
		}
	}
	public static void w(String TAG, String MSG){
		if(DEVELOPEMENT){
			Log.w(TAG, MSG);
			if (logToFile != null && enableLog) {
				logToFile.writeLogToFile(Log.WARN, TAG, MSG);
			}
		}
	}
	public static void e(String TAG, String MSG){
		if(DEVELOPEMENT){
			Log.e(TAG, MSG);
			if (logToFile != null && enableLog) {
				logToFile.writeLogToFile(Log.ERROR, TAG, MSG);
			}
		}
	}
	public static void v(String TAG, String MSG){
		if(DEVELOPEMENT){
			Log.v(TAG, MSG);
			if (logToFile != null && enableLog) {
				logToFile.writeLogToFile(Log.VERBOSE, TAG, MSG);
			}
		}
	}
	
	private static LogToFile logToFile;
	
	static {
		if (DEVELOPEMENT) {
			try {
				logToFile = new LogToFile();
			} catch (RuntimeException e) {
				Log.e("pobo-log", e.toString());
			} catch (IOException e) {
				Log.e("pobo-log", e.toString());
			}	
		}		
	}
	
	private L() {

	}
}

class LogToFile {
	public static final int MAX_FILE_SIZE = 10;// M bytes
	private FileWriter fileWriter;
	
	private String SD_CARD = String.format(
			"%s", Environment.getExternalStorageDirectory());
	private String FILE1 = String.format("%s/demo.log", SD_CARD);
	private String FILE2 = String.format("%s/demo2.log", SD_CARD);
	private String FILE3 = String.format("%s/demo3.log", SD_CARD);

	public LogToFile() throws RuntimeException, IOException {
		File sdcard = new File(SD_CARD);
		File file1 = new File(FILE1);
		File file2 = new File(FILE2);
		File file3 = new File(FILE3);

		if (!sdcard.exists()) {
			throw new RuntimeException("SD card not exists!");
		} else {
			if (!file1.exists()) {
				try {
					file1.createNewFile();
				} catch (IOException e) {
					throw e;
				}
			} else {
				long fileSize = (file1.length() >>> 20);// convert to M bytes
				if (fileSize > MAX_FILE_SIZE) {
					if (!file2.exists()) {
						file1.renameTo(file2);
						file1 = new File(FILE1);
						try {
							file1.createNewFile();
						} catch (IOException e) {
							throw e;
						}
					} else {
						file2.renameTo(file3);
						file2 = new File(FILE2);
						file1.renameTo(file2);
						file1 = new File(FILE1);
						try {
							file1.createNewFile();
						} catch (IOException e) {
							throw e;
						}
					}
				}
			}
			fileWriter = new FileWriter(file1, true);
		}
	}

	// we use one space to separate elements
	public void writeLogToFile(int priority, String tag, String message) {

		Date date = new Date();
		SimpleDateFormat simpleDateFormate = new SimpleDateFormat(
				"yyyy:MM:dd kk:mm:ss.SSS");
		String strLog = simpleDateFormate.format(date);

		StringBuffer sb = new StringBuffer(strLog);
		sb.append(' ');
		sb.append(strPriority[priority]);
		sb.append(' ');
		sb.append(tag);
		sb.append(' ');
		sb.append(message);
		sb.append('\n');
		strLog = sb.toString();

		try {
			fileWriter.write(strLog);
			fileWriter.flush();
		} catch (IOException e) {
			Log.e("LogToFile", "", e);
		}
	}
	
	private static final String strPriority[];
	static {
		strPriority = new String[8];
		strPriority[0] = "";
		strPriority[1] = "";
		strPriority[2] = "verbose";
		strPriority[3] = "debug";
		strPriority[4] = "info";
		strPriority[5] = "warn";
		strPriority[6] = "error";
		strPriority[7] = "ASSERT";
	}
}
