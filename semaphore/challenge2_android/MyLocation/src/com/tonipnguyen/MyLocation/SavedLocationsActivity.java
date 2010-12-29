package com.tonipnguyen.MyLocation;

import java.util.Date;
import java.text.DateFormat;
import com.tonipnguyen.MyLocation.R;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;


public class SavedLocationsActivity extends Activity {

	static final String TAG = "SavedLocationsActivity";
	private MyLocationDbHelper dbHelper;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbHelper = new MyLocationDbHelper(this);
		setContentView(layoutView());
	}

	private ViewGroup layoutView() {
		LinearLayout linear = new LinearLayout(this);

		linear.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		linear.setOrientation(LinearLayout.VERTICAL);

		TextView tv = new TextView(this);
		tv.setMovementMethod(ScrollingMovementMethod.getInstance());
		tv.setText(R.string.previous_locations_title);
		tv.append("\n\n");
		
		linear.addView(tv);

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] columns = new String[] { MyLocationDbHelper.C_CREATED,
				MyLocationDbHelper.C_DESCRIPTION, MyLocationDbHelper.C_LONGITUDE, MyLocationDbHelper.C_LATITUDE };

		Cursor cursor = db.query(MyLocationDbHelper.TABLE, columns, null, null,
				null, null, MyLocationDbHelper.C_CREATED + " DESC");
		startManagingCursor(cursor);

		if (cursor.getCount() > 0) {
			int dateIndex = cursor.getColumnIndexOrThrow(MyLocationDbHelper.C_CREATED);
			int descrIndex = cursor.getColumnIndexOrThrow(MyLocationDbHelper.C_DESCRIPTION);
			int longitudeIndex = cursor.getColumnIndexOrThrow(MyLocationDbHelper.C_LONGITUDE);
			int latitudeIndex = cursor.getColumnIndexOrThrow(MyLocationDbHelper.C_LATITUDE);

			while (cursor.moveToNext()) {
				Date date = new Date(cursor.getLong(dateIndex));
				DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

				String description = cursor.getString(descrIndex);
				float longitude = cursor.getFloat(longitudeIndex);
				float latitude = cursor.getFloat(latitudeIndex);

				String s = new String(df.format(date) + "\n"
						+ getString(R.string.latitude) + latitude + "\n"
						+ getString(R.string.longitude) + longitude + "\n"
						+ getString(R.string.description) + description
						+ "\n\n");
				
				tv.append(s);
				
				Log.d(TAG, "descr: " + description);
			}
		} else {
			tv.append(getString(R.string.no_previous_locations));
			
			Log.d(TAG, "no records");
		}

		db.close();

		return linear;
	}
}
