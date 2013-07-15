package com.framework.ImageFileLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * �ڴ滺������� ���õ���ģʽ
 * 
 * @author  May 3, 2011
 */
public class MemoryCache
{
	public MemoryCache()
	{
	}

//	private static MemoryCache instance;
//
//	public static MemoryCache getInstance()
//	{
//		if (instance == null)
//		{
//			instance = new MemoryCache();
//		}
//		return instance;
//	}

	/**
	 * ������󣬴�����ʱ�������ʱ��
	 * 
	 * @author yanghx May 4, 2011
	 */
	private class CachedObject
	{
		public long timeCached; // ����ʱ��
		public long timeSurvival;// ����ʱ��
		public Object obj;// �����ڴ����

		public boolean isExpired()// �Ƿ����
		{
			long diff = System.currentTimeMillis() - timeCached;
			if (diff > timeSurvival)
			{
				return true;
			}
			return false;
		}
	}

	// ʹ��WeakHashMapά�����������������ڴ����״�����������ᱻ��������
//	private static WeakHashMap<String, CachedObject> weakHashMap = new WeakHashMap<String, CachedObject>();
	private WeakHashMap<String, CachedObject> weakHashMap = new WeakHashMap<String, CachedObject>();

	/**
	 * ���ڴ滺���л�ȡkeyֵ�Ļ������
	 * 
	 * @param key
	 *            ��ֵ
	 * @return �������CachedObject
	 */
	public Object get(String key)
	{
		CachedObject cachedObject = weakHashMap.get(key);
		if (cachedObject == null)
		{
			return null;
		}
		if (cachedObject.isExpired())
		{
			weakHashMap.remove(key);
			return null;
		}
		return cachedObject.obj;
	}

	/**
	 * ��ȡ������󼯺�
	 * 
	 * @param keys
	 *            ��ֵ����
	 * @return ������󼯺�
	 */
	public List<Object> get(List<String> keys)
	{
		List<Object> list = new ArrayList<Object>();
		for (String key : keys)
		{
			Object object = this.get(key);
			list.add(object);
		}
		return list;
	}

	/**
	 * ��������ڴ滺��
	 * 
	 * @param key
	 *            ��ֵ
	 * @param cachedObject
	 *            �������
	 * @return �������
	 */
	public Object save(String key, Object object, long life)
	{
		if (null == object)
			return null;
		
		CachedObject cachedObject = new CachedObject();
		cachedObject.timeCached = System.currentTimeMillis();
		cachedObject.timeSurvival = life;
		cachedObject.obj = object;
		synchronized (weakHashMap)
		{
			weakHashMap.put(key, cachedObject);
		}
		return object;
	}

	public void clear()
	{
		weakHashMap.clear();
	}
	
	/**
	 * ���ڴ滺����ɾ��ָ��keyֵ�Ļ������
	 * 
	 * @param key
	 *            ��ֵ
	 * @return ɾ���Ļ������
	 */
	public Object delete(String key)
	{
		CachedObject cachedObject = weakHashMap.get(key);
		if (cachedObject == null)
		{
			return null;
		}
		synchronized (weakHashMap)
		{
			weakHashMap.remove(key);
		}
		return cachedObject;
	}

	/**
	 * �ж��ڴ滺�����Ƿ����ָ��key�Ļ������
	 * 
	 * @param key
	 *            �����ֵ����
	 * @return ���ڷ���true�����򷵻�false
	 */
	public boolean exists(String key)
	{
		if (!weakHashMap.containsKey(key))
		{
			return false;
		}
		else if (weakHashMap.get(key) == null)
		{
			return false;
		}
		else if (weakHashMap.get(key).obj == null)
		{
			return false;
		}
		return true;
	}

}
