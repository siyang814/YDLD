package com.framework.orm;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines DB schema definition statements from provided Java classes. <br/>
 * Use this class to specify structure of your DB. Call method addClass() for
 * each table and provide corresponding Java class. <br/>
 * Normally this class instantiated only once at the very beginning of the
 * application lifecycle. Once instantiated it is used by underlying
 * SQLDatabaseHelper and provides SQL statements for create or upgrade of DB
 * schema.
 * 
 * @author 
 *         <p>
 *         This project based on and inspired by 'androidactiverecord' project
 *         written by JEREMYOT
 *         </p>
 */
public class DatabaseBuilder {

	@SuppressWarnings("unchecked")
	Map<String, Class> mClasses = new HashMap<String, Class>();
	String mDBName;

	/**
	 * Create a new DatabaseBuilder for a database.
	 */
	public DatabaseBuilder(String dbName) {
		this.mDBName = dbName;
	}

	/**
	 * Add or update a table for an AREntity that is stored in the current
	 * database.
	 * 
	 * @param <T>
	 *            Any ActiveRecordBase type.
	 * @param c
	 *            The class to reference when updating or adding a table.
	 */
	public <T extends BaseModel> void addClass(Class<T> c) {
		mClasses.put(c.getSimpleName(), c);
	}

	/**
	 * Returns list of DB tables according to classes added to a schema map
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String[] getTables() {
		String[] ret = new String[mClasses.size()];
		Class[] arr = new Class[mClasses.size()];
		arr = mClasses.values().toArray(arr);
		for (int i = 0; i < arr.length; i++) {
			Class c = arr[i];
			ret[i] = Utils.toSQLName(c.getSimpleName());
		}
		return ret;
	}

	/**
	 * Returns SQL create statement for specified table
	 * 
	 * @param table
	 *            name in SQL notation
	 * @throws DataAccessException
	 */
	@SuppressWarnings("unchecked")
	public <T extends BaseModel> String getSQLCreate(String table) throws DataAccessException {
		StringBuilder sb = null;
		Class<T> c = getClassBySqlName(table);
		T e = null;
		try {
			e = c.newInstance();
		} catch (IllegalAccessException e1) {
			throw new DataAccessException(e1.getLocalizedMessage());
		} catch (InstantiationException e1) {
			throw new DataAccessException(e1.getLocalizedMessage());
		}

		if (null != c) {
			sb = new StringBuilder("CREATE TABLE ").append(table).append(" (_id integer primary key");
			for (Field column : e.getColumnFieldsWithoutID()) {
				String jname = column.getName();
				String qname = Utils.toSQLName(jname);
				Class<?> columntype = column.getType();
				String sqliteType = Utils.getSQLiteTypeString(columntype);
				sb.append(", ").append(qname).append(" ").append(sqliteType);
			}
			sb.append(")");

		}
		return sb.toString();
	}

	/**
	 * Returns SQL drop table statement for specified table
	 * 
	 * @param table
	 *            name in SQL notation
	 */
	public String getSQLDrop(String table) {
		return "DROP TABLE IF EXISTS " + table;
	}

	public String getDatabaseName() {
		return mDBName;
	}

	@SuppressWarnings("unchecked")
	private Class getClassBySqlName(String table) {
		String jName = Utils.toJavaClassName(table);
		return mClasses.get(jName);
	}
}
