/**
 * 
 */
package com.framework.orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.framework.Exception.EvtLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;



/**
 * sqlite 数据管理器
 * 
 * @author 
 * 
 */
public abstract class DataManager {

	private static final String OBJECT_CAN_NOT_NULL = "插入的对象不能为空。";
	private static final String ID_CAN_NOT_NULL = "使用Update方法时，_id必须有值";
	private static final String WHERE_CLAUSE_CAN_NOT_NULL = "更新条件不能为空，否则会更新整个数据库。";
	private static final String ID_WHERE_CLAUSE = "_id = ?";
	private static final String COLUMN_NOT_EXISITS = "指定的列不存在";
	private static final String SET_DATABASE_FIRST = "Set database first";

	private Context mContext;
	private String mDBName;
	private int mDBVersion;
	private DatabaseBuilder mDatabaseBuilder;
	public Database mDatabase;

	/**
	 * 初始化数据库
	 * 
	 * @param context
	 *            当前上下文
	 * @param dbName
	 *            数据库名称
	 * @param dbVersion
	 *            数据库版本
	 * @param databaseBuilder
	 *            数据库表的描述类
	 */
	protected DataManager(Context context, String dbName, int dbVersion, DatabaseBuilder databaseBuilder) {
		this.mContext = context;
		this.mDBName = dbName;
		this.mDBVersion = dbVersion;
		this.mDatabaseBuilder = databaseBuilder;

		setDatabase();
	}

	protected void setDatabase() {
		if (mDatabase == null) {
			mDatabase = new Database(mContext, mDBName, mDBVersion, mDatabaseBuilder);
		}
	}

	/**
	 * 开启事务。该方法会调用open方法，自动打开数据库连接
	 */
	public void beginTransaction() {
		// open();
		mDatabase.beginTransaction();
	}

	/**
	 * 结束事务。该方法会调用close方法，自动关闭数据库连接
	 */
	public void endTransaction() {
		mDatabase.endTransaction();
		// close();
	}

	/**
	 * 回滚事务
	 */
	public void rollBack() {
		mDatabase.rollTransaction();
		// close();
	}

	public void open() {
		// if(!mDatabase.isOpen()){
		// mDatabase.open();
		// }
	}

	public void close() {
		// mDatabase.close();
	}

	public void firstOpen() {
		mDatabase.open();
	}

	public void lastClose() {
		mDatabase.close();
	}

	/**
	 * 根据指定的条件，获取单个实体。如果存在多条符合条件的记录，则只返回第一条记录
	 * 
	 * @param <T>
	 *            实体类型
	 * @param type
	 *            指定的实体类型
	 * @param whereClause
	 *            查询条件
	 * @param whereArgs
	 *            查询参数，他会按照顺序替换 whereClause 中间的？号
	 * @return 返回单个实体。如果存在多条符合条件的记录，则只返回第一条记录
	 * @throws DataAccessException
	 *             数据库访问异常
	 */
	public <T extends BaseModel> T get(Class<T> type, String whereClause, String[] whereArgs)
			throws DataAccessException {
		if (mDatabase == null) {
			throw new DataAccessException(SET_DATABASE_FIRST);
		}
		T entity = null;

		Cursor c = null;
		try {
			c = mDatabase.query(false, Utils.getTableName(type), null, whereClause, whereArgs, null, null, null, "1");
			if (c.moveToNext()) {
				entity = type.newInstance();
				entity = Utils.inflate(c, entity);
			}
		} catch (IllegalAccessException e) {
			throw new DataAccessException(e.getLocalizedMessage());
		} catch (InstantiationException e) {
			throw new DataAccessException(e.getLocalizedMessage());
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}

		return entity;
	}

	/**
	 * 根据指定的条件，获取实体的列表
	 * 
	 * @param <T>
	 *            泛型实体类，必须是BaseModel的子类
	 * @param type
	 *            指定的实体类型
	 * @param whereClause
	 *            查询条件
	 * @param whereArgs
	 *            查询参数，他会按照顺序替换 whereClause 中间的？号
	 * @return 返回实体列表
	 * @throws DataAccessException
	 *             数据库访问异常
	 */
	public <T extends BaseModel> List<T> getList(Class<T> type, String whereClause, String[] whereArgs)
			throws DataAccessException {
		return getList(type, false, whereClause, whereArgs, null, null, null, null);
	}

	/**
	 * 根据指定的条件，获取实体的列表
	 * 
	 * @param <T>
	 *            泛型实体类，必须是BaseModel的子类
	 * @param type
	 *            指定的实体类型
	 * @param whereClause
	 *            查询条件
	 * @param whereArgs
	 *            查询参数，他会按照顺序替换 whereClause 中间的？号
	 * @param orderBy
	 *            排序字段
	 * @param limit
	 *            取数据的条数
	 * @return 返回实体列表
	 * @throws DataAccessException
	 *             数据库访问异常
	 */
	public <T extends BaseModel> List<T> getList(Class<T> type, String whereClause, String[] whereArgs, String orderBy,
			String limit) throws DataAccessException {
		return getList(type, false, whereClause, whereArgs, null, null, orderBy, null);
	}

	/**
	 * 根据指定的条件，获取实体的列表
	 * 
	 * @param <T>
	 *            泛型实体类，必须是BaseModel的子类
	 * @param type
	 *            指定的实体类型
	 * @param whereClause
	 *            查询条件
	 * @param whereArgs
	 *            查询参数，他会按照顺序替换 whereClause 中间的？号
	 * @param groupBy
	 *            分组条件
	 * @param having
	 *            分组条件
	 * @param orderBy
	 *            排序字段
	 * @param limit
	 *            取数据的条数
	 * @return 返回实体列表
	 * @throws DataAccessException
	 *             数据库访问异常
	 */
	public <T extends BaseModel> List<T> getList(Class<T> type, boolean distinct, String whereClause,
			String[] whereArgs, String groupBy, String having, String orderBy, String limit) throws DataAccessException {
		if (mDatabase == null) {
			throw new DataAccessException(SET_DATABASE_FIRST);
		}

		List<T> resultList = new ArrayList<T>();

		Cursor c = mDatabase.query(distinct, Utils.getTableName(type), null, whereClause, whereArgs, groupBy, having,
				orderBy, limit);
		try {
			while (c.moveToNext()) {
				// T entity = EntitiesMap.instance().get(type,
				// c.getLong(c.getColumnIndex("_id")));
				// if (entity == null) {
				T entity = type.newInstance();
				entity = Utils.inflate(c, entity);
				// }
				resultList.add(entity);
			}
		} catch (IllegalAccessException e) {
			throw new DataAccessException(e.getLocalizedMessage());
		} catch (InstantiationException e) {
			throw new DataAccessException(e.getLocalizedMessage());
		} finally {
			c.close();

		}

		return resultList;
	}

	/**
	 * 
	 * @param <T>
	 * @param <T2>
	 * @param distinct 是否去掉结果中的重复项
	 * @param whereClause 查询条件
	 * @param whereArgs 查询参数，他会按照顺序替换 whereClause 中间的？号
	 * @param groupBy 分组字段
	 * @param having 分组过滤条件
	 * @param orderBy 排序字段
	 * @param limit 取数据的条数
	 * @param type1 要关联的第一个表
	 * @param type2 要关联的第二个表
	 * @return T类型的列表
	 * @throws DataAccessException
	 */
	public <T extends BaseModel, T2 extends BaseModel> List<T> getList(boolean distinct, String whereClause, String[] whereArgs,
			String groupBy, String having, String orderBy, String limit, Class<T> type1, Class<T2> type2) throws DataAccessException {
		if (mDatabase == null) {
			throw new DataAccessException(SET_DATABASE_FIRST);
		}

		List<T> resultList = new ArrayList<T>();
		Class<T> returnType = type1;
		BaseModel returnModel;

		try {
			returnModel = returnType.newInstance();

			//for column_name
			List<Field> columns = returnModel.getColumnFields();
			StringBuffer returnCols = new StringBuffer();
			String returnTableName = Utils.getTableName(returnType);
			for (int i = 0; i < columns.size(); ++i) {
				Field column = columns.get(i);
				String colName = Utils.toSQLName(column.getName());
				if (i != columns.size() - 1) {
					returnCols.append(returnTableName).append(".").append(colName + ", ");
				} else {
					returnCols.append(returnTableName).append(".").append(colName);
				}
			}
			StringBuffer sql = new StringBuffer("select");
			if (distinct) {
				sql.append(" distinct");
			}
			sql.append(" ").append(returnCols);
			sql.append(" from ");
			
			//for table_name
			sql.append(Utils.getTableName(type1)).append(", ");
			sql.append(Utils.getTableName(type2));

			//for where clause
			if (whereClause != null) {
				StringBuilder whereStr = new StringBuilder(whereClause);
				if (whereArgs != null) {
					for (int i = 0; i < whereArgs.length; ++i) {
						whereStr.replace(0, whereStr.length(), whereArgs[i]);
					}
				}
				sql.append(" where ").append(whereStr);
			}

			if (groupBy != null) {
				sql.append(" group by ").append(groupBy);
			}
			if (having != null) {
				sql.append(" having ").append(having);
			}
			if (orderBy != null) {
				sql.append(" order by ").append(orderBy);
			}
			if (limit != null) {
				sql.append(" limit ").append(limit);
			}
			EvtLog.d("debug", "getList, " + sql.toString());

			//execute
			Cursor c = mDatabase.rawQuery(sql.toString());
			// Cursor c = mDatabase.query(distinct,
			// Utils.getTableName(returnModel.getClass()), null, whereClause,
			// whereArgs, groupBy, having,
			// orderBy, limit);
			try {
				while (c.moveToNext()) {
					// T entity = EntitiesMap.instance().get(type,
					// c.getLong(c.getColumnIndex("_id")));
					// if (entity == null) {
					T entity = returnType.newInstance();
					entity = Utils.inflate(c, entity);
					// }
					resultList.add(entity);
				}
			} catch (IllegalAccessException e) {
				throw new DataAccessException(e.getLocalizedMessage());
			} catch (InstantiationException e) {
				throw new DataAccessException(e.getLocalizedMessage());
			} finally {
				c.close();

			}

		} catch (IllegalAccessException e1) {
			throw new DataAccessException(e1.getLocalizedMessage());
		} catch (InstantiationException e1) {
			throw new DataAccessException(e1.getLocalizedMessage());
		}

		return resultList;
	}

	/**
	 * 插入记录
	 * 
	 * @param model
	 *            实体类的实例
	 * @return 返回插入记录的id
	 * @throws DataAccessException
	 *             数据库访问异常
	 */
	public long insert(BaseModel model) throws DataAccessException {
		if (model == null) {
			throw new DataAccessException(OBJECT_CAN_NOT_NULL);
		}

		List<Field> columns = model.getID() > 0 ? model.getColumnFields() : model.getColumnFieldsWithoutID();
		ContentValues values = new ContentValues();
		for (Field column : columns) {
			try {
				// 字段不为空时才插入值
				Object fieldValue = column.get(model);
				if (fieldValue != null && column.getType().getSuperclass() != BaseModel.class) {
					values.put(Utils.toSQLName(column.getName()), String.valueOf(fieldValue));
				}
			} catch (IllegalAccessException e) {
				throw new DataAccessException(e.getLocalizedMessage());
			}
		}

		long id;
		try {
			id = mDatabase.insert(model.getTableName(), values);
		} finally {

		}
		return id;
	}

	/**
	 * 
	 * @param <T>
	 *            泛型实体类，必须是BaseModel的子类。 实例model的_id必须赋值，否则不能更新
	 * @param model
	 *            实体的实例。 实例model的_id必须赋值，否则不能更新
	 * @return 受影响的记录数
	 * @throws DataAccessException
	 *             数据库访问异常
	 */
	public <T extends BaseModel> int update(BaseModel model) throws DataAccessException {
		if (model == null) {
			throw new DataAccessException(OBJECT_CAN_NOT_NULL);
		}

		if (model.getID() <= 0) {
			throw new DataAccessException(ID_CAN_NOT_NULL);
		}

		List<Field> columns = model.getColumnFieldsWithoutID();
		ContentValues values = new ContentValues(columns.size());
		for (Field column : columns) {
			try {
				Object fieldValue = column.get(model);
				if (null != fieldValue) {
					values.put(Utils.toSQLName(column.getName()), String.valueOf(fieldValue));
				}
			} catch (IllegalArgumentException e) {
				throw new DataAccessException(COLUMN_NOT_EXISITS + column.getName());
			} catch (IllegalAccessException e) {
				throw new DataAccessException(COLUMN_NOT_EXISITS + column.getName());
			}
		}

		int r = updateByClause(model.getClass(), values, ID_WHERE_CLAUSE,
				new String[] { String.valueOf(model.getID()) });

		return r;
	}

	/**
	 * 
	 * @param <T>
	 *            泛型实体类，必须是BaseModel的子类。 实例model的_id可以没有赋值
	 * @param model
	 *            实体的实例
	 * @return 受影响的记录数
	 * @throws DataAccessException
	 *             数据库访问异常
	 */
	public <T extends BaseModel> int updateByClause(Class<T> type, BaseModel model, String whereClause,
			String[] whereArgs) throws DataAccessException {
		if (model == null) {
			throw new DataAccessException(OBJECT_CAN_NOT_NULL);
		}

		if (whereClause == null) {
			throw new DataAccessException(WHERE_CLAUSE_CAN_NOT_NULL);
		}

		List<Field> columns = model.getColumnFieldsWithoutID();
		ContentValues values = new ContentValues(columns.size());
		for (Field column : columns) {
			try {
				Object fieldValue = column.get(model);
				if (null != fieldValue) {
					values.put(Utils.toSQLName(column.getName()), String.valueOf(fieldValue));
				}
			} catch (IllegalArgumentException e) {
				throw new DataAccessException(COLUMN_NOT_EXISITS + column.getName());
			} catch (IllegalAccessException e) {
				throw new DataAccessException(COLUMN_NOT_EXISITS + column.getName());
			}
		}

		int r = updateByClause(model.getClass(), values, whereClause, whereArgs);
		return r;
	}

	/**
	 * 根据条件修改多条数据
	 * 
	 * @param <T>
	 *            泛型类
	 * @param type
	 *            需要修改的表的实体类
	 * @param values
	 *            需要修改的内容
	 * @param whereClause
	 *            查询条件
	 * @param whereArgs
	 *            查询条件对应的值
	 * @return 返回受影响的行数
	 */
	public <T extends BaseModel> int updateByClause(Class<T> type, ContentValues values, String whereClause,
			String[] whereArgs) {

		int rowAffect;
		try {

			String table = Utils.getTableName(type);
			rowAffect = mDatabase.update(table, values, whereClause, whereArgs);
		} finally {

		}
		return rowAffect;
	}

	/**
	 * 实体保存方法，如果已经存在则更新，否则插入新纪录
	 * 
	 * @param model
	 *            保存的实例
	 * @return 保存实例的主键值
	 * @throws DataAccessException
	 *             数据库异常
	 */
	public long save(BaseModel model) throws DataAccessException {
		if (model == null) {
			throw new DataAccessException(OBJECT_CAN_NOT_NULL);
		}

		long id = model.getID();
		if (id <= 0) {
			id = insert(model);
		} else {
			BaseModel existModel = get(model.getClass(), ID_WHERE_CLAUSE, new String[] { model.getID() + "" });
			if (existModel == null) {
				update(model);
			} else {
				id = insert(model);
			}
		}

		return id;
	}

	/**
	 * 删除指定主键的实例
	 * 
	 * @param <T>
	 *            泛型类
	 * @param type
	 *            要删除的记录类型
	 * @param id
	 *            要删除记录的主键
	 * @return 删除成功后，返回true；否则返回false
	 */
	public <T extends BaseModel> boolean delete(Class<T> type, long id) {
		boolean toRet = delete(type, ID_WHERE_CLAUSE, new String[] { String.valueOf(id) });

		return toRet;
	}

	/**
	 * 删除指定主键的实例
	 * 
	 * @param <T>
	 *            泛型类
	 * @param type
	 *            要删除的记录类型
	 * @param whereClause
	 *            where条件
	 * @param whereArgs
	 *            where参数
	 * @return 删除成功后，返回true；否则返回false
	 */
	public <T extends BaseModel> boolean delete(Class<T> type, String whereClause, String[] whereArgs) {

		boolean result;
		try {

			result = mDatabase.delete(Utils.getTableName(type), whereClause, whereArgs) != 0;
		} finally {

		}

		return result;
	}

	/**
	 * select 查询方法，可以进行跨表查询
	 * 
	 * @param sql
	 *            查询的语句
	 * @return 符合条件的Cursor
	 */
	public Cursor rawQuery(String sql) {
		Cursor cursor = rawQuery(sql, null);

		return cursor;
	}

	/**
	 * select 查询方法，可以进行跨表查询
	 * 
	 * @param sql
	 *            查询的语句
	 * @param selectionArgs
	 *            sql查询语句中参数的值
	 * @return 符合条件的Cursor
	 */
	public Cursor rawQuery(String sql, String[] selectionArgs) {

		Cursor cursor = mDatabase.rawQuery(sql, selectionArgs);

		return cursor;
	}

	/**
	 * Execute a single SQL statement that is not a query. For example, CREATE
	 * TABLE, DELETE, INSERT, etc. Multiple statements separated by ;s are not
	 * supported. it takes a write lock
	 */
	public void execSQL(String sql) {
		mDatabase.execSQL(sql);
	}
}
