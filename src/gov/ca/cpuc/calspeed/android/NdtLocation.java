// Original work: Copyright 2009 Google Inc. All Rights Reserved.
//
// Modified work: The original source code comes from the NDT Android app
//                that is available from http://code.google.com/p/ndt/.
//                No modification by the CalSPEED Android app by California 
//                State University Monterey Bay (CSUMB) in this file.
//

package gov.ca.cpuc.calspeed.android;

import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import gov.ca.cpuc.calspeed.android.Calspeed;
import gov.ca.cpuc.calspeed.android.UiServices;

import java.util.Iterator;


/**
 * Handle the location related functions and listeners.
 */
public class NdtLocation implements LocationListener {
  /**
   * Location variable, publicly accessible to provide access to geographic data.
   */
  public Location location;
  public LocationManager locationManager;
  private Criteria criteria;
  public String bestProvider;
  public Boolean gpsEnabled;
  private Calspeed context;
  private AndroidUiServices uiServices;


  /**
   * Passes context to this class to initialize members.
   * 
   * @param context context which is currently running
   */
  public NdtLocation(Calspeed context,AndroidUiServices uiServices) {
	this.context = context;
	this.uiServices = uiServices;
    locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    Iterator<String> providers = locationManager.getAllProviders().iterator();
    location = null;
    while(providers.hasNext()) {
        Log.v("debug", providers.next());
    }
    criteria = new Criteria();
    criteria.setAccuracy(Criteria.ACCURACY_FINE);
    criteria.setCostAllowed(true);
    criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
    
    bestProvider = locationManager.getBestProvider(criteria, true);
    Log.v("debug","Best provider is:"+ bestProvider);
    addGPSStatusListener();
    
  }
  public void addGPSStatusListener(){
	 if (locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER)){ 
	    locationManager.addGpsStatusListener(onGpsStatusChange) ;
	    gpsEnabled = true;
	 }
	 else{
	    gpsEnabled = false;
	 }
  }
  public void removeGPSStatusListener(){
	  if (locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER)){
		  locationManager.removeGpsStatusListener(onGpsStatusChange);
		  gpsEnabled = false;
	  }
  }
  private final GpsStatus.Listener onGpsStatusChange=new GpsStatus.Listener()
  {
          public void onGpsStatusChanged(int event)
          {
                  switch( event )
                  {
                          case GpsStatus.GPS_EVENT_STARTED:
                                  // Started...
                        	  	startListen();
                        	  	context.updateLatitudeLongitude();
                        	  	if (Constants.DEBUG)
                        	  		Log.v("debug","GPS starting...\n");
                                  break ;
                          case GpsStatus.GPS_EVENT_FIRST_FIX:
                                  // First Fix...
                        	  if (Constants.DEBUG)
                        		  Log.v("debug","GPS first fix \n");
                        	  context.updateLatitudeLongitude();
                                  break ;
                          case GpsStatus.GPS_EVENT_STOPPED:
                                  // Stopped...
                        	  stopListen();
                        	  location = null;
                        	  context.updateLatitudeLongitude();
                        	  if (Constants.DEBUG)
                        		  Log.v("debug","GPS stopped.\n");
                                  break ;
                          case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                              GpsStatus xGpsStatus = locationManager.getGpsStatus(null) ;
                              Iterable<GpsSatellite> iSatellites = xGpsStatus.getSatellites() ;
                              Iterator<GpsSatellite> it = iSatellites.iterator() ;
                              while ( it.hasNext() )
                              {
                                      GpsSatellite oSat = (GpsSatellite) it.next() ;
                                      if (Constants.DEBUG)
                                    	  Log.v("debug","LocationActivity - onGpsStatusChange: Satellites: " +
                      oSat.getSnr() ) ;
                              }
                              break ; 
                  }
          }
  } ;

  @Override
  public void onLocationChanged(Location location) {
	  float distance = 0;
	  if (this.location != null){
	    	distance = location.distanceTo(this.location); 		    	
	  }
	  String locInfo = String.format("Current location = (%f,%f)\n",
	  	location.getLatitude(),location.getLongitude());
	  this.location = location;
	  context.updateLatitudeLongitude();
	  context.updateLatLongDisplay();
	  locInfo += String.format("\n Distance from last = %f meters \n",distance);
	  if (Constants.DEBUG)
		  Log.v("debug",locInfo);
	  

  }
 @Override
  public void onProviderDisabled(String provider) {
    stopListen();
    location = null;
    context.updateLatitudeLongitude();
  }

 @Override
  public void onProviderEnabled(String provider) {
    startListen();
    context.updateLatitudeLongitude();
  }
 @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
	 switch (status) {
		case LocationProvider.OUT_OF_SERVICE:
			if (Constants.DEBUG)
				Log.v("debug", "Status Changed: Out of Service");
			stopListen();
			location = null;
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			if (Constants.DEBUG)
				Log.v("debug", "Status Changed: Temporarily Unavailable");
			stopListen();
			location = null;
			break;
		case LocationProvider.AVAILABLE:
			if (Constants.DEBUG)
				Log.v("debug", "Status Changed: Available");
			startListen();
			break;
		}
  }

  /**
   * Stops requesting the location update.
   */
  public void stopListen() {

    locationManager.removeUpdates(this);
  }

  /**
   * Begins to request the location update.
   */
  public void startListen() {
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
        this);
  }
}
