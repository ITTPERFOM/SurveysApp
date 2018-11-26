package com.timetracker.business;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.timetracker.surveys.SeekbarWithIntervals;
import com.timetracker.data.Message;
import com.timetracker.data.Questions;
import com.timetracker.sqlite.MySQLiteHelper;

public class Validations {

	protected int idKey = 100000;
	//================================================================================
    // Choose Control
    //================================================================================
	
	public  Message ValidateControl(Context context, View control,Questions question,boolean isTablet,String other, boolean isVisible)
	{
		 Message validated= new Message(2,"");
		 switch (question.QuestionTypeID) {
		 case 1:  
			 validated = ValidateText(control,question,isVisible);
         break;
		 case 2:  
			 validated = ValidateEmailText(control,question,isVisible);
         break;
		 case 3:  
			 validated = ValidateNumericText(control,question,isVisible);
         break;
		 case 4:  
			 validated = ValidateRadiobuttons(control,question,other,isVisible);
         break;
		 case 5:  
			 validated = ValidateCheckBoxes(control,question,isVisible);
         break;
		 case 6:  
			 validated = ValidateSingleGrid(control,question,isVisible);
         break;
		 case 7:  
			 validated = ValidateMultipleGrid(control,question,isVisible);
         break;
		 case 8:  
			 validated = ValidateSliderIntervalGrid(control,question,isVisible);
         break;
		 case 9:  
			 validated = ValidateSlider(control,question,isVisible);
         break;
		 case 10: 
			 validated =  ValidateSliderInterval(control,question,other,isVisible);
	         break;
		 case 11:  
			 validated = ValidateInformation(control);
         break;
		 case 12:  
			 validated = ValidateInformationImage(control);
         break;
		 case 13:  
			 validated = ValidateDateTime(control,isTablet);
         break;
		 case 14:  
			 validated = ValidateSignature(control,question,isVisible);
         break;
		 case 15:  
			 validated = ValidateImage(control,question,context,isVisible);
         break;
		 case 16:  
			 validated = ValidateBarcode(control,question,isVisible);
         break;
		 case 17:  
			 validated = ValidateDropDown(control,question,isVisible);
         break;
		 case 18:  
			 validated = ValidateAutoComplete(control,question,isVisible);
         break;
		 case 19:  
			 validated = ValidateSignature(control,question,isVisible);
         break;
		 case 20:  
			 validated = ValidateImage(control,question,context,isVisible);
         break;
		 case 22:  
			 validated = ValidateButton(control);
         break;
		 default:
         break;
        
		 }
		 return validated;
	}
	
	//================================================================================
    // Validate Text Control
    //================================================================================
	
	public  Message ValidateText(View control,Questions question, Boolean isVisible)
	{
		final EditText editText = (EditText) control;
		String value=editText.getText().toString();
		if ((question.Minimum!=0 ||  question.Maximum!=0) && !value.equals(""))
		{
			if(value.length()>=question.Minimum && value.length()<=question.Maximum)
			{
			}
			else
			{
				return new Message (2,"El valor ingresado debe tener un m�nimo de "+Integer.toString(question.Minimum) +" y un m�ximo de "+Integer.toString(question.Maximum)+" caracteres" );
			}
		}
		if(question.Required && isVisible)
		{
			if(value.length()>0)
			{
				return new Message (1,value );
			}
			else
			{
				return new Message (2,"Debe llenar este campo para continuar" );
			}
		}
		else
		{
		return new Message (1,value );
		}
	}
	
	//================================================================================
    // Validate Email Text Control
    //================================================================================
	
	public  Message ValidateEmailText(View control,Questions question, Boolean isVisible)
	{
		final EditText editText = (EditText) control;
		String Value=editText.getText().toString();
		if ((question.Minimum!=0 ||  question.Maximum!=0) && !Value.equals(""))
		{
			if(Value.length()>=question.Minimum && Value.length()<=question.Maximum)
			{
			}
			else
			{
				return new Message (2,"El valor ingresado debe tener un m�nimo de "+Integer.toString(question.Minimum) +" y un m�ximo de "+Integer.toString(question.Maximum)+" caracteres" );
			}
		}
		if(question.Required && isVisible)
		{
			if(isEmailValid(Value))
			{
				return new Message (1, editText.getText().toString());
			}
			else
			{
				return new Message (2, "Favor de ingresar una direcci�n de correo valida");
			}
		}
		else
		{
			if(Value.length()>0)
			{
				if(isEmailValid(Value))
				{
					return new Message (1, editText.getText().toString());
				}
				else
				{
					return new Message (2, "Favor de ingresar una direcci�n de correo valida");
				}
			}
			else
			{
				return new Message (1, editText.getText().toString());
			}
		}
	}
	
	//================================================================================
    // Validate Numeric Text Control
    //================================================================================
	
	public  Message ValidateNumericText(View control,Questions question, Boolean isVisible)
	{
		final EditText editText = (EditText) control;
		String value=editText.getText().toString();
		if ((question.Minimum!=0 ||  question.Maximum!=0) && !value.equals("") )
		{
			if(value.length()>=question.Minimum && value.length()<=question.Maximum)
			{
			}
			else
			{
				return new Message (2,"El valor ingresado debe tener un m�nimo de "+Integer.toString(question.Minimum) +" y un m�ximo de "+Integer.toString(question.Maximum)+" caracteres" );
			}
		}
		if (question.Decimals>0)
		{
			String[] Decimals=value.split("\\.");
			if(Decimals.length>1)
			{
				if(Decimals[1].length()<=question.Decimals)
				{
				}
				else
				{
					return new Message (2,"El valor ingresado debe tener un un m�ximo de "+Integer.toString(question.Decimals)+" Decimales" );
				}
			}
		}
		if(question.Required && isVisible)
		{
			if(value.length()>0)
			{
				return new Message (1,value );
			}
			else
			{
				return new Message (2,"Favor de proporcionar  la informaci�n solicitada" );
			}
		}
		else
		{
		return new Message (1,value );
		}
	}	
	
	//================================================================================
    // Validate Radio Button Control
    //================================================================================
	
	public  Message ValidateRadiobuttons(View control,Questions question,String other, Boolean isVisible)
	{
		RadioGroup radioGroup = (RadioGroup) control;
		int radioButtonID = radioGroup.getCheckedRadioButtonId();
		View radioButton = radioGroup.findViewById(radioButtonID);
		if(question.Required==true && !question.Hidden && isVisible)
		{
			int idx = radioGroup.indexOfChild(radioButton);
			if (idx!=-1)
			{
				 RadioButton btn = (RadioButton) radioGroup.getChildAt(idx);
				 if(other == null){
					 return new Message(1, (String) btn.getText());
				 }else{
					 return new Message(1, other);
				 }
				 
			}
			else
			{
				return new Message(2, "Debe seleccionar una respuesta");
			}
		}
		else
		{
			int idx = radioGroup.indexOfChild(radioButton);
			if (idx!=-1)
			{
				 RadioButton btn = (RadioButton) radioGroup.getChildAt(idx);
				 if(other == null){
					 return new Message(1, (String) btn.getText());
				 }else{
					 return new Message(1, other);
				 }
			}
			else
			{
				return new Message(1, "");
			}
		}
	}
	
	//================================================================================
    // Validate Check Box Control
    //================================================================================
	
	public  Message ValidateCheckBoxes(View control,Questions question, Boolean isVisible)
	{
		String Value="";
		int flag=1;
		for(int count = 0; count < ((LinearLayout)control).getChildCount(); count ++) {
			CheckBox checkBox=(CheckBox)((LinearLayout)control).getChildAt(count);
			
			if (checkBox.isChecked())
			{
				if(flag==1)
				{
				Value= (String) checkBox.getText();
				flag++;
				}
				else
				{
					Value=Value+","+ (String) checkBox.getText();
				}
			}
			
		}
		if (question.MinAnswers!=0 ||  question.MaxAnswers!=0)
		{
			
			String[] Responses=Value.split("\\,");
			if(Responses.length>=question.MinAnswers && Responses.length<=question.MaxAnswers)
			{
			
			}
			else
			{
				return new Message (2,"Debe seleccionar un m�ximo de "+Integer.toString(question.MinAnswers) +" y un minimo de "+Integer.toString(question.MaxAnswers)+" respuestas" );
			}
		}
		if(question.Required && isVisible)
		{
			if(Value.length()>0)
			{
				return new Message(1,Value);
			}
			else
			{
				return new Message(2,Value);
			}
		}
		else
		{
			return new Message(1,Value);
		}
	}
	
	//================================================================================
    // Validate Single Grid Control
    //================================================================================
	
	public Message ValidateSingleGrid(View control,Questions question, Boolean isVisible)
	{
		TableLayout TL = (TableLayout) control;
		String value = "";
		List<String> Sentencias = new ArrayList<String>();
		for(int i = 0; i < TL.getChildCount(); i++) {
		    if (TL.getChildAt(i) instanceof TableRow) {
		    	TableRow TR = (TableRow)TL.getChildAt(i);
		    	int sentenceCount = 0;
		        for(int j = 0; j < TR.getChildCount(); j++) {
		        	if(i == 0){
		        		if (TR.getChildAt(j) instanceof TextView) {
		        			TextView TV = (TextView)TR.getChildAt(j);
			        		if(!TV.getText().equals("")){
			        			Sentencias.add((String)TV.getText());
			        		}
			        	}
		        	}else{
		        		if (TR.getChildAt(j) instanceof RadioButton) {
			        		if(((RadioButton)TR.getChildAt(j)).isChecked()){
			        			if(!value.equals("")){
			        				value+= "||@@||";
			        			}
			        			value+= (i - 1) + "|" + Sentencias.get(sentenceCount);
			        		}
			        		sentenceCount++;
			        	}
		        	}
		        }
		    }
		}
		if(question.Required==true && isVisible)
		{
			if (value.equals(""))
			{
				return new Message(2, "Debe seleccionar un valor");
			}
			else
			{
				return new Message(1,  value);
			}
		}
		else
		{
			return new Message(1, value);
		}
	}
	
	//================================================================================
    // Validate Multiple Grid Control
    //================================================================================
	
	public Message ValidateMultipleGrid(View control,Questions question, Boolean isVisible)
	{
		TableLayout TL = (TableLayout) control;
		String value = "";
		String valueRow ="";
		for(int i = 0; i < TL.getChildCount(); i++) {
			if(i != 0){
		    	TableRow TR = (TableRow)TL.getChildAt(i);
		        for(int j = 0; j < TR.getChildCount(); j++) {
		        	if(j != 0){
		        		if(!valueRow.equals("")){
		        			valueRow += "|";
		        		}
		        		if (TR.getChildAt(j) instanceof CheckBox) {
			        		if(((CheckBox)TR.getChildAt(j)).isChecked()){
			        			valueRow += "1";
			        		}else{
			        			valueRow += "0";
			        		}
			        	}
		        		if (TR.getChildAt(j) instanceof EditText) {
		        			String text = ((EditText)TR.getChildAt(j)).getText().toString();
		        			if(text.equals("")){
		        				valueRow += " ";
		        			}else{
		        				valueRow += text;
		        			}
		        			
			        	}
		        	}
		        }
		        if(!value.equals("")){
		        	value += "||@@||";
        		}
		        value += valueRow;
		        valueRow = "";
			}
		}
		if(question.Required && isVisible)
		{
			if (value.equals(""))
			{
				return new Message(2, "Debe ingresar un valor");
			}
			else
			{
				return new Message(1,  value);
			}
		}
		else
		{
			return new Message(1, value);
		}
	}
	
	//================================================================================
    // Validate Slider Interval Grid Control
    //================================================================================
	
	public Message ValidateSliderIntervalGrid(View control,Questions question, Boolean isVisible)
	{
		TableLayout TL = (TableLayout) control;
		String value = "";
		List<String> Sentencias = new ArrayList<String>();
		for(int i = 0; i < TL.getChildCount(); i++) {
		    if (TL.getChildAt(i) instanceof TableRow) {
		    	TableRow TR = (TableRow)TL.getChildAt(i);
		        for(int j = 0; j < TR.getChildCount(); j++) {
		        	if(i == 0){
		        		if (TR.getChildAt(j) instanceof TextView) {
		        			TextView TV = (TextView)TR.getChildAt(j);
			        		if(!TV.getText().equals("")){
			        			Sentencias.add((String)TV.getText());
			        		}
			        	}
		        	}else{
		        		if (TR.getChildAt(j) instanceof SeekbarWithIntervals) {
		        			Integer iValue = ((SeekbarWithIntervals)TR.getChildAt(j)).getProgress();
		        			if(!value.equals("")){
		        				value+= "||@@||";
		        			}
		        			value+= (i - 1) + "|" + Sentencias.get(iValue);
			        	}
		        	}
		        }
		    }
		}
		if(question.Required && isVisible)
		{
			if (value.equals(""))
			{
				return new Message(2, "Debe seleccionar un valor");
			}
			else
			{
				return new Message(1,  value);
			}
		}
		else
		{
			return new Message(1, value);
		}
	}
	
	//================================================================================
    // Validate Slider Control
    //================================================================================
	
	public  Message ValidateSlider(View control,Questions question, Boolean isVisible)
	{
		SeekBar seekBar = (SeekBar) control;
		int value =seekBar.getProgress();
		if(question.Required && isVisible)
		{
			if (value==0)
			{
				return new Message(2, "Debe seleccionar un valor en el deslizador");
			}
			else
			{
				return new Message(1,  Integer.toString(value));
			}
		}
		else
		{
			return new Message(1,  Integer.toString(value));
		}
	}
	
	//================================================================================
    // Validate Slider Interval Control
    //================================================================================
	
	public  Message ValidateSliderInterval(View control,Questions question,String other, Boolean isVisible)
	{
		SeekbarWithIntervals sk = (SeekbarWithIntervals)control;
		String value= Integer.toString(sk.getProgress());
		if (value.equals(" ") || other.equals(""))
		{
			value="";
		}
		if(question.Required && isVisible)
		{
			if(value.length()>0)
			{
				return new Message (1,value );
			}
			else
			{
				return new Message (2,"Favor de proporcionar  la informaci�n solicitada" );
			}
		}
		else
		{
		return new Message (1,value );
		}
	}
	
	//================================================================================
    // Validate Information Control
    //================================================================================
	
	public  Message ValidateInformation(View control)
	{
		return new Message (1,"  ");
	}
	
	//================================================================================
    // Validate Information Image Control
    //================================================================================
	
	public  Message ValidateInformationImage(View control)
	{
		return new Message (1,"  ");
	}
	
	//================================================================================
    // Validate Date Time Control
    //================================================================================
	
	@SuppressWarnings("deprecation")
	public  Message ValidateDateTime(View control, boolean isTablet)
	{ 
		RelativeLayout relativeLayout = (RelativeLayout) control;
		if(isTablet){
			DatePicker datePicker = (DatePicker)relativeLayout.getChildAt(0);
			TimePicker TimePicker = (TimePicker)relativeLayout.getChildAt(1);
			String Value=String.valueOf(datePicker.getYear())+"/"+String.valueOf(datePicker.getMonth())+"/"+String.valueOf(datePicker.getDayOfMonth());
			Value=Value+"@"+String.valueOf(TimePicker.getCurrentHour())+":"+String.valueOf(TimePicker.getCurrentMinute());
			return new Message (1, Value);
		}else{
			LinearLayout linLayout = (LinearLayout)relativeLayout.getChildAt(0);
			DatePicker datePicker = (DatePicker)linLayout.getChildAt(0);
			TimePicker TimePicker = (TimePicker)linLayout.getChildAt(1);
			String Value=String.valueOf(datePicker.getYear())+"/"+String.valueOf(datePicker.getMonth())+"/"+String.valueOf(datePicker.getDayOfMonth());
			Value=Value+"@"+String.valueOf(TimePicker.getCurrentHour())+":"+String.valueOf(TimePicker.getCurrentMinute());
			return new Message (1, Value);
		}
	}
	
	//================================================================================
    // Validate Signature Control
    //================================================================================
	
	public  Message ValidateSignature(View control,Questions question, Boolean isVisible)
	{
		ImageView imageView = (ImageView) control;
		if(question.Required && isVisible)
		{
			if(imageView.getDrawable() == null)
			{
				return new Message(2,"Debe ingresar la firma");
			}
			BitmapDrawable BD = (BitmapDrawable) imageView.getDrawable();
			if(BD == null){
				return new Message(2,"BitmapDrawable es nulo");
			}
			Bitmap bitmap = BD.getBitmap();
			if(bitmap == null){
				return new Message(2,"Bitmap es nulo");
			}
			String resultado = BitMapToString(bitmap);
			if(resultado.startsWith("ERROR:")){
				return new Message(2,resultado);
			}
			return new Message(1, resultado);
		}
		else
		{
			if(imageView.getDrawable() == null)
			{
				return new Message(1,"");
			}
			BitmapDrawable BD = (BitmapDrawable) imageView.getDrawable();
			if(BD == null){
				return new Message(2,"BitmapDrawable es nulo");
			}
			Bitmap bitmap = BD.getBitmap();
			if(bitmap == null){
				return new Message(2,"Bitmap es nulo");
			}
			String resultado = BitMapToString(bitmap);
			if(resultado.startsWith("ERROR:")){
				return new Message(2,resultado);
			}
			return new Message(1, resultado);
		}
	}
	
	//================================================================================
    // Validate Image Control
    //================================================================================
	
	public  Message ValidateImage(View control,Questions question,Context context, Boolean isVisible)
	{
		ImageView imageView = (ImageView) control;
		if(question.Required && isVisible)
		{
			if(imageView.getDrawable() == null)
			{
				return new Message (2, "Favor de tomar la fotograf�a");
			}
			BitmapDrawable BD = (BitmapDrawable) imageView.getDrawable();
			if(BD == null){
				return new Message(2,"BitmapDrawable es nulo");
			}
			Bitmap bitmap = BD.getBitmap();
			if(bitmap == null){
				return new Message(2,"Bitmap es nulo");
			}
			String resultado = BitMapToString(bitmap);
			if(resultado.startsWith("ERROR:")){
				return new Message(2,resultado);
			}
			MySQLiteHelper db = new MySQLiteHelper(context);
			String photoExist = db.getPhoto(question.QuestionID);
			if(photoExist.equals("")){
				db.addPhoto(resultado, question.QuestionID);
			}
			else{	
				db.UpdatePhoto(resultado, question.QuestionID);
			}
			db.close();
			return new Message (1, "1");
		}
		else
		{
			if(imageView.getDrawable() == null)
			{
				return new Message (1, "");
			}
			BitmapDrawable BD = (BitmapDrawable) imageView.getDrawable();
			if(BD == null){
				return new Message(2,"BitmapDrawable es nulo");
			}
			Bitmap bitmap = BD.getBitmap();
			if(bitmap == null){
				return new Message(2,"Bitmap es nulo");
			}
			String resultado = BitMapToString(bitmap);
			if(resultado.startsWith("ERROR:")){
				return new Message(2,resultado);
			}
			MySQLiteHelper db = new MySQLiteHelper(context);
			String photoExist = db.getPhoto(question.QuestionID);
			if(photoExist.equals("")){
				db.addPhoto(resultado, question.QuestionID);
			}
			else{	
				db.UpdatePhoto(resultado, question.QuestionID);
			}
			db.close();
			return new Message (1, "1");
		}
	}
	
	//================================================================================
    // Validate Bar Code Control
    //================================================================================
	
	public  Message ValidateBarcode(View control,Questions question, Boolean isVisible)
	{
		TextView editText = (TextView) control;
		String value=editText.getText().toString();
		if(question.Required && isVisible)
		{
			if(value.length()>0)
			{
				return new Message (1,value );
			}
			else
			{
				return new Message (2,"Favor de proporcionar  la informaci�n solicitada" );
			}
		}
		else
		{
		return new Message (1,value );
		}
	}
	
	//================================================================================
    // Validate DropDown Control
    //================================================================================
	
	public  Message ValidateDropDown(View control,Questions question, Boolean isVisible)
	{
	    Spinner editText = (Spinner) control;
		String value= editText.getSelectedItem().toString();
		if (value.equals(" "))
		{
			value="";
		}
		
		if(question.Required && isVisible)
		{
			if(value.length()>0)
			{
				return new Message (1,value );
			}
			else
			{
				return new Message (2,"Favor de proporcionar  la informaci�n solicitada" );
			}
			
		}
		else
		{
			return new Message (1,value );
		}
	}
	
	//================================================================================
    // Validate Auto Complete Control
    //================================================================================
	
	public  Message ValidateAutoComplete(View control,Questions question, Boolean isVisible)
	{
		final AutoCompleteTextView AutoComplete = (AutoCompleteTextView) control;
		String value=AutoComplete.getText().toString();
		if ((question.Minimum!=0 ||  question.Maximum!=0) && !value.equals(""))
		{
			if(value.length()>=question.Minimum && value.length()<=question.Maximum)
			{
			}
			else
			{
				return new Message (2,"El valor ingresado debe tener un m�nimo de "+Integer.toString(question.Minimum) +" y un m�ximo de "+Integer.toString(question.Maximum)+" caracteres" );
			}
		}
		if(question.Required && isVisible)
		{
			if(value.length()>0)
			{
				return new Message (1,value );
			}
			else
			{
				return new Message (2,"Debe llenar este campo para continuar" );
			}
		}
		else
		{
		return new Message (1,value );
		}
	}
	
	//================================================================================
    // Validate Button Control
    //================================================================================
	
	public  Message ValidateButton(View control)
	{
		return new Message (1,"  ");
	}
	
	//================================================================================
    // Helper Methods
    //================================================================================
	
	public String BitMapToString(Bitmap bitmap){
		try{
			if(bitmap == null){
				return "ERROR: Bitmap es nulo";
			}
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
	        if(baos.size() == 0){
				return "ERROR: Tama�o de ByteArrayOutputStream es 0";
			}
	        byte[] b = baos.toByteArray();
	        if(b == null){
				return "ERROR: Array de Bytes es nulo";
			}
	        String temp = Base64.encodeToString(b, Base64.DEFAULT);
	        return temp;
		}catch(Exception ex){
			return "ERROR:" + ex.toString();
		}
  }
	
	public static boolean isEmailValid(String email) {
	        boolean isValid = false;

	        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	        CharSequence inputStr = email;

	        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	        Matcher matcher = pattern.matcher(inputStr);
	        if (matcher.matches()) {
	            isValid = true;
	        }
	        return isValid;
	    }

}
