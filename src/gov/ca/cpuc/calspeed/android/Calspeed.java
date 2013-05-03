// Original work: Copyright 2009 Google Inc. All Rights Reserved.
//
// Modified work: The original source code (AndroidNdt.java) comes from the NDT Android app
//                that is available from http://code.google.com/p/ndt/.
//                It's modified for the CalSPEED Android app by California 
//                State University Monterey Bay (CSUMB) on April 29, 2013.
//

package gov.ca.cpuc.calspeed.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.telephony.TelephonyManager;
import android.net.wifi.*;
import android.content.SharedPreferences;

import gov.ca.cpuc.calspeed.android.UiServices;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import gov.ca.cpuc.calspeed.android.R;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

/**
 * UI Thread and Entry Point of mobile client.
 */
public class Calspeed extends Activity {
	private int serverNumber;
	private int progress;
	private String operatorName;
	private String providerName;
	private String serverName;
	private String serverHost;
	private String mobileInfo;
	private String statistics;
	private Button buttonStandardTest;
	private ProgressBar progressBar;
	private TextView topText;
	private TextView textViewMain;
	private UiHandler uiHandler;
	private PowerManager powerManager;
	private PowerManager.WakeLock wakeLock;
	private NetworkInfo networkInfo;
	private AndroidUiServices uiServices;
	private static NdtLocation ndtLocation;
	private AssetManager assetManager;
	private String applicationFilesDir;
	private TelephonyManager telephonyManager;
	private String telephoneInfo;
	private String bssid, ssid;
	private Date date;
	private Double startLatitude;
	private Double startLongitude;
	private Context context;
	private String DeviceId;
	Boolean usingUploadButton = false;
	Boolean validLocation = false;
	private LatLong myLatLong;
	private ProgressDialog GPSdialog, Mobiledialog;
	private String Provider;
	private String TCPPort;
	private String UDPPort;
	private String location = null;
	private TextView uploadText;
	private TextView uploadNum;
	private TextView uploadUnits;
	private TextView downloadText;
	private TextView downloadNum;
	private TextView downloadUnits;
	private TextView latencyText;
	private TextView latencyNum;
	private TextView latencyUnits;
	private TextView jitterText;
	private TextView jitterNum;
	private TextView jitterUnits;
	private View resultsView;
	private Animation slideOut;
	private Animation slideIn;
	private Float smoothUpload;
	private Float smoothDownload;
	private String finalDownload;
	private String finalUpload;
	private Timer UploadTimer;
	private Timer DownloadTimer;
	private TimerTask uploadTask;
	private TimerTask downloadTask;

	/**
	 * Initializes the activity.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences legal = getSharedPreferences("Legal", MODE_PRIVATE);

		if (!(legal.getBoolean("privacyPolicyAccepted", false))) {
			createPrivacyPolicyAlert();
		}

		setContentView(R.layout.main);
		try {
			context = createPackageContext("gov.ca.cpuc.calspeed.android", 0);
			Prefs.resetGPSoverride(context);
		} catch (Exception e) {
			if (Constants.DEBUG)
				Log.v("debug", "unable to set context OnCreate");
		}

		// Set the default server
		serverNumber = Constants.DEFAULT_SERVER;
		serverName = Constants.SERVER_NAME[serverNumber];
		serverHost = Constants.SERVER_HOST[serverNumber];

		powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		networkInfo = connectivityManager.getActiveNetworkInfo();
		wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"Network Testing");

		uiHandler = new UiHandler(Looper.myLooper());

		uiServices = new AndroidUiServices(this, uiHandler);
		assetManager = getAssets();
		ndtLocation = new NdtLocation(this, uiServices);
		myLatLong = new LatLong();

		applicationFilesDir = GetApplicationFilesDir();
		SetupIperf();

		textViewMain = (TextView) findViewById(R.id.TextViewMain);
		textViewMain.setMovementMethod(ScrollingMovementMethod.getInstance());
		textViewMain.setClickable(false);
		textViewMain.setLongClickable(false);

		topText = (TextView) findViewById(R.id.topText);

		
		textViewMain.append(getString(R.string.nonofficial) + "\n");
		uiServices.appendString(getString(R.string.nonofficial) + "\n",
					uiServices.STAT_VIEW);


		

		date = new Date();
		textViewMain.append(date.toString() + "\n");

		statistics = "";

		initComponents();
		ndtLocation.addGPSStatusListener();
		if (!ndtLocation.gpsEnabled) {
			createGpsDisabledAlert();
		}
		startGPS();

		setupUploadTimer();
		setupDownloadTimer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDestroy() {
		ndtLocation.stopListen();
		ndtLocation.removeGPSStatusListener();
		if (Constants.DEBUG)
			Log.v("debug", "onDestory");
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			if (Constants.DEBUG)
				Log.v("debug", "Release Wake Lock onDestroy");
		}
		super.onDestroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStart() {
		if (Constants.DEBUG)
			Log.v("debug", "onStart");
		super.onStart();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResume() {
		if (Constants.DEBUG)
			Log.v("debug", "onResume");
		super.onResume();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStop() {
		if (Constants.DEBUG)
			Log.v("debug", "onStop");
		super.onStop();
	}

	/**
	 * Initializes the components on main view.
	 */
	private void initComponents() {

		buttonStandardTest = (Button) findViewById(R.id.ButtonStandardTest);
		buttonStandardTest.setOnClickListener(new StandardTestButtonListener());

		progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
		progressBar.setIndeterminate(false);

		uploadText = (TextView) findViewById(R.id.uploadLabel);
		uploadNum = (TextView) findViewById(R.id.uploadSpeed);
		uploadUnits = (TextView) findViewById(R.id.uploadUnits);

		downloadText = (TextView) findViewById(R.id.downloadLabel);
		downloadNum = (TextView) findViewById(R.id.downloadSpeed);
		downloadUnits = (TextView) findViewById(R.id.downloadUnits);

		latencyText = (TextView) findViewById(R.id.latencyLabel);
		latencyNum = (TextView) findViewById(R.id.latencySpeed);
		latencyUnits = (TextView) findViewById(R.id.latencyUnits);

		jitterText = (TextView) findViewById(R.id.jitterLabel);
		jitterNum = (TextView) findViewById(R.id.jitterSpeed);
		jitterUnits = (TextView) findViewById(R.id.jitterUnits);

		resultsView = findViewById(R.id.testResults);
		slideOut = AnimationUtils.loadAnimation(this, R.anim.slideout);
		slideOut.setAnimationListener(new SlideOutAnimationListener());
		slideIn = AnimationUtils.loadAnimation(this, R.anim.slidein);
	}

	/**
	 * Check to see if a resource/asset file exists in
	 * /data/data/<package>/files directory
	 */
	private boolean FileExistsInActivity(String filename) {
		FileInputStream fileinput = null;

		try {
			fileinput = openFileInput(filename);
			fileinput.close();
			return true;
		} catch (IOException e) {
			return false;
		}

	}




	private void SetupIperf() {

		CopyBinaryFile("android_iperf_2_0_2_3", "iperfT");
		ExecCommandLine command = new ExecCommandLine("chmod 755 "
				+ this.applicationFilesDir + "/iperfT", 60000, null, null,
				null, uiServices);
		try {
			command.runCommand();
		} catch (InterruptedException e) {
			// do nothing
		}
		
		PrintAppDirectoryInfo();
	}

	private String GetApplicationFilesDir() {
		File pathForAppFiles = getFilesDir();
		String path = pathForAppFiles.getAbsolutePath();
		return (path);
	}

	private void PrintAppDirectoryInfo() {
		File pathForAppFiles = getFilesDir();
		if (Constants.DEBUG)
			Log.i("debug",
					"Listing Files in " + pathForAppFiles.getAbsolutePath());
		String[] fileList = pathForAppFiles.list();
		File[] fileptrs = pathForAppFiles.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			if (Constants.DEBUG)
				Log.i("debug", "Filename " + i + ": " + fileList[i] + " size: "
						+ fileptrs[i].length());
		}
	}

	public void CopyBinaryFileIfNotExists(String inputFilename,
			String outputFilename) {
		try {
			InputStream inputFile = this.assetManager.open(inputFilename);
			try {
				FileInputStream inputFileTest = openFileInput(outputFilename);
			} catch (Exception e) { // file not found, so copy it to files
									// directory
				FileOutputStream outputFile = openFileOutput(outputFilename,
						MODE_PRIVATE);
				copy(inputFile, outputFile);
				inputFile.close();
				outputFile.flush();
				outputFile.close();
			}
		} catch (IOException e) {
			if (Constants.DEBUG)
				Log.e("Asset File Error", e.getMessage());
		}
	}

	public void CopyBinaryFile(String inputFilename, String outputFilename) {
		try {
			InputStream inputFile = this.assetManager.open(inputFilename);
			FileOutputStream outputFile = openFileOutput(outputFilename,
					MODE_PRIVATE);
			copy(inputFile, outputFile);
			inputFile.close();
			outputFile.flush();
			outputFile.close();
		} catch (IOException e) {
			if (Constants.DEBUG)
				Log.e("Asset File Error", e.getMessage());
		}
	}

	private static void copy(InputStream in, FileOutputStream out)
			throws IOException {

		byte[] b = new byte[4096];
		int read;
		try {
			while ((read = in.read(b)) != -1) {
				out.write(b, 0, read);

			}
		} catch (EOFException e) {
			// just exit at end of file
		}
	}

	private class StandardTestButtonListener implements OnClickListener {
		public void onClick(View view) {

			statistics = "";

			ndtLocation.addGPSStatusListener();
			if (!ndtLocation.gpsEnabled) {
				createGpsDisabledAlert();
			} else {

				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetwork = connectivityManager
						.getActiveNetworkInfo();

				usingUploadButton = false;
				context = view.getContext();

				if (isNetworkActive()) {

					WifiManager wifiManager = (WifiManager) getBaseContext()
							.getSystemService(Context.WIFI_SERVICE);
					if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
						WifiInfo winfo = wifiManager.getConnectionInfo();
						bssid = winfo.getBSSID();
						ssid = winfo.getSSID();
						createWifiAlert();
					} else {

						if (wifiManager.isWifiEnabled()) {
							createDisableWifiAlert();
						} else {
							finishStartButton();
						}
					}
				} else {
					finishStartButton();
				}

			}
		}

	}

	public void finishApp() {
		ndtLocation.stopListen();
		ndtLocation.removeGPSStatusListener();
		System.exit(0);
	}

	public void finishStartButton() {

		resultsView.setBackgroundColor(Color.BLACK);
		ProgressBar loadingIcon = (ProgressBar) findViewById(R.id.loadingIcon);
		loadingIcon.setVisibility(View.VISIBLE);
		buttonStandardTest.setVisibility(View.INVISIBLE);

		ToggleButton indoorOutdoor = (ToggleButton) findViewById(R.id.indoorOutdoorToggle);
		indoorOutdoor.setEnabled(false);
		topText.setVisibility(View.VISIBLE);

		progressBar.setVisibility(View.VISIBLE);

		resetResults();

		progressBar.setProgress(0);

		textViewMain.setText("");

		date = new Date();
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = null; // reset
		networkInfo = connectivityManager.getActiveNetworkInfo();
		mobileInfo = getMobileProperty();
		telephoneInfo = getTelephoneProperty();

		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(getString(R.string.nonofficial) + "\n");
		stringBuilder.append(
				getString(R.string.test_begins_at, date.toString() + "\n"))
				.append("\n");

		if (Prefs.getGPSoverride(context)) {
			stringBuilder.append("GPS override set by Tester.\n");
		}
		ndtLocation.startListen();

		uiServices.appendString(stringBuilder.toString(), UiServices.MAIN_VIEW);

		uiServices.appendString(stringBuilder.toString(),
				UiServices.SUMMARY_VIEW);

		if (isNetworkActive()) {
			stringBuilder.append("\nThe network is active\n");
		} else {
			stringBuilder.append("\nThe network is not active\n");
		}

		statistics += stringBuilder.toString();
		stringBuilder = new StringBuilder().append("\n")

		.append(getSystemProperty()).append("\n").append(mobileInfo)
				.append("\n\n")
				.append("\n").append(telephoneInfo).append("\n");



		if (isNetworkActive()
				&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			stringBuilder.append("\nWifi BSSID: " + bssid + "\nWifi SSID: "
					+ ssid + "\n");
		}

		uiServices.appendString(stringBuilder.toString(), UiServices.MAIN_VIEW);

		statistics += stringBuilder.toString();



		CheckGpsOverride();
	}

	// Menu routines
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, About.class));
			return true;
		}
		return false;
	}

	private void startGPS() {
		if (ndtLocation.bestProvider != null) {
			ndtLocation.startListen();
		}
	}

	public class AcquireGPS extends Thread {

		private AndroidUiServices uiServices;
		private LatLong gpsLatLong;

		public AcquireGPS(AndroidUiServices uiServices) {

			this.uiServices = uiServices;
			this.gpsLatLong = new LatLong();

		}

		@Override
		public void run() {

			for (int i = 0; i < 3; i++) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				gpsLatLong.getLatitudeLongitude(gpsLatLong);
				if (gpsLatLong.valid) {
					uiServices.goodGpsSignal();
					break;
				}
			}
			if (!gpsLatLong.valid) {
				ndtLocation.stopListen();
				uiServices.noGpsSignal();
			}
		}

	}

	private void CheckGpsOverride() {

		if (Prefs.getGPSoverride(context) == true) {

			startTest(location);

		} else {
			AcquiringGPS();
		}
	}

	private void AcquiringGPS() {

		GPSdialog = ProgressDialog.show(context, "",
				"Acquiring GPS information...", true);

		Thread checkGPS = new Thread(new AcquireGPS(uiServices));
		checkGPS.start();

	}

	public Boolean isNetworkMobile() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = null; // reset
		networkInfo = connectivityManager.getActiveNetworkInfo();
		if ((networkInfo == null)
				|| (networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
			return (false);
		} else {
			return (true);
		}
	}

	public Boolean isNetworkActive() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = null; // reset
		networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return (false);
		} else {
			return (true);
		}
	}

	public class WaitForMobileConnection extends Thread {

		private AndroidUiServices uiServices;

		public WaitForMobileConnection(AndroidUiServices uiServices) {

			this.uiServices = uiServices;

		}

		@Override
		public void run() {

			for (int i = 0; i < 3; i++) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (isNetworkMobile()) {
					break;
				}

			}

			if (!isNetworkMobile()) {
				uiServices.noMobileConnection();
			} else {
				uiServices.gotMobileConnection();
			}
		}

	}

	private void resultsNotSaved() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Unable to save results to SD card. Please check your settings.")
				.setCancelable(false)
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void createGpsDisabledAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your GPS is disabled! Would you like to enable it?")
				.setCancelable(false)
				.setPositiveButton("Enable GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								showGpsOptions();
							}
						});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						finishApp();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showGpsOptions() {
		Intent gpsOptionsIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}

	public void openWebURL(String inURL) {
		Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));
		startActivity(browse);
	}

	private void createPrivacyPolicyAlert() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Have you read and agree to our terms and conditions?")
				.setCancelable(false)
				.setPositiveButton("Read",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								// Overwritten by privacyPolicyReadListener to
								// prevent dialog closing
							}
						});
		builder.setNegativeButton("Yes, I agree.",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						SharedPreferences legal = getSharedPreferences("Legal",
								MODE_PRIVATE);
						SharedPreferences.Editor legalEditor = legal.edit();
						legalEditor.putBoolean("privacyPolicyAccepted", true);
						legalEditor.commit();

						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
		Button readButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
		readButton.setOnClickListener(new privacyPolicyReadListener(alert));
	}

	class privacyPolicyReadListener implements OnClickListener {

		private final AlertDialog dialog;

		public privacyPolicyReadListener(AlertDialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void onClick(View v) {
			openWebURL(Constants.privacyPolicyURL);
		}
	}

	private void createWifiAlert() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"You're connected to Wifi!\n Would you like to use Wifi?")
				.setCancelable(false)
				.setPositiveButton("Wifi",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								uiServices.printWifiID();
								finishStartButton();
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

	private void createDisableWifiAlert() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Wifi is turned on, but you may not be logged in. Please log into your Wifi network or turn it off before running CalSPEED.")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	public void disableWifi() {
		WifiManager wifiManager = (WifiManager) getBaseContext()
				.getSystemService(Context.WIFI_SERVICE);
		if (Constants.CHECK_FOR_WIFI) {
			if (wifiManager.isWifiEnabled()) {
				textViewMain.append("\nDisabling Wifi...\n");

				wifiManager.setWifiEnabled(false);
			}
			if (!wifiManager.isWifiEnabled()) {
				textViewMain.append("\nWifi is disabled.\n");
			}
		}
	}

	/**
	 * Gets the system related properties.
	 * 
	 * @return a string describing the OS and Java environment
	 */
	private String getSystemProperty() {
		String osName, osArch, osVer, javaVer, javaVendor;
		osName = System.getProperty("os.name");
		osArch = System.getProperty("os.arch");
		osVer = System.getProperty("os.version");
		javaVer = System.getProperty("java.version");
		javaVendor = System.getProperty("java.vendor");
		StringBuilder sb = new StringBuilder().append("\n")
				.append(getString(R.string.os_line, osName, osArch, osVer))
				.append("\n")
				.append(getString(R.string.java_line, javaVer, javaVendor));
		return sb.toString();
	}

	public void updateLatitudeLongitude() {
		if (ndtLocation.location != null) {
			myLatLong.setLatitudeLongitude(
					(Double) ndtLocation.location.getLatitude(),
					(Double) ndtLocation.location.getLongitude(), true);
		} else {
			myLatLong.setLatitudeLongitude(0.0, 0.0, false);
		}

	}

	/**
	 * Gets the mobile device related properties.
	 * 
	 * @return a string about location, network type (MOBILE or WIFI)
	 */
	private String getMobileProperty() {
		StringBuilder sb = new StringBuilder();
		if (ndtLocation.gpsEnabled && ndtLocation.location != null) {
			LatLong newLatLong = new LatLong();
			newLatLong.getLatitudeLongitude(newLatLong);
			if (newLatLong.valid) {
				if (Constants.DEBUG)
					Log.v("debug", ndtLocation.location.toString());

				sb.append(
						getString(R.string.latitude_result, newLatLong.Latitude))
						.append("\n")
						.append(getString(R.string.longitude_result,
								newLatLong.Longitude));
			}
		} else {
			sb.append("").append(getString(R.string.no_GPS_info, ""));
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			if (Constants.DEBUG)
				Log.v("debug", networkInfo.toString());
			sb.append("\n")
					.append(getString(R.string.network_type_indicator,
							networkInfo.getTypeName())).append("\n");
		}

		return sb.toString();
	}

	/**
	 * Gets the mobile provider related properties.
	 * 
	 * @return a string about network providers and network type
	 */
	@SuppressLint("NewApi")
	private String getTelephoneProperty() {
		StringBuilder sb = new StringBuilder();
		this.telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		providerName = telephonyManager.getSimOperatorName();

		String deviceModel = Build.MODEL;
		String manufacturer = Build.MANUFACTURER;
		String APIVersion = Build.VERSION.RELEASE;
		int SDKVersion = Build.VERSION.SDK_INT;

		if (providerName == null) {
			providerName = "Unknown";
		}
		if (Constants.DEBUG)
			Log.v("debug", providerName);
		sb.append("\n").append(
				getString(R.string.network_provider, providerName));

		Provider = sb.substring(18, sb.length());
		operatorName = telephonyManager.getNetworkOperatorName();
		if (Provider.equalsIgnoreCase("")) {
			Provider = operatorName;
		}
		getPorts();
		if (operatorName == null) {
			operatorName = "Unknown";
		}
		if (Constants.DEBUG)
			Log.v("debug", operatorName);
		sb.append("\n").append(
				getString(R.string.network_operator, operatorName));
		if (telephonyManager.isNetworkRoaming()) {
			sb.append("\n").append("Network is Roaming.");
		} else {
			sb.append("\n").append("Network is Not Roaming.");
		}

		ToggleButton indoorOutdoor = (ToggleButton) findViewById(R.id.indoorOutdoorToggle);
		if (indoorOutdoor.isChecked()) {
			sb.append("\nThis device was: " + indoorOutdoor.getTextOn());
		} else {
			sb.append("\nThis device was " + indoorOutdoor.getTextOff());
		}

		String connectionType = getConnectionType();
		if (Constants.DEBUG)
			Log.v("debug", connectionType);
		sb = printInfoLine(R.string.connection_type, connectionType, sb);

		sb.append("\n").append("Phone Model: " + deviceModel);
		sb.append("\n").append("Phone Manufacturer: " + manufacturer);
		sb.append("\n").append("API Version: " + APIVersion);
		sb.append("\n").append("SDK Version: " + SDKVersion);

		return sb.toString();
	}

	private void getPorts() {
		if (Provider.equalsIgnoreCase("at&t")) {
			TCPPort = Constants.ports[0];
			UDPPort = Constants.ports[1];
		} else if (Provider.equalsIgnoreCase("sprint")) {
			TCPPort = Constants.ports[2];
			UDPPort = Constants.ports[3];
		} else if (Provider.equalsIgnoreCase("t-mobile")) {
			TCPPort = Constants.ports[4];
			UDPPort = Constants.ports[5];
		} else if (Provider.equalsIgnoreCase("verizon")) {
			TCPPort = Constants.ports[6];
			UDPPort = Constants.ports[7];
		} else {
			TCPPort = Constants.ports[8];
			UDPPort = Constants.ports[9];
		}
	}

	private StringBuilder printInfoLine(int label, String variable,
			StringBuilder buffer) {
		if (Constants.DEBUG)
			Log.v("debug", variable);
		buffer.append("\n").append(getString(label, variable));
		return buffer;
	}

	private String getConnectionType() {
		Integer intcon = 0;
		String type = "UNKNOWN";
		final int connection = this.telephonyManager.getNetworkType();
		for (int i = 0; i < Constants.NETWORK_TYPE.length; i++) {
			intcon = Integer.valueOf(Constants.NETWORK_TYPE[i][0]);
			if (intcon == connection) {
				type = Constants.NETWORK_TYPE[i][1];
				break;
			}
		}
		return type;
	}

	/**
	 * Gets the type of the active network, networkInfo should be initialized
	 * before called this function.
	 * 
	 */
	private String getNetworkType() {
		if (networkInfo != null) {
			int networkType = networkInfo.getType();
			switch (networkType) {
			case ConnectivityManager.TYPE_MOBILE:
				return Constants.NETWORK_MOBILE;
			case ConnectivityManager.TYPE_WIFI:
				return Constants.NETWORK_WIFI;
			default:
				return Constants.NETWORK_UNKNOWN;
			}
		} else {
			return Constants.NETWORK_UNKNOWN;
		}
	}

	public class LatLong {
		public Double Latitude;
		public Double Longitude;
		public Boolean valid;

		public LatLong() {
			this.Latitude = 0.0;
			this.Longitude = 0.0;
			this.valid = false;
		}

		public synchronized void setLatitudeLongitude(Double latitude,
				Double longitude, Boolean valid) {
			myLatLong.Latitude = latitude;
			myLatLong.Longitude = longitude;
			myLatLong.valid = valid;
		}

		public synchronized Boolean getLatitudeLongitude(LatLong structLatLong) {

			structLatLong.Latitude = myLatLong.Latitude;
			structLatLong.Longitude = myLatLong.Longitude;
			structLatLong.valid = myLatLong.valid;
			return (structLatLong.valid);
		}

	}

	public void updateLatLongDisplay() {
		LatLong newLatLong = new LatLong();
		newLatLong.getLatitudeLongitude(newLatLong);
		if (newLatLong.valid) {
			String newLat = newLatLong.Latitude.toString();
			String newLong = newLatLong.Longitude.toString();
			if (newLat.length() > 11)
				newLat = newLat.substring(0, 10);
			if (newLong.length() > 11)
				newLong = newLong.substring(0, 10);
		}
	}

	private void setupUploadTimer() {
		smoothUpload = 0.0f;
		UploadTimer = new Timer();
		uploadTask = new TimerTask() {
			@Override
			public void run() {
				if (smoothUpload != 0.0f) {
					uiServices.updateUploadNumber();
				}
				return;
			}
		};
	}

	private void setupDownloadTimer() {
		smoothDownload = 0.0f;
		DownloadTimer = new Timer();
		downloadTask = new TimerTask() {
			@Override
			public void run() {
				if (smoothDownload != 0.0f) {
					uiServices.updateDownloadNumber();
				}
				return;
			}
		};
	}

	private Float LowPassFilter(Float newValue, Float smoothValue) {
		Float newSmooth;
		newSmooth = smoothValue + Constants.SMOOTH * (newValue - smoothValue);
		return (newSmooth);
	}

	public class UiHandler extends Handler {
		public UiHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message message) {

			String results;


			switch (message.what) {
			case Constants.THREAD_MAIN_APPEND:
				textViewMain.append(message.obj.toString());
				break;
			case Constants.THREAD_STAT_APPEND:
				statistics += message.obj.toString();
				break;

			case Constants.THREAD_LAT_LONG_APPEND:
				textViewMain.append("\nLatitude: " + startLatitude);
				textViewMain.append("\nLongitude: " + startLongitude + "\n");

				break;
			case Constants.THREAD_BEGIN_TEST:
				if (Constants.DEBUG)
					Log.v("debug", "Begin the test");
				progress = 0;
				buttonStandardTest.setEnabled(false);

				progressBar.setProgress(progress);
				progressBar.setMax(Constants.TEST_STEPS);
				if (wakeLock.isHeld() == false) {
					wakeLock.acquire();
					if (Constants.DEBUG)
						Log.v("debug", "wakeLock acquired");
				}
				break;
			case Constants.THREAD_END_TEST:
				if (Constants.DEBUG)
					Log.v("debug", "End the test");
				textViewMain.append("\n-----End of Test------\n");

				statistics += "\n";
				buttonStandardTest.setEnabled(true);

				if (wakeLock.isHeld()) {
					wakeLock.release();
					if (Constants.DEBUG)
						Log.v("debug", "wakeLock released");
				}

				Button toggleButton = (ToggleButton) findViewById(R.id.indoorOutdoorToggle);
				toggleButton.setEnabled(true);
				ProgressBar loadingIcon = (ProgressBar) findViewById(R.id.loadingIcon);
				loadingIcon.setVisibility(ProgressBar.INVISIBLE);
				buttonStandardTest.setText("Test Again");
				buttonStandardTest.setVisibility(Button.VISIBLE);

				break;
			case Constants.THREAD_TEST_INTERRUPTED:
				if (Constants.DEBUG)
					Log.v("debug", "End the test");
				textViewMain.append("\n-----End of Test------\n");

				statistics += "\n";
				buttonStandardTest.setEnabled(true);

				if (wakeLock.isHeld()) {
					wakeLock.release();
					if (Constants.DEBUG)
						Log.v("debug", "wakeLock released");
				}

				break;
			case Constants.THREAD_ADD_PROGRESS:
				Integer increment = message.getData().getInt("increment");
				progressBar.setProgress(progressBar.getProgress() + increment);
				break;
			case Constants.THREAD_SET_PROCESS_HANDLE:
				break;
			case Constants.THREAD_CLEAR_PROCESS_HANDLE:
				break;
			case Constants.THREAD_GOOD_GPS_SIGNAL:
				GPSdialog.dismiss();
				startTest(location);

				break;
			case Constants.THREAD_NO_GPS_SIGNAL:
				GPSdialog.dismiss();
				break;
			case Constants.THREAD_NO_MOBILE_CONNECTION:
				Mobiledialog.dismiss();
				break;
			case Constants.THREAD_GOT_MOBILE_CONNECTION:
				Mobiledialog.dismiss();
				finishStartButton();
				break;
			case Constants.THREAD_UPDATE_LATLONG:

				LatLong newLatLong = new LatLong();
				newLatLong.getLatitudeLongitude(newLatLong);
				if (newLatLong.valid) {
					String newLat = newLatLong.Latitude.toString();
					String newLong = newLatLong.Longitude.toString();
					if (newLat.length() > 11)
						newLat = newLat.substring(0, 10);
					if (newLong.length() > 11)
						newLong = newLong.substring(0, 10);
				}
				break;
			case Constants.THREAD_RESULTS_SAVED:
				break;
			case Constants.THREAD_RESULTS_NOT_SAVED:
				resultsNotSaved();
				break;
			case Constants.THREAD_RESULTS_UPLOADED:
				break;
			case Constants.THREAD_RESULTS_NOT_UPLOADED:
				break;
			case Constants.THREAD_RESULTS_ATTEMP_UPLOAD:
				break;
			case Constants.THREAD_SET_STATUS_TEXT:
				results = message.getData().getString("text");
				topText.setText(results);
				break;
			case Constants.THREAD_PRINT_BSSID_SSID:
				textViewMain.append("Wifi BSSID: " + bssid + "\nWifi SSID: "
						+ ssid + "\n");
				statistics += "Wifi BSSID: " + bssid + "\nWifi SSID: " + ssid
						+ "\n";
				break;
			case Constants.THREAD_WRITE_UPLOAD_DATA:
				if (message.getData().getBoolean("redText")) {
					uploadText.setTextColor(Color.RED);
				} else {
					uploadText.setTextColor(Color.WHITE);
				}

				if (message.getData().getBoolean("numbersHidden")) {
					uploadNum.setVisibility(View.GONE);
					uploadUnits.setVisibility(View.GONE);
				} else {
					uploadNum.setVisibility(View.VISIBLE);
					uploadUnits.setVisibility(View.VISIBLE);
					finalUpload = removeDecimalPlaces(message.getData()
							.getString("number"));
					smoothUpload = LowPassFilter(Float.valueOf(finalUpload),
							smoothUpload);
				}

				uploadText.setText(message.getData().getString("text"));
				break;
			case Constants.THREAD_WRITE_DOWNLOAD_DATA:
				if (message.getData().getBoolean("redText")) {
					downloadText.setTextColor(Color.RED);
				} else {
					downloadText.setTextColor(Color.WHITE);
				}

				if (message.getData().getBoolean("numbersHidden")) {
					downloadNum.setVisibility(View.GONE);
					downloadUnits.setVisibility(View.GONE);
				} else {
					downloadNum.setVisibility(View.VISIBLE);
					downloadUnits.setVisibility(View.VISIBLE);
					finalDownload = removeDecimalPlaces(message.getData()
							.getString("number"));
					smoothDownload = LowPassFilter(
							Float.valueOf(finalDownload), smoothDownload);
				}

				downloadText.setText(message.getData().getString("text"));

				break;
			case Constants.THREAD_WRITE_LATENCY_DATA:

				if (message.getData().getBoolean("redText")) {
					latencyText.setTextColor(Color.RED);
				} else {
					latencyText.setTextColor(Color.WHITE);
				}

				if (message.getData().getBoolean("numbersHidden")) {
					latencyNum.setVisibility(View.GONE);
					latencyUnits.setVisibility(View.GONE);
				} else {
					latencyNum.setVisibility(View.VISIBLE);
					latencyUnits.setVisibility(View.VISIBLE);
				}

				latencyText.setText(message.getData().getString("text"));
				latencyNum.setText(removeDecimalPlaces(message.getData()
						.getString("number")));

				break;
			case Constants.THREAD_WRITE_JITTER_DATA:
				if (message.getData().getBoolean("redText")) {
					jitterText.setTextColor(Color.RED);
				} else {
					jitterText.setTextColor(Color.WHITE);
				}

				if (message.getData().getBoolean("numbersHidden")) {
					jitterNum.setVisibility(View.GONE);
					jitterUnits.setVisibility(View.GONE);
				} else {
					jitterNum.setVisibility(View.VISIBLE);
					jitterUnits.setVisibility(View.VISIBLE);
				}
				jitterText.setText(message.getData().getString("text"));
				jitterNum.setText(removeDecimalPlaces(message.getData()
						.getString("number")));
				break;
			case Constants.FINISH_PHASE_1:
				resultsView.startAnimation(slideOut);
				resultsView.setVisibility(View.INVISIBLE);
				break;
			case Constants.THREAD_START_UPLOAD_TIMER:
				smoothUpload = 0.0f;
				UploadTimer.scheduleAtFixedRate(uploadTask, 0, 500);
				break;
			case Constants.THREAD_STOP_UPLOAD_TIMER:
				UploadTimer.cancel();
				UploadTimer.purge();
				UploadTimer = null;
				setupUploadTimer();
				break;
			case Constants.THREAD_UPDATE_UPLOAD_NUMBER:
				if (Constants.UploadDebug)
					Log.v("debug", "in handler update Upload Timer number="
							+ smoothUpload.toString());
				if (smoothUpload != 0.0f) {
					uploadNum.setText(removeDecimalPlaces(smoothUpload
							.toString()));
				}
				break;
			case Constants.THREAD_SET_UPLOAD_NUMBER:
				String num2 = message.getData().getString("number");
				if (Constants.UploadDebug)
					Log.v("debug", "in handler set upload number number="
							+ num2);
				uploadNum.setText(removeDecimalPlaces(message.getData()
						.getString("number")));
				break;
			case Constants.THREAD_SET_UPLOAD_NUMBER_STOP_TIMER:
				UploadTimer.cancel();
				UploadTimer.purge();
				UploadTimer = null;
				String num1 = message.getData().getString("number");
				if (Constants.UploadDebug)
					Log.v("debug", "in handler stop Upload Timer number="
							+ num1);
				uploadNum.setText(removeDecimalPlaces(message.getData()
						.getString("number")));
				setupUploadTimer();
				break;
			case Constants.THREAD_START_DOWNLOAD_TIMER:
				smoothDownload = 0.0f;
				DownloadTimer.scheduleAtFixedRate(downloadTask, 0, 500);
				break;
			case Constants.THREAD_STOP_DOWNLOAD_TIMER:
				DownloadTimer.cancel();
				DownloadTimer.purge();
				setupDownloadTimer();
				break;
			case Constants.THREAD_UPDATE_DOWNLOAD_NUMBER:
				if (smoothDownload != 0.0f) {
					downloadNum.setText(removeDecimalPlaces(smoothDownload
							.toString()));
				}
				break;
			case Constants.THREAD_SET_DOWNLOAD_NUMBER:
				downloadNum.setText(removeDecimalPlaces(message.getData()
						.getString("number")));
				break;
			case Constants.THREAD_SET_DOWNLOAD_NUMBER_STOP_TIMER:
				DownloadTimer.cancel();
				DownloadTimer.purge();
				DownloadTimer = null;
				downloadNum.setText(removeDecimalPlaces(message.getData()
						.getString("number")));
				setupDownloadTimer();
				break;
			default:
				break;
			}
		}

	}

	private void startTest(String location) {
		String s1 = Constants.SERVER_HOST[0];
		String s2 = Constants.SERVER_HOST[1];
		Thread netWorker = new Thread(new StandardTest((Calspeed) context, s1,
				s2, uiServices, getNetworkType(), assetManager, ndtLocation,
				applicationFilesDir, DeviceId, date, startLongitude,
				startLatitude, statistics, location, TCPPort, UDPPort));
		netWorker.start();
	}

	private void resetResults() {
		uiServices.setResults(Constants.THREAD_WRITE_UPLOAD_DATA,
				"Upload Speed", "0", false, false);
		uiServices.setUploadNumber("0");
		uiServices.setResults(Constants.THREAD_WRITE_DOWNLOAD_DATA,
				"Download Speed", "0", false, false);
		uiServices.setDownloadNumber("0");
		uiServices.setResults(Constants.THREAD_WRITE_LATENCY_DATA, "Delay",
				"0", false, false);
		uiServices.setResults(Constants.THREAD_WRITE_JITTER_DATA,
				"Delay Variation", "0", false, false);

	}

	private String removeDecimalPlaces(String value) {
		try {
			if (Constants.DEBUG)
				Log.v("Debug", "In removeDecimalPlaces value=" + value);
			Long numInt = Math.round(Double.parseDouble(value));
			if (Constants.DEBUG)
				Log.v("Debug", numInt.toString() + "");
			return numInt.toString() + "";
		} catch (Exception e) {
			if (Constants.DEBUG)
				Log.v("Debug", "removeDecimalPlaces Exception");
			return ("0");
		}
	}

	private class SlideOutAnimationListener implements AnimationListener {
		@Override
		public void onAnimationEnd(Animation arg0) {
			resetResults();
			resultsView.setVisibility(View.VISIBLE);
			resultsView.startAnimation(slideIn);
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
			// Nothing

		}

		@Override
		public void onAnimationStart(Animation arg0) {
			// Nothing

		}

	}
}
