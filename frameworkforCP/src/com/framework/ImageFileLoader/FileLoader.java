package com.framework.ImageFileLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.lang.Thread;

import com.framework.R;
import com.framework.Exception.Debugger;
import com.framework.Exception.UnhandledExceptionHandler;
import com.framework.Util.AppCfgManager;
import com.framework.Util.ImagetoRoundCorner;
import com.framework.Util.JavaUtil;
import com.framework.Util.Logger;






import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;




/**
 * ��ȡͼ�����,���õ���ģʽ ͼ��Cache��ģ�飬ʹ��ThreadPoolExecator����ִ�в���ͼ������
 * 
 * @author  May 3, 2011
 */
public class FileLoader extends Thread
{
	private static final String TAG = FileLoader.class.getSimpleName();
	
	public static final int VIEW_TYPE_DEFAULT = 0;
	public static final int VIEW_TYPE_LISTVIEW = 1;
	public static final int VIEW_TYPE_SIMPLELISTVIEW = 2;
	public static final int VIEW_TYPE_GALLERY = 3;
	public static final int VIEW_TYPE_GRIDVIEW = 4;
	public static final int VIEW_TYPE_HEADER_LISTVIEW = 5;
	
	private static List<Task> allTask = new ArrayList<Task>();
	
	private boolean useMemoryCache = false;//�Ƿ�ʹ���ڴ滺��

	private static FileLoader instance;

	private FileLoader()
	{
	}

	public static FileLoader getInstance()
	{
		if (instance == null)
		{
			instance = new FileLoader();
		}
		return instance;
	}

	public static long MEMORY_CACHE_SURVIVAL = 5 * 60 * 1000;// �ڴ滺������ʱ��(����)

	MemoryCache memoryCache = new MemoryCache();	// �ڴ滺�������
	FileSystemCache fileSytstemCache = new FileSystemCache();// �ļ����������

	class Task {
		Bitmap bitmap;
		String url;
		ImageAdapter adapter;
		View view;
		int viewType;
		
		boolean cacheInFile;
		Image image;
		public Task(String url, ImageAdapter adapter, boolean cacheInFile) {
			super();
			this.url = url;
			this.adapter = adapter;
			this.cacheInFile = cacheInFile;
		}
		public Task(Bitmap bitmap, String url, View view, int viewType, boolean cacheInFile) {
			super();
			this.bitmap = bitmap;
			this.url = url;
			this.view = view;
			this.viewType = viewType;
			this.cacheInFile = cacheInFile;
		}
	}
	
	/**
	 * �ص��ӿڣ���ȡ��ͼƬ������ʾ һ��ͼƬ
	 * 
	 * @author  May 3, 2011
	 */
	public interface ImageAdapter
	{
		void display(Image image);
	};
	
	private boolean running = false;
	
	
	public void run() {
		this.setName("FileLoader_run");
		new UnhandledExceptionHandler();
		Task task = null;
		running = true;
		try {
			while (running) {
				synchronized(allTask) {
					if (allTask.size() == 0) {
						allTask.wait();
						continue;
					}
					task = allTask.remove(allTask.size() - 1);	
				}
				
				doWork(task);
			}
		} catch (Exception e) {
			Logger.e(TAG, JavaUtil.getDetailFromThrowable(e));
		} catch (Error er) {
			cancelAllTask();
			Logger.e(TAG, JavaUtil.getDetailFromThrowable(er));
		}
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Task task = (Task)msg.obj;
			if (task != null) {
				if (task.view != null) {
					switch (task.viewType) {
					case VIEW_TYPE_LISTVIEW :
						((BaseAdapter)((ListView)task.view).getAdapter()).notifyDataSetChanged();
						//((BaseAdapter)((HeaderViewListAdapter)task.view.getAdapter()).notifyDataSetChanged();
					
						break;
					case VIEW_TYPE_HEADER_LISTVIEW :
						HeaderViewListAdapter listAdapter = (HeaderViewListAdapter)(((ListView)task.view).getAdapter());
						((BaseAdapter)listAdapter.getWrappedAdapter()).notifyDataSetChanged();
						break;
						
					case VIEW_TYPE_GALLERY :
						((BaseAdapter)((Gallery)task.view).getAdapter()).notifyDataSetChanged();
						break;
					case VIEW_TYPE_GRIDVIEW :
						((BaseAdapter)((GridView)task.view).getAdapter()).notifyDataSetChanged();
						break;
					default :
						Debugger.Assert(false, "view type is wrong!!!");
					}
				} else {
					if (task.adapter != null)
					task.adapter.display(task.image);
				}
			}
			super.handleMessage(msg);
		}
		
	};
	
	private void doWork(final Task task) {
		final String md5 = parseMd5FromUrl(task.url);
		final Image image = new Image();
		
		Bitmap bitmap = this.getBitmapImmediately(md5, task.cacheInFile);
		if (bitmap != null) {
			image.setBitmap(bitmap);
			image.setMd5(md5);
			image.setUrl(task.url);
			task.image = image;
			Message msg = new Message();
			msg.obj = task;
			handler.sendMessage(msg);
			Logger.d(TAG, "load Immediately: url=" + task.url);
			return;
		}
		
		byte[] bytes = null;
		bytes = getBytesFromNetwork(task.url);// ����url�������������ȡͼƬ
		Bitmap tmpBitmap = _decodeBitmap(bytes);
		if (task.cacheInFile && null != bytes && bytes.length != 0) {
			Logger.d(TAG, "load from network: url=" + task.url);
			fileSytstemCache.save(md5, bytes);
		}

		if (null != tmpBitmap)
		{
			if (task.view != null) {
				task.bitmap = tmpBitmap;
				Message msg = handler.obtainMessage();
				msg.obj = task;
				handler.sendMessage(msg);
			}
			
			if (useMemoryCache) {
				memoryCache.save(md5, tmpBitmap, MEMORY_CACHE_SURVIVAL);// ��ӵ��ڴ滺��
			}
		}
		
		Bitmap bitmap2 = this.getBitmapImmediately(md5, task.cacheInFile);
		if (bitmap2 != null) {
			image.setBitmap(bitmap2);
			image.setMd5(md5);
			image.setUrl(task.url);
			task.image = image;
			Message msg = new Message();
			msg.obj = task;
			handler.sendMessage(msg);
			return;
		}
	}

	private Bitmap _decode(byte[] bytes, BitmapFactory.Options opts)
	{
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		} catch (Exception e) {
			Logger.e(TAG, JavaUtil.getDetailFromThrowable(e));
			return null;
		} catch (Error err) {
			Logger.e(TAG, JavaUtil.getDetailFromThrowable(err));
			return null;
		}
		return bitmap;
	}
	
	protected Bitmap _decodeBitmap(byte[] bytes)
	{
		if (null == bytes)
			return null;
		
		//parse 1
		Bitmap bitmap = null;
		bitmap = _decode(bytes, null);
		if (null != bitmap)
			return bitmap;
		
		//parse 2
		//try to alloc temp-memeory
		BitmapFactory.Options opts = null;
		
		opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		
		int width = opts.outWidth;
		int height = opts.outHeight;
		
		opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		while ((width > 512 || height > 512) && (width > 16 && height > 16))
		{
			opts.inSampleSize <<= 1;
			width >>= 1;
			height >>= 1;
		}
		opts.inTempStorage = new byte[32 * 1024]; //��ʱ�洢�ռ�, 16K is recommenced
		bitmap = _decode(bytes, opts);

		//if (null != bitmap)
		//	return bitmap;
		
		//even it is null
		return bitmap;
	}
	
	private Bitmap _decode(String pathName, BitmapFactory.Options opts)
	{
		Bitmap bitmap = null;
		try {
//			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
			bitmap = BitmapFactory.decodeFile(pathName, opts);
		} catch (Exception e) {
			Logger.e(TAG, JavaUtil.getDetailFromThrowable(e));
			return null;
		} catch (Error err) {
			Logger.e(TAG, JavaUtil.getDetailFromThrowable(err));
			return null;
		}
		return bitmap;
	}
	
	protected Bitmap _decodeBitmap(String pathName)
	{
		if (null == pathName || "".equals(pathName.trim()))
			return null;
		
		//parse 1
		Bitmap bitmap = null;
		bitmap = _decode(pathName, null);
		if (null != bitmap)
			return bitmap;
		
		//parse 2
		//try to alloc temp-memeory
		BitmapFactory.Options opts = null;
		
		opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		
//		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		BitmapFactory.decodeFile(pathName, opts);
		
		int width = opts.outWidth;
		int height = opts.outHeight;
		
		opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		while ((width > 512 || height > 512) && (width > 16 && height > 16))
		{
			opts.inSampleSize <<= 1;
			width >>= 1;
			height >>= 1;
		}
		opts.inTempStorage = new byte[32 * 1024]; //��ʱ�洢�ռ�, 16K is recommenced
		bitmap = _decode(pathName, opts);

		//if (null != bitmap)
		//	return bitmap;
		
		//even it is null
		return bitmap;
	}
	
	public Bitmap getBitmapByUrl(String url, View view, int viewType) {
		String md5 =  (url);
		Log.d("TAG","url"+url);
		Log.d("TAG","md5"+md5);
		Bitmap bitmap = this.getBitmapImmediately(md5, true);
		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(AppCfgManager.getApplication().getResources(), R.drawable.ic_launcher);
			if (md5 != null && !"".equals(md5.trim())) {
				Task task = new Task(bitmap, url, view, viewType, true);
				synchronized (allTask) {
					allTask.add(task);
					allTask.notify();
				}
			}
		} 
		return bitmap;
	}
	

	/**
	 * ����url���ӷ�������ȡͼƬ��ʾ��������View�� ��ȡͼƬ˳���ڴ滺�桢�ļ����桢������
	 * 
	 * @param url
	 * @param view
	 * @param adapter
	 *            �������Լ�ʵ�ֵ�ImageAdapter�ص��ӿ�
	 */
	public void load(String url, ImageAdapter adapter, boolean cacheInFile)
	{
		Logger.d(TAG, "load() url=" + url);
		
		Task task = new Task(url, adapter, cacheInFile);
		synchronized (allTask) {
			allTask.add(task);
			allTask.notify();
		}
	}
	
	/**
	 * ����url���ӷ�������ȡͼƬ��ʾ��������View�� ��ȡͼƬ˳���ڴ滺�桢�ļ����桢������
	 * 
	 * @param url
	 * @param view
	 * @param cacheInFile
	 *            �Ƿ�֧���ļ�����
	 */
	public void load(String url, ImageView imageView, boolean cacheInFile)
	{
		Debugger.Assert(url != null && !"".equals(url), "url is null or length is 0");
		Debugger.Assert(imageView != null, "your view is null");
		
		final ImageView view = imageView;
		load(url, new ImageAdapter()
		{
			@Override
			public void display(Image image)
			{
				Bitmap bitmap = ImagetoRoundCorner.toRoundCorner(image.getBitmap(), 15);
				view.setImageBitmap(bitmap);
				view.invalidate();
			}
		}, cacheInFile);
	}
	
	public void load(String url, ProgressBar progressbar, ImageView imageView, boolean cacheInFile)
	{
		Debugger.Assert(url != null && !"".equals(url), "url is null or length is 0");
		Debugger.Assert(imageView != null, "your view is null");
		final ProgressBar _progressbar  = progressbar;
		final ImageView view = imageView;
		load(url, new ImageAdapter()
		{
			@Override
			public void display(Image image)
			{
				view.setImageBitmap(image.getBitmap());
				
				view.setVisibility(View.VISIBLE);
				_progressbar.setVisibility(View.GONE);
				view.invalidate();
			}
		}, cacheInFile);
	}
	
	
	/**
	 * ͬ����ȡͼƬbitmap���ڴ���ļ���
	 * @return Bitmap
	 */
	protected Bitmap getBitmapImmediately(String md5, boolean cacheInFile) {
		if (md5 == null || "".equals(md5.trim())) return null;
		Bitmap bitmap = null;
		byte[] bytes = null;
		if (useMemoryCache) {
			bitmap = (Bitmap)(memoryCache.get(md5));// ���ڴ滺���ȡͼƬ
		}
		if (null == bitmap)
		{
			if (cacheInFile)
			{
				try {
//					bitmap = BitmapFactory.decodeStream(fileSytstemCache.getAsInputStream(md5));
					bitmap = _decodeBitmap(FileSystemCache.FILE_CACHE_DIR + md5);
					if (useMemoryCache) {
						memoryCache.save(md5, bitmap, MEMORY_CACHE_SURVIVAL);//���浽�ڴ滺��
					}
					
				} catch (Exception e) {
					Logger.e(TAG, JavaUtil.getDetailFromThrowable(e));
				} catch (Error er) {
					this.cancelAllTask();
					Logger.e(TAG, JavaUtil.getDetailFromThrowable(er));
				}
			}
		}
		return bitmap;
	}
	

	/**
	 * �첽��ȡͼƬ�ֽ����飨���磩
	 * @return byte[]
	 */
	public byte[] getBytesFromNetwork(String url)
	{
		if (url == null || "".equals(url.trim())) return null;
			
		byte[] bytes = null;
		URL httpUrl = null;
		URLConnection conn = null;
		InputStream in = null;
		ByteArrayOutputStream baos = null;
		try
		{
			httpUrl = new URL(url);
			conn = httpUrl.openConnection();
			in = conn.getInputStream();
			byte[] buffer;
			int len;
			buffer = new byte[1024];
			len = 0;
			baos = new ByteArrayOutputStream();
			while ((len = in.read(buffer)) > 0)
			{
				baos.write(buffer, 0, len);
				Thread.sleep(10);
			}
			bytes = baos.toByteArray();
		}
		catch (MalformedURLException e)
		{
			Logger.e(TAG, "url:" + url + "\n" + JavaUtil.getDetailFromThrowable(e));
			return null;
		}
		catch (Exception e)
		{
			Logger.e(TAG, "url:" + url + "\n" + JavaUtil.getDetailFromThrowable(e));
			return null;
		}
		catch (Error e)
		{
			Logger.e(TAG, "url:" + url + "\n" + JavaUtil.getDetailFromThrowable(e));
			return null;
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
					in = null;
				}
				if (baos != null)
				{
					baos.close();
					baos = null;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		return bytes;
	}
	
	
	
	/**
	 * ͨ��url�����ļ�������md5
	 * 
	 * @param url
	 * @return
	 */
	public static String parseMd5FromUrl(String url)
	{
		Debugger.Assert((url != null && !"".equals(url.trim()) && url.matches("[a-zA-z]+://[^\\s]*/[^\\s]+.[a-zA-z]+")), "your url is illegal: url=" + url);
		if (url == null || "".equals(url.trim())) return "";
		
		String md5 = "";
		try
		{
			String urlStr = url.substring(url.lastIndexOf("/")+1);
			if(!"null".equals(urlStr)&&urlStr.contains(".")){
				md5 = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
			}else{
					//return "";
				md5 = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("?"));
				//	System.out.println(md5);
			}

	    /*  if(!"null".equals(url.substring(url.lastIndexOf("/")+1))){
			md5 = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
			}else{
				return "";
			}*/
		}
		catch (Throwable e)
		{
			Logger.e(TAG, "url:" + url + "\n" + JavaUtil.getDetailFromThrowable(e));
			return "";
		}
		return md5;
	}

	public void cancelAllTask() {
		synchronized (allTask) {
			allTask.clear();
		}
	}
	
	public void exitRun() {
		running = false;
		synchronized (allTask) {
			allTask.notify();
		}
		this.interrupt();
	}

}
