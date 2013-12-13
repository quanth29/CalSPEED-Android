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

import gov.ca.cpuc.calspeed.android.AndroidUiServices;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class ProcessPing {

    public String average;
    public String minimum;
    public String maximum;
    public String loss;
    private Integer state;
    public String message;
    public Boolean success;
    public Float rollingSum;
    public Integer rollingAverageCount;
    private AndroidUiServices uiServices;
    public Float phase1Average;
    public Boolean isPhase2;
   

    public ProcessPing(String message, AndroidUiServices uiServices) {
        average = "NA";
        minimum = "NA";
        maximum = "NA";
        loss = "NA";
        state = 0;
        this.message = message;
        success = false;
        rollingSum = 0.0f;
        rollingAverageCount = 0;
        this.uiServices = uiServices;
        this.phase1Average=0.0f;
        this.isPhase2=false;
    }
    public void setPhase2(String firstPingAverage){
    	if (firstPingAverage.contains("NA")){
    		this.phase1Average = 0.0f;
    	}else{
    		NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
    		try {
    			firstPingAverage = firstPingAverage.replaceAll("\\s+","");
    			if(firstPingAverage.indexOf('.')==-1){
    				firstPingAverage = firstPingAverage.replace(",", "." );
    			}
    			
				Number toParse = nf.parse(firstPingAverage);
				this.phase1Average = toParse.floatValue();
			} catch (ParseException e) {
				e.printStackTrace();
				this.phase1Average = Float.valueOf(firstPingAverage.replace(",", "." ));
			}	
    		
    	}
    	this.isPhase2 = true;
    }
    public void displayInitialMessage(){
    	uiServices.setResults(Constants.THREAD_WRITE_LATENCY_DATA, message, "0", false, false);
    }
    public void SetPingSuccess(String message) {
        this.message = message;
        success = true;
    }

    public void SetPingFail(String message) {
        this.message = message;
        success = false;
    }
 

    public void SetPingFinalStatus(String name) {
        if (average.contains("NA") || loss.contains("100%")) {
            SetPingFail("Delay Incomplete");
        } else {
            SetPingSuccess("Delay");
        }
        if (!isPhase2){
        	uiServices.setResults(Constants.THREAD_WRITE_LATENCY_DATA, message,average,!success,!success);
        }
    }
    private static String formatFloatString(String value){
      	 Float flAverage = Float.valueOf(value);
           NumberFormat numberFormat  = new DecimalFormat("#.0");
   		return(numberFormat.format(flAverage));
      }
    public void displayRollingAverage(){
    	//phase1Average is zero if  phase 1 Ping Failed
    	Float rollingAvg;
    	if (isPhase2){
    		if (phase1Average != 0.0f){
    			rollingAvg = (phase1Average + (rollingSum/rollingAverageCount))/2;
    		}else{
    			// phase 1 Ping failed, so ignore it
    			rollingAvg = rollingSum/rollingAverageCount;
    		}
    	}else{
    		rollingAvg = rollingSum/rollingAverageCount;
    	}
    		String rollingString = formatFloatString(rollingAvg.toString());
    		uiServices.setResults(Constants.THREAD_WRITE_LATENCY_DATA, message,rollingString , false, false);
    }
    private void ParseLine(String line, String clientType) {
        int indexStart = -1;
        int indexEnd = -1;

        if (clientType.contains("Phone")) {
            String[] delimited;
            switch (state) {
                case 0:
                    indexStart = line.indexOf("received,");
                    if (indexStart != -1) {
                        indexEnd = line.indexOf("%", indexStart + 10);
                        loss = line.substring(indexStart + 10, indexEnd);
                        state = 1;
                    }else{
                    	indexStart = line.indexOf("time=");
                    	if (indexStart != -1){
                    		indexEnd = line.indexOf(" ms");
                    		if (indexEnd != -1){
                    			String time = line.substring(indexStart+5,indexEnd);
                    			try {
                    				float currentSpeed = Float.parseFloat(time);
                    				rollingSum += currentSpeed;
                    				rollingAverageCount++;
                    				displayRollingAverage();
                    			}catch (Exception e){
                    					// ignore if not a number
                    			}
                    		}
                    	}
                    }
                    break;
                case 1:
                    indexStart = line.indexOf("/mdev = ");
                    if (indexStart != -1) {
                        String statsString = line.substring(indexStart + 8, line.length());
                        delimited = statsString.split("/");
                        minimum = delimited[0];
                        average = delimited[1];
                        average = average.replace(",", ".");
                        Float flAverage = Float.valueOf(average);
                        NumberFormat numberFormat  = new DecimalFormat("#.0");
                		average = numberFormat.format(flAverage);    
                        maximum = delimited[2];
                        state = 2;
                    }
                    break;
                default:
                    break;
            }
        } else {
            switch (state) {
                case 0:
                    indexEnd = line.indexOf("% loss)");
                    if (indexEnd != -1) {
                        indexStart = line.indexOf("(", indexEnd - 6);
                        loss = line.substring(indexStart + 1, indexEnd);
                        state = 1;
                    }else{
                    	indexStart = line.indexOf("time=");
                    	if (indexStart != -1){
                    		indexEnd = line.indexOf("ms");
                    		if (indexEnd != -1){
                    			String time = line.substring(indexStart+5,indexEnd);
                    			try {
                    				float currentSpeed = Float.parseFloat(time);
                    				rollingSum += currentSpeed;
                               		rollingAverageCount++;
                    			}catch (Exception e){
                    					// ignore if not a number
                    			}
                    		}
                    	}
                    }
                    break;
                case 1:
                    indexStart = line.indexOf("Minimum = ");
                    if (indexStart != -1) {
                        indexEnd = line.indexOf("ms", indexStart + 10);
                        if (indexEnd != -1) {
                            minimum = line.substring(indexStart + 10, indexEnd);
                        }
                        indexStart = line.indexOf("Maximum = ", indexEnd);
                        if (indexStart != -1) {
                            indexEnd = line.indexOf("ms", indexStart + 10);
                            if (indexEnd != -1) {
                                maximum = line.substring(indexStart + 10, indexEnd);
                            }
                            indexStart = line.indexOf("Average = ", indexEnd);
                            if (indexStart != -1) {
                                indexEnd = line.indexOf("ms", indexStart + 10);
                                if (indexEnd != -1) {
                                    average = line.substring(indexStart + 10, indexEnd);
                                    average = average.replace(",", ".");
                                    state = 2;
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void ProcessOutput(String lineout, String clientType) {
        Integer startIndex = 0;
        Integer endIndex = 0;
        while (startIndex < lineout.length()) {
            endIndex = lineout.indexOf("\n", startIndex);
            if (endIndex != -1) {
                ParseLine(lineout.substring(startIndex, endIndex),clientType);
                startIndex = endIndex + 1;
            } else {
                ParseLine(lineout.substring(startIndex, lineout.length() - 1),clientType);
                startIndex = lineout.length();
            }
        }

    }
}

