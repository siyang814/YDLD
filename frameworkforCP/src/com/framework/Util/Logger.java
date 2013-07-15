package com.framework.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.StringTokenizer;

import com.framework.Exception.UnhandledExceptionHandler;






/**
 * 日志输出类。hengsing项目强制使用此类进行日志信息的输出。
 * 
 * @author 
 * 
 */
public class Logger
{
	private static final String TAG = Logger.class.getSimpleName();
	
	public static final int ERROR = 1;
	public static final int WARN = 2;
	public static final int INFO = 3;
	public static final int DEBUG = 4;
	public static final int VERBOSE = 5;

	private static int logLevel = ERROR;
	
	private static boolean _bLogging = false;
	private static boolean _bRunning = false;
	protected static Object lock = new Object();
	//this list stores the pending Request's key
	private static LinkedList<String> _pendingLogLines = new LinkedList<String>();

	private static File _logFile = null;
	private static OutputStream _outputStream = null;

	private static boolean _bSystemPrint = false;
	private static boolean _bMutilLog = true;//多日志文件
	
	/**
	 * 日志级别设置。
	 * 
	 * @param level
	 *            低于或等于此级别的日志信息提供输出功能。
	 */
	public static void setLogLevel(int level)
	{
		logLevel = level;
		if (level >= VERBOSE)
		{
			_bSystemPrint = true;
		}
	}

	public static void e(String tag, String msg)
	{
		if (logLevel >= ERROR)
		{
			_log(ERROR, tag, msg);
		}
	}

	public static void w(String tag, String msg)
	{
		if (logLevel >= WARN)
		{
			_log(WARN, tag, msg);
		}
	}

	public static void i(String tag, String msg)
	{
		if (logLevel >= INFO)
		{
			_log(INFO, tag, msg);
		}
	}

	public static void d(String tag, String msg)
	{
		if (logLevel >= DEBUG)
		{
			_log(DEBUG, tag, msg);
		}
	}

	public static void v(String tag, String msg)
	{
		if (logLevel >= VERBOSE)
		{
			_log(VERBOSE, tag, msg);
		}
	}
	
	private static void _log(int lev, String tag, String msg)
	{
//		Date d = new Date();
//		String time = String.format("%d-%d-%d %d:%d:%d", d.getYear() + 1900, d.getMonth() + 1, d.getDate(), d.getHours(), d.getMinutes(), d.getSeconds());
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
		String log = String.format("%s %s %s", time, tag, msg);
		synchronized(lock)
		{
			_pendingLogLines.add(log);
			lock.notifyAll();
		}
	}
	
	/**
	 * 启动日志输出服务
	 * @param file 日志文件
	 */
	public static void start(String file)
	{
		_openLogFile(file);
		_startRecordor();
	}
	
	
	public static void stop()
	{
		_bLogging = false;
		synchronized(lock)
		{
			lock.notifyAll();
		}
		while (_bRunning)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Logger.e(TAG, e.toString());
			}
		}
		_closeLogFile();
	}
	
	private static void _openLogFile(String logfile)
	{
		if (null != _logFile)
			return;
		
		try {
			if (_bMutilLog) {
				String currentTime = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date());
				logfile = logfile + "_" + currentTime + ".log";
			}
			
			_logFile = new File(logfile);
			if(!_logFile.exists())
			{
				String dir = logfile.substring(0, logfile.lastIndexOf("/"));
				createMultiDir(dir, "/");
				_logFile.createNewFile();
			} 
 			_outputStream = new FileOutputStream(_logFile);
		} catch (FileNotFoundException e) {
			Logger.e(TAG, e.toString());
		} catch (IOException e) {
			Logger.e(TAG, e.toString());
		}
	}
	
	/**
	 * 创建多级目录
	 * 
	 * @param dir
	 * @param separator
	 */
	private static void createMultiDir(String dir, String separator)
	{
		StringTokenizer tokenizer = new StringTokenizer(dir, separator);
		String path1 = tokenizer.nextToken() + separator;
		String path2 = path1;
		while (tokenizer.hasMoreTokens())
		{
			path1 = tokenizer.nextToken() + separator;
			path2 += path1;
			File inbox = new File(path2);
			if (!inbox.exists())
				inbox.mkdir();
		}
	}
	
	private static void _startRecordor()
	{
		Thread _recordor = new Thread()
		{
			public void run()
			{
				new UnhandledExceptionHandler();
				
				_bRunning = true;
				while (_bLogging)
				{
					synchronized(lock)
					{
						try
						{
							lock.wait();
						}
						catch(InterruptedException e)
						{
						}
					}

					String line;
					do
					{
						line = null;
						synchronized(lock)
						{
							if (!_pendingLogLines.isEmpty())
							{
								line = _pendingLogLines.removeFirst();
							}
						}
						if (null != line)
						{
							line += "\n";
							_writeLine(line);
						}
					}
					while (null != line);
				}
				_bRunning = false;
			}
		};
		_bLogging = true;
		_recordor.setName("HSLogger");
		_recordor.start();
	}
	
	
	private static void _writeLine(String line)
	{
		byte[] data;
		try {
			if (null != _outputStream)
			{
				data = line.getBytes("UTF-8");
				_outputStream.write(data, 0, data.length);
				_outputStream.flush();
				if (_bSystemPrint)
				{
					System.out.println(line);
					//System.out.println("\n");
				}
			}
		} catch (UnsupportedEncodingException e) {
			Logger.e(TAG, e.toString());
		} catch (IOException e) {
			Logger.e(TAG, e.toString());
		}
	}
	
	
	private static void _closeLogFile()
	{
		try {
			_outputStream.close();
		} catch (IOException e) {
			Logger.e(TAG, e.toString());
		}
	}
}
