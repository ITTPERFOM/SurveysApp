package com.timetracker.surveys;

import java.util.List;

import com.timetracker.data.Devices;
import com.timetracker.data.Surveys;
import com.timetracker.sqlite.MySQLiteHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class SelectSurvey extends Activity {

	private int UbicheckID = 0;
	LinearLayout lyButtons;
	private MySQLiteHelper db = new MySQLiteHelper(SelectSurvey.this);
	private TableRow tr_options;
	boolean extra = false;
	
	@Override
	 protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_survey);

		Devices Device = db.GetDevice();

		tr_options = (TableRow)findViewById(R.id.tr_options);
		Intent MyIntent = getIntent();
		Bundle extras = MyIntent.getExtras();



		if (extras != null) {
			extra = extras.getBoolean("extra");
		}

		if(Device.UsesFormWithUbicheck == 1  && db.CheckUbicheckID()){
			if(!extra){
				tr_options.setVisibility(View.INVISIBLE);
			}
		}
		try
        {
	        String DistributionChannel = "";
	        if (extras != null) {
	            if (extras.containsKey("UbicheckID")) {
	            	UbicheckID = extras.getInt("UbicheckID", 0);
	            	//db.AppendUbicheckID(UbicheckID);
	            }
	            if (extras.containsKey("DistributionChannel")) {
	            	DistributionChannel = extras.getString("DistributionChannel", "");
	            }
	        }else {
	        	if(db.GetFormSettings()){
	        		UbicheckID = db.GetUbicheckID();
				}
			}
	        List<Surveys> list = db.getAllsurveys(DistributionChannel);
	        lyButtons = (LinearLayout) findViewById(R.id.Buttons);
      	  	if(list == null || list.size() == 0){
      	  		if(DistributionChannel.equals("")){
      	  			Toast.makeText(getBaseContext(), "No hay Formularios Cargados", Toast.LENGTH_LONG).show();
      	  		}else{
      	  			Toast.makeText(getBaseContext(), "No hay Formularios Cargados con ese Canal de Distribucion", Toast.LENGTH_LONG).show();
      	  		}
      	  		Intent intent = new Intent(this,com.timetracker.surveys.HomeActivity.class);
      	  		startActivity(intent);
      	  	}
      	  	for ( Surveys row : list ) { 
      	  		LinearLayout linLayout = new LinearLayout(SelectSurvey.this);
      	  		Button newButton = new Button(SelectSurvey.this);
      	  		newButton.setText(row.SurveyName);
      	  		newButton.setId(row.SurveyID);
      	  		if(row.StatusID == 2){
      	  			newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_test));
      	  		}else{
      	  			newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_check));
      	  		}
      	  		newButton.setTextAppearance(getApplicationContext(), R.style.button_text);
       			LinearLayout.LayoutParams tdf = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
       			tdf.setMargins(0, 10, 0, 10);
       			newButton.setLayoutParams(tdf);
       			newButton.setOnClickListener(new View.OnClickListener() {
	                    @Override
	                    public void onClick(View v) {
	                      CallStartSurvey(String.valueOf(((Button) v).getId()),String.valueOf(((Button) v).getText()));
	                    }
	                });
       			linLayout.addView(newButton);
        		lyButtons.addView(linLayout);
      	  	}
        } catch (Exception ex) {
			 Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_LONG).show();
		}
    }

	@Override
	public void onBackPressed() {
		Devices Device = db.GetDevice();
		if(!extra){
			buildAlertBackToHome();
		}else {
			super.onBackPressed();
		}

	}

	private void buildAlertBackToHome() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("¿Desea continuar con la seleccion de forma?")
				.setCancelable(false)
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						dialog.cancel();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						Intent intent = new Intent(SelectSurvey.this,HomeActivity.class);
						startActivity(intent);
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void onDestroy() {
	    super.onDestroy();
	    db.close();
	}
	
	private void CallStartSurvey(String SurveyID,String SurveryName) {
		db.deleteSelectedSurveys();
		db.addSelectedSurvey(SurveryName, Integer.parseInt(SurveyID),"",UbicheckID);
		if(SurveyID.equals("68")){
			Intent intent = new Intent(SelectSurvey.this,com.timetracker.surveys.TestActivity.class);
	        startActivity(intent);
		}else{
			Intent intent = new Intent(SelectSurvey.this,com.timetracker.surveys.StartSurvey.class);
	        startActivity(intent);
		}
		finish();
    }
	
	public void SendHome(View view){
		Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
		startActivity(intent);
		finish();
	}
}
