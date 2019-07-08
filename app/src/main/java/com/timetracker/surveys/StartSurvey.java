
package com.timetracker.surveys;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.timetracker.business.ConnectionMethods;
import com.timetracker.data.Questions;
import com.timetracker.data.SelectedSurvey;
import com.timetracker.data.Surveys;
import com.timetracker.sqlite.MySQLiteHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class StartSurvey extends Activity {
	ProgressDialog progress;
	int SurveyID;
	TextView tvIsConnected;
	private MySQLiteHelper db = new MySQLiteHelper(StartSurvey.this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		progress = new ProgressDialog(StartSurvey.this);
		progress.setCancelable(false);
		setContentView(R.layout.activity_start_survey);
		TextView txtIntroduction = (TextView) findViewById(R.id.txtIntroduction);
		ImageView imgIntroduction = (ImageView) findViewById(R.id.imgIntroduction);

		SelectedSurvey SelectedSurvey = db.GetSelectedSurvey();
		Surveys survey = db.getSurvey(SelectedSurvey.SurveyID);
		txtIntroduction.setText(survey.IntroductionText);
		Bitmap bitmap = StringToBitMap(survey.IntroductionImage);
		imgIntroduction.setImageBitmap(bitmap);
	}

	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

	public void Start(View view){
		try
		{
			SelectedSurvey SelectedSurvey = db.GetSelectedSurvey();
			String SurveyID = Integer.toString(SelectedSurvey.SurveyID);
			SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.US);
			SelectedSurvey.DateFormStart = ft.format(new Date());
			db.UpdateSelectedSurvey(SelectedSurvey);
			List<Questions> AutocompleteQuestions = db.getQuestionsByAutocompleteAndFeedback(Integer.parseInt(SurveyID));
			if(AutocompleteQuestions != null && AutocompleteQuestions.size() > 0 && ConnectionMethods.isInternetConnected(this,false).equals("")){
				progress.setMessage("Cargando opciones, por favor espere...");
				progress.show();
				String QuestionIDs = "";
				for(Questions Q: AutocompleteQuestions)
				{
					if(!QuestionIDs.equals("")){
						QuestionIDs += "|";
					}
					QuestionIDs += Integer.toString(Q.QuestionID);
				}
				AsyncOptions AsyncOptions = new AsyncOptions(Integer.parseInt(SurveyID));
				AsyncOptions.execute("/Catalogs?QuestionIDs=" + QuestionIDs);
			}else{
				Intent intent = new Intent(this,com.timetracker.surveys.SurveyActivity.class);
				intent.putExtra("SurveyID",SurveyID);
				intent.putExtra("Index","0");
				startActivity(intent);
				finish();
			}
		}
		catch(Exception ex){
			Toast.makeText(getApplicationContext(), ex.toString(),Toast.LENGTH_LONG).show();
		}
	}

	private class AsyncOptions extends AsyncTask<String, Void, String> {
		int intSurveyID;
		public AsyncOptions(int intSurveyID) {
			this.intSurveyID = intSurveyID;
		}
		@Override
		protected String doInBackground(String... urls) {
			return ConnectionMethods.GET(StartSurvey.this,urls[0]);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			try {
				if(!result.equals("\"\""))
				{
					JSONArray catalogs = new JSONArray(result);
					for(int i=0; i< catalogs.length(); i++)
					{
						JSONObject jCatalog = (JSONObject)catalogs.getJSONObject(i);
						int QuestionID = jCatalog.getInt("QuestionID");
						String CatalogElements = jCatalog.getString("CatalogElements");
						Questions Q = db.getQuestion(QuestionID);
						Q.CatalogElements =  CatalogElements;
						db.updateQuestionElements(Q);
						Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.SurveyActivity.class);
						intent.putExtra("SurveyID",Integer.toString(intSurveyID));
						intent.putExtra("Index","0");
						startActivity(intent);
						finish();
					}
				}
			} catch (Exception ex) {
				Toast.makeText(getBaseContext(), "Ocurrio un error al momento de sincronizar objectos. Info: " + ex.toString(), Toast.LENGTH_LONG).show();
			}
		}
	}

	public void SendHome(View view){
		Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
		startActivity(intent);
		finish();
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


}