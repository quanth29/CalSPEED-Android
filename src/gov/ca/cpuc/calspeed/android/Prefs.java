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

import gov.ca.cpuc.calspeed.android.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public class Prefs extends PreferenceActivity {


	private static final String OPT_GPSOVERRIDE = "GPSoverride";
	private static final boolean OPT_GPSOVERRIDE_DEF = true;
	
	public static final String OPT_LOCATION_ID = "location_id";
	public static final String OPT_LOCATION_ID_DEF = "1";
	
	CharSequence[] entry_names;
	CharSequence[] entry_values;
	Context mycontext;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.settings);
      //get the specified preferences using the key declared in preferences.xml
      
      try{
      mycontext = createPackageContext("gov.ca.cpuc.calspeed.android", 0);
      }catch (Exception e){
    	  Log.i("debug","Error getting context in Preference activity onCreate.\n");
    	  return;
      }
      
   } 
   public static boolean getGPSoverride(Context context){
	   return PreferenceManager.getDefaultSharedPreferences(context)
	   	.getBoolean(OPT_GPSOVERRIDE, OPT_GPSOVERRIDE_DEF);
   }
   public static void resetGPSoverride(Context context){
	   SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
	   prefEditor.putBoolean(OPT_GPSOVERRIDE, OPT_GPSOVERRIDE_DEF);
	   prefEditor.commit();
   } 

}

