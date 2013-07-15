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
 * sqlite ���ݹ�����
 * 
 * @author 
 * 
 */
public abstract class DataManager {

	private static final String OBJECT_CAN_NOT_NULL = "����Ķ�����Ϊ�ա�";
	private static final String ID_CAN_NOT_NULL = "ʹ��Update����ʱ��_id������ֵ";
	private static final String WHERE_CLAUSE_CAN_NOT_NULL = "������������Ϊ�գ����������������ݿ⡣";
	private static final String ID_WHERE_CLAUSE = "_id = ?";
	private static final String COLUMN_NOT_EXISITS = "ָ�����в�����";
	private static final String SET_DATABASE_FIRST = "Set database first";

	private Context mContext;
	private String mDBName;
	private int mDBVersion;
	private DatabaseBuilder mDatabaseBuilder;
	public Database mDatabase;

	/**
	 * ��ʼ�����ݿ�
	 * 
	 * @param context
	 *            ��ǰ������
	 * @param dbName
	 *            ���ݿ�����
	 * @param dbVersion
	 *            ���ݿ�汾
	 * @param databaseBuilder
	 *            ���ݿ���������
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
	 * �������񡣸÷��������open�������Զ������ݿ�����
	 */
	public void beginTransaction() {
		// open();
		mDatabase.beginTransaction();
	}

	/**
	 * �������񡣸÷��������close�������Զ��ر����ݿ�����
	 */
	public void endTransaction() {
		mDatabase.endTransaction();
		// close();
	}

	/**
	 * �ع�����
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
	 * ����ָ������������ȡ����ʵ�塣������ڶ������������ļ�¼����ֻ���ص�һ����¼
	 * 
	 * @param <T>
	 *            ʵ������
	 * @param type
	 *            ָ����ʵ������
	 * @param whereClause
	 *            ��ѯ����
	 * @param whereArgs
	 *            ��ѯ���������ᰴ��˳���滻 whereClause �м�ģ���
	 * @return ���ص���ʵ�塣������ڶ������������ļ�¼����ֻ���ص�һ����¼
	 * @throws DataAccessException
	 *             ���ݿ�����쳣
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
	 * ����ָ������������ȡʵ����б�
	 * 
	 * @param <T>
	 *            ����ʵ���࣬������BaseModel������
	 * @param type
	 *            ָ����ʵ������
	 * @param whereClause
	 *            ��ѯ����
	 * @param whereArgs
	 *            ��ѯ���������ᰴ��˳���滻 whereClause �м�ģ���
	 * @return ����ʵ���б�
	 * @throws DataAccessException
	 *             ���ݿ�����쳣
	 */
	public <T extends BaseModel> List<T> getList(Class<T> type, String whereClause, String[] whereArgs)
			throws DataAccessException {
		return getList(type, false, whereClause, whereArgs, null, null, null, null);
	}

	/**
	 * ����ָ������������ȡʵ����б�
	 * 
	 * @param <T>
	 *            ����ʵ���࣬������BaseModel������
	 * @param type
	 *            ָ����ʵ������
	 * @param whereClause
	 *            ��ѯ����
	 * @param whereArgs
	 *            ��ѯ���������ᰴ��˳���滻 whereClause �м�ģ���
	 * @param orderBy
	 *            �����ֶ�
	 * @param limit
	 *            ȡ���ݵ�����
	 * @return ����ʵ���б�
	 * @throws DataAccessException
	 *             ���ݿ�����쳣
	 */
	public <T extends BaseModel> List<T> getList(Class<T> type, String whereClause, String[] whereArgs, String orderBy,
			String limit) throws DataAccessException {
		return getList(type, false, whereClause, whereArgs, null, null, orderBy, null);
	}

	/**
	 * ����ָ������������ȡʵ����б�
	 * 
	 * @param <T>
	 *            ����ʵ���࣬������BaseModel������
	 * @param type
	 *            ָ����ʵ������
	 * @param whereClause
	 *            ��ѯ����
	 * @param whereArgs
	 *            ��ѯ���������ᰴ��˳���滻 whereClause �м�ģ���
	 * @param groupBy
	 *            ��������
	 * @param having
	 *            ��������
	 * @param orderBy
	 *            �����ֶ�
	 * @param limit
	 *            ȡ���ݵ�����
	 * @return ����ʵ���б�
	 * @throws DataAccessException
	 *             ���ݿ�����쳣
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
	 * @param distinct �Ƿ�ȥ������е��ظ���
	 * @param whereClause ��ѯ����
	 * @param whereArgs ��ѯ���������ᰴ��˳���滻 whereClause �м�ģ���
	 * @param groupBy �����ֶ�
	 * @param having �����������
	 * @param orderBy �����ֶ�
	 * @param limit ȡ���ݵ�����
	 * @param type1 Ҫ�����ĵ�һ����
	 * @param type2 Ҫ�����ĵڶ�����
	 * @return T���͵��б�
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
	 * �����¼
	 * 
	 * @param model
	 *            ʵ�����ʵ��
	 * @return ���ز����¼��id
	 * @throws DataAccessException
	 *             ���ݿ�����쳣
	 */
	public long insert(BaseModel model) throws DataAccessException {
		if (model == null) {
			throw new DataAccessException(OBJECT_CAN_NOT_NULL);
		}

		List<Field> columns = model.getID() > 0 ? model.getColumnFields() : model.getColumnFieldsWithoutID();
		ContentValues values = new ContentValues();
		for (Field column : columns) {
			try {
				// �ֶβ�Ϊ��ʱ�Ų���ֵ
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
	 *            ����ʵ���࣬������BaseModel�����ࡣ ʵ��model��_id���븳ֵ�������ܸ���
	 * @param model
	 *            ʵ���ʵ���� ʵ��model��_id���븳ֵ�������ܸ���
	 * @return ��Ӱ��ļ�¼��
	 * @throws DataAccessException
	 *             ���ݿ�����쳣
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
	 *            ����ʵ���࣬������BaseModel�����ࡣ ʵ��model��_id����û�и�ֵ
	 * @param model
	 *            ʵ���ʵ��
	 * @return ��Ӱ��ļ�¼��
	 * @throws DataAccessException
	 *             ���ݿ�����쳣
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
	 * ���������޸Ķ�������
	 * 
	 * @param <T>
	 *            ������
	 * @param type
	 *            ��Ҫ�޸ĵı��ʵ����
	 * @param values
	 *            ��Ҫ�޸ĵ�����
	 * @param whereClause
	 *            ��ѯ����
	 * @param whereArgs
	 *            ��ѯ������Ӧ��ֵ
	 * @return ������Ӱ�������
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
	 * ʵ�屣�淽��������Ѿ���������£���������¼�¼
	 * 
	 * @param model
	 *            �����ʵ��
	 * @return ����ʵ��������ֵ
	 * @throws DataAccessException
	 *             ���ݿ��쳣
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
	 * ɾ��ָ��������ʵ��
	 * 
	 * @param <T>
	 *            ������
	 * @param type
	 *            Ҫɾ���ļ�¼����
	 * @param id
	 *            Ҫɾ����¼������
	 * @return ɾ���ɹ��󣬷���true�����򷵻�false
	 */
	public <T extends BaseModel> boolean delete(Class<T> type, long id) {
		boolean toRet = delete(type, ID_WHERE_CLAUSE, new String[] { String.valueOf(id) });

		return toRet;
	}

	/**
	 * ɾ��ָ��������ʵ��
	 * 
	 * @param <T>
	 *            ������
	 * @param type
	 *            Ҫɾ���ļ�¼����
	 * @param whereClause
	 *            where����
	 * @param whereArgs
	 *            where����
	 * @return ɾ���ɹ��󣬷���true�����򷵻�false
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
	 * select ��ѯ���������Խ��п���ѯ
	 * 
	 * @param sql
	 *            ��ѯ�����
	 * @return ����������Cursor
	 */
	public Cursor rawQuery(String sql) {
		Cursor cursor = rawQuery(sql, null);

		return cursor;
	}

	/**
	 * select ��ѯ���������Խ��п���ѯ
	 * 
	 * @param sql
	 *            ��ѯ�����
	 * @param selectionArgs
	 *            sql��ѯ����в�����ֵ
	 * @return ����������Cursor
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
