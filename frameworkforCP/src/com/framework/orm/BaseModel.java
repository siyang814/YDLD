package com.framework.orm;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * ʵ������࣬��Ҫ�������ݵ�sqlite��ʵ�壬����Ҫ�Ӹ���̳�
 * <p>
 * Ŀǰ֧������ļ����������ͣ�int, java.lang.Integer, long, java.lang.Long, java.lang.String<br>
 * </p>
 * ע�⣺1�������ֶε��������ʹ�ÿɿ����ͣ��� java.lang.Integer �ȣ�<br>
 * 2��������ʹ��Date�����ͣ�ʹ��java.lang.Long�滻��<br>
 * 3��
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
	 * ��ȡ���id����Ĺ����ֶΣ��������� m_ �� s_ ��ͷ���ֶ�
	 * 
	 * @return ����ֶ������б�.
	 */
	String[] getColumnsWithoutID() {
		List<String> columns = new ArrayList<String>();
		for (Field field : getColumnFieldsWithoutID()) {
			columns.add(field.getName());
		}

		return columns.toArray(new String[0]);
	}

	/**
	 * ��ȡ�๫�е��ֶΣ��������� m_ �� s_ ��ͷ���ֶ�
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
	 * ��ȡ���id������ֶΣ��������� m_ �� s_ ��ͷ���ֶ�
	 * 
	 * @return ����ֶ��б�.
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
