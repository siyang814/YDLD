package com.framework.ImageFileLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.framework.Exception.Debugger;
import com.framework.Util.AppCfgManager;
import com.framework.Util.JavaUtil;
import com.framework.Util.Logger;




import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;




/**
 * 文件缓存操作类
 * 
 * @author  May 3, 2011
 */
public class FileSystemCache
{
	

	private static final String TAG = FileSystemCache.class.getSimpleName();

	public static String FILE_CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AppName/cache/";// 文件缓存目录
	private static final String FILE_CACHE_NONE_SDCARD_DIR = "/data/data/"+AppCfgManager.getPackageName()+"/cache/";//手机没SD卡时的文件缓存目录

	/*-----------------------------缓存清理参数----------------------------*/
	private static final int MAX_CACHE_FILE_NUM = 2000;// 最大缓存文件数量
	private static final int DELETE_NUM_MIN = 50;// 最小删除50个
	private static final int DELETE_NUM_MAX = 1500;// 最大删除1500个

	private int cacheFileNum;// 缓存文件总数量
	private TreeMap<Long, File> cachedFiles = new TreeMap<Long, File>();// 缓存的文件集合,按文件最后修改时间升序排序
	
	public FileSystemCache() {
		String status = Environment.getExternalStorageState(); 
		if(!status.equals(Environment.MEDIA_MOUNTED)) {
			FILE_CACHE_DIR = FILE_CACHE_NONE_SDCARD_DIR;
		}
	}

	/**
	 * 从文件缓存中读取指定文件名的输入流
	 * 
	 * @param name
	 *            文件名
	 * @return
	 */
	public InputStream getAsInputStream(String name)
	{
		Debugger.Assert(name != null && !"".equals(name), "your name is null or length=0");
		if (name == null || "".equals(name)) {
			return null;
		}

		InputStream inputStream = null;
		try
		{
			File file = new File(FILE_CACHE_DIR + name);
			if (file.exists() && file.length() != 0)
			{
				inputStream = new FileInputStream(file);
				file.setLastModified(System.currentTimeMillis());// if file exists, touch this
																	// file
			}
		}
		catch (FileNotFoundException e)
		{
			Logger.e(TAG, e.toString());
			return null;
		}
		return inputStream;
	}

	/**
	 * 从文件缓存中获取指定文件名的图片
	 * 
	 * @param name
	 *            图片文件名称
	 * @return 图片文件bitmap
	 */
	public Bitmap getAsBitmap(String name)
	{
		Debugger.Assert(name != null && !"".equals(name), "your name is null or length=0");
		if (name == null || "".equals(name)) {
			return null;
		}

		InputStream inputStream = this.getAsInputStream(name);
		if (inputStream == null)
			return null;
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		try
		{
			inputStream.close();
			inputStream = null;
		}
		catch (IOException e)
		{
			Logger.e(TAG, e.toString());
			return bitmap;
		}
		return bitmap;
	}

	/**
	 * 从文件缓存中获取指定文件名文件
	 * 
	 * @param name
	 *            指定文件名
	 * @return 文件字节数组
	 */
	public byte[] getAsByteArray(String name)
	{
		byte[] bytes = null;
		InputStream in = getAsInputStream(name);
		if (in == null)
		{
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[1024];
		try
		{
			while ((len = in.read(buffer)) > 0)
			{
				baos.write(buffer, 0, len);
			}
			baos.flush();
			bytes = baos.toByteArray();
		}
		catch (IOException e)
		{
			Logger.e(TAG, e.toString());
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
	 * 创建多级目录
	 * 
	 * @param dir
	 * @param separator
	 */
	public void createMultiDir(String dir, String separator)
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


	/**
	 * 保存文件到文件缓存
	 * 
	 * @param name
	 * @param bytes
	 * @return
	 * @throws Exception 
	 */
	public byte[] save(String name, byte[] bytes)
	{
		Debugger.Assert(name != null && !"".equals(name), "your name is null or length=0");
		Debugger.Assert(bytes != null && bytes.length != 0, "your bitmap is null or length=0");
		if (name == null || "".equals(name) || bytes == null || bytes.length == 0) {
			return null;
		}

		createMultiDir(FILE_CACHE_DIR, "/");// 创建多级目录
		OutputStream outputStream = null;
		String file = FILE_CACHE_DIR + name;
		boolean bSaved = false;
		try
		{
			outputStream = new FileOutputStream(file);
			//采用分步存储，并增加Thread.sleep
			int offset = 0;

			while (offset < bytes.length) {
				int count = Math.min(1024, bytes.length - offset);
				outputStream.write(bytes, offset, count);
				outputStream.flush();
				offset += count;
				Thread.sleep(10);
			}
			outputStream.close();
			bSaved = true;
		}
		catch (FileNotFoundException e)
		{
			Logger.e(TAG, JavaUtil.getDetailFromThrowable(e));
			bytes = null; 
		}
		catch (IOException e)
		{
			Logger.e(TAG, JavaUtil.getDetailFromThrowable(e));
			bytes = null;
		} catch (InterruptedException e) {
			Logger.e(TAG, JavaUtil.getDetailFromThrowable(e));
			bytes = null;
		}
		finally
		{
			if (!bSaved)
			{
				try {
					File f = new File(file);
					if (f.exists())
						f.delete();
					bytes = null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return bytes;
	}

	/**
	 * 从文件缓存中删除指定文件名的图片
	 * 
	 * @param name
	 *            图片文件名称
	 * @return 删除的图片文件成功返回true,删除失败则返回false
	 */
	public boolean delete(String name)
	{
		Debugger.Assert(name != null && !"".equals(name), "your name is null or length=0");
		if (name == null || "".equals(name)) {
			return false;
		}

		boolean success = false;
		File file = new File(FILE_CACHE_DIR + name);
		if (file != null && file.exists())
		{
			success = file.delete();
		}
		return success;
	}

	/**
	 * 清理文件缓存，程序退出时调用 1.最小删50个，最大删1500个 2.删除个数为:50 < (cacheFileNum-MAX_CACHE_FILE_NUM) * 2 <1500
	 * 如：2.1 文件个数为2300个，则删除600个，剩1700个，原来这就是镜像原理啊 2.2 文件个数为2010个，则删除50个，不是删除10*2个 2.3
	 * 文件个数为3000个，则删除1500个，不是删除1000*2个
	 */
	public int cleanupCache()
	{
		int successDeleteNum = 0;// 成功删除文件的个数
		int delta = getCacheFileNum() - MAX_CACHE_FILE_NUM;// 超出最大缓存文件数量的多少个

		// 如果超出最大缓存文件数量，开始按自定义策略清理缓存文件
		if (delta > 0)
		{
			int deleteNum = delta * 2;// 待清理缓存文件个数
			deleteNum = deleteNum < 50 ? 50 : deleteNum;// 最小删50个
			deleteNum = deleteNum > 1500 ? 1500 : deleteNum;// 最大删1500个

			// 获取所有文件，并按照lastModified时间进行升序排序

			File cacheDir = new File(FILE_CACHE_DIR);
			cachedFiles.clear();
			cachedFiles = _getFilesFromDir(cacheDir);// 获取所有缓存文件

			// 删除排序后的缓存文件集合中前deleteNum个文件
			Set<Long> set = cachedFiles.keySet();
			Iterator<Long> it = set.iterator();
			while (it.hasNext() && deleteNum > 0)
			{
				Object key = it.next();
				Object objValue = cachedFiles.get(key);
				File tempFile = (File) objValue;
				if (tempFile.delete())
					successDeleteNum++;
				deleteNum--;
			}
		}
		return successDeleteNum;
	}

	/**
	 * 获取目录下所有的文件
	 * 
	 * @param dir
	 *            目录
	 * @return 文件集合
	 */
	private TreeMap<Long, File> _getFilesFromDir(File dir)
	{
		if (dir.exists() && dir.isDirectory())
		{
			File[] files = dir.listFiles();
			for (File file : files)
			{
				if (file.isFile())
				{
					cachedFiles.put(file.lastModified(), file);
				}
				else if (file.isDirectory())
				{
					_getFilesFromDir(file);// 递归调用
				}
			}
		}
		return cachedFiles;
	}

	/**
	 * 获取缓存文件总个数
	 * 
	 * @return
	 */
	public int getCacheFileNum()
	{
		cacheFileNum = 0;
		File cacheDir = new File(FILE_CACHE_DIR);
		cacheFileNum = _getFileNum(cacheDir);
		return cacheFileNum;
	}

	/**
	 * 获取目录下文件总数量
	 * 
	 * @param dir
	 *            目录
	 * @return 文件总数量
	 */
	private int _getFileNum(File dir)
	{
		if (dir.exists() && dir.isDirectory())
		{
			File[] files = dir.listFiles();
			for (File file : files)
			{
				if (file.isFile())
				{
					cacheFileNum++;
				}
				else if (file.isDirectory())
				{
					_getFileNum(file);// 递归调用
				}
			}
		}
		return cacheFileNum;
	}

	/**
	 * 判断文件缓存中是否存在指定文件名的图片
	 * 
	 * @param name
	 *            图片文件名称
	 * @return 存在返回true，否则返回false
	 */
	public boolean exists(String name)
	{
		Bitmap bitmap = this.getAsBitmap(name);
		if (bitmap == null)
		{
			return false;
		}
		return true;
	}

	
}
