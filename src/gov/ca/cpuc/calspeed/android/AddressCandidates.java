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

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AddressCandidates {

	public AddressCandidates() {
	}

	SpatialReference spatialReference;

	public List<Candidates> candidates;

	public static class Candidates {
		public Candidates() {
		}

		@SerializedName("address")
		public String address;

		Location location;

		@SerializedName("score")
		public double score;

		Attribute attributes;
	}

	public static class Attribute {

		public Attribute() {
		}
		
		// "Loc_name" can have "US_RoofTop", "US_Streets", "US_StreetName", "US_Zipcode", etc.
		@SerializedName("Loc_name")
		public String Loc_name;
		
		@SerializedName("State")
		public String State;		

		@SerializedName("Disp_Lon")
		public double dispLong;

		@SerializedName("Disp_Lat")
		public double dispLat;
		
		@SerializedName("X")
		public double X;

		@SerializedName("Y")
		public double Y;

	}

	public static class Location {

		public Location() {
		}

		@SerializedName("x")
		public double x;

		@SerializedName("y")
		public double y;

	}

	public static class SpatialReference {

		SpatialReference() {
		}

		@SerializedName("wkid")
		public int wkid;

	}

}
