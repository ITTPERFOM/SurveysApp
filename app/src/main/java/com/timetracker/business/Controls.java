package com.timetracker.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.timetracker.surveys.R;
import com.timetracker.surveys.SeekbarWithIntervals;
import com.timetracker.data.QuestionOptions;
import com.timetracker.data.QuestionSentences;
import com.timetracker.data.Questions;
import com.timetracker.data.Surveys;
import com.timetracker.sqlite.MySQLiteHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

@SuppressLint("InflateParams")
@SuppressWarnings("deprecation")
public class Controls {
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
	protected int TextColor = 0;
	protected int TextSize = 0;
	
	public  View CreateControls(Context context, List<Questions> questions)
	{
        ScrollView scroll = new ScrollView(context);
		scroll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		LinearLayout linLayout = new LinearLayout(context);
	    linLayout.setOrientation(LinearLayout.VERTICAL);
	    LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
	    linLayout.setLayoutParams(linLayoutParam);
	    Boolean focusflag = true;
	    if(questions.size() > 0){
	    	MySQLiteHelper db = new MySQLiteHelper(context);
	    	Surveys S = db.getSurvey(questions.get(0).SurveyID);
	    	if(S != null){
	    		try{
	    			TextColor = Integer.parseInt(S.TextColor);
		    		TextSize = Integer.parseInt(S.TextSize);
	    		}catch(Exception ex){
	    		}
	    	}
	    }
		for(Questions question: questions)
		{
			linLayout.addView(ChooseControl(context,question,focusflag));
			focusflag=false;
		}
		linLayout.addView(AddFooterSpace(context));
		scroll.addView(linLayout);
		return scroll;
	}
	
	private LinearLayout AddFooterSpace(Context context) {
		LinearLayout linLayout= new LinearLayout(context);
		LinearLayout.LayoutParams tdf = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		tdf.setMargins(0, 30, 0, 30);
		linLayout.setLayoutParams(tdf);
		return linLayout;
	}
	
	//================================================================================
    // Choose Control Method
    //================================================================================
	
	public  View ChooseControl(Context context,Questions question,Boolean focusflag)
	{
		View control;
		int QuestionTypeID = question.QuestionTypeID;
		switch (QuestionTypeID) {
		 case 0:  
			 control = CreateButtonFinishControl(context,question,focusflag);
         break;
		 case 1:  
			 control = CreateTextControl(context,question,focusflag);
         break;
		 case 2:  
			 control = CreateEmailControl(context,question,focusflag);
	         break;
		 case 3:  
			 control = CreateNumericControl(context,question,focusflag);
	         break;
		 case 4:  
			 control = CreateRadiobuttonsControl(context,question,focusflag);
	         break;
		 case 5: 
			 control = CreateCheckBoxsControl(context,question,focusflag);
	         break;
		 case 6: 
			 control = CreateSingleGridControl(context,question,focusflag);
	         break;
		 case 7: 
			 control = CreateMultipleGridControl(context,question,focusflag);
	         break;
		 case 8: 
			 control = CreateSliderIntervalGridControl(context,question,focusflag);
	         break;
		 case 9: 
			 control = CreateSliderControl(context,question,focusflag);
	         break;
		 case 10: 
			 control = CreateSliderIntervalControl(context,question,focusflag);
	         break;
		 case 11: 
			 control = CreateInformationControl(context,question,focusflag);
	         break;
		 case 12:  
			 control = CreateInformationImageControl(context,question,focusflag);
         break;
		 case 13:
			 control=CreateDateTimeControl(context,question,focusflag);
			 break;
		 case 14:
			 control=CreateSignatureControl(context,question,focusflag);
			 break;
		 case 15:
			 control=CreateImageControl(context,question,focusflag);
			 break;
		 case 16:  
			 control = CreateBarcodeReaderControl(context,question,focusflag);
         break;
		 case 17: 
			 control = CreateDropDownControl(context,question,focusflag);
         break;
		 case 18: 
			 control = CreateAutoCompleteControl(context,question,focusflag);
         break;
		 case 19: 
			 control = CreateCanvasControl(context,question,focusflag);
         break;
		 case 20: 
			 control = CreatePhotoCanvasControl(context,question,focusflag);
         break;
		 case 22: 
			 control = CreateButtonControl(context,question,focusflag);
         break;
		 default:
			 control=null;
         break;
		 }
		 return control;
	}
	
	//================================================================================
    // Create Button Finish Method
    //================================================================================
	
	public  View CreateButtonFinishControl(Context context,Questions question,Boolean focusflag)
	{
        LinearLayout linLayout = new LinearLayout(context);
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
		Button button = new Button(context);
		int id =question.QuestionID+idKey;
		button.setId(id);
		button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.green_button));
		button.setTextColor(Color.parseColor("#ffffff"));
		button.setText(question.Answer);
		if (focusflag==true)
		{
			button.requestFocus();
		}
		MySQLiteHelper db = new MySQLiteHelper(context);
		Surveys survey = db.getSurvey(question.SurveyID); 
		if (!survey.EndingImage.equals(""))
		{
			ImageView imgEnding = new ImageView(context);
			Bitmap bitmap = StringToBitMap(survey.EndingImage);
			imgEnding.setImageBitmap(bitmap);
			imgEnding.setAdjustViewBounds(true);
			linLayout.addView(imgEnding);
		}
		if(!survey.EndingText.equals("")){
			LinearLayout.LayoutParams td = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		    td.setMargins(0, 0, 0, 7);
		    TextView txtEnding =  new TextView(context);
		    txtEnding.setText(survey.EndingText);
		    txtEnding.setGravity(Gravity.CENTER);
		    txtEnding.setTextAppearance(context, R.style.textview_QuestionTitle);
		    txtEnding.setTypeface(null, Typeface.BOLD);
		    txtEnding.setLayoutParams(td);
		    linLayout.addView(txtEnding);
		}
		linLayout.addView(button);
        return linLayout;
	}
	
	//================================================================================
    // Create Text Method
    //================================================================================
	
	public  View CreateTextControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
		EditText editText = new EditText(context);
		editText.setSingleLine(true);
		int id = question.QuestionID+idKey;
		editText.setId(id);
		editText.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.edittext));
		editText.setText(question.Answer);
		if (focusflag==true)
		{
			editText.requestFocus();
		}
		if(question.QuestionID == 82591){
			editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		linLayout.addView(editText);
		if(question.ProcedureID > 0){
			Button button = new Button(context);
			button.setId(question.QuestionID+idKey2);
			button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.green_button));
			button.setTextColor(Color.parseColor("#ffffff"));
			button.setText("Enviar");
			linLayout.addView(button);
			HorizontalScrollView horizontalView = new HorizontalScrollView(context);
			TableLayout GridTable = new TableLayout(context);
    		TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
    		GridTable.setLayoutParams(tableParams);
    		GridTable.setColumnStretchable(0, true);
    		GridTable.setId(question.QuestionID+idKey3);
    		horizontalView.addView(GridTable);
			linLayout.addView(horizontalView);
		}
		if(question.Blocked == 1 || question.QuestionID == 22279 || question.QuestionID == 22287 
				|| question.QuestionID == 22280 || question.QuestionID == 22281 || question.QuestionID == 22282 || question.QuestionID == 22283){
			editText.setEnabled(false);
		}
		linLayout=AddFooter(linLayout,question,context);
        return linLayout;
	}
	
	//================================================================================
    // Create Email Method
    //================================================================================
	
	public  View CreateEmailControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
		EditText editText = new EditText(context);
		int id =question.QuestionID+idKey;
		editText.setId(id);
		editText.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.edittext));
		editText.setText(question.Answer);
		editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		if (focusflag==true)
		{
			editText.requestFocus();
		}
		linLayout.addView(editText);
		linLayout=AddFooter(linLayout,question,context);
        return linLayout;
	}
	
	//================================================================================
    // Create Numeric Method
    //================================================================================
	
	public  View CreateNumericControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
		 RelativeLayout relativeLayout = new RelativeLayout(context);   
		 TextView Preffix = new TextView(context);
		 Preffix.setText(question.Preffix);  
		 TextView Suffix = new TextView(context);
		 Suffix.setId(question.QuestionID+12); 
		 Suffix.setText(question.Suffix);
		 RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		 lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		 Suffix.setLayoutParams(lp);
		 EditText editText = new EditText(context);
		 int id = question.QuestionID+idKey;
		 editText.setId(id);
		 editText.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.edittext));
		 editText.setText(question.Answer);
		 if(question.Decimals==0)
		 {
			 editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		 }
		 else
		 {
			 editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		 }
		 if (focusflag==true)
		 {
			 editText.requestFocus();
		 }
		 RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		 lp2.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		 lp2.setMargins(80, 0, 80, 0);
		 editText.setLayoutParams(lp2);
		 relativeLayout.addView(editText);
		 relativeLayout.addView(Preffix);
		 relativeLayout.addView(Suffix);
		 linLayout.addView(relativeLayout);
		 linLayout=AddFooter(linLayout,question,context);
		 return linLayout;
	}
	
	//================================================================================
    // Create Radio Method
    //================================================================================
	
	public  View CreateRadiobuttonsControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
	    linLayout=AddHeader(linLayout,question,context);
	    linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
		RadioGroup radioGroup = new RadioGroup(context);
		EditText editText = new EditText(context);
		radioGroup.setId(question.QuestionID+idKey);
		String[] Respuestas=question.Value.split("\\|\\|@@\\|\\|");
		int id = (question.QuestionID * 1000) + idRadiobutton;
		if(question.Randomize==true)
		{
			shuffleArray(Respuestas);
		}
		boolean isOtherOption = true;
		for(String row: Respuestas)
		{
			RadioButton radioBtn1 = new RadioButton(context);
			radioBtn1.setText(row);
			radioBtn1.setId(id);
			if(question.Answer.equals(row))
			{
				radioBtn1.setChecked(true);
				isOtherOption = false;
			}
			id++;
			if(question.DisplayImages){
				radioBtn1.setTextSize(0);
				radioBtn1.setPadding(0, 5, 0, 5);
				MySQLiteHelper db = new MySQLiteHelper(context); 
				QuestionOptions QO = db.getQuestionOptionByQuestionIDAndName(question.QuestionID, row);
				db.close();
				if(QO.Image != null){
					Bitmap bitmap = StringToBitMap(QO.Image);
					BitmapDrawable DR = new BitmapDrawable(context.getResources(), bitmap);
					radioBtn1.setCompoundDrawablesWithIntrinsicBounds(DR, null, null, null);
				}
			}
			radioGroup.addView(radioBtn1);
		}
		if(!question.OtherOption.equals("")){
			RadioButton radioBtnOther = new RadioButton(context);
			radioBtnOther.setText(question.OtherOption);
			radioBtnOther.setId(id);
			editText.setId(id);
			editText.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.edittext));
			if(!question.Answer.equals("") && isOtherOption)
			{
				radioBtnOther.setChecked(true);
				editText.setText(question.Answer);
				question.Answer = question.OtherOption;
				MySQLiteHelper db = new MySQLiteHelper(context); 
				db.updateQuestion(question);
			}else{
				editText.setVisibility(View.GONE);
			}
			id++;
			radioGroup.addView(radioBtnOther);
		}
		if (focusflag==true)
		{
			radioGroup.requestFocus();
		}
		linLayout.addView(radioGroup);
		if(!question.OtherOption.equals("")){
			linLayout.addView(editText);
		}
		linLayout=AddFooter(linLayout,question,context);
        return linLayout;
	}
	
	//================================================================================
    // Create Check Box Method
    //================================================================================
	
	public  View CreateCheckBoxsControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linearLayout=AddHeader(linearLayout,question,context);
		linearLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linearLayout.setVisibility(View.GONE);
	    }
		 LinearLayout linLayout = new LinearLayout(context);
	     linLayout.setOrientation(LinearLayout.VERTICAL);
	     LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
	     linLayout.setLayoutParams(linLayoutParam);
	     linLayout.setId(question.QuestionID+idKey);
		 String[] Respuestas=question.Value.split("\\|\\|@@\\|\\|");
		 
		 int id = (question.QuestionID * 1000) + idCheckbox;

	     if(question.Randomize==true)
			{
			 shuffleArray(Respuestas);
			}
			for(String row: Respuestas)
			{
				CheckBox checkBox = new CheckBox(context);
				checkBox.setId(id);
				checkBox.setText(row);
				String[] Selected=question.Answer.split(",");
				for(String val: Selected)
				{
					if (val.equals(row))
					{
						checkBox.setChecked(true);
					}
				
				}
				id++;
				if(question.DisplayImages){
					checkBox.setTextSize(0);
					checkBox.setPadding(0, 5, 0, 5);
					MySQLiteHelper db = new MySQLiteHelper(context); 
					QuestionOptions QO = db.getQuestionOptionByQuestionIDAndName(question.QuestionID, row);
					db.close();
					if(QO.Image != null){
						Bitmap bitmap = StringToBitMap(QO.Image);
						BitmapDrawable DR = new BitmapDrawable(context.getResources(), bitmap);
						checkBox.setCompoundDrawablesWithIntrinsicBounds(DR, null, null, null);
					}
				}
				linLayout.addView(checkBox);
			}
			if (focusflag==true)
			{
				linLayout.requestFocus();
			}
			linearLayout.addView(linLayout);
			linearLayout=AddFooter(linearLayout,question,context);
	        return linearLayout;
	}
	
	static void shuffleArray(String[] ar)
	  {
	    Random rnd = new Random();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      String a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }

	//================================================================================
    // Create Simple Grid Method
    //================================================================================
	
	public  View CreateSingleGridControl(Context context, Questions question, Boolean focusflag)
	{
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linearLayout=AddHeader(linearLayout,question,context);
		linearLayout.setId(idLinearLayout + (question.OrderNumber + 1));
		if(question.Hidden)
	    {
			linearLayout.setVisibility(View.GONE);
	    }
		
		HorizontalScrollView horizontalView = new HorizontalScrollView(context);
		
		TableLayout GridTable = new TableLayout(context);
		GridTable.setId(question.QuestionID+idKey);
		TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
		GridTable.setLayoutParams(tableParams);
		GridTable.setColumnStretchable(0, true);
		
		horizontalView.setLayoutParams(tableParams);
		
		MySQLiteHelper db = new MySQLiteHelper(context); 
		List<QuestionSentences> QuestionSentences = db.getQuestionSentences(question.QuestionID);
		List<QuestionOptions> QuestionOptions = db.getQuestionOptions(question.QuestionID);
		
		TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		TableRow tableRow = new TableRow(context);
		tableRow.setLayoutParams(rowParams);
		
		TextView RowHeader = new TextView(context);
		RowHeader.setText("");
		RowHeader.setLayoutParams(rowParams);
		RowHeader.setPadding(3, 3, 3, 3);
		RowHeader.setTextAppearance(context, R.style.GridTextHeaderLeft);
		tableRow.addView(RowHeader);
		
		for(QuestionOptions QO: QuestionOptions){
			RowHeader = new TextView(context);
			RowHeader.setText(QO.Name);
			RowHeader.setLayoutParams(rowParams);
			RowHeader.setPadding(10, 3, 10, 3);
			RowHeader.setTextAppearance(context, R.style.GridTextHeaderCenter);
			tableRow.addView(RowHeader);
		}
		GridTable.addView(tableRow);
		
		String[] Answers = question.Answer.split("\\|\\|@@\\|\\|");
		for(int i =0; i < QuestionSentences.size();i++){
			tableRow = new TableRow(context);
			tableRow.setLayoutParams(rowParams);
			
			RowHeader = new TextView(context);
			RowHeader.setText(QuestionSentences.get(i).Name);
			RowHeader.setLayoutParams(rowParams);
			RowHeader.setPadding(3, 3, 3, 3);
			RowHeader.setTextAppearance(context, R.style.GridTextHeaderLeft);
			tableRow.addView(RowHeader);
			
			for(QuestionOptions QO: QuestionOptions){
				RadioButton radioQuestion = new RadioButton(context);
				radioQuestion.setLayoutParams(rowParams);
				radioQuestion.setPadding(3, 3, 3, 3);
				if(Answers.length > 0){
					for(int j = 0; j< Answers.length;j++){
						String[] valores = Answers[j].split("\\|");
						if(valores[0].equals(String.valueOf(i)) && valores[1].equals(QO.Name)){
							radioQuestion.setChecked(true);
						}
					}
				}
				tableRow.addView(radioQuestion);
			}
			
			GridTable.addView(tableRow);
		}
		if (focusflag==true)
		{
			linearLayout.requestFocus();
		}
		horizontalView.addView(GridTable);
		linearLayout.addView(horizontalView);
		linearLayout = AddFooter(linearLayout,question,context);
		return linearLayout;
	}
	
	//================================================================================
    // Create Multiple Grid Method
    //================================================================================
	
	public  View CreateMultipleGridControl(Context context, Questions question, Boolean focusflag)
	{
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linearLayout=AddHeader(linearLayout,question,context);
		linearLayout.setId(idLinearLayout + (question.OrderNumber + 1));
		if(question.Hidden)
	    {
			linearLayout.setVisibility(View.GONE);
	    }
		
		HorizontalScrollView horizontalView = new HorizontalScrollView(context);
		
		TableLayout GridTable = new TableLayout(context);
		GridTable.setId(question.QuestionID+idKey);
		TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
		GridTable.setLayoutParams(tableParams);
		GridTable.setColumnStretchable(0, true);
		
		horizontalView.setLayoutParams(tableParams);
		
		MySQLiteHelper db = new MySQLiteHelper(context); 
		List<QuestionSentences> QuestionSentences = db.getQuestionSentences(question.QuestionID);
		List<QuestionOptions> QuestionOptions = db.getQuestionOptions(question.QuestionID);
		
		TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		TableRow tableRow = new TableRow(context);
		tableRow.setLayoutParams(rowParams);
		
		TextView RowHeader = new TextView(context);
		RowHeader.setText("");
		RowHeader.setLayoutParams(rowParams);
		RowHeader.setPadding(3, 3, 3, 3);
		RowHeader.setTextAppearance(context, R.style.GridTextHeaderLeft);
		tableRow.addView(RowHeader);
		
		for(QuestionOptions QO: QuestionOptions){
			RowHeader = new TextView(context);
			RowHeader.setText(QO.Name);
			RowHeader.setLayoutParams(rowParams);
			RowHeader.setPadding(10, 3, 10, 3);
			RowHeader.setTextAppearance(context, R.style.GridTextHeaderCenter);
			tableRow.addView(RowHeader);
		}
		GridTable.addView(tableRow);
		String[] Answers = question.Answer.split("\\|\\|@@\\|\\|");
		for(int i =0; i < QuestionSentences.size(); i++){
			tableRow = new TableRow(context);
			tableRow.setLayoutParams(rowParams);
			
			RowHeader = new TextView(context);
			RowHeader.setText(QuestionSentences.get(i).Name);
			RowHeader.setLayoutParams(rowParams);
			RowHeader.setPadding(3, 3, 3, 3);
			RowHeader.setTextAppearance(context, R.style.GridTextHeaderLeft);
			tableRow.addView(RowHeader);
			
			for(int j =0; j < QuestionOptions.size(); j++){
				if(QuestionOptions.get(j).IsText == 0 || QuestionOptions.get(j).Type.equals("Si/No")){
					CheckBox checkQuestion = new CheckBox(context);
					checkQuestion.setLayoutParams(rowParams);
					checkQuestion.setPadding(3, 3, 3, 3);
					if(!question.Answer.equals("")){
						if(((String)Answers[i].split("\\|")[j]).equals("1")){
							checkQuestion.setChecked(true);
						}
					}
					tableRow.addView(checkQuestion);
				}else{
					EditText editQuestion = new EditText(context);
					if(QuestionOptions.get(j).Type.equals("Numerico")){
						editQuestion.setInputType(InputType.TYPE_CLASS_NUMBER);
					}else if(QuestionOptions.get(j).Type.equals("Decimal")){
						editQuestion.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
					}
					editQuestion.setLayoutParams(rowParams);
					if(!question.Answer.equals("")){
						String texto = (String)Answers[i].split("\\|")[j];
						if(!texto.equals(" ")){
							editQuestion.setText(texto);
						}
					}
					tableRow.addView(editQuestion);
				}
			}
			
			GridTable.addView(tableRow);
		}
		if (focusflag==true)
		{
			linearLayout.requestFocus();
		}
		horizontalView.addView(GridTable);
		linearLayout.addView(horizontalView);
		linearLayout = AddFooter(linearLayout,question,context);
		return linearLayout;
	}
	
	//================================================================================
    // Create Slider Interval Grid Method
    //================================================================================
	
	public  View CreateSliderIntervalGridControl(Context context, Questions question, Boolean focusflag)
	{
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linearLayout=AddHeader(linearLayout,question,context);
		linearLayout.setId(idLinearLayout + (question.OrderNumber + 1));
		if(question.Hidden)
	    {
			linearLayout.setVisibility(View.GONE);
	    }
		
		HorizontalScrollView horizontalView = new HorizontalScrollView(context);
		
		TableLayout GridTable = new TableLayout(context);
		GridTable.setId(question.QuestionID+idKey);
		TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
		GridTable.setLayoutParams(tableParams);
		GridTable.setColumnStretchable(0, true);
		
		horizontalView.setLayoutParams(tableParams);
		
		MySQLiteHelper db = new MySQLiteHelper(context); 
		List<QuestionSentences> QuestionSentences = db.getQuestionSentences(question.QuestionID);
		List<QuestionOptions> QuestionOptions = db.getQuestionOptions(question.QuestionID);
		
		TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		TableRow tableRow = new TableRow(context);
		tableRow.setLayoutParams(rowParams);
		
		TextView RowHeader = new TextView(context);
		RowHeader.setText("");
		RowHeader.setLayoutParams(rowParams);
		RowHeader.setPadding(3, 3, 3, 3);
		RowHeader.setTextAppearance(context, R.style.GridTextHeaderLeft);
		tableRow.addView(RowHeader);
		
		for(QuestionOptions QO: QuestionOptions){
			RowHeader = new TextView(context);
			RowHeader.setText(QO.Name);
			RowHeader.setLayoutParams(rowParams);
			RowHeader.setPadding(3, 3, 3, 3);
			RowHeader.setTextAppearance(context, R.style.GridTextHeaderLeft);
			tableRow.addView(RowHeader);
		}
		GridTable.addView(tableRow);
		String[] Answers = question.Answer.split("\\|\\|@@\\|\\|");
		String[] Values = question.Value.split("\\|\\|@@\\|\\|");
		List<String> seekbarIntervals= new ArrayList<String>();
		for(String item: Values)
		{
			seekbarIntervals.add(item);
		}
		
		for(int i =0; i < QuestionSentences.size();i++){
			tableRow = new TableRow(context);
			tableRow.setLayoutParams(rowParams);
			
			RowHeader = new TextView(context);
			RowHeader.setText(QuestionSentences.get(i).Name);
			RowHeader.setLayoutParams(rowParams);
			RowHeader.setPadding(3, 3, 3, 3);
			RowHeader.setTextAppearance(context, R.style.GridTextHeaderLeft);
			tableRow.addView(RowHeader);
			
			View seekBarxml = (View) LayoutInflater.from(context).inflate(R.layout.seekbar_with_intervals_control, null);
			TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 0, 0, 7);
			params.span = seekbarIntervals.size();
			seekBarxml.setLayoutParams(params);
			SeekbarWithIntervals seekBar = (SeekbarWithIntervals)seekBarxml.findViewById(R.id.seekbarWithIntervals);
			seekBar.setIntervals(seekbarIntervals);
			
			if(Answers.length > 0){
				for(int j = 0; j< Answers.length;j++){
					String[] valores = Answers[j].split("\\|");
					if(valores[0].equals(String.valueOf(i))){
						if(Values.length > 0){
							for(int k = 0; k < Values.length;k++){
								if(valores[1].equals(Values[k].toString())){
									seekBar.setProgress(k);
								}
							}
						}
					}
				}
			}else{
				seekBar.setProgress(0);
			}
			tableRow.addView(seekBar);
			GridTable.addView(tableRow);
		}
		if (focusflag==true)
		{
			linearLayout.requestFocus();
		}
		horizontalView.addView(GridTable);
		linearLayout.addView(horizontalView);
		linearLayout = AddFooter(linearLayout,question,context);
		return linearLayout;
	}
	
	//================================================================================
    // Create Slider Method
    //================================================================================
	
	public  View CreateSliderControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
		 //String[] Respuestas=question.Answer.split("\\|\\|@@\\|\\|");
		
		 
			 RelativeLayout relativeLayout = new RelativeLayout(context);   
			 TextView textViewLeft = new TextView(context);
			 textViewLeft.setId(question.QuestionID+111); 
			 textViewLeft.setText(question.LeftLabel);  
			 TextView textViewRight = new TextView(context);
			 textViewRight.setId(question.QuestionID+12); 
			 textViewRight.setText(question.RightLabel);
			 
			// Defining the layout parameters of the TextView
			 RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
			          RelativeLayout.LayoutParams.WRAP_CONTENT,
			          RelativeLayout.LayoutParams.WRAP_CONTENT);
			          lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			          textViewRight.setLayoutParams(lp);

			 relativeLayout.addView(textViewLeft);
			 relativeLayout.addView(textViewRight);
			 
			

			 
		 
		SeekBar seekBar = new SeekBar(context);
		seekBar.setId(question.QuestionID+idKey);
		int index =0;
		if(question.Answer.length()>0)
        {
		  index = Integer.parseInt(question.Answer);
        }
		seekBar.setProgress(index);
		
		if (focusflag==true)
		{
			seekBar.requestFocus();
		}
		linLayout.addView(relativeLayout);
		linLayout.addView(seekBar);
		linLayout=AddFooter(linLayout,question,context);
        return linLayout;
	}
	
	//================================================================================
    // Create Slider Interval Method
    //================================================================================
	
	public  View CreateSliderIntervalControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
	    String[] Respuestas = question.Value.split("\\|\\|@@\\|\\|");
		List<String> seekbarIntervals= new ArrayList<String>();
		for(String item: Respuestas)
		{
			seekbarIntervals.add(item);
		}
		LinearLayout.LayoutParams td = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		td.setMargins(0, 0, 0, 7);

		View seekBarxml = (View) LayoutInflater.from(context).inflate(R.layout.seekbar_with_intervals_control, null);
		SeekbarWithIntervals seekBar = (SeekbarWithIntervals)seekBarxml.findViewById(R.id.seekbarWithIntervals);
		seekBar.setIntervals(seekbarIntervals);
		int lastdigit=(question.QuestionID%1000)*1000; 
		int id=question.QuestionID+idKey+lastdigit+1;
		TextView txtTitle =  new TextView(context);
		if (question.Answer!= null && !question.Answer.equals("")){
			seekBar.setProgress(Integer.parseInt(question.Answer));
			txtTitle.setText(Respuestas[Integer.parseInt(question.Answer)]);
		}
		else{
			seekBar.setProgress(0);
		}
	    txtTitle.setId(id);
	    txtTitle.setGravity(Gravity.CENTER);
	    txtTitle.setTypeface(null, Typeface.BOLD);
	    txtTitle.setLayoutParams(td);
		seekBar.setId(question.QuestionID+idKey);
		seekBar.setFocusable(true);
		seekBar.setFocusableInTouchMode(true);
		if(focusflag==true){
			seekBar.requestFocus();
		}
		else{
			seekBar.clearFocus();
		} 
		linLayout.addView(txtTitle);
		linLayout.addView(seekBar);
		linLayout=AddFooter(linLayout,question,context);
        return linLayout;
	}
	
	//================================================================================
    // Create Information Method
    //================================================================================
	
	public  View CreateInformationControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
		EditText editText = new EditText(context);
		editText.setId(question.QuestionID+idKey);
		editText.setText(question.Answer);
		if (focusflag==true)
		{
			editText.setFocusable(true);
			editText.setFocusableInTouchMode(true);
		//editText.requestFocus();
		}
		editText.setVisibility(View.GONE);
		linLayout.addView(editText);
		linLayout=AddFooter(linLayout,question,context);
        return linLayout;

	}
	
	//================================================================================
    // Create Information Image Method
    //================================================================================
	
	public  View CreateInformationImageControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
	    
		ImageView  imageView = new ImageView(context);
		imageView.setId(question.QuestionID+idKey);
		Bitmap bitmap = StringToBitMap(question.Image);
		imageView.setImageBitmap(bitmap);

		
		linLayout.addView(imageView);
		linLayout=AddFooter(linLayout,question,context);
		return linLayout;
	}
	
	//================================================================================
    // Create Date Time Method
    //================================================================================
	
	public  View CreateDateTimeControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
	    
		if (!"1/1/1800 12:00:00 AM".equals(question.DefaultDate))
		{
			if (question.Answer.length()  ==0)
			{
				String Date =question.DefaultDate.split(" ")[0];
				String fecha=Date;
				fecha=fecha+"@"+question.DefaultDate.split(" ")[1];
				
				question.Answer=fecha;
			}
		}
		
		RelativeLayout relativeLayout = new RelativeLayout(context);  
		relativeLayout.setId(question.QuestionID+idKey);
		LayoutInflater inflater = LayoutInflater.from(context);
		DatePicker datePicker = (DatePicker)inflater.inflate(R.layout.date_picker, null);
		datePicker.setId(question.QuestionID + idDatePicker);
		datePicker.setCalendarViewShown(false);
		
		TimePicker TimePicker = new TimePicker(context);
		TimePicker.setId(question.QuestionID + idTimePicker);
		//TimePicker.setIs24HourView(true);
		
		if (question.Answer.length()  >0)
		{
			String Date = question.Answer.split("@")[0];
			String Hour = question.Answer.split("@")[1];
			int year = Integer.parseInt(Date.split("/")[0]);
			int month = Integer.parseInt(Date.split("/")[1]);
			int dayOfMonth = Integer.parseInt(Date.split("/")[2]);
			int hour = Integer.parseInt(Hour.split(":")[0]);;
			int minutes = Integer.parseInt(Hour.split(":")[1]);;
			datePicker.updateDate(year, month, dayOfMonth);
			TimePicker.setCurrentHour(hour);
			TimePicker.setCurrentMinute(minutes);
		}
		if (focusflag==true)
		{
			datePicker.requestFocus();
		}
		if(isTablet(context)){
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			TimePicker.setLayoutParams(lp);
			relativeLayout.addView(datePicker);
	        relativeLayout.addView(TimePicker);
		}else{
			LinearLayout linLayout2 = new LinearLayout(context);
			linLayout2.setOrientation(LinearLayout.VERTICAL);
			linLayout2.addView(datePicker);
			linLayout2.addView(TimePicker);
			relativeLayout.addView(linLayout2);
		}
		
		if(question.DateTypeID==3)
		{
		        
		}
		else if(question.DateTypeID==2)
		{
			datePicker.setVisibility(View.GONE);
		}
		else
		{
			TimePicker.setVisibility(View.GONE);
		}
		
		linLayout.addView(relativeLayout);
		linLayout=AddFooter(linLayout,question,context);
        return linLayout;
	}
	
	//================================================================================
    // Create Signature Method
    //================================================================================
	
	public  View CreateSignatureControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
		Button  button = new Button(context);
		button.setId(question.QuestionID+idKey2);
		button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.green_button));
		button.setTextAppearance(context, R.style.button_text);
		button.setText("Ingresar Firma");
    	
		
		ImageView  imageView = new ImageView(context);
		imageView.setId(question.QuestionID+idKey);
		if (question.Answer.length()>0)
		{
		Bitmap bitmap = StringToBitMap( question.Answer);
		imageView.setImageBitmap(bitmap);
		}
		
		linLayout.addView(imageView);
		linLayout.addView(button);
		linLayout=AddFooter(linLayout,question,context);
		return linLayout;
	}
	
	//================================================================================
    // Create Image Method
    //================================================================================
	
	public  View CreateImageControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
	    LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
	    linearLayout.setLayoutParams(linLayoutParam);
	    linearLayout.setId(question.QuestionID+111);
		
		Button  button = new Button(context);
		button.setId(question.QuestionID+idKey2);
		button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.green_button));
		button.setTextAppearance(context, R.style.button_text);
		button.setText("Tomar La foto");
        
		
    	
		ImageView  imageView = new ImageView(context);
		imageView.setId(question.QuestionID+idKey);
		if (question.Answer.length()>0)
		{
			MySQLiteHelper db = new MySQLiteHelper(context);
			Bitmap bitmap = StringToBitMap(db.getPhoto(question.QuestionID));
			imageView.setImageBitmap(bitmap);
		}
		linearLayout.addView(imageView);
		linearLayout.addView(button);
		
		linLayout.addView(linearLayout);
		linLayout=AddFooter(linLayout,question,context);
        return linLayout;
	}
	
	//================================================================================
    // Create Bar Code Method
    //================================================================================
	
	public  View CreateBarcodeReaderControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
		Button  button = new Button(context);
		button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.green_button));
		button.setTextAppearance(context, R.style.button_text);
		button.setId(question.QuestionID+idKey2);
		button.setText("Escanear codigo");
    	TextView textView = new TextView(context);
    	textView.setId(question.QuestionID+idKey);
    	textView.setGravity(Gravity.CENTER);
    	textView.setHeight(30);
		if (question.Answer.length()>0)
		{
			textView.setText(question.Answer);
		}
		linLayout.addView(textView);
		linLayout.addView(button);
		if(question.ProcedureID > 0){
			HorizontalScrollView horizontalView = new HorizontalScrollView(context);
			TableLayout GridTable = new TableLayout(context);
    		TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
    		GridTable.setLayoutParams(tableParams);
    		GridTable.setColumnStretchable(0, true);
    		GridTable.setId(question.QuestionID+idKey3);
    		horizontalView.addView(GridTable);
			linLayout.addView(horizontalView);
		}
		linLayout=AddFooter(linLayout,question,context);
		return linLayout;
	}
	//================================================================================
    // Create Drop Down Method
    //================================================================================
	
	public  View CreateDropDownControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
		Spinner dropdown = new Spinner (context,Spinner.MODE_DIALOG);
		int id =question.QuestionID+idKey;
		dropdown.setId(id);
		dropdown.setPrompt("Selecciona Respuesta");
		dropdown.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_dropdown));
		String values=" ||@@||"+  question.Value;
		String[] Respuestas= values.split("\\|\\|@@\\|\\|");
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item , Respuestas);
		dropdown.setAdapter(spinnerArrayAdapter);
		dropdown.setFocusable(true);
		dropdown.setFocusableInTouchMode(true);
		if(focusflag==true)
		{
			dropdown.requestFocus();
		}
		else
		{
			dropdown.clearFocus();
		}
		if (question.Answer.length()>0){
	        int spinnerPostion = spinnerArrayAdapter.getPosition(question.Answer);
	        dropdown.setSelection(spinnerPostion);
	        spinnerPostion = 0;
	    }
		if(question.Blocked == 1){
			dropdown.setEnabled(false);
		}
		linLayout.addView(dropdown);
		linLayout=AddFooter(linLayout,question,context);
        return linLayout;
	}
	
	//================================================================================
    // Create Auto Complete Method
    //================================================================================
	
	public  View CreateAutoCompleteControl(Context context,Questions question,Boolean focusflag)
	{
        LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
	    AutoCompleteTextView AutoComplete = new AutoCompleteTextView(context);
	    AutoComplete.setSingleLine(true);
		int id =question.QuestionID+idKey;
		AutoComplete.setId(id);
		AutoComplete.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.edittext));
		AutoComplete.setText(question.Answer);
		String values=" ||@@||" +  question.Value;
		if(!question.CatalogElements.equals("")){
			values= " ||@@||" +  question.CatalogElements;
		}
		String[] Respuestas= values.split("\\|\\|@@\\|\\|");
		ArrayAdapter<String> AutoCompleteAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item , Respuestas);
		AutoComplete.setAdapter(AutoCompleteAdapter);
		if(focusflag==true)
		{
			AutoComplete.requestFocus();
		}
		else
		{
			AutoComplete.clearFocus();
		}
		linLayout.addView(AutoComplete);
		linLayout=AddFooter(linLayout,question,context);
        return linLayout;
	}
	
	//================================================================================
    // Create Canvas Method
    //================================================================================
	
	public  View CreateCanvasControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
		Button  button = new Button(context);
		button.setId(question.QuestionID+idKey2);
		button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.green_button));
		button.setTextAppearance(context, R.style.button_text);
		button.setText("Mostrar Imagen");
    	
		
		ImageView  imageView = new ImageView(context);
		imageView.setId(question.QuestionID+idKey);
		if (question.Answer.length()>0)
		{
			Bitmap bitmap = StringToBitMap(question.Answer);
			imageView.setImageBitmap(bitmap);
		}
		
		linLayout.addView(imageView);
		linLayout.addView(button);
		linLayout=AddFooter(linLayout,question,context);
		return linLayout;
	}
	
	//================================================================================
    // Create PhotoCanvas Method
    //================================================================================
	
	public  View CreatePhotoCanvasControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
	    LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
	    linearLayout.setLayoutParams(linLayoutParam);
	    linearLayout.setId(question.QuestionID+111);
		
		Button  button = new Button(context);
		button.setId(question.QuestionID+idKey2);
		button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.green_button));
		button.setTextAppearance(context, R.style.button_text);
		button.setText("Tomar Foto");
        
		ImageView  imageView = new ImageView(context);
		imageView.setId(question.QuestionID+idKey);
		if (question.Answer.length()>0)
		{
			MySQLiteHelper db = new MySQLiteHelper(context);
			Bitmap bitmap = StringToBitMap(db.getPhoto(question.QuestionID));
			imageView.setImageBitmap(bitmap);
		}
		linearLayout.addView(imageView);
		linearLayout.addView(button);
		
		linLayout.addView(linearLayout);
		linLayout=AddFooter(linLayout,question,context);
        return linLayout;
	}
	
	//================================================================================
    // Create Button Method
    //================================================================================
	
	public  View CreateButtonControl(Context context,Questions question,Boolean focusflag)
	{
		LinearLayout linLayout = new LinearLayout(context);
		linLayout.setBackgroundColor(Color.parseColor("#eaeaea"));
		linLayout=AddHeader(linLayout,question,context);
		linLayout.setId(idLinearLayout + (question.OrderNumber + 1));
	    if(question.Hidden)
	    {
	    	linLayout.setVisibility(View.GONE);
	    }
		Button button = new Button(context);
		button.setId(question.QuestionID+idKey2);
		button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.green_button));
		button.setTextColor(Color.parseColor("#ffffff"));
		button.setText(question.Question1);
		linLayout.addView(button);
		HorizontalScrollView horizontalView = new HorizontalScrollView(context);
		TableLayout GridTable = new TableLayout(context);
		TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
		GridTable.setLayoutParams(tableParams);
		GridTable.setColumnStretchable(0, true);
		GridTable.setId(question.QuestionID+idKey3);
		horizontalView.addView(GridTable);
		linLayout.addView(horizontalView);
		linLayout=AddFooter(linLayout,question,context);
        return linLayout;
	}
	
	//================================================================================
    // Helper Methods
    //================================================================================
	
	private LinearLayout AddFooter(LinearLayout linLayout, Questions question,
			Context context) {
		
		 LinearLayout.LayoutParams tdf = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		    tdf.setMargins(0, 0, 0, 15);

		 TextView txtDescription = new TextView(context);
		 txtDescription.setTextAppearance(context, R.style.textview_QuestionTitle);
	        txtDescription.setText(question.Instruction);
	        txtDescription.setGravity(Gravity.CENTER);
	        txtDescription.setLayoutParams(tdf);
		
	        
	        LinearLayout.LayoutParams tef = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		    tdf.setMargins(0, 0, 0, 15);
	        
	   	 TextView txtError = new TextView(context);
	   	txtError.setGravity(Gravity.CENTER);
	   	txtDescription.setTextAppearance(context, R.style.textview_Question);
	   	txtError.setId(question.QuestionID + idFooterLayout);
	   	txtError.setTextColor(Color.RED);
	   	txtError.setLayoutParams(tef);
	        
	     linLayout.addView(txtError);
	     linLayout.addView(txtDescription);
			
		
			View v = new View(context);
			v.setLayoutParams(new LinearLayout.LayoutParams(
			        LayoutParams.MATCH_PARENT,      
			        10
			));
			
			v.setBackgroundColor(Color.BLACK);
			linLayout.addView(v);
		
		return linLayout;
	}

	private LinearLayout AddHeader(LinearLayout linLayout,Questions question,Context context) {
		linLayout.setOrientation(LinearLayout.VERTICAL);
	    LinearLayout.LayoutParams  linLayoutParam = new LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
	    linLayoutParam.setMargins(0, 0, 0, 10);
	    linLayout.setLayoutParams(linLayoutParam);
		
	    LinearLayout.LayoutParams td = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    td.setMargins(0, 0, 0, 7);

	    TextView txtTitle =  new TextView(context);
        txtTitle.setText(question.Title);
        txtTitle.setGravity(Gravity.CENTER);
        txtTitle.setTextAppearance(context, R.style.textview_QuestionTitle);
        txtTitle.setTypeface(null, Typeface.BOLD);
        txtTitle.setLayoutParams(td);

        
		TextView txtQuestion = new TextView(context);
		txtQuestion.setTextAppearance(context, R.style.textview_Question);
		txtQuestion.setText(Html.fromHtml(question.Question1));
		txtQuestion.setMovementMethod(LinkMovementMethod.getInstance());
		txtQuestion.setTypeface(null, Typeface.ITALIC);
		txtQuestion.setId(question.QuestionID + idHeaderLayout);
		switch(TextColor){
			case 1:
				txtQuestion.setTextColor(Color.parseColor("#000000"));
				break;
			case 2:
				txtQuestion.setTextColor(Color.parseColor("#FF0000"));
				break;
			case 3:
				txtQuestion.setTextColor(Color.parseColor("#696969"));
				break;
			case 4:
				txtQuestion.setTextColor(Color.parseColor("#0000FF"));
				break;
			case 5:
				txtQuestion.setTextColor(Color.parseColor("#A9A9A9"));
				break;
			case 6:
				txtQuestion.setTextColor(Color.parseColor("#008000"));
				break;
			case 7: 
				txtQuestion.setTextColor(Color.parseColor("#FFFFFF"));
				break;
			case 8:
				txtQuestion.setTextColor(Color.parseColor("#FFFF00"));
				break;
			default:
				break;
		}
		switch(TextSize){
			case 1:
				txtQuestion.setTextSize(10);
				break;
			case 2:
				txtQuestion.setTextSize(12);
				break;
			case 3:
				txtQuestion.setTextSize(14);
				break;
			case 4:
				txtQuestion.setTextSize(19);
				break;
			case 5:
				txtQuestion.setTextSize(22);
				break;
			default:
				break;
		}
		
		linLayout.addView(txtTitle);
		linLayout.addView(txtQuestion);
		
		return linLayout;
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
    // Section Method
    //================================================================================

	public View CreateSectionControls(Context context,List<List<Questions>> surveyQuestions) {
		  ScrollView scroll = new ScrollView(context);
			scroll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			LinearLayout linLayout = new LinearLayout(context);
		    linLayout.setOrientation(LinearLayout.VERTICAL);
		    LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
		    linLayout.setLayoutParams(linLayoutParam);
		    int index=0;
			for(List<Questions> question: surveyQuestions)
			{
				linLayout.addView(CreateSections(context,question,index));
				index++;
			}
			linLayout.addView(AddFooterSpace(context));
			scroll.addView(linLayout);
			return scroll;
	}
	
	public  View CreateSections(Context context,List<Questions> question,int index)
	{
		View control;
		control=CreateButtonSeccion(context,question,index);
		return control;
	}
	
	public  View CreateButtonSeccion(Context context,List<Questions> question,int index)
	{
        LinearLayout linLayout = new LinearLayout(context);
		Button button = new Button(context);
		button.setId(index);
		button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.green_button));
		button.setTextAppearance(context, R.style.button_text);
		button.setText(question.get(0).SectionName);
		LinearLayout.LayoutParams tdf = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tdf.setMargins(0, 10, 0, 10);
		button.setLayoutParams(tdf);
		linLayout.addView(button);
        return linLayout;
        
	}
	
	//================================================================================
    // Check Device type
	//================================================================================
	
	public boolean isTablet(Context context) {
	    boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
	    boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    return (xlarge || large);
	}
	
}