package com.timetracker.surveys;



import com.timetracker.surveys.R;

import android.app.Activity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

@SuppressLint("CutPasteId")
public class MainActivity extends Activity {
	  private EditText  username=null;
	   private EditText  password=null;
	   private TextView attempts;
	   private Button login;
	   int counter = 3;
	   private Context context;
	   
	   public static final String MyPREFERENCES = "MyPrefs" ;
	   public static final String user = "userKey"; 
	   public static final String pass = "passwordKey"; 
	   SharedPreferences sharedpreferences;


	   @Override
	   protected void onResume() {
	      sharedpreferences=getSharedPreferences(MyPREFERENCES, 
	      Context.MODE_PRIVATE);
	      if (sharedpreferences.contains(user))
	      {
	      if(sharedpreferences.contains(pass)){
	    	  
	    	  if(sharedpreferences.contains("savequestions")){
	    		  
	    		  
	    		  AlertDialog.Builder builder = new AlertDialog.Builder(this);
		          builder.setTitle("Encueta incompleta")
		          .setMessage("�le gustar�a completar la encuesta?")
		          .setCancelable(false)
		          .setPositiveButton("Si",new DialogInterface.OnClickListener() {
		              public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
		                  
		                  Intent intent = new Intent(getApplicationContext(),com.timetracker.surveys.SurveyActivity.class);
				    		int SurveyID= sharedpreferences.getInt("savequestions", 0);
				    		int Index= sharedpreferences.getInt("saveindex", 0);
				    		  intent.putExtra("SurveyID",Integer.toString(SurveyID)); 
				   		     intent.putExtra("Index",Integer.toString(Index));
				   		  
				   		 
				   		     Editor editor = sharedpreferences.edit();
			     		      editor.remove("savequestions");
			     		     editor.remove("saveindex");
			     		      editor.commit();
				   		     
				   	        startActivity(intent);
				   	     finish();
		                  
		              }
		          })
		          .setNegativeButton("No",new DialogInterface.OnClickListener() {
		              public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
		                  int SurveyID= sharedpreferences.getInt("savequestions", 0);
		                  Editor editor = sharedpreferences.edit();
		     		      editor.remove("savequestions");
		     		      editor.remove("saveindex");
		     		      editor.commit();
		                  Intent i = new Intent(getApplicationContext(),com.timetracker.surveys.HomeActivity.class);
     	         		startActivity(i);
     	         		finish();
		              }
		          });
		          AlertDialog alert = builder.create();
		          alert.show();
	    		  
	    		  
	    		 
		   
	    		  
	    	  }
	    	  else
	    	  {
	    	  
	         Intent i = new Intent(this,com.timetracker.surveys.HomeActivity.class);
	         startActivity(i);
	         finish();
	    	  }
	      }
	      }
	      super.onResume();
	   }

	   
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	  super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);
          username = (EditText)findViewById(R.id.editTextEmail);
          password = (EditText)findViewById(R.id.editTextEmail);
          attempts = (TextView)findViewById(R.id.textView5);
          attempts.setText(Integer.toString(counter));
          login = (Button)findViewById(R.id.btnCheckIn);
    }

    public void login(View view){
        if(username.getText().toString().equals("admin") && 
        password.getText().toString().equals("admin")){
        Toast.makeText(getApplicationContext(), "Redirecting...", 
        Toast.LENGTH_SHORT).show();
        
        Editor editor = sharedpreferences.edit();
        String u = username.getText().toString();
        String p = password.getText().toString();
        editor.putString(user, u);
        editor.putString(pass, p);
        editor.commit();

        
        Intent intent = new Intent(this,com.timetracker.surveys.HomeActivity.class);
        startActivity(intent);
        finish();
     }	
     else{
        Toast.makeText(getApplicationContext(), "Wrong Credentials",
        Toast.LENGTH_SHORT).show();
        attempts.setBackgroundColor(Color.RED);	
        counter--;
        attempts.setText(Integer.toString(counter));
        if(counter==0){
           login.setEnabled(false);
        }

     }
    }
    
    
    
    //check Internet connection.
    public void CheckConectivity(View view){
    	try
    	{
    		context=getApplicationContext();
    		ConnectivityManager check = (ConnectivityManager) this.context.
    			      getSystemService(Context.CONNECTIVITY_SERVICE);
    		int flag=0;
    			      if (check != null) 
    			      {
    			         @SuppressWarnings("deprecation")
						NetworkInfo[] info = check.getAllNetworkInfo();
    			         if (info != null) 
    			            for (int i = 0; i <info.length; i++) 
    			            if (info[i].getState() == NetworkInfo.State.CONNECTED)
    			            {
    			            	flag=1;
    			               Toast.makeText(context, "Internet is connected",
    			               Toast.LENGTH_SHORT).show();
    			            }
    			      }
    			      else{
    			         Toast.makeText(context, "not conencted to internet",
    			         Toast.LENGTH_SHORT).show();
    			          }
    			      if (flag != 1) 
    			      {
    			    	  Toast.makeText(context, "not conencted to internet",
    		    			         Toast.LENGTH_SHORT).show();
    			      }
    			      

    	}
       catch(Exception ex){
    	   Toast.makeText(getApplicationContext(), ex.toString(),
    		        Toast.LENGTH_SHORT).show();

       }

    }
    
    
    
    
  
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
