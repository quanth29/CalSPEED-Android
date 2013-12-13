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

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.FileInputStream;


public class TextFileIO {
	private Context context;
	private String filename;
	private FileOutputStream fileOutput;
	private FileInputStream fileInput;
	private StringBuffer fileContents;
	
	public TextFileIO(Context context,String filename){
		this.context = context;
		this.filename = filename;
		this.fileOutput = null;
		this.fileInput= null;
		this.fileContents = null;
		this.fileContents = new StringBuffer("");	
	}
	
	public String GetStringFileContents(){
		String contents = null;
		contents = fileContents.toString();
		return(contents);
	}

	public Boolean WriteToTextFile(String text){
		Boolean status = false;
		try{		
			fileOutput = context.openFileOutput(filename,Context.MODE_APPEND + Context.MODE_PRIVATE);
			fileOutput.write(text.getBytes());
			status = true;
		}catch (Exception e){
			if (Constants.DEBUG){
				Log.e("debug","unable to write to history file\n");
			}
		}
		
		try{
			fileOutput.close();
		}catch(Exception e){			
		}
		return(status);		
	}
	
	public Boolean ReadTextFile(){
		Boolean status = false;
		try{
			fileInput = context.openFileInput(filename);
			fileContents = new StringBuffer("");

			byte[] buffer = new byte[1024];

			while (fileInput.read(buffer) != -1) {
			    fileContents.append(new String(buffer));
			}
			status = true;
		}
		catch(Exception e){
			if (Constants.DEBUG){
				Log.e("debug","unable to read to history file\n");
			}
		}
		
		try{
			fileInput.close();
		}catch(Exception e){
		}
		return(status);
	}

}
