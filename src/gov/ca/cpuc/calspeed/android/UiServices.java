// Original work: Copyright 2009 Google Inc. All Rights Reserved.
//
// Modified work: The original source code comes from the NDT Android app
//                that is available from http://code.google.com/p/ndt/.
//                It's modified for the CalSPEED Android app by California 
//                State University Monterey Bay (CSUMB) on April 29, 2013.
//

package gov.ca.cpuc.calspeed.android;

/**
 * Provides platform-specific UI services. Defines several functions for
 * platform-specific classes to implement, to help the working thread dispatch
 * messages to the UI.
 *
 * <p>
 * Flow of the Interface:
 * <ol>
 * <li>Calls onBeginTest() on test begins.
 * <li>Calls appendString() whenever new output exists, could be sent to
 * different views.
 * <li>Calls increaseProgress() when a work step has been finished.
 * <li>Calls onFailure() when test failed, to leave an interface for some
 * platform-specific debug methods or just output some failure messages.
 * <li>Calls submitData() to pass the result variable into the main activity for
 * later reference (in email app, etc.).
 * <li>Calls onEndTest() to finish the test.
 * </ol>
 */
public interface UiServices {
  /**
   * Id refers to the main view for sending the output string.
   *
   * @see #appendString
   */
  public static final int MAIN_VIEW = 0;

  /**
   * Id refers to the statistics view for sending the output string.
   *
   * @see #appendString
   */
  public static final int STAT_VIEW = 1;

  /**
   * Id refers to the diagnose view for sending the output string.
   *
   * @see #appendString
   */
  public static final int DIAG_VIEW = 2;

  /**
   * Id refers to the debug view for sending the output string.
   *
   * @see #appendString
   */
  public static final int DEBUG_VIEW = 3;
  /**
   * Id refers to the summary view for minimal tester output window.
   *
   * @see #appendString
   */
  public static final int SUMMARY_VIEW = 4;


  /**
   * Sends message to the designated view.
   *
   * @param str the string which is sent to the statistics view
   * @param viewId id that indicates where the string should be sent to, could
   *        be {@link #MAIN_VIEW} or {@link #STAT_VIEW}
   */
  public void appendString(String str, int viewId);

  /**
   * Called each time a test step completes. Should be called a maximum of
   * TEST_STEPS times during the whole test.
   */
  public void incrementProgress(Integer increment);

  /**
   * Notifies the callee that the test is starting.
   */
  public void onBeginTest();

  /**
   * Notifies the callee that the test has ended.
   */
  public void onEndTest();

  /**
   * Notifies the callee to print Lat Long values.
   */
  public void printLatLong();
  /**
   * Called when the test ends abnormally. Should be called before onEndTest();
   */
  public void onFailure(String errorMessage);

  /**
   * Called when packet queuing is detected.
   */

  public void onPacketQueuingDetected();

  /**
   * Called when the test 'login' packet has been successfully sent.
   */

  public void onLoginSent();

  /**
   * Abstract to make the logging action generic in different platforms.
   *
   * @param str message which is sent to the log.
   */
  public void logError(String str);

  /**
   * Updates the status message, which indicates what test is running.
   */
  public void updateStatus(String status);

  /**
   * Updates the status panel. (Applet-specific)
   */
 
  public void updateStatusPanel(String status);

  /**
   * Returns true if the test should be aborted, false if it should continue.
   */

  public boolean wantToStop();

  // Hack for the Applet's JavaScript access API extension
  public void setVariable(String name, int value);
  public void setVariable(String name, double value);
  public void setVariable(String name, Object value);



}


