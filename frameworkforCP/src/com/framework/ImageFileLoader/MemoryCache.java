package com.framework.ImageFileLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * 内存缓存操作类 采用单例模式
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
	 * 缓存对象，带缓存时间和生存时间
	 * 
	 * @author yanghx May 4, 2011
	 */
	private class CachedObject
	{
		public long timeCached; // 缓存时间
		public long timeSurvival;// 生存时间
		public Object obj;// 缓存内存对象

		public boolean isExpired()// 是否过期
		{
			long diff = System.currentTimeMillis() - timeCached;
			if (diff > timeSurvival)
			{
				return true;
			}
			return false;
		}
	}

	// 使用WeakHashMap维护缓存对象，如果发生内存回收状况，缓存对象会被立即回收
//	private static WeakHashMap<String, CachedObject> weakHashMap = new WeakHashMap<String, CachedObject>();
	private WeakHashMap<String, CachedObject> weakHashMap = new WeakHashMap<String, CachedObject>();

	/**
	 * 从内存缓存中获取key值的缓存对象
	 * 
	 * @param key
	 *            键值
	 * @return 缓存对象CachedObject
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
	 * 获取缓存对象集合
	 * 
	 * @param keys
	 *            键值集合
	 * @return 缓存对象集合
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
	 * 保存对象到内存缓存
	 * 
	 * @param key
	 *            键值
	 * @param cachedObject
	 *            缓存对象
	 * @return 缓存对象
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
	 * 从内存缓存中删除指定key值的缓存对象
	 * 
	 * @param key
	 *            键值
	 * @return 删除的缓存对象
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
	 * 判断内存缓存中是否存在指定key的缓存对象
	 * 
	 * @param key
	 *            缓存键值对象
	 * @return 存在返回true，否则返回false
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
