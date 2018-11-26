package com.timetracker.data;

public class Surveys {
	 public int SurveyID;
	 public String SurveyName;
	 public int ClientID;
	 public String ClientName;
	 public String TransactionNumber;
	 public int StatusID;
	 public String TextSize;
	 public String TextColor;
	 public String IntroductionText;
	 public String IntroductionImage;
	 public String EndingText;
	 public String EndingImage;
	 public String FooterImage;
	 public String BackgroundImage;
	 public int ProcedureID;
	 public Surveys(int SurveyID, String SurveyName,int ClientID,String ClientName,String TransactionNumber,int StatusID,String TextSize,String TextColor,String IntroductionText,String IntroductionImage, String EndingText,String EndingImage,String FooterImage,String BackgroundImage,int ProcedureID){
		 this.SurveyID = SurveyID;
		 this.SurveyName = SurveyName;
		 this.ClientID = ClientID;
		 this.ClientName = ClientName;
		 this.TransactionNumber = TransactionNumber;
		 this.StatusID = StatusID;
		 this.TextSize = TextSize;
		 this.TextColor = TextColor;
		 this.IntroductionText = IntroductionText;
		 this.IntroductionImage = IntroductionImage;
		 this.EndingText = EndingText;
		 this.EndingImage = EndingImage;
		 this.FooterImage = FooterImage;
		 this.BackgroundImage = BackgroundImage;
		 this.ProcedureID = ProcedureID;
	 }
}
