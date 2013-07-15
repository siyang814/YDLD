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
 * �ļ����������
 * 
 * @author  May 3, 2011
 */
public class FileSystemCache
{
	

	private static final String TAG = FileSystemCache.class.getSimpleName();

	public static String FILE_CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AppName/cache/";// �ļ�����Ŀ¼
	private static final String FILE_CACHE_NONE_SDCARD_DIR = "/data/data/"+AppCfgManager.getPackageName()+"/cache/";//�ֻ�ûSD��ʱ���ļ�����Ŀ¼

	/*-----------------------------�����������----------------------------*/
	private static final int MAX_CACHE_FILE_NUM = 2000;// ��󻺴��ļ�����
	private static final int DELETE_NUM_MIN = 50;// ��Сɾ��50��
	private static final int DELETE_NUM_MAX = 1500;// ���ɾ��1500��

	private int cacheFileNum;// �����ļ�������
	private TreeMap<Long, File> cachedFiles = new TreeMap<Long, File>();// ������ļ�����,���ļ�����޸�ʱ����������
	
	public FileSystemCache() {
		String status = Environment.getExternalStorageState(); 
		if(!status.equals(Environment.MEDIA_MOUNTED)) {
			FILE_CACHE_DIR = FILE_CACHE_NONE_SDCARD_DIR;
		}
	}

	/**
	 * ���ļ������ж�ȡָ���ļ�����������
	 * 
	 * @param name
	 *            �ļ���
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
	 * ���ļ������л�ȡָ���ļ�����ͼƬ
	 * 
	 * @param name
	 *            ͼƬ�ļ�����
	 * @return ͼƬ�ļ�bitmap
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
	 * ���ļ������л�ȡָ���ļ����ļ�
	 * 
	 * @param name
	 *            ָ���ļ���
	 * @return �ļ��ֽ�����
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
	 * �����༶Ŀ¼
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
	 * �����ļ����ļ�����
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

		createMultiDir(FILE_CACHE_DIR, "/");// �����༶Ŀ¼
		OutputStream outputStream = null;
		String file = FILE_CACHE_DIR + name;
		boolean bSaved = false;
		try
		{
			outputStream = new FileOutputStream(file);
			//���÷ֲ��洢��������Thread.sleep
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
	 * ���ļ�������ɾ��ָ���ļ�����ͼƬ
	 * 
	 * @param name
	 *            ͼƬ�ļ�����
	 * @return ɾ����ͼƬ�ļ��ɹ�����true,ɾ��ʧ���򷵻�false
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
	 * �����ļ����棬�����˳�ʱ���� 1.��Сɾ50�������ɾ1500�� 2.ɾ������Ϊ:50 < (cacheFileNum-MAX_CACHE_FILE_NUM) * 2 <1500
	 * �磺2.1 �ļ�����Ϊ2300������ɾ��600����ʣ1700����ԭ������Ǿ���ԭ�� 2.2 �ļ�����Ϊ2010������ɾ��50��������ɾ��10*2�� 2.3
	 * �ļ�����Ϊ3000������ɾ��1500��������ɾ��1000*2��
	 */
	public int cleanupCache()
	{
		int successDeleteNum = 0;// �ɹ�ɾ���ļ��ĸ���
		int delta = getCacheFileNum() - MAX_CACHE_FILE_NUM;// ������󻺴��ļ������Ķ��ٸ�

		// ���������󻺴��ļ���������ʼ���Զ�������������ļ�
		if (delta > 0)
		{
			int deleteNum = delta * 2;// ���������ļ�����
			deleteNum = deleteNum < 50 ? 50 : deleteNum;// ��Сɾ50��
			deleteNum = deleteNum > 1500 ? 1500 : deleteNum;// ���ɾ1500��

			// ��ȡ�����ļ���������lastModifiedʱ�������������

			File cacheDir = new File(FILE_CACHE_DIR);
			cachedFiles.clear();
			cachedFiles = _getFilesFromDir(cacheDir);// ��ȡ���л����ļ�

			// ɾ�������Ļ����ļ�������ǰdeleteNum���ļ�
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
	 * ��ȡĿ¼�����е��ļ�
	 * 
	 * @param dir
	 *            Ŀ¼
	 * @return �ļ�����
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
					_getFilesFromDir(file);// �ݹ����
				}
			}
		}
		return cachedFiles;
	}

	/**
	 * ��ȡ�����ļ��ܸ���
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
	 * ��ȡĿ¼���ļ�������
	 * 
	 * @param dir
	 *            Ŀ¼
	 * @return �ļ�������
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
					_getFileNum(file);// �ݹ����
				}
			}
		}
		return cacheFileNum;
	}

	/**
	 * �ж��ļ��������Ƿ����ָ���ļ�����ͼƬ
	 * 
	 * @param name
	 *            ͼƬ�ļ�����
	 * @return ���ڷ���true�����򷵻�false
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
