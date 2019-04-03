package com.timetracker.surveys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.timetracker.sqlite.MySQLiteHelper;

import java.text.SimpleDateFormat;
import java.util.Date;


public class InformationActivity extends Activity {
	String currentMonthString,data;
	int monthInDB;
	double realData;
    public MySQLiteHelper db = new MySQLiteHelper(InformationActivity.this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
		}
		TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
		String Mensaje = txtMessage.getText().toString() + "\n\nApp Version: " + pInfo.versionName;
		txtMessage.setText(Mensaje);


		currentMonthString = new SimpleDateFormat("MM").format(new Date());

		TextView txtMonth = (TextView)findViewById(R.id.txtMonth);



		if(db.LastMonth() ==  0){
		    db.insertCurrentDate(new Date());
        }
        monthInDB = db.LastMonth();


		if(Integer.parseInt(currentMonthString) !=  monthInDB ){
			db.EraseDatausageTable();
        }
		realData = db.GetDataUsage()/100;

		data = Double.toString(realData) ;

		txtMonth.setText(
		     "Se han usado " + data + " Kb \n " + " Durante el mes de "
             + GetActualMonthStringSpanish(currentMonthString)
        );

	}

	public String GetActualMonthStringSpanish(String m){
        int dateInt;
	    String Month[] = new String[12];
        Month[0] = "Enero";
        Month[1] = "Febrero";
        Month[2] = "Marzo";
        Month[3] = "Abril";
        Month[4] = "Mayo";
        Month[5] = "Junio";
        Month[6] = "Julio";
        Month[7] = "Agosto";
        Month[8] = "Septiembre";
        Month[9] = "Octubre";
        Month[10] = "Noviembre";
        Month[11] = "Diciembre";
        try {
            dateInt = Integer.parseInt(m);
        }
        catch (NumberFormatException e)
        {
            return "";
        }

        return Month[dateInt - 1] ;
    }

	@SuppressLint("DefaultLocale")
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    Runtime.getRuntime().gc();      
	}
	
	//================================================================================
    // Menu Options
    //================================================================================

	public void onTitleClick(View v) {
		Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.TestActivity.class);
		startActivity(intent);
		finish();
      }  

	public void SendHome(View view){
		Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
		startActivity(intent);
		finish();
	}
	
	//================================================================================
    // Menu Options
    //================================================================================
	
	public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) 
        {
            return true;
        }
        else
        {
        	Toast.makeText(getBaseContext(), "No tiene conexion a internet ", Toast.LENGTH_LONG).show();
            return false;  
        } 
    }
	
}
