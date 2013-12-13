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
import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCustomBaseAdapter extends BaseAdapter {
	
	private Integer[] imgid = { 
			R.drawable.up2, 
			R.drawable.up3, 
			R.drawable.up4,
			R.drawable.up5, 
			R.drawable.up6, 
			R.drawable.up7, 
			R.drawable.up8,
			R.drawable.up9, 
			R.drawable.up10, 
			R.drawable.up11,
			R.drawable.upnull, 
			R.drawable.down2, 
			R.drawable.down3,
			R.drawable.down4, 
			R.drawable.down5, 
			R.drawable.down6,
			R.drawable.down7, 
			R.drawable.down8, 
			R.drawable.down9,
			R.drawable.down10, 
			R.drawable.down11, 
			R.drawable.downnull, 
			R.drawable.legend
	};

	private static ArrayList<DisplayResults> searchArrayList;

	private LayoutInflater mInflater;

	public MyCustomBaseAdapter(Context context, List<DisplayResults> list) {
		searchArrayList = (ArrayList<DisplayResults>) list;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return searchArrayList.size();
	}

	public Object getItem(int position) {
		return searchArrayList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_row, null);
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView.findViewById(R.id.ServiceName);
			holder.imgPhoto = (ImageView) convertView.findViewById(R.id.Up);
			holder.imgPhoto2 = (ImageView) convertView.findViewById(R.id.Down);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.txtName.setText(searchArrayList.get(position).getName());
		holder.imgPhoto.setImageResource(imgid[searchArrayList.get(position).getImageNumber()]);
		holder.imgPhoto2.setImageResource(imgid[searchArrayList.get(position).getImageNumber2()]);
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView txtName;
		ImageView imgPhoto;
		ImageView imgPhoto2;
	}
}
