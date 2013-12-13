/* Original work: Copyright 2009 Google Inc. All Rights Reserved.
 
   Modified work: The original source code (AndroidNdt.java) comes from the NDT Android app
                  that is available from http://code.google.com/p/ndt/.
                  It's modified for the CalSPEED Android app by California 
                  State University Monterey Bay (CSUMB) on April 29, 2013.
*/
package gov.ca.cpuc.calspeed.android;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.view.*;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class DisplayHistory extends SherlockFragment  {
	
	private ArrayList<History> data;
	private ListView list;
	private HistoryResultsAdapter adapter;
	private History selected;
	private Integer selectedPosition;
	private View selectedView;

	@Override
	public void onStart() {
		SetupHistoryList();
	    ReadHistory();
	    
		ActionBar actionBar = ((SherlockFragmentActivity) getActivity()).getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
		
		super.onStart();
	}
	
	@Override
	public void onDestroy() {

		if (Constants.DEBUG)
			Log.v("debug", "onDestory");
		super.onDestroy();
	}
	@Override
	public void onPause() {

		if (Constants.DEBUG)
			Log.v("debug", "onPause");

		super.onPause();
	}

	@Override
	public void onResume() {

		if (Constants.DEBUG)
			Log.v("debug", "onResume");
		super.onResume();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStop() {

		if (Constants.DEBUG)
			Log.v("debug", "onStop");
		super.onStop();
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.displayhistory, container, false);

		return v;
	}
  private void SetupHistoryList(){
	  data = new ArrayList<History>();
	  adapter = new HistoryResultsAdapter(getActivity(), data);
		list = (ListView) getView().findViewById(R.id.ListView);
		list.setAdapter(adapter);
		list.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
		list.setTextFilterEnabled(true);
		list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
 
            	selectedView = view;
            	selectedPosition = position;
            	selected = (History)list.getItemAtPosition(selectedPosition);
            	Integer itemID = selected.getID();
            	HistoryDatabaseHandler db = new HistoryDatabaseHandler(getActivity());
          	    History dbItemSelected = db.getHistory(itemID);
            	createCustomHistoryDialog(dbItemSelected);
            }
		});
                
  }
  private String createStatsMessage(History selected){
	  DecimalFormat df = new DecimalFormat("#.00000");
	  String message = "";
	  
	  message = "\nStatistics\n\n";
	  message+= "Date:     "+selected.getFormattedDate(" ")+ "\n";
	  message+= "Upload:   " + selected.getUploadAverage() + " kbps\n";
	  message+= "Download: " + selected.getDownloadAverage() + " kbps\n";
	  message+= "Delay:    " + selected.getLatencyAverage() + " ms\n";
	  message+= "Delay Variation: " + selected.getJitterAverage() + " ms\n\n";
	  message+= "Network Information\n\n";
	  if (selected.getNetworkType().contains("WIFI")){
		  message+= "Network Type:    " + selected.getNetworkType()+ "\n";
	  }else{
		  message+= "Network Type:    " + selected.getConnectionType()+ "\n";
	  }
	  if (Float.parseFloat(selected.getLatitude()) == 0){
		  message+= "Latitude:   Not Available\n";
	  }else{
		  Float lat = Float.parseFloat(selected.getLatitude());
		  message+= "Latitude:        " + df.format(lat)+ "\n";
	  }
	  if (Float.parseFloat(selected.getLongitude()) == 0){
		  message+= "Longitude:  Not Available\n";
	  }else{
		  Float lng = Float.parseFloat(selected.getLongitude());
		  message+= "Longitude:       " + df.format(lng)+ "\n";
	  }
	  
	  return(message);
  }
  private void createCustomHistoryDialog(History selectedItem){
	  
	  	DecimalFormat df = new DecimalFormat("#.00000");
	  	
		// custom dialog
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.historydetailsdialog);

		// set the custom dialog components - text and buttons
		TextView date = (TextView) dialog.findViewById(R.id.DateHistoryValue);
		date.setText(selectedItem.getMonthDayYear()+" ");
		
		TextView time = (TextView) dialog.findViewById(R.id.TimeHistoryValue);
		time.setText(selectedItem.getTime()+" ");
		
		TextView upload = (TextView) dialog.findViewById(R.id.uploadHistorySpeed);
		upload.setText(selectedItem.getUploadAverage());
		
		TextView download = (TextView) dialog.findViewById(R.id.downloadHistorySpeed);
		download.setText(selectedItem.getDownloadAverage());
		
		TextView delay = (TextView) dialog.findViewById(R.id.latencyHistorySpeed);
		delay.setText(selectedItem.getLatencyAverage());
		
		TextView delayVar = (TextView) dialog.findViewById(R.id.jitterHistorySpeed);
		delayVar.setText(selectedItem.getJitterAverage());

		TextView networkType = (TextView) dialog.findViewById(R.id.NetworkHistoryType);
		
		if (selectedItem.getNetworkType().contains("WIFI")){
			  networkType.setText(selectedItem.getNetworkType()+" ");
		  }else{
			  networkType.setText(selectedItem.getConnectionType()+" ");
		  }
		
		  TextView latitude = (TextView) dialog.findViewById(R.id.LatitudeHistoryValue);
		  if (Float.parseFloat(selectedItem.getLatitude().replace(",", ".")) == 0){
			  latitude.setText("Not Available ");
		  }else{
			  latitude.setText(selectedItem.getLatitude().replace(",", ".")+" ");
			  
		  }
		  
		  TextView longitude = (TextView) dialog.findViewById(R.id.LongtitudeHistoryValue);
		  if (Float.parseFloat(selectedItem.getLongitude().replace(",", ".")) == 0){
			  longitude.setText("Not Available ");
		  }else{
			  longitude.setText(selectedItem.getLongitude().replace(",", ".")+" ");
		  }
		  

		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
		// if button is clicked, close the custom dialog

		dialogButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		Button dialogButtonDelete = (Button) dialog.findViewById(R.id.dialogButtonDelete);
		// if button is clicked, close the custom dialog

		dialogButtonDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createDeleteConfirmAlert();
				dialog.dismiss();
			}
		});

		dialog.show();
  }
  private void createDeleteConfirmAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Do you want to delete this test result?")
				.setCancelable(false)
				.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
								DeleteHistoryItem(selected);
								dialog.cancel();
								
							}
						});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();						
					}
				});
		AlertDialog alert = builder.create();
		alert.show();

		
}
  private void createDeleteHistoryAlert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
								DeleteHistoryItem(selected);
								dialog.cancel();
								
							}
						});
		builder.setNegativeButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();						
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
  
		
  }
  private void ReadHistory(){
	  Boolean status;
	  Integer historyCount = 0;
	  HistoryDatabaseHandler db = new HistoryDatabaseHandler(getActivity());
	  historyCount = db.getHistoryCount();

	  if (historyCount != 0){
		  DisplayHistory(db);
	  }
	  db.close();
  }
  @SuppressLint("NewApi")
private void DisplayHistory(HistoryDatabaseHandler db){
	  History tempLine;
	  
	  List<History> historyList = new ArrayList<History>();
	  
	  historyList = db.getAllHistoryRecords();
	  data.clear();
	  for (int i=0; i< historyList.size(); i++){
		  
		  tempLine = new History(historyList.get(i).getID(),historyList.get(i).getDate(),historyList.get(i).getUploadAverage(),
				  historyList.get(i).getDownloadAverage(),historyList.get(i).getLatencyAverage(),
				  historyList.get(i).getJitterAverage(),historyList.get(i).getNetworkType(),
				  historyList.get(i).getConnectionType(),historyList.get(i).getLatitude(),
				  historyList.get(i).getLongitude());
			if (!data.add(tempLine)) {
				System.out
						.println("Error adding new line to results line.");
			}
			adapter.notifyDataSetChanged();
			list.smoothScrollToPosition(0);
	  }
  }
  private void DeleteHistoryItem(History historyItem){
	  HistoryDatabaseHandler db = new HistoryDatabaseHandler(getActivity());
	  db.deleteHistory(historyItem.getStringID()); // remove from database
	  data.remove(list.getItemAtPosition(selectedPosition)); // remove from ListView
	  adapter.notifyDataSetChanged();
	  db.close();
  }

}
