package com.tonipnguyen.MyLocation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyLocationDbHelper extends SQLiteOpenHelper {

	static final String TAG = "MyLocationDbHelper";
	static final String DB_NAME = "savedLocations.db";
	static final int DB_VERSION = 1;
	static final String TABLE = "savedLocations";
	static final String C_CREATED = "created";
	static final String C_DESCRIPTION = "description";
	static final String C_LONGITUDE = "longitude";
	static final String C_LATITUDE = "latitude";

	Context context;

	public MyLocationDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE + " (" + C_CREATED + " int, " + C_DESCRIPTION
				+ " text, " + C_LONGITUDE + " real, " + C_LATITUDE + " real)";

		db.execSQL(sql);

		Log.d(TAG, "onCreate'd sql: " + sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE); // drops the old database
		Log.d(TAG, "onUpdate'd");
		onCreate(db); // run onCreate to get new database
	}

}
