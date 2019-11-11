package com.timetracker.surveys;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
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
import com.timetracker.data.Answers;
import com.timetracker.data.Devices;
import com.timetracker.data.QuestionOptions;
import com.timetracker.data.QuestionSentences;
import com.timetracker.data.Questions;
import com.timetracker.data.SelectedSurvey;
import com.timetracker.data.Surveys;
import com.timetracker.data.Tracker;
import com.timetracker.sqlite.MySQLiteHelper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;





public class HomeActivity extends Activity {

	//================================================================================
	// Global Variables
	//================================================================================

	Timer timer;
	TimerTask timerTask;
	GPSTracker GPSTracker;
	static ProgressDialog progress;

	final Handler handler = new Handler();
	public  Boolean Authenticated = false;
	SharedPreferences sharedpreferences;
	public MySQLiteHelper db = new MySQLiteHelper(HomeActivity.this);
	final int SurveyCount = 0;

	// New GPS
	private FusedLocationProviderClient fusedLocationProviderClient;
	private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
	private LocationRequest locationRequest;
	private LocationCallback locationCallback;
	private Location lc;
	private Button CheckIn;
	private Devices Device = null;
	private TextView txtSelectedSurveyHeader ;
	private TextView txtSelectedSurveyText ;
	private Button btnSelectSurvey ;
	private Button btnDownloadSurvey;
	private Button btnUploadAnswers;
	private Button btnCheckIn;
	private Button btnStartSurvey;

	//public SurveyPhotoFragment surveyPhoto;
	public android.app.FragmentTransaction fragmentTransaction;

	//================================================================================
	// Activity Events
	//================================================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GPSTracker = new GPSTracker(getApplicationContext());
		progress = new ProgressDialog(HomeActivity.this);
		progress.setCancelable(false);
		db.InitTableDataUsage();
		setContentView(R.layout.activity_home);
		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

		db.CheckifAllTableAreCreate();

		CheckIn = (Button)findViewById(R.id.btnCheckIn);

		 txtSelectedSurveyHeader = (TextView) findViewById(R.id.txtSelectedSurveyHeader);
		 txtSelectedSurveyText = (TextView) findViewById(R.id.txtSelectedSurveyText);

		 btnSelectSurvey = (Button) findViewById(R.id.btnSelectSurvey);
		 btnDownloadSurvey = (Button) findViewById(R.id.btnDownloadSurvey);
		 btnUploadAnswers = (Button) findViewById(R.id.btnUploadAnswers);
		 btnCheckIn = (Button) findViewById(R.id.btnCheckIn);
		 btnStartSurvey = (Button) findViewById(R.id.btnStartSurvey);

		 btnCheckIn.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 try {
					 File sd = Environment.getExternalStorageDirectory();

					 if (sd.canWrite()) {
						 String currentDBPath = "/data/data/" + getPackageName() + "/databases/SurveysDB";
						 File currentDB = new File(currentDBPath);
						 String backupDBPath = "backDB.db";
						 File backupDB = new File(sd, backupDBPath);


						 if (currentDB.exists()) {

							 FileChannel src = new FileInputStream(currentDB).getChannel();
							 FileChannel dst = new FileOutputStream(backupDB).getChannel();
							 dst.transferFrom(src, 0, src.size());
							 src.close();
							 dst.close();


							 StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
							 StrictMode.setVmPolicy(builder.build());
							 Log.d("Prueba", "Existe DB");
							 Uri path = Uri.fromFile(backupDB);
							 Intent emailIntent = new Intent(Intent.ACTION_SEND);
							 // set the type to 'email'
							 emailIntent .setType("vnd.android.cursor.dir/email");
							 //String to[] = {"Daniel@timetracker"};
							 emailIntent .putExtra(Intent.EXTRA_EMAIL,"erick.furlong@timetracker.com.mx");
							 // the attachment
							 emailIntent .putExtra(Intent.EXTRA_STREAM, path);
							 // the mail subject
							 emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Base de datos Formax");
							 emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
							 HomeActivity.this.startActivity(Intent.createChooser(emailIntent , "Envia DB ..."));
						 }
					 }
				 } catch (Exception e) {
					 Log.d("Prueba", "No existe DB  " + e.toString());
				 }
			 }
		 });


		 

		GetLocation(fusedLocationProviderClient,this);
		try
		{
			Device = db.GetDevice();
			if(Device == null){
				if(isConnected()){
					Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.RegisterActivity.class);
					startActivity(intent);
					finish();
				}
			}
			TextView txtfooter = (TextView) findViewById(R.id.txtfooter);
			txtfooter.setText("Favor de registrar el dispositivo");
			Buttons(true);
			SelectedSurvey SelectedSurvey = db.GetSelectedSurvey();
			String txtSelectedSurvey = "";
			if(SelectedSurvey != null){
				txtSelectedSurvey = SelectedSurvey.SurveyName;
			}else {
				btnStartSurvey.setEnabled(false);
			}
			if(!txtSelectedSurvey.equals("")){
				txtSelectedSurveyHeader.setText("Forma Seleccionada");
				txtSelectedSurveyText.setText(txtSelectedSurvey);
				btnStartSurvey.setEnabled(true);
			}else {
				btnStartSurvey.setEnabled(false);
			}
		}
		catch(Exception ex){
			DialogMethods.showErrorDialog(HomeActivity.this, "Ocurrio un error al momento de iniciar aplicacion. Info: " + ex.toString(),"Activity:Home | Method:onCreate | Error:" + ex.toString());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		InitialVerification();
		startLocationUpdates(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(timer != null) {
			timer.cancel();
			timer = null;
		}
		db.close();
		Runtime.getRuntime().gc();
	}

	public void InitialVerification(){
		Devices Device = db.GetDevice();
		TextView txtfooter = (TextView) findViewById(R.id.txtfooter);
		if(Device != null){
			switch(Device.Status){
				case 1:
					startTimer();
					txtfooter.setText("Dispositivo en espera de autorizacion");
					break;
				case 2:
					startTimer();
					if(!progress.isShowing()){
						Buttons(true);
						txtfooter.setText("");
					}
					break;
				case 3:
					txtfooter.setText("Dispositivo rechazado");
					break;
				default:
					break;
			}
		}
	}

	//================================================================================
	// Timer Methods
	//================================================================================

	public void startTimer() {
		timer = new Timer();
		initializeTimerTask();
		timer.schedule(timerTask, 1, 60000); //
	}

	public void stopTimer(View v) {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void initializeTimerTask() {
		timerTask = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						if(isConnected()){
							Devices Device = db.GetDevice();
							String VersionName = "";
							PackageInfo pInfo = null;
							try {
								pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
								VersionName = pInfo.versionName;
							} catch (NameNotFoundException e) {
							}
							new AsyncCheckDevice().execute("/Devices?DeviceID=" + Device.DeviceID + "&AppVersion=" + VersionName);
						}
					}
				});
			}
		};
	}

	//================================================================================
	// Assign Message
	//================================================================================

	private static final Handler ProgressMessageHandler = new Handler() {
		public void handleMessage(Message msg) {
			final String what = (String)msg.obj;
			updateMessage(what);
		}
	};

	private static void updateMessage(String strMessage) {
		progress.setMessage(strMessage);
	}

	//================================================================================
	// Start Survey
	//================================================================================

	public void StartSurvey(View view){
		Intent intent = new Intent(this,com.timetracker.surveys.StartSurvey.class);
		startActivity(intent);
		finish();
	}

	//================================================================================
	// Select Survey
	//================================================================================

	public void SelectSurvey(View view){
		Intent intent = new Intent(this,SelectSurvey.class);
		if(Device.UsesFormWithUbicheck == 1){
			intent.putExtra("extra", true);
		}
		startActivity(intent);
		finish();
	}

	//================================================================================
	// Update Surveys
	//================================================================================

	public void DownloadSurvey(View view){
		if(isConnected()){
			String message = "Buscando Formas";
			Message msg = Message.obtain();
			msg.obj = message;
			ProgressMessageHandler.sendMessage(msg);
			progress.show();
			Buttons(false);
			Devices Device = db.GetDevice();
			AsyncUpdateSurveys AsyncUpdateSurveys = new AsyncUpdateSurveys();
			AsyncUpdateSurveys.execute("/Surveys/" + Device.DeviceID);
		}
	}

	//================================================================================
	// Upload Answers
	//================================================================================

	public void UploadAnswers(View view){
		try
		{
			if(isConnected()){
				Buttons(false);
				int respuestas = db.getDistincAnswersQty();
				if(respuestas > 0){
					String message = "Preparando informacion";
					Message msg = Message.obtain();
					msg.obj = message;
					ProgressMessageHandler.sendMessage(msg);
					progress.show();
					new AsyncUploadAnswers().execute("/Answers");
				}else{
					respuestas = db.getDistincTrackersQty();
					if(respuestas > 0){
						String message = "Subiendo ubicaciones";
						Message msg = Message.obtain();
						msg.obj = message;
						ProgressMessageHandler.sendMessage(msg);
						progress.show();
						Tracker Tracker = db.GetTracker();
						AsynTrackerCreate AsynTrackerCreate = new AsynTrackerCreate(Tracker);
						AsynTrackerCreate.execute("/Trackers");
					}
				}
			}else{
				Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
				startActivity(intent);
				finish();
			}
		}catch(Exception ex) {
			DialogMethods.showErrorDialog(HomeActivity.this, "Ocurrio un error al momento de subir resultados. Info: " + ex.toString(),"Activity:Home | Method:UploadAnswers | Error:" + ex.toString());
		}
	}

	//================================================================================
	// Menu Options
	//================================================================================

	public void RegisterDevices(View view){
		Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.RegisterActivity.class);
		startActivity(intent);
		finish();
	}

	public void SendHome(View view){
		Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
		startActivity(intent);
		finish();
	}

	public void SendInformation(View view){
		Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.InformationActivity.class);
		startActivity(intent);
		finish();
	}

	public void SendSettings(View view){
		Devices Device = db.GetDevice();
		if(isConnected()){
			if(Device != null && Device.UsesBiometric == 1 && Device.UsesKioskMode == 1){
				Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.SelectBiometricActivity.class);
				startActivity(intent);
				finish();
			}else{
				Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.UbicheckActivity.class);
				startActivity(intent);
				finish();
			}
		}else{
			buildAlertMessageNoOnline();
		}
	}

	//================================================================================
	// Check In
	//================================================================================

	public void CheckIn(final View view){
		//db.Data(140);
		CheckIn.setText("Enviando Ubicacion");
		CheckIn.setBackgroundColor(ContextCompat.getColor(this, R.color.Neutra));
		if(lc == null){
			final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
			if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
				buildAlertMessageNoGps();
			}
		}else {
			try {
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						Devices Device = db.GetDevice();
						if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
							int isPermited = 0;
							ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, isPermited);
						} else {
							if (lc != null) {
								Buttons(false);

								String message = "Registrando Ubicacion";
								Message msg = Message.obtain();
								msg.obj = message;
								ProgressMessageHandler.sendMessage(msg);
								progress.show();
								SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
								Tracker Tracker = new Tracker(0, Device.DeviceID, lc.getLatitude(), lc.getLongitude(), ft.format(new Date()));
								if (ConnectionMethods.isInternetConnected(HomeActivity.this, false).equals("")) {
									Tracker.TrackerID = db.addTrackers(Tracker);
									AsynTrackerCreate AsynTrackerCreate = new AsynTrackerCreate(Tracker);
									AsynTrackerCreate.execute("/Trackers");
								} else {
									db.addTrackers(Tracker);
									Buttons(true);
									progress.dismiss();
									DialogMethods.showInformationDialog(HomeActivity.this, "Ubicacion guardada", "Ubicacion guardada de manera local.", null);
								}
							} else {
								CheckIn(view);
							}
						}
					}
				}, 1000);
			} catch (Exception ex) {
				DialogMethods.showErrorDialog(HomeActivity.this, "Ocurrio un error al momento de checar ubicacion. Info: " + ex.toString(), "Activity:Home | CheckIn | Error:" + ex.toString());
			}
		}
	}

	//================================================================================
	// Web Tasks
	//================================================================================

	@SuppressLint("SimpleDateFormat")
	private class AsynTrackerCreate extends AsyncTask<String, Void, String> {
		Tracker Tracker;
		public AsynTrackerCreate(Tracker Tracker) {
			this.Tracker=Tracker;
		}
		@Override
		protected String doInBackground(String... params) {
			String resultado = "0";
			try {
				JSONObject item = new JSONObject();
				item.put("TrackerID", Tracker.TrackerID);
				item.put("DeviceID", Tracker.DeviceID);
				item.put("Latitude", Tracker.Latitude);
				item.put("Longitude", Tracker.Longitude);
				item.put("Date", Tracker.Date);
				resultado = ConnectionMethods.Post(HomeActivity.this,item.toString(), params[0],false);
			} catch (Exception e) {
				return "Error: " + e.toString();
			}
			return resultado;
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			if(result.equals("\"1\""))
			{
				db.deleteTrackersByID(Tracker.TrackerID);
				DialogMethods.showInformationDialog(HomeActivity.this, "Ubicacion enviada", "Ubicacion enviada exitosamente.", null);
				CheckIn.setText("Enviar Posicion");
				CheckIn.setBackgroundResource(R.drawable.btn_checkin);
			}
			else
			{
				DialogMethods.showErrorDialog(HomeActivity.this, "Ocurrio un error al momento de enviar ubicacion. Info: " + result, "Activity:Home | Method:AsynTrackerCreate | Result:" + result);
				CheckIn.setText("Enviar Posicion");
				CheckIn.setBackgroundResource(R.drawable.btn_checkin);
			}
			Buttons(true);
		}
	}

	private class AsyncUploadAnswers extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			String AlertMessage = "";
			try
			{
				String strMessage = "Buscando en base de datos";
				Message msg = Message.obtain();
				msg.obj = strMessage;
				ProgressMessageHandler.sendMessage(msg);
				List<Integer> AnswersWithImages = db.getAnswersWithImages();
				if(AnswersWithImages != null && AnswersWithImages.size() > 0){
					strMessage = "Imagenes encontradas";
					msg = Message.obtain();
					msg.obj = strMessage;
					ProgressMessageHandler.sendMessage(msg);
					int i = 1;
					for(Integer AnswerID: AnswersWithImages){
						strMessage = "Subiendo imagen " + i + " de " + AnswersWithImages.size();
						msg = Message.obtain();
						msg.obj = strMessage;
						ProgressMessageHandler.sendMessage(msg);
						Answers Answer = db.getAnswerByAnswerID(AnswerID);
						JSONObject item = new JSONObject();
						item.put("image", Answer.Value);
						String result = ConnectionMethods.Post(HomeActivity.this,item.toString(), "/UploadImage",true);
						if(result.startsWith("\"http://timetrackerstorage.blob.core.windows.net/")){
							Answer.Value = result.replaceAll("\"","");
							db.UpdateAnswers(Answer);
						}else{
							AlertMessage = "Ocurrio un error al cargar imagenes";
							return AlertMessage;
						}
						i++;
					}
				}
				strMessage = "Buscando resultados";
				msg = Message.obtain();
				msg.obj = strMessage;
				ProgressMessageHandler.sendMessage(msg);
				List<String> Results = db.getDistincAnswers();
				Devices Device = db.GetDevice();
				int i = 1;
				for(String Result: Results){
					strMessage = "Subiendo resultado " + i + " de " + Results.size();
					msg = Message.obtain();
					msg.obj = strMessage;
					ProgressMessageHandler.sendMessage(msg);
					List<Answers> listAnswers = db.getAnswersByIdentifier(Result);
					if(listAnswers.isEmpty() || listAnswers.size() <= 0){
						AlertMessage = "La Base de Datos no contiene resultados con el identificador " + Result;
						continue;
					}
					JSONArray jsArray = new JSONArray();
					for(Answers row: listAnswers)
					{
						if(row.Identifier.equals(Result)){
							JSONObject item = new JSONObject();
							item.put("QuestionID", row.QuestionID);
							if(row.Value == null){
								row.Value = "_.";
							}
							row.Value = row.Value.replace('�', ' ');
							String Valor = Normalizer.normalize(row.Value,Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+","");
							Valor = Valor.replace("�", "");
							item.put("Value", Valor);
							item.put("QuestionTypeID", row.QuestionTypeID);
							item.put("Latitude", row.Latitude);
							item.put("Longitude", row.Longitude);
							item.put("DeviceMac", row.DeviceMac);
							item.put("Identifier", row.Identifier);
							item.put("DateFormStart", row.DateFormStart);
							item.put("DateFormFinish", row.DateFormFinish);
							item.put("UbicheckID", row.Ubicheck);
							item.put("DeviceID", Device.DeviceID);
							jsArray.put(item);
						}
					}
					if(jsArray == null || jsArray.length() <= 0 ){
						AlertMessage = "La Lista no contiene resultados con el identificador " + Result;
						continue;
					}
					AlertMessage = ConnectionMethods.Post(HomeActivity.this,jsArray.toString(), params[0],true);
					if(AlertMessage == ""){
						db.deleteAnswersByIdentifier(Result);
					}
				}
			} catch (Exception e) {
				return e.toString();
			}
			if(AlertMessage == ""){
				AlertMessage = "HTTP/1.1 200 OK";
			}
			return AlertMessage;
		}

		@Override
		protected void onPostExecute(String result) {
			Buttons(true);
			progress.dismiss();
			if(result.equals("HTTP/1.1 200 OK"))
			{
				DialogMethods.showInformationDialog(HomeActivity.this, "Resultados enviados", "Resultados enviados exitosamente.", null);
			}
			else
			{
				DialogMethods.showErrorDialog(HomeActivity.this, "Ocurrio un error al momento de enviar resultados. Info: " + result, "Activity:Home | Method:AsyncUploadAnswers | Result:" + result);
			}
		}
	}

	private class AsyncCheckDevice extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return ConnectionMethods.GET(HomeActivity.this,urls[0]);
		}
		@Override
		protected void onPostExecute(String result) {
			try
			{
				JSONObject JO = new JSONObject(result);
				switch(JO.getInt("StatusID")){
					case 1:
						break;
					case 2:
						Devices Device = db.GetDevice();
						Device.Status = 2;
						if(JO.getBoolean("UsesFormSelection")){
							Device.UsesFormSelection = 1;
						}else{
							Device.UsesFormSelection = 0;
						}
						if(JO.getBoolean("UsesFormWithUbicheck")){
							Device.UsesFormWithUbicheck = 1;
						}else{
							Device.UsesFormWithUbicheck = 0;
						}
						if(JO.getBoolean("UsesClientValidation")){
							Device.UsesClientValidation = 1;
						}else{
							Device.UsesClientValidation = 0;
						}
						if(JO.getBoolean("UsesCreateBranch")){
							Device.UsesCreateBranch = 1;
						}else{
							Device.UsesCreateBranch = 0;
						}
						if(JO.getBoolean("UsesUbicheckDetails")){
							Device.UsesUbicheckDetails = 1;
						}else{
							Device.UsesUbicheckDetails = 0;
						}
						if(JO.getBoolean("UsesBiometric")){
							Device.UsesBiometric = 1;
						}else{
							Device.UsesBiometric = 0;
						}
						if(JO.getBoolean("UsesKioskMode")){
							Device.UsesKioskMode = 1;
						}else{
							Device.UsesKioskMode = 0;
						}
						Device.KioskBranchID = JO.getInt("KioskBranchID");
						Device.Account = JO.getString("Account");
						Device.Name = JO.getString("Name");
						db.updateDevice(Device);
						TextView txtfooter = (TextView) findViewById(R.id.txtfooter);
						txtfooter.setText("");
						Buttons(true);
						break;
					case 3:
						db.deleteQuestionOptions();
						db.deleteQuestionSentences();
						db.deleteQuestions();
						db.deleteAllSurveys();
						db.deleteDevice();
						db.deleteSelectedSurveys();
						txtSelectedSurveyHeader.setText("");
						txtSelectedSurveyText.setText("");
						Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
						startActivity(intent);
						finish();
						break;
				}
			}
			catch (Exception ex) {
				//	DialogMethods.showErrorDialog(HomeActivity.this, "Ocurrio un error al momento de checar dispositivo. Info: " + ex.toString(), "Activity:Home | Method:AsyncCheckDevice | Error:" + ex.toString());
			}
		}
	}

	private class AsyncUpdateSurveys extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return ConnectionMethods.GET(HomeActivity.this,urls[0]);
		}
		@Override
		protected void onPostExecute(String result) {
			try {
				List<SurveyTemp> lSurveyTemp = new ArrayList<SurveyTemp>();
				JSONArray jr = new JSONArray(result);
				int max=jr.length();
				db.deleteQuestionOptions();
				db.deleteQuestionSentences();
				db.deleteQuestions();
				db.deleteAllSurveys();
				db.deleteSelectedSurveys();
				txtSelectedSurveyHeader.setText("");
				txtSelectedSurveyText.setText("");
				for(int i=0;i<max;i++)
				{
					JSONObject jSurvey = (JSONObject)jr.getJSONObject(i);
					int SurveyID = Integer.parseInt((String) jSurvey.get("SurveyID"));
					String SurveyName= (String) jSurvey.get("SurveyName");
					int ClientID = Integer.parseInt((String) jSurvey.get("ClientID"));
					String ClientName= (String) jSurvey.get("ClientName");
					String TransactionNumber = (String) jSurvey.get("TransactionNumber");
					int StatusID = Integer.parseInt((String) jSurvey.get("StatusID"));
					String TextSize = String.valueOf(jSurvey.getInt("SizeID"));
					String TextColor = String.valueOf(jSurvey.getInt("ColorID"));
					String IntroductionText = (String) jSurvey.get("IntroductionText");
					String IntroductionImage = (String) jSurvey.get("IntroductionImage");
					String EndingText = (String) jSurvey.get("FinishText");
					String EndingImage = (String) jSurvey.get("FinishImage");
					String FooterImage = (String) jSurvey.get("FooterImage");
					String BackgroundImage = (String) jSurvey.get("BackgroundImage");
					int ProcedureID = Integer.parseInt((String) jSurvey.get("ProcedureID"));
					Surveys Surveys = new Surveys(SurveyID,SurveyName,ClientID,ClientName,TransactionNumber,StatusID,TextSize,TextColor,IntroductionText,IntroductionImage,EndingText,EndingImage,FooterImage,BackgroundImage,ProcedureID);
					SurveyTemp SurveyTemp = new SurveyTemp(Surveys.SurveyID, Surveys.IntroductionImage, Surveys.EndingImage,Surveys.FooterImage,Surveys.BackgroundImage);
					lSurveyTemp.add(SurveyTemp);
					db.addSurvey(Surveys);
					JSONArray jQuestions = jSurvey.getJSONArray("Questions");
					for (int j = 0; j < jQuestions.length(); j++) {
						JSONObject jQuestion = jQuestions.getJSONObject(j);
						int QuestionID=Integer.parseInt((String) jQuestion.get("QuestionID"));
						int QuestionTypeID=Integer.parseInt((String) jQuestion.get("QuestionTypeID"));
						int SectionID=Integer.parseInt((String) jQuestion.get("SectionID"));
						String SectionName=(String) jQuestion.get("SectionName");
						String Title=(String) jQuestion.get("Title");
						String Text=(String) jQuestion.get("Text");
						String Value=(String) jQuestion.get("Value");
						String Comment=(String) jQuestion.get("Comment");
						int OrderNumber=Integer.parseInt((String) jQuestion.get("OrderNumber"));
						String Question1=(String) jQuestion.get("Question1");
						String Instruction=(String) jQuestion.get("Instruction");
						String ShortName=(String) jQuestion.get("ShortName");
						int Minimum=Integer.parseInt((String) jQuestion.get("Minimum"));
						int Maximum=Integer.parseInt((String) jQuestion.get("Maximum"));
						Boolean Required=Boolean.parseBoolean((String) jQuestion.get("Required"));
						int Decimals=Integer.parseInt((String) jQuestion.get("Decimals"));
						String Preffix=(String) jQuestion.get("Preffix");
						String Suffix=(String) jQuestion.get("Suffix");
						Boolean Randomize=Boolean.parseBoolean((String) jQuestion.get("Randomize"));
						Boolean IncludeScoring=Boolean.parseBoolean((String) jQuestion.get("IncludeScoring"));
						Boolean DisplayImages=Boolean.parseBoolean((String) jQuestion.get("DisplayImages"));
						int MinAnswers=Integer.parseInt((String) jQuestion.get("MinAnswers"));
						int MaxAnswers=Integer.parseInt((String) jQuestion.get("MaxAnswers"));
						String LeftLabel=(String) jQuestion.get("LeftLabel");
						String RightLabel=(String) jQuestion.get("RightLabel");
						Boolean ImageAboveText=Boolean.parseBoolean((String) jQuestion.get("ImageAboveText"));
						String DefaultDate = (String) jQuestion.get("DefaultDate");
						int DateTypeID = Integer.parseInt((String) jQuestion.get("DateTypeID"));
						String DateTypeName = (String) jQuestion.get("DateTypeName");
						int CatalogID = Integer.parseInt((String) jQuestion.get("CatalogID"));
						String CatalogElements = (String) jQuestion.get("CatalogElements");
						String Condition=(String) jQuestion.get("Condition");
						String Valu=(String) jQuestion.get("Valu");
						String SendTo=(String) jQuestion.get("SendTo");
						String Image=(String) jQuestion.get("Image");
						String Options=(String) jQuestion.get("Options");
						String OtherOption=(String) jQuestion.get("OtherOption");
						Boolean Hidden = Boolean.parseBoolean((String) jQuestion.get("Hidden"));
						int OProcedureID = Integer.parseInt((String) jQuestion.get("ProcedureID"));
						int PostProcedureID = Integer.parseInt((String) jQuestion.get("PostProcedureID"));
						String Answer="";
						if (QuestionTypeID == 12 || QuestionTypeID == 19)
						{
							Questions question = new Questions(QuestionID,SurveyID,QuestionTypeID,SectionID,SectionName,Title,Text,Value,Comment,OrderNumber,Question1,Instruction,ShortName,Minimum,Maximum,Required,Decimals,Preffix,Suffix,Randomize,IncludeScoring,DisplayImages,MinAnswers,MaxAnswers,LeftLabel,RightLabel,ImageAboveText,DefaultDate,DateTypeID,DateTypeName,CatalogID,CatalogElements,Condition,Valu,SendTo,Image,Options,OtherOption,Hidden,Answer,OProcedureID,0,PostProcedureID);
							if(!Image.equals("")){
								new LoadImage(question).execute(Image);
							}else{
								db.addQuestion(question);
							}
						}
						else
						{
							db.addQuestion(new Questions(QuestionID,SurveyID,QuestionTypeID,SectionID,SectionName,Title,Text,Value,Comment,OrderNumber,Question1,Instruction,ShortName,Minimum,Maximum,Required,Decimals,Preffix,Suffix,Randomize,IncludeScoring,DisplayImages,MinAnswers,MaxAnswers,LeftLabel,RightLabel,ImageAboveText,DefaultDate,DateTypeID,DateTypeName,CatalogID,CatalogElements,Condition,Valu,SendTo,Image,Options,OtherOption, Hidden,Answer,OProcedureID,0,PostProcedureID));
						}
						JSONArray jQuestionOptions = jQuestion.getJSONArray("QuestionOptions");
						for (int k = 0; k < jQuestionOptions.length(); k++) {
							JSONObject jQuestionOption = jQuestionOptions.getJSONObject(k);
							int QuestionOptionID = jQuestionOption.getInt("QuestionOptionID");
							String QOName = jQuestionOption.getString("Name");
							double QOScore = jQuestionOption.getDouble("Score");
							String QOImage = jQuestionOption.getString("Image");
							Boolean IsText = Boolean.parseBoolean((String) jQuestionOption.get("IsText"));
							int IsTextNum = 0;
							if(IsText){
								IsTextNum = 1;
							}
							String QOCondition = jQuestionOption.getString("Condition");
							String QOValue = jQuestionOption.getString("Value");
							String QOSendTo = jQuestionOption.getString("SendTo");
							String QOType = jQuestionOption.getString("Type");
							if(!QOImage.equals("")){
								new LoadImageOption(new QuestionOptions(QuestionOptionID,QuestionID,QOName,QOScore,QOImage,QOCondition,QOValue,QOSendTo,IsTextNum,QOType)).execute(QOImage);
							}else{
								db.addQuestionOption(new QuestionOptions(QuestionOptionID,QuestionID,QOName,QOScore,QOImage,QOCondition,QOValue,QOSendTo,IsTextNum,QOType));
							}
						}
						JSONArray jQuestionSentences = jQuestion.getJSONArray("QuestionSentences");
						for (int k = 0; k < jQuestionSentences.length(); k++) {
							JSONObject jQuestionSentence = jQuestionSentences.getJSONObject(k);
							int QuestionSentenceID = jQuestionSentence.getInt("QuestionSentenceID");
							String QSName = jQuestionSentence.getString("Name");
							db.addQuestionSentence(new QuestionSentences(QuestionSentenceID,QuestionID,QSName));
						}
					}
				}
				if(lSurveyTemp.size() > 0){
					new LoadImageSurvey(lSurveyTemp).execute();
				}else{
					DialogMethods.showInformationDialog(HomeActivity.this, "Formas Sincronizadas", "Formas Sincronizadas exitosamente.", null);
					Buttons(true);
					progress.dismiss();
				}
			} catch (Exception ex) {
				DialogMethods.showErrorDialog(HomeActivity.this, "Ocurrio un error al momento de sincronizar formas. Info: " + ex.toString(), "Activity:Home | Method:AsyncUpdateSurveys | Error:" + ex.toString());
			}
		}
	}

	//================================================================================
	// Helpers
	//================================================================================

	public boolean isConnected(){
		if (ConnectionMethods.isInternetConnected(HomeActivity.this,false).equals("")){
			TextView txtConnection = (TextView) findViewById(R.id.txtConnection);
			txtConnection.setText("Conectado");
			txtConnection.setTextColor(Color.rgb(71, 164, 71));
			return true;
		}
		else {
			TextView txtConnection = (TextView) findViewById(R.id.txtConnection);
			txtConnection.setText("Sin Conexion");
			txtConnection.setTextColor(Color.RED);
			return false;
		}
	}

	public void Buttons(boolean show){
		Devices Device = db.GetDevice();
		SelectedSurvey SelectedSurvey = db.GetSelectedSurvey();
		boolean isAuthorized = false;
		if(Device != null){
			if(Device.Status == 2){
				isAuthorized = true;
			}
		}
		int respuestas = db.getDistincAnswersQty();
		if(respuestas > 0){
			btnUploadAnswers.setText("(" + respuestas + ")Subir Resultados");
		}else{
			respuestas = db.getDistincTrackersQty();
			if(respuestas > 0){
				btnUploadAnswers.setText("(" + respuestas + ")Subir Ubicaciones");
			}else{
				btnUploadAnswers.setText("(0)Subir Resultados");
			}
		}
		int formas = db.getSurveysQty();
		if(formas > 0){
			btnSelectSurvey.setText("(" + formas + ")Seleccionar Forma");
		}else{
			btnSelectSurvey.setText("(0)Seleccionar Forma");
		}
		if(isAuthorized)
		{
			if(isConnected())
			{
				if(show)
				{
					if (db.getSurveysQty() == 0 || Device.UsesFormSelection == 0)
					{
						btnSelectSurvey.setEnabled(false);
					}
					else
					{
						btnSelectSurvey.setEnabled(true);
					}
					btnDownloadSurvey.setEnabled(true);
					btnCheckIn.setEnabled(true);
					if (db.getAnswersQty() == 0 && db.getTrackersQty() == 0)
					{
						btnUploadAnswers.setEnabled(false);
					}
					else
					{
						btnUploadAnswers.setEnabled(true);
					}
					if (SelectedSurvey == null)
					{
						btnStartSurvey.setEnabled(false);
					}
					else
					{
						btnStartSurvey.setEnabled(true);
					}
				}
				else
				{
					btnSelectSurvey.setEnabled(false);
					btnDownloadSurvey.setEnabled(false);
					btnUploadAnswers.setEnabled(false);
					btnCheckIn.setEnabled(false);
					btnStartSurvey.setEnabled(false);
				}
			}
			else
			{
				if (db.getSurveysQty() == 0 || Device.UsesFormSelection == 0)
				{
					btnSelectSurvey.setEnabled(false);
				}
				else
				{
					btnSelectSurvey.setEnabled(true);
				}
				btnDownloadSurvey.setEnabled(false);
				btnUploadAnswers.setEnabled(false);
				btnCheckIn.setEnabled(true);
				if (SelectedSurvey == null)
				{
					btnStartSurvey.setEnabled(false);
				}
				else
				{
					btnStartSurvey.setEnabled(true);
				}
			}
		}
		else
		{
			btnSelectSurvey.setEnabled(false);
			btnDownloadSurvey.setEnabled(false);
			btnUploadAnswers.setEnabled(false);
			btnCheckIn.setEnabled(false);
			btnStartSurvey.setEnabled(false);
		}
		String txtSelectedSurvey = "";
		if( Device.UsesFormWithUbicheck == 1  && db.CheckUbicheckID() ){
			btnStartSurvey.setEnabled(false);
			btnSelectSurvey.setEnabled(true);
			SelectedSurvey = db.GetSelectedSurvey();
			if(SelectedSurvey != null){
				txtSelectedSurvey = SelectedSurvey.SurveyName;
			}
			if(!txtSelectedSurvey.equals("")){
				txtSelectedSurveyHeader.setText("Forma Seleccionada");
				txtSelectedSurveyText.setText(txtSelectedSurvey);
			}else{
				txtSelectedSurveyHeader.setText("");
				txtSelectedSurveyText.setText("");
				btnStartSurvey.setEnabled(false);
			}
		}else {
			txtSelectedSurveyHeader.setText("");
			txtSelectedSurveyText.setText("");
			btnSelectSurvey.setEnabled(false);
		}
		if( Device.UsesFormWithUbicheck == 1 && !txtSelectedSurvey.equals("")){
			btnStartSurvey.setEnabled(true);
		}else {
			btnStartSurvey.setEnabled(false);
		}
		if( Device.UsesFormSelection == 1){
			btnSelectSurvey.setEnabled(true);
		}
	}

	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		Questions  question;
		public LoadImage(Questions Question) {
			this.question=Question;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Bitmap doInBackground(String... args) {
			Bitmap bitmap=null;
			try {
				bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
			} catch (Exception e) {
				bitmap = null;
			}
			return bitmap;
		}

		protected void onPostExecute(Bitmap image) {
			if(image != null){
				question.Image = BitMapToString(image);
				db.addQuestion(question);
				image.recycle();
			}else{
				Toast.makeText(getBaseContext(), "Image Does Not exist or Network Error", Toast.LENGTH_LONG).show();
			}
		}
	}

	private class LoadImageOption extends AsyncTask<String, String, Bitmap> {
		QuestionOptions  questionOption;
		public LoadImageOption(QuestionOptions QuestionOption) {
			this.questionOption = QuestionOption;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Bitmap doInBackground(String... args) {
			Bitmap bitmap=null;
			try {
				bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
			} catch (Exception e) {
				bitmap = null;
			}
			return bitmap;
		}

		protected void onPostExecute(Bitmap image) {
			if(image != null){
				questionOption.Image = BitMapToString(image);
				db.addQuestionOption(questionOption);
				image.recycle();
			}else{
				Toast.makeText(getBaseContext(), "Image Does Not exist or Network Error", Toast.LENGTH_LONG).show();
			}
		}
	}

	private class LoadImageSurvey extends AsyncTask<String, String, Bitmap> {
		List<SurveyTemp>  surveys;
		public LoadImageSurvey(List<SurveyTemp> temp) {
			this.surveys=temp;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Bitmap doInBackground(String... args) {
			Bitmap bmImage = null;
			for(SurveyTemp survey: surveys){
				try {
					if(!survey.IntroductionImage.equals("")){
						bmImage = BitmapFactory.decodeStream((InputStream)new URL(survey.IntroductionImage).getContent());
						survey.IntroductionImage = BitMapToString(bmImage);
					}
				} catch (Exception e) {
				}
				try {
					if(!survey.EndingImage.equals("")){
						bmImage = BitmapFactory.decodeStream((InputStream)new URL(survey.EndingImage).getContent());
						survey.EndingImage = BitMapToString(bmImage);
					}
				} catch (Exception e) {
				}
				try {
					if(!survey.FooterImage.equals("")){
						bmImage = BitmapFactory.decodeStream((InputStream)new URL(survey.FooterImage).getContent());
						survey.FooterImage = BitMapToString(bmImage);
					}
				} catch (Exception e) {
				}
				try {
					if(!survey.BackgroundImage.equals("")){
						bmImage = BitmapFactory.decodeStream((InputStream)new URL(survey.BackgroundImage).getContent());
						survey.BackgroundImage = BitMapToString(bmImage);
					}
				} catch (Exception e) {
				}
			}
			if(bmImage != null){
				bmImage.recycle();
			}
			return bmImage;
		}

		protected void onPostExecute(Bitmap image) {
			for(SurveyTemp s: surveys){
				db.updateSurveyImages(s.SurveyID,s.IntroductionImage,s.EndingImage,s.FooterImage,s.BackgroundImage);
			}
			DialogMethods.showInformationDialog(HomeActivity.this, "Formas Sincronizadas", "Formas Sincronizadas exitosamente.", null);
			Buttons(true);
			progress.dismiss();
		}
	}

	public String BitMapToString(Bitmap bitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
		byte [] b=baos.toByteArray();
		String temp=Base64.encodeToString(b, Base64.DEFAULT);
		return temp;
	}

	public class SurveyTemp {
		public  int SurveyID;
		public String IntroductionImage;
		public String EndingImage;
		public String FooterImage;
		public String BackgroundImage;
		public SurveyTemp(int SurveyID, String IntroductionImage, String EndingImage, String FooterImage, String BackgroundImage){
			this.SurveyID = SurveyID;
			this.IntroductionImage = IntroductionImage;
			this.EndingImage = EndingImage;
			this.FooterImage = FooterImage;
			this.BackgroundImage = BackgroundImage;
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
		stopLocationUpdates();
	}

	private void stopLocationUpdates() {
		fusedLocationProviderClient.removeLocationUpdates(locationCallback);
	}

	private LocationRequest InicializeLR(){
		locationRequest = new LocationRequest();
		locationRequest.setInterval(10);
		locationRequest.setFastestInterval(15);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		return locationRequest;
	};

	private LocationCallback InicializeLC(){
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
	private void  GetLocation(FusedLocationProviderClient fusedLocationProviderClient, Context context){
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
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressLint("MissingPermission") NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		} else {
			return false;
		}
	}
}