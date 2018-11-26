package com.timetracker.surveys;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;

import org.json.JSONObject;

import com.timetracker.business.ConnectionMethods;
import com.timetracker.business.DialogMethods;
import com.timetracker.data.Answers;
import com.timetracker.data.Devices;
import com.timetracker.sqlite.MySQLiteHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

public class TestActivity extends Activity {

	public MySQLiteHelper db = new MySQLiteHelper(TestActivity.this);
	ProgressDialog progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		progress = new ProgressDialog(TestActivity.this);
		progress.setCancelable(false);
	}
	
	public void SendData(View view){
		Devices Device = db.GetDevice();
		List<String> Results = db.getDistincAnswers();
		String Value = "Device: " + Integer.toString(Device.DeviceID) + " ";
		for(String Result: Results){
		   	List<Answers> listAnswers = db.getAnswersByIdentifier(Result);
		   	for(Answers row: listAnswers)
	   		{
	   			if(row.Identifier.equals(Result)){
	   				Value += " QuestionID:" + Integer.toString(row.QuestionID);
	   				if(row.QuestionTypeID != 15){
			   			if(row.Value == null){
		 					row.Value = "_.";
		 				}
		   				row.Value = row.Value.replace('�', ' ');
			   			String Valor = Normalizer.normalize(row.Value,Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+","");
			   			Valor = Valor.replace("�", "");
			   			Value += " Valor:" + Valor;
			   			
	   				}else{
	   					if(row.Value.equals(null) || row.Value.equals("")){
	   						Value += " Valor: Imagen Vacia";
	   					}else{
	   						Value += " Valor: Imagen";
	   					}
	   				}
	   				Value += "||||||";
	   			}
   			}
		}
		progress.setMessage("Enviando Reporte, por favor espere...");
		progress.show();
		AsyncErrorLogCreate AsyncErrorLogCreate = new AsyncErrorLogCreate(Value);
		AsyncErrorLogCreate.execute("/ErrorLogs");
   }
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    db.close();
	    Runtime.getRuntime().gc();
	}
	
	public void SendHome(View view){
		Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
		startActivity(intent);
		finish();
	}
	
	@SuppressLint("SimpleDateFormat")
	private class AsyncErrorLogCreate extends AsyncTask<String, Void, String> {
   		String Message;
        public AsyncErrorLogCreate(String Message) {
        	this.Message = Message;
		}
		@Override
        protected String doInBackground(String... params) {
        	String resultado = "0";
        	 try {
        		 JSONObject item = new JSONObject();
        		 item.put("Message", Message);
        		 resultado = ConnectionMethods.Post(TestActivity.this,item.toString(), params[0],false);
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
        		DialogMethods.showInformationDialog(TestActivity.this, "Reporte enviado", "Ubicacion enviada exitosamente.", null);
        		
        	}
        	else
        	{
        		DialogMethods.showErrorDialog(TestActivity.this, "Ocurrio un error al momento de enviar reporte. Info: " + result, "Activity:Home | AsyncErrorLogCreate | Result:" + result);
        	}
       }
    }
}
