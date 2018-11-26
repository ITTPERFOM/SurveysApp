package com.timetracker.data;

public class QuestionOptions {
	 public int QuestionOptionID;
	 public int QuestionID;
	 public String Name;
	 public double Score;
	 public String Image;
	 public String Condition;
	 public String Value;
	 public String SendTo;
	 public int IsText;
	 public String Type;
	 public QuestionOptions(int QuestionOptionID, int QuestionID,String Name,double Score,String Image, String Condition,String Value,String SendTo,int IsText,String Type){
		 this.QuestionOptionID = QuestionOptionID;
		 this.QuestionID = QuestionID;
		 this.Name = Name;
		 this.Score = Score;
		 this.Image = Image;
		 this.Condition = Condition;
		 this.Value = Value;
		 this.SendTo = SendTo;
		 this.IsText = IsText;
		 this.Type = Type;
	 }
}
