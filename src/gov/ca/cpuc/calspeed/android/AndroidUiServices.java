// Original work: Copyright 2009 Google Inc. All Rights Reserved.
//
// Modified work: The original source code comes from the NDT Android app
//                that is available from http://code.google.com/p/ndt/.
//                It's modified for the CalSPEED Android app by California 
//                State University Monterey Bay (CSUMB) on April 29, 2013.
//

package gov.ca.cpuc.calspeed.android;

import gov.ca.cpuc.calspeed.android.UiServices;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import gov.ca.cpuc.calspeed.android.R;

/**
 * Implementation of UiServices for Android.
 */
public class AndroidUiServices implements UiServices {
  private Message message;
  private Handler handler;
  private Context context;
  private boolean userStop;
  private String currentTask;
  

  /**
   * Constructor to get the handler from Android's UI Thread.
   * 
   * @param handler handler from main activity for dispatching messages
   */
  public AndroidUiServices(Context context, Handler handler) {
    this.handler = handler;
    this.context = context;
    this.userStop = false;
  }
	/**
	 * Adapter from JTextArea#append to UiServices#appendString.
	 */
	
	public static class TextOutputAdapter {
		private final int viewId;
		private final UiServices uiServices;

		/**
		 * @param viewId
		 *            UiServices constant to pass to appendString
		 */
		public TextOutputAdapter(UiServices uiServices, int viewId) {
			this.viewId = viewId;
			this.uiServices = uiServices;
		}

		public void append(String s) {
			uiServices.appendString(s, viewId);
		}
	}

  /**
   * {@inheritDoc}
   */
  @Override
  public void appendString(String str, int objectId) {
    switch (objectId) {
      case MAIN_VIEW:
        message = handler.obtainMessage(Constants.THREAD_MAIN_APPEND, str);
        handler.sendMessage(message);
        break;
      case STAT_VIEW:
        message = handler.obtainMessage(Constants.THREAD_STAT_APPEND, str);
        handler.sendMessage(message);
        break;
      case DIAG_VIEW:
        // Diagnosis view is redirected to Statistics view on Android.
        message = handler.obtainMessage(Constants.THREAD_STAT_APPEND, str);
        handler.sendMessage(message);
        break;
      case SUMMARY_VIEW:
          message = handler.obtainMessage(Constants.THREAD_SUMMARY_APPEND, str);
          handler.sendMessage(message);
          break;
      case DEBUG_VIEW:
        // We don't have diagnosis view here, just ignore this action.
      default:
        break;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void incrementProgress(Integer increment) {
    message = handler.obtainMessage(Constants.THREAD_ADD_PROGRESS);
    Bundle messageData = new Bundle();
	messageData.putInt("increment", increment);
	message.setData(messageData);
    handler.sendMessage(message);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void printLatLong(){
	  message = handler.obtainMessage(Constants.THREAD_LAT_LONG_APPEND);
	    handler.sendMessage(message);
  } void goodGpsSignal(){
	  message = handler.obtainMessage(Constants.THREAD_GOOD_GPS_SIGNAL);
	    handler.sendMessage(message);
  }
  
  public void noGpsSignal(){
	  message = handler.obtainMessage(Constants.THREAD_NO_GPS_SIGNAL);
	    handler.sendMessage(message);
  }
  public void noMobileConnection(){
	  message = handler.obtainMessage(Constants.THREAD_NO_MOBILE_CONNECTION);
	    handler.sendMessage(message);
  }
  public void gotMobileConnection(){
	  message = handler.obtainMessage(Constants.THREAD_GOT_MOBILE_CONNECTION);
	    handler.sendMessage(message);
  }
  @Override
  public void onBeginTest() {
    message = handler.obtainMessage(Constants.THREAD_BEGIN_TEST);
    handler.sendMessage(message);
    userStop = false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onEndTest() {
    message = handler.obtainMessage(Constants.THREAD_END_TEST);
    handler.sendMessage(message);
  }
  
  public void onTestInterrupt() {
    message = handler.obtainMessage(Constants.THREAD_TEST_INTERRUPTED);
    handler.sendMessage(message);
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public void onFailure(String errorMessage) {
    String str = "\n" + context.getString(R.string.fail_tip) + "\n";
    appendString("\n" + errorMessage, UiServices.MAIN_VIEW);
    appendString("\n"+ errorMessage, STAT_VIEW);
    appendString(str, UiServices.MAIN_VIEW);
    appendString(str, STAT_VIEW);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void logError(String str) {
    Log.e("debug", str);
  }
//Sets handle of current executing process
 /**
  * {@inheritDoc}
  */

 public void setProcessHandle(Process process) {
	 message = handler.obtainMessage(Constants.THREAD_SET_PROCESS_HANDLE,process);
     handler.sendMessage(message);
 }
//Unimplemented (and unnecessary Applet-specific) methods below
/**
 * {@inheritDoc}
 */

public void clearProcessHandle() {
	message = handler.obtainMessage(Constants.THREAD_CLEAR_PROCESS_HANDLE);
    handler.sendMessage(message);
}

  // Unimplemented (and unnecessary Applet-specific) methods below
  /**
   * {@inheritDoc}
   */
  @Override
  public void updateStatus(String status) {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateStatusPanel(String status) {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onLoginSent() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onPacketQueuingDetected() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean wantToStop() {
    return userStop;
  }
  public void setUserStop(boolean value){
	  this.userStop= value;
  }
  public void setCurrentTask(String name){
	  currentTask = name;
  }
  public String getCurrentTask(){
	  return currentTask;
  }

  @Override
  public void setVariable(String name, int value) {
  }

  @Override
  public void setVariable(String name, double value) {
  }

  @Override
  public void setVariable(String name, Object value) {
  }
  public void printWifiID() {
		message = handler.obtainMessage(Constants.THREAD_PRINT_BSSID_SSID);
	    handler.sendMessage(message);
	}
public void upDateLatLong() {
	message = handler.obtainMessage(Constants.THREAD_UPDATE_LATLONG);
    handler.sendMessage(message);
}

public void resultsSaved() {
	message = handler.obtainMessage(Constants.THREAD_RESULTS_SAVED);
    handler.sendMessage(message);
}

public void resultsNotSaved() {
	message = handler.obtainMessage(Constants.THREAD_RESULTS_NOT_SAVED);
    handler.sendMessage(message);
}

public void resultsUploaded() {
	message = handler.obtainMessage(Constants.THREAD_RESULTS_UPLOADED);
    handler.sendMessage(message);
}

public void resultsNotUploaded() {
	message = handler.obtainMessage(Constants.THREAD_RESULTS_NOT_UPLOADED);
    handler.sendMessage(message);
}
public void attemptingToUpload() {
	message = handler.obtainMessage(Constants.THREAD_RESULTS_ATTEMP_UPLOAD);
    handler.sendMessage(message);
}


public void setStatusText(String text){
	message = handler.obtainMessage(Constants.THREAD_SET_STATUS_TEXT);
	Bundle messageData = new Bundle();
	messageData.putString("text", text);
	message.setData(messageData);
	handler.sendMessage(message);
}

public void setResults(int constant, String text, String number, boolean redText, boolean numbersHidden) {
	
	message = handler.obtainMessage(constant);
	Bundle messageData = new Bundle();
	messageData.putString("text", text);
	messageData.putString("number", number);
	messageData.putBoolean("redText", redText);
	messageData.putBoolean("numbersHidden", numbersHidden);
	message.setData(messageData);
	handler.sendMessage(message);
}

public void resetGui() {
	message = handler.obtainMessage(Constants.THREAD_CONNECTIVITY_FAIL);
	handler.sendMessage(message);
}

public void phase1Complete() {
	message = handler.obtainMessage(Constants.FINISH_PHASE_1);
	handler.sendMessage(message);
}
public void startUploadTimer() {
	message = handler.obtainMessage(Constants.THREAD_START_UPLOAD_TIMER);
	handler.sendMessage(message);
}
public void stopUploadTimer() {
	message = handler.obtainMessage(Constants.THREAD_STOP_UPLOAD_TIMER);
	handler.sendMessage(message);
}
public void updateUploadNumber() {
	if (Constants.UploadDebug)
		Log.v("debug","in updateUploadNumber");
	message = handler.obtainMessage(Constants.THREAD_UPDATE_UPLOAD_NUMBER);
	handler.sendMessage(message);
}
public void setUploadNumber(String number) {
	message = handler.obtainMessage(Constants.THREAD_SET_UPLOAD_NUMBER);
	if (Constants.UploadDebug)
		Log.v("debug","in setUploadNumber number="+number);
	Bundle messageData = new Bundle();
	messageData.putString("number", number);
	message.setData(messageData);
	handler.sendMessage(message);
}
public void setUploadNumberStopTimer(String number) {
	message = handler.obtainMessage(Constants.THREAD_SET_UPLOAD_NUMBER_STOP_TIMER);
	if (Constants.UploadDebug)
		Log.v("debug","in stop Upload Timer number="+number);
	Bundle messageData = new Bundle();
	messageData.putString("number", number);
	message.setData(messageData);
	handler.sendMessage(message);
}
public void startDownloadTimer() {
	message = handler.obtainMessage(Constants.THREAD_START_DOWNLOAD_TIMER);
	handler.sendMessage(message);
}
public void stopDownloadTimer() {
	message = handler.obtainMessage(Constants.THREAD_STOP_DOWNLOAD_TIMER);
	handler.sendMessage(message);
}
public void updateDownloadNumber() {
	message = handler.obtainMessage(Constants.THREAD_UPDATE_DOWNLOAD_NUMBER);
	handler.sendMessage(message);
}
public void setDownloadNumber(String number) {
	message = handler.obtainMessage(Constants.THREAD_SET_DOWNLOAD_NUMBER);
	
	Bundle messageData = new Bundle();
	messageData.putString("number", number);
	message.setData(messageData);
	handler.sendMessage(message);
}
public void setDownloadNumberStopTimer(String number) {
	message = handler.obtainMessage(Constants.THREAD_SET_DOWNLOAD_NUMBER_STOP_TIMER);
	
	Bundle messageData = new Bundle();
	messageData.putString("number", number);
	message.setData(messageData);
	handler.sendMessage(message);
}

}
