package com.lectorh;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.biomini.BioMiniAdnroid;
import com.android.biomini.IBioMiniCallback;
//import com.example.androiddemosample.R;




public class MainActivity extends Activity {
	
	// BioMini SDK variable 
		private static BioMiniAdnroid mBioMiniHandle; //find device
		
		private int ufa_res;
		private String errmsg = "Hi";
		private byte[] pImage = new byte[320 * 480];
		
		// Enroll Template Array 
		private byte[][] ptemplate1 = new byte[50][1024];
		// Enroll Template Size
		private int[][] ntemplateSize1 = new int[50][4];
		// Enroll User
		private String[] pEnrolledUser = new String[50];
		// Input Template Buffer
		private byte[] ptemplate2 = new byte[1024]; 
		// Input Template Size
		private int[] ntemplateSize2 = new int[4];
		// Quality of template
		private int[] nquality = new int[4];
		
		private int nenrolled = 0;
		
		private boolean isname = false;
		
	/*	private int sensitivity;
		private int timeout;
		private int securitylevel;
		private int fastmode;*/
		
		private int nsensitivity;
		private int ntimeout;
		private int nsecuritylevel;
		private int bfastmode;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        setContentView(R.layout.layout_main);
        
     // allocate SDK instance 
     		if(mBioMiniHandle == null) {
     			mBioMiniHandle = new BioMiniAdnroid(this);
     		}
     	// ====================================== Begin Event FindDevice ==================================================================		
    		((Button)findViewById(R.id.btnStart)).setOnClickListener(new View.OnClickListener() {        	
    			@Override
    			public void onClick(View v) {		
    				if(mBioMiniHandle == null) { // Check SDK initialized
    					Log.e(">==< Main Activity >==<", String.valueOf("BioMini SDK Handler with NULL!"));  	        
    		
    				}
    				else{
    	//  ================== Begin find BioMini device and request permission ======================================
    					ufa_res = mBioMiniHandle.UFA_FindDevice();
    					
    					errmsg = mBioMiniHandle.UFA_GetErrorString(ufa_res); 
    					TextView tv2 = (TextView)findViewById(R.id.txmessage2); 
    					tv2.setText("UFA_FindDevice res: " + errmsg); 
    	// =================== End find BioMini device and request permission ========================================
    					
    	// =================== SDK initialization ====================================================================
    					ufa_res = mBioMiniHandle.UFA_Init();
    					String errmsg1 = mBioMiniHandle.UFA_GetErrorString(ufa_res);
    					//String Serial = null;
    					
    					if(ufa_res == 0) {
    						
    						
    				// =========== Default Values ===========		
    						nsensitivity = 7; 
    						ntimeout = 10;
    						nsecuritylevel = 4;
    						bfastmode = 1;
    						
    						mBioMiniHandle.UFA_SetParameter(mBioMiniHandle.UFA_PARAM_SENSITIVITY, nsensitivity);
    						mBioMiniHandle.UFA_SetParameter(mBioMiniHandle.UFA_PARAM_TIMEOUT, ntimeout * 1000);
    						mBioMiniHandle.UFA_SetParameter(mBioMiniHandle.UFA_PARAM_SECURITY_LEVEL, nsecuritylevel);
    						mBioMiniHandle.UFA_SetParameter(mBioMiniHandle.UFA_PARAM_FAST_MODE, bfastmode);

    						// set callback
    						mBioMiniHandle.UFA_SetCallback(mBioMiniCallbackHandler);
    						
    						//Serial = mBioMiniHandle.UFA_GetSerialNumber(); // Get Serial number of device connected
    					
    					}//end if
    					
    					// get return code string
    					TextView tv3 = (TextView)findViewById(R.id.txmessage3); 
    					tv3.setText("UFA_Init res: " + errmsg1 ); //Show Message with serial number of device
    					((Button)findViewById(R.id.btnStart)).setTextColor(Color.WHITE);
    			
    	// ================ End SDK Initialization ==============================================================================
    	
    	// ================ Action Load Template Suprema =========================================================================
    					TextView tv4 = (TextView)findViewById(R.id.txmessage4);
    					
    					ufa_res = mBioMiniHandle.UFA_SetTemplateType(mBioMiniHandle.UFA_TEMPLATE_TYPE_ANSI378);
    					
    					errmsg = mBioMiniHandle.UFA_GetErrorString(ufa_res); 
    					tv4.setText("UFA_SetTemplateType(ANSI) res: " + errmsg);
    					
    					if(ufa_res == 0) {
    						int[] nValue = new int[4];
    			    		ufa_res = mBioMiniHandle.UFA_GetTemplateType(nValue);
    						//tv3.setText("TemplateType("+nValue[0]+")");
    				
    					}
    	// ======================== End action Load Template ANSI =================================================================
    				
    	// ======================== Begin Matching ====================================================================================
    					
    					// capture fingerprint image
    					mBioMiniHandle.UFA_CaptureSingle(pImage);
    					
    					if(ufa_res != 0) {						
    						errmsg = mBioMiniHandle.UFA_GetErrorString(ufa_res); 
    						TextView tv = (TextView)findViewById(R.id.txmessage); 
    						tv.setText("UFA_CaptureSingle res: " + errmsg);
    						return;
    					}
    					
    					mBioMiniCallbackHandler.onCaptureCallback(pImage, 320, 480, 500, true);
            	        
    					// extract fingerprint template from captured image
    					ufa_res = mBioMiniHandle.UFA_ExtractTemplate(ptemplate2, ntemplateSize2, nquality, 1024);
    					
    					
    					
    					if(ufa_res != 0) {						
    						errmsg = mBioMiniHandle.UFA_GetErrorString(ufa_res); 
    						TextView tv = (TextView)findViewById(R.id.txmessage); 
    						tv.setText("UFA_ExtractTemplate res: " + errmsg);
    						return;
    					}
    					
    					int[] nVerificationResult = new int[4];
    					nVerificationResult[0] = 0;
    					
    					for(int i=0;i<nenrolled;i++)
    					{
    						// try 1:1 template matching 
    						ufa_res = mBioMiniHandle.UFA_Verify(ptemplate1[i], ntemplateSize1[i][0], ptemplate2, ntemplateSize2[0], nVerificationResult);
    						if(nVerificationResult[0] == 1) {
    							TextView tv = (TextView)findViewById(R.id.txmessage );
    							tv.setText("Match with: " +pEnrolledUser[i]);
    							break;
    						}
    					}
    					
    					if(ufa_res != 0) {						
    						errmsg = mBioMiniHandle.UFA_GetErrorString(ufa_res); 
    						TextView tv = (TextView)findViewById(R.id.txmessage); 
    						tv.setText("UFA_Verify res: " + errmsg);
    						return;
    					}
    					
    					TextView tv = (TextView)findViewById(R.id.txmessage);
    					if(nVerificationResult[0] != 1) {
    						tv.setText("matching result: not matched");
    						
    						TextView tv1 = (TextView)findViewById(R.id.txmessage );
    						tv1.setText("Identification fail");
    					}
    // ==================== End Matching ===========================================================================
    				} //end if
    			} //end OnClick
    		}); //end btnStart
    		
    		
    // ====================== Begin Event btnName ==================================================================	
    		((Button)findViewById(R.id.btnName)).setOnClickListener(new View.OnClickListener() {        	
    			@Override
    			public void onClick(View v) {		
    				Context mContext = getApplicationContext();
    	            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
    	            final View layout = inflater.inflate(R.layout.custom_dialog,(ViewGroup) findViewById(R.id.layout_root));
    	                   
    	            AlertDialog.Builder aDialog = new AlertDialog.Builder(MainActivity.this);
    	            aDialog.setTitle("Insert your name");
    	            aDialog.setView(layout);
    	            
    	            aDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    	            	public void onClick(DialogInterface dialog, int which) {
    	            		EditText tv = (EditText)layout.findViewById(R.id.EditTest01); 
    	            		pEnrolledUser[nenrolled] = tv.getText().toString();
    	            		isname = true;
    	            		}
    	            });
    	            aDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	            	public void onClick(DialogInterface dialog, int which) {
    	            	}
    	            });
    	            AlertDialog ad = aDialog.create(); 
    	            ad.show();
    			}
    		});
    		
    		((Button)findViewById(R.id.btndelete)).setOnClickListener(new View.OnClickListener() {        	
    			@Override
    			public void onClick(View v) {
    				for(int i=0; i<nenrolled; i++) {
    					Arrays.fill(ptemplate1[i], 0, 1024, (byte)0);	
    					ntemplateSize1[i][0] = 0;
    				}
    				
    				nenrolled = 0;
    			}
    		});
    		
    		((Button)findViewById(R.id.btnenroll)).setOnClickListener(new View.OnClickListener() {        	
    			@Override
    			public void onClick(View v) {		
    				if(mBioMiniHandle == null) {
    					Log.e(">==< Main Activity >==<", String.valueOf("BioMini SDK Handler with NULL!"));     	        
    		
    				}
    				else{
    					if(nenrolled == 50) { 
    						TextView tv = (TextView)findViewById(R.id.txmessage); 
    						tv.setText("out of memory");
    						return;
    					}		            
    					
    					if(!isname) {
    						TextView tv = (TextView)findViewById(R.id.txmessage); 
    						tv.setText("there is no inserted user name");
    						return;
    					}
    					
    					// capture fingerprint image
    					ufa_res = mBioMiniHandle.UFA_CaptureSingle(pImage);
    				
    					
    					if(ufa_res != 0) {					
    						errmsg = mBioMiniHandle.UFA_GetErrorString(ufa_res); 
    						TextView tv = (TextView)findViewById(R.id.txmessage); 
    						tv.setText("UFA_CaptureSingle res: " + errmsg);
    						return;
    					}
    					
    					mBioMiniCallbackHandler.onCaptureCallback(pImage, 320, 480, 500, true);
            	        
    					// extract fingerpirnt template from captured image
    					// extracted template is saved in memory (ptemplate1: 2-D byte array)
    					ufa_res = mBioMiniHandle.UFA_ExtractTemplate(ptemplate1[nenrolled], ntemplateSize1[nenrolled], nquality, 1024);
    					
    					if(ufa_res != 0) {	
    						errmsg = mBioMiniHandle.UFA_GetErrorString(ufa_res); 
    						TextView tv = (TextView)findViewById(R.id.txmessage2); 
    						tv.setText("UFA_ExtractTemplate res: " + errmsg);
    						return;
    					} else {
    						errmsg = mBioMiniHandle.UFA_GetErrorString(ufa_res); 
    						TextView tv = (TextView)findViewById(R.id.txmessage2); 
    						tv.setText("UFA_ExtractTemplate res: " + errmsg);
    						
    						TextView tv2 = (TextView)findViewById(R.id.txmessage );
    						tv2.setText(pEnrolledUser[nenrolled] + " is enrolled");
    						
    						nenrolled++;
    						isname = false;
    						
    						TextView tv3 = (TextView)findViewById(R.id.txmessage2 );
    						tv3.setText("");
    						TextView tv4 = (TextView)findViewById(R.id.txmessage3 );
    						tv4.setText("");
    						TextView tv5 = (TextView)findViewById(R.id.txmessage4 );
    						tv5.setText("");
    					}
    				}
    			}
    		});
    		
    		((Button)findViewById(R.id.btnverify)).setOnClickListener(new View.OnClickListener() {        	
    			@Override
    			public void onClick(View v) {
    				if(mBioMiniHandle == null) {
    					Log.e(">==< Main Activity >==<", String.valueOf("BioMini SDK Handler with NULL!"));     	        
    		
    				}
    				else{
    					// capture fingerprint image
    					mBioMiniHandle.UFA_CaptureSingle(pImage);
    					
    					if(ufa_res != 0) {						
    						errmsg = mBioMiniHandle.UFA_GetErrorString(ufa_res); 
    						TextView tv = (TextView)findViewById(R.id.txmessage); 
    						tv.setText("UFA_CaptureSingle res: " + errmsg);
    						return;
    					}
    					
    					mBioMiniCallbackHandler.onCaptureCallback(pImage, 320, 480, 500, true);
            	        
    					// extract fingerprint template from captured image
    					ufa_res = mBioMiniHandle.UFA_ExtractTemplate(ptemplate2, ntemplateSize2, nquality, 1024);
    					
    					if(ufa_res != 0) {						
    						errmsg = mBioMiniHandle.UFA_GetErrorString(ufa_res); 
    						TextView tv = (TextView)findViewById(R.id.txmessage); 
    						tv.setText("UFA_ExtractTemplate res: " + errmsg);
    						return;
    					}
    					
    					int[] nVerificationResult = new int[4];
    					nVerificationResult[0] = 0;
    					
    					for(int i=0;i<nenrolled;i++)
    					{
    						// try 1:1 template matching 
    						ufa_res = mBioMiniHandle.UFA_Verify(ptemplate1[i], ntemplateSize1[i][0], ptemplate2, ntemplateSize2[0], nVerificationResult);
    						if(nVerificationResult[0] == 1) {
    							TextView tv = (TextView)findViewById(R.id.txmessage );
    							tv.setText("Match with: " +pEnrolledUser[i]);
    							break;
    						}
    					}
    					
    					if(ufa_res != 0) {						
    						errmsg = mBioMiniHandle.UFA_GetErrorString(ufa_res); 
    						TextView tv = (TextView)findViewById(R.id.txmessage); 
    						tv.setText("UFA_Verify res: " + errmsg);
    						return;
    					}
    					
    					TextView tv = (TextView)findViewById(R.id.txmessage);
    					if(nVerificationResult[0] != 1) {
    						tv.setText("matching result: not matched");
    						
    						TextView tv2 = (TextView)findViewById(R.id.txmessage );
    						tv2.setText("Identification fail");
    					}
    				}
    				
    			} 
    		});
    		
   // ======================================== Begin Event Un-Initialization =========================================================	
    		((Button)findViewById(R.id.btnuninit)).setOnClickListener(new View.OnClickListener() {        	
    			@Override
    			public void onClick(View v) {		
    				if(mBioMiniHandle == null) {
    					Log.e(">==< Main Activity >==<", String.valueOf("BioMini SDK Handler with NULL!"));     	        
    		
    				}
    				else{
    					// uninitialize SDK
    					ufa_res = mBioMiniHandle.UFA_Uninit();
    					
    					String errmsg1 = mBioMiniHandle.UFA_GetErrorString(ufa_res); 
    					
    					if(ufa_res == 0) {
    						/*sensitivity.setProgress(0);
    						timeout.setProgress(0);
    						securitylevel.setProgress(0);
    						fastmode.setChecked(false);
    						
    						((Button)findViewById(R.id.btnsuprema)).setTextColor(Color.BLACK);
    						((Button)findViewById(R.id.btntypeiso)).setTextColor(Color.BLACK);
    						((Button)findViewById(R.id.btntypeansi)).setTextColor(Color.BLACK);*/
    						
    						((Button)findViewById(R.id.btnStart)).setTextColor(Color.BLACK);
    						nsensitivity = 0;
    						ntimeout = 0;
    						nsecuritylevel = 0;
    						bfastmode = 0;
    					}
    					
    					TextView tv1 = (TextView)findViewById(R.id.txmessage); 
    					TextView tv2 = (TextView)findViewById(R.id.txmessage2); 
    					TextView tv3 = (TextView)findViewById(R.id.txmessage3); 
    					tv1.setText("UFA_Uninit res: " + errmsg1);
    					tv2.setText("");
    					tv3.setText("");
    				}
    			}
    		}); 
    }//End OnCreate

 // Callback	
 	private final IBioMiniCallback mBioMiniCallbackHandler = new IBioMiniCallback() {
 		@Override
 		public void onCaptureCallback(final byte[] capturedimage, int width, int height, int resolution, boolean bfingeron) {
 			Log.e(">==< Main Activity >==<", String.valueOf("onCaptureCallback called!" + " width:" + width + " height:" + height + " fingerOn:" + bfingeron));  			
 			runOnUiThread(new Runnable() {
                 @Override
                  public void run() {
                 	byte[] Bits = new byte[320*480*4];
         		    for(int i=0;i<320*480;i++)
         		    {
         		        Bits[i*4] =  
         		        Bits[i*4+1] = 
         		        Bits[i*4+2] = capturedimage[i]; 
         		        Bits[i*4+3] = -1;
         		    }
         		    Bitmap bm = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
         		    bm.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));
         		    ImageView vv = (ImageView) findViewById(R.id.imageView1);
         	        vv.setImageBitmap(bm);

         	        vv.invalidate();
                 }
             });
 		}
 		@Override
		public void onErrorOccurred(String msg) {
			
		}		
	};
	
	@Override
	protected void onStop() {
		
		if(mBioMiniHandle == null) {
			Log.e(">==< Main Activity >==<", String.valueOf("BioMini SDK Handler with NULL!"));     	        

		} 
		else {
			mBioMiniHandle.UFA_AbortCapturing();
			
			mBioMiniHandle.UFA_Uninit();
		} 
		
		super.onStop();
		
	}
}//End ActivityMain
