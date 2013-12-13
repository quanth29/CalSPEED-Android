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
import java.util.Collections;

public class DisplayResults {

	private String name;
	private int imageNumber;    // Upload speed index
	private int imageNumber2;   // Download speed index

	public DisplayResults()
	{
		name = "";
		imageNumber = 10;   // upload index 10 is "unavailable" at "MyCustomBaseAdapter.java
		imageNumber2 =21;   // download index 21 is "unavailable" at "MyCustomBaseAdapter.java
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setImageNumber(int imageNumber) {
		this.imageNumber = imageNumber;
	}

	public int getImageNumber() {
		return imageNumber;
	}

	public void setImageNumber2(int imageNumber) {
		this.imageNumber2 = imageNumber;
	}

	public int getImageNumber2() {
		return imageNumber2;
	}
	
	public static void selectSort(ArrayList<DisplayResults> data_list)
	{
		int i, j, k, s;
		
		// Sort the carriers in the order of "download" speed.
		for (i = 0; i < data_list.size()-1; i++) 
		{
			for (j = i+1; j < data_list.size(); j++) 
			{
				// download index 21 is "unavailable" at "MyCustomBaseAdapter.java
				if ((data_list.get(i).getImageNumber2() == 21) ||
					( (data_list.get(i).getImageNumber2() != 21) &&
					  (data_list.get(j).getImageNumber2() != 21) &&
					  (data_list.get(i).getImageNumber2() < data_list.get(j).getImageNumber2()))) {

					//Exchange elements
					Collections.swap(data_list, i, j);
	            }
	        }
	    }
		
		// If the "download" speeds are tied, large "upload" comes first.
		for (k = 0; k < data_list.size()-1; k++) 
		{
			s = k+1;
			while ((s < data_list.size()) &&
				   (data_list.get(k).getImageNumber2() == data_list.get(s).getImageNumber2()))
			{
				s++;
			}
			
			// Sort the upload speeds from index "k" to "s-1" because their downloads are tied.
			if (s != (k+1))
			{
				for (i = k; i < s-1; i++) 
				{
					for (j = i+1; j < s; j++) 
					{
						// upload index 10 is "unavailable" at "MyCustomBaseAdapter.java
						if ((data_list.get(i).getImageNumber() == 10) ||
							( (data_list.get(i).getImageNumber() != 10) &&
							  (data_list.get(j).getImageNumber() != 10) &&
							  (data_list.get(i).getImageNumber() < data_list.get(j).getImageNumber()))) {
							
							// Exchange elements
							Collections.swap(data_list, i, j);
			            }
			        }
			    }					
			}
		}		
	}
}
