// Original work: Copyright 2009 Google Inc. All Rights Reserved.
//
// Modified work: The original source code comes from the NDT Android app
//                that is available from http://code.google.com/p/ndt/.
//                It's modified for the CalSPEED Android app by California 
//                State University Monterey Bay (CSUMB) on April 29, 2013.
//

package gov.ca.cpuc.calspeed.android;

/**
 * Definition for constant values .
 */
public class Constants {
	/* SMOOTHING NUMBER BETWEEN 0 AND 1 FOR LOW PASS FILTERING
	 * 
	 */
	public static final float SMOOTH= 0.15f;
	
  /**
   * Maximum test steps for ProgressBar setting.
   */
  public static final int TEST_STEPS = 18;

  public static final String privacyPolicyURL="http://www.cpuc.ca.gov/PUC/Telco/Information+for+providing+service/Broadband+Availability+Maps.htm";
 

  // Messages submitted from working thread to UI
  public static final int THREAD_MAIN_APPEND = 0;
  public static final int THREAD_STAT_APPEND = 1;
  public static final int THREAD_BEGIN_TEST = 2;
  public static final int THREAD_END_TEST = 3;
  public static final int THREAD_ADD_PROGRESS = 4;
  public static final int THREAD_SUMMARY_APPEND = 5;
  public static final int THREAD_LAT_LONG_APPEND = 6;
  public static final int THREAD_SET_PROCESS_HANDLE = 7;
  public static final int THREAD_CLEAR_PROCESS_HANDLE = 8;
  public static final int THREAD_GOOD_GPS_SIGNAL = 9;
  public static final int THREAD_NO_GPS_SIGNAL = 10;
  public static final int THREAD_UPDATE_LATLONG = 11;
  public static final int THREAD_RESULTS_SAVED = 12;
  public static final int THREAD_RESULTS_NOT_SAVED = 13;
  public static final int THREAD_RESULTS_UPLOADED = 14;
  public static final int THREAD_RESULTS_NOT_UPLOADED = 15;
  public static final int THREAD_RESULTS_ATTEMP_UPLOAD = 16;
  public static final int THREAD_ADD_TO_RESULTS_LIST = 17;
  public static final int THREAD_UPDATE_RESULTS_LIST = 18;
  public static final int THREAD_TEST_TIMED_OUT = 19;
  public static final int THREAD_TEST_INTERRUPTED = 20;
  public static final int THREAD_SET_STATUS_TEXT = 21;
  public static final int THREAD_PRINT_BSSID_SSID = 22;
  public static final int THREAD_NO_MOBILE_CONNECTION = 23;
  public static final int THREAD_GOT_MOBILE_CONNECTION = 24;
  public static final int THREAD_START_ANIMATION = 25;
  public static final int THREAD_STOP_ANIMATION = 26;
  public static final int THREAD_WRITE_UPLOAD_DATA = 27;
  public static final int THREAD_WRITE_DOWNLOAD_DATA = 28;
  public static final int THREAD_WRITE_LATENCY_DATA = 29;
  public static final int THREAD_WRITE_JITTER_DATA = 30;
  
  public static final int THREAD_CONNECTIVITY_FAIL = 31;
  public static final int FINISH_PHASE_1 = 32;
  public static final int THREAD_START_UPLOAD_TIMER = 33;
  public static final int THREAD_STOP_UPLOAD_TIMER = 34;
  public static final int THREAD_START_DOWNLOAD_TIMER = 35;
  public static final int THREAD_STOP_DOWNLOAD_TIMER = 36;
  public static final int THREAD_UPDATE_DOWNLOAD_NUMBER = 37;
  public static final int THREAD_UPDATE_UPLOAD_NUMBER = 38;
  public static final int THREAD_SET_DOWNLOAD_NUMBER = 39;
  public static final int THREAD_SET_DOWNLOAD_NUMBER_STOP_TIMER = 40;
  public static final int THREAD_SET_UPLOAD_NUMBER = 41;
  public static final int THREAD_SET_UPLOAD_NUMBER_STOP_TIMER = 42;
  
  
  public static final Integer IPERF_TCP_THREADS = 8;    //number of -P option for TCP test
  public static final Integer IPERF_TCP_LOWEST_THREAD_NUM = 3; // lowest -P iperf thread number used [X]
  public static final Integer NUM_UDP_TESTS_PER_SERVER = 4;       // number of total 1 sec and 5 sec tests
  public static final Integer NUM_TCP_TESTS_PER_SERVER = 2;       // number of TCP tests per server
  public static final Double IPERF_BIG_NUMBER_ERROR = 9999999999.99; //iperf error puts big number in kbytes/sec data
  public static final Integer NUM_TOTAL_TESTS = 15;  // Total number of tests
  // Iperf test errorState values
  public static final Integer IPERF_TEST_NO_ERRORS = 0;  // no errors
  public static final Integer IPERF_TEST_TIMEOUT = 1;  // timeout errorState
  public static final Integer IPERF_TEST_WRITE1_FAILED = 2; // write1 failed errorState
  public static final Integer IPERF_TEST_WRITE2_FAILED = 3; // write2 failed errorState
  public static final Integer IPERF_TEST_CONNECT_FAILED = 4; // connect failed errorState
  
  public static final boolean DEBUG = false;
  public static final boolean UploadDebug = false;
  public static final boolean DownloadDebug = false;
   
  public static final int IPERF_TCP_TIMEOUT = 60000;
  public static final int IPERF_UDP_TIMEOUT = 60000;
  public static final int PING_TIMEOUT = 30000;
  
  public static final int MIN_LOCATION = 1000;
  public static final int MAX_LOCATION = 3000;
 
 
  
  public static final String dataServers[]={
	  "127.0.0.1", //Update this to your database server
  };
  
//Network (physical layer) types
	public static final String NETWORK_WIFI = "WIFI";
	public static final String NETWORK_MOBILE = "MOBILE";
	public static final String NETWORK_WIRED = "WIRED";
	public static final String NETWORK_UNKNOWN = "UNKNOWN";
  
  // Network type from call TelephonyManager.getNetworkType()
  public static final String NETWORK_TYPE[][]= {
	  {"7","1xRTT"},
	  {"4","CDMA"},
	  {"2","EDGE"},
	  {"14","EHRPD"},
	  {"5","EVDO REV 0"},
	  {"6","EVDO REV A"},
	  {"12","EVDO REV B"},
	  {"1","GPRS"},
	  {"8","HSDPA"},
	  {"10","HSPA"},
	  {"15","HSPAP"},
	  {"9","HSUPA"},
	  {"11","IDEN"},
	  {"13","LTE"},
	  {"3","UMTS"},
	  {"0","UNKNOWN"}
  };
  // Use with TelephonyManager.getDataState
  public static final int DATA_CONNECTED = 2;
  
  public static final boolean CHECK_FOR_WIFI= true;
 

  // ID of Activities
  public static final int ACTIVITY_OPTION = 0;
  public static final int ACTIVITY_STATISTICS = 1;
  public static final int EXIT_ACTIVITY = 2;
 
  
  public static final int DEFAULT_SERVER = 0;
  public static final String SERVER_NAME[] =
      {"Iperf server 1", "Iperf server 2"};
  public static final String SERVER_HOST[] =
     {"127.0.0.1", "127.0.0.1"}; // Update to two Iperf servers
 
  public static final String ports[] =
	  {"5001", "5002", "5003", "5004", "5005", "5006", "5007", "5008", "5009", "5010"};
  
  //Port numbers for TCP and UDP dependent upon Carrier
  public static final int ATT_TCP_Port = 5001;
  public static final int ATT_UDP_Port = 5002;
  public static final int Sprint_TCP_Port = 5003;
  public static final int Sprint_UDP_Port = 5004;
  public static final int TMobile_TCP_Port = 5005;
  public static final int TMobile_UDP_Port = 5006;
  public static final int Verizon_TCP_Port = 5007;
  public static final int Verizon_UDP_Port = 5008;
  public static final int Other_TCP_Port = 5009;
  public static final int Other_UDP_Port = 5010;

  /**
   * Number of servers. All the arrays should have the same length.
   */
  public static final int NUMBER_OF_SERVERS = SERVER_NAME.length;


  private Constants() {
    // private constructor to make sure it can't be instantiated
  }
}
