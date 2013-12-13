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

public class EstimatedMobileFragment extends Fragment {

	EstimatedData est_data;
	String address;
	
	View view;
	ListView listView;
	TextView address_display;
	LayoutInflater inflaterT;
	ImageView legendImage;

	public EstimatedMobileFragment(EstimatedData data, String address) {
		this.est_data = data;
		this.address = address;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		int i, j;
		String carrier;
		boolean exist;
		
		ArrayList<DisplayResults> data_list = new ArrayList<DisplayResults>();
		DisplayResults tmp_data_hold = null;
		view = inflater.inflate(R.layout.est_mobile_frag, container, false);
		inflaterT = inflater;
		listView = (ListView) view.findViewById(R.id.list);
		
		address_display = (TextView)view.findViewById(R.id.address);
		address_display.setText(address);
		
		//Add legend image here
		legendImage = (ImageView)view.findViewById(R.id.legend);
		legendImage.setImageResource(R.drawable.legend);

		for (i = 0; i < est_data.features.size(); i++) {

			// For debugging
			Log.w("Viewer", est_data.features.get(i).attributes.dbANAME 
					        + "/(UP)" + est_data.features.get(i).attributes.MUP 
					        + "/(DOWN)" + est_data.features.get(i).attributes.MDOWN);
			
			// Check if the dbANAME already exists in the data list.
			exist = false;
			carrier = est_data.features.get(i).attributes.dbANAME;
			
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
				tmp_data_hold = new DisplayResults();
				tmp_data_hold.setName(est_data.features.get(i).attributes.dbANAME);
				if (!est_data.features.get(i).attributes.MUP.equals(" ")) {
					tmp_data_hold.setImageNumber(Key.upKey(est_data.features.get(i).attributes.MUP));
				} 
				if (!est_data.features.get(i).attributes.MDOWN.equals(" ")) {
					tmp_data_hold.setImageNumber2(Key.downKey(est_data.features.get(i).attributes.MDOWN));
				} 
				data_list.add(tmp_data_hold);
			}
			else
			{
				if (!est_data.features.get(i).attributes.MUP.equals(" ")) {
					data_list.get(j).setImageNumber(Key.upKey(est_data.features.get(i).attributes.MUP));
				} 
				if (!est_data.features.get(i).attributes.MDOWN.equals(" ")) {
					data_list.get(j).setImageNumber2(Key.downKey(est_data.features.get(i).attributes.MDOWN));
				} 
			}
		}
		
		
		if (data_list.size() == 0)
		{
			address_display.setText("No estimated mobile data in this address.");
		}
		else
		{
			// Sort the carriers in the order of "download" speed.
			DisplayResults.selectSort(data_list);
		}
		
		listView.setAdapter(new MyCustomBaseAdapter(getActivity(), data_list));

		return view;

	}
}
