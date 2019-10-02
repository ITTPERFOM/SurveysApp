package com.timetracker.surveys;

import org.json.JSONObject;

import com.timetracker.business.ConnectionMethods;
import com.timetracker.data.Devices;
import com.timetracker.sqlite.MySQLiteHelper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SplashScreenActivity extends Activity {

    public MySQLiteHelper db = new MySQLiteHelper(SplashScreenActivity.this);
    public String MockApps = "";
    public String ThirdPartyApps = "";
    private static TextView txtSplashMessage;

    //================================================================================
    // Create Activity
    //================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        txtSplashMessage = (TextView)findViewById(R.id.txtSplashMessage);
        myHandler.sendEmptyMessage(1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                myHandler.sendEmptyMessage(2);
                if (ContextCompat.checkSelfPermission(SplashScreenActivity.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(SplashScreenActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(SplashScreenActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    int isPermited = 0;
                    ActivityCompat.requestPermissions(SplashScreenActivity.this,new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},isPermited);
                }else{
                    ValidateDevice();
                }
            }
        }, 1000);
    }

    //================================================================================
    // Destroy Activity
    //================================================================================

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    //================================================================================
    // Assign Message
    //================================================================================

    private static final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch(what) {
                case 1: updateMessage("Cargando..."); break;
                case 2: updateMessage("Validando Permisos..."); break;
                case 3: updateMessage("Verificando Dispositivo..."); break;
                case 4: updateMessage("Enviando Informacion..."); break;
                case 5: updateMessage("Procesando Respuesta..."); break;
                case 6: updateMessage("Sin conexion..."); break;
                case 7: updateMessage("Eliminando Datos..."); break;
                case 8: updateMessage("Verificando Version..."); break;
                case 9: updateMessage("Solicitando Actualizacion..."); break;
                case 10: updateMessage("Verificacion App..."); break;
            }
        }
    };

    private static void updateMessage(String strMessage) {
        txtSplashMessage.setText(strMessage);
    }

    //================================================================================
    // Request Permissions Result
    //================================================================================

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        ValidateDevice();
    }

    //================================================================================
    // Validate Device
    //================================================================================

    public void ValidateDevice(){
        myHandler.sendEmptyMessage(10);
        VerifyMockLocationApps();
        myHandler.sendEmptyMessage(3);
        Devices Device = db.GetDevice();
        int DeviceID = 0;
        boolean DeviceExists = false;
        String VersionName = "";
        PackageInfo pInfo = null;
        if(Device != null && Device.Status == 2){
            DeviceExists = true;
            DeviceID = Device.DeviceID;
        }
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            VersionName = pInfo.versionName;
        } catch (NameNotFoundException e) {
        }
        new AsyncCheckDevice(DeviceExists,VersionName).execute("/Devices?DeviceID=" + DeviceID + "&AppVersion=" + VersionName +  "&MockApps=" + MockApps);
    }

    //================================================================================
    // Verify Mock Location Apps
    //================================================================================

    public void VerifyMockLocationApps(){
        int count = 0;
        String AppNames = "";
        PackageManager pm = SplashScreenActivity.this.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;
                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i].equals("android.permission.ACCESS_MOCK_LOCATION") && !applicationInfo.packageName.equals(SplashScreenActivity.this.getPackageName())) {
                            count++;
                            if(!AppNames.equals("")){
                                AppNames+=",";
                            }
                            AppNames+= applicationInfo.packageName;
                        }
                    }
                }
            } catch (NameNotFoundException e) {
            }
        }
        if(count > 0){
            MockApps = AppNames;
        }
    }

    //================================================================================
    // Verify Banned Apps
    //================================================================================

    public boolean VerifyBannedApps(){
        try{
            if(!ThirdPartyApps.equals("")){
                List<String> BannedAppList = new ArrayList<String> ();
                if(ThirdPartyApps.contains("|")){
                    String[] arrayThirdPartyApps = ThirdPartyApps.split(Pattern.quote("|"));
                    if(arrayThirdPartyApps.length > 0) {
                        for (int j = 0; j < arrayThirdPartyApps.length; j++) {
                            BannedAppList.add(arrayThirdPartyApps[j]);
                        }
                    }else{
                        return true;
                    }
                }else{
                    BannedAppList.add(ThirdPartyApps);
                }
                PackageManager pm = SplashScreenActivity.this.getPackageManager();
                List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                for (ApplicationInfo applicationInfo : packages) {
                    for(String BannedApp : BannedAppList){
                        if(applicationInfo.packageName.equals(BannedApp)){
                            Intent intent = new Intent(Intent.ACTION_DELETE);
                            intent.setData(Uri.parse("package:" + BannedApp));
                            startActivity(intent);
                            return false;
                        }
                    }
                }
                return true;
            }else{
                return true;
            }
        }catch (Exception ex){
            return true;
        }
    }

    //================================================================================
    // Redirect to Home
    //================================================================================

    public void RedirectToHome(){
        if(VerifyBannedApps()){
            Intent i = new Intent(getBaseContext(), HomeActivity.class);
            startActivity(i);
            finish();
        }else{
            finish();
        }
    }

    //================================================================================
    // Device Check Method
    //================================================================================

    private class AsyncCheckDevice extends AsyncTask<String, Void, String> {
        Boolean DeviceExists;
        String VersionName;
        public AsyncCheckDevice(Boolean DeviceExists,String VersionName) {
            this.DeviceExists = DeviceExists;
            this.VersionName = VersionName;
        }
        @Override
        protected String doInBackground(String... urls) {
            myHandler.sendEmptyMessage(4);
            return ConnectionMethods.GET(SplashScreenActivity.this, urls[0]);
        }
        /* (non-Javadoc)
      * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
      */
        @Override
        protected void onPostExecute(String result) {
            myHandler.sendEmptyMessage(5);
            if(result.startsWith("Error:")){
                myHandler.sendEmptyMessage(6);
                RedirectToHome();
            }else{
                try
                {
                    JSONObject JO = new JSONObject(result);
                    if(DeviceExists && !JO.getBoolean("IsValid"))
                    {
                        myHandler.sendEmptyMessage(7);
                        db.deleteQuestionOptions();
                        db.deleteQuestionSentences();
                        db.deleteQuestions();
                        db.deleteAllSurveys();
                        db.deleteDevice();
                        db.deleteSelectedSurveys();
                        db.deleteBiometrics();
                        RedirectToHome();
                    }else{
                        myHandler.sendEmptyMessage(8);
                        if(DeviceExists){
                            Devices Device = db.GetDevice();
                            if(JO.getBoolean("UsesFormSelection")){
                                Device.UsesFormSelection = 1;
                            }else{
                                Device.UsesFormSelection = 0;
                            }
                            if(JO.getBoolean("UsesFormWithUbicheck")){
                                Device.UsesFormWithUbicheck = 1;
                            }else{
                                Device.UsesFormWithUbicheck = 0;
                            }
                            if(JO.getBoolean("UsesClientValidation")){
                                Device.UsesClientValidation = 1;
                            }else{
                                Device.UsesClientValidation = 0;
                            }
                            if(JO.getBoolean("UsesCreateBranch")){
                                Device.UsesCreateBranch = 1;
                            }else{
                                Device.UsesCreateBranch = 0;
                            }
                            if(JO.getBoolean("UsesUbicheckDetails")){
                                Device.UsesUbicheckDetails = 1;
                            }else{
                                Device.UsesUbicheckDetails = 0;
                            }
                            if(JO.getBoolean("UsesBiometric")){
                                Device.UsesBiometric = 1;
                            }else{
                                Device.UsesBiometric = 0;
                                Device.BiometricID = 0;
                            }
                            if(JO.getBoolean("UsesKioskMode")){
                                Device.UsesKioskMode = 1;
                                Device.ImageWareRegister = 0;
                                Device.BiometricID = 0;
                            }else{
                                Device.UsesKioskMode = 0;
                                db.deleteBiometrics();
                                if(Device.BiometricID == 0){
                                    Device.ImageWareRegister = 0;
                                }
                            }
                            Device.KioskBranchID = JO.getInt("KioskBranchID");
                            Device.Account = JO.getString("Account");
                            Device.Name = JO.getString("Name");
                            ThirdPartyApps = JO.getString("ThirdPartyApps");
                            db.updateDevice(Device);
                        }
                        float ServerVersion = Float.parseFloat(JO.getString("LatestVersion"));
                        float DeviceVersion = Float.parseFloat(VersionName);
                        if(ServerVersion > DeviceVersion){
                            ShowOldVersionWarning();
                        }else{
                            RedirectToHome();
                        }
                    }
                }
                catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                    RedirectToHome();
                }
            }
        }
    }

    //================================================================================
    // Device Check Method
    //================================================================================

    public void ShowOldVersionWarning(){
        myHandler.sendEmptyMessage(9);
        if(!isFinishing()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Version obsoleta de iTT Formax Detectada")
                    .setMessage("Es necesario tener la ultima version de iTT Formax ¿Desea Actualizar Ahora?")
                    .setCancelable(false)
                    .setPositiveButton("Si",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            final String appPackageName = getPackageName();
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                            finish();
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

}
