package com.tonipnguyen.MyLocation;

import com.tonipnguyen.MyLocation.R;
import android.app.*;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.location.*;
import android.os.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.util.Log;


public class MainActivity extends Activity implements OnClickListener, LocationListener {
	
	static final String TAG = "MainActivity";

	private LocationManager locationManager;
	private String bestLocationProvider;
	private Location lastKnownLocation;
	private Location currentLocation;
	
	private MyLocationDbHelper dbHelper;
	private SQLiteDatabase db;
	
	private TextView tv;
	private Button saveButton;
	private Button previousLocButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupLocationService();
		setContentView(layoutView());
		dbHelper = new MyLocationDbHelper(this);
	}

	protected void onResume() {
		super.onResume();
		// Start updates (doc recommends delay >= 60000 ms)
		locationManager.requestLocationUpdates(bestLocationProvider, 15000, 1, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Stop updates to save power while app paused
		locationManager.removeUpdates(this);
	}
	
	@Override
	public void onClick(View v) {

		if (v == saveButton) {
			saveLocation();
		} else if (v == previousLocButton) {
			showSavedLocations();
		}
	}
	
	private void setupLocationService() {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		bestLocationProvider = locationManager.getBestProvider(new Criteria(), true);
		Log.d(TAG, "best location provider: " + bestLocationProvider);
		
		lastKnownLocation = locationManager.getLastKnownLocation(bestLocationProvider);

		locationManager.requestLocationUpdates(bestLocationProvider, 15000, 1, this);
	}

	private ViewGroup layoutView() {
		LinearLayout linear = new LinearLayout(this);

		linear.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		linear.setOrientation(LinearLayout.VERTICAL);
		linear.setPadding(20, 20, 20, 20);

		tv = new TextView(this);
		tv.setText(getLastKnownLocation());
		linear.addView(tv);

		saveButton = new Button(this);
		saveButton.setText(R.string.save_button);
		saveButton.setOnClickListener(this);
		linear.addView(saveButton);

		previousLocButton = new Button(this);
		previousLocButton.setText(R.string.previous_locations_button);
		previousLocButton.setOnClickListener(this);
		linear.addView(previousLocButton);

		return linear;
	}

	private String getCurrentLocation() {

		StringBuilder sb = new StringBuilder();
		sb.append(getText(R.string.current_location));
		sb.append("\n");
		
		if (currentLocation != null) {
			sb.append(String.format(getString(R.string.latitude_longitude), currentLocation.getLatitude(), currentLocation.getLongitude()));
			
			Log.d(TAG, "current location: " + currentLocation.toString());
		}
		else {
			sb.append(getString(R.string.unknown_location));
		}

		sb.append("\n\n");
		
		return sb.toString();
	}
	
	private String getLastKnownLocation(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(getText(R.string.last_known_location));
		sb.append("\n");

		if (lastKnownLocation != null) {
			sb.append(String.format(getString(R.string.latitude_longitude), lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
			
			Log.d(TAG, "last known location: " + lastKnownLocation.toString());
		}
		else {
			sb.append(getString(R.string.unknown_location));
		}
		
		sb.append("\n\n");

		return sb.toString();
	}

	private void saveLocation() {

		if (currentLocation != null) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle(R.string.description_dialog_title);
			alert.setMessage(R.string.description_dialog_msg);

			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton(R.string.ok_button,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String description = input.getText().toString();

							db = dbHelper.getWritableDatabase();

							ContentValues values = new ContentValues();
							values.clear();
							values.put(MyLocationDbHelper.C_CREATED, System.currentTimeMillis());
							values.put(MyLocationDbHelper.C_DESCRIPTION, description);
							values.put(MyLocationDbHelper.C_LONGITUDE, currentLocation.getLongitude());
							values.put(MyLocationDbHelper.C_LATITUDE, currentLocation.getLatitude());

							db.insertOrThrow(MyLocationDbHelper.TABLE, null, values);
							
							Log.d(TAG, "saved location");

							db.close();

						}
					});

			alert.setNegativeButton(R.string.cancel_button,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// user canceled.
						}
					});

			alert.show();
		}
		else {
			Context context = getApplicationContext();
			CharSequence text = getString(R.string.no_location_information);
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.show();
		}
	}

	private void showSavedLocations() {
		Intent i = new Intent(this, SavedLocationsActivity.class);
		startActivity(i);
	}

	@Override
	public void onLocationChanged(Location location) {
		currentLocation = location;
		
		Log.d(TAG, "Location changed: " + location.toString());
		
		tv.setText(getCurrentLocation());
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, "Location provider disabled");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(TAG, "Location provider enabled");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(TAG, "Location status changed");
	}
	
}
