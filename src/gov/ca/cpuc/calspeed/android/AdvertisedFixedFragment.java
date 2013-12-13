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

import java.util.ArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AdvertisedFixedFragment extends Fragment {

	AdvertisedData datas;
	String address;

	View view;
	ListView listView;
	TextView address_display;
	LayoutInflater inflaterT;
	ImageView legendImage;

	public AdvertisedFixedFragment(AdvertisedData data, String address) {
		this.datas = data;
		this.address = address;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		int i, j;
		String carrier;
		boolean exist;
		
		// For Debugging
		Log.w("Viewer", "********************* AdvertisedFixedFragment.java - onCreateView() invoked");
		
		view = inflater.inflate(R.layout.adver_fixed_frag, container, false);
		inflaterT = inflater;
		listView = (ListView) view.findViewById(R.id.list);
		
		address_display = (TextView)view.findViewById(R.id.address);
		address_display.setText(address);
		
		//Add legend image here
		legendImage = (ImageView)view.findViewById(R.id.legend);
		legendImage.setImageResource(R.drawable.legend);
		
		ArrayList<DisplayResults> data_list = new ArrayList<DisplayResults>();

		DisplayResults temp = new DisplayResults();

		for (i = 0; i < datas.features.size(); i++) {
			if (datas.features.get(i).attributes.ServiceTyp.equalsIgnoreCase("Fixed")) {
				
				// For Debugging
				Log.w("Viewer", datas.features.get(i).attributes.dbANAME 
				         + "/(UP)" + datas.features.get(i).attributes.maxADUP 
				         + "/(DOWN)" + datas.features.get(i).attributes.maxADDOWN);

				// Check if the dbANAME already exists in the data list.
				exist = false;
				carrier = datas.features.get(i).attributes.dbANAME;

				for(j = 0; j < data_list.size(); j++)
				{
					if(carrier.equals(data_list.get(j).getName()))
					{
						exist = true;
						break;
					}
				}
				
				if(exist == false)
				{
					temp = new DisplayResults();
					temp.setName(datas.features.get(i).attributes.dbANAME);
					if (datas.features.get(i).attributes.maxADUP != null) {
						temp.setImageNumber(Key.upKey(datas.features.get(i).attributes.maxADUP));
					} 

					if (datas.features.get(i).attributes.maxADDOWN != null) {
						temp.setImageNumber2(Key.downKey(datas.features.get(i).attributes.maxADDOWN));
					} 
					
					data_list.add(temp);
				}
				else
				{
					int new_upload = Key.upKey(datas.features.get(i).attributes.maxADUP);
					int new_download = Key.downKey(datas.features.get(i).attributes.maxADDOWN);
					
					if ( ((new_upload != 10) && (data_list.get(j).getImageNumber() < new_upload)) ||
						 ((new_download != 21) && (data_list.get(j).getImageNumber2() < new_download)))
					{
						data_list.get(j).setImageNumber(new_upload);
						data_list.get(j).setImageNumber2(new_download);
					}
				}

			}
		}
				
		// If no carrier exists, display a message.
		if (data_list.size() == 0)
		{
			address_display.setText("No advertised fixed data in this address.");
		}
		else
		{
			// Sort the carriers in the order of "download" speed.
			DisplayResults.selectSort(data_list);
		}
		
		
		listView.setAdapter(new MyCustomBaseAdapter(getActivity(), data_list));

		return view;

	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		// For Debugging
		Log.w("Viewer", "********************* AdvertisedFixedFragment.java - onPause() invoked");

		System.gc();
	}

}
