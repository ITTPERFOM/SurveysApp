package com.timetracker.surveys;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.timetracker.business.ConnectionMethods;
import com.timetracker.business.DialogMethods;
import com.timetracker.data.Biometrics;
import com.timetracker.data.Devices;
import com.timetracker.sqlite.MySQLiteHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.timetracker.surveys.SurveyActivity.calculateInSampleSize;

public class VacationActivity extends AppCompatActivity {


    String url = "https://prenom.timetracker.com.mx/sw_vacation_request/";
    ImageButton search;
    LinearLayout buttons;
    String Name;
    LottieAnimationView LoadAnimation;
    View view;
    int BiometricID;
    EditText txtName;
    Button btn_return;
    WebView webView ;
    public String _CameraImagePath;
    private MySQLiteHelper db = new MySQLiteHelper(VacationActivity.this);

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation);

        view = findViewById(R.id.view);

        search = findViewById(R.id.search_user);
        buttons = findViewById(R.id.Buttons);
        txtName = findViewById(R.id.txtName);
        webView = findViewById(R.id.WebView);
        btn_return = findViewById(R.id.btn_return);

        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        Devices device = db.GetDevice();

        _CameraImagePath = Environment.getExternalStorageDirectory() + "/Imagen.jpg";

        // Screen Configuration
        getSupportActionBar().hide();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = txtName.getText().toString();

                if(!Name.isEmpty()){
                    InputMethodManager imm = (InputMethodManager) getSystemService(VacationActivity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    new AsyncSearchClient().execute("/Biometrics?Name=" + Name + "&DeviceID=" + device.DeviceID);

                }
            }
        });

    }

    private class AsyncSearchClient extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return ConnectionMethods.GET(VacationActivity.this, urls[0]);
        }
        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(String result) {
            buttons.removeAllViews();
            if(result.startsWith("Error:")){
                DialogMethods.showInformationDialog(VacationActivity.this, "Ocurrio un Error", result,null);
            }else{
                try
                {
                    JSONArray JR = new JSONArray(result);
                    int x = 0;
                    if(JR.length() > 0){
                        for(int i=0;i < JR.length();i++)
                        {
                            JSONObject JO = (JSONObject)JR.getJSONObject(i);
                            final int ID = JO.getInt("BiometricID");
                            final String Name = JO.getString("Name");
                            final String payrollNumber = JO.getString("PayrollNumber");
                            Button newButton = new Button(VacationActivity.this);
                            if(payrollNumber.equals("null")){
                                newButton.setText(Name);
                            }else {
                                newButton.setText(Name + "\n"+ "Número de nómina: " + payrollNumber);
                            }
                            newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_user_sport));
                            newButton.setTextColor(getResources().getColor(R.color.blackSportWorld));
                            LinearLayout.LayoutParams tdf = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            tdf.setMargins(0, 10, 0, 10);
                            newButton.setLayoutParams(tdf);
                            newButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        VerifyPersonWithLuxand(ID);
                                }
                            });
                            LinearLayout linLayout = new LinearLayout(VacationActivity.this);
                            linLayout.addView(newButton);
                            buttons.addView(linLayout);
                        }
                        buttons.setVisibility(View.VISIBLE);
                    }
                }
                catch (Exception ex) {
                    DialogMethods.showErrorDialog(VacationActivity.this, "Ocurrio un error al momento de utilizar Ubicheck. Info: " + ex.toString(),"Activity:UbicheckNew | Method:AsyncSearchClient | Error:" + ex.toString());
                }
            }
        }
    }

    public void addRandomImage(Button newButton,int Option){
        switch(Option) {
            case 0:
                newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_user_sport));
                break;
            case 1:
                newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_tennis));
                break;
            case 2:
                newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_training));
                break;
            case 3:
                newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_swimming));
                break;
            case 4:
                newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_running));
                break;
            case 5:
                newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_medal));
                break;
            case 6:
                newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_karate));
                break;
            case 7:
                newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_cycling));
                break;
            case 8:
                newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_swimmingtwo));
                break;
            case 9:
                newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_runningtwo));
                break;
            default:
                newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_user_sport));
        }
    }

    public void VerifyPersonWithLuxand(int biometricID) {
        BiometricID = biometricID;
        StartAnimation();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        File file = new File(_CameraImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        //Select Photo
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, 0);
    }

    private void StartAnimation() {
        LoadAnimation = (LottieAnimationView) findViewById(R.id.load);
        LoadAnimation.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
    }

    private void HideAnimation(){
        LoadAnimation = (LottieAnimationView) findViewById(R.id.load);
        LoadAnimation.setVisibility(View.INVISIBLE);
        view.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            switch (resultCode) {
                case 0:
                    break;
                case -1:
                    onPhotoTaken();
                    break;
            }
        } catch (Exception e) {
            Toast toas1t = Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
            toas1t.show();
        }
    }

    protected void onPhotoTaken() {
        String image = decodeFile(_CameraImagePath);
        new UbicheckImageRequest(BiometricID,image).execute();
    }


    public String encoder(String FilePath) {
        String encodedString;
        Bitmap bitmap = BitmapFactory.decodeFile(FilePath);

        // encode base64 from image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        encodedString = Base64.encodeToString(b, Base64.URL_SAFE | Base64.NO_WRAP);
        return encodedString;
    }

    public  String decodeFile(String path) {
        int orientation;
        try {
            if (path == null) {
                return null;
            }
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = calculateInSampleSize(o, 580, 680);
            o.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeFile(path, o);
            ExifInterface exif = new ExifInterface(path);
            File fdelete = new File(path);
            if (fdelete.exists()) {
                fdelete.delete();
            }
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix m = new Matrix();
            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
                Bitmap rotated = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotated.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                return Base64.encodeToString(b, Base64.DEFAULT);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
                Bitmap rotated = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotated.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                return Base64.encodeToString(b, Base64.DEFAULT);
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                Bitmap rotated = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotated.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                return Base64.encodeToString(b, Base64.DEFAULT);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }
    private class UbicheckImageRequest extends AsyncTask<String, Void, String> {
        int BiometricID;
        String encodedImage;

        public UbicheckImageRequest(int BiometricID,String encodedImage) {
            this.BiometricID = BiometricID;
            this.encodedImage = encodedImage;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject item = new JSONObject();
                item.put("BiometricID", BiometricID);
                item.put("image", encodedImage);
                return ConnectionMethods.Post(VacationActivity.this, item.toString(), "/UbicheckImage", true);
            } catch (Exception e) {
                return "Error:" + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            HideAnimation();
            if (result.startsWith("Error")) {
                Toast.makeText(VacationActivity.this," Parece que el rostro introducido no coincide  " , Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject JO = new JSONObject(result);
                    if (JO.getBoolean("success")) {
                        btn_return.setVisibility(View.VISIBLE);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.loadUrl(url+BiometricID);
                        webView.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(VacationActivity.this, JO.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(VacationActivity.this, "Error al esperar servicio:" + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
