package com.timetracker.data;

public class BranchItem {
    public int BranchID;
    public String Name;
    public double Latitude;
    public double Longitude;
    public String Identifier;
    public String TitularName;
    public String BussinessType;

    public BranchItem(int BranchID,String Name,double Latitude,double Longitude,String Identifier,String TitularName,String BussinessType){
        this.BranchID = BranchID;
        this.Name = Name;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Identifier = Identifier;
        this.TitularName = TitularName;
        this.BussinessType = BussinessType;
    }
}
