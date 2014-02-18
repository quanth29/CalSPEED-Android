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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.widget.ScrollView;
import android.widget.TextView;


import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends SherlockFragmentActivity {

	Fragment calspeedFragment;  
	Fragment viewerFragment; 
	Fragment displayHistoryFragment;

	ActionBar.Tab calspeedTab;
	ActionBar.Tab viewerTab;
	ActionBar.Tab displayHistoryTab;
	
	ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tabs);
		
		actionbar = getSupportActionBar();
		
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		calspeedTab = actionbar.newTab().setText("Speed Test");
		displayHistoryTab = actionbar.newTab().setText("Results");
		viewerTab = actionbar.newTab().setText("Map View");
		
		calspeedFragment = new CalspeedFragment();				
		displayHistoryFragment = new DisplayHistory();
		viewerFragment = new ViewerFragment();
						
		calspeedTab.setTabListener(new MainTabsListener(calspeedFragment));
		displayHistoryTab.setTabListener(new MainTabsListener(displayHistoryFragment));
		viewerTab.setTabListener(new MainTabsListener(viewerFragment));

		actionbar.addTab(calspeedTab, 0, true);
		actionbar.addTab(displayHistoryTab, 1, false);
		
		int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
	    if (checkGooglePlayServices == ConnectionResult.SUCCESS) {
	    	actionbar.addTab(viewerTab, 2, false);
	    }
		
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater(); 
		inflater.inflate(R.menu.main_activity, menu);
			
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menuitem_about:

			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.dismiss();
			           }
			       });
			
			AlertDialog dialog = builder.create();
			if(actionbar.getSelectedTab()==viewerTab){
				dialog.setTitle("About Map View (v1.2.1)");
				TextView viewerHelp = new TextView(this);
				viewerHelp.setText(Html.fromHtml(this.getString(R.string.about_text_viewer)));
				viewerHelp.setMovementMethod(LinkMovementMethod.getInstance());
				viewerHelp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				viewerHelp.setPadding(10,10,10,10);
				ScrollView scroller = new ScrollView(this);
				scroller.addView(viewerHelp);
				dialog.setView(scroller);
			}
			else{
				// Setting Dialog Title
				dialog.setTitle("About CalSPEED (v1.2.1)");
				// Setting Dialog Message
				TextView viewerHelp = new TextView(this);
				viewerHelp.setText(Html.fromHtml(this.getString(R.string.about_text)));
				viewerHelp.setMovementMethod(LinkMovementMethod.getInstance());
				viewerHelp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				viewerHelp.setPadding(10,10,10,10);
				ScrollView scroller = new ScrollView(this);
				scroller.addView(viewerHelp);
				dialog.setView(scroller);
			}
			// Show Alert Message
			dialog.show();
			return true;
		default:

		}
		
		return false;
	}
	public Location getLocationFromCalspeedFragment(){
		Location newLocation = null;
		int locationTries = 0;
		while(newLocation == null && locationTries < 10){
			newLocation = ((CalspeedFragment) calspeedFragment).getLocation();
			locationTries++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		return newLocation;
	}
	
	public void disableTabsWhileTesting(){
		actionbar.removeTab(displayHistoryTab);
		int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
	    if (checkGooglePlayServices == ConnectionResult.SUCCESS) {
	    	actionbar.removeTab(viewerTab);
	    }
	}
	
	public void enableTabsAfterTesting(){
		actionbar.addTab(displayHistoryTab, 1, false);
		int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
	    if (checkGooglePlayServices == ConnectionResult.SUCCESS) {
	    	actionbar.addTab(viewerTab, 2, false);
	    }
	}
	
	@Override
	public void onBackPressed() {
	    moveTaskToBack(true);
	}
	
	
	class MainTabsListener implements ActionBar.TabListener {
		public Fragment fragment;

		public MainTabsListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.tabs, fragment);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(fragment);
		}
	}
}

