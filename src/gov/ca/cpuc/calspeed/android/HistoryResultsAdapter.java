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

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HistoryResultsAdapter extends ArrayAdapter<History>{
	
	private ArrayList<History> result = new ArrayList<History>();
	
	private Context context;
	
	static class ViewHolder {
		public TextView id;
		public TextView date;
		public TextView upload;
		public TextView download;
		public TextView latency;
		public TextView jitter;
		
	}
	
	public HistoryResultsAdapter(Context context, ArrayList<History> result) {
		super(context, R.layout.historylist,result);
		this.result = result;
		
		this.context = context;
		
	}
	

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context
    				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.historylist, null);
            
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.id = (TextView)v.findViewById(R.id.historyID);
			viewHolder.date = (TextView) v.findViewById(R.id.historyDate);
			viewHolder.upload = (TextView) v.findViewById(R.id.historyUpload);
			viewHolder.download = (TextView) v.findViewById(R.id.historyDownload);
			viewHolder.latency = (TextView) v.findViewById(R.id.historyLatency);
			viewHolder.jitter = (TextView) v.findViewById(R.id.historyJitter);
			

			v.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) v.getTag();
		
        holder.id.setText(result.get(position).getStringID());
		holder.date.setText(result.get(position).getFormattedDate("\n "));
		holder.upload.setText(result.get(position).getUploadAverage()+"\nkbps");
		holder.download.setText(result.get(position).getDownloadAverage()+"\nkbps");
		holder.latency.setText(result.get(position).getLatencyAverage()+"\nms");
		holder.jitter.setText(result.get(position).getJitterAverage()+"\nms");
		
		
		return (v);
	}
	
}
