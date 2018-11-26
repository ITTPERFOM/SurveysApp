package com.timetracker.surveys;

import java.io.File;
import java.io.FileOutputStream;

import com.timetracker.data.Questions;
import com.timetracker.sqlite.MySQLiteHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SignatureActivity extends Activity { 

    LinearLayout mContent;
    signature mSignature;
    Button mClear, mGetSign, mCancel;
    public static String tempDir;
    public int count = 1;
    private Bitmap mBitmap;
    View mView;
    File mypath;
    public boolean useBitmap = false; 
    public float widthBitmap = 0;
    public float heightBitmap = 0;
    
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
    		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		setContentView(R.layout.activity_signature);
    		tempDir = Environment.getExternalStorageDirectory() + "/Survey_Signature.png";
    		mypath= new File(tempDir);
    		mContent = (LinearLayout) findViewById(R.id.linearLayout);
    		mContent.post(new Runnable() 
    	    {
    	        @Override
    	        public void run()
    	        {
    	        	if(useBitmap){
    	        		float widthView = mContent.getWidth();
        	        	float heightView = mContent.getHeight();
        	        	LayoutParams params = mContent.getLayoutParams();
        	        	
        	        	float imageSideRatio = widthBitmap / heightBitmap;
        	            float viewSideRatio = widthView / heightView;
        	            if (imageSideRatio >= viewSideRatio) {
        	            	params.width = Math.round(widthView);
        	            	params.height = (int)(params.width / imageSideRatio);
        	            } else {
        	                params.height = Math.round(heightView);
        	                params.width = (int)(params.height * imageSideRatio);
        	            }
    	            	mContent.setLayoutParams(params);
    	        	}
    	        }
    	    });
    		Intent myIntent = getIntent();
    		String sQuestionID = myIntent.getStringExtra("sQuestionID");
    		if(sQuestionID == null){
    			mSignature = new signature(this, null, false);
    			mSignature.setBackgroundColor(Color.WHITE);
    		}else{
    			MySQLiteHelper db = new MySQLiteHelper(getBaseContext());
    			Questions Question = db.getQuestion(Integer.parseInt(sQuestionID));
    			Bitmap BM = StringToBitMap(Question.Image);
    			BitmapDrawable DR = new BitmapDrawable(getResources(), BM);
    			mSignature = new signature(this, null, true);
    			mSignature.setBackground(DR);
    			useBitmap = true;
    			heightBitmap = BM.getHeight();
    			widthBitmap = BM.getWidth();
    			if(Question.QuestionTypeID == 20){
    				Question.Image = "";
    				db.updateQuestionImage(Question);
    			}
    			db.close();
    		}
    		mContent.addView(mSignature, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    		mClear = (Button)findViewById(R.id.clear);
    		mGetSign = (Button)findViewById(R.id.getsign);
    		mGetSign.setEnabled(false);
    		mCancel = (Button)findViewById(R.id.cancel);
    		mView = mContent;
    		mClear.setOnClickListener(new OnClickListener() 
    		{        
    			public void onClick(View v) 
    			{
    				Log.v("log_tag", "Panel Cleared");
    				mSignature.clear();
    				mGetSign.setEnabled(false);
    			}
    		});
    		mGetSign.setOnClickListener(new OnClickListener() 
    		{        
    			public void onClick(View v) 
    			{
                    mView.setDrawingCacheEnabled(true);
                    mSignature.save(mView);
                    Bundle b = new Bundle();
                    b.putString("status", "done");
                    Intent intent = new Intent();
                    intent.putExtras(b);
                    setResult(RESULT_OK,intent);   
                    finish();
                
    			}
    		});
    		mCancel.setOnClickListener(new OnClickListener() 
    		{        
    			public void onClick(View v) 
    			{
    				Log.v("log_tag", "Panel Canceled");
    				Bundle b = new Bundle();
    				b.putString("status", "cancel");
    				Intent intent = new Intent();
    				intent.putExtras(b);
    				setResult(RESULT_OK,intent);  
    				finish();
    			}
    		});
    	}
    	catch(Exception ex){
    		Toast.makeText(getApplicationContext(), ex.toString(),Toast.LENGTH_SHORT).show();
    	}
    }
    
    @Override
    protected void onDestroy() {
        Log.w("GetSignature", "onDestory");
        super.onDestroy();
    }
    
    public class signature extends View 
    {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs, boolean useImage) 
        {
            super(context, attrs);
            paint.setAntiAlias(true);
            if(useImage){
            	paint.setColor(Color.rgb(0, 70, 127));
            }else{
            	paint.setColor(Color.BLACK);
            }
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v) 
        {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if(mBitmap == null)
            {
                mBitmap =  Bitmap.createBitmap (mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);;
            }
            Canvas canvas = new Canvas(mBitmap);
            try 
            {
                FileOutputStream mFileOutStream = new FileOutputStream(mypath);
                v.draw(canvas); 
                mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream); 
                mFileOutStream.flush();
                mFileOutStream.close();
            }
            catch(Exception e) 
            { 
                Log.v("log_tag", e.toString()); 
            } 
        }

        public void clear() 
        {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) 
        {
            canvas.drawPath(path, paint);
        }

        @SuppressLint("ClickableViewAccessibility") @Override
        public boolean onTouchEvent(MotionEvent event) 
        {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) 
            {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                return true;

            case MotionEvent.ACTION_MOVE:

            case MotionEvent.ACTION_UP:

                resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) 
                {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    path.lineTo(historicalX, historicalY);
                }
                path.lineTo(eventX, eventY);
                break;

            default:
                debug("Ignored touch event: " + event.toString());
                return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string){
        }

        private void expandDirtyRect(float historicalX, float historicalY) 
        {
            if (historicalX < dirtyRect.left) 
            {
                dirtyRect.left = historicalX;
            } 
            else if (historicalX > dirtyRect.right) 
            {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) 
            {
                dirtyRect.top = historicalY;
            } 
            else if (historicalY > dirtyRect.bottom) 
            {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) 
        {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
    
    public Bitmap StringToBitMap(String encodedString){
	      try{
	        byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
	        Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
	        return bitmap;
	      }catch(Exception e){
	        e.getMessage();
	        return null;
	      }
	}
}










