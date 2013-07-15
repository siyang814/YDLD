package com.framework.orm;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.framework.Exception.EvtLog;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * Sqlite 辅助类，用于创建sqlite数据库
 * 
 * @author 
 * 
 */
class DatabaseOpenHelper extends SQLiteOpenHelper {

	DatabaseBuilder _builder;
	int _version;
	Context _context;

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param dbPath
	 * @param dbVersion
	 * @param builder
	 */
	public DatabaseOpenHelper(Context context, String dbPath, int dbVersion, DatabaseBuilder builder) {
		super(context, dbPath, null, dbVersion);
		_builder = builder;
		_version = dbVersion;
		_context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (String table : _builder.getTables()) {
			String sqlStr = null;
			try {
				sqlStr = _builder.getSQLCreate(table);
			} catch (DataAccessException e) {
				EvtLog.e(this.getClass().getName(), e);
			}
			if (sqlStr != null)
				db.execSQL(sqlStr);
		}
		db.setVersion(_version);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ArrayList<String> scripts = databaseUpgrade(oldVersion, newVersion);
		if (scripts == null || scripts.size() == 0) {
			for (String table : _builder.getTables()) {
				String sqlStr = _builder.getSQLDrop(table);
				db.execSQL(sqlStr);
			}
			onCreate(db);
		} else {
			for (int i = 0; i < scripts.size(); ++i) {
				String sql = scripts.get(i);
				if (!(sql.trim().equals("") || sql.trim().startsWith("--"))) {
					db.execSQL(scripts.get(i));
				}
			}
		}
	}

	private ArrayList<String> databaseUpgrade(int oldVersion, int newVersion) {
		AssetManager assetMgr = _context.getAssets();
		ArrayList<String> sqls = new ArrayList<String>();
		try {
			InputStream inputStream = assetMgr.open("db_upgrade_" + oldVersion + "_" + newVersion);
			DataInputStream dataInput = new DataInputStream(inputStream);
			StringBuffer sql = new StringBuffer("");
			String clause = null;
			while ((clause = dataInput.readLine()) != null) {
				clause = clause.trim();
				// 过滤空行和注释
				if (clause.equals("") || clause.startsWith("--")) {
					continue;
				}
				// 查找语句结束的分号
				sql.append(" ").append(clause);
				if (clause.endsWith(";")) {
					sqls.add(sql.toString());
					EvtLog.d("debugDB", sql.toString());
					sql.delete(0, sql.length());
				}
			}
		} catch (IOException e) {
			EvtLog.e("debugDB", e);
		}
		return sqls;
	}

}
