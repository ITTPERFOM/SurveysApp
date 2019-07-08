package com.timetracker.business;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.timetracker.data.Questions;
//All JSON reads are in this class
public class JsonMethods {

	
	//Get questions from restful JSON response
	public List<List<Questions>> GetSurveyQuestions(String result) {
		 List<List<Questions>> SurveyQuestions = new ArrayList<List<Questions>>();
		 List<Questions> data = new ArrayList<Questions>();
		 try {
			 JSONArray jr = new JSONArray(result);
           	 int max=jr.length();
           	 String flag="";
           	 for(int i=0;i<max;i++)
               {
           		 JSONObject jb = (JSONObject)jr.getJSONObject(i);
           		 int QuestionID=Integer.parseInt((String) jb.get("QuestionID"));
           	     int SurveyID=Integer.parseInt((String) jb.get("SurveyID"));
           	     int QuestionTypeID=Integer.parseInt((String) jb.get("QuestionTypeID"));
           	     int SectionID=Integer.parseInt((String) jb.get("SectionID"));
           	     String SectionName=(String) jb.get("SectionName");
           	     String Title=(String) jb.get("Title");
           	     String Text=(String) jb.get("Text");
           	     String Value=(String) jb.get("Value");
           	     String Comment=(String) jb.get("Comment");
           	     int OrderNumber=Integer.parseInt((String) jb.get("OrderNumber"));
           	     String Question1=(String) jb.get("Question1");
           	     String Instruction=(String) jb.get("Instruction");
           	     String ShortName=(String) jb.get("ShortName");
           	     int Minimum=Integer.parseInt((String) jb.get("Minimum"));
           	     int Maximum=Integer.parseInt((String) jb.get("Maximum"));
           	     Boolean Required=Boolean.parseBoolean((String) jb.get("Required"));
           	     int Decimals=Integer.parseInt((String) jb.get("Decimals"));
           	     String Preffix=(String) jb.get("Preffix");
           	     String Suffix=(String) jb.get("Suffix");
           	     Boolean Randomize=Boolean.parseBoolean((String) jb.get("Randomize"));
           	     Boolean IncludeScoring=Boolean.parseBoolean((String) jb.get("IncludeScoring"));
           	     Boolean DisplayImages=Boolean.parseBoolean((String) jb.get("DisplayImages"));
           	     int MinAnswers=Integer.parseInt((String) jb.get("MinAnswers"));
           	     int MaxAnswers=Integer.parseInt((String) jb.get("MaxAnswers"));
           	     String LeftLabel=(String) jb.get("LeftLabel");
           	     String RightLabel=(String) jb.get("RightLabel");
           	     Boolean ImageAboveText=Boolean.parseBoolean((String) jb.get("ImageAboveText"));
           	     String DefaultDate=(String) jb.get("DefaultDate");
           	     int DateTypeID=Integer.parseInt((String) jb.get("QuestDateTypeIDionID"));
           	     String DateTypeName=(String) jb.get("DateTypeName");
           	     int CatalogID=Integer.parseInt((String) jb.get("CatalogID"));
           	     String CatalogElements=(String) jb.get("CatalogElements");
           	     String Condition=(String) jb.get("Condition");
           	     String Valu=(String) jb.get("Valu");
           	     String SendTo=(String) jb.get("SendTo");
           	     String Image=(String) jb.get("Image");
       	     	 String Options=(String) jb.get("Options");
       	     	 String OtherOption=(String) jb.get("OtherOption");
       	     	 Boolean Hidden=Boolean.parseBoolean((String) jb.get("Hidden"));
           	     String Answer="";
           	     int ProcedureID=Integer.parseInt((String) jb.get("ProcedureID"));
            	Questions item = new Questions(QuestionID,SurveyID,QuestionTypeID,SectionID,SectionName,Title,Text,Value,Comment,OrderNumber,Question1,Instruction,ShortName,Minimum,Maximum,Required,Decimals,Preffix,Suffix,Randomize,IncludeScoring,DisplayImages,MinAnswers,MaxAnswers,LeftLabel,RightLabel,ImageAboveText,DefaultDate,DateTypeID,DateTypeName,CatalogID,CatalogElements,Condition,Valu,SendTo,Image,Options,OtherOption,Hidden,Answer,ProcedureID,0,0);
            	if(max==i+1)
                {   
                	data.add(item);
                	SurveyQuestions.add(data);
                }      
            	else if(flag=="")
                {
                flag=SectionName;
            	data.add(item);
                }
                else if(flag.equals(SectionName))
                {
                	flag=SectionName;
	            	data.add(item);
                }
                else if(!flag.equals(SectionName))
                {
                	SurveyQuestions.add(data);
                	data=new ArrayList<Questions>();
                	flag=SectionName;
	            	data.add(item);
                }              
                    
	             }
		  }
		  catch(Exception ex){
			  SurveyQuestions=null;
		    }
		return SurveyQuestions;
	}

	public List<List<Questions>> GetList(List<Questions> list ) {
		 List<List<Questions>> SurveyQuestions = new ArrayList<List<Questions>>();
		 List<Questions> data = new ArrayList<Questions>();
		 int max=list.size();
		 String flag="";
		 for(int i=0;i<max;i++)
         {
			 Questions item = list.get(i);
         	if(max==i+1)
             {   
             	data.add(item);
             	SurveyQuestions.add(data);
             }      
         	else if(flag=="")
             {
             flag=item.SectionName;
         	data.add(item);
             }
             else if(flag.equals(item.SectionName))
             {
             	flag=item.SectionName;
	            	data.add(item);
             }
             else if(!flag.equals(item.SectionName))
             {
             	SurveyQuestions.add(data);
             	data=new ArrayList<Questions>();
             	flag=item.SectionName;
	            	data.add(item);
             }              
         
         }
		 
		 return SurveyQuestions;
	}
	
}
