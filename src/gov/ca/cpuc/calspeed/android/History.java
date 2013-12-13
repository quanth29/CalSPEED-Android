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

import java.text.SimpleDateFormat;
import java.util.Date;

public class History {
	private Integer ID;
	private String date;
	private String uploadAverage;
	private String downloadAverage;	
	private String latencyAverage;
	private String jitterAverage;
	private String networkType;
	private String connectionType;
	private String latitude;
	private String longitude;
	
	public History(){
	}
	public History(Integer id,String date, String uploadAverage, String downloadAverage, String latencyAverage, String jitterAverage,
				String networkType, String connectionType, String latitude, String longitude){
		this.ID = id;
		this.date = date;
		this.uploadAverage = uploadAverage;
		this.downloadAverage = downloadAverage;
		this.latencyAverage = latencyAverage;
		this.jitterAverage = jitterAverage;
		this.networkType = networkType;
		this.connectionType = connectionType;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public History(String date, String uploadAverage, String downloadAverage, String latencyAverage, String jitterAverage,
			String networkType,String connectionType, String latitude, String longitude){
		
		this.date = date;
		this.uploadAverage = uploadAverage;
		this.downloadAverage = downloadAverage;
		this.latencyAverage = latencyAverage;
		this.jitterAverage = jitterAverage;
		this.networkType = networkType;
		this.connectionType = connectionType;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Integer getID(){
		return(this.ID);
	}
	public String getStringID(){
		String stringID = this.ID.toString();
		return(stringID);
	}
	public void setID(Integer ID){
		this.ID = ID;
	}
	public String getDate(){
		return(this.date);
	}
	public String getFormattedDate(String separator){
		String newFormattedDate="Error";
		try{
		Date newDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.date);
		SimpleDateFormat datePart = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat timePart = new SimpleDateFormat("hh:mm a");
	    newFormattedDate = datePart.format(newDate);
	    newFormattedDate += separator + timePart.format(newDate);
		}catch (Exception e){
			
		}
		return (newFormattedDate);
	}
	public String getMonthDayYear(){
		String newFormattedDate="Error";
		try{
		Date newDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.date);
		SimpleDateFormat datePart = new SimpleDateFormat("MM/dd/yyyy");
	    newFormattedDate = datePart.format(newDate);
		}catch (Exception e){
			
		}
		return (newFormattedDate);
	}
	public String getTime(){
		String newFormattedTime="Error";
		try{
		Date newDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.date);
		SimpleDateFormat timePart = new SimpleDateFormat("hh:mm a");
	    newFormattedTime = timePart.format(newDate);
		}catch (Exception e){
			
		}
		return (newFormattedTime);
	}
	public void setDate(String date){
		this.date  = date;
	}
	public String getUploadAverage(){
		return(this.uploadAverage);
	}
	public void setUploadAverage(String uploadAverage){
		this.uploadAverage = uploadAverage;
	}
	public String getDownloadAverage(){
		return(this.downloadAverage);
	}
	public void setDownloadAverage(String downloadAverage){
		this.downloadAverage = downloadAverage;
	}
	public String getLatencyAverage(){
		return(this.latencyAverage);
	}
	public void setLatencyAverage(String latencyAverage){
		this.latencyAverage = latencyAverage;
	}
	public String getJitterAverage(){
		return(this.jitterAverage);
	}
	public void setJitterAverage(String jitterAverage){
		this.jitterAverage = jitterAverage;
	}
	public String getNetworkType(){
		return(this.networkType);
	}
	public void setNetworkType(String networkType){
		this.networkType = networkType;
	}
	
	public String getConnectionType(){
		return(this.connectionType);
	}
	public void setConnectionType(String connectionType){
		this.connectionType = connectionType;
	}

	public String getLatitude(){
		return(this.latitude);
	}
	public void setLatitude(String latitude){
		this.latitude = latitude;
	}
	public String getLongitude(){
		return(this.longitude);
	}
	public void setLongitude(String longitude){
		this.longitude = longitude;
	}
}
