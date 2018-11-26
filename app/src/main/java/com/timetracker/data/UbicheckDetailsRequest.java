package com.timetracker.data;

import java.util.Date;

public class UbicheckDetailsRequest {
	public int UbicheckDetailID;
	public int ElementID;
	public int DeviceID;
	public double Lat;
	public double Lon;
	public Date Date;
	public int BiometricID;
	public UbicheckDetailsRequest(int UbicheckDetailID, int ElementID,int DeviceID, double Lat, double Lon,Date Date,int BiometricID){
	    this.UbicheckDetailID = UbicheckDetailID;
	    this.ElementID = ElementID;
	    this.DeviceID = DeviceID;
	    this.Lat = Lat;
	    this.Lon = Lon;
	    this.Date = Date;
	    this.BiometricID = BiometricID;
	}
}
