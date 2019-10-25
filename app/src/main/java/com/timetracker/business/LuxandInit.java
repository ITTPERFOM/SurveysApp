package com.timetracker.business;

import android.content.Context;
import android.widget.Toast;

import com.luxand.FSDK;

public class LuxandInit {
    private static LuxandInit instance;

    public LuxandInit() {

    }

    public static LuxandInit getInstance(Context context) {
        if (instance == null) {
            startSDK(context);
            instance = new LuxandInit();
        }
        return  instance;
    }

    public  static void startSDK(Context context){
        try {
            int res = FSDK.ActivateLibrary("r+SutpWhDEDrYMnlgN+RHkAqGTl5MXDm9wwLO/t+glu1hX6OWo0Yb5j8E33vgUZ5Q9jIDVFN8B0FWd4G6qzZV/uLhEirVamEvJHVyTfoT+nwl2U/FJPVmX8G5u5cnf45wkntz2b1i743/79QhSoqa8OmyL89sLp8okwxd2s56F4=");
            FSDK.Initialize();
            FSDK.SetFaceDetectionParameters(false, false, 100);
            FSDK.SetFaceDetectionThreshold(5);
            if (res != FSDK.FSDKE_OK) {
                Toast.makeText(context, "Error activando FaceSDK: " + res , Toast.LENGTH_LONG).show();
            }
        }catch (Exception e) {
            Toast.makeText(context, "exception " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
