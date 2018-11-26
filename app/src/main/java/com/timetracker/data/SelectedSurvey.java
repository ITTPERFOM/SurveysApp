package com.timetracker.data;

public class SelectedSurvey {
	public int SurveyID;
	public String SurveyName;
	public String DateFormStart;
	public int UbicheckID;
	public SelectedSurvey(int SurveyID, String SurveyName, String DateFormStart,int UbicheckID){
	    this.SurveyID = SurveyID;
	    this.SurveyName = SurveyName;
	    this.DateFormStart = DateFormStart;
	    this.UbicheckID = UbicheckID;
	}
}
