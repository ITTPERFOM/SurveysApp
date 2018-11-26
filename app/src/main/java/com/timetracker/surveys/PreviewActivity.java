package com.timetracker.surveys;


import java.util.ArrayList;
import java.util.List;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
 
public class PreviewActivity extends Activity {

	private SeekbarWithIntervals SeekbarWithIntervals = null;
	private Button scanBtn;
	private TextView formatTxt, contentTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        
        scanBtn = (Button)findViewById(R.id.scan_button);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);

        scanBtn.setOnClickListener(new ButtonReadCodeBar());

        
        List<String> seekbarIntervals = getIntervals();
		getSeekbarWithIntervals().setIntervals(seekbarIntervals);
        
    		}
    @SuppressWarnings("serial")
	private List<String> getIntervals() {
		return new ArrayList<String>() {{
			add("1");
			add("aaa");
			add("3");
			add("bbb");
			add("5");
			add("ccc");
			add("7");
			add("ddd");
			add("9");
		}};
	}

	private SeekbarWithIntervals getSeekbarWithIntervals() {
		if (SeekbarWithIntervals == null) {
			SeekbarWithIntervals = (SeekbarWithIntervals) findViewById(R.id.seekbarWithIntervals);
		}
		
		return SeekbarWithIntervals;
	}
    
    
    
    public class ButtonReadCodeBar implements OnClickListener 
    {
    	@Override
    	public void onClick(View v) {
    		// TODO Auto-generated method stub
    		if(v.getId()==R.id.scan_button){
    			//scan
    			IntentIntegrator scanIntegrator = new IntentIntegrator(PreviewActivity.this);
    			scanIntegrator.initiateScan();

    			}
    	}
    	    	
    }
    
    
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve scan result
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null) {
			//we have a result
			String scanContent = scanningResult.getContents();
					String scanFormat = scanningResult.getFormatName();
					formatTxt.setText("FORMAT: " + scanFormat);
					contentTxt.setText("CONTENT: " + scanContent);
			}
		else{
		    Toast toast = Toast.makeText(getApplicationContext(), 
		        "No scan data received!", Toast.LENGTH_SHORT);
		    toast.show();
		}


		}

	
    

    }
 
   