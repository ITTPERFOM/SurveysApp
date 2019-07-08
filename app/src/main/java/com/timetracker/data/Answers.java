package com.timetracker.data;

public class Answers {
	public int AnswerID;
	public int QuestionID;
	public String Value;
	public int QuestionTypeID;
	public double Latitude;
	public double Longitude;
	public String DeviceMac;
	public String Identifier;
	public String DateFormStart;
	public String DateFormFinish;
	public int Ubicheck;
	public int PostProcedureID;
	public Answers(int AnswerID, int QuestionID, String Value,int QuestionTypeID, double Latitude,double Longitude, String DeviceMac,String Identifier,String DateFormStart,String DateFormFinish,int Ubicheck,int PostProcedureID){
	    this.AnswerID = AnswerID;
		this.QuestionID = QuestionID;
	    this.Value = Value;
	    this.QuestionTypeID = QuestionTypeID;
	    this.Latitude = Latitude;
	    this.Longitude = Longitude;
	    this.DeviceMac = DeviceMac;
	    this.Identifier = Identifier;
	    this.DateFormStart = DateFormStart;
	    this.DateFormFinish = DateFormFinish;
	    this.Ubicheck = Ubicheck;
	    this.PostProcedureID = PostProcedureID;
	}
}
