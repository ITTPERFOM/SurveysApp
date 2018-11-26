package com.timetracker.data;

import java.util.Date;

public class UbicheckRequest {
	public int Status;
	public int DeviceID;
	public double Latitude;
	public double Longitude;
	public Date Date;
	public int BranchID;
	public int BiometricID;
	public int IsMock;
	public UbicheckRequest(int Status, int DeviceID,double Latitude,double Longitude,Date Date,int BranchID,int BiometricID,int IsMock){
	    this.Status = Status;
	    this.DeviceID = DeviceID;
	    this.Latitude = Latitude;
	    this.Longitude = Longitude;
	    this.Date = Date;
	    this.BranchID = BranchID;
	    this.BiometricID = BiometricID;
	    this.IsMock = IsMock;
	}
}
