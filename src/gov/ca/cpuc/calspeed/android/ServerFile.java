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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class ServerFile {
	public String filename;

	public ArrayList<CharSequence> entry_names = new ArrayList<CharSequence>();
	public ArrayList<CharSequence> entry_values = new ArrayList<CharSequence>();
	private Context context;

	public ServerFile(Context context, String filename) {
		this.filename = filename;
		this.context = context;
	}

	public ServerFile(Context context, String filename,
			CharSequence[] entry_names, CharSequence[] entry_values) {
		this.filename = filename;
		this.context = context;
		SetupEntryArrays(entry_names, entry_values);
	}

	private void SetupEntryArrays(CharSequence[] entry_names,
			CharSequence[] entry_values) {
		for (int i = 0; i < entry_names.length; i++) {
			this.entry_names.add(entry_names[i]);
			this.entry_values.add(entry_values[i]);
		}
	}

	public Boolean ReadServerFile() {
		Boolean status = false;

		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(
					context.openFileInput("servers.txt")));
			String line;

			while ((line = input.readLine()) != null) {
				String tmp[] = line.split(":");
				if (tmp.length == 3) {
					entry_names.add(tmp[0]);
					entry_values.add(tmp[1] + ":" + tmp[2]);
				} else {
					return status;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
					status = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return (status);
	}

	public Boolean SaveServerFile() {
		Boolean status = false;
		String serverString;
		FileOutputStream fos;

		serverString = getServerString(entry_names, entry_values);

		try {
			fos = context.openFileOutput("servers.txt", Context.MODE_PRIVATE);
			fos.write(serverString.getBytes());
			fos.close();

			return (status);
		} catch (Exception e) {
			Log.v("debug", "Unable to write servers.txt file.\n");
			return (status);
		}

	}
	public void addEntry(String entry,String entryValue){
		entry_names.add(entry);
		entry_values.add(entryValue);
		SaveServerFile();
	}

	private String getServerString(ArrayList<CharSequence> entry_names,
			ArrayList<CharSequence> entry_values) {
		String name, addressPort;
		String stringInfo = "";

		for (int i = 0; i < entry_names.size(); i++) {
			name = (String) entry_names.get(i);
			addressPort = (String) entry_values.get(i);
			stringInfo += name + ":" + addressPort + "\n";
		}
		return stringInfo;
	}

	public boolean SDCardIsWritable() {
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageWriteable = false;
		}
		return mExternalStorageWriteable;
	}
}
