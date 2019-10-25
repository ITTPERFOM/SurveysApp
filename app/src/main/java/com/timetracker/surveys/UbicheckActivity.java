package com.timetracker.surveys;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.timetracker.business.ConnectionMethods;
import com.timetracker.business.DialogMethods;
import com.timetracker.business.GPSTracker;
import com.timetracker.business.ImageMethods;
import com.timetracker.data.Devices;
import com.timetracker.data.UbicheckRequest;
import com.timetracker.data.UbicheckDetailsRequest;
import com.timetracker.sqlite.MySQLiteHelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.luxand.FSDK;

import static com.timetracker.surveys.SurveyActivity.calculateInSampleSize;


public class UbicheckActivity extends Activity {

	//================================================================================
	// Global Variables
	//================================================================================
	private ProgressDialog progressDialog;
	private GPSTracker GPSTracker;
	private LinearLayout lyButtons;
	private LinearLayout lyCheckOut;
	private LinearLayout lyActivityCheckIn;
	private LinearLayout lyActivityCheckOut;
	private List<Integer> ElementsValue;
	private int OpenUbicheckDetailID;
	private MySQLiteHelper db = new MySQLiteHelper(UbicheckActivity.this);
	private int UbicheckOption = 0;
	String[] Img = null;
	private int CheckInBranchID = 0;
	private boolean UsesBiometric = false;
	private int BiometricID = 0;
	private boolean UsesKioskMode = false;
	private int KioskBranchID = 0;
	//Luxand
	protected String _CameraImagePath;
	protected String _DataBaseImagePath;
	private boolean processingImage;
	protected Bitmap CameraBitmap;

	// New GPS
	private FusedLocationProviderClient fusedLocationProviderClient;
	private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
	private LocationRequest locationRequest;
	private LocationCallback locationCallback;
	private Location lc;
	private Button btnRefresh;
	public LinearLayout Animation, Ui;

	//	//================================================================================
	// Activity Events
	//================================================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ubicheck);

		btnRefresh = (Button) findViewById(R.id.btnRefresh);

		//ScanAnimation = (LottieAnimationView) findViewById(R.id.Scan);

		Animation = (LinearLayout) findViewById(R.id.Animation);

		Ui = (LinearLayout) findViewById(R.id.Ui);

		Animation.setVisibility(View.GONE);

		GPSTracker = new GPSTracker(getApplicationContext());
		//NEW GPS
		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

		GetLocation(fusedLocationProviderClient, this);

		//Luxand
		_CameraImagePath = Environment.getExternalStorageDirectory() + "/_CameraImage.jpg";
		_DataBaseImagePath = Environment.getExternalStorageDirectory() + "/_DataBaseImage.jpg";
		//
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(getBaseContext(), com.timetracker.surveys.HomeActivity.class);
				startActivity(intent);
				finish();
			}
		};
		Devices Device = db.GetDevice();
		if (Device == null || Device.Status != 2) {
			DialogMethods.showInformationDialog(UbicheckActivity.this, "Dispositivo sin registrar", "Dispositivo sin registrar. Favor de registrarlo para continuar.", onClickListener);
			return;
		}
		if (Device.UsesBiometric == 1 && Device.BiometricID == 0) {
			DialogMethods.showInformationDialog(UbicheckActivity.this, "Biometrico sin registrar", "Biometrico sin registrar. Favor de registrarlo para continuar.", onClickListener);
			return;
		}
		if (Device.UsesBiometric == 1 && Device.UsesKioskMode == 1 && Device.KioskBranchID == 0) {
			DialogMethods.showInformationDialog(UbicheckActivity.this, "Biometrico sin registrar", "Kiosco sin sucursal asignada. Favor de agregar sucursal para continuar.", onClickListener);
			return;
		}
		if (Device.UsesBiometric == 1) {
			UsesBiometric = true;
			BiometricID = Device.BiometricID;
			if (Device.UsesKioskMode == 1) {
				UsesKioskMode = true;
				KioskBranchID = Device.KioskBranchID;
			}
		}

		if (!ConnectionMethods.isInternetConnected(UbicheckActivity.this, false).equals("")) {
			DialogMethods.showInformationDialog(UbicheckActivity.this, "Sin Conexion", "No se detecto una conexion a internet. Favor de conectarse a una red antes de volver a intentar.", onClickListener);
			return;
		}
		if (ContextCompat.checkSelfPermission(UbicheckActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			int isPermited = 0;
			ActivityCompat.requestPermissions(UbicheckActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, isPermited);
			return;
		}


		if (!processingImage) {
			Handler handlers = new Handler();
			handlers.postDelayed(new Runnable() {
				public void run() {
					Refresh(null);
				}
			}, 1000);
		}


		if (Device.UsesCreateBranch == 0) {
			LinearLayout AddNewBranch = (LinearLayout) findViewById(R.id.AddNewBranch);
			AddNewBranch.setVisibility(View.GONE);
		}
		btnRefresh.setVisibility(View.INVISIBLE);

		Handler btn = new Handler();
		btn.postDelayed(new Runnable() {
			public void run() {
				btnRefresh.setVisibility(View.VISIBLE);
			}
		}, 20000);
	}

	public void onDestroy() {
		super.onDestroy();
		if (UsesKioskMode) {
			Devices Device = db.GetDevice();
			Device.BiometricID = 0;
			Device.ImageWareRegister = 0;
			db.updateDevice(Device);
		}
		db.close();
	}

	public void AddBranch(View view) {
		Intent intent = new Intent(getBaseContext(), com.timetracker.surveys.UbicheckNewActivity.class);
		startActivity(intent);
		finish();
	}

	public void CheckOut(View view) {
		if (isOnline()) {
			if (UsesBiometric) {
				UbicheckOption = 2;
				showSpinner("Registrando Salida");
				AuthenticateUser(BiometricID);
			} else {
				DoUbicheckCheckOut();
			}
		} else {
			buildAlertMessageNoOnline();
		}
	}

	public void ActivityCheckIn(View view) {
		if (UsesBiometric) {
			UbicheckOption = 3;
			showSpinner("Registrando Actividad");
			AuthenticateUser(BiometricID);
		} else {
			DoActivityCheckIn();
		}
	}

	public void ActivityCheckOut(View view) {
		if (UsesBiometric) {
			UbicheckOption = 4;
			showSpinner("Cerrando Actividad");
			AuthenticateUser(BiometricID);
		} else {
			DoActivityCheckOut();
		}
	}

	//================================================================================
	// Main Processes
	//================================================================================

	public void AuthenticateUser(int BiometricID) {
		VerifyPersonWithLuxand();
	}

	public void Refresh(View view) {
		if (lc == null) {
			final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				buildAlertMessageNoGps();
			}
		} else {
			LinearLayout layoutRefresh = (LinearLayout) findViewById(R.id.layoutRefresh);
			layoutRefresh.setVisibility(View.GONE);
			//showSpinner("Buscando Sucursales");
			Devices Device = db.GetDevice();
			UbicheckRequest UbicheckRequest = new UbicheckRequest(0, Device.DeviceID, lc.getLatitude(), lc.getLongitude(), new Date(), 0, BiometricID, GPSTracker.usesMockLocation);
			AsyncUbicheck AsyncUbicheck = new AsyncUbicheck(UbicheckRequest, false);
			AsyncUbicheck.execute("/Ubicheck");
		}
	}

	public void DoUbicheckCheckIn() {
		showSpinner("Registrando Entrada");
		Devices Device = db.GetDevice();
		UbicheckRequest UbicheckRequest = new UbicheckRequest(1, Device.DeviceID, lc.getLatitude(), lc.getLongitude(), new Date(), CheckInBranchID, BiometricID, GPSTracker.usesMockLocation);
		AsyncUbicheck AsyncUbicheck = new AsyncUbicheck(UbicheckRequest, true);
		AsyncUbicheck.execute("/Ubicheck");
	}

	public void DoUbicheckStatus() {
		Devices Device = db.GetDevice();
		UbicheckRequest UbicheckRequest = new UbicheckRequest(0, Device.DeviceID, lc.getLatitude(), lc.getLongitude(), new Date(), 0, BiometricID, GPSTracker.usesMockLocation);
		AsyncUbicheck AsyncUbicheck = new AsyncUbicheck(UbicheckRequest, false);
		AsyncUbicheck.execute("/Ubicheck");
	}

	public void DoUbicheckCheckOut() {
		showSpinner("Registrando Salida");
		Devices Device = db.GetDevice();
		UbicheckRequest UbicheckRequest = new UbicheckRequest(2, Device.DeviceID, lc.getLatitude(), lc.getLongitude(), new Date(), 0, BiometricID, GPSTracker.usesMockLocation);
		AsyncUbicheck AsyncUbicheck = new AsyncUbicheck(UbicheckRequest, false);
		AsyncUbicheck.execute("/Ubicheck");
	}

	public void DoActivityCheckIn() {
		showSpinner("Registrando Actividad");
		Spinner ActivityCheckInSpinner = (Spinner) findViewById(R.id.ActivityCheckInSpinner);
		int ElementID = ElementsValue.get(ActivityCheckInSpinner.getSelectedItemPosition());
		Devices Device = db.GetDevice();
		UbicheckDetailsRequest UbicheckDetailsRequest = new UbicheckDetailsRequest(0, ElementID, Device.DeviceID, lc.getLatitude(), lc.getLongitude(), new Date(), BiometricID);
		AsyncUbicheckDetails AsyncUbicheckDetails = new AsyncUbicheckDetails(UbicheckDetailsRequest);
		AsyncUbicheckDetails.execute("/UbicheckDetails");
		//db.Data(140);
	}

	public void DoActivityCheckOut() {
		showSpinner("Cerrando Actividad");
		int UbicheckDetailID = OpenUbicheckDetailID;
		Devices Device = db.GetDevice();
		UbicheckDetailsRequest UbicheckDetailsRequest = new UbicheckDetailsRequest(UbicheckDetailID, 0, Device.DeviceID, lc.getLatitude(), lc.getLongitude(), new Date(), BiometricID);
		AsyncUbicheckDetails AsyncUbicheckDetails = new AsyncUbicheckDetails(UbicheckDetailsRequest);
		AsyncUbicheckDetails.execute("/UbicheckDetails");
		//db.Data(140);
	}

	//================================================================================
	// Web Services
	//================================================================================

	@SuppressLint("SimpleDateFormat")
	private class AsyncUbicheck extends AsyncTask<String, Void, String> {
		UbicheckRequest UbicheckRequest;
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
				resultado = ConnectionMethods.Post(UbicheckActivity.this, item.toString(), params[0], false);
			} catch (Exception e) {
				resultado = "";
			}
			return resultado;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String result) {
			hideSpinner();
			try {
				if (!result.equals("\"\"")) {
					final JSONObject JO = new JSONObject(result);
					final int Status = JO.getInt("Status");
					final String message = JO.getString("Message");
					if (Status == 0 || Status == 3) {
						DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Devices Device = db.GetDevice();
								if (SendToForm && Device.UsesFormWithUbicheck == 1) {
									Intent intent = new Intent(getBaseContext(), com.timetracker.surveys.SelectSurvey.class);
									int UbicheckID = 0;
									String DistributionChannel = "";
									try {
										UbicheckID = JO.getInt("UbicheckID");
										DistributionChannel = JO.getString("DistributionChannel");
									} catch (JSONException e) {
									}
									intent.putExtra("UbicheckID", UbicheckID);
									if (Device.UsesClientValidation == 1) {
										intent.putExtra("DistributionChannel", DistributionChannel);
									}
									dialog.dismiss();
									startActivity(intent);
									finish();
								} else {
									if (Status == 0) {
										LinearLayout layoutRefresh = (LinearLayout) findViewById(R.id.layoutRefresh);
										layoutRefresh.setVisibility(View.VISIBLE);
										dialog.dismiss();
									} else {
										dialog.dismiss();
										if (UsesKioskMode) {
											Intent intent = new Intent(getBaseContext(), com.timetracker.surveys.SelectBiometricActivity.class);
											startActivity(intent);
											finish();
										} else {
											Intent intent = new Intent(getBaseContext(), com.timetracker.surveys.HomeActivity.class);
											startActivity(intent);
											finish();
										}
									}
								}
							}
						};
						if (JO.getString("Message").equals("Entrada Registrada") || JO.getString("Message").equals("Salida Registrada")) {
							SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							DialogMethods.showSuccessDialog(UbicheckActivity.this, "Mensaje Ubicheck", JO.getString("Message") + "\n\nFecha:" + ft.format(UbicheckRequest.Date), onClickListener);
						} else {
							if (JO.getString("Message").equals("No se encontraron Sucursales")) {
								buildAlertnobranch();
							} else {
								DialogMethods.showInformationDialog(UbicheckActivity.this, "Mensaje Ubicheck", JO.getString("Message"), onClickListener);
							}
						}
					} else if (Status == 1) {
						JSONArray jBranches = JO.getJSONArray("Branches");
						if (UsesKioskMode) {
							boolean containsKioskBranch = false;
							if (jBranches.length() > 0) {
								for (int j = 0; j < jBranches.length(); j++) {
									JSONObject jBranch = jBranches.getJSONObject(j);
									if (jBranch.getInt("BranchID") == KioskBranchID) {
										containsKioskBranch = true;
										break;
									}
								}
							}
							if (containsKioskBranch) {
								CheckInBranchID = KioskBranchID;
								UbicheckOption = 1;
								showSpinner("Registrando Entrada");
								AuthenticateUser(BiometricID);
							} else {
								hideSpinner();
								DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										SendHome(null);
									}
								};
								DialogMethods.showInformationDialog(UbicheckActivity.this, "Sucursal de kiosco NO se encuentra cerca.", JO.getString("Message"), onClickListener);
							}
						} else {
							lyButtons = (LinearLayout) findViewById(R.id.Buttons);
							for (int j = 0; j < jBranches.length(); j++) {
								JSONObject jBranch = jBranches.getJSONObject(j);
								Button newButton = new Button(UbicheckActivity.this);
								newButton.setText(jBranch.getString("Branch"));
								newButton.setId(jBranch.getInt("BranchID"));
								newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_checkin));
								newButton.setTextAppearance(getApplicationContext(), R.style.button_text);
								LinearLayout.LayoutParams tdf = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
								tdf.setMargins(0, 10, 0, 10);
								newButton.setLayoutParams(tdf);
								newButton.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										if (isOnline()) {
											CheckInBranchID = ((Button) v).getId();
											if (UsesBiometric) {
												UbicheckOption = 1;
												showSpinner("Registrando Entrada");
												AuthenticateUser(BiometricID);
											} else {
												DoUbicheckCheckIn();
											}
										} else {
											buildAlertMessageNoOnline();
										}
									}
								});
								LinearLayout linLayout = new LinearLayout(UbicheckActivity.this);
								linLayout.addView(newButton);
								lyButtons.addView(linLayout);
							}
							lyButtons.setVisibility(View.VISIBLE);
						}

					} else if (Status == 2) {
						lyCheckOut = (LinearLayout) findViewById(R.id.CheckOut);
						lyCheckOut.setVisibility(View.VISIBLE);
						TextView CheckOutMessage = (TextView) findViewById(R.id.CheckOutMessage);
						CheckOutMessage.setText(JO.getString("Message"));
						Devices Device = db.GetDevice();
						if (Device.UsesUbicheckDetails == 1) {
							if (JO.getInt("OpenUbicheckDetailID") == 0) {
								lyActivityCheckIn = (LinearLayout) findViewById(R.id.ActivityCheckIn);
								lyActivityCheckIn.setVisibility(View.VISIBLE);
								JSONArray jElements = JO.getJSONArray("Elements");
								List<String> ElementsText = new ArrayList<String>();
								ElementsValue = new ArrayList<Integer>();
								if (jElements.length() > 0) {
									for (int j = 0; j < jElements.length(); j++) {
										JSONObject jElement = jElements.getJSONObject(j);
										ElementsText.add(jElement.getString("Name"));
										ElementsValue.add(jElement.getInt("ElementID"));
									}
								}
								ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(UbicheckActivity.this, android.R.layout.simple_spinner_item, ElementsText);
								Spinner ActivityCheckInSpinner = (Spinner) findViewById(R.id.ActivityCheckInSpinner);
								ActivityCheckInSpinner.setAdapter(dataAdapter);
							} else {
								lyCheckOut.setVisibility(View.GONE);
								lyActivityCheckOut = (LinearLayout) findViewById(R.id.ActivityCheckOut);
								lyActivityCheckOut.setVisibility(View.VISIBLE);
								TextView ActivityCheckOutMessage = (TextView) findViewById(R.id.ActivityCheckOutMessage);
								String Actividad = "Actividad actual: " + JO.getString("OpenUbicheckDetailName");
								ActivityCheckOutMessage.setText(Actividad);
								OpenUbicheckDetailID = JO.getInt("OpenUbicheckDetailID");
							}
						}

					}
					if (Status == 3) {
						if (message.equalsIgnoreCase("Entrada Registrada")) {
							db.AppendUbicheckID(JO.getInt("UbicheckID"));
						}
						if (message.equalsIgnoreCase("Salida Registrada")) {
							db.DeleteUbicheckIDFromActualUbicheck();
						}
					}
				}
			} catch (Exception ex) {
				DialogMethods.showErrorDialog(UbicheckActivity.this, "Ocurrio un error al momento de utilizar Ubicheck. Result:" + result + " Info: " + ex.toString(), " Result:" + result + " Activity:Ubicheck | Method:AsyncUbicheck | Error:" + ex.toString());
			}
		}
	}

	private void buildAlertnobranch() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("         Sucursales no encontradas")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						SendHome(null);
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@SuppressLint("SimpleDateFormat")
	private class AsyncUbicheckDetails extends AsyncTask<String, Void, String> {
		UbicheckDetailsRequest UbicheckDetailsRequest;

		public AsyncUbicheckDetails(UbicheckDetailsRequest UbicheckDetailsRequest) {
			this.UbicheckDetailsRequest = UbicheckDetailsRequest;
		}

		@Override
		protected String doInBackground(String... params) {
			String resultado = "";
			try {
				JSONObject item = new JSONObject();
				SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				item.put("UbicheckDetailID", UbicheckDetailsRequest.UbicheckDetailID);
				item.put("ElementID", UbicheckDetailsRequest.ElementID);
				item.put("DeviceID", UbicheckDetailsRequest.DeviceID);
				item.put("Lat", UbicheckDetailsRequest.Lat);
				item.put("Lon", UbicheckDetailsRequest.Lon);
				item.put("Date", ft.format(UbicheckDetailsRequest.Date));
				item.put("BiometricID", UbicheckDetailsRequest.BiometricID);
				resultado = ConnectionMethods.Post(UbicheckActivity.this, item.toString(), params[0], false);
			} catch (Exception e) {
				resultado = "";
			}
			return resultado;
		}

		@Override
		protected void onPostExecute(String result) {
			hideSpinner();
			try {
				if (result.equals("\"1\"")) {
					DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (UsesBiometric && UsesKioskMode) {
								Intent intent = new Intent(getBaseContext(), com.timetracker.surveys.SelectBiometricActivity.class);
								startActivity(intent);
								finish();
							} else {
								Intent intent = new Intent(getBaseContext(), com.timetracker.surveys.HomeActivity.class);
								startActivity(intent);
								finish();
							}
						}
					};
					DialogMethods.showInformationDialog(UbicheckActivity.this, "Mensaje Ubicheck", "Se ha guardado la actividad", onClickListener);
				} else {
					DialogMethods.showErrorDialog(UbicheckActivity.this, "Ocurrio un error al momento de utilizar Actividades Ubicheck. Result:" + result, " Result:" + result + " Activity:Ubicheck | Method:AsyncUbicheckDetails");
				}
			} catch (Exception ex) {
				DialogMethods.showErrorDialog(UbicheckActivity.this, "Ocurrio un error al momento de utilizar Actividades Ubicheck. Result:" + result + " Info: " + ex.toString(), " Result:" + result + " Activity:Ubicheck | Method:AsyncUbicheckDetails | Error:" + ex.toString());
			}
		}
	}

	//================================================================================
	// Luxand Methods
	//================================================================================

	public void VerifyPersonWithLuxand() {
		processingImage = true;
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
		File file = new File(_CameraImagePath);
		Uri outputFileUri = Uri.fromFile(file);
		//Select Photo
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			switch (resultCode) {
				case 0:
					hideSpinner();
					break;
				case -1:
					onPhotoTaken();
					break;
			}
		} catch (Exception e) {
			Toast toas1t = Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
			toas1t.show();
		}
	}

	protected void onPhotoTaken() {



		new UbicheckImageRequest(BiometricID,decodeFile(_CameraImagePath)).execute();

		//try {
			//new AsyncGetPicture().execute("/Biometrics?BiometricID=" + BiometricID + "&Provider=Luxand");
	//	} catch (Exception ex) {
	//		if (isOnline()) {
	//			Toast.makeText(getApplicationContext(), "Parece que usted No cuenta con ninguna fotografia en el sietema", Toast.LENGTH_SHORT).show();
	//		} else {
	//			Toast.makeText(getApplicationContext(), "Usted No se encuentra conectado a internet ", Toast.LENGTH_SHORT).show();
	//		}
	//		Ui.setVisibility(View.VISIBLE);
	//		Animation.setVisibility(View.GONE);
	//	}
	}

	private class AsyncGetPicture extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return ConnectionMethods.GET(UbicheckActivity.this, urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != "") {
				try {
					Animation.setVisibility(View.VISIBLE);
					Ui.setVisibility(View.INVISIBLE);
					JSONArray jsonArray = new JSONArray(result);
					Img = new String[jsonArray.length()];
					for (int i = 0; i < jsonArray.length(); i++) {
						Img[i] = jsonArray.getString(i);
					}
					new AsyncComparePictures().execute();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(UbicheckActivity.this, " Usted no cuenta con Imagenes ", Toast.LENGTH_LONG).show();
				Log.d("PRUEBA", " La codificacion de imagenes no son correctas ");
			}
		}
	}


	private class AsyncComparePictures extends AsyncTask<Void, Void, Boolean> {
		ArrayList<Bitmap> DataBaseBitmap = new ArrayList<Bitmap>();
		float Similarity[] = new float[1];
		float MatchingThreshold[] = new float[1];

		@Override
		protected Boolean doInBackground(Void... args) {
			try {
				for (int i = 0; i < Img.length; i++) {
					DataBaseBitmap.add(BitmapFactory.decodeStream((InputStream) new URL(Img[i]).getContent()));
				}
			} catch (Exception e) {
				Log.d("PRUEBA", " La codificacion de imagenes no son correctas ");
			}
			FSDK.HImage CameraImage = new FSDK.HImage();
			FSDK.LoadImageFromFile(CameraImage, _CameraImagePath);
			FSDK.FSDK_FaceTemplate template1 = new FSDK.FSDK_FaceTemplate();

			FSDK.GetFaceTemplate(CameraImage, template1);

			for (int i = 0; i < DataBaseBitmap.size(); i++) {
				ImageMethods.CreateImageFile(DataBaseBitmap.get(i), _DataBaseImagePath);

				FSDK.HImage DataBaseImage = new FSDK.HImage();

				FSDK.LoadImageFromFile(DataBaseImage, _DataBaseImagePath);

				FSDK.FSDK_FaceTemplate template2 = new FSDK.FSDK_FaceTemplate();

				FSDK.GetFaceTemplate(DataBaseImage, template2);

				FSDK.GetMatchingThresholdAtFAR((float) 0.42, MatchingThreshold);

				FSDK.MatchFaces(template1, template2, Similarity);

				if (Similarity[0] > MatchingThreshold[0]) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				FaceDoUbicheck(UbicheckOption);
			} else {
				Intent intent = new Intent(getBaseContext(), com.timetracker.surveys.HomeActivity.class);
				startActivity(intent);
				finish();
			}

		}
	}

	//================================================================================
	// Menu Options
	//================================================================================

	private void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(UbicheckActivity.this, toast, Toast.LENGTH_LONG).show();
			}
		});
	}

	private void showSpinner(String Message) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(Message);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	private void hideSpinner() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog != null)
					progressDialog.cancel();
			}
		});
	}

	//================================================================================
	// Menu Options
	//================================================================================

	public void SendHome(View view) {
		if (UsesKioskMode) {
			Intent intent = new Intent(getBaseContext(), com.timetracker.surveys.SelectBiometricActivity.class);
			startActivity(intent);
			finish();
		} else {
			Intent intent = new Intent(getBaseContext(), com.timetracker.surveys.HomeActivity.class);
			startActivity(intent);
			finish();
		}
	}
	//================================================================================
	// NEW GPS METHODS
	//================================================================================

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode) {
			case MY_PERMISSION_REQUEST_FINE_LOCATION:

				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					//permission was granted do nothing and carry on
				} else {
					Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_SHORT).show();
					finish();
				}
				break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		startLocationUpdates(this);
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

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void stopLocationUpdates() {
		fusedLocationProviderClient.removeLocationUpdates(locationCallback);
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

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Parece que su GPS esta apagado Quiere prenderlo?")
				.setCancelable(false)
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void buildAlertMessageNoOnline() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Parece que no esta conectado a internet, quiere activar su conexión?")
				.setCancelable(false)
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	protected boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressLint("MissingPermission") NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		} else {
			return false;
		}
	}

	private void FaceDoUbicheck(int option) {
		switch (option) {
			case 1:
				DoUbicheckCheckIn();
				break;
			case 2:
				DoUbicheckCheckOut();
				break;
			case 3:
				DoActivityCheckIn();
				break;
			case 4:
				DoActivityCheckOut();
				break;
		}
	}

	private class UbicheckImageRequest extends AsyncTask<String, Void, String> {
		int BiometricID;
		String encodedImage;

		public UbicheckImageRequest(int BiometricID,String encodedImage) {
			this.BiometricID = BiometricID;
			this.encodedImage = encodedImage;
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				JSONObject item = new JSONObject();
				item.put("BiometricID", BiometricID);
				item.put("image", encodedImage);
				String result = ConnectionMethods.Post(UbicheckActivity.this, item.toString(), "/UbicheckImage", true);
				return result;
			} catch (Exception e) {
				return "Error:" + e.toString();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.startsWith("Error")) {
				hideSpinner();
				Ui.setVisibility(View.VISIBLE);
				Toast.makeText(UbicheckActivity.this, result, Toast.LENGTH_LONG).show();
			} else {
				try {
					JSONObject JO = new JSONObject(result);
					if (JO.getBoolean("success")) {
						FaceDoUbicheck(UbicheckOption);
						hideSpinner();
					} else {
						hideSpinner();
						Ui.setVisibility(View.VISIBLE);
						Toast.makeText(UbicheckActivity.this, JO.getString("message"), Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					hideSpinner();
					Ui.setVisibility(View.VISIBLE);
					Toast.makeText(UbicheckActivity.this, "Error al esperar servicio:" + e.toString(), Toast.LENGTH_LONG).show();
				}
			}
		}
	}
	public  String decodeFile(String path) {
		int orientation;
		try {
			if (path == null) {
				return null;
			}
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			o.inSampleSize = calculateInSampleSize(o, 580, 680);
			o.inJustDecodeBounds = false;
			Bitmap bm = BitmapFactory.decodeFile(path, o);
			ExifInterface exif = new ExifInterface(path);
			File fdelete = new File(path);
			if (fdelete.exists()) {
				fdelete.delete();
			}
			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
			Matrix m = new Matrix();
			if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
				m.postRotate(180);
				Bitmap rotated = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				rotated.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
				byte[] b = baos.toByteArray();
				return Base64.encodeToString(b, Base64.DEFAULT);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				m.postRotate(90);
				Bitmap rotated = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				rotated.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
				byte[] b = baos.toByteArray();
				return Base64.encodeToString(b, Base64.DEFAULT);
			}
			else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				m.postRotate(270);
				Bitmap rotated = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				rotated.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
				byte[] b = baos.toByteArray();
				return Base64.encodeToString(b, Base64.DEFAULT);
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
			byte[] b = baos.toByteArray();
			return Base64.encodeToString(b, Base64.DEFAULT);
		} catch (Exception e) {
			return null;
		}
	}
}
