package com.timetracker.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Questions implements Parcelable  {

    public int QuestionID;
    public int SurveyID;
    public int QuestionTypeID;
    public int SectionID;
    public String SectionName;
    public String Title;
    public String Text;
    public String Value;

    public String Comment;
    public int OrderNumber;
    public String Question1;
    public String Instruction;
    public String ShortName;
    public int Minimum;
    public int Maximum;
    public Boolean Required;
    public int Decimals;

    public String Preffix;
    public String Suffix;
    public Boolean Randomize;
    public Boolean IncludeScoring;
    public Boolean DisplayImages;
    public int MinAnswers;
    public int MaxAnswers;
    public String LeftLabel;
    public String RightLabel;

    public Boolean ImageAboveText;
    public String DefaultDate;
    public int DateTypeID;
    public String DateTypeName;
    public int CatalogID;
    public String CatalogElements;
    public String Condition;
    public String Valu;
    public String SendTo;
    public String Answer;

    public String Image;
    public String Options;
    public String OtherOption;

    public Boolean Hidden;

    public int ProcedureID;
    public int Blocked;

    public Questions(int QuestionID,int SurveyID,int QuestionTypeID,int SectionID,String SectionName,String Title,String Text,String Value,String Comment,int OrderNumber,String Question1,String Instruction,String ShortName,int Minimum,int Maximum,Boolean Required,int Decimals,String Preffix,String Suffix,Boolean Randomize,Boolean IncludeScoring,Boolean DisplayImages,int MinAnswers,int MaxAnswers,String LeftLabel,String RightLabel,Boolean ImageAboveText,String DefaultDate,int DateTypeID,String DateTypeName,int CatalogID,String CatalogElements,String Condition,String Valu,String SendTo,String Image,String Options,String OtherOption,Boolean Hidden,String Answer,int ProcedureID,int Blocked ){
        this.QuestionID = QuestionID;
        this.SurveyID = SurveyID;
        this.QuestionTypeID = QuestionTypeID;
        this.SectionID = SectionID;
        this.SectionName = SectionName;
        this.Title = Title;
        this.Text = Text;
        this.Value = Value;
        this.Comment = Comment;
        this.OrderNumber = OrderNumber;
        this.Question1 = Question1;
        this.Instruction = Instruction;
        this.ShortName = ShortName;
        this.Minimum = Minimum;
        this.Maximum = Maximum;
        this.Required = Required;
        this.Decimals = Decimals;
        this.Preffix = Preffix;
        this.Suffix = Suffix;
        this.Randomize = Randomize;
        this.IncludeScoring = IncludeScoring;
        this.DisplayImages = DisplayImages;
        this.MinAnswers = MinAnswers;
        this.MaxAnswers = MaxAnswers;
        this.LeftLabel = LeftLabel;
        this.RightLabel = RightLabel;
        this.ImageAboveText = ImageAboveText;
        this.DefaultDate = DefaultDate;
        this.DateTypeID = DateTypeID;
        this.DateTypeName = DateTypeName;
        this.CatalogID = CatalogID;
        this.CatalogElements = CatalogElements;
        this.Condition = Condition;
        this.Valu = Valu;
        this.SendTo = SendTo;
        this.Valu = Valu;
        this.Image = Image;
        this.Options = Options;
        this.OtherOption = OtherOption;
        this.Hidden = Hidden;
        this.Answer = Answer;
        this.ProcedureID = ProcedureID;
        this.Blocked = Blocked;
    }

    private Questions(Parcel in) {
        QuestionID= in.readInt();
        SurveyID= in.readInt();
        QuestionTypeID= in.readInt();
        SectionID= in.readInt();
        SectionName = in.readString();
        Title = in.readString();
        Text = in.readString();
        Value = in.readString();
        Comment = in.readString();
        OrderNumber= in.readInt();
        Question1= in.readString();
        Instruction = in.readString();
        ShortName=in.readString();
        Minimum= in.readInt();
        Maximum= in.readInt();
        Required= in.readByte() != 0;
        Decimals= in.readInt();
        Preffix = in.readString();
        Suffix = in.readString();
        Randomize= in.readByte() != 0;
        IncludeScoring= in.readByte() != 0;
        DisplayImages= in.readByte() != 0;
        MinAnswers= in.readInt();
        MaxAnswers= in.readInt();
        LeftLabel = in.readString();
        RightLabel = in.readString();
        ImageAboveText= in.readByte() != 0;
        DefaultDate = in.readString();
        DateTypeID= in.readInt();
        DateTypeName = in.readString();
        CatalogID= in.readInt();
        CatalogElements = in.readString();
        Condition = in.readString();
        Valu = in.readString();
        SendTo = in.readString();
        Image = in.readString();
        Options = in.readString();
        OtherOption = in.readString();
        Hidden = in.readByte() != 0;
        Answer=in.readString();
        ProcedureID = in.readInt();
    }



    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(QuestionID);
        out.writeInt(SurveyID);
        out.writeInt(QuestionTypeID);
        out.writeInt(SectionID);
        out.writeString(SectionName);
        out.writeString(Title);
        out.writeString(Text);
        out.writeString(Value);
        out.writeString(Comment);
        out.writeInt(OrderNumber);
        out.writeString(Question1);
        out.writeString(Instruction);
        out.writeString(ShortName);
        out.writeInt(Minimum);
        out.writeInt(Maximum);
        out.writeByte((byte) (Required ? 1 : 0));
        out.writeInt(Decimals);
        out.writeString(Preffix);
        out.writeString(Suffix);
        out.writeByte((byte) (Randomize ? 1 : 0));
        out.writeByte((byte) (IncludeScoring ? 1 : 0));
        out.writeByte((byte) (DisplayImages ? 1 : 0));
        out.writeInt(MinAnswers);
        out.writeInt(MaxAnswers);
        out.writeString(LeftLabel);
        out.writeString(RightLabel);
        out.writeByte((byte) (ImageAboveText ? 1 : 0));
        out.writeString(DefaultDate);
        out.writeInt(DateTypeID);
        out.writeString(DateTypeName);
        out.writeInt(CatalogID);
        out.writeString(CatalogElements);
        out.writeString(Condition);
        out.writeString(Valu);
        out.writeString(SendTo);
        out.writeString(Answer);
        out.writeString(Image);
        out.writeString(Options);
        out.writeString(OtherOption);
        out.writeByte((byte) (Hidden ? 1 : 0));
    }

    public static final Parcelable.Creator<Questions> CREATOR = new Parcelable.Creator<Questions>() {
        public Questions createFromParcel(Parcel in) {
            return new Questions(in);
        }

        public Questions[] newArray(int size) {
            return new Questions[size];
        }
    };



}
