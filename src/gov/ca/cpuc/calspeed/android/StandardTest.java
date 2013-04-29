/*
 Copyright 2003 University of Chicago.  All rights reserved.
 The Web100 Network Diagnostic Tool (NDT) is distributed subject to
 the following license conditions:
 SOFTWARE LICENSE AGREEMENT
 Software: Web100 Network Diagnostic Tool (NDT)

 1. The "Software", below, refers to the Web100 Network Diagnostic Tool (NDT)
 (in either source code, or binary form and accompanying documentation). Each
 licensee is addressed as "you" or "Licensee."

 2. The copyright holder shown above hereby grants Licensee a royalty-free
 nonexclusive license, subject to the limitations stated herein and U.S. Government
 license rights.

 3. You may modify and make a copy or copies of the Software for use within your
 organization, if you meet the following conditions:
 a. Copies in source code must include the copyright notice and this Software
 License Agreement.
 b. Copies in binary form must include the copyright notice and this Software
 License Agreement in the documentation and/or other materials provided with the copy.

 4. You may make a copy, or modify a copy or copies of the Software or any
 portion of it, thus forming a work based on the Software, and distribute copies
 outside your organization, if you meet all of the following conditions:
 a. Copies in source code must include the copyright notice and this
 Software License Agreement;
 b. Copies in binary form must include the copyright notice and this
 Software License Agreement in the documentation and/or other materials
 provided with the copy;
 c. Modified copies and works based on the Software must carry prominent
 notices stating that you changed specified portions of the Software.

 5. Portions of the Software resulted from work developed under a U.S. Government
 contract and are subject to the following license: the Government is granted
 for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable
 worldwide license in this computer software to reproduce, prepare derivative
 works, and perform publicly and display publicly.

 6. WARRANTY DISCLAIMER. THE SOFTWARE IS SUPPLIED "AS IS" WITHOUT WARRANTY
 OF ANY KIND. THE COPYRIGHT HOLDER, THE UNITED STATES, THE UNITED STATES
 DEPARTMENT OF ENERGY, AND THEIR EMPLOYEES: (1) DISCLAIM ANY WARRANTIES,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES
 OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE OR NON-INFRINGEMENT,
 (2) DO NOT ASSUME ANY LEGAL LIABILITY OR RESPONSIBILITY FOR THE ACCURACY,
 COMPLETENESS, OR USEFULNESS OF THE SOFTWARE, (3) DO NOT REPRESENT THAT USE
 OF THE SOFTWARE WOULD NOT INFRINGE PRIVATELY OWNED RIGHTS, (4) DO NOT WARRANT
 THAT THE SOFTWARE WILL FUNCTION UNINTERRUPTED, THAT IT IS ERROR-FREE OR THAT
 ANY ERRORS WILL BE CORRECTED.

 7. LIMITATION OF LIABILITY. IN NO EVENT WILL THE COPYRIGHT HOLDER, THE
 UNITED STATES, THE UNITED STATES DEPARTMENT OF ENERGY, OR THEIR EMPLOYEES:
 BE LIABLE FOR ANY INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL OR PUNITIVE
 DAMAGES OF ANY KIND OR NATURE, INCLUDING BUT NOT LIMITED TO LOSS OF PROFITS
 OR LOSS OF DATA, FOR ANY REASON WHATSOEVER, WHETHER SUCH LIABILITY IS ASSERTED
 ON THE BASIS OF CONTRACT, TORT (INCLUDING NEGLIGENCE OR STRICT LIABILITY), OR
 OTHERWISE, EVEN IF ANY OF SAID PARTIES HAS BEEN WARNED OF THE POSSIBILITY OF
 SUCH LOSS OR DAMAGES.
 The Software was developed at least in part by the University of Chicago,
 as Operator of Argonne National Laboratory (http://miranda.ctd.anl.gov:7123/).
 */

//
// Modified work: The original source code (NdtTests.java) comes from the NDT Android app
//                that is available from http://code.google.com/p/ndt/.
//                It's modified for the CalSPEED Android app by California 
//                State University Monterey Bay (CSUMB) on April 29, 2013.
// 
 
package gov.ca.cpuc.calspeed.android;

import android.os.Bundle;
import gov.ca.cpuc.calspeed.android.Constants;
import gov.ca.cpuc.calspeed.android.SaveResults;
import gov.ca.cpuc.calspeed.android.Calspeed.LatLong;
import gov.ca.cpuc.calspeed.android.AndroidUiServices.TextOutputAdapter;

import java.io.*;
import java.util.concurrent.TimeoutException;
import java.net.*;
import java.util.*;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.content.Context;
import android.view.*;


public class StandardTest implements Runnable {
	public static final String VERSION = "3.6.3";

	// Network (physical layer) types
	public static final String NETWORK_WIFI = "WIFI";
	public static final String NETWORK_MOBILE = "MOBILE";
	public static final String NETWORK_WIRED = "WIRED";
	public static final String NETWORK_UNKNOWN = "UNKNOWN";

	

	private static final int RUNCOMMAND_SUCCESS = 0;
	private static final int RUNCOMMAND_FAIL = 1;
	private static final int RUNCOMMAND_INTERRUPT = 2;
	private static final String PING_100_PERCENT = "100% packet loss";
	private static final int PING_100_PERCENT_LOSS = 3;
	private static final int PING_CONNECTIVITY_FAIL = 4;

	private TextOutputAdapter statistics, results, summary;
	private int runStatus;
	private String textResults;

	private ResourceBundle messages;

	private final String server1;
	private final String server2;
	private final AndroidUiServices uiServices;
	private final String networkType;
	private final AssetManager assetManager;
	private final NdtLocation ndtLocation;
	private final String applicationFilesDir;
	private final String DeviceId;
	private final Date date;
	private final Double longitude;
	private final Double latitude;
	private final String locationID;
	public ExecCommandLine command;
	private LatLong testLatLong;
	private Calspeed context;
	private TextView topText2;
	private TextView topText;
	private ImageView green;
	private ProcessPing pingConnectivity;
	private ProcessPing pingStatsEast;
	private ProcessPing pingStatsWest;
	private ProcessIperf[] tcpResultsWest;
	private ProcessIperf[] tcpResultsEast;
	private ProcessIperf[] udpResultsWest;
	private ProcessIperf[] udpResultsEast;
	private String TCPPort;
	private String UDPPort;
	Thread currentThread;

	/*
	 * Initializes the network test thread.
	 * 
	 * @param host hostname of the test server
	 * 
	 * @param uiServices object for UI interaction
	 * 
	 * @param networkType indicates the type of network, e.g. 3G, Wifi, Wired,
	 * etc.
	 */
	public StandardTest(Calspeed context, String server1, String server2,
			AndroidUiServices uiServices, String networkType,
			AssetManager assetManager, NdtLocation ndtLocation,
			String applicationFilesDir, String DeviceId, Date date,
			Double startLongitude, Double startLatitude, String textResults,
			String locationID, String TCPPort, String UDPPort) {
		this.server1 = server1;
		this.server2 = server2;
		this.uiServices = uiServices;
		this.networkType = networkType;
		this.assetManager = assetManager;
		this.ndtLocation = ndtLocation;
		this.applicationFilesDir = applicationFilesDir;
		this.DeviceId = DeviceId;
		this.date = date;
		this.longitude = startLongitude;
		this.latitude = startLatitude;
		this.textResults = textResults;
		this.locationID = locationID;
		this.command = null;
		this.context = context;
		this.testLatLong = context.new LatLong();
		this.testLatLong.getLatitudeLongitude(testLatLong);
		this.TCPPort = TCPPort;
		this.UDPPort = UDPPort;

		setupResultsObjects();

		statistics = new TextOutputAdapter(uiServices, UiServices.STAT_VIEW);
		results = new TextOutputAdapter(uiServices, UiServices.MAIN_VIEW);
		summary = new TextOutputAdapter(uiServices, UiServices.SUMMARY_VIEW);

		try {
			messages = ResourceBundle.getBundle(
					"resourcebundles.Tcpbw100_msgs_en_US", Locale.getDefault());
		} catch (MissingResourceException e) {
			// Fall back to US English if the locale we want is missing
			messages = ResourceBundle.getBundle(
					"resourcebundles.Tcpbw100_msgs_en_US", new Locale("en",
							"US"));
		}
	}


	private void printLatLong() {

		testLatLong.getLatitudeLongitude(testLatLong);
		showResults("\nLatitude:" + testLatLong.Latitude);
		showResults("\nLongitude:" + testLatLong.Longitude);
		String Lat0 = Double.toString(testLatLong.Latitude);
		statistics.append("\nLatitude:" + Lat0);
		String Long0 = Double.toString(testLatLong.Longitude);
		statistics.append("\nLongitude:" + Long0);
		uiServices.upDateLatLong();
	}

	

	public void run() {
		try {
			uiServices.onBeginTest();

			printLatLong();

			showResults("\nChecking Connectivity.....\n");
			statistics.append("\nChecking Connectivity.....\n");
			printToSummary("\nChecking Connectivity.....\n");


			uiServices.setStatusText("Testing in progress...");
			MoveProgressBar(1);
			for (int i = 0; i < 3; i++) {
				runStatus = RunCommand("Ping Quick Test", "ping -c 4 "
						+ server1, null, pingConnectivity);
				if ((runStatus != PING_100_PERCENT_LOSS)
						&& (runStatus != RUNCOMMAND_INTERRUPT)) {
					break;  // success
				}
				waiting(3);
			}
			if (runStatus == PING_100_PERCENT_LOSS) {
				ndtLocation.stopListen();		
				showResults("\nConnectivity Test Failed--Exiting Test.\n");
				printToSummary("\nConnectivity Test Failed--Exiting Test.\n");
				runStatus = PING_CONNECTIVITY_FAIL;
				uiServices.setStatusText("No Network Connection.");
				SaveAllResults();
				uiServices.onEndTest();
				
			} else if (runStatus == RUNCOMMAND_INTERRUPT){
				Log.v("LAWDebug", "RUNCOMMAND_INTERRUPT.");
				ndtLocation.stopListen();
				showResults("\nConnectivity Test Failed--Exiting Test.\n");
				printToSummary("\nConnectivity Test Failed--Exiting Test.\n");
				runStatus = PING_CONNECTIVITY_FAIL;
			} else {
				Log.v("LAWDebug", "ELSE.");
				printLatLong();

				showResults("\nStarting Test 1: Iperf TCP West....\n");
				printToSummary("\nStarting Test 1....\n");

				statistics.append("\nStarting Test 1: Iperf TCP West....\n");
				
				RunCommand(
						"Iperf",
						this.applicationFilesDir + "/iperfT -c "
								+ server1
								+ " -e -w 32k -P 4 -i 1 -t 10 -f k -p "
								+ TCPPort, tcpResultsWest[0], null);

				tcpResultsWest[0].SetPhase1TCPIperfStatus();

				MoveProgressBar(2);
				
				printLatLong();
				
				showResults("\nStarting Test 2: Ping West....\n");
				printToSummary("\nStarting Test 2....\n");

				statistics.append("\nStarting Test 2: Ping West....\n");
		
				RunCommand("Ping", "ping -c 10 " + server1, null,
						pingStatsWest);

				printLatLong();

				
				pingStatsWest.SetPingFinalStatus("West Server");
				MoveProgressBar(2);
				
				showResults("\nStarting Test 3: Iperf West UDP 1 second test....\n");
				printToSummary("\nStarting Test 3....\n");

				statistics
						.append("\nStarting Test 3: Iperf West UDP 1 second test....\n");
				RunCommandUDPWait(
						Constants.SERVER_NAME[0],
						this.applicationFilesDir + "/iperfT -c "
								+ server1
								+ " -u -l 220 -b 88k -i 1 -t 1 -f k -p "
								+ UDPPort, udpResultsWest, 1);

				

				MoveProgressBar(2);
				
				waiting(3);
				
				uiServices.phase1Complete();
				uiServices.setStatusText("Validating Results...");

				printLatLong();

				tcpResultsEast[0].setTCPPhase2(tcpResultsWest[0].uploadSpeed, tcpResultsWest[0].downloadSpeed);
				
				showResults("\nStarting Test 4: Iperf TCP East....\n");
				printToSummary("\nStarting Test 4....\n");

				statistics.append("\nStarting Test 4: Iperf TCP East....\n");
				
				RunCommand(
						"Iperf",
						this.applicationFilesDir + "/iperfT -c "
								+ server2
								+ " -e -w 32k -P 4 -i 1 -t 10 -f k -p "
								+ TCPPort, tcpResultsEast[0], null);

				tcpResultsEast[0].SetIperfTCPAvgFinal();
				

				
				printLatLong();

				MoveProgressBar(2);
				
				
				

				showResults("\nStarting Test 5: Ping East....\n");
				printToSummary("\nStarting Test 5....\n");

				statistics.append("\nStarting Test 5: Ping East....\n");

				pingStatsEast.setPhase2(pingStatsWest.average);
				RunCommand("Ping", "ping -c 10 " + server2, null,
						pingStatsEast);

				pingStatsEast.SetPingFinalStatus("West Server");
				SetPingAvgFinal(pingStatsWest,pingStatsEast);

				printLatLong();

				MoveProgressBar(2);
				
				showResults("\nStarting Test 6: Iperf East UDP 1 second test....\n");
				printToSummary("\nStarting Test 6....\n");

				statistics
						.append("\nStarting Test 6: Iperf East UDP 1 second test....\n");
				
				setUDPPhase2(udpResultsEast);
				RunCommandUDPWait(
						Constants.SERVER_NAME[1],
						this.applicationFilesDir + "/iperfT -c "
								+ server2
								+ " -u -l 220 -b 88k -i 1 -t 1 -f k -p "
								+ UDPPort, udpResultsEast, 1);
				
				setUDPJitterFinal(udpResultsWest[0],udpResultsEast[0]);

				printLatLong();

				MoveProgressBar(2);
	
				runStatus = SaveAllResults();
			}
		
			ndtLocation.stopListen();

			// Finish the Test
			if (runStatus == RUNCOMMAND_INTERRUPT){
				uiServices.setStatusText("Test Interrupted.");
				uiServices.onTestInterrupt();
			}else if (runStatus == RUNCOMMAND_FAIL){
				MoveProgressBar(1);
				//uiServices.setStatusText("Results Not Processed.");
			}else if (runStatus == PING_CONNECTIVITY_FAIL){
				MoveProgressBar(20);
				uiServices.setStatusText("No Network Connection.");
			} else {
				MoveProgressBar(1);
				Log.v("LAWDebug", "Else.");
				uiServices.setStatusText("Test Complete.");
			}

		} catch (InterruptedException e) {
			showResults("\nQuitting Operations...\n");
			printToSummary("\nStandard Test Interrupted...\n");
			uiServices.setStatusText("Test Interrupted.");
		
			uiServices.onTestInterrupt();
			return;
		}
		
		uiServices.onEndTest();
		return;
	}
	public void MoveProgressBar(Integer increment){
		uiServices.incrementProgress(increment);
		try{
			Thread.currentThread().sleep(2000);
		}catch(Exception e){
			//do nothing
		}
	}
	private void resetResults(){
		uiServices.setResults(Constants.THREAD_WRITE_UPLOAD_DATA, "Upload Speed", "0", false, false);
		uiServices.setResults( Constants.THREAD_WRITE_DOWNLOAD_DATA,"Download Speed", "0", false, false);
		uiServices.setResults(Constants.THREAD_WRITE_LATENCY_DATA,"Delay", "0", false, false);
		uiServices.setResults(Constants.THREAD_WRITE_JITTER_DATA,"Delay Variation", "0", false, false);
	}
	private void SetPingAvgFinal(ProcessPing Ping1,ProcessPing Ping2){
		Float avg = 0.0f;
		Boolean pingStatusFailed = false;
		String pingMessage = "Delay";
		
		if (Ping1.success){
			if (Ping2.success){
				avg = (Float.parseFloat(Ping1.average) + Float.parseFloat(Ping2.average))/2;
			}else{
				avg = Float.parseFloat(Ping1.average);
			}
		}else{
			if (Ping2.success){
				avg = Float.parseFloat(Ping2.average);
			}else{
				avg = 0.0f;
				pingMessage = "Delay Incomplete";
				pingStatusFailed = true;
			}
		}
		uiServices.setResults(Constants.THREAD_WRITE_LATENCY_DATA, pingMessage,ProcessIperf.formatFloatString(avg.toString()), pingStatusFailed, pingStatusFailed);	
	}
	private void setUDPPhase2(ProcessIperf[] udpIperfTest){
		for (int i = 0; i < Constants.NUM_UDP_TESTS_PER_SERVER - 1; i++) {
			udpResultsEast[i].setUDPPhase2();
		}
	}
	private void setUDPJitterFinal(ProcessIperf udp1,ProcessIperf udp2){
		Float avg = 0.0f;
		Boolean udpStatusFailed = false;
		String udpMessage = "Delay Variation";
		
		if (udp1.udpSuccess){
			if (udp2.udpSuccess){
				avg = (Float.parseFloat(udp1.jitter) + Float.parseFloat(udp2.jitter))/2;
			}else{
				avg = Float.parseFloat(udp1.jitter);
			}
		}else{
			if (udp2.udpSuccess){
				avg = Float.parseFloat(udp2.jitter);
			}else{
				avg = 0.0f;
				udpMessage = "Delay Variation Incomplete";
				udpStatusFailed = true;
			}
		}
		uiServices.setResults(Constants.THREAD_WRITE_JITTER_DATA, udpMessage,ProcessIperf.formatFloatString(avg.toString()), udpStatusFailed, udpStatusFailed);
	}
	private Integer SaveAllResults() {
		Integer returnStatus = RUNCOMMAND_SUCCESS;
		try {
			showResults("\nSaving Results to sdcard...\n");
			statistics.append("\nSaving Results to sdcard...\n");
			printToSummary("\nSaving Results...\n");
			SaveResults localResult = new SaveResults(results, summary,
					textResults, DeviceId, date, testLatLong);
			String status = localResult.saveResultsLocally();
			showResults(status);

			if (status.indexOf("successfully") > 0) {
				printToSummary("File successfully saved.\n");
				statistics.append("File successfully saved.\n");
			} else {
				printToSummary(status);
				statistics.append(status);
				uiServices.resultsNotSaved();
				uiServices.setStatusText("Results Not Saved.");
				returnStatus = RUNCOMMAND_FAIL;
			}
			
			showResults("\nAttempting Upload to Server...\n");
			printToSummary("\nAttempting Upload to Server...\n");
			statistics.append("\nAttempting Upload to Server...\n");
			
			Timer timer = new Timer();
			currentThread = Thread.currentThread();
			TimerTask task = new TimerTask(){
	            @Override
	            public void run() {
	            		printToSummary("Upload Timeout. \n");
	            		uiServices.setStatusText("Test Complete");
	            		if (Constants.DEBUG)
	            			Log.v("LAWDebug", "Upload Timeout");
	            		runStatus = Constants.THREAD_RESULTS_NOT_UPLOADED;
	            		currentThread = null;
	            		uiServices.onEndTest();
	            		this.cancel();
	                    return;
	            }    
	        };
		        
		    timer.schedule(task, 60000);
			
			if (localResult.uploadAllFiles(summary, results)) {
				timer.cancel();
				printToSummary("All Files successfully uploaded.\n");
				showResults("All Files successfully uploaded.\n");
				statistics.append("All Files successfully uploaded.\n");
				localResult.clearErrorMessage();
				
			} else {
				timer.cancel();
				printToSummary("Upload Failed. Try again later.\n");
				showResults("Upload Failed. Try again later.\n");
				statistics.append("Upload Failed. Try again later.\n");				
				uiServices.setStatusText("Test Complete.");
				if (Constants.DEBUG)
					Log.v("debug","Upload Failed");
				statistics.append(localResult.getErrorMessage());
				localResult.clearErrorMessage();
				returnStatus = RUNCOMMAND_FAIL;
			}
			
		} catch (InterruptedException e) {
		}
			
		return(returnStatus);
	}

	private void setupResultsObjects() {
		pingStatsEast = new ProcessPing("Delay",uiServices);
		pingStatsWest = new ProcessPing("Delay",uiServices);
		tcpResultsWest = new ProcessIperf[Constants.NUM_TCP_TESTS_PER_SERVER];
		tcpResultsEast = new ProcessIperf[Constants.NUM_TCP_TESTS_PER_SERVER];
		tcpResultsWest[0] = new ProcessIperf(Constants.SERVER_NAME[0],uiServices);
		tcpResultsWest[1] = new ProcessIperf(Constants.SERVER_NAME[0],uiServices);
		tcpResultsEast[0] = new ProcessIperf(Constants.SERVER_NAME[1],uiServices);
		tcpResultsEast[1] = new ProcessIperf(Constants.SERVER_NAME[1],uiServices);

		udpResultsWest = new ProcessIperf[Constants.NUM_UDP_TESTS_PER_SERVER];
		udpResultsEast = new ProcessIperf[Constants.NUM_UDP_TESTS_PER_SERVER];

		for (int i = 0; i < Constants.NUM_UDP_TESTS_PER_SERVER - 1; i++) {
			udpResultsWest[i] = new ProcessIperf("1", Constants.SERVER_NAME[0],uiServices);
			udpResultsEast[i] = new ProcessIperf("1", Constants.SERVER_NAME[1],uiServices);
		}
	}

	private void printToSummary(String message) {
		summary.append(message);
	}

	private void showResults(String message) {
		results.append(message);
		textResults += message;
	}

	private int RunCommand(String program, String commandline,
			ProcessIperf iperfTest, ProcessPing pingTest)
			throws InterruptedException {

		if (program.equals("Iperf")) {
			uiServices.setCurrentTask("Iperf");
			try {
				command = new ExecCommandLine(commandline,
						Constants.IPERF_TCP_TIMEOUT, results, iperfTest,
						pingTest,uiServices);
				uiServices.setProcessHandle(command.process);
				String commandOutput = command.runIperfCommand();
				textResults += commandOutput;
				statistics.append("\n" + commandOutput);
				if (command.commandTimedOut) {
					showResults("\nIperf timed out after "
							+ Constants.IPERF_TCP_TIMEOUT / 1000
							+ " seconds.\n");
					printToSummary("\nIperf timed out after "
							+ Constants.IPERF_TCP_TIMEOUT / 1000
							+ " seconds.\n");
					statistics.append("\nIperf timed out after "
							+ Constants.IPERF_TCP_TIMEOUT / 1000
							+ " seconds.\n");
					if (!iperfTest.finishedUploadTest){
						iperfTest.uploadSuccess=false;
					}
					uiServices.clearProcessHandle();
				}
			} catch (InterruptedException e) {
				uiServices.clearProcessHandle();
				throw new InterruptedException();

			} catch (TimeoutException e) {
				showResults("\nIperf timed out after "
						+ Constants.IPERF_TCP_TIMEOUT / 1000
						+ " seconds.\n");
				printToSummary("\nIperf timed out after "
						+ Constants.IPERF_TCP_TIMEOUT / 1000
						+ " seconds.\n");
				statistics.append("\nIperf timed out after "
						+ Constants.IPERF_TCP_TIMEOUT / 1000
						+ " seconds.\n");
				uiServices.clearProcessHandle();
				return (RUNCOMMAND_FAIL);
			}
			uiServices.clearProcessHandle();
			return (RUNCOMMAND_SUCCESS);

		} else if (program.equals("Iperf UDP")) {
			uiServices.setCurrentTask("Iperf");
			try {
				command = new ExecCommandLine(commandline,
						Constants.IPERF_UDP_TIMEOUT, results, iperfTest,
						pingTest,uiServices);
				uiServices.setProcessHandle(command.process);
				String commandOutput = command.runIperfCommand();
				textResults += commandOutput;
				statistics.append("\n" + commandOutput);
				if (command.commandTimedOut) {
					showResults("\nIperf timed out after "
							+ Constants.IPERF_UDP_TIMEOUT / 1000
							+ " seconds.\n");
					printToSummary("\nIperf timed out after "
							+ Constants.IPERF_UDP_TIMEOUT / 1000
							+ " seconds.\n");
					statistics.append("\nIperf timed out after "
							+ Constants.IPERF_UDP_TIMEOUT / 1000
							+ " seconds.\n");
					uiServices.clearProcessHandle();
				}
			} catch (InterruptedException e) {
				uiServices.clearProcessHandle();
				throw new InterruptedException();

			} catch (TimeoutException e) {
				showResults("\n" + e);
				printToSummary("\n" + e);
				statistics.append("\n" + e);
				uiServices.clearProcessHandle();
				return (RUNCOMMAND_FAIL);
			}
			uiServices.clearProcessHandle();
			return (RUNCOMMAND_SUCCESS);

		} else if (program.equals("Iperf UDP Wait")) { // Not Used--superceded
														// by RunCommandUDPWait
			uiServices.setCurrentTask("Iperf");
			int udpSeconds;
			int count = 0;
			for (udpSeconds = 3; udpSeconds > 0; udpSeconds--) {
				try {
					count++;
					showResults("\nStarting UDP 1 second Test #" + count + "\n");
					statistics.append("\nStarting UDP 1 second Test #" + count
							+ "\n");
					command = new ExecCommandLine(commandline,
							Constants.IPERF_UDP_TIMEOUT, results, iperfTest,
							pingTest,uiServices);
					uiServices.setProcessHandle(command.process);
					String commandOutput = command.runIperfCommand();
					textResults += commandOutput;
					statistics.append("\n" + commandOutput);
					if (command.commandTimedOut) {
						showResults("\nIperf timed out after "
								+ Constants.IPERF_UDP_TIMEOUT / 1000
								+ " seconds.\n");
						printToSummary("\nIperf timed out after "
								+ Constants.IPERF_UDP_TIMEOUT / 1000
								+ " seconds.\n");
						statistics.append("\nIperf timed out after "
								+ Constants.IPERF_UDP_TIMEOUT / 1000
								+ " seconds.\n");
						uiServices.clearProcessHandle();
					}

				} catch (InterruptedException e) {
					uiServices.clearProcessHandle();
					throw new InterruptedException();

				} catch (TimeoutException e) {
					results.append("\n" + e);
					statistics.append("\n" + e);
					uiServices.clearProcessHandle();
					return (RUNCOMMAND_FAIL);
				}
				waiting(1);
			}
			command = null;
			return (RUNCOMMAND_SUCCESS);
		} else {
			// Ping command
			uiServices.setCurrentTask(program);
			String commandOutput = null;
			try {
				ExecCommandLine command = new ExecCommandLine(commandline,
						Constants.PING_TIMEOUT, results, iperfTest, pingTest,uiServices);
				commandOutput = command.runCommand();
				showResults("\n" + commandOutput);
				statistics.append("\n" + commandOutput);
				if (command.commandTimedOut) {
					showResults("\nPing timed out after "
							+ Constants.PING_TIMEOUT / 1000 + " seconds.\n");
					printToSummary("\nPing timed out after "
							+ Constants.PING_TIMEOUT / 1000 + " seconds.\n");
					statistics.append("\nPing timed out after "
							+ Constants.PING_TIMEOUT / 1000 + " seconds.\n");
					pingTest.SetPingFail("Test Timed Out.");
				}
			} catch (InterruptedException e) {
				if (program.equals("Ping Quick Test")) {
					return (RUNCOMMAND_INTERRUPT);
				} else {
					throw new InterruptedException();
				}
			}
			if (program.equals("Ping Quick Test")) {
				if ((commandOutput.indexOf(PING_100_PERCENT) != -1)
						|| (!commandOutput.contains("rtt min"))) {
					return (PING_100_PERCENT_LOSS);
				} else {
					return (RUNCOMMAND_SUCCESS);
				}
			} else {
				return (RUNCOMMAND_SUCCESS);
			}
		}
	}

	private int RunCommandUDPWait(String server, String commandline,
			ProcessIperf[] iperfTest, Integer numTests)
			throws InterruptedException {

		uiServices.setCurrentTask("Iperf");

		for (int i = 0; i < numTests; i++) {
			try {

				showResults("\nStarting UDP 1 second Test #" + (i + 1) + "\n");
				statistics.append("\nStarting UDP 1 second Test #" + (i + 1)
						+ "\n");


				command = new ExecCommandLine(commandline,
						Constants.IPERF_UDP_TIMEOUT, results, iperfTest[i],
						null,uiServices);

				String commandOutput = command.runIperfCommand();
				textResults += commandOutput;

				statistics.append("\n" + commandOutput);
				if (command.commandTimedOut) {
					showResults("\nIperf timed out after "
							+ Constants.IPERF_UDP_TIMEOUT / 1000
							+ " seconds.\n");
					printToSummary("\nIperf timed out after "
							+ Constants.IPERF_UDP_TIMEOUT / 1000
							+ " seconds.\n");
					statistics.append("\nIperf timed out after "
							+ Constants.IPERF_UDP_TIMEOUT / 1000
							+ " seconds.\n");

				}

				iperfTest[i].SetUDPIperfFinalStatus();
				
			} catch (InterruptedException e) {
				uiServices.clearProcessHandle();
				throw new InterruptedException();

			} catch (TimeoutException e) {
				results.append("\n" + e);
				statistics.append("\n" + e);
				uiServices.clearProcessHandle();
				return (RUNCOMMAND_FAIL);
			}
			waiting(1);
		}
		command = null;
		return (RUNCOMMAND_SUCCESS);
	}

	public static void waiting(int n) {

		long t0, t1;

		t0 = System.currentTimeMillis();

		do {
			t1 = System.currentTimeMillis();
		} while ((t1 - t0) < (n * 1000));
	}

	class Message {
		byte type;
		byte[] body;
	}

}
