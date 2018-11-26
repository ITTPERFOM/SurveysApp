package com.timetracker.data;

public class Tracker {
	public int TrackerID ;
	public int DeviceID;
	public double Latitude;
	public double Longitude;
	public String Date;
	public Tracker(int TrackerID, int DeviceID,double Latitude,double Longitude,String Date){
	    this.TrackerID = TrackerID;
	    this.DeviceID = DeviceID;
	    this.Latitude = Latitude;
	    this.Longitude = Longitude;
	    this.Date = Date;
	}
}
