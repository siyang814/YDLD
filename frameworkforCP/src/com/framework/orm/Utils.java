package com.framework.orm;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;

import com.framework.Exception.EvtLog;

import android.database.Cursor;


/**
 * ORM�ĸ���������
 * 
 * @author 
 */
public class Utils {
	public static final String TAG = "ORM";

	public static final String ERR_DB_IS_NOT_OPEN = "���ݿ⴦�ڹر�״̬����ȷ���Ƿ��Ѵ����ݿ�";
	public static final String ERR_INSERT_FAILED = "�������ʧ��. Internal error.";
	public static final String ID = "_id";

	/**
	 * ��java��������ת��Ϊsql�е��������磺entityIsAName Ϊ entity_IS_A_NAME��
	 * <p>
	 * �����Сд����ת��Ϊ��д�����Ǵ�д��������ĸǰ���»��ߡ�_�����磺<br>
	 * "AbCd"->"AB_CD" <br>
	 * "ABCd"->"AB_CD"<br>
	 * "AbCD"->"AB_CD"<br>
	 * "ShowplaceDetailsVO"->"SHOWPLACE_DETAILS_VO"<br>
	 * </p>
	 * 
	 * @param java����
	 * @return ת�����Sql����
	 */
	public static String toSQLName(String javaNotation) {
		if (ID.equalsIgnoreCase(javaNotation)) {
			return ID;
		}

		StringBuilder sb = new StringBuilder();
		char[] buf = javaNotation.toCharArray();

		for (int i = 0; i < buf.length; i++) {
			char prevChar = (i > 0) ? buf[i - 1] : 0;
			char c = buf[i];
			char nextChar = (i < buf.length - 1) ? buf[i + 1] : 0;
			boolean isFirstChar = (i == 0) ? true : false;

			if (isFirstChar || Character.isLowerCase(c)) {
				sb.append(Character.toUpperCase(c));
			} else if (Character.isUpperCase(c)) {
				if (Character.isLetterOrDigit(prevChar)) {
					if (Character.isLowerCase(prevChar)) {
						sb.append('_').append(Character.toUpperCase(c));
					} else if (nextChar > 0 && Character.isLowerCase(nextChar)) {
						sb.append('_').append(Character.toUpperCase(c));
					} else {
						sb.append(c);
					}
				} else {
					sb.append(c);
				}
			}
		}

		return sb.toString();
	}

	/**
	 * ��sql������ת��Ϊjava�����Ի��߷����������磺entity_IS_A_NAME Ϊ entityIsAName��
	 * <p>
	 * �����ĸ���»��ߡ�_����תΪ��д������ת��ΪСд
	 * </p>
	 * 
	 * @param sqlNotation
	 *            Sql����
	 * @return ת�����java����
	 */
	public static String toJavaMethodName(String sqlNotation) {
		StringBuilder dest = new StringBuilder();
		char[] src = sqlNotation.toCharArray();

		for (int i = 0; i < src.length; i++) {
			char c = src[i];
			boolean isFirstChar = (i == 0) ? true : false;

			if (isFirstChar || c != '_') {
				dest.append(Character.toLowerCase(c));
			} else {
				i++;
				if (i < src.length) {
					dest.append(src[i]);
				}
			}
		}

		return dest.toString();
	}

	/**
	 * ��Sql�ı���ת��Ϊ�������� entity_IS_A_NAME to entityIsAName;
	 * <p>
	 * ���û���»��ߣ���ת��ΪСд��������»��ߣ�������»��ߣ����ҽ���һ����ĸת��Ϊ��д
	 * </p>
	 * 
	 * @param sqlNotation
	 *            Sql����
	 * @return תΪ���������
	 */
	public static String toJavaClassName(String sqlNotation) {
		StringBuilder sb = new StringBuilder();
		char[] buf = sqlNotation.toCharArray();
		for (int i = 0; i < buf.length; i++) {
			char c = buf[i];
			if (i == 0) {
				sb.append(buf[i]);
			} else if (c != '_') {
				sb.append(Character.toLowerCase(c));
			} else {
				i++;
				if (i < buf.length) {
					sb.append(buf[i]);
				}
			}
		}
		return sb.toString();
	}

	static <T extends BaseModel> String getTableName(Class<T> clazz) {
		return toSQLName(clazz.getSimpleName());
	}

	/**
	 * Inflate entity entity using the current row from the given cursor.
	 * 
	 * @param cursor
	 *            The cursor to get object data from.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	static <T extends BaseModel> T inflate(Cursor cursor, BaseModel entity) throws DataAccessException {
		try {
			entity = entity.getClass().newInstance();
		} catch (IllegalAccessException e) {
			throw new DataAccessException(e.getLocalizedMessage());
		} catch (InstantiationException e) {
			throw new DataAccessException(e.getLocalizedMessage());
		}

		for (Field field : entity.getColumnFields()) {
			String typeString = null;
			String colName = null;
			try {
				typeString = field.getType().getName();
				colName = toSQLName(field.getName());
				if (cursor.isNull(cursor.getColumnIndex(colName))) {
					continue;
				}
				if (typeString.equals("long") || typeString.equals("java.lang.Long")) {
					field.set(entity, cursor.getLong(cursor.getColumnIndex(colName)));
				} else if (typeString.equals("java.lang.String")) {
					field.set(entity, cursor.getString(cursor.getColumnIndex(colName)));
				} else if (typeString.equals("double") || typeString.equals("java.lang.Double")) {
					field.set(entity, cursor.getDouble(cursor.getColumnIndex(colName)));
				} else if (typeString.equals("java.lang.Boolean") || typeString.equals("boolean")) {
					String fieldValue = cursor.getString(cursor.getColumnIndex(colName));
					field.set(entity, fieldValue != null && fieldValue.equals("true"));
				} else if (typeString.equals("[B")) {
					field.set(entity, cursor.getBlob(cursor.getColumnIndex(colName)));
				} else if (typeString.equals("int") || typeString.equals("java.lang.Integer")) {
					field.set(entity, cursor.getInt(cursor.getColumnIndex(colName)));
				} else if (typeString.equals("float") || typeString.equals("java.lang.Float")) {
					field.set(entity, cursor.getFloat(cursor.getColumnIndex(colName)));
				} else if (typeString.equals("short") || typeString.equals("java.lang.Short")) {
					field.set(entity, cursor.getShort(cursor.getColumnIndex(colName)));
				} else if (typeString.equals("java.sql.timestamp")) {
					long l = cursor.getLong(cursor.getColumnIndex(colName));
					field.set(entity, new Timestamp(l));
				} else if (typeString.equals("java.util.Date")) {
					long l = cursor.getLong(cursor.getColumnIndex(colName));
					field.set(entity, new Date(l));
				} else
					throw new DataAccessException("Class cannot be read from Sqlite3 database.");
			} catch (IllegalArgumentException e) {
				throw new DataAccessException(e.getLocalizedMessage());
			} catch (IllegalAccessException e) {
				throw new DataAccessException(e.getLocalizedMessage());
			} catch (Exception e) {
				EvtLog.e(TAG, e);
			}
		}

		// EntitiesMap.instance().set(entity);

		return (T) entity;
	}

	/**
	 * ����java�ֶ����ͣ�ת��Ϊsqlite���е�����
	 * 
	 * @param c
	 *            �ֶ����Ͷ���
	 * @return sqlite�е�����
	 */
	static String getSQLiteTypeString(Class<?> c) {
		String result = "text";
		String name = c.getName();

		if ("java.lang.String".equals(name) || "string".equals(name)) {
			result = "text";
		} else if ("short".equals(name) || "int".equals(name) || "java.lang.Integer".equals(name)
				|| "long".equals(name) || "java.lang.Long".equals(name) || "java.sql.Timestamp".equals(name)
				|| "java.util.Date".equals(name)) {
			result = "int";
		} else if ("double".equals(name) || "java.lang.Double".equals(name) || "float".equals(name)
				|| "java.lang.Float".equals(name)) {
			result = "real";
		} else if ("[B".equals(name)) {
			result = "blob";
		} else if ("java.lang.Boolean".equals(name) || "boolean".equals(name)) {
			result = "bool";
		} else {
			throw new IllegalArgumentException("�಻�ܱ��浽Sqlite���ݿ⣬��鿴�Ƿ���ڲ�֧�ֵ����ͣ�");
		}

		return result;
	}
}
