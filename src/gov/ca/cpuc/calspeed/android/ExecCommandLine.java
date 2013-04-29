/*
Copyright (c) 2013, California State University Monterey Bay (CSUMB).
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    1. Redistributions of source code must retain the above copyright notice,
       this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above
	   copyright notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

    3. Neither the name of the CPUC, CSU Monterey Bay, nor the names of
       its contributors may be used to endorse or promote products derived from
       this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package gov.ca.cpuc.calspeed.android;

import gov.ca.cpuc.calspeed.android.AndroidUiServices.TextOutputAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import java.lang.InterruptedException;


public class ExecCommandLine {

	private ProcessIperf iperfTest;
	private ProcessPing pingTest;
	private String commandline;
	public Process process;
	public Boolean commandTimedOut;
	private long timeout;
	private TextOutputAdapter display;
	private AndroidUiServices uiServices;

	public ExecCommandLine(String line, long timeout,
			TextOutputAdapter display, ProcessIperf iperfTest,
			ProcessPing pingTest, AndroidUiServices uiServices) {

		this.iperfTest = iperfTest;
		this.pingTest = pingTest;
		this.commandline = line;
		this.commandTimedOut = false;
		this.timeout = timeout;
		this.display = display;
		this.uiServices = uiServices;
	}

	public String runIperfCommand() throws TimeoutException,
			InterruptedException {
//		long startTime = 0;
		boolean finished = false;

		int read;
		char[] buffer = new char[4096];
		StringBuffer output = new StringBuffer();
		StringBuffer line = new StringBuffer();
		StringBuffer lineerror = new StringBuffer();
		int readerror;
		char[] buffererror = new char[4096];
		StringBuffer outputerror = new StringBuffer();
		Timer timer = new Timer();

		try {
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					commandTimedOut = true;
					if (!iperfTest.isUDP) {
						iperfTest.downloadSuccess = false;
						if (!iperfTest.finishedUploadTest) {
							iperfTest.uploadSuccess = false;
							iperfTest.uploadMessage = "Upload Incomplete";
						}
					}
					this.cancel();
					process.destroy();
					return;
				}
			};

			timer.schedule(task, timeout);
			if (!iperfTest.isUDP) {
				uiServices.startUploadTimer();
			}
			// Executes the command.
			this.process = Runtime.getRuntime().exec(commandline);

			output.append("\nIperf command line:" + commandline + "\n");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			BufferedReader readererror = new BufferedReader(
					new InputStreamReader(process.getErrorStream()));

			while (!finished) {
				try {

					while (((read = reader.read(buffer)) > 0)) {
						output.append(buffer, 0, read);
						line.delete(0, line.length());
						line.append(buffer, 0, read);
						if (this.display != null) {
							this.display.append(line.toString());
						}

						if (iperfTest != null & !commandTimedOut) {
							iperfTest.ProcessOutput(line.toString() + "\n");
							iperfTest.ProcessErrorOutput(line.toString());
						}
						if (Thread.currentThread().isInterrupted()) {
							process.destroy();
							reader.close();
							readererror.close();
							finished = true;
							timer.cancel();
							throw new InterruptedException();
						}

					}

					while (((readerror = readererror.read(buffererror)) > 0)) {
						outputerror.append(buffererror, 0, readerror);
						lineerror.delete(0, lineerror.length());
						lineerror.append(buffererror, 0, readerror);
						if (this.display != null) {
							this.display.append(lineerror.toString());
						}
						if (iperfTest != null) {
							iperfTest.ProcessErrorOutput(lineerror.toString()
									+ "\n");
						}
						if (Thread.currentThread().isInterrupted()) {
							process.destroy();
							reader.close();
							readererror.close();
							finished = true;
							timer.cancel();
							throw new InterruptedException();
						}
					}
					process.waitFor();
					timer.cancel();
					reader.close();
					readererror.close();
					return (output.toString() + outputerror.toString());
				} catch (IllegalThreadStateException ex) {
					// if process hasn't exited yet, so keep checking
				} catch (IOException e) {
					throw new RuntimeException();
				} catch (InterruptedException e) {
					process.destroy();
					reader.close();
					readererror.close();
					finished = true;
					timer.cancel();
					throw new InterruptedException();
				}

			}
		} catch (IOException e) {
			throw new RuntimeException();
		}
		return (output.toString() + outputerror.toString());
	}

	public String runCommand() throws InterruptedException {


		StringBuffer output = new StringBuffer();

		Timer timer = new Timer();

		try {
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					commandTimedOut = true;
					pingTest.success = false;
					this.cancel();
					process.destroy();
					return;
				}
			};

			timer.schedule(task, timeout);

			// Executes the command.
			this.process = Runtime.getRuntime().exec(commandline);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			BufferedReader readererror = new BufferedReader(
					new InputStreamReader(process.getErrorStream()));
			String line = null;

			while ((line = reader.readLine()) != null) {
				// output.append(buffer, 0, read);
				output.append(line + "\n");
				if (pingTest != null) {
					pingTest.ProcessOutput(line.toString() + "\n", "Phone");
				}
				if (Thread.currentThread().isInterrupted()) {
					timer.cancel();
					process.destroy();
					reader.close();
					readererror.close();
					throw new InterruptedException();
				}
			}
			line = null;
			while ((line = readererror.readLine()) != null) {
				output.append(line + "\n");
				if (Thread.currentThread().isInterrupted()) {
					timer.cancel();
					process.destroy();
					reader.close();
					readererror.close();
					throw new InterruptedException();
				}
			}
			process.waitFor();
			timer.cancel();
			reader.close();
			readererror.close();

			int exval = process.exitValue();
			if (exval != 0) {
				output.append("\nbad exit value--error executing command: "
						+ commandline);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (IllegalThreadStateException ex) {
			output.append("\nerror executing command: " + commandline);
			return output.toString();
		} catch (InterruptedException e) {
			process.destroy();

			timer.cancel();
			process.destroy();

			throw new InterruptedException();
		}
		return output.toString();
	}

}
