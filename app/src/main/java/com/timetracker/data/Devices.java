package com.timetracker.data;

public class Devices {
	public String Name;
	public String Code;
	public String DeviceTypeID;
	public int DeviceID;
	public int Status;
	public int UsesFormSelection;
	public int UsesFormWithUbicheck;
	public int UsesClientValidation;
	public int UsesCreateBranch;
	public int UsesUbicheckDetails;
	public int UsesBiometric;
	public int UsesKioskMode;
	public int KioskBranchID;
	public int ImageWareRegister;
	public int BiometricID;
	public String Account;
	public int Level;
	public Devices(String Name, String Code,String DeviceTypeID,int DeviceID,int Status,int UsesFormSelection, int UsesFormWithUbicheck, int UsesClientValidation, int UsesCreateBranch, int UsesUbicheckDetails,int UsesBiometric,int UsesKioskMode,int KioskBranchID,int ImageWareRegister, int BiometricID,String Account,int Level){
	    this.Name = Name;
	    this.Code = Code;
	    this.DeviceTypeID = DeviceTypeID;
	    this.DeviceID = DeviceID;
	    this.Status = Status;
	    this.UsesFormSelection = UsesFormSelection;
	    this.UsesFormWithUbicheck = UsesFormWithUbicheck;
	    this.UsesClientValidation = UsesClientValidation;
	    this.UsesCreateBranch = UsesCreateBranch;
	    this.UsesUbicheckDetails = UsesUbicheckDetails;
	    this.UsesBiometric = UsesBiometric;
	    this.UsesKioskMode = UsesKioskMode;
	    this.KioskBranchID = KioskBranchID;
	    this.ImageWareRegister = ImageWareRegister;
	    this.BiometricID = BiometricID;
	    this.Account = Account;
	    this.Level = Level;
	}
}
