package com.framework.orm;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 实体类基类，需要保存数据到sqlite的实体，都需要从该类继承
 * <p>
 * 目前支持下面的几种数据类型：int, java.lang.Integer, long, java.lang.Long, java.lang.String<br>
 * </p>
 * 注意：1、各个字段的类型最好使用可空类型，如 java.lang.Integer 等；<br>
 * 2、不建议使用Date等类型，使用java.lang.Long替换；<br>
 * 3、
 * 
 * @author 
 * 
 */
public class BaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1907916979605330513L;

	public long _id = 0;

	/**
	 * This entities row id.
	 * 
	 * @return The SQLite row id.
	 */
	public long getID() {
		return _id;
	}

	/**
	 * This entities row id.
	 * 
	 * @param id
	 *            the specified entities row id
	 */
	public void setID(long id) {
		_id = id;
	}

	String getTableName() {
		return Utils.toSQLName(getClass().getSimpleName());
	}

	/**
	 * 获取类除id以外的公有字段，不包含以 m_ 或 s_ 开头的字段
	 * 
	 * @return 类的字段数组列表.
	 */
	String[] getColumnsWithoutID() {
		List<String> columns = new ArrayList<String>();
		for (Field field : getColumnFieldsWithoutID()) {
			columns.add(field.getName());
		}

		return columns.toArray(new String[0]);
	}

	/**
	 * 获取类公有的字段，不包含以 m_ 或 s_ 开头的字段
	 * 
	 * @return An array of fields for this class.
	 */
	List<Field> getColumnFields() {
		Field[] fields = getClass().getFields();
		List<Field> columns = new ArrayList<Field>();
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
				columns.add(field);
			}
		}

		return columns;
	}

	/**
	 * 获取类除id以外的字段，不包含以 m_ 或 s_ 开头的字段
	 * 
	 * @return 类的字段列表.
	 */
	List<Field> getColumnFieldsWithoutID() {
		Field[] fields = getClass().getFields();
		List<Field> columns = new ArrayList<Field>();
		for (Field field : fields) {
			if (!field.getName().startsWith("_id") && !Modifier.isStatic(field.getModifiers())) {
				columns.add(field);
			}
		}

		return columns;
	}
}
