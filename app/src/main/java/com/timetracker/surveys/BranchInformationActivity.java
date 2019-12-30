package com.timetracker.surveys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.timetracker.business.ConnectionMethods;
import com.timetracker.business.DialogMethods;
import com.timetracker.business.GPSTracker;
import com.timetracker.data.BranchItem;
import com.timetracker.data.Devices;
import com.timetracker.data.UbicheckRequest;
import com.timetracker.sqlite.MySQLiteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BranchInformationActivity extends AppCompatActivity {
    int branchID;
    TextInputEditText txtName, txtCardID, txtResponsableName, txtbusiness;
    ImageView back;
    Button save;
    // New GPS
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location lc;
    private GPSTracker GPSTracker;
    private int BiometricID = 0;
    BranchItem branchItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_information);

        MySQLiteHelper db = new MySQLiteHelper(BranchInformationActivity.this);
        Devices Device = db.GetDevice();

        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        GPSTracker = new GPSTracker(getApplicationContext());

        //NEW GPS
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        GetLocation(fusedLocationProviderClient, this);

        back = findViewById(R.id.back);

        save = findViewById(R.id.save);

        txtName = findViewById(R.id.txtName);

        txtCardID = findViewById(R.id.txtCardId);

        txtResponsableName = findViewById(R.id.txtResponsableName);

        txtbusiness = findViewById(R.id.txtbusiness);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String DistributionChanne = txtbusiness.getText().toString();

                String Name = txtName.getText().toString();

                String Identifier = txtCardID.getText().toString();

                String ContactName = txtResponsableName.getText().toString();

                String ModifiedUser = Device.Name;

                if(branchItem.BranchID != 0 && !Name.isEmpty() ) {
                    AsynUpdateBranch asynUpdateBranch = new AsynUpdateBranch(BranchInformationActivity.this);
                    asynUpdateBranch.execute("/Branches?BranchID="+branchItem.BranchID+"&Name="+Name+"&DistributionChanne="+DistributionChanne+"&Identifier="+Identifier+"&ContactName="+ContactName+"&ModifiedUser="+ModifiedUser);
                }
            }
        });

        UbicheckRequest UbicheckRequest = new UbicheckRequest(0, Device.DeviceID, GPSTracker.getLatitude(), GPSTracker.getLongitude(), new Date(), 0, BiometricID, GPSTracker.usesMockLocation);
        AsyncUbicheck AsyncUbicheck = new AsyncUbicheck(UbicheckRequest, false);
        AsyncUbicheck.execute("/Ubicheck");
    }


    private static class AsynUpdateBranch extends AsyncTask<String, Void, String> {
        Context context;
        public AsynUpdateBranch(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... params) {
            String resultado = "0";
            try {
                resultado = ConnectionMethods.GET(context,params[0]);
            } catch (Exception e) {
                return "Error: " + e.toString();
            }
            return resultado;
        }
        @Override
        protected void onPostExecute(String result) {
            if(result.equals("\"Success\"")) {
                Toast toas1t = Toast.makeText(context, "Datos Guardados", Toast.LENGTH_SHORT);
                toas1t.show();
            }
            else {
                Toast toas1t = Toast.makeText(context, "Los Datos no se pudieron Guardar ", Toast.LENGTH_SHORT);
                toas1t.show();
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private class AsyncUbicheck extends AsyncTask<String, Void, String> {
        com.timetracker.data.UbicheckRequest UbicheckRequest;
        Boolean SendToForm;

        public AsyncUbicheck(UbicheckRequest UbicheckRequest, Boolean SendToForm) {
            this.UbicheckRequest = UbicheckRequest;
            this.SendToForm = SendToForm;
        }

        @Override
        protected String doInBackground(String... params) {
            String resultado = "";
            try {
                JSONObject item = new JSONObject();
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                item.put("Status", UbicheckRequest.Status);
                item.put("DeviceID", UbicheckRequest.DeviceID);
                item.put("Lat", UbicheckRequest.Latitude);
                item.put("Lon", UbicheckRequest.Longitude);
                item.put("Date", ft.format(UbicheckRequest.Date));
                item.put("BranchID", UbicheckRequest.BranchID);
                item.put("BiometricID", UbicheckRequest.BiometricID);
                item.put("IsMock", UbicheckRequest.IsMock);
                resultado = ConnectionMethods.Post(BranchInformationActivity.this, item.toString(), params[0], false);
            } catch (Exception e) {
                resultado = "";
            }
            return resultado;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(String result) {
            try {
                final JSONObject JO = new JSONObject(result);
                if(JO.getInt("Status") > 1){
                    Toast toas1t = Toast.makeText(getApplicationContext(), "Usted no puede modificar local mientras tiene Ubicheck Abierto", Toast.LENGTH_LONG);
                    toas1t.show();
                    finish();
                }else {
                    JSONArray c = JO.getJSONArray("Branches");
                    List<BranchItem> Branches = new ArrayList<BranchItem>();

                    for (int i = 0 ; i < c.length(); i++) {
                        JSONObject obj = c.getJSONObject(i);
                        Branches.add(new BranchItem(
                                obj.getInt("BranchID"),
                                obj.getString("Name"),
                                obj.getDouble("Latitude"),
                                obj.getDouble("Longitude"),
                                obj.getString("Identifier"),
                                obj.getString("ContactName"),
                                obj.getString("DistributionChannel")));
                    }
                    if(Branches.size()== 0) {
                        Toast toas1t = Toast.makeText(getApplicationContext(), "No cuenta con ninguna Sucursal cerca", Toast.LENGTH_SHORT);
                        toas1t.show();
                        finish();
                    }
                    if(Branches.size() == 1){

                        branchItem =  Branches.get(0);

                        if(!branchItem.Name.equals("null")){
                            txtName.setText( branchItem.Name );
                        }
                        if(!branchItem.BussinessType.equals("null")){
                            txtbusiness.setText( branchItem.BussinessType );
                        }

                        if(!branchItem.Identifier.equals("null")){
                            txtCardID.setText(branchItem.Identifier);
                        }

                        if(!branchItem.TitularName.equals("null")){
                            txtResponsableName.setText(branchItem.TitularName);
                        }
                    }else {

                        branchItem =  NearBranch(Branches,GPSTracker.getLatitude(), GPSTracker.getLongitude());

                        if(!branchItem.Name.equals("null")){
                            txtName.setText( branchItem.Name );
                        }
                        if(!branchItem.BussinessType.equals("null")){
                            txtbusiness.setText( branchItem.BussinessType );
                        }

                        if(!branchItem.Identifier.equals("null")){
                            txtCardID.setText(branchItem.Identifier);
                        }

                        if(!branchItem.TitularName.equals("null")){
                            txtResponsableName.setText(branchItem.TitularName);
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private BranchItem NearBranch(List<BranchItem> branches, double latitude, double longitude) {
        BranchItem tempBranch = null;
        Double TempDistance = null,ActualDistance = null;
        for (int i = 0 ; i < branches.size(); i++) {
            if( i == 0 ){
                tempBranch = branches.get(i);
               TempDistance =distance(tempBranch.Latitude,tempBranch.Longitude,latitude,longitude);
            }else {
               ActualDistance = distance(tempBranch.Latitude,tempBranch.Longitude,latitude,longitude);
               if(ActualDistance > TempDistance){
                    TempDistance = ActualDistance;
                    tempBranch = branches.get(i);
               }
            }
        }
          return tempBranch;
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75;

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist;
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(InicializeLR(), InicializeLC(), null);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void GetLocation(FusedLocationProviderClient fusedLocationProviderClient, Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lc = location;
                    }
                }
            });
        } else {
            // request permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            }
        }
    }

    private LocationRequest InicializeLR() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10);
        locationRequest.setFastestInterval(15);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    ;

    private LocationCallback InicializeLC() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        lc = location;
                    }
                }
            }
        };
        return locationCallback;
    }
}
