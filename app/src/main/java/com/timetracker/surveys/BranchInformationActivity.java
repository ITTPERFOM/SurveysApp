package com.timetracker.surveys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.textfield.TextInputEditText;
import com.timetracker.business.ConnectionMethods;
import com.timetracker.business.DialogMethods;

public class BranchInformationActivity extends AppCompatActivity {
    int branchID;
    TextInputEditText txtName,txtCardID,txtResponsableName,txtbusiness;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_information);

        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        txtName = findViewById(R.id.txtName);

        txtCardID = findViewById(R.id.txtCardId);

        txtResponsableName = findViewById(R.id.txtResponsableName);

        txtbusiness = findViewById(R.id.txtbusiness);


       // Bundle extras = getIntent().getExtras();
       // branchID = extras.getInt("branchID");
       // public void getBranchData(int BranchID){
         //   AsyncgetBranchData asyncUpdateSurveys = new AsyncgetBranchData();
           // asyncUpdateSurveys.execute("/Branches" + "?BranchID="+BranchID);
           // Log.d("Prueba","/Branches" + "?BranchID="+BranchID);
        // }

        class AsyncgetBranchData extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {
                return ConnectionMethods.GET(BranchInformationActivity.this,urls[0]);
            }
            @Override
            protected void onPostExecute(String result) {
                try {
                    Log.d("Prueba",result);
                } catch (Exception ex) {
                    DialogMethods.showErrorDialog(BranchInformationActivity.this, "Ocurrio un error al momento de sincronizar formas. Info: " + ex.toString(), "Activity:Home | Method:AsyncUpdateSurveys | Error:" + ex.toString());
                }
            }
        }



    }
}
