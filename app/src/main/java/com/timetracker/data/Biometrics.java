package com.timetracker.data;

public class Biometrics {
	public int BiometricID;
	public String Name;
	public int LastConecction;
	
	public Biometrics(int BiometricID, String Name, int LastConecction){
	    this.BiometricID = BiometricID;
	    this.Name = Name;
	    this.LastConecction = LastConecction;
	}
}
