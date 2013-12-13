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

import gov.ca.cpuc.calspeed.android.AddressCandidates.Candidates;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONObject;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView.OnEditorActionListener;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewerFragment extends SherlockFragment implements
		OnMapLongClickListener, OnInfoWindowClickListener {

	private LatLng currentLocation;
	private LatLng target;
	private Location location;
	private AddressCandidates addrCandidate;
	private double longitude;
	private double latitude;
	private GoogleMap mMap;
	private String address;
	private String request;
	private String URLXY;
	private Marker marker;
	private Intent intent;
	private EditText search;
	private static View myview;

	@Override
	public void onStart() {
		super.onStart();

		FragmentManager myFragmentManager = getActivity()
				.getSupportFragmentManager();
		SupportMapFragment mMapFragment = (SupportMapFragment) myFragmentManager
				.findFragmentById(R.id.map);
		//this will be null without Google Play Store
		mMap = mMapFragment.getMap();

		intent = new Intent(getActivity(), DisplayInfo.class);
		
		// To include an option menu such as "search".
		setHasOptionsMenu(true);

		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			
		location = ((MainActivity) getActivity()).getLocationFromCalspeedFragment();

		// If the app doesn't know the location, it displays the center of
		// California.
		if (location == null) {
			mMap.setMyLocationEnabled(false);
			longitude = -119.838867;
			latitude = 36.742993;
			currentLocation = new LatLng(latitude, longitude);
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,
					6));
		} else {
			mMap.setMyLocationEnabled(true);
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			currentLocation = new LatLng(latitude, longitude);
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,
					14));
			
		}

		//Instructions for Map View
		SharedPreferences mapViewIntro = getActivity().getSharedPreferences("MapViewData", Context.MODE_PRIVATE);

		if (!(mapViewIntro.getBoolean("mapViewPreference", false))) {
			

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(
					"\t\t\tWelcome to Map View\n\n1. Press and hold a location on the map or type an address at the search bar.\n\n2. Tap the address of selected location.\n\n3. View upstream and downstream speeds of carriers.")
					.setCancelable(false)
					.setPositiveButton("Okay",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {

									dialog.cancel();
								}
							});
			builder.setNegativeButton("Don't show again",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							SharedPreferences mapViewIntro = getActivity().getSharedPreferences("MapViewData",
									Constants.MODE_PRIVATE);
							SharedPreferences.Editor mapViewIntroEditor = mapViewIntro.edit();
							mapViewIntroEditor.putBoolean("mapViewPreference", true);
							mapViewIntroEditor.commit();

							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
		
		// A custom actionbar with an address search function.
		ActionBar actionBar = ((SherlockFragmentActivity) getActivity())
				.getSupportActionBar();
		actionBar.setCustomView(R.layout.actionbar_for_viewer);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);

		search = (EditText) actionBar.getCustomView().findViewById(
				R.id.mainfield);

		search.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				if (marker != null) {
					mMap.clear();
				}
				address = search.getText().toString();

				// Hide the virtual keyboard
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(search.getWindowToken(), 0);

				// No address is entered by a user. Nothing should happen.
				if (address.equals("")) {
					return false;
				}

				try {
					request = URLEncoder.encode(address, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

				URLXY = "http://tasks.arcgisonline.com/ArcGIS/rest/services/Locators/TA_Address_NA_10/"
						+ "GeocodeServer/findAddressCandidates?Address=&City=&State=&Zip=&Zip4=&Country=&"
						+ "SingleLine="
						+ request
						+ "&outFields=*&outSR=102113&f=pjson";

				try {
					addrCandidate = new GetAddressCandidates(getActivity())
							.execute(URLXY).get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}

				List<Candidates> candidates = addrCandidate.candidates;

				try {
					drawSearchResult(candidates);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				return true;
			}
		});


		mMap.setOnMapLongClickListener(this);
		mMap.setOnInfoWindowClickListener(this);

	}
    	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (myview != null) {
			ViewGroup parent = (ViewGroup) myview.getParent();
			if (parent != null)
				parent.removeView(myview);
		}
		try {
			myview = inflater.inflate(R.layout.viewer_fragment, container,
					false);
		} catch (InflateException e) {
			/* map is already there, just return myview as it is */
		}
		return myview;
	}

	public void drawSearchResult(final List<Candidates> list)
			throws InterruptedException {
		ArrayList<String> SpinnerArray = new ArrayList<String>();
		final ArrayList<Integer> builderIndex = new ArrayList<Integer>();

		int i, j = 0;

		for (i = 0; i < list.size(); i++) {

			// If "State" is not "CA", do not add it as a candidate.
			if ((list.get(i).attributes.State == null)
					|| (!list.get(i).attributes.State.equals("CA"))) {
				continue;
			}
			// If "Loc_name" has "US_RoofTop", we found the address
			else if (list.get(i).attributes.Loc_name.equals("US_RoofTop")) {
				target = new LatLng(list.get(i).attributes.Y,
						list.get(i).attributes.X);

				// Put the selected address on the center of the map.
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 12));

				marker = mMap.addMarker(new MarkerOptions().position(target));
				address = list.get(i).address;
				marker.setSnippet(address);
				marker.setTitle("Selected Location:");
				marker.showInfoWindow();

				return;
			} else {
				SpinnerArray.add(list.get(i).address);
				builderIndex.add(j, i);
				j++;
			}

		}

		// No candidate for the address
		if (j == 0) {

			// For debugging
			Log.w("Viewer", "MainActivity - drawSearchResult()");

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder.setTitle("No Results Found");
			alertDialogBuilder
					.setMessage("Your search did not match any locations.")
					.setCancelable(false)
					.setNegativeButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();

			return;
		}

		// If there's only one candidate, we should display it directly.
		if (j == 1) {

			int k = builderIndex.get(0);

			target = new LatLng(list.get(k).attributes.Y,
					list.get(k).attributes.X);

			// Put the selected address on the center of the map.
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 12));

			marker = mMap.addMarker(new MarkerOptions().position(target));
			address = list.get(k).address;
			marker.setSnippet(address);
			marker.setTitle("Selected Location:");
			marker.showInfoWindow();

			return;
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, SpinnerArray);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Choose Address");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {

				int k = builderIndex.get(item); // Get the index of "list" for
												// the item selected by a user.
												// Note that some items are not
												// displayed on the "builder"
												// dialog.
												// For example, just an item has
												// non"CA" state.

				target = new LatLng(list.get(k).attributes.Y,
						list.get(k).attributes.X);

				// Put the selected address on the center of the map.
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 12));

				marker = mMap.addMarker(new MarkerOptions().position(target));
				address = list.get(k).address;
				marker.setSnippet(address);
				marker.setTitle("Selected Location:");
				marker.showInfoWindow();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();

		return;
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		
		
		mMap.clear();
		marker = null;
		//check to make sure location is not null
		if(!(location==null)){
			
			//check to see what zoom level is, then set proximity to blue dot - JC
			int currentZoom = (int)mMap.getCameraPosition().zoom;
			
			double distanceInMeters = 0;
			
			switch(currentZoom){
				case 14: distanceInMeters = 200.0;
					break;
				case 15: distanceInMeters = 150.0;
					break;
				case 16: distanceInMeters = 100.0;
					break;
				case 17: distanceInMeters = 50.0;
					break;
				case 18: distanceInMeters = 25.0;
					break;
				case 19: distanceInMeters = 10.0;
					break;
				case 20: distanceInMeters = 10.0;
					break;
				case 21: distanceInMeters = 10.0;
					break;
				default: distanceInMeters = 10.0;
					break;
			}
			
			// Checks to see if user selected a location less than distanceInMeters from current test location. 
			float [] dist = new float[1];
			Location.distanceBetween(arg0.latitude, arg0.longitude, location.getLatitude(),location.getLongitude(), dist);
			if(dist[0] < distanceInMeters){
				target = new LatLng(location.getLatitude(), location.getLongitude());
			}
			else{
				target = new LatLng(arg0.latitude, arg0.longitude);
			}
		}
		else{
			target = new LatLng(arg0.latitude, arg0.longitude);
		}
		// Drop a marker from top to the location.
		// Code from
		// http://stackoverflow.com/questions/16604206/drop-marker-slowly-from-top-of-screen-to-location-on-android-map-v2

		final long duration = 400;
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = mMap.getProjection();

		Point startPoint = proj.toScreenLocation(target);
		startPoint.y = 0;
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final Interpolator interpolator = new LinearInterpolator();

		marker = mMap.addMarker(new MarkerOptions().position(new LatLng(
				startLatLng.latitude, startLatLng.longitude)));

		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t * target.longitude + (1 - t)
						* startLatLng.longitude;
				double lat = t * target.latitude + (1 - t)
						* startLatLng.latitude;

				if (t < 1.0) {
					marker.setPosition(new LatLng(lat, lng));

					// Post again 40ms later.
					handler.postDelayed(this, 40);

				} else { // End of animation.

					marker.setPosition(new LatLng(target.latitude,
							target.longitude));
					marker.setTitle("Selected Location:");
					marker.showInfoWindow();

					// Google API
					String reverseGeo = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
							+ target.latitude
							+ ","
							+ target.longitude
							+ "&sensor=true";

					/*************************************************************************************************
					 * String reverseGeo =
					 * "http://tasks.arcgisonline.com/ArcGIS/rest/services/Locators/TA_Address_NA_10/GeocodeServer/reverseGeocode?location="
					 * + target.longitude + "%2C+" + target.latitude +
					 * "&distance=0&outSR=102113&f=pjson";
					 *************************************************************************************************/

					String geoCodingResults = "";

					try {
						final ReverseGeo geoCoder = new ReverseGeo();
						geoCoder.execute(reverseGeo);
						geoCodingResults = geoCoder.get();
						Handler handler = new Handler();
						handler.postDelayed(new Runnable(){
							@Override
							public void run() {
								if (geoCoder.getStatus() == AsyncTask.Status.RUNNING) {
									geoCoder.cancel(true);

									AlertDialog.Builder builder = new AlertDialog.Builder(
											getActivity());
									builder.setTitle("Timeout")
											.setMessage(
													"A network timeout error occurred. Please try again later.")
											.setCancelable(false)
											.setNegativeButton("Dismiss",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog, int id) {
															dialog.dismiss();
														}
													});
									AlertDialog alert = builder.create();
									alert.show();
									
									mMap.clear();
									marker = null;
								}
							}
							
						}, 20000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
					

					try {
						JSONObject jsonObject = new JSONObject(geoCodingResults);
						
						String statusGeo = jsonObject.getString("status");
						//If Google Reverse Geocode doesn't find it, try MapQuest
						if(!statusGeo.equals("OK")){
							String latVal = String.valueOf(target.latitude);
							String lngVal = String.valueOf(target.longitude);
							reverseGeo = "";
							try {
								reverseGeo = "Place mapquest API key here"
								   + URLEncoder.encode("{location:{latLng:{lat:", "UTF-8") 
								   + URLEncoder.encode(latVal, "UTF-8")
								   + ",lng:"
								   + URLEncoder.encode(lngVal, "UTF-8")
								   + URLEncoder.encode("}}}","UTF-8");
								final ReverseGeo geoCoder = new ReverseGeo();
								geoCoder.execute(reverseGeo);
								geoCodingResults = geoCoder.get();
								Handler handler = new Handler();
								handler.postDelayed(new Runnable(){
									@Override
									public void run() {
										if (geoCoder.getStatus() == AsyncTask.Status.RUNNING) {
											geoCoder.cancel(true);

											AlertDialog.Builder builder = new AlertDialog.Builder(
													getActivity());
											builder.setTitle("Timeout")
													.setMessage(
															"A network timeout error occurred. Please try again later.")
													.setCancelable(false)
													.setNegativeButton("Dismiss",
															new DialogInterface.OnClickListener() {
																public void onClick(
																		DialogInterface dialog, int id) {
																	dialog.dismiss();
																}
															});
											AlertDialog alert = builder.create();
											alert.show();
											
											mMap.clear();
											marker = null;
										}
									}
									
								}, 20000);
								jsonObject = new JSONObject(geoCodingResults);
								
							} catch (UnsupportedEncodingException e1) {
								e1.printStackTrace();
							}	
							
							JSONArray resultsItems = new JSONArray(jsonObject.getString("results"));
							
							// get the first formatted_address value
							if (resultsItems.length() == 0) {
								address = "UNKNOWN ADDRESS. TRY AGAIN!!!";

							} else {
								JSONObject resultItem = resultsItems.getJSONObject(0);
															
								//For Mapquest, replace address with Full Address
								JSONArray newArray = new JSONArray(resultItem.getString("locations"));
								JSONObject resultItem2 = newArray.getJSONObject(0);
								String addStreet = resultItem2.getString("street");
								String addCity = resultItem2.getString("adminArea5");
								String addState = resultItem2.getString("adminArea3");
								String addZip = resultItem2.getString("postalCode");
								address = addStreet + ", " + addCity + ", " + addState + ", " + addZip;
							}
						}
						//Otherwise, just use the Google Reverse Geocode
						else{
							
							JSONArray resultsItems = new JSONArray(jsonObject.getString("results"));
							// get the first formatted_address value
							if (resultsItems.length() == 0) {
								address = "UNKNOWN ADDRESS. TRY AGAIN!!!";

							} else {
								JSONObject resultItem = resultsItems
										.getJSONObject(0);
								address = resultItem.getString("formatted_address");

								// If the address has ", USA" at the end, remove it.
								address = address.replace(", USA", "");
							}
							
						}
					} catch (Exception e) {
						Log.d("Viewer", e.getLocalizedMessage());
					}

					marker.setSnippet(address);
					marker.showInfoWindow();
				}
			}
		});

		return;

	}

	@Override
	public void onInfoWindowClick(Marker marker) {

		if (address.equalsIgnoreCase("UNKNOWN ADDRESS. TRY AGAIN!!!")) {
			return; // Nothing to do if we don't know the address.
		}
		//if address is not in California, do nothing
		if (!address.contains(", CA")) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder.setTitle("No Results");
			alertDialogBuilder
					.setMessage("We are sorry. Map View is available only for California regions.")
					.setCancelable(false)
					.setNegativeButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			return;
		}

		// Get x and y coordinates from the target latitude and longitude
		URLXY = "http://tasks.arcgisonline.com/ArcGIS/rest/services/Geometry/GeometryServer/"
				+ "project?inSR=4326&outSR=102113&"
				+ "geometries="
				+ target.longitude + "," + target.latitude + "&f=pjson";

		// GetXYCoordinates(MainActivity.this).execute(URLXY).get();
		final ProgressDialog dialog = new ProgressDialog(getActivity());
		final GetXYCoordinates xyDataHandler = new GetXYCoordinates(
				getActivity(), dialog);
		xyDataHandler.execute(URLXY);

		// 20 seconds timeout to get the X/Y coordinates
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (xyDataHandler.getStatus() == AsyncTask.Status.RUNNING) {
					xyDataHandler.cancel(true);
					dialog.dismiss();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Timeout")
							.setMessage(
									"A network timeout error occurred. Please try again later.")
							.setCancelable(false)
							.setNegativeButton("Dismiss",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.dismiss();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				}
			}
		}, 20000);

		xyDataHandler.addObserver(new GetXYCoordinates.Callback() {
			@Override
			public void onFailure() {

			}

			@Override
			public void onComplete(XYCoordinates datas) {
				intent.putExtra("address", address);
				intent.putExtra("x", datas.geometries.get(0).x);
				intent.putExtra("y", datas.geometries.get(0).y);
				startActivity(intent);

			}
		});

	}

	public static void showOkDialogWithText(Context context, String messageText) {
		android.app.AlertDialog.Builder builder = new AlertDialog.Builder(
				context);
		builder.setMessage(messageText);
		builder.setCancelable(true);
		builder.setPositiveButton("OK", null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

}
