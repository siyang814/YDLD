package com.framework.orm;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 
 * @author 
 *         <p>
 *         这个类用于缓存实体，减少对数据库的访问
 *         </p>
 */
class EntitiesMap {
	private static EntitiesMap mEntitiesMap = new EntitiesMap();
	
	private Map<String, WeakReference<BaseModel>> map = new HashMap<String, WeakReference<BaseModel>>();
	WeakHashMap<BaseModel, String> _map = new WeakHashMap<BaseModel, String>();

	static EntitiesMap instance(){
		return mEntitiesMap;
	}
	
	<T extends BaseModel> T get(Class<T> c, long id) {
		String key = makeKey(c, id);
		WeakReference<BaseModel> i = map.get(key);
		if (i == null)
			return null;
		return (T) i.get();
	}

	void set(BaseModel e) {
		String key = makeKey(e.getClass(), e.getID());
		map.put(key, new WeakReference<BaseModel>(e));
	}

	private String makeKey(Class entityType, long id) {
		StringBuilder sb = new StringBuilder();
		sb.append(entityType.getName()).append(id);
		return sb.toString();
	}
}
