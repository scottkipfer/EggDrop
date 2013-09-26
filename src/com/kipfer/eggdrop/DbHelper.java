package com.kipfer.eggdrop;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper{
	private static final String TAG = DbHelper.class.getSimpleName();
	public static final String DB_NAME = "eggdrop.db";
	public static final int DB_VERSION = 1;
	public static final String TABLE = "scores";
	public static final String T_ID = BaseColumns._ID;
	public static final String T_USERNAME = "username";
	public static final String T_SCORE = "score";
	public static final String T_DATE = "date";
	

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = String.format("create table %s (%s int primary key, %s text , %s int, %s date)", 
				TABLE, T_ID, T_USERNAME, T_SCORE,T_DATE);
		Log.d(TAG, "onCreate sql: " + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE);
		Log.d(TAG, " onUpdate dropped table " + TABLE);
		this.onCreate(db);
	}

}
