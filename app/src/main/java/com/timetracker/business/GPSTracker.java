package com.timetracker.business;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import androidx.core.content.ContextCompat;

public class GPSTracker extends Service implements LocationListener {

	private final Context mContext;
	boolean canGetLocation = false;
	Location location;
	double latitude;
	double longitude;
	private static final long MIN_DISTANCE = 0;
	private static final long MIN_TIME = 1000 * 10;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	public int usesMockLocation;
	protected LocationManager locationManager;
	
	public GPSTracker(Context context)
	{
		this.mContext = context;
		getLocation();
	}
	
	public Location getLocation()
	{
		try
		{
			if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
				locationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);
				boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
				boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
				Location locationNetwork = null;
				Location locationGPS = null;
				if (isGPSEnabled || isNetworkEnabled) {
					this.canGetLocation = true;
					if (isNetworkEnabled) {
						locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME,MIN_DISTANCE,this);
						if (locationManager != null) {
							locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						}
					}
					if (isGPSEnabled) {
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DISTANCE,this);
						if (locationManager != null) {
							locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						}
					}
					if(locationNetwork != null && locationGPS != null){
						if(isBetterLocation(locationNetwork,locationGPS)){
							location = locationNetwork;
						}else{
							location = locationGPS;
						}
						latitude = location.getLatitude();
						longitude = location.getLongitude();
					}else if(locationGPS != null){
						location = locationGPS;
						latitude = location.getLatitude();
						longitude = location.getLongitude();
					}else if(locationNetwork != null){
						location = locationNetwork;
						latitude = location.getLatitude();
						longitude = location.getLongitude();
					}else{
						this.canGetLocation = false;
					}
				}else{
					this.canGetLocation = false;
				}
				if(location != null){
					CheckMockLocation(location);
				}
			}
		}
		catch(Exception ex)
		{
		}
		return location;
	}

	public void CheckMockLocation(Location Newlocation){
		try{
			if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				if(Newlocation.isFromMockProvider()){
					usesMockLocation = 1;
				}else{
					usesMockLocation = 0;
				}
			}else{
				if (Settings.Secure.getString(mContext.getContentResolver(),Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")){
					usesMockLocation = 0;
				}else{
					usesMockLocation = 1;
				}
			}
		}catch (Exception ex){

		}
	}
	
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	
	public void stopUsingGPS()
	{
		if(locationManager != null)
		{
			locationManager.removeUpdates(GPSTracker.this);
		}
	}
	
	
	public double getLatitude()
	{
		if(location != null)
		{
			latitude = location.getLatitude();
		}
		return latitude;
	}
	
	
	public double getLongitude()
	{
		if(location != null)
		{
			longitude = location.getLongitude();
		}
		return longitude;
	}
	
	public boolean canGetLocation()
	{
		return this.canGetLocation;
	}
	
	public void showSettingsAlert()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		alertDialog.setTitle("GPS is settings");
		alertDialog.setPositiveButton("settings", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	}
	
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		if(isBetterLocation(arg0,location)){
			location = arg0;
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			CheckMockLocation(location);
		}
	}
	
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		getLocation();
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		getLocation();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	

}


