package com.timetracker.surveys;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Jasiel Lap on 6/6/2018.
 */

public class SimilarityPreview extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.similaritypreview);
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        Bundle b = getIntent().getExtras();
        String value = "";
        if(b != null)
            value = b.getString("Similitud");
        txtTitle.setText("Biometrico no coincide\nSimilitud:" + value);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width *.8),(int)(height *.8));
        Button btnExitView = (Button) findViewById(R.id.btnExitView);
        btnExitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimilarityPreview.this.finish();
            }
        });
        String _DataBaseImagePath = Environment.getExternalStorageDirectory() + "/_DataBaseImage.jpg";
        File file = new File(_DataBaseImagePath);
        Uri BaseUri = Uri.fromFile(file);
        ImageView imgBase = (ImageView) findViewById(R.id.imgBase);
        imgBase.setImageURI(BaseUri);

        String _CameraImagePath = Environment.getExternalStorageDirectory() + "/_CameraImage.jpg";
        File file2 = new File(_CameraImagePath);
        Uri CameraUri = Uri.fromFile(file2);
        ImageView imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        imgPhoto.setImageURI(CameraUri);
    }
}
