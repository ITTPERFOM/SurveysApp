package com.timetracker.sqlite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.timetracker.data.Answers;
import com.timetracker.data.Biometrics;
import com.timetracker.data.Devices;
import com.timetracker.data.QuestionOptions;
import com.timetracker.data.QuestionSentences;
import com.timetracker.data.Questions;
import com.timetracker.data.SelectedSurvey;
import com.timetracker.data.Surveys;
import com.timetracker.data.Tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
 
public class MySQLiteHelper extends SQLiteOpenHelper {
 
	//================================================================================
    // Global Variables
    //================================================================================
	
    private static final int DATABASE_VERSION = 47;
    private static final String DATABASE_NAME = "SurveysDB";
 
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }
    
    //================================================================================
    // Create Tables
    //================================================================================
    @Override
    public void onCreate(SQLiteDatabase db) {
    	String CREATE_DEVICE_TABLE = "CREATE TABLE Devices ( " +
			"DeviceID INTEGER PRIMARY KEY , " + 
            "Name TEXT, "+
            "Code TEXT, "+
            "DeviceTypeID TEXT, "+
            "Status INTEGER, " +
            "UsesFormSelection INTEGER, " +
            "UsesFormWithUbicheck INTEGER, " +
            "UsesClientValidation INTEGER, " +
            "UsesCreateBranch INTEGER, " +
            "UsesUbicheckDetails INTEGER, " +
            "UsesBiometric INTEGER, " +
            "UsesKioskMode INTEGER, " +
            "KioskBranchID INTEGER, " +
    		"ImageWareRegister INTEGER, " +
    		"BiometricID INTEGER, " +
    		"Account TEXT)";

		String CREATE_ActualUbicheck= "CREATE TABLE ActualUbicheck ( " +
				"UbicheckID INTEGER )";
    	
    	String CREATE_TRACKERS_TABLE = "CREATE TABLE trackers ( " +
			"TrackerID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
            "DeviceID INTEGER, "+
            "Latitude REAL, "+
            "Longitude REAL, "+
            "Date TEXT)";
    	
	   String CREATE_ANSWERS_TABLE = "CREATE TABLE Answers ( " +
		   "AnswerID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
		   "QuestionID INTEGER, " + 
	       "Value TEXT, "+
	       "QuestionTypeID INTEGER, "+
	       "Latitude REAL, "+
	       "Longitude REAL, "+
	       "DeviceMac TEXT,"+
	       "Identifier TEXT, " +
   		   "DateFormStart TEXT, " +
		   "DateFormFinish TEXT," +
	   	   "UbicheckID INTEGER )";
	   	   
		String CREATE_SURVEYS_TABLE = "CREATE TABLE surveys ( " +
		        "SurveyID INTEGER PRIMARY KEY , " + 
		        "SurveyName TEXT, "+
		        "ClientID INTEGER, "+
		        "ClientName TEXT, "+
		        "TransactionNumber TEXT, "+
		        "StatusID INTEGER, "+
		        "TextSize TEXT, "+
		        "TextColor TEXT, "+
		        "IntroductionText TEXT, "+
		        "IntroductionImage TEXT, "+
		        "EndingText TEXT, "+
		        "EndingImage TEXT, "+
				"FooterImage TEXT," +
				"BackgroundImage TEXT," +
				"ProcedureID int)";
        
        String CREATE_QUESTIONS_TABLE = "CREATE TABLE questions ( " +
                "QuestionID INTEGER PRIMARY KEY , " + 
                "SurveyID INTEGER, "+
                "QuestionTypeID INTEGER, "+
                "SectionID INTEGER, "+
                "SectionName TEXT, "+
                "Title TEXT, "+
                "Text TEXT, "+
                "Value TEXT, "+
                "Comment TEXT, "+
                "OrderNumber INTEGER, "+
                "Question1 TEXT, "+
                "Instruction TEXT, "+
                "ShortName TEXT, "+
                "Minimum INTEGER, "+
                "Maximum INTEGER, "+
                "Required INTEGER, "+
                "Decimals INTEGER, "+
				"Preffix TEXT, "+
				"Suffix TEXT, "+
				"Randomize INTEGER, "+
				"IncludeScoring INTEGER, "+
				"DisplayImages INTEGER, "+
				"MinAnswers INTEGER, "+
				"MaxAnswers INTEGER, "+
				"LeftLabel TEXT, "+
				"RightLabel TEXT, "+
				"ImageAboveText INTEGER, "+
				"DefaultDate TEXT, "+
				"DateTypeID INTEGER, "+
				"DateTypeName TEXT, "+
                "CatalogID INTEGER, "+
                "CatalogElements TEXT, "+
                "Condition TEXT, "+
                "Valu TEXT, "+
                "SendTo TEXT, "+
                "Image TEXT, "+
                "Options TEXT, "+
                "OtherOption TEXT, "+
                "Hidden INTEGER, "+
                "Answer TEXT, " +
                "ProcedureID int," +
        		"Blocked int)";
        
        String CREATE_QUESTIONOPTIONS_TABLE = "CREATE TABLE questionOptions ( " +
		        "QuestionOptionID INTEGER PRIMARY KEY , " + 
		        "QuestionID INTEGER, "+
		        "Name TEXT, "+
		        "Score REAL, "+
		        "Image TEXT, "+
		        "Condition TEXT, "+
		        "Value TEXT, "+
		        "SendTo TEXT,"+
		        "IsText INTEGER, "+ 
		        "Type TEXT)";
 
        String CREATE_QUESTIONSENTENCES_TABLE = "CREATE TABLE questionSentences ( " +
		        "QuestionSentenceID INTEGER PRIMARY KEY , " + 
		        "QuestionID INTEGER, "+
		        "Name TEXT )";
        
        String CREATE_PHOTO_TABLE = "CREATE TABLE photos ( " +
		        "PhotoID INTEGER PRIMARY KEY NOT NULL , " + 
		        "Photo TEXT )";
        
        String CREATE_SELECTED_SURVEY_TABLE = "CREATE TABLE selectedSurveys ( " +
		        "SurveyID INTEGER PRIMARY KEY NOT NULL , " + 
		        "SurveyName TEXT, " +
		        "DateFormStart TEXT, " +
		        "UbicheckID INTEGER )";
        
        String CREATE_BIOMETRICS_TABLE = "CREATE TABLE biometrics ( " +
        		"BiometricID INTEGER PRIMARY KEY , " +
                "Name TEXT, " +
        		"LastConecction INTEGER)";

        String CREATE_DataUsed_TABLE = ("CREATE TABLE DataUsed ( " +
                "LastMonth INTEGER, "+
                "Data INTEGER)");
        
        try
        {
        	db.execSQL(CREATE_DEVICE_TABLE);
        	db.execSQL(CREATE_ANSWERS_TABLE);
        	db.execSQL(CREATE_SURVEYS_TABLE);
        	db.execSQL(CREATE_QUESTIONS_TABLE);
        	db.execSQL(CREATE_QUESTIONOPTIONS_TABLE);
        	db.execSQL(CREATE_QUESTIONSENTENCES_TABLE);
        	db.execSQL(CREATE_PHOTO_TABLE);
        	db.execSQL(CREATE_SELECTED_SURVEY_TABLE);
        	db.execSQL(CREATE_TRACKERS_TABLE);
            db.execSQL(CREATE_BIOMETRICS_TABLE);
            db.execSQL(CREATE_DataUsed_TABLE);
            db.execSQL(CREATE_ActualUbicheck);
        }
        catch(Exception e)
        {
        }
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    	if(oldVersion < 46){
			db.execSQL("CREATE TABLE IF NOT EXISTS ActualUbicheck ( " +
					"UbicheckID INTEGER )");

			db.execSQL("CREATE TABLE IF NOT EXISTS DataUsed ( " +
					"LastMonth INTEGER, " +
					"Data INTEGER)");
		}

    	if(oldVersion == 45){
			db.execSQL("CREATE TABLE IF NOT EXISTS DataUsed ( " +
					"LastMonth INTEGER, " +
					"Data INTEGER)");
		}
    	if(oldVersion < 24){
    		db.execSQL("DROP TABLE IF EXISTS Devices");
        	db.execSQL("DROP TABLE IF EXISTS surveys");
        	db.execSQL("DROP TABLE IF EXISTS questions");
        	db.execSQL("DROP TABLE IF EXISTS Answers");
        	db.execSQL("DROP TABLE IF EXISTS questionOptions");
        	db.execSQL("DROP TABLE IF EXISTS questionSentences");
        	db.execSQL("DROP TABLE IF EXISTS photos");
        	this.onCreate(db);
    	}
    	if(oldVersion == 25){
    		db.execSQL("ALTER TABLE questionOptions ADD COLUMN IsText INTEGER;");
    		db.execSQL("UPDATE questionOptions SET IsText=0;");
    		db.execSQL("CREATE TABLE photos ( " +
    		        "PhotoID INTEGER PRIMARY KEY NOT NULL , " + 
    		        "Photo TEXT )");
    		db.execSQL("ALTER TABLE surveys ADD COLUMN BackgroundImage TEXT;");
    		db.execSQL("UPDATE surveys SET BackgroundImage='';");
    	}
		if(oldVersion < 31){
			db.execSQL("CREATE TABLE selectedSurveys ( " +
			        "SurveyID INTEGER PRIMARY KEY NOT NULL , " + 
			        "SurveyName TEXT )");
		}
		if(oldVersion < 44){
			db.execSQL("CREATE TABLE biometrics ( " +
        		"BiometricID INTEGER PRIMARY KEY , " +
                "Name TEXT)");
		}
        String query = "SELECT * FROM SURVEYS LIMIT 0,1" ;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getColumnIndex("BackgroundImage") == -1){
        	db.execSQL("ALTER TABLE surveys ADD COLUMN BackgroundImage TEXT;");
    		db.execSQL("UPDATE surveys SET BackgroundImage='';");
        }
        if(cursor.getColumnIndex("ProcedureID") == -1){
        	db.execSQL("ALTER TABLE surveys ADD COLUMN ProcedureID int;");
    		db.execSQL("UPDATE surveys SET ProcedureID=0;");
        }
        query = "SELECT * FROM QUESTIONS LIMIT 0,1" ;
        cursor = db.rawQuery(query, null);
        if(cursor.getColumnIndex("ProcedureID") == -1){
        	db.execSQL("ALTER TABLE questions ADD COLUMN ProcedureID int;");
    		db.execSQL("UPDATE questions SET ProcedureID=0;");
        }
        if(cursor.getColumnIndex("Blocked") == -1){
        	db.execSQL("ALTER TABLE questions ADD COLUMN Blocked int;");
    		db.execSQL("UPDATE questions SET Blocked=0;");
        }
        if(oldVersion < 33){
			db.execSQL("CREATE TABLE trackers ( " +
					"TrackerID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
	                "DeviceID INTEGER, "+
	                "Latitude REAL, "+
	                "Longitude REAL, "+
	                "Date TEXT )");
		}
        query = "SELECT * FROM Answers LIMIT 0,1" ;
        cursor = db.rawQuery(query, null);
        if(cursor.getColumnIndex("DateFormStart") == -1){
        	db.execSQL("ALTER TABLE Answers ADD COLUMN DateFormStart TEXT;");
        	db.execSQL("UPDATE Answers SET DateFormStart='';");
        	db.execSQL("ALTER TABLE Answers ADD COLUMN DateFormFinish TEXT;");
        	db.execSQL("UPDATE Answers SET DateFormFinish='';");
        }
        if(cursor.getColumnIndex("UbicheckID") == -1){
        	db.execSQL("ALTER TABLE Answers ADD COLUMN UbicheckID int;");
        	db.execSQL("UPDATE Answers SET UbicheckID=0;");
        }
        query = "SELECT * FROM selectedSurveys LIMIT 0,1" ;
        cursor = db.rawQuery(query, null);
        if(cursor.getColumnIndex("DateFormStart") == -1){
        	db.execSQL("ALTER TABLE selectedSurveys ADD COLUMN DateFormStart TEXT;");
        	db.execSQL("UPDATE selectedSurveys SET DateFormStart='';");
        }
        if(cursor.getColumnIndex("UbicheckID") == -1){
        	db.execSQL("ALTER TABLE selectedSurveys ADD COLUMN UbicheckID int;");
        	db.execSQL("UPDATE selectedSurveys SET UbicheckID=0;");
        }
        query = "SELECT * FROM Devices LIMIT 0,1" ;
        cursor = db.rawQuery(query, null);
        if(cursor.getColumnIndex("UsesFormSelection") == -1){
        	db.execSQL("ALTER TABLE Devices ADD COLUMN UsesFormSelection int;");
    		db.execSQL("UPDATE Devices SET UsesFormSelection=1;");
        }
        if(cursor.getColumnIndex("UsesFormWithUbicheck") == -1){
        	db.execSQL("ALTER TABLE Devices ADD COLUMN UsesFormWithUbicheck int;");
    		db.execSQL("UPDATE Devices SET UsesFormWithUbicheck=0;");
        }
        if(cursor.getColumnIndex("UsesClientValidation") == -1){
        	db.execSQL("ALTER TABLE Devices ADD COLUMN UsesClientValidation int;");
    		db.execSQL("UPDATE Devices SET UsesClientValidation=0;");
        }
        if(cursor.getColumnIndex("UsesCreateBranch") == -1){
        	db.execSQL("ALTER TABLE Devices ADD COLUMN UsesCreateBranch int;");
    		db.execSQL("UPDATE Devices SET UsesCreateBranch=0;");
        }
        if(cursor.getColumnIndex("UsesUbicheckDetails") == -1){
        	db.execSQL("ALTER TABLE Devices ADD COLUMN UsesUbicheckDetails int;");
    		db.execSQL("UPDATE Devices SET UsesUbicheckDetails=0;");
        }
        if(cursor.getColumnIndex("ImageWareRegister") == -1){
        	db.execSQL("ALTER TABLE Devices ADD COLUMN ImageWareRegister int;");
    		db.execSQL("UPDATE Devices SET ImageWareRegister=0;");
        }
        if(cursor.getColumnIndex("BiometricID") == -1){
        	db.execSQL("ALTER TABLE Devices ADD COLUMN BiometricID int;");
    		db.execSQL("UPDATE Devices SET BiometricID=0;");
        }
        if(cursor.getColumnIndex("UsesBiometric") == -1){
        	db.execSQL("ALTER TABLE Devices ADD COLUMN UsesBiometric int;");
    		db.execSQL("UPDATE Devices SET UsesBiometric=0;");
        }
        if(cursor.getColumnIndex("UsesKioskMode") == -1){
        	db.execSQL("ALTER TABLE Devices ADD COLUMN UsesKioskMode int;");
    		db.execSQL("UPDATE Devices SET UsesKioskMode=0;");
        }
        if(cursor.getColumnIndex("KioskBranchID") == -1){
        	db.execSQL("ALTER TABLE Devices ADD COLUMN KioskBranchID int;");
    		db.execSQL("UPDATE Devices SET KioskBranchID=0;");
        }
        if(cursor.getColumnIndex("Account") == -1){
        	db.execSQL("ALTER TABLE Devices ADD COLUMN Account TEXT;");
    		db.execSQL("UPDATE Devices SET Account='';");
        }
        query = "SELECT * FROM questionOptions LIMIT 0,1" ;
        cursor = db.rawQuery(query, null);
        if(cursor.getColumnIndex("Type") == -1){
        	db.execSQL("ALTER TABLE questionOptions ADD COLUMN Type TEXT;");
    		db.execSQL("UPDATE questionOptions SET Type='';");
        }
        query = "SELECT * FROM biometrics LIMIT 0,1" ;
        cursor = db.rawQuery(query, null);
        if(cursor.getColumnIndex("LastConecction") == -1){
        	db.execSQL("ALTER TABLE biometrics ADD COLUMN LastConecction int;");
    		db.execSQL("UPDATE biometrics SET LastConecction=0;");
        }
        if(cursor != null){
        	cursor.close();
        }  
    }
    
    //================================================================================
    // Device Methods
    //================================================================================
    
    public Devices GetDevice() {
        String query = "SELECT Name,Code,DeviceTypeID,DeviceID,Status,UsesFormSelection,UsesFormWithUbicheck,UsesClientValidation,UsesCreateBranch,UsesUbicheckDetails,UsesBiometric,UsesKioskMode,KioskBranchID,ImageWareRegister,BiometricID,Account FROM Devices";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Devices Device = null;
        if (cursor.moveToFirst()) {
            do {
            	Device = new Devices(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getInt(7),cursor.getInt(8),cursor.getInt(9),cursor.getInt(10),cursor.getInt(11),cursor.getInt(12),cursor.getInt(13),cursor.getInt(14),cursor.getString(15));
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }  
        db.close();
        return Device;
    }
    
    public int updateDevice(Devices Device) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", Device.Name);
		values.put("Code", Device.Code);
		values.put("DeviceID", Device.DeviceID);
		values.put("Status", Device.Status);
		values.put("DeviceTypeID", Device.DeviceTypeID);
		values.put("UsesFormSelection", Device.UsesFormSelection);
		values.put("UsesFormWithUbicheck", Device.UsesFormWithUbicheck);
		values.put("UsesClientValidation", Device.UsesClientValidation);
		values.put("UsesCreateBranch", Device.UsesCreateBranch);
		values.put("UsesUbicheckDetails", Device.UsesUbicheckDetails);
		values.put("UsesBiometric", Device.UsesBiometric);
		values.put("UsesKioskMode", Device.UsesKioskMode);
		values.put("KioskBranchID", Device.KioskBranchID);
		values.put("ImageWareRegister", Device.ImageWareRegister);
		values.put("BiometricID", Device.BiometricID);
		values.put("Account", Device.Account);
        int i = db.update("Devices", values, "DeviceID"+" = ?",new String[] { String.valueOf(Device.DeviceID) });
        db.close();
        return i;
    }
    
    public void addDevice(Devices Device){	
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("Name", Device.Name);
		values.put("Code", Device.Code);
		values.put("DeviceID", Device.DeviceID);
		values.put("Status", Device.Status);
		values.put("DeviceTypeID", Device.DeviceTypeID);
		values.put("UsesFormSelection", Device.UsesFormSelection);
		values.put("UsesFormWithUbicheck", Device.UsesFormWithUbicheck);
		values.put("UsesClientValidation", Device.UsesClientValidation);
		values.put("UsesCreateBranch", Device.UsesCreateBranch);
		values.put("UsesUbicheckDetails", Device.UsesUbicheckDetails);
		values.put("UsesBiometric", Device.UsesBiometric);
		values.put("UsesKioskMode", Device.UsesKioskMode);
		values.put("KioskBranchID", Device.KioskBranchID);
		values.put("ImageWareRegister", Device.ImageWareRegister);
		values.put("BiometricID", Device.BiometricID);
		values.put("Account", Device.Account);
		db.insert("Devices",null,values);
		db.close(); 
	}
    
    public void deleteDevice() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Devices");
        db.close();
    }
    
    public int updateDeviceImageWare() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
		values.put("ImageWareRegister", "1");
        int i = db.update("Devices", values, "DeviceID"+" != ?", new String[] { String.valueOf(0) });
        db.close();
        return i;
    }
    //================================================================================
    // Data Used Methods
    //================================================================================
    public int  LastMonth() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("CREATE TABLE IF NOT EXISTS DataUsed ( " +
				"LastMonth INTEGER, "+
				"Data INTEGER)");
        String query = "SELECT LastMonth FROM DataUsed ";
        Cursor cursor = db.rawQuery(query, null);
        int lastMonth = 0;
        try {
            if (cursor.moveToFirst()) {
                if(cursor.getString(0)!=null)
                {
                    lastMonth =  Integer.parseInt(cursor.getString(0));
                }
            }
            if(cursor != null){
                cursor.close();
            }
            db.close();
            return lastMonth;
        }catch (Exception e){
            return 0;
        }
    }
	public void InitTableDataUsage(){
		SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS DataUsed ( " +
                "LastMonth INTEGER, "+
                "Data INTEGER)");
	}
	/*////////////////////) ANSWERS WITHOUT UBICHECK METHODS (///////////////////////////////*/

	public boolean GetFormSettings() {
		boolean Settings = false;
		String query = "SELECT UsesFormWithUbicheck FROM Devices ";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		int UsesFormWithUbicheck = 0;
		try {
			if (cursor.moveToFirst()) {
				if(cursor.getString(0)!=null)
				{
					UsesFormWithUbicheck =  Integer.parseInt(cursor.getString(0));
				}
			}
			if(cursor != null){
				cursor.close();
			}
		}catch (Exception e){

		}

		if(UsesFormWithUbicheck != 0){
			Settings = true;
		}
		return Settings;
	}

	public int GetUbicheckID(){
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT ubicheckID FROM selectedSurveys ";
		Cursor cursor = db.rawQuery(query, null);
		int UbicheckID = 0;
		try {
			if (cursor.moveToFirst()) {
				if(cursor.getString(0)!=null)
				{
					UbicheckID =  Integer.parseInt(cursor.getString(0));
				}
			}
			if(cursor != null){
				cursor.close();
			}

			if(UbicheckID == 0){
				UbicheckID = GetUbicheckIDFromActualUbicheck();
			}
			return UbicheckID;
		}catch (Exception e){
			return 0;
		}
	}

	public boolean CheckUbicheckID(){
		if(GetUbicheckID() != 0){
			return true;
		}else {
			return false;
		}
	};

	public int GetUbicheckIDFromActualUbicheck(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("CREATE TABLE IF NOT EXISTS ActualUbicheck ( " +
				"UbicheckID INTEGER )");
		String query = "SELECT ubicheckID FROM ActualUbicheck ";
		Cursor cursor = db.rawQuery(query, null);
		int UbicheckID = 0;
		try {
			if (cursor.moveToFirst()) {
				if(cursor.getString(0)!=null)
				{
					UbicheckID =  Integer.parseInt(cursor.getString(0));
				}
			}
			if(cursor != null){
				cursor.close();
			}
			return UbicheckID;
		}catch (Exception e){
			return 0;
		}
	}
	public void AppendUbicheckID(int UbicheckID) {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "DELETE FROM ActualUbicheck";
		db.execSQL(query);
			String sql = "insert into ActualUbicheck(UbicheckID) "
					+ "values(?)";
			Object[] args = new Object[]{UbicheckID};
			db.execSQL(sql, args);
	}
	public void DeleteUbicheckIDFromActualUbicheck(){
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "DELETE FROM ActualUbicheck";
		db.execSQL(query);
	}

	public void AddUbicheckIDToSurveys( int UbicheckID) {
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "UPDATE selectedSurveys" +
				"SET UbicheckID = " + UbicheckID +
				"WHERE UbicheckID";
		db.execSQL(sql);
		db.close();
	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////

	public int  GetDataUsage() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("CREATE TABLE IF NOT EXISTS DataUsed ( " +
				"LastMonth INTEGER, "+
				"Data INTEGER)");
        String query = "SELECT Data FROM DataUsed ";
        Cursor cursor = db.rawQuery(query, null);
        int data = 0;
        try {
            if (cursor.moveToFirst()) {
                if(cursor.getString(0)!=null)
                {
                    data =  Integer.parseInt(cursor.getString(0));
                }
            }
            if(cursor != null){
                cursor.close();
            }
            db.close();
            return data;
        }catch (Exception e){
            return 0;
        }
    }

    public  void EraseDatausageTable(){
        String query = "DELETE FROM DataUsed ";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    public void  Data(int data) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("CREATE TABLE IF NOT EXISTS DataUsed ( " +
				"LastMonth INTEGER, "+
				"Data INTEGER)");
        int trueData = GetDataUsage() + data;
        EraseDatausageTable();
		String query = "insert into DataUsed(Data,LastMonth) "
				+ "values(?,?)";
		int currentdate = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
		Object[] args = new Object[]{trueData,currentdate};
        db.execSQL(query);
    }
    public void insertCurrentDate(Date date){
        SQLiteDatabase db = this.getWritableDatabase();
        int currentdate = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        String sql = "insert into DataUsed(Data,LastMonth) "
                + "values(?,?)";
        Object[] args = new Object[]{0,currentdate};
        db.execSQL(sql, args);
    }
    //================================================================================
    // Answers Methods
    //================================================================================
    
    public int  getAnswersQty() {
        String query = "SELECT count(*) FROM ANSWERS";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int qty = 0;
        if (cursor.moveToFirst()) {
            do {
            	if(cursor.getString(0)!=null)
            	{
            		qty =  Integer.parseInt(cursor.getString(0));
            	}
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }  
        db.close();
        return qty;
    }
    
    public void addAnswers(Answers Answer){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("QuestionID", Answer.QuestionID);
		values.put("Value", Answer.Value);
		values.put("QuestionTypeID", Answer.QuestionTypeID);
		values.put("Latitude", Answer.Latitude);
		values.put("Longitude", Answer.Longitude);
		values.put("DeviceMac", Answer.DeviceMac);
		values.put("Identifier", Answer.Identifier);
		values.put("DateFormStart", Answer.DateFormStart);
		values.put("DateFormFinish", Answer.DateFormFinish);
		values.put("UbicheckID", Answer.Ubicheck);
		db.insert("Answers", null, values); 
		db.close(); 
	}
    
    public void deleteAnswers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Answers");
        db.close();
    }
    
    public List<Answers> getAnswers() {
        List<Answers> answers = new LinkedList<Answers>();
        String query = "SELECT * FROM ANSWERS" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Answers answer = null;
        if (cursor.moveToFirst()) {
            do {
            	String lat = cursor.getString(4);
            	String lon = cursor.getString(5);
            	String mac = cursor.getString(6);
            	if(lat == null){
            		lat = "0";
            	}
            	if(lon == null){
            		lon = "0";
            	}
            	if(mac == null){
            		mac = "";
            	}
            	answer = new Answers(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),cursor.getString(2),Integer.parseInt(cursor.getString(3)),Double.parseDouble(lat),Double.parseDouble(lon),mac,cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getInt(10));
                answers.add(answer);
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }  
        db.close();
        return answers;
    }
    
    public List<Answers> getAnswersByIdentifier(String Identifier) {
        List<Answers> answers = new LinkedList<Answers>();
        String query = "SELECT * FROM ANSWERS WHERE Identifier='" + Identifier + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Answers answer = null;
        if (cursor.moveToFirst()) {
            do {
            	String lat = cursor.getString(4);
            	String lon = cursor.getString(5);
            	String mac = cursor.getString(6);
            	if(lat == null){
            		lat = "0";
            	}
            	if(lon == null){
            		lon = "0";
            	}
            	if(mac == null){
            		mac = "";
            	}
            	answer = new Answers(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),cursor.getString(2),Integer.parseInt(cursor.getString(3)),Double.parseDouble(lat),Double.parseDouble(lon),mac,cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getInt(10));
                answers.add(answer);
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }  
        db.close();
        return answers;
    }
    
    public int getDistincAnswersQty() {
        String query = "SELECT count(DISTINCT Identifier) FROM ANSWERS";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int qty = 0;
        if (cursor.moveToFirst()) {
            do {
            	if(cursor.getString(0)!=null)
            	{
            		qty =  Integer.parseInt(cursor.getString(0));
            	}
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }  
        db.close();
        return qty;
    }
    
    public List<String> getDistincAnswers() {
    	List<String> Result = new ArrayList<String>();
        String query = "SELECT DISTINCT Identifier FROM ANSWERS LIMIT 5";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
            	if(cursor.getString(0)!=null)
            	{
            		Result.add(cursor.getString(0));
            	}
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return Result;
    }
    
    public List<Integer> getAnswersWithImages() {
    	List<Integer> Result = new ArrayList<Integer>();
        String query = "SELECT AnswerID FROM ANSWERS WHERE (QuestionTypeID = 15 OR QuestionTypeID = 20) AND Value NOT LIKE '%.jpg' AND Value != ''";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
            	if(cursor.getString(0)!=null)
            	{
            		Result.add(cursor.getInt(0));
            	}
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return Result;
    }
    
    public Answers getAnswerByAnswerID(int AnswerID) {
        String query = "SELECT * FROM ANSWERS WHERE AnswerID=" + AnswerID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Answers answer = null;
        if (cursor.moveToFirst()) {
            do {
            	String lat = cursor.getString(4);
            	String lon = cursor.getString(5);
            	String mac = cursor.getString(6);
            	if(lat == null){
            		lat = "0";
            	}
            	if(lon == null){
            		lon = "0";
            	}
            	if(mac == null){
            		mac = "";
            	}
            	answer = new Answers(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),cursor.getString(2),Integer.parseInt(cursor.getString(3)),Double.parseDouble(lat),Double.parseDouble(lon),mac,cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getInt(10));
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }  
        db.close();
        return answer;
    }
    
    public void UpdateAnswers(Answers Answer){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("QuestionID", Answer.QuestionID);
		values.put("Value", Answer.Value);
		values.put("QuestionTypeID", Answer.QuestionTypeID);
		values.put("Latitude", Answer.Latitude);
		values.put("Longitude", Answer.Longitude);
		values.put("DeviceMac", Answer.DeviceMac);
		values.put("Identifier", Answer.Identifier);
		values.put("DateFormStart", Answer.DateFormStart);
		values.put("DateFormFinish", Answer.DateFormFinish);
		values.put("UbicheckID", Answer.Ubicheck);
		db.update("Answers", values, "AnswerID"+" = ?",new String[] { String.valueOf(Answer.AnswerID) }); 
		db.close(); 
	}
    
    public void deleteAnswersByIdentifier(String Identifier) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Answers WHERE Identifier='" + Identifier + "'");
        db.close();
    }
    
    
    //================================================================================
    // Trackers Methods
    //================================================================================
    
    public int  getTrackersQty() {
        String query = "SELECT count(*) FROM TRACKERS";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int qty = 0;
        if (cursor.moveToFirst()) {
            do {
            	if(cursor.getString(0)!=null)
            	{
            		qty =  Integer.parseInt(cursor.getString(0));
            	}
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return qty;
    }
    
    public int addTrackers(Tracker Tracker){
    	int newId = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("DeviceID", Tracker.DeviceID);
		values.put("Latitude", Tracker.Latitude);
		values.put("Longitude", Tracker.Longitude);
		values.put("Date", Tracker.Date);
		newId = (int)db.insert("Trackers", null, values); 
		db.close(); 
		return newId;
	}
    
    public int getDistincTrackersQty() {
        String query = "SELECT count(DISTINCT TrackerID) FROM TRACKERS";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int qty = 0;
        if (cursor.moveToFirst()) {
            do {
            	if(cursor.getString(0)!=null)
            	{
            		qty =  Integer.parseInt(cursor.getString(0));
            	}
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return qty;
    }
    
    public Tracker GetTracker() {
        String query = "SELECT TrackerID,DeviceID,Latitude,Longitude,Date FROM Trackers";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Tracker Tracker = null;
        if (cursor.moveToFirst()) {
            do {
            	Tracker = new Tracker(cursor.getInt(0),cursor.getInt(1),cursor.getDouble(2),cursor.getDouble(3),cursor.getString(4));
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return Tracker;
    }
    
    public void deleteTrackersByID(int TrackerID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Trackers WHERE TrackerID=" + TrackerID);
        db.close();
    }
    
    //================================================================================
    // Survey Methods
    //================================================================================
    
    public void addSurvey(Surveys survey){	
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("SurveyID", survey.SurveyID);
		values.put("SurveyName", survey.SurveyName);
		values.put("ClientID", survey.ClientID);
		values.put("ClientName", survey.ClientName); 
		values.put("TransactionNumber", survey.TransactionNumber);
		values.put("StatusID", survey.StatusID);
		values.put("TextSize", survey.TextSize);
		values.put("TextColor", survey.TextColor);
		values.put("IntroductionText", survey.IntroductionText);
		values.put("IntroductionImage", survey.IntroductionImage);
		values.put("EndingText", survey.EndingText);
		values.put("EndingImage", survey.EndingImage);
		values.put("FooterImage", survey.FooterImage);
		values.put("BackgroundImage", survey.BackgroundImage);
		values.put("ProcedureID", survey.ProcedureID);
		db.insert("surveys", null, values);
		db.close(); 
	}
		     
    public List<Surveys> getAllsurveys(String DistributionChannel) {
        List<Surveys> surveys = new LinkedList<Surveys>();
        String condition = "";
        if(!DistributionChannel.equals("")){
        	condition = " WHERE TransactionNumber = '" + DistributionChannel + "'";
        }
        String query = "SELECT SurveyID,SurveyName,ClientID,ClientName,TransactionNumber,StatusID,TextSize,TextColor,IntroductionText,EndingText,ProcedureID FROM SURVEYS" + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Surveys survey = null;
        if (cursor.moveToFirst()) {
            do {
            	survey = new Surveys(Integer.parseInt(cursor.getString(0)),cursor.getString(1),Integer.parseInt(cursor.getString(2)),cursor.getString(3),cursor.getString(4),Integer.parseInt(cursor.getString(5)),cursor.getString(6),cursor.getString(7),cursor.getString(8),"",cursor.getString(9),"","","",Integer.parseInt(cursor.getString(10)));
            	surveys.add(survey);
            } while (cursor.moveToNext());
        }
        
        for(Surveys s: surveys)
		{
        	query = "SELECT IntroductionImage FROM SURVEYS WHERE SurveyID="+s.SurveyID;
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                	s.IntroductionImage = cursor.getString(0);
                	break;
                } while (cursor.moveToNext());
            }
            query = "SELECT EndingImage FROM SURVEYS WHERE SurveyID="+s.SurveyID;
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                	s.EndingImage = cursor.getString(0);
                	break;
                } while (cursor.moveToNext());
            }
            query = "SELECT FooterImage FROM SURVEYS WHERE SurveyID="+s.SurveyID;
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                	s.FooterImage = cursor.getString(0);
                	break;
                } while (cursor.moveToNext());
            }
            query = "SELECT BackgroundImage FROM SURVEYS WHERE SurveyID="+s.SurveyID;
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                	s.BackgroundImage = cursor.getString(0);
                	break;
                } while (cursor.moveToNext());
            }
		}
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return surveys;
    }

    public int  getSurveysQty() {
        String query = "SELECT count(*) FROM SURVEYS" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int qty = 0;
        if (cursor.moveToFirst()) {
            do {
            	if(cursor.getString(0)!=null)
            	{
            		qty =  Integer.parseInt(cursor.getString(0));
            	}
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return qty;
    }
		     
    public int updateSurvey(Surveys survey) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
		values.put("SurveyID", survey.SurveyID);
		values.put("SurveyName", survey.SurveyName);
		values.put("ClientID", survey.ClientID); // 
		values.put("ClientName", survey.ClientName); 
		values.put("TransactionNumber", survey.TransactionNumber);
		values.put("StatusID", survey.StatusID);
		values.put("TextSize", survey.TextSize);
		values.put("TextColor", survey.TextColor);
		values.put("IntroductionText", survey.IntroductionText);
		values.put("IntroductionImage", survey.IntroductionImage);
		values.put("EndingText", survey.EndingText);
		values.put("EndingImage", survey.EndingImage);
		values.put("FooterImage", survey.FooterImage);
		values.put("BackgroundImage", survey.BackgroundImage);
		values.put("ProcedureID", survey.ProcedureID);
        int i = db.update("SURVEYS",values, "SurveyID"+" = ?", new String[] { String.valueOf(survey.SurveyID) });
        db.close();
        return i;
    }
    
    public int updateSurveyImages(int SurveyID, String IntroductionImage, String EndingImage, String FooterImage, String BackgroundImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
		values.put("IntroductionImage", IntroductionImage);
		values.put("EndingImage", EndingImage);
		values.put("FooterImage", FooterImage);
		values.put("BackgroundImage", BackgroundImage);
        int i = db.update("SURVEYS",values, "SurveyID"+" = ?", new String[] { String.valueOf(SurveyID) });
        db.close();
        return i;
    }

    public void deleteSurveys(String[] surveys) {
        SQLiteDatabase db = this.getWritableDatabase();
        String args = TextUtils.join(", ", surveys);
        db.execSQL(String.format("DELETE FROM SURVEYS WHERE SurveyID NOT IN (%s);", args));
        db.close();
    }
    
    public void deleteAllSurveys() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM SURVEYS"));
        db.close();
    }
    
    public Surveys getSurvey(int SurveyID) {
    	Surveys resultado = null;
        String query = "SELECT SurveyID,SurveyName,ClientID,ClientName,TransactionNumber,StatusID,TextSize,TextColor,IntroductionText,EndingText,ProcedureID FROM SURVEYS WHERE SurveyID="+SurveyID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
            	resultado = new Surveys(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getString(3),cursor.getString(4),Integer.parseInt(cursor.getString(5)),cursor.getString(6),cursor.getString(7),cursor.getString(8),"",cursor.getString(9),"","","",Integer.parseInt(cursor.getString(10)));
            	break;
            } while (cursor.moveToNext());
        }
        query = "SELECT IntroductionImage FROM SURVEYS WHERE SurveyID="+resultado.SurveyID;
        cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
            	resultado.IntroductionImage = cursor.getString(0);
            	break;
            } while (cursor.moveToNext());
        }
        query = "SELECT EndingImage FROM SURVEYS WHERE SurveyID="+resultado.SurveyID;
        cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
            	resultado.EndingImage = cursor.getString(0);
            	break;
            } while (cursor.moveToNext());
        }
        query = "SELECT FooterImage FROM SURVEYS WHERE SurveyID="+resultado.SurveyID;
        cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
            	resultado.FooterImage = cursor.getString(0);
            	break;
            } while (cursor.moveToNext());
        }
        query = "SELECT BackgroundImage FROM SURVEYS WHERE SurveyID="+resultado.SurveyID;
        cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
            	resultado.BackgroundImage = cursor.getString(0);
            	break;
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close(); 
        return resultado;
    }

    //================================================================================
    // Questions Methods
    //================================================================================
		    
    public String getQuestionValueByID(int id) {
    	String value="";
        String query = "SELECT  Value FROM QUESTIONS WHERE QuestionID="+id ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
            	if(cursor.getString(0)!=null)
            	{
            	value=cursor.getString(0);
            	}
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return value;
    }
    
    public void deleteQuestions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM QUESTIONS"));
        db.close();
    }



    public int  getQuestionsQty(String SurveyID) {
        String query = "SELECT count(*) FROM QUESTIONS WHERE SurveyID="+SurveyID ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int qty = 0;
        if (cursor.moveToFirst()) {
            do {
            	if(cursor.getString(0)!=null)
            	{
            		qty =  Integer.parseInt(cursor.getString(0));
            	}
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return qty;
    }
    
    public void addQuestion(Questions question){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("QuestionID", question.QuestionID);
		values.put("SurveyID", question.SurveyID);
		values.put("QuestionTypeID", question.QuestionTypeID);
		values.put("SectionID", question.SectionID); 
		values.put("SectionName", question.SectionName);
		values.put("Title", question.Title);
		values.put("Text", question.Text);
		values.put("Value", question.Value);
		values.put("Comment", question.Comment); 
		values.put("OrderNumber", question.OrderNumber);
		values.put("Question1", question.Question1);
		values.put("Instruction", question.Instruction);
		values.put("ShortName", question.ShortName);
		values.put("Minimum", question.Minimum); 
		values.put("Maximum", question.Maximum);
		
		int Required=0;
		if(question.Required==true)
		{
			Required=1;
		}
		
		values.put("Required", Required);
		values.put("Decimals", question.Decimals);
		values.put("Preffix", question.Preffix); // 
		values.put("Suffix", question.Suffix); 
		int Randomize=0;
		if(question.Randomize==true)
		{
			Randomize=1;
		}
		values.put("Randomize", Randomize);
		
		int IncludeScoring=0;
		if(question.IncludeScoring==true)
		{
			IncludeScoring=1;
		}
		values.put("IncludeScoring", IncludeScoring); 
		
		int DisplayImages=0;
		if(question.DisplayImages==true)
		{
			DisplayImages=1;
		}
		
		values.put("DisplayImages", DisplayImages);
		values.put("MinAnswers", question.MinAnswers);
		values.put("MaxAnswers", question.MaxAnswers);
		values.put("LeftLabel", question.LeftLabel); // 
		values.put("RightLabel", question.RightLabel); 
		
		int ImageAboveText=0;
		if(question.ImageAboveText==true)
		{
			ImageAboveText=1;
		}
		
		values.put("ImageAboveText", ImageAboveText);
		values.put("DefaultDate", question.DefaultDate);
		values.put("DateTypeID", question.DateTypeID); 
		values.put("DateTypeName", question.DateTypeName);
		values.put("CatalogID", question.CatalogID);
		values.put("CatalogElements", question.CatalogElements);
		values.put("Condition", question.Condition); // 
		values.put("Valu", question.Valu); 
		values.put("SendTo", question.SendTo);
		
		
		values.put("Image", question.Image);
		values.put("Options", question.Options);
		values.put("OtherOption", question.OtherOption);
		values.put("Hidden", question.Hidden);
		values.put("Answer", question.Answer);
		values.put("ProcedureID", question.ProcedureID);
		values.put("Blocked", question.Blocked);
		db.insert("questions", null, values);
		db.close(); 
	}

    public void deleteQuestionsbyQuestionID(int QuestionID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM questions WHERE SurveyID="+QuestionID);
        db.close();
    }

    public List<List<Questions>> getQuestions(int SurveyID) {
    	List<List<Questions>> questions = new ArrayList<List<Questions>>();
    	List<Questions> data = new ArrayList<Questions>();
        String query = "SELECT  distinct([SectionID]) FROM QUESTIONS WHERE SurveyID="+SurveyID + " order by OrderNumber";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
            	data=null;
            	int SectionID=Integer.parseInt(cursor.getString(0));
            	data = getQuestionsBySection(SurveyID,SectionID);
            	questions.add(data);
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return questions;
    }

    public List<Questions> getQuestionsBySection(int SurveyID,int SectionID) {
    	List<Questions> questions = new ArrayList<Questions>();
        String query = "SELECT  * FROM QUESTIONS WHERE SurveyID="+SurveyID +" and SectionID="+SectionID +" order by OrderNumber";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Questions question = null;
        if (cursor.moveToFirst()) {
            do {
            	Boolean required=false;
            	if (Integer.parseInt(cursor.getString(15))==1)
            	{
            		required=true;
            	}
            	
            	Boolean Randomize=false;
            	if (Integer.parseInt(cursor.getString(19))==1)
            	{
            		Randomize=true;
            	}
            	
            	Boolean IncludeScoring=false;
            	if (Integer.parseInt(cursor.getString(20))==1)
            	{
            		IncludeScoring=true;
            	}
            	
            	Boolean DisplayImages=false;
            	if (Integer.parseInt(cursor.getString(21))==1)
            	{
            		DisplayImages=true;
            	}
            	
            	Boolean ImageAboveText=false;
            	if (Integer.parseInt(cursor.getString(26))==1)
            	{
            		ImageAboveText=true;
            	}
            	Boolean Hidden = false;
    			if (Integer.parseInt(cursor.getString(38))==1)
            	{
    				Hidden=true;
            	}
            	question = new Questions(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3)),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),Integer.parseInt(cursor.getString(9)),cursor.getString(10),cursor.getString(11),cursor.getString(12),Integer.parseInt(cursor.getString(13)),Integer.parseInt(cursor.getString(14)),required,Integer.parseInt(cursor.getString(16)),cursor.getString(17),cursor.getString(18),Randomize,IncludeScoring,DisplayImages,Integer.parseInt(cursor.getString(22)),Integer.parseInt(cursor.getString(23)),cursor.getString(24),cursor.getString(25),ImageAboveText,cursor.getString(27),Integer.parseInt(cursor.getString(28)),cursor.getString(29),Integer.parseInt(cursor.getString(30)),cursor.getString(31),cursor.getString(32),cursor.getString(33),cursor.getString(34),cursor.getString(35),cursor.getString(36),cursor.getString(37), Hidden,cursor.getString(39),Integer.parseInt(cursor.getString(40)),Integer.parseInt(cursor.getString(41)));
            	questions.add(question);
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return questions;
    }
    
    public List<Questions> getQuestionsByAutocompleteAndFeedback(int SurveyID) {
    	List<Questions> questions = new ArrayList<Questions>();
        String query = "SELECT  * FROM QUESTIONS WHERE SurveyID="+SurveyID +" and QuestionTypeID = 18 and Randomize = 1 order by OrderNumber";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Questions question = null;
        if (cursor.moveToFirst()) {
            do {
            	Boolean required=false;
            	if (Integer.parseInt(cursor.getString(15))==1)
            	{
            		required=true;
            	}
            	
            	Boolean Randomize=false;
            	if (Integer.parseInt(cursor.getString(19))==1)
            	{
            		Randomize=true;
            	}
            	
            	Boolean IncludeScoring=false;
            	if (Integer.parseInt(cursor.getString(20))==1)
            	{
            		IncludeScoring=true;
            	}
            	
            	Boolean DisplayImages=false;
            	if (Integer.parseInt(cursor.getString(21))==1)
            	{
            		DisplayImages=true;
            	}
            	
            	Boolean ImageAboveText=false;
            	if (Integer.parseInt(cursor.getString(26))==1)
            	{
            		ImageAboveText=true;
            	}
            	Boolean Hidden = false;
    			if (Integer.parseInt(cursor.getString(38))==1)
            	{
    				Hidden=true;
            	}
            	question = new Questions(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3)),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),Integer.parseInt(cursor.getString(9)),cursor.getString(10),cursor.getString(11),cursor.getString(12),Integer.parseInt(cursor.getString(13)),Integer.parseInt(cursor.getString(14)),required,Integer.parseInt(cursor.getString(16)),cursor.getString(17),cursor.getString(18),Randomize,IncludeScoring,DisplayImages,Integer.parseInt(cursor.getString(22)),Integer.parseInt(cursor.getString(23)),cursor.getString(24),cursor.getString(25),ImageAboveText,cursor.getString(27),Integer.parseInt(cursor.getString(28)),cursor.getString(29),Integer.parseInt(cursor.getString(30)),cursor.getString(31),cursor.getString(32),cursor.getString(33),cursor.getString(34),cursor.getString(35),cursor.getString(36),cursor.getString(37), Hidden,cursor.getString(39),Integer.parseInt(cursor.getString(40)),Integer.parseInt(cursor.getString(41)));
            	questions.add(question);
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return questions;
    }
    
    public Questions getQuestion(int QuestionID) {
        String query = "SELECT  * FROM QUESTIONS WHERE QuestionID=" + QuestionID ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Questions question = null;
        if (cursor.moveToFirst()) {
            do {
            	Boolean required=false;
            	if (Integer.parseInt(cursor.getString(15))==1)
            	{
            		required=true;
            	}
            	
            	Boolean Randomize=false;
            	if (Integer.parseInt(cursor.getString(19))==1)
            	{
            		Randomize=true;
            	}
            	
            	Boolean IncludeScoring=false;
            	if (Integer.parseInt(cursor.getString(20))==1)
            	{
            		IncludeScoring=true;
            	}
            	
            	Boolean DisplayImages=false;
            	if (Integer.parseInt(cursor.getString(21))==1)
            	{
            		DisplayImages=true;
            	}
            	
            	Boolean ImageAboveText=false;
            	if (Integer.parseInt(cursor.getString(26))==1)
            	{
            		ImageAboveText=true;
            	}
            	Boolean Hidden = false;
    			if (Integer.parseInt(cursor.getString(38))==1)
            	{
    				Hidden=true;
            	}
            	question = new Questions(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3)),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),Integer.parseInt(cursor.getString(9)),cursor.getString(10),cursor.getString(11),cursor.getString(12),Integer.parseInt(cursor.getString(13)),Integer.parseInt(cursor.getString(14)),required,Integer.parseInt(cursor.getString(16)),cursor.getString(17),cursor.getString(18),Randomize,IncludeScoring,DisplayImages,Integer.parseInt(cursor.getString(22)),Integer.parseInt(cursor.getString(23)),cursor.getString(24),cursor.getString(25),ImageAboveText,cursor.getString(27),Integer.parseInt(cursor.getString(28)),cursor.getString(29),Integer.parseInt(cursor.getString(30)),cursor.getString(31),cursor.getString(32),cursor.getString(33),cursor.getString(34),cursor.getString(35),cursor.getString(36),cursor.getString(37), Hidden,cursor.getString(39),Integer.parseInt(cursor.getString(40)),Integer.parseInt(cursor.getString(41)));
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return question;
    }
    
    public Questions getQuestionByOrderNumberAndSurveyID(int OrderNumber,int SurveyID) {
        String query = "SELECT  * FROM QUESTIONS WHERE OrderNumber=" + (OrderNumber - 1 )+ " AND SurveyID=" + SurveyID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Questions question = null;
        if (cursor.moveToFirst()) {
            do {
            	Boolean required=false;
            	if (Integer.parseInt(cursor.getString(15))==1)
            	{
            		required=true;
            	}
            	
            	Boolean Randomize=false;
            	if (Integer.parseInt(cursor.getString(19))==1)
            	{
            		Randomize=true;
            	}
            	
            	Boolean IncludeScoring=false;
            	if (Integer.parseInt(cursor.getString(20))==1)
            	{
            		IncludeScoring=true;
            	}
            	
            	Boolean DisplayImages=false;
            	if (Integer.parseInt(cursor.getString(21))==1)
            	{
            		DisplayImages=true;
            	}
            	
            	Boolean ImageAboveText=false;
            	if (Integer.parseInt(cursor.getString(26))==1)
            	{
            		ImageAboveText=true;
            	}
            	Boolean Hidden = false;
    			if (Integer.parseInt(cursor.getString(38))==1)
            	{
    				Hidden=true;
            	}
            	question = new Questions(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3)),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),Integer.parseInt(cursor.getString(9)),cursor.getString(10),cursor.getString(11),cursor.getString(12),Integer.parseInt(cursor.getString(13)),Integer.parseInt(cursor.getString(14)),required,Integer.parseInt(cursor.getString(16)),cursor.getString(17),cursor.getString(18),Randomize,IncludeScoring,DisplayImages,Integer.parseInt(cursor.getString(22)),Integer.parseInt(cursor.getString(23)),cursor.getString(24),cursor.getString(25),ImageAboveText,cursor.getString(27),Integer.parseInt(cursor.getString(28)),cursor.getString(29),Integer.parseInt(cursor.getString(30)),cursor.getString(31),cursor.getString(32),cursor.getString(33),cursor.getString(34),cursor.getString(35),cursor.getString(36),cursor.getString(37), Hidden,cursor.getString(39),Integer.parseInt(cursor.getString(40)),Integer.parseInt(cursor.getString(41)));
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return question;
    }
    
    public int updateQuestionBlocked(Questions question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
		values.put("Blocked", question.Blocked);
        int i = db.update("QUESTIONS", values, "QuestionID"+" = ?", new String[] { String.valueOf(question.QuestionID) });
        db.close();
        return i;
    }
    
    public int updateQuestionElements(Questions question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
		values.put("CatalogElements", question.CatalogElements);
        int i = db.update("QUESTIONS", values, "QuestionID"+" = ?", new String[] { String.valueOf(question.QuestionID) });
        db.close();
        return i;
    }
    
    public int updateQuestion(Questions question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
		values.put("Answer", question.Answer);
        int i = db.update("QUESTIONS", values, "QuestionID"+" = ?", new String[] { String.valueOf(question.QuestionID) });
        db.close();
        return i;
    }
    
    public int updateQuestionImage(Questions question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
		values.put("Image", question.Image);
        int i = db.update("QUESTIONS", values, "QuestionID"+" = ?", new String[] { String.valueOf(question.QuestionID) });
        db.close();
        return i;
    }
    
    public int updateQuestionAnswers(int  surveyID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
		values.put("Answer", "");
		values.put("Blocked", "0");
        int i = db.update("QUESTIONS", values, "surveyID"+" = ?", new String[] { String.valueOf(surveyID) });
        db.close();
        return i;
    }
    
    //================================================================================
    // Question Options Methods
    //================================================================================
    
    public void addQuestionOption(QuestionOptions QuestionOptions){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("QuestionOptionID", QuestionOptions.QuestionOptionID);
		values.put("QuestionID", QuestionOptions.QuestionID);
		values.put("Name", QuestionOptions.Name);
		values.put("Score", QuestionOptions.Score);
		values.put("Image", QuestionOptions.Image);
		values.put("Condition", QuestionOptions.Condition);
		values.put("Value", QuestionOptions.Value);
		values.put("SendTo", QuestionOptions.SendTo);
		values.put("IsText", QuestionOptions.IsText);
		values.put("Type", QuestionOptions.Type);
		db.insert("QuestionOptions", null, values); 
		db.close(); 
	}
    
    public void deleteQuestionOptions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM QuestionOptions");
        db.close();
    }
    
    public List<QuestionOptions> getQuestionOptions(int QuestionID) {
    	List<QuestionOptions> resultado = new ArrayList<QuestionOptions>();
        String query = "SELECT * FROM QUESTIONOPTIONS WHERE QuestionID="+QuestionID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        QuestionOptions QuestionOptions = null;
        if (cursor.moveToFirst()) {
            do {
            	QuestionOptions = new QuestionOptions(cursor.getInt(0),cursor.getInt(1),cursor.getString(2),cursor.getDouble(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getInt(8),cursor.getString(9));
            	resultado.add(QuestionOptions);
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return resultado;
    }
    
    public QuestionOptions getQuestionOption(int QuestionOptionID) {
        String query = "SELECT * FROM QUESTIONOPTIONS WHERE QuestionOptionID="+QuestionOptionID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        QuestionOptions QuestionOptions = null;
        if (cursor.moveToFirst()) {
            do {
            	QuestionOptions = new QuestionOptions(cursor.getInt(0),cursor.getInt(1),cursor.getString(2),cursor.getDouble(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getInt(8),cursor.getString(9));
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return QuestionOptions;
    }
    
    public QuestionOptions getQuestionOptionByQuestionIDAndName(int QuestionID, String Name) {
        String query = "SELECT * FROM QUESTIONOPTIONS WHERE QuestionID="+QuestionID + " AND Name='" + Name + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        QuestionOptions QuestionOptions = null;
        if (cursor.moveToFirst()) {
            do {
            	QuestionOptions = new QuestionOptions(cursor.getInt(0),cursor.getInt(1),cursor.getString(2),cursor.getDouble(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getInt(8),cursor.getString(9));
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return QuestionOptions;
    }
    
    //================================================================================
    // Question Sentences Methods
    //================================================================================
    
    public void addQuestionSentence(QuestionSentences QuestionSentences){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("QuestionSentenceID", QuestionSentences.QuestionSentenceID);
		values.put("QuestionID", QuestionSentences.QuestionID);
		values.put("Name", QuestionSentences.Name);
		db.insert("QuestionSentences", null, values); 
		db.close(); 
	}
    
    public void deleteQuestionSentences() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM QuestionSentences");
        db.close();
    }

    public List<QuestionSentences> getQuestionSentences(int QuestionID) {
    	List<QuestionSentences> resultado = new ArrayList<QuestionSentences>();
        String query = "SELECT * FROM QuestionSentences WHERE QuestionID="+QuestionID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        QuestionSentences QuestionSentences = null;
        if (cursor.moveToFirst()) {
            do {
            	QuestionSentences = new QuestionSentences(cursor.getInt(0),cursor.getInt(1),cursor.getString(2));
            	resultado.add(QuestionSentences);
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return resultado;
    }

    //================================================================================
    // PhotoMethods Methods
    //================================================================================
    
    public void addPhoto(String photo, int ID){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("PhotoID", ID);
		values.put("Photo", photo);
		db.insert("photos", null, values); 
		db.close(); 
	}
    
    public int UpdatePhoto(String photo, int ID){		
        SQLiteDatabase db = this.getWritableDatabase();		
        ContentValues values = new ContentValues();		
        values.put("Photo", photo);		
        int i = db.update("photos", values, "PhotoID"+" = ?",new String[] { String.valueOf(ID) });		
        db.close();		
        return i;		
    }
    
    public String getPhoto(int ID) {
    	String resultado = "";
        String query = "SELECT * FROM photos WHERE PhotoID="+ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
            	resultado = cursor.getString(1);
            	break;
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return resultado;
    }
    
    public void deletePhotos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM photos");
        db.close();
    }
    
    //================================================================================
    // SelectedSurvey Methods
    //================================================================================
    
    public void addSelectedSurvey(String SurveyName, int SurveyID,String DateFormStart,int UbicheckID){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("SurveyID", SurveyID);
		values.put("SurveyName", SurveyName);
		values.put("DateFormStart", DateFormStart);
		values.put("UbicheckID", UbicheckID);
		db.insert("selectedSurveys", null, values); 
		db.close(); 
	}
    
    public SelectedSurvey GetSelectedSurvey() {
        String query = "SELECT SurveyID,SurveyName,DateFormStart,UbicheckID FROM selectedSurveys";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        SelectedSurvey SelectedSurvey = null;
        if (cursor.moveToFirst()) {
            do {
            	SelectedSurvey = new SelectedSurvey(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3));
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }  
        db.close();
        return SelectedSurvey;
    }
    
    public int UpdateSelectedSurvey(SelectedSurvey SelectedSurvey) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("SurveyID", SelectedSurvey.SurveyID);
		values.put("SurveyName", SelectedSurvey.SurveyName);
		values.put("DateFormStart", SelectedSurvey.DateFormStart);
		values.put("UbicheckID", SelectedSurvey.UbicheckID);
        int i = db.update("selectedSurveys", values, "SurveyID"+" = ?",new String[] { String.valueOf(SelectedSurvey.SurveyID) });
        db.close();
        return i;
    }
    
    public void deleteSelectedSurveys() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM selectedSurveys");
        db.close();
    }
    
    //================================================================================
    // Biometrics Methods
    //================================================================================
    
    public void addBiometric(int BiometricID, String Name,int LastConecction){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("BiometricID", BiometricID);
		values.put("Name", Name);
		values.put("LastConecction", LastConecction);
		db.insert("biometrics", null, values); 
		db.close(); 
	}
    
    public int UpdateBiometric(int LastConecction, int BiometricID){		
        SQLiteDatabase db = this.getWritableDatabase();		
        ContentValues values = new ContentValues();		
        values.put("LastConecction", LastConecction);		
        int i = db.update("biometrics", values, "BiometricID"+" = ?",new String[] { String.valueOf(BiometricID) });		
        db.close();		
        return i;		
    }
    
    public List<Biometrics> getBiometricsByName() {
    	List<Biometrics> resultado = new ArrayList<Biometrics>();
        String query = "SELECT * FROM biometrics ORDER BY Name";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Biometrics Biometrics = null;
        if (cursor.moveToFirst()) {
            do {
            	Biometrics = new Biometrics(cursor.getInt(0),cursor.getString(1),cursor.getInt(2));
            	resultado.add(Biometrics);
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return resultado;
    }
    
    public List<Biometrics> getBiometricsByLastConnection() {
    	List<Biometrics> resultado = new ArrayList<Biometrics>();
        String query = "SELECT * FROM biometrics ORDER BY LastConecction";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Biometrics Biometrics = null;
        if (cursor.moveToFirst()) {
            do {
            	Biometrics = new Biometrics(cursor.getInt(0),cursor.getString(1),cursor.getInt(2));
            	resultado.add(Biometrics);
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return resultado;
    }
    
    public Biometrics getBiometricByID(int BiometricID) {
        String query = "SELECT * FROM biometrics WHERE BiometricID=" + BiometricID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Biometrics Biometrics = null;
        if (cursor.moveToFirst()) {
            do {
            	Biometrics = new Biometrics(cursor.getInt(0),cursor.getString(1),cursor.getInt(2));
            } while (cursor.moveToNext());
        }
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return Biometrics;
    }
    
    public void deleteBiometrics() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM biometrics");
        db.close();
    }
    
    public void deleteBiometricByID(int BiometricID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM biometrics WHERE BiometricID=" + BiometricID);
        db.close();
    }


}

