package com.timetracker.surveys;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.timetracker.sqlite.MySQLiteHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.Manifest;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.widget.SeekBar.OnSeekBarChangeListener;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.timetracker.data.Devices;
import com.timetracker.data.Message;
import com.timetracker.data.QuestionOptions;
import com.timetracker.data.Questions;
import com.timetracker.data.SelectedSurvey;
import com.timetracker.data.Surveys;
import com.timetracker.business.JsonMethods;
import com.timetracker.data.Answers;
import com.timetracker.business.ConnectionMethods;
import com.timetracker.business.Controls;
import com.timetracker.business.GPSTracker;
import com.timetracker.business.Validations;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class SurveyActivity extends Activity {

	//================================================================================
    // Global Variables
    //================================================================================
	
	int j=0;
	List<List<Questions>> SurveyQuestions;
	LinearLayout lm;
	private ProgressBar progressBar;
	private int progressStatus = 0;
	private int increase = 0;
	protected String _tempDir;
	protected String _path;
	protected boolean _taken;
	protected boolean _signed;
	protected boolean _Scanned;
	protected String _ScannedValue;
	Bitmap bitmap;
	int _currentControlID;
	SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs" ;
	protected Location newLocation = null;
	GPSTracker GPSTracker;
    protected int idKey = 100000;
    protected int idKey2 = 200000;
    protected int idKey3 = 300000;
    protected int idCheckbox = 30000;
    protected int idRadiobutton = 40000;
    protected int idLinearLayout = 50000;
    protected int idHeaderLayout = 60000;
    protected int idDatePicker = 70000;
    protected int idTimePicker = 80000;
    protected int idFooterLayout = 90000;
	
    protected List<String> oldListSentTo = new ArrayList<String>();
    private MySQLiteHelper db = new MySQLiteHelper(SurveyActivity.this);
    ProgressDialog progress;

	// New GPS
	private FusedLocationProviderClient fusedLocationProviderClient;
	private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
	private LocationRequest locationRequest;
	private LocationCallback locationCallback;
	private Location lc;
	private double latitude,longitude;
    
	//================================================================================
    // Activity Events
    //================================================================================
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);
		progress = new ProgressDialog(SurveyActivity.this);
		progress.setCancelable(false);
		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

		GetLocation(fusedLocationProviderClient,this);
		try
		{ 
			setContentView(R.layout.activity_survey);
			setupUI(findViewById(R.id.rlMain));
			_tempDir = Environment.getExternalStorageDirectory() + "/Survey_Signature.png";
			_path = Environment.getExternalStorageDirectory() + "/Survey_Photo.jpg";
			if(savedInstanceState == null || !savedInstanceState.containsKey("listQuestions")) {
				Intent myIntent = getIntent();
				String SurveyID = myIntent.getStringExtra("SurveyID");
				String Index = myIntent.getStringExtra("Index");
				j=Integer.parseInt(Index);
				loadcontrols(SurveyID);
				Surveys S = db.getSurvey(Integer.parseInt(SurveyID));
				if(!S.BackgroundImage.equals("")){
					Bitmap BM = StringToBitMap(S.BackgroundImage);
					BitmapDrawable DR = new BitmapDrawable(getResources(), BM);
					DR.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
					RelativeLayout layout =(RelativeLayout)findViewById(R.id.rlMain);
					layout.setBackgroundDrawable(DR);
				}
			}
			else{
				j = savedInstanceState.getInt("j");
				progressStatus = savedInstanceState.getInt("progressStatus");
				increase = savedInstanceState.getInt("increase");
				JsonMethods jsonMethods = new JsonMethods();
				List<Questions> ListQuestions = savedInstanceState.getParcelableArrayList("listQuestions");
				SurveyQuestions = jsonMethods.GetList(ListQuestions);
				loadSavecontrols();
				if(savedInstanceState.getBoolean("PHOTO_TAKEN" )) {
					onPhotoTaken(savedInstanceState.getInt("currentControlID"));
				}
				if(savedInstanceState.getBoolean("signed" )) {
					onSignatureTaken(savedInstanceState.getInt("currentControlID" ));
				}
				if(savedInstanceState.getBoolean("Scanned" )) {
					onScanned(savedInstanceState.getString("ScannedValue" ),savedInstanceState.getInt("currentControlID" ));
				}
			}
		}
		catch(Exception ex){
			Toast.makeText(getApplicationContext(), "E001:" + ex.toString(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
  	protected void onResume() {
	      sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
	      if(sharedpreferences.contains("savequestions")){
	    	  int Flag= sharedpreferences.getInt("Flag", 0);
	    	  if (Flag==1)
	    	  {
		    	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
		          builder.setTitle("Encuesta incompleta")
		          .setMessage("�le gustar�a completar la encuesta?")
		          .setCancelable(false)
		          .setPositiveButton("Si",new DialogInterface.OnClickListener() {
		              public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
		              }
		          })
		          .setNegativeButton("No",new DialogInterface.OnClickListener() {
		              public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
		                  Editor editor = sharedpreferences.edit();
		     		      editor.remove("savequestions");
		     		      editor.remove("saveindex");
		     		      editor.commit();
		     		      Devices Device = db.GetDevice();
		     		      db.deletePhotos();
		     		      if(Device.UsesFormWithUbicheck == 1 && Device.UsesClientValidation == 1){
		     		    	  db.deleteSelectedSurveys();
		     		    	  Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
		     		    	  startActivity(intent);
		     		    	  finish();
		     		      }else{
		     		    	  SelectedSurvey SelectedSurvey = db.GetSelectedSurvey();
		     		    	  if(SelectedSurvey != null){
		     		    		 db.updateQuestionAnswers(SelectedSurvey.SurveyID);
		     		    	  }
		     		    	  Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.StartSurvey.class);
		     		    	  startActivity(intent);
		     		    	  finish();
		     		      }
		              }
		          });
		          AlertDialog alert = builder.create();
		          alert.show();
	    	  }
	    	  else
	    	  {
	    		  Editor editor = sharedpreferences.edit();
	    		  editor.putInt("Flag", 1);
	    		  editor.commit();
	    	  }
	      }
	      super.onResume();
	   }
	  
	@Override
    protected void onSaveInstanceState(Bundle outState ) {
		try
		{ 
			outState.putBoolean("PHOTO_TAKEN", _taken );
			outState.putBoolean("Scanned", _Scanned );
			outState.putBoolean("signed", _signed );
			outState.putString("ScannedValue", _ScannedValue );
			outState.putInt("currentControlID", _currentControlID -idKey);
			outState.putInt("j", j);
			outState.putInt("progressStatus", progressStatus);
			outState.putInt("increase", increase);
			List<Questions> listQuestions = new ArrayList<Questions>();
			if(SurveyQuestions != null){
				for(List<Questions> Listquestions: SurveyQuestions)
				{
					for(Questions question: Listquestions)
					{
						listQuestions.add(question);
					}
				}
				ArrayList<Questions> arrayquestions=(ArrayList<Questions>) listQuestions;
				outState.putParcelableArrayList("listQuestions", arrayquestions);
			}
		}catch(Exception ex){
			Toast.makeText(getApplicationContext(), "E00X:" + ex.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    db.close();
	    Runtime.getRuntime().gc();
	}
	
	//================================================================================
    // Hide Soft Keyboard Methods
    //================================================================================
	
	public static void hideSoftKeyboard(Activity activity) {
	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	
	public void setupUI(View view) {
	    if(!(view instanceof EditText)) {
	        view.setOnTouchListener(new View.OnTouchListener() {
				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					hideSoftKeyboard(SurveyActivity.this);
					return false;
				}
			});
	    }
	    if (view instanceof ViewGroup) {
	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	            View innerView = ((ViewGroup) view).getChildAt(i);
	            setupUI(innerView);
	        }
	    }
	}
	
	//================================================================================
    // Load Saved Controls
    //================================================================================
	  
	private void loadSavecontrols() {
		 increase=100/SurveyQuestions.size();
         List<Questions>  questions= SurveyQuestions.get(j);
         TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
         txtTitle.setText(questions.get(0).SectionName);
         Controls control= new Controls();
         lm = (LinearLayout) findViewById(R.id.activity_survey);
         View input=control.CreateControls(this,questions);
         lm.addView(input);
         CreateListeners(questions, false);
         progressBar = (ProgressBar) findViewById(R.id.progressBar1);
         progressStatus=progressStatus+increase;
         progressBar.setProgress(progressStatus);
         TextView textView = (TextView) findViewById(R.id.txtPercent);
         textView.setText(progressStatus+"%");
         Surveys S = db.getSurvey(questions.get(0).SurveyID);
         if(!S.FooterImage.equals("")){
        	 Bitmap BM = StringToBitMap(S.FooterImage);
        	 ImageView IV =(ImageView)findViewById(R.id.imgFooter);
        	 IV.setImageBitmap(BM);
         }
          
	}

	//================================================================================
    // Load Controls
    //================================================================================
	
	private void loadcontrols(String SurveyID) {
		SurveyQuestions=db.getQuestions(Integer.parseInt(SurveyID));
		increase=100/SurveyQuestions.size();
		List<Questions>  questions= SurveyQuestions.get(j);
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtTitle.setText(questions.get(0).SectionName);
		Controls control= new Controls();
		lm = (LinearLayout) findViewById(R.id.activity_survey);
		View input=control.CreateControls(this,questions);
		lm.addView(input);
		CreateListeners(questions,true);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressStatus=progressStatus+increase;
		if (j!=0){
			progressStatus = progressStatus  * (j+1);
		}
		progressBar.setProgress(progressStatus);
		TextView textView = (TextView) findViewById(R.id.txtPercent);
		textView.setText(progressStatus+"%");
		Surveys S = db.getSurvey(questions.get(0).SurveyID);
		if(!S.FooterImage.equals("")){
			Bitmap BM = StringToBitMap(S.FooterImage);
			ImageView IV =(ImageView)findViewById(R.id.imgFooter);
			IV.setImageBitmap(BM);
		}
	}
	
	//================================================================================
    // Section Methods
    //================================================================================
	
	public void Sections(View view){
		   try
			{
			   lm = (LinearLayout) findViewById(R.id.activity_survey);
				Validations validations= new Validations();
				Boolean EmptyControls= false;
				if (SurveyQuestions.size()>j)
				{
					List<Questions> questions= SurveyQuestions.get(j);
					int i=0;
					for(Questions question: questions)
						{
						   View control =(View) findViewById(question.QuestionID+idKey);
						   String other = null;
							if(question.QuestionTypeID == 10){
								int lastdigit = (question.QuestionID%1000)*1000; 
					    		int seekBarID = question.QuestionID + idKey + lastdigit + 2;
				 				SeekBar currentSeekBar= (SeekBar)control.findViewById(seekBarID);
				 				TextView currentTitle = (TextView)findViewById(currentSeekBar.getId() - (int)1);
								other = currentTitle.getText().toString();
							}
							if(question.QuestionTypeID == 4){
								Questions Q2 = db.getQuestion(question.QuestionID);
								if(!question.OtherOption.equals("") && (Q2.Answer.equals(question.OtherOption))){
									int idLayout = (idLinearLayout + (question.OrderNumber + 1));
									LinearLayout linearLayoutEdit = (LinearLayout)findViewById(idLayout);
									int childcount = linearLayoutEdit.getChildCount();
									for (int x=0; x < childcount; x++){
										View v = linearLayoutEdit.getChildAt(x);
										if (v instanceof EditText) {
											EditText EditText = (EditText)v;
			 		    	    	    	other = EditText.getText().toString();
										}
			 		    	    	}
							   }
							}
							Boolean isVisible = true;
							View invisibleLayout = (View)findViewById(idLinearLayout + (question.OrderNumber + 1));
							if(invisibleLayout != null && invisibleLayout.getVisibility() != View.VISIBLE){
								isVisible = false;
							}
						   	Message validate = validations.ValidateControl(getApplicationContext(),control,question,isTablet(this),other,isVisible);

						   if (validate.ID ==2)
							{
						        EmptyControls=true;
						        //TextView txtQuestion = (TextView) findViewById(question.QuestionID);
						        //txtQuestion.setBackgroundColor(Color.RED);
						        TextView txtError = (TextView) findViewById(question.QuestionID + idFooterLayout);
						        txtError.setText(validate.Value);
							}
							else
							{
								 TextView txtError = (TextView) findViewById(question.QuestionID + idFooterLayout);
							        txtError.setText("");
								// TextView txtQuestion = (TextView) findViewById(question.QuestionID);
							      //txtQuestion.setBackgroundColor(Color.WHITE);
								question.Answer=validate.Value;
								questions.set(i, question);
							}
						   i++;
						}
						//Save questions
						 sharedpreferences=getSharedPreferences(MyPREFERENCES, 
				          	      Context.MODE_PRIVATE);
						 Editor editor = sharedpreferences.edit();
				         editor.putInt("savequestions", questions.get(0).SurveyID);
				         editor.putInt("saveindex", j);
				         editor.putInt("Flag", 1);
				         editor.commit();
				         MySQLiteHelper sqlite = new MySQLiteHelper(getApplicationContext());
				         for(Questions questionstosave: questions)
				         {
				        	 sqlite.updateQuestion(questionstosave);
				         }
						SurveyQuestions.set(j, questions);
						if (EmptyControls==true)
						{
							Button btnSections = (Button) findViewById(R.id.btnSections);
							//btnSections.setBackgroundColor(Color.RED);
							btnSections.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_button));
							return;
						}
						else
						{
							Button btnSections = (Button) findViewById(R.id.btnSections);
							btnSections.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_button));
							//btnSections.setBackgroundColor(Color.GRAY);
					        
					        
					        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
				            txtTitle.setText("Seleccione Secci�n");
				            Button btnNext = (Button) findViewById(R.id.btnNext);
				            Button btnBack = (Button) findViewById(R.id.btnBack);
				            Button btnSections1 = (Button) findViewById(R.id.btnSections);
				            btnNext.setVisibility(View.GONE);
				            btnBack.setVisibility(View.GONE);
				            btnSections1.setVisibility(View.GONE);
				            
				            Controls control= new Controls();
							lm.removeAllViews();
							View controls=control.CreateSectionControls(this,SurveyQuestions);
							lm.addView(controls);
							
		
							for(int e=0;e<SurveyQuestions.size();e++ )
							{
								Button button=  (Button) findViewById(e);
								button.setOnClickListener( new ButtonChangeSecion() );
							}
				            progressBar = (ProgressBar) findViewById(R.id.progressBar1);
				            progressBar.setProgress(progressStatus);
				            TextView textView = (TextView) findViewById(R.id.txtPercent);
				            textView.setText(progressStatus+"%");
						}
				}
				else
				{
					Button btnSections = (Button) findViewById(R.id.btnSections);
					btnSections.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_button));
					//btnSections.setBackgroundColor(Color.GRAY);
			        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		            txtTitle.setText("Seleccione Secci�n");
		            Button btnNext = (Button) findViewById(R.id.btnNext);
		            Button btnBack = (Button) findViewById(R.id.btnBack);
		            Button btnSections1 = (Button) findViewById(R.id.btnSections);
		            btnNext.setVisibility(View.GONE);
		            btnBack.setVisibility(View.GONE);
		            btnSections1.setVisibility(View.GONE);
		            
		            Controls control= new Controls();
					lm.removeAllViews();
					View controls=control.CreateSectionControls(this,SurveyQuestions);
					lm.addView(controls);
					

					for(int e=0;e<SurveyQuestions.size();e++ )
					{
						Button button=  (Button) findViewById(e);
						button.setOnClickListener( new ButtonChangeSecion() );
					}
		            progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		            progressBar.setProgress(progressStatus);
		            TextView textView = (TextView) findViewById(R.id.txtPercent);
		            textView.setText(progressStatus+"%");   
					
				}
		    }
		       catch(Exception ex){
		    	   Toast.makeText(getApplicationContext(), "E002:" + ex.toString(), Toast.LENGTH_LONG).show();
			}	  
	  }
	
	public class ButtonChangeSecion implements View.OnClickListener 
	{
	   	public void onClick(View view ){
	   		try
	   		{
	   			int id= view.getId();
	   			SelectSection(id);
	
	   	    } catch (Exception e) {
	   				 Toast.makeText(getBaseContext(), "E003:" + e.toString(), Toast.LENGTH_LONG).show();
	   	  } 
	   	} 	
	}
	
	public void SelectSection(int id){
		   try
			{
			 //Lines added to avoid to lose the progress bar
        		int increaseQty=100/SurveyQuestions.size();
        		int qty =increaseQty*(id+1);
        		 progressStatus=qty;
        		 j=id;
				
			   
			   Button btnNext = (Button) findViewById(R.id.btnNext);
	            Button btnBack = (Button) findViewById(R.id.btnBack);
	            Button btnSections = (Button) findViewById(R.id.btnSections);
	            btnNext.setVisibility(View.VISIBLE);
	            btnBack.setVisibility(View.VISIBLE);
	            btnSections.setVisibility(View.VISIBLE);
			   
        	    List<Questions> questions= SurveyQuestions.get(id);
        	    TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
	            txtTitle.setText(questions.get(0).SectionName);
	            Controls control= new Controls();
				lm.removeAllViews();
				View controls=control.CreateControls(this,questions);
				lm.addView(controls);
				CreateListeners(questions,true);
	            progressBar = (ProgressBar) findViewById(R.id.progressBar1);
	            progressStatus=qty;
	            progressBar.setProgress(progressStatus);
	            TextView textView = (TextView) findViewById(R.id.txtPercent);
	            textView.setText(qty+"%");      

		    }
		       catch(Exception ex){
		    	   Toast.makeText(getApplicationContext(), "E004:" + ex.toString(),Toast.LENGTH_LONG).show();
	       }	  
	}
	
	//================================================================================
    // Survey Movement Buttons
    //================================================================================
	
	public void Back(View view){
	   try
		{
		   Button btnNext = (Button) findViewById(R.id.btnNext);
		   btnNext.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_button));
		   Button btnSections = (Button) findViewById(R.id.btnSections);
		   btnSections.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_button));
	        if (j<=0)
	        {	
	        	Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
	    		startActivity(intent);
	    		finish();
	        }
	        else
	        { 
	        	if(j>=SurveyQuestions.size())
				{
	        		 progressStatus=progressStatus+increase;
				}
	        	 j=j-1;
	        	
	        	    List<Questions> questions= SurveyQuestions.get(j);
	        	    TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		            txtTitle.setText(questions.get(0).SectionName);
		            Controls control= new Controls();
					lm.removeAllViews();
					View controls=control.CreateControls(this,questions);
					lm.addView(controls);
					CreateListeners(questions,true);
		            progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		            progressStatus=progressStatus-increase;
		            progressBar.setProgress(progressStatus);
		            TextView textView = (TextView) findViewById(R.id.txtPercent);
		            textView.setText(progressStatus+"%");      
	              Surveys S = db.getSurvey(questions.get(0).SurveyID);
	              if(!S.BackgroundImage.equals("")){
	            	  Bitmap BM = StringToBitMap(S.BackgroundImage);
	            	  BitmapDrawable DR = new BitmapDrawable(getResources(), BM);
	            	  DR.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
	            	  RelativeLayout layout =(RelativeLayout)findViewById(R.id.rlMain);
	            	  layout.setBackgroundDrawable(DR);
	              }
	        }
	    }
	       catch(Exception ex){
	    	   Toast.makeText(getApplicationContext(), "E005:" + ex.toString(), Toast.LENGTH_LONG).show();
		}	  
	}

	public void Next(View view){
			int ErrorQuestion = 0;
			try
			{
				lm = (LinearLayout) findViewById(R.id.activity_survey);
				Validations validations= new Validations();
				if(j>=SurveyQuestions.size())
				{
					return;
				}
				Boolean EmptyControls= false;
				List<Questions> questions= SurveyQuestions.get(j);
				int i=0;
				for(Questions question: questions)
					{
						
						ErrorQuestion = question.QuestionID;
						View control =(View) findViewById(question.QuestionID+idKey);
						if(control != null && !control.isEnabled()){
							Questions Q2 = db.getQuestion(question.QuestionID);
							Q2.Blocked = 1;
	    					db.updateQuestionBlocked(Q2);
							question.Blocked = 1;
						}
						String other = null;
						if(question.QuestionTypeID == 10){
							int lastdigit = (question.QuestionID%1000)*1000; 
				    		int seekBarID = question.QuestionID + idKey + lastdigit + 2;
			 				SeekBar currentSeekBar= (SeekBar)control.findViewById(seekBarID);
			 				TextView currentTitle = (TextView)findViewById(currentSeekBar.getId() - (int)1);
							other = currentTitle.getText().toString();
						}
						if(question.QuestionTypeID == 4){
							Questions Q2 = db.getQuestion(question.QuestionID);
							if(!question.OtherOption.equals("") && (Q2.Answer.equals(question.OtherOption))){
								int idLayout = (idLinearLayout + (question.OrderNumber + 1));
								LinearLayout linearLayoutEdit = (LinearLayout)findViewById(idLayout);
								int childcount = linearLayoutEdit.getChildCount();
								for (int x=0; x < childcount; x++){
									View v = linearLayoutEdit.getChildAt(x);
									if (v instanceof EditText) {
										EditText EditText = (EditText)v;
		 		    	    	    	other = EditText.getText().toString();
									}
		 		    	    	}
						   }
						}
						Boolean isVisible = true;
						View invisibleLayout = (View)findViewById(idLinearLayout + (question.OrderNumber + 1));
						if(invisibleLayout != null && invisibleLayout.getVisibility() != View.VISIBLE){
							isVisible = false;
						}
						Message validate= validations.ValidateControl(getApplicationContext(),control,question,isTablet(this),other,isVisible);
					   if (validate.ID ==2)
						{
					        EmptyControls=true;
					        //TextView txtQuestion = (TextView) findViewById(question.QuestionID);
					        //txtQuestion.setBackgroundColor(Color.RED);
					        
					        TextView txtError = (TextView) findViewById(question.QuestionID+90000);
					        txtError.setText(validate.Value);
						}
						else
						{
							 TextView txtError = (TextView) findViewById(question.QuestionID+90000);
							 txtError.setText("");
							// TextView txtQuestion = (TextView) findViewById(question.QuestionID);
						      //txtQuestion.setBackgroundColor(Color.WHITE);
							question.Answer=validate.Value;
							questions.set(i, question);
						}
					   i++;
					}
				   
				  	SurveyQuestions.set(j, questions);
					if (EmptyControls==true)
					{
						Button btnNext = (Button) findViewById(R.id.btnNext);
						btnNext.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_button));
						//btnNext.setBackgroundColor(Color.RED);
						
						Button btnSections = (Button) findViewById(R.id.btnSections);
						btnSections.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_button));
						//btnSections.setBackgroundColor(Color.RED);
						
						return;
					}
					else
					{
						Button btnNext = (Button) findViewById(R.id.btnNext);
						btnNext.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_button));
				        //btnNext.setBackgroundColor(Color.GRAY);
				        
				    	Button btnSections = (Button) findViewById(R.id.btnSections);
				    	 btnSections.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_button));
						//btnSections.setBackgroundColor(Color.GRAY);
					}
				if (SurveyQuestions.size()>j+1)
				{
					j=j+1;
					List<Questions> nextQuestions= SurveyQuestions.get(j);
					TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
					txtTitle.setText(nextQuestions.get(0).SectionName);
					Controls control= new Controls();
					lm.removeAllViews();
					View controls=control.CreateControls(this,nextQuestions);
					lm.addView(controls);
					CreateListeners(nextQuestions,true);
		            progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		            progressStatus=progressStatus+increase;
		            progressBar.setProgress(progressStatus);
		            TextView textView = (TextView) findViewById(R.id.txtPercent);
		            textView.setText(progressStatus+"%");
		            
		            //Save questions
		            MySQLiteHelper sqlite = new MySQLiteHelper(getApplicationContext());
			        for(Questions questionstosave: questions)
			        {
			        	sqlite.updateQuestion(questionstosave);
			        }
		            
		            sharedpreferences = getSharedPreferences(MyPREFERENCES, 
		          	      Context.MODE_PRIVATE);
		            Editor editor = sharedpreferences.edit();
		            editor.putInt("savequestions", nextQuestions.get(0).SurveyID);
		            editor.putInt("saveindex", j);
		            editor.putInt("Flag", 1);
		            editor.commit();
		            
				}
				else
				{
					RelativeLayout relative = (RelativeLayout) findViewById(R.id.rlMain);
					relative.setBackgroundResource(0);
					MySQLiteHelper sqlite = new MySQLiteHelper(getApplicationContext());
			        for(Questions questionstosave: questions)
			        {
			        	sqlite.updateQuestion(questionstosave);
			        }
		            sharedpreferences=getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
		            Editor editor = sharedpreferences.edit();
		            editor.putInt("savequestions", questions.get(0).SurveyID);
		            editor.putInt("saveindex", j);
		            editor.putInt("Flag", 1);
		            editor.commit();
					Questions item = new Questions(1,questions.get(0).SurveyID,0,1,"","Finalizar","","","",0,"","","",0,0,false,0,"","",false,false,false,1,1,"","",false,"",0,"",0,"","","","","","","",false,"Finalizar Forma",0,0);
					List<Questions> listitems = new ArrayList<Questions>();
					listitems.add(item);
					j=j+1;
					TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
					txtTitle.setText(listitems.get(0).SectionName);
					Controls control= new Controls();
					lm.removeAllViews();
					View controls=control.CreateControls(this,listitems);
					lm.addView(controls);
					CreateListeners(listitems,false);
				}
	    	}
	       catch(Exception ex){
	    	   Toast.makeText(getApplicationContext(), "E006: Q:" + String.valueOf(ErrorQuestion) + " " + ex.toString(),Toast.LENGTH_LONG).show();

	       }
	}
	
	public void Next2(View view){
			int ErrorQuestion = 0;
			try
			{
				lm = (LinearLayout) findViewById(R.id.activity_survey);
				Validations validations= new Validations();
				if(j>=SurveyQuestions.size())
				{
					return;
				}
				Boolean EmptyControls= false;
				List<Questions> questions= SurveyQuestions.get(j);
				int i=0;
				for(Questions question: questions)
					{
						String other = null;
						ErrorQuestion = question.QuestionID;
						View control =(View) findViewById(question.QuestionID+idKey);
						if(question.QuestionTypeID == 10){
							int lastdigit = (question.QuestionID%1000)*1000; 
				    		int seekBarID = question.QuestionID + idKey + lastdigit + 2;
			 				SeekBar currentSeekBar= (SeekBar)control.findViewById(seekBarID);
			 				TextView currentTitle = (TextView)findViewById(currentSeekBar.getId() - (int)1);
							other = currentTitle.getText().toString();
						}
						if(!question.OtherOption.equals("") && (question.Answer.equals(question.OtherOption))){
						   int idLayout = (idLinearLayout + (question.OrderNumber + 1));
						   LinearLayout linearLayoutEdit = (LinearLayout)findViewById(idLayout);
						   int childcount = linearLayoutEdit.getChildCount();
						   for (int x=0; x < childcount; x++){
	 		    	    	      View v = linearLayoutEdit.getChildAt(x);
	 		    	    	      if (v instanceof EditText) {
	 		    	    	    	  EditText EditText = (EditText)v;
	 		    	    	    	  other = EditText.getText().toString();
	 		    	            }
	 		    	    	}
					   }
						Boolean isVisible = true;
						View invisibleLayout = (View)findViewById(idLinearLayout + (question.OrderNumber + 1));
						if(invisibleLayout != null && invisibleLayout.getVisibility() != View.VISIBLE){
							isVisible = false;
						}
					   Message validate= validations.ValidateControl(getApplicationContext(),control,question,isTablet(this),other,isVisible);
					   if (validate.ID ==2)
						{
					        EmptyControls=true;
					        //TextView txtQuestion = (TextView) findViewById(question.QuestionID);
					        //txtQuestion.setBackgroundColor(Color.RED);
					        
					        TextView txtError = (TextView) findViewById(question.QuestionID+90000);
					        txtError.setText(validate.Value);
						}
						else
						{
							 TextView txtError = (TextView) findViewById(question.QuestionID+90000);
						        txtError.setText("");
							// TextView txtQuestion = (TextView) findViewById(question.QuestionID);
						      //txtQuestion.setBackgroundColor(Color.WHITE);
							question.Answer=validate.Value;
							questions.set(i, question);
						}
					   i++;
					}
				   
				  	SurveyQuestions.set(j, questions);
					if (EmptyControls==true)
					{
						Button btnNext = (Button) findViewById(R.id.btnNext);
						btnNext.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_button));
						//btnNext.setBackgroundColor(Color.RED);
						
						Button btnSections = (Button) findViewById(R.id.btnSections);
						btnSections.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_button));
						//btnSections.setBackgroundColor(Color.RED);
						
						return;
					}
					else
					{
						Button btnNext = (Button) findViewById(R.id.btnNext);
						btnNext.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_button));
				        //btnNext.setBackgroundColor(Color.GRAY);
				        
				    	Button btnSections = (Button) findViewById(R.id.btnSections);
				    	 btnSections.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_button));
						//btnSections.setBackgroundColor(Color.GRAY);
					}
				if (SurveyQuestions.size()>j+1)
				{
					j=j+1;
					List<Questions> nextQuestions= SurveyQuestions.get(j);
					TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
					txtTitle.setText(nextQuestions.get(0).SectionName);
					Controls control= new Controls();
					lm.removeAllViews();
					View controls=control.CreateControls(this,nextQuestions);
					lm.addView(controls);
					CreateListeners(nextQuestions,true);
		            progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		            progressStatus=progressStatus+increase;
		            progressBar.setProgress(progressStatus);
		            TextView textView = (TextView) findViewById(R.id.txtPercent);
		            textView.setText(progressStatus+"%");
		            
		            //Save questions
		            MySQLiteHelper sqlite = new MySQLiteHelper(getApplicationContext());
			         for(Questions questionstosave: questions)
			         {
			        	 sqlite.updateQuestion(questionstosave);
			         }
		            
		            sharedpreferences=getSharedPreferences(MyPREFERENCES, 
		          	      Context.MODE_PRIVATE);
		            Editor editor = sharedpreferences.edit();
		            editor.putInt("savequestions", nextQuestions.get(0).SurveyID);
		            editor.putInt("saveindex", j);
		            editor.putInt("Flag", 0);
		            editor.commit();
		            
				}
				else
				{
					RelativeLayout relative = (RelativeLayout) findViewById(R.id.rlMain);
					relative.setBackgroundResource(0);
					MySQLiteHelper sqlite = new MySQLiteHelper(getApplicationContext());
					for(Questions questionstosave: questions)
					{
						sqlite.updateQuestion(questionstosave);
					}
		            sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		            Editor editor = sharedpreferences.edit();
		            editor.putInt("savequestions", questions.get(0).SurveyID);
		            editor.putInt("saveindex", j);
		            editor.putInt("Flag", 0);
		            editor.commit();
		            Questions item = new Questions(1,questions.get(0).SurveyID,0,1,"","Finalizar","","","",0,"","","",0,0,false,0,"","",false,false,false,1,1,"","",false,"",0,"",0,"","","","","","","",false,"Finalizar Forma",0,0);
					List<Questions> listitems = new ArrayList<Questions>();
					listitems.add(item);
					j=j+1;
					TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
					txtTitle.setText(listitems.get(0).SectionName);
					Controls control= new Controls();
					lm.removeAllViews();
					View controls=control.CreateControls(this,listitems);
					lm.addView(controls);
					CreateListeners(listitems,false);
				}
	    	}
	       catch(Exception ex){
	    	   Toast.makeText(getApplicationContext(), "E007: Q:" + String.valueOf(ErrorQuestion) + " " + ex.toString(),Toast.LENGTH_LONG).show();
	       }
	}
	
	//================================================================================
    // Helpers
    //================================================================================
	
  	public void ChangeColos(Boolean flag)
  	{
  		try
  		{
  			if(flag == true)
  			{
				Button btnNext = (Button) findViewById(R.id.btnNext);
  	 			btnNext.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_button));
  	 			Button btnSections = (Button) findViewById(R.id.btnSections);
  	 			btnSections.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_button));
  			}
  			else
  			{
  				Button btnNext = (Button) findViewById(R.id.btnNext);
  	 			btnNext.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_button));
  	 			Button btnSections = (Button) findViewById(R.id.btnSections);
  	 			btnSections.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_button));
  			}
 		 } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
 		 }

  	}
  	
  	public Bitmap StringToBitMap(String encodedString){
	      try{
	        byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
	        Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
	        return bitmap;
	      }catch(Exception e){
	        e.getMessage();
	        return null;
	      }
	}
	
  	//================================================================================
    // Save Answers
    //================================================================================ 
	    
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
    	int SurveyID;
    	String Identifier;
    	int DeviceID;
        public HttpAsyncTask(int SurveyID, String Identifier,int DeviceID) {
        	this.SurveyID = SurveyID;
        	this.Identifier = Identifier;
        	this.DeviceID = DeviceID;
		}
		@Override
        protected String doInBackground(String... params) {
        	String resultado = "";
        	try {
        		List<Integer> AnswersWithImages = db.getAnswersWithImages(); 
        		if(AnswersWithImages != null && AnswersWithImages.size() > 0){
     			   for(Integer AnswerID: AnswersWithImages){
     				   Answers Answer = db.getAnswerByAnswerID(AnswerID);
     				   JSONObject item = new JSONObject();
     				   item.put("image", Answer.Value);
     				   String result = ConnectionMethods.Post(SurveyActivity.this,item.toString(), "/UploadImage",true);
     				   if(result.startsWith("\"http://timetrackerstorage.blob.core.windows.net/")){
     					   Answer.Value = result.replaceAll("\"","");
     					   db.UpdateAnswers(Answer);
     				   }else{
     					   return "Ocurrio un error al cargar imagenes";
     				   }
     			   }
     		   	}
        		List<Answers> Answers = db.getAnswersByIdentifier(Identifier);
        		JSONArray jsArray = new JSONArray();
	    		for(Answers row: Answers)
	    		{
	    			JSONObject item = new JSONObject();
	    			item.put("QuestionID", row.QuestionID);
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
   		   			item.put("DeviceID", DeviceID);
	    			jsArray.put(item);
	    		}
	    		resultado = ConnectionMethods.Post(SurveyActivity.this,jsArray.toString(), params[0],true);
	    		if(resultado.equals("")){
	    			return resultado;
	    		} else{
	    			return "Ocurrion un error " + resultado;
	    		}
        	}catch (Exception e) {
        		return "Ocurrion un error Exception";                  
        	}
        }
        @Override
        protected void onPostExecute(String result) {
        	progress.dismiss();
        	if(result.equals(""))
        	{
        		 Toast.makeText(getBaseContext(), "Respuestas Subidas", Toast.LENGTH_LONG).show();
        		 db.deleteAnswersByIdentifier(Identifier);
        		 TakeSurveyAgain(SurveyID,true);
        	}
        	else
        	{
        		Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
        		TakeSurveyAgain(SurveyID,false);
        	}
       }
    }
	 	
	//================================================================================
    // Create Control Listeners
    //================================================================================
	 
	public  void CreateListeners(List<Questions> questions,Boolean HasValues)
	{
		for(Questions question: questions)
		{
			ChooseControl(question.QuestionID,question.QuestionTypeID,question.Answer);
		}
		if(HasValues){
			for(Questions question: questions)
			{
				oldListSentTo = new ArrayList<String>();
				CheckFlowFromControl(question.QuestionID,0,0,false);
			}
		}
	}
	
	public  void ChooseControl(int QuestionID,int QuestionTypeID,String Answer)
	{
		 switch (QuestionTypeID) {
		 	case 0:  
	 			CreateListenerButtonFinish(QuestionID);
	 			break;
		 	case 1:  
		 		CreateListenerText(QuestionID);
		 		break;
		 	case 2:  
		 		CreateListenerText(QuestionID);
	 			break;
		 	case 3:  
		 		CreateListenerText(QuestionID);
		 		break;
		 	case 4:  
		 		CreateListenerRadiobuttons(QuestionID);
		 		break;	 				 
		 	case 5:  
	 			CreateListenerCheckBoxes(QuestionID);
	 			break;
		 	case 6:  
	        	CreateListenerGridSingle(QuestionID);
	        	break;
		 	case 7:  
	        	CreateListenerGridMultiple(QuestionID);
	        	break;
		 	case 8:  
	        	CreateListenerGridSliderInterval(QuestionID);
	        	break;
		 	case 9:  
		 		CreateListenerSlider(QuestionID);
		 		break;
	        case 10:  
	        	CreateListenerSliderInterval(QuestionID);
	        	break;
	        case 11:  
	        	CreateListenerInformation(QuestionID);
	        	break;
			case 12:  
				CreateListenerInformationImage(QuestionID,Answer);
				break;
			case 13:  
				CreateListenerDateTime(QuestionID);
				break;
			case 14:  
				CreateListenerSignature(QuestionID);
				break;
			case 15:  
				CreateListenerImage(QuestionID);
				break;
			case 16:  
				CreateListenerBarcode(QuestionID);
				break;
			case 17:  
				CreateListenerDropdown(QuestionID);
				break;
			case 18:
				CreateListenerAutoComplete(QuestionID);
				break;
			case 19:
				CreateListenerCanvas(QuestionID);
				break;
			case 20:
				CreateListenerPhotoCanvas(QuestionID);
				break;
			case 22:
				CreateListenerButton(QuestionID);
				break;
			default:
				break;
		 }
	 }
	 
	//================================================================================
    // Control Flow Methods
    //================================================================================
    
    public String ShowLayout(String SendTo){
    	 if(SendTo.equals("") || oldListSentTo.contains(SendTo)){
    		 return "";
    	 }
    	 int intSendTo = Integer.parseInt(SendTo); 
    	 if(idLinearLayout < 0){
    		 Next2(null);
			 return "";
		 }
    	 if(!oldListSentTo.contains(SendTo)){
			 oldListSentTo.add(SendTo);
		 }
    	 View linearLayout = (View)findViewById(idLinearLayout + intSendTo);
    	 if(linearLayout != null && linearLayout.getVisibility() != View.VISIBLE){
    		 linearLayout.setVisibility(View.VISIBLE);
    		 return SendTo;
    	 }else{
    		 return "";
    	 }
     }
     
    public String HideLayout(String SendTo, int SurveyID){
    	 if(SendTo.equals("") || oldListSentTo.contains(SendTo)){
    		 return "";
    	 }
    	 int intSendTo = Integer.parseInt(SendTo); 
    	 if(idLinearLayout < 0){
			 return "";
		 }
		 View linearLayout = (View)findViewById(idLinearLayout + intSendTo);
		 if(linearLayout != null && linearLayout.getVisibility() != View.GONE){
			 linearLayout.setVisibility(View.GONE);
	    	 Questions Q = db.getQuestionByOrderNumberAndSurveyID(intSendTo, SurveyID);
	    	 ClearValueFromControl(Q.QuestionID,Q.QuestionTypeID);
			 return SendTo;
		 }else{
			 return "";
		 }
     }
     
    @SuppressLint("DefaultLocale")
    public void CheckFlowFromControl(int QuestionID, int qOrderNumber, int qSurveyID, Boolean isListener){
    	 int flowOption = 0;
    	 List<String> listSentTo = new ArrayList<String>();
    	 int nextOrderNumber = 0;
    	 Questions Q = null;
    	 List<QuestionOptions> QuestionOptions = new ArrayList<QuestionOptions>();
    	 if(QuestionID != 0){
    		 Q = db.getQuestion(QuestionID);
    	 }else{
    		 Q = db.getQuestionByOrderNumberAndSurveyID(qOrderNumber, qSurveyID);
    	 }
    	 if(Q == null){
    		 Toast.makeText(getApplicationContext(), "E0011: Pregunta no encontrada. QuestionID:" + QuestionID + " Order Number:" + qOrderNumber + " Forma: " + qSurveyID, Toast.LENGTH_LONG).show();
    		 return;
    	 }
    	 if(Q.SendTo.equals("") && Q.QuestionTypeID != 4 && Q.QuestionTypeID != 5 && Q.QuestionTypeID != 10){
    		 return;
    	 }
    	 if(Q.QuestionTypeID == 4 || Q.QuestionTypeID == 5 || Q.QuestionTypeID == 10){
    		 QuestionOptions = db.getQuestionOptions(Q.QuestionID);
    		 Boolean hasSendTo = false;
    		 for(QuestionOptions QO: QuestionOptions){
    			 if(!QO.SendTo.equals("")){
    				 hasSendTo = true;
    				 break;
    			 }
    		 }
    		 if(!hasSendTo){
    			 return;
    		 }
    	 }
    	 if((Q.QuestionTypeID >= 1 && Q.QuestionTypeID <= 3) || Q.QuestionTypeID == 17 || Q.QuestionTypeID == 18){
    		 flowOption = 1;
    	 }else if(Q.QuestionTypeID == 4 || Q.QuestionTypeID == 5 || Q.QuestionTypeID == 10){
    		 flowOption = 2;
    	 }else if((Q.QuestionTypeID >= 6 && Q.QuestionTypeID <= 9) || (Q.QuestionTypeID >= 11 && Q.QuestionTypeID <= 13) || Q.QuestionTypeID == 19 || Q.QuestionTypeID == 22){
    		 flowOption = 3;
    	 }else if((Q.QuestionTypeID >= 14 && Q.QuestionTypeID <= 16) || Q.QuestionTypeID == 20){
    		 flowOption = 4;
	     }
    	 if(flowOption == 0){
    		 Toast.makeText(getApplicationContext(), "E0011: Tipo " + Q.QuestionTypeID + " no tiene opcion", Toast.LENGTH_LONG).show();
    		 return;
    	 }
    	 View currentLayout = (View)findViewById(idLinearLayout + (Q.OrderNumber + 1));
    	 if(!isListener && currentLayout != null && currentLayout.getVisibility() != View.VISIBLE){
    		 return;
    	 }
		 switch(flowOption){
		 	case 1:
		 		if(Q.Condition.equals("")){
		 			Toast.makeText(getApplicationContext(), "E0011: Pregunta " + Q.QuestionID + " no tiene condicion", Toast.LENGTH_LONG).show();
		 			return;
		 		}
		 		String ValueAnswer = GetValueFromControl(Q.QuestionID, Q.QuestionTypeID);
		 		String ValueQuestion = Q.Valu.toLowerCase();
		 		if(ValueAnswer.equals(" ")){
		 			ValueAnswer = "";
		 		}
		 		float nValueQuestion = 0;
		 		float nValueAnswer = 0;
		 		boolean isQuestionNumeric = false;
		 		boolean isAnswerNumeric = false;
		 		try{
		 			nValueQuestion = Float.parseFloat(Q.Valu);
		 			isQuestionNumeric = true;
		 		}catch(Exception ex){
		 			isQuestionNumeric = false;
		 		}
		 		try{
		 			nValueAnswer = Float.parseFloat(ValueAnswer);
		 			isAnswerNumeric = true;
		 		}catch(Exception ex){
		 			isAnswerNumeric = false;
		 		}
		 		
		 		if(Q.Condition.equals("=")){
		 			if(ValueQuestion.equals(ValueAnswer)){
		 				listSentTo.add(ShowLayout(Q.SendTo));
		 			}else{
		 				listSentTo.add(HideLayout(Q.SendTo,Q.SurveyID));
		 			}
		 		}else if(Q.Condition.equals("!=")){
		 			if(!ValueQuestion.equals(ValueAnswer)){
		 				listSentTo.add(ShowLayout(Q.SendTo));
		 			}else{
		 				listSentTo.add(HideLayout(Q.SendTo,Q.SurveyID));
		 			}
		 		}else if(Q.Condition.equals("Auto")){
		 			if(currentLayout.getVisibility() == View.VISIBLE){
			 			listSentTo.add(ShowLayout(Q.SendTo));
			 		}else{
			 			listSentTo.add(HideLayout(Q.SendTo,Q.SurveyID));
			 		}
		 		}else if(isQuestionNumeric && isAnswerNumeric){
		 			if(Q.Condition.equals(">")){
		 				if(nValueAnswer > nValueQuestion){
		 					listSentTo.add(ShowLayout(Q.SendTo));
		 				}else{
		 					listSentTo.add(HideLayout(Q.SendTo,Q.SurveyID));
		 				}
		 			}else if(Q.Condition.equals(">=")){
		 				if(nValueAnswer >= nValueQuestion){
		 					listSentTo.add(ShowLayout(Q.SendTo));
		 				}else{
		 					listSentTo.add(HideLayout(Q.SendTo,Q.SurveyID));
		 				}
		 			}else if(Q.Condition.equals("<")){
		 				if(nValueAnswer < nValueQuestion){
		 					listSentTo.add(ShowLayout(Q.SendTo));
		 				}else{
		 					listSentTo.add(HideLayout(Q.SendTo,Q.SurveyID));
		 				}
		 			}else if(Q.Condition.equals("<=")){
		 				if(nValueAnswer <= nValueQuestion){
		 					listSentTo.add(ShowLayout(Q.SendTo));
		 				}else{
		 					listSentTo.add(HideLayout(Q.SendTo,Q.SurveyID));
		 				}
		 			}
	 			}
		 		break;
		 	case 2:
	 			List<String> showedQuestions = new ArrayList<String>();
	 			if(Q.QuestionTypeID == 4){
	 				RadioGroup currentRadioGroup = (RadioGroup)findViewById(Q.QuestionID + idKey);
	 				if(isListener){
	 					for(int count = 0; count < currentRadioGroup.getChildCount(); count++) {
			 				RadioButton currentRadioButton = (RadioButton)currentRadioGroup.getChildAt(count);
				 			for(QuestionOptions QO: QuestionOptions){
				 				if(currentRadioButton.getText().toString().equals(QO.Name))
								{
									if(currentRadioButton.isChecked()){
										if(!listSentTo.contains(QO.SendTo)){
											listSentTo.add(ShowLayout(QO.SendTo));
										}
										showedQuestions.add(QO.SendTo);
									}else{
										if(!showedQuestions.contains(QO.SendTo)){
											if(!listSentTo.contains(QO.SendTo)){
												listSentTo.add(HideLayout(QO.SendTo,Q.SurveyID));
											}
										}
									}
									break;
								}
					 		}
		 				}
	 				}else{
	 					if (currentRadioGroup.getCheckedRadioButtonId() != -1)
	 					{
	 						for(int count = 0; count < currentRadioGroup.getChildCount(); count++) {
				 				RadioButton currentRadioButton = (RadioButton)currentRadioGroup.getChildAt(count);
					 			for(QuestionOptions QO: QuestionOptions){
					 				if(currentRadioButton.getText().toString().equals(QO.Name))
									{
										if(currentRadioButton.isChecked()){
											ShowLayout(QO.SendTo);
											if(!listSentTo.contains(QO.SendTo)){
												listSentTo.add(QO.SendTo);
											}
											showedQuestions.add(QO.SendTo);
										}
										break;
									}
						 		}
			 				}
	 					}
	 				}
	 			}else if(Q.QuestionTypeID == 5){
	 				LinearLayout currentCheckBoxGroup = (LinearLayout)findViewById(Q.QuestionID + idKey);
			 		for(int count = 0; count < currentCheckBoxGroup.getChildCount(); count++) {
			 			CheckBox currentCheckBox = (CheckBox)currentCheckBoxGroup.getChildAt(count);
			 			for(QuestionOptions QO: QuestionOptions){
			 				if(currentCheckBox.getText().toString().equals(QO.Name))
							{
			 					if(isListener){
			 						if(currentCheckBox.isChecked()){
										ShowLayout(QO.SendTo);
										if(!listSentTo.contains(QO.SendTo)){
											listSentTo.add(QO.SendTo);
										}
										showedQuestions.add(QO.SendTo);
									}else{
										if(!showedQuestions.contains(QO.SendTo)){
											HideLayout(QO.SendTo,Q.SurveyID);
											if(!listSentTo.contains(QO.SendTo)){
												listSentTo.add(QO.SendTo);
											}
										}
									}
					 			}else{
					 				if(currentCheckBox.isChecked()){
										ShowLayout(QO.SendTo);
										if(!listSentTo.contains(QO.SendTo)){
											listSentTo.add(QO.SendTo);
										}
									}
					 			}
								break;
							}
				 		}
			 		}
	 			}else if(Q.QuestionTypeID == 10){
	 				SeekbarWithIntervals currentSeekbarWithIntervals = (SeekbarWithIntervals)findViewById(Q.QuestionID + idKey);  
	 				int lastdigit = (Q.QuestionID%1000)*1000; 
		    		int seekBarID = Q.QuestionID + idKey + lastdigit + 2;
	 				SeekBar currentSeekBar= (SeekBar)currentSeekbarWithIntervals.findViewById(seekBarID);
	 				TextView currentTitle = (TextView)findViewById(currentSeekBar.getId() - (int)1);
	 				int currentProgress = 0;
	 				for(QuestionOptions QO : QuestionOptions) {
	 					if(isListener){
	 						if(currentProgress == currentSeekBar.getProgress() && currentTitle.getText().toString().equals(QO.Name)){
								 ShowLayout(QO.SendTo);
								 if(!listSentTo.contains(QO.SendTo)){
									 listSentTo.add(QO.SendTo);
								 }
								 showedQuestions.add(QO.SendTo);
							 }else{
								 if(!showedQuestions.contains(QO.SendTo)){
									 HideLayout(QO.SendTo,Q.SurveyID);
									 if(!listSentTo.contains(QO.SendTo)){
										listSentTo.add(QO.SendTo);
									 }
								 }
							 }
	 					}else{
	 						if(currentProgress == currentSeekBar.getProgress() && currentTitle.getText().toString().equals(QO.Name)){
								 ShowLayout(QO.SendTo);
								 if(!listSentTo.contains(QO.SendTo)){
									 listSentTo.add(QO.SendTo);
								 }
							 }
	 					}
						currentProgress++;
	 				}
	 			}
		 		break;
		 	case 3:
		 		if(currentLayout.getVisibility() == View.VISIBLE){
		 			listSentTo.add(ShowLayout(Q.SendTo));
		 		}else{
		 			listSentTo.add(HideLayout(Q.SendTo,Q.SurveyID));
		 		}
		 		break;
		 	case 4:
		 		if(Q.QuestionTypeID == 14 || Q.QuestionTypeID == 15 || Q.QuestionTypeID == 20){
		 			String hasImage = GetValueFromControl(Q.QuestionID, Q.QuestionTypeID);
		 			if(hasImage.equals("1")){
			 			listSentTo.add(ShowLayout(Q.SendTo));
			 		}else{
			 			listSentTo.add(HideLayout(Q.SendTo,Q.SurveyID));
			 		}
		 		}else if(Q.QuestionTypeID == 16){
		 			if(!GetValueFromControl(Q.QuestionID, Q.QuestionTypeID).equals("")){
			 			listSentTo.add(ShowLayout(Q.SendTo));
			 		}else{
			 			listSentTo.add(HideLayout(Q.SendTo,Q.SurveyID));
			 		}
		 		}
		 		break;
		 }
    	 if(isListener){
    		 for (String sentTo: listSentTo) {
        		 if(!sentTo.equals("")){
            		 Boolean isInteger = false;
            		 try {  
            			 nextOrderNumber = Integer.parseInt(sentTo);
            			 isInteger = true;
            	      } catch (NumberFormatException e) {  
            	    	  Toast.makeText(getApplicationContext(), "E0011: No se pudo convertir " + sentTo + " a entero", Toast.LENGTH_LONG).show();
            	      } 
            		 if(isInteger){
        				 CheckFlowFromControl(0,nextOrderNumber,Q.SurveyID, true);
            		 }
            	 } 
        	 }
    	 }
     }
     
    @SuppressLint("DefaultLocale")
	public String GetValueFromControl(int QuestionID, int QuestionTypeID){
    	 String result = "";
    	 View currentControl = findViewById(QuestionID + idKey);
    	 if(currentControl == null){
    		 return result;
    	 }
    	 switch(QuestionTypeID){
	    	 case 1:
	    	 case 2:
	    	 case 3:
	    		 EditText editControl = (EditText)currentControl;
	    		 result = editControl.getText().toString().toLowerCase();
	    		 break;
	    	 case 9:
	    		 SeekBar seekBarControl = (SeekBar)currentControl;
	    		 result = Integer.toString(seekBarControl.getProgress());
	    		 break;
	    	 case 14:
	    	 case 15:
	    	 case 20:
	    		 ImageView imgView = (ImageView)findViewById(QuestionID + idKey);
	    		 if(imgView.getDrawable() != null)
	    		 {
	    			 result = "1";
	    		 }
	    		 break;
	    	 case 16:
	    		 TextView txtBarCode = (TextView)findViewById(QuestionID + idKey);
	    		 result = txtBarCode.getText().toString();
	    		 break;
	    	 case 17:
	    		 Spinner spinnerControl = (Spinner)currentControl;
	    		 result = spinnerControl.getSelectedItem().toString();
	    		 break;
	    	 case 18:
	    		 AutoCompleteTextView autoCompleteControl = (AutoCompleteTextView)currentControl;
	    		 result = autoCompleteControl.getText().toString();
	    		 break;
    	 }
    	 return result;
     }
    
    @SuppressLint("DefaultLocale")
	public void ClearValueFromControl(int QuestionID, int QuestionTypeID){
    	 View currentControl = findViewById(QuestionID + idKey);
    	 if(currentControl == null){
    		 return;
    	 }
    	 switch(QuestionTypeID){
	    	 case 1:
	    	 case 2:
	    	 case 3:
	    		 EditText editControl = (EditText)currentControl;
	    		 if(!editControl.getText().toString().equals("")){
	    			 editControl.setText("");
	    		 }
	    		 break;
	    	 case 4:
	    		 RadioGroup currentRadioGroup = (RadioGroup)findViewById(QuestionID + idKey);
	    		 if (currentRadioGroup.getCheckedRadioButtonId() != -1)
	    		 {
	    			 currentRadioGroup.clearCheck();
	    		 }
	    		 break;
	    	 case 5:
	    		 LinearLayout currentCheckBoxGroup = (LinearLayout)findViewById(QuestionID + idKey);
	    		 for(int count = 0; count < currentCheckBoxGroup.getChildCount(); count++) {
	    			 CheckBox currentCheckBox = (CheckBox)currentCheckBoxGroup.getChildAt(count);
	    			 if(currentCheckBox.isChecked()){
	    				 currentCheckBox.setChecked(false);
	    			 }
	    		 }
	    		 break;
	    	 case 9:
	    		 SeekBar seekBarControl = (SeekBar)currentControl;
	    		 if(seekBarControl.getProgress() != 0){
	    			 seekBarControl.setProgress(0);
	    		 }
	    		 break;
	    	 case 10:
	    		 SeekbarWithIntervals currentSeekbarWithIntervals = (SeekbarWithIntervals)findViewById(QuestionID + idKey); 
	    		 int lastdigit = (QuestionID%1000)*1000; 
	    		 int seekBarID = QuestionID + idKey + lastdigit + 2;
	    		 SeekBar currentSeekBar = (SeekBar)currentSeekbarWithIntervals.findViewById(seekBarID);
	    		 TextView currentSeekBarTitle = (TextView)findViewById(currentSeekBar.getId() - (int)1);
	    		 currentSeekBarTitle.setText("");
	    		 //currentSeekBar.setProgress(0);
	    		 break;
	    	 case 14:
	    	 case 15:
	    	 case 20:
	    		 ImageView imgView = (ImageView)findViewById(QuestionID + idKey);
	    		 imgView.setImageDrawable(null);
	    		 break;
	    	 case 16:
	    		 TextView txtBarCode = (TextView)findViewById(QuestionID + idKey);
	    		 if(!txtBarCode.getText().toString().equals("")){
	    			 txtBarCode.setText("");
	    		 }
	    		 break;
	    	 case 17:
	    		 Spinner spinnerControl = (Spinner)currentControl;
	    		 if(!spinnerControl.getSelectedItem().toString().equals("")){
	    			 spinnerControl.setSelection(0);
	    		 }
	    		 break;
	    	 case 18:
	    		 AutoCompleteTextView autoCompleteControl = (AutoCompleteTextView)currentControl;
	    		 if(!autoCompleteControl.getText().toString().equals("")){
	    			 autoCompleteControl.setText("");
	    		 }
	    		 break;
    	 }
     }
    
    
	//================================================================================
    // Create Button Finish Listener
    //================================================================================
	 
	 private void CreateListenerButtonFinish(int controlid) {
		 Button button =  (Button) findViewById(controlid+idKey);
		 button.setOnClickListener( new ButtonSendSurvey() );
	 } 	
	 
	 public class ButtonSendSurvey implements View.OnClickListener 
	    {
			public void onClick(final View view ){
				progress.setMessage("Guardando respuestas, por favor espere...");
				progress.show();
	    		try
	    		{
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							view.setEnabled(false);
							view.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_button));
							List<Answers> listAnswers = new ArrayList<Answers>();


							if(lc != null)
							{
								latitude = lc.getLatitude();
								longitude = lc.getLongitude();
							}
							else
							{
								Toast.makeText(getApplicationContext(), "GPS deshabilitado", Toast.LENGTH_LONG).show();
							}
							Devices Device = db.GetDevice();
							String DeviceMac = Device.Name;
							if(DeviceMac == null){
								DeviceMac = "";
							}
							Calendar c = Calendar.getInstance();
							int year = c.get(Calendar.YEAR);
							int month = c.get(Calendar.MONTH);
							int day = c.get(Calendar.DAY_OF_MONTH);
							int hour = c.get(Calendar.HOUR_OF_DAY);
							int seconds = c.get(Calendar.SECOND);
							int milliseccons = c.get(Calendar.MILLISECOND);
							String randomID = UUID.randomUUID().toString();
							int SurveyID = 0;
							String Identifier = "";
							SelectedSurvey SelectedSurvey = db.GetSelectedSurvey();
							SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.US);
							String DateFormFinish = ft.format(new Date());
							for(List<Questions> listrow: SurveyQuestions)
							{
								for(Questions row: listrow)
								{
									if(row.Answer == null){
										row.Answer = "_.";
									}
									SurveyID = row.SurveyID;
									if((row.QuestionTypeID == 15 ||  row.QuestionTypeID == 20) && row.Answer.equals("1")){
										row.Answer = db.getPhoto(row.QuestionID);
									}
									Identifier = randomID + String.valueOf(year) + String.valueOf(month) + String.valueOf(day) + String.valueOf(hour) + String.valueOf(seconds) + String.valueOf(milliseccons);
									Answers item = new Answers(0,row.QuestionID,row.Answer,row.QuestionTypeID,latitude,longitude,DeviceMac,Identifier,SelectedSurvey.DateFormStart,DateFormFinish,SelectedSurvey.UbicheckID);
									listAnswers.add(item);
								}
							}
							sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
							Editor editor = sharedpreferences.edit();
							editor.remove("savequestions");
							editor.remove("saveindex");
							editor.commit();
							db.updateQuestionAnswers(SurveyID);
							db.deletePhotos();
							for(Answers row: listAnswers)
							{
								db.addAnswers(row);
							}
							SelectedSurvey.UbicheckID = 0;
							SelectedSurvey.DateFormStart = "";
							db.UpdateSelectedSurvey(SelectedSurvey);
							if (ConnectionMethods.isInternetConnected(SurveyActivity.this,false).equals(""))
							{
								HttpAsyncTask httpAsyncTask = new HttpAsyncTask(SurveyID,Identifier,Device.DeviceID);
								httpAsyncTask.execute("/Answers");
							}
							else
							{
								progress.dismiss();
								TakeSurveyAgain(SurveyID,true);
							}
						}
					}, 1000);
		        }
	    		catch (Exception e)
		        {
	    			progress.dismiss();
		        	Toast.makeText(getBaseContext(), "E008:" + e.toString(), Toast.LENGTH_LONG).show();
				}
			}
	    }
	 
	 //================================================================================
	 // Take Survey Again
	 //================================================================================
	 
	 public void TakeSurveyAgain(final int SurveyID,Boolean isSuccessful){
		 try{
			 Devices Device = db.GetDevice();
			 if(Device.UsesFormWithUbicheck == 1 && Device.UsesClientValidation == 1){
				 db.deleteSelectedSurveys();
				 Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
	        	 startActivity(intent);
			 }else{
				 AlertDialog.Builder builder = new AlertDialog.Builder(SurveyActivity.this);
		         builder.setTitle("Forma Terminada")
		         .setCancelable(false)
		         .setPositiveButton("Si",new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int id) {
		                 dialog.dismiss();
		                 Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.StartSurvey.class);
		                 startActivity(intent);
		                 finish();
		             }
		         })
		         .setNegativeButton("No",new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int id) {
		            	 dialog.dismiss();
		            	 Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
		            	 startActivity(intent);
		            	 finish();
		             }
		         });
		         if(isSuccessful){
		        	 builder.setMessage("Forma enviada. �Le gustar�a contestar la forma de nuevo?");
		         }else{
		        	 builder.setMessage("Forma guardada de manera local. �Le gustar�a contestar la forma de nuevo?")
		        	 .setIcon(android.R.drawable.ic_dialog_alert);
		         }
		         AlertDialog alert = builder.create();
		         if(!((Activity)SurveyActivity.this).isFinishing()){
		        	 alert.show();
		         }
			 }
		 }catch (Exception e) {
			 Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
			 Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
        	 startActivity(intent);
		} 
	 }
	 
	 //================================================================================
	 // Create Text Listener
	 //================================================================================
	 
	 public void CreateListenerText(int controlid) {
		 final EditText editText = (EditText)findViewById(controlid+idKey);
		 Button button = (Button) findViewById(controlid+idKey2);
		 final int QuestionID = controlid;
		 if(button != null){
			 button.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View view) {
		            	if (ConnectionMethods.isInternetConnected(SurveyActivity.this,false).equals("")){
		            		progress.setMessage("Realizando consulta, por favor espere...");
		         		   	progress.show();
		            		new AsyncGetTable(QuestionID).execute("/Procedures?value=" + URLEncoder.encode(String.valueOf(QuestionID) + "|" + editText.getText().toString()));
		          		}else{
		          			Toast.makeText(getApplicationContext(), "Dispositivo no conectado a internet",Toast.LENGTH_LONG).show();
		          		}
		            }
			 });
		 }
		 editText.addTextChangedListener(new TextWatcher() {
			 public void afterTextChanged(Editable s) {
			 }
			 public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			 }
			 public void onTextChanged(CharSequence s, int start,int before, int count) {
				 int id = editText.getId();
				 oldListSentTo = new ArrayList<String>();
				 CheckFlowFromControl(id - idKey,0,0,true);
				 if(s.length() != 0)
				 {
					 ChangeColos(true);
				 }
				 else
				 {
					 ChangeColos(false);
				 }
			 }
		 });
     }
	 
	 //================================================================================
	 // Create Radio Listener
	 //================================================================================
	 
	 private void CreateListenerRadiobuttons(final int controlid) {
    	 RadioGroup radioGroup = (RadioGroup) findViewById(controlid+idKey);
    	 for(int count = 0; count < radioGroup.getChildCount(); count++) {
    		 RadioButton RadioButton = (RadioButton)radioGroup.getChildAt(count);
    		 RadioButton.setOnClickListener(new OnClickListener(){
 				@Override
 			    public void onClick(View v) {
 					RadioGroup radioGroup = (RadioGroup) findViewById(controlid+idKey);
 					RadioButton RadioButton = (RadioButton)v;
 		        	int id = RadioButton.getId();
 	 		    	 int idOriginal = id;
 	 		    	 id = id - idRadiobutton;
 	 		    	 int questionID = id/1000;
 	 		    	oldListSentTo = new ArrayList<String>();
 	 		    	 CheckFlowFromControl(questionID,0,0,true);
 	 		    	 Questions Question = db.getQuestion(questionID);
 	 		    	 int idLayout = (idLinearLayout + (Question.OrderNumber + 1));
 	 		    	 LinearLayout linearLayoutEdit = (LinearLayout)findViewById(idLayout);
 	 		    	 int childcount = linearLayoutEdit.getChildCount();
 	 		    	 if(radioGroup.getCheckedRadioButtonId()!=-1){
 	 		    	    View radioButton = radioGroup.findViewById(idOriginal);
 	 		    	    int radioId = radioGroup.indexOfChild(radioButton);
 	 		    	    RadioButton btn = (RadioButton) radioGroup.getChildAt(radioId);
 	 		    	    String selection = btn.getText().toString();
 	 		    	    if(selection.equals(Question.OtherOption)){
 	 		    	    	for (int i=0; i < childcount; i++){
 	 		    	    		Question.Answer = Question.OtherOption;
 	 		    	    		db.updateQuestion(Question);
 	 		    	    	      View EditOther = linearLayoutEdit.getChildAt(i);
 	 		    	    	      if (EditOther instanceof EditText) {
 	 		    	    	    	EditOther.setVisibility(View.VISIBLE);
 	 		    	            }
 	 		    	    	}
 	 		    	    }else{
 	 		    	    	Question.Answer = "";
 	 		    	    	db.updateQuestion(Question);
 	 		    	    	for (int i=0; i < childcount; i++){
 	 		    	    	      View EditOther = linearLayoutEdit.getChildAt(i);
 	 		    	    	      if (EditOther instanceof EditText) {
 	 		    	    	    	EditOther.setVisibility(View.GONE);
 	 		    	    	    	((EditText) EditOther).setText("");
 	 		    	            }
 	 		    	    	}
 	 		    	    }
 			    	}else{
 			    		for (int i=0; i < childcount; i++){
 		    	    	      View EditOther = linearLayoutEdit.getChildAt(i);
 		    	    	      if (EditOther instanceof EditText) {
 		    	    	    	 EditOther.setVisibility(View.GONE);
 		    	            }
 		    	    	}
 			    	}
 			    }
 		    });
 		}
	 }

	 //================================================================================
	 // Create Check box Listener
	 //================================================================================
	 
	 private void CreateListenerCheckBoxes(int controlid) {
    	 LinearLayout linearLayout=  (LinearLayout) findViewById(controlid+idKey);
    	 for(int count = 0; count < linearLayout.getChildCount(); count++) {
			CheckBox checkBox = new CheckBox(this);
			checkBox = (CheckBox)linearLayout.getChildAt(count);
			checkBox.setOnClickListener(new OnClickListener(){
				@Override
			    public void onClick(View v) {
		        	CheckBox check = (CheckBox)v;
		        	int id = check.getId();
		        	id = id - idCheckbox;
		        	int questionID = id/1000;
		        	oldListSentTo = new ArrayList<String>();
		        	CheckFlowFromControl(questionID,0,0,true);
			    }
		    });
		}
 	}
	 
	 //================================================================================
	 // Create Simple Grid Listener
	 //================================================================================
	 
	 private void CreateListenerGridSingle(int controlid) {
		 TableLayout TL = (TableLayout)findViewById(controlid + idKey); 
		 for(int i = 0; i < TL.getChildCount(); i++) {
		    if (TL.getChildAt(i) instanceof TableRow) {
		    	TableRow TR = (TableRow)TL.getChildAt(i);
		        for(int j = 0; j < TR.getChildCount(); j++) {
		        	if (TR.getChildAt(j) instanceof RadioButton) {
		        		RadioButton RB = (RadioButton)TR.getChildAt(j);
		        		RB.setOnClickListener(GridRadioButtonListener);
		        	}
		        }
		    }
		}
	 }
	 
	 OnClickListener GridRadioButtonListener = new OnClickListener (){
		 public void onClick(View v) {
			 boolean checked = ((RadioButton) v).isChecked();
			 TableRow TR = (TableRow)v.getParent();
			 for (int i=0; i < TR.getChildCount(); i++){
			   	 if (TR.getChildAt(i) instanceof RadioButton) {
			   		 ((RadioButton) TR.getChildAt(i)).setChecked(false);
			     }
			 }
			 ((RadioButton) v).setChecked(checked);
		 }
	 };
	 
	 //================================================================================
	 // Create Slider Interval Grid Listener
	 //================================================================================
		 
	 private void CreateListenerGridSliderInterval(int controlid) {
	 }

	 //================================================================================
	 // Create Multiple Grid Listener
	 //================================================================================
	 
	 private void CreateListenerGridMultiple(int controlid) {
	 }
	 
	 //================================================================================
	 // Create Slider Listener
	 //================================================================================
	 
	 private void CreateListenerSlider(int controlid) {
	 }

	 //================================================================================
	 // Create Interval Slider Listener
	 //================================================================================
	 
	 private void CreateListenerSliderInterval(int controlid) {
		SeekbarWithIntervals sk = (SeekbarWithIntervals)findViewById(controlid+idKey);  
		View sb= (View)sk.findViewById(R.id.seekbar);
		int lastdigit=(controlid%1000)*1000; 
		int id=controlid+idKey+lastdigit+2;
		sb.setId(id);
	    sk.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			  @Override
			  public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
				  int controlid = seekBar.getId();
				  TextView txtTitle = (TextView)findViewById(controlid-1);
				  int lastdigit =((controlid-2)%1000)*1000; 
				  int questionid = controlid-lastdigit-2-idKey; 
				  String Value  = db.getQuestionValueByID(questionid);
				  String[] Respuestas=Value.split("\\|\\|@@\\|\\|");
				  txtTitle.setText(Respuestas[progresValue]);
				  oldListSentTo = new ArrayList<String>();
				  CheckFlowFromControl(questionid,0,0,true);
			  }
			
			  @Override
			  public void onStartTrackingTouch(SeekBar seekBar) {
			  }
			
			  @Override
			  public void onStopTrackingTouch(SeekBar seekBar) {
			  }
		   });
		}

	 //================================================================================
	 // Create Information Listener
	 //================================================================================
	 
	 private void CreateListenerInformation(final int controlid) {
		  Button btnNext = (Button) findViewById(R.id.btnNext);
		  btnNext.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_button));
	 }
	 
	 //================================================================================
	 // Create Information Image Listener
	 //================================================================================
	 
	 private void CreateListenerInformationImage(int controlid,String Answer) {
	 }

	 //================================================================================
	 // Create Date Time Listener
	 //================================================================================

	 public void CreateListenerDateTime(final int controlid) {
		 DatePicker dp = (DatePicker)findViewById(controlid + idDatePicker); 
		 if(!dp.equals(null)){
			 dp.init(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), new DatePicker.OnDateChangedListener()
			 { 
				@Override
				public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				} 
			 });
		 }
		 TimePicker tp = (TimePicker)findViewById(controlid + idTimePicker);
		 if(!tp.equals(null)){
			 tp.setOnTimeChangedListener(new OnTimeChangedListener()
			 { 
				 public void onTimeChanged(TimePicker arg0, int arg1, int arg2) {
				 } 
			 });
		 }
	 }
	 
	 //================================================================================
	 // Create Signature Listener
	 //================================================================================
	 
	 private void CreateListenerSignature(int controlid) {
		 Button button = (Button) findViewById(controlid+idKey2);
		 button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	_currentControlID=view.getId();
                Intent intent = new Intent(SurveyActivity.this, SignatureActivity.class); 
                startActivityForResult(intent,1);
            }
		 });	 
	 }
	 
	 protected void onSignatureTaken(int controlid)
	 {
		try
		{
			 sharedpreferences = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
			 Editor editor = sharedpreferences.edit();
			 editor.putInt("Flag", 0);
			 editor.commit();
			
			_currentControlID=controlid+idKey;
			_Scanned=false;
		    _taken = false;
			_signed = true;
		
	    	Bitmap bitmap = decodeFile(_tempDir);
	    	ImageView _image = (ImageView) findViewById(controlid);
	    	_image.setImageBitmap(bitmap);
	    	oldListSentTo = new ArrayList<String>();
	    	CheckFlowFromControl(controlid - idKey,0,0,true);
		}
		catch(Exception ex){
			Toast.makeText(getApplicationContext(), "E009:" + ex.toString(),Toast.LENGTH_SHORT).show();
		}
	 }
	 
	 //================================================================================
	 // Create Photo Listener
	 //================================================================================
	 
	 private void CreateListenerImage(int controlid) {
		 Button button=  (Button) findViewById(controlid+idKey2);
		 button.setOnClickListener( new ButtonClickHandler() );
	 }
	 
	 protected void onPhotoTaken(int controlid)
	 {
    	try
    	{
    		sharedpreferences=getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
    		Editor editor = sharedpreferences.edit();
    		editor.putInt("Flag", 0);
    		editor.commit();
	    		
	    	_currentControlID=controlid+idKey;
	    	_taken = true;
	        _signed=false;
	        _Scanned=false;
	    	Bitmap bitmap = decodeFile(_path);
	    	Questions Question  = db.getQuestion(controlid - idKey);
	    	if(Question.QuestionTypeID == 20){
	    		Question.Image = BitMapToString(bitmap);
	    		db.updateQuestionImage(Question);
	    		Intent intent = new Intent(SurveyActivity.this, SignatureActivity.class); 
                intent.putExtra("sQuestionID", Integer.toString(controlid - idKey));
                startActivityForResult(intent,1);
	    	}else{
	    		ImageView _image = (ImageView) findViewById(controlid);
		    	_image.setImageBitmap(bitmap);
	    	}
	    	oldListSentTo = new ArrayList<String>();
	    	CheckFlowFromControl(controlid - idKey,0,0,true);
    	}
    	catch(Exception ex){
    		Toast.makeText(getApplicationContext(), "E010:" + ex.toString(),Toast.LENGTH_SHORT).show();
    	}
    }
	 
	 //================================================================================
	 // Create Bar code Listener
	 //================================================================================
	 
	 private void CreateListenerBarcode(int controlid) {
		 Button button=  (Button) findViewById(controlid+idKey2);
		 button.setOnClickListener( new ButtonClickBarcode() );
     }
	 
	 private void onScanned(String scanContent,int ControID) {
		 
		 sharedpreferences=getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
		 Editor editor = sharedpreferences.edit();
		 editor.putInt("Flag", 0);
		 editor.commit();
 		
		 _currentControlID=ControID+idKey;
		 _Scanned=true;
		 _taken = false;
		 _signed=false;
		 _ScannedValue=scanContent;
		 TextView _textView = (TextView) findViewById(ControID);
		 _textView.setText(scanContent); 
		 oldListSentTo = new ArrayList<String>();
		 CheckFlowFromControl(ControID - idKey,0,0,true);
		 
		 Questions Question = db.getQuestion(ControID - idKey);
		 if(Question.ProcedureID > 0){
			 if (ConnectionMethods.isInternetConnected(SurveyActivity.this,false).equals("")){
				progress.setMessage("Realizando consulta, por favor espere...");
      		   	progress.show();
      		   	if (Question.Options.equals("DeviceID")){
					Devices Device = db.GetDevice();
      		   		scanContent += "-" + Device.DeviceID;
				}

	     		new AsyncGetTable(Question.QuestionID).execute("/Procedures?value=" + URLEncoder.encode(String.valueOf(Question.QuestionID) + "|" + scanContent));
			 }else{
	   			Toast.makeText(getApplicationContext(), "Dispositivo no conectado a internet",Toast.LENGTH_LONG).show();
			 }
		 }
		 //Codigo Andrea 2
		 if(Question.SurveyID == 2135){
		 	try{
				Questions Q = db.getQuestionByOrderNumberAndSurveyID(Question.OrderNumber + 2,2135);
				EditText control = (EditText) findViewById(Q.QuestionID+idKey);
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				control.setText(dateFormat.format(date));
			}catch (Exception ex){
				Toast.makeText(getApplicationContext(), "Error:" + ex.toString(),Toast.LENGTH_LONG).show();
			}
		 }
	 }
	 
	 //================================================================================
	 // Camera Methods
	 //================================================================================
	 
	 public class ButtonClickBarcode implements View.OnClickListener 
	 {
		 public void onClick( View view ){  
			 try {
	    		_Scanned=false;
			    _taken = false;
	    		_signed = false;
	    		_currentControlID=view.getId();
	    		IntentIntegrator scanIntegrator = new IntentIntegrator(SurveyActivity.this);
				scanIntegrator.initiateScan();
			 } catch (Exception e) {
				 Toast toast = Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);
				 toast.show();
			 }
		 }
	 }
		
	 public class ButtonClickHandler implements View.OnClickListener 
	 {
		 public void onClick(View view ){
			 try {
				 if (ContextCompat.checkSelfPermission(SurveyActivity.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			    	 int isPermited = 0;
			         ActivityCompat.requestPermissions(SurveyActivity.this,new String[]{Manifest.permission.CAMERA},isPermited);
				 }else{
					 _Scanned=false;
					 _taken = false;
					 _signed = false;
					 _currentControlID=view.getId();
					 StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
					 StrictMode.setVmPolicy(builder.build());
					 File file = new File(_path);
					 Uri outputFileUri = Uri.fromFile(file);
					 Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					 intent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
					 startActivityForResult(intent, 0);
				 }
			 } catch (Exception e) {
				 Toast toast = Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);
				 toast.show();
			 }
		 }
	 }
	 
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	 {	
		 try {
			 super.onActivityResult(requestCode, resultCode, data);
			 
			 if (requestCode==49374)
			 {  
				 IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
				 if (scanningResult != null) {
					 String scanContent = scanningResult.getContents();
					 onScanned(scanContent,_currentControlID-idKey);	
				 }
				 else{
					 Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
					 toast.show();
				 }
			 }
			 else if (requestCode==1)
			 {
				 if (resultCode == RESULT_OK) {
					 Bundle bundle = data.getExtras();
					 String status  = bundle.getString("status");
					 if(status.equalsIgnoreCase("done")){
						 onSignatureTaken(_currentControlID-idKey);
					 }
				 }  
			 }
			 else
			 {
				 switch(resultCode)
				 {
		    		case 0:
		    			break;
		    		case -1:
		    			onPhotoTaken(_currentControlID-idKey);
		    			break;
				 }
			 }
		 } catch (Exception e) {
			 Toast toas1t = Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
			 toas1t.show();
		 }
	 }
		    
	 public  Bitmap decodeFile(String path) {
		 int orientation;
		 try {
			 if (path == null) {
				 return null;
			 }
			 BitmapFactory.Options o = new BitmapFactory.Options();
			 o.inJustDecodeBounds = true;
			 Bitmap bm = BitmapFactory.decodeFile(path, o);
			 o.inSampleSize = calculateInSampleSize(o, 280, 380);
			 o.inJustDecodeBounds = false;
			 bm = BitmapFactory.decodeFile(path, o);
			 ExifInterface exif = new ExifInterface(path);
			 File fdelete = new File(path);
			 if (fdelete.exists()) {
				 fdelete.delete();
			 }
			 orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
			 Matrix m = new Matrix();
			 if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
				 m.postRotate(180);
				 return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
			 } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				 m.postRotate(90); 
				 return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
			 }
			 else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				 m.postRotate(270);
				 return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
			 } 
			 return bm;
		 } catch (Exception e) {
			 return null;
		 }
	 }
	 
	 public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    int height = options.outHeight;
	    int width = options.outWidth;
	    int inSampleSize = 1;
	    if (height > reqHeight || width > reqWidth) {
	        while (height > reqHeight && width  > reqWidth) {
	        	height = height / 2;
		        width = width / 2;
	            inSampleSize *= 2;
	        }
	    }
	    return inSampleSize;
	 }
	 
	 public String BitMapToString(Bitmap bitmap){
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
		    byte [] b=baos.toByteArray();
		    String temp=Base64.encodeToString(b, Base64.DEFAULT);
		    return temp;
		}
	 
	 //================================================================================
	 // Create Drop Down Listener
	 //================================================================================
	 
	 private void CreateListenerDropdown(final int controlid) {
		Spinner dropdown=  (Spinner) findViewById(controlid+idKey);
		dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				ChangeColos(true);
				try{
					int idNew = parentView.getId() - idKey;
					oldListSentTo = new ArrayList<String>();
					CheckFlowFromControl(idNew,0,0,true);
				}catch(Exception ex){
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});	
 	}
	 
	//================================================================================
	// Create Auto Complete Listener
	//================================================================================
	 
	 public void CreateListenerAutoComplete(int controlid) {
		 final AutoCompleteTextView currentAutoComplete = (AutoCompleteTextView)findViewById(controlid+idKey);
		 currentAutoComplete.addTextChangedListener(new TextWatcher() {
			 public void afterTextChanged(Editable s) {
			 }
			 public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			 }
			 public void onTextChanged(CharSequence s, int start,int before, int count) {
				 int id = currentAutoComplete.getId();
				 oldListSentTo = new ArrayList<String>();
				 CheckFlowFromControl(id - idKey,0,0,true);
				 if(s.length() != 0)
				 {
					 ChangeColos(true);
				 }
				 else
				 {
					 ChangeColos(false);
				 }
			 }
		 });
		 if(controlid == 45917){
			 currentAutoComplete.setOnItemClickListener(new OnItemClickListener() {
		        public void onItemClick(AdapterView<?> parent, View arg1, int pos,long id) {
		        	if (ConnectionMethods.isInternetConnected(SurveyActivity.this,false).equals("")){
	            		progress.setMessage("Realizando consulta, por favor espere...");
	         		   	progress.show();
	         		   	String selection = (String)parent.getItemAtPosition(pos);
	            		new AsyncGetTable(45917).execute("/Procedures?value=" + URLEncoder.encode(String.valueOf(45917) + "|" + selection));
	          		}else{
	          			Toast.makeText(getApplicationContext(), "Dispositivo no conectado a internet",Toast.LENGTH_LONG).show();
	          		}
		        }
		    });
		 }
		 //Codigo Andrea
		 if(controlid == 79750){
			 currentAutoComplete.setOnItemClickListener(new OnItemClickListener() {
				 public void onItemClick(AdapterView<?> parent, View arg1, int pos,long id) {
					 try{
						 List<List<Questions>>  questions = db.getQuestions(2135);
						 String strTexto = currentAutoComplete.getText().toString();
						 String[] strNumero = strTexto.split("-");
						 int intNumero = Integer.parseInt(strNumero[1].replaceAll("\\s+",""));
						 intNumero = intNumero *2;
						 int contador = 1;
						 for(List<Questions> Listquestions: questions)
						 {
							 for(Questions question: Listquestions)
							 {
								 if(question.QuestionID != 79750){
									 View Layout = (View)findViewById(idLinearLayout + (question.OrderNumber + 1));
									 Layout.setVisibility(View.VISIBLE);
									 View control = (View) findViewById(question.QuestionID+idKey);
									 if(question.QuestionTypeID == 16){
										TextView txt = (TextView)control;
										txt.setText("PENDIENTE");
									 }
									 else
									 {
									 	EditText txt = (EditText)control;
									 	txt.setText("PENDIENTE");
									 	txt.setEnabled(false);
									 }

									 contador++;
								 }
								 if(contador > intNumero){
									 break;
								 }
							 }
						 }
					 }catch (Exception ex){
						 Toast.makeText(getApplicationContext(), "Error" + ex.toString(),Toast.LENGTH_LONG).show();
					 }
				 }
			 });

		 }
	 }
	 
	 //================================================================================
	 // Create Canvas Listener
	 //================================================================================
	 
	 private void CreateListenerCanvas(final int controlid) {
		 Button button = (Button) findViewById(controlid+idKey2);
		 button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	_currentControlID = view.getId();
                Intent intent = new Intent(SurveyActivity.this, SignatureActivity.class); 
                intent.putExtra("sQuestionID", Integer.toString(controlid));
                startActivityForResult(intent,1);
            }
		 });	 
	 }
	 
	//================================================================================
	// Create Photo Canvas Listener
	//================================================================================
	 
	 private void CreateListenerPhotoCanvas(int controlid) {
		 Button button=  (Button) findViewById(controlid+idKey2);
		 button.setOnClickListener( new ButtonClickHandler() );
	 }
	
	//================================================================================
	// Create Button Listener
	//================================================================================
		 
	 public void CreateListenerButton(int controlid) {
		 Button button = (Button) findViewById(controlid+idKey2);
		 final int QuestionID = controlid;
		 if(button != null){
			 button.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View view) {
		            	Questions Q = db.getQuestion(QuestionID);
		            	Questions Q2 = db.getQuestion(Integer.parseInt(Q.Value));
		            	//Q2.Answer
		            	EditText editText = (EditText)findViewById(Q2.QuestionID+idKey);
		            	if (ConnectionMethods.isInternetConnected(SurveyActivity.this,false).equals("")){
		            		progress.setMessage("Realizando consulta, por favor espere...");
		         		   	progress.show();
		            		new AsyncGetTable(QuestionID).execute("/Procedures?value=" + URLEncoder.encode(String.valueOf(QuestionID) + "|" + editText.getText().toString()));
		          		}else{
		          			Toast.makeText(getApplicationContext(), "Dispositivo no conectado a internet",Toast.LENGTH_LONG).show();
		          		}
		            }
			 });
		 }
     }
	 
	//================================================================================
    // Check Device type
	//================================================================================
		
	public boolean isTablet(Context context) {
	    boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
	    boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    return (xlarge || large);
	}
	
	//================================================================================
    // External Data Base Functionality
	//================================================================================
	
	@SuppressLint("ClickableViewAccessibility")
	private class AsyncGetTable extends AsyncTask<String, Void, String> {
   		int QuestionID;
        public AsyncGetTable(int QuestionID) {
        	this.QuestionID=QuestionID;
		}
	   @Override
	   	protected String doInBackground(String... urls) {
		   return ConnectionMethods.GET(SurveyActivity.this, urls[0]);
	   }
	   @Override
	   	protected void onPostExecute(String result) {
		   	progress.dismiss();
		   	if(QuestionID == 45917)
		   	{
		   		if(!result.equals("\"0\"")){
		   			result = result.substring(1, result.length()-1);
            		String[] rows = result.split(Pattern.quote("\\r\\n"));
            		if(rows.length > 1){
            			String[] columns = rows[1].split(Pattern.quote(","));
            			for(int x =0;x< SurveyQuestions.size();x++)
        				{
        					List<Questions> questions= SurveyQuestions.get(x);
        					for(Questions Q: questions)
    						{
        						if(Q.QuestionID == 22755){
        							Q.Answer = columns[1].toString();
        							break;
        						}
        						if(Q.QuestionID == 22756){
        							Q.Answer = columns[2].toString();
        							break;
        						}
        						if(Q.QuestionID == 22758){
        							Q.Answer = columns[3].toString();
        							break;
        						}
        						if(Q.QuestionID == 22762){
        							Q.Answer = columns[4].toString();
        							break;
        						}
    						}
        				}
            			Questions Q = db.getQuestion(22755);
    					Q.Answer = columns[1].toString();
    					db.updateQuestion(Q);
    					Q = db.getQuestion(22756);
    					Q.Answer = columns[2].toString();
    					db.updateQuestion(Q);
    					Q = db.getQuestion(22758);
    					Q.Answer = columns[3].toString();
    					db.updateQuestion(Q);
    					Q = db.getQuestion(22762);
    					Q.Answer = columns[4].toString();
    					db.updateQuestion(Q);
            			AlertDialog.Builder builder = new AlertDialog.Builder(SurveyActivity.this);
       		         	builder.setTitle("Resultados Encontrados")
       		         	.setCancelable(false)
       		         	.setPositiveButton("Si",new DialogInterface.OnClickListener() {
       		         		public void onClick(DialogInterface dialog, int id) {
       		         			dialog.dismiss();
   		         			}
   		         		});
       		         	builder.setMessage("Se encontraron resultados, estos ya se cargaron a la Forma.");
       		         	AlertDialog alert = builder.create();
       		         	if(!((Activity)SurveyActivity.this).isFinishing()){
       		         		alert.show();
       		         	}
            		}else{
            			Toast.makeText(getBaseContext(), "No se encontraron resultados", Toast.LENGTH_LONG).show();
            		}
		   		}else{
		   			Toast.makeText(getBaseContext(), "No se pudo realizar la conexion", Toast.LENGTH_LONG).show();
		   		}
		   		return;
		   	}
            try 
            {
            	final TableLayout GridTable = (TableLayout) findViewById(QuestionID+idKey3);
            	GridTable.removeAllViews();
            	if(!result.equals("\"0\"")){
            		result = result.substring(1, result.length()-1);
            		String[] rows = result.split(Pattern.quote("\\r\\n"));
            		boolean isHeader = true;
            		if(rows.length > 0){
            			if(rows.length == 2 && (QuestionID == 22272 || QuestionID == 85289 || QuestionID == 85840)){
            				final Questions Q = db.getQuestion(QuestionID);
				    		int currentOrder = Q.OrderNumber + 1;
            				String[] columns = rows[1].split(Pattern.quote(","));
            				boolean blockAll = false;
        					if(columns[columns.length-1].equals("btn:SelectAllBlock")){
    							blockAll = true;
    						}
            				for(int j = 0; j < columns.length; j++) {
				    			if(!columns[j].toString().equals("") && !columns[j].toString().startsWith("btn:Select")){
				    				Questions newQuestion = db.getQuestionByOrderNumberAndSurveyID(currentOrder, Q.SurveyID);
				    				if(newQuestion != null){
				    					currentOrder++;
				    					View View = (View)findViewById(newQuestion.QuestionID+idKey);
				    					if(View instanceof EditText){
				    						EditText editText = (EditText)View;
        					    			if(editText != null){
        					    				editText.setText(columns[j].toString());
        					    				if(blockAll){
        					    					editText.setEnabled(false);
        					    				}
        					    			}
				    					}else if(View instanceof Spinner){
				    						Spinner Spinner = (Spinner)View;
        					    			if(Spinner != null){
        					    				Spinner.setSelection(getIndex(Spinner, columns[j].toString()));
        					    				if(blockAll){
        					    					Spinner.setEnabled(false);
        					    				}
        					    			}
				    					}
    					    			
				    				}
					    		}
				    		}
            			}else{
            				for(int i=0;i < rows.length;i++){
                				TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                				TableRow tableRow = new TableRow(getBaseContext());
                				tableRow.setLayoutParams(rowParams);
                				String[] columns = rows[i].split(Pattern.quote(","));
        	            		if(columns.length > 0){
        	            			for(int j=0;j < columns.length;j++){
        	            				final TextView rowText = new TextView(getBaseContext());
        	            				rowText.setText(columns[j]);
        	            				rowText.setTextColor(Color.BLACK);
        	            				rowText.setLayoutParams(rowParams);
        	            				rowText.setPadding(3, 3, 5, 3);
        	            				if(columns[j].startsWith("btn:Select")){
        	            					rowText.setText("Seleccionar");
        	            					rowText.setTextColor(Color.BLUE);
        	            					rowText.setTypeface(null, Typeface.BOLD);
        	            					boolean blockAll = false;
        	            					if(columns[j].equals("btn:SelectAllBlock")){
    	            							blockAll = true;
    	            						}
        	            					if(columns[j].equals("btn:SelectAll") || columns[j].equals("btn:SelectAllBlock")){
        	            						final boolean finalBlockAll = blockAll;
        	            						rowText.setOnTouchListener(new OnTouchListener() {
            	            					    public boolean onTouch(View v, MotionEvent event) {
            	            					    	if(GridTable.getChildCount() > 1){
            	            					    		final Questions Q = db.getQuestion(QuestionID);
            	            					    		int currentOrder = Q.OrderNumber + 1;
            	            					    		String currentValue = "";
            	            					    		TableRow SelectedRow = (TableRow)rowText.getParent();
            	            					    		if(SelectedRow != null){
            	            					    			for(int j = 0; j < SelectedRow.getChildCount(); j++) {
                	            					    			currentValue = ((TextView)SelectedRow.getChildAt(j)).getText().toString();
                	            					    			if(!currentValue.equals("") && !currentValue.equals("Seleccionar")){
                	            					    				Questions newQuestion = db.getQuestionByOrderNumberAndSurveyID(currentOrder, Q.SurveyID);
                	            					    				if(newQuestion != null){
                	            					    					currentOrder++;
                	            					    					View View = (View)findViewById(newQuestion.QuestionID+idKey);
                	            					    					if(View instanceof EditText){
                	            					    						EditText editText = (EditText)View;
                            	            					    			if(editText != null){
                            	            					    				editText.setText(currentValue);
                            	            					    				if(finalBlockAll){
                            	            					    					editText.setEnabled(false);
                            	            					    				}
                            	            					    			}
                	            					    					}else if(View instanceof Spinner){
                	            					    						Spinner Spinner = (Spinner)View;
                            	            					    			if(Spinner != null){
                            	            					    				Spinner.setSelection(getIndex(Spinner, currentValue));
                            	            					    				if(finalBlockAll){
                            	            					    					Spinner.setEnabled(false);
                            	            					    				}
                            	            					    			}
                	            					    					}
                        	            					    			
                	            					    				}
                    	            					    		}
                	            					    		}
            	            					    			GridTable.removeAllViews();
            	            					    		}
            	            					    	}
            	            					        return true;
            	            					    }
            	            					});
        	            					}else{
        	            						rowText.setOnTouchListener(new OnTouchListener() {
            	            					    public boolean onTouch(View v, MotionEvent event) {
            	            					    	if(GridTable.getChildCount() > 1){
            	            					    		String txtFirstColumn = "";
            	            					    		TableRow rowFirst = (TableRow)rowText.getParent();
            	            					    		if(rowFirst != null && rowFirst.getChildAt(0) instanceof TextView){
            	            					    			txtFirstColumn = ((TextView)rowFirst.getChildAt(0)).getText().toString();
            	            					    		}
            	            					    		if(!txtFirstColumn.equals("")){
            	            					    			EditText editText = (EditText)findViewById(QuestionID+idKey);
            	            					    			editText.setText(txtFirstColumn);
            	            					    			GridTable.removeAllViews();
            	            					    		}
            	            					    	}
            	            					        return true;
            	            					    }
            	            					});
        	            					}
        	            				}else{
        	            					if(isHeader){
            	            					rowText.setTextAppearance(getBaseContext(), R.style.SQLGridHeader);
            	            				}else{
            	            					rowText.setTextAppearance(getBaseContext(), R.style.SQLGridText);
            	            				}
        	            				}
        	            				tableRow.addView(rowText);
        	            			}
        	            		}
        	            		if(isHeader){
        	            			tableRow.setBackgroundColor(Color.parseColor("#126BAD"));
        	            		}else{
        	            			tableRow.setBackgroundColor(Color.WHITE);
        	            		}
        	            		isHeader = false;
        	            		GridTable.addView(tableRow);
                			}
            			}
            		}
            	}
            	else {
            		Questions Q = db.getQuestion(QuestionID);
            		if (Q.Options.equals("DeviceID")){
						Toast.makeText(getBaseContext(), "No existe ubicheck", Toast.LENGTH_LONG).show();
					}
				}
			} 
            catch (Exception e) {
				 Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
			} 
       }
   }
	
	private int getIndex(Spinner spinner, String myString){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
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
}
