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

public class Key {
	
	public static Integer upKey (String max)
	{
		
		if(max.equalsIgnoreCase("2"))
		{
			return 0;
		}
		else if(max.equalsIgnoreCase("3"))
		{
			return  1;
		}
		else if(max.equalsIgnoreCase("4"))
		{
			return 2;
		}
		else if(max.equalsIgnoreCase("5"))
		{
			return  3;
		}
		else if(max.equalsIgnoreCase("6"))
		{
			return  4;
		}
		else if(max.equalsIgnoreCase("7"))
		{
			return  5;
		}
		else if(max.equalsIgnoreCase("8"))
		{
			return  6;
		}
		else if(max.equalsIgnoreCase("9"))
		{
			return  7;
		}
		else if(max.equalsIgnoreCase("10"))
		{
			return 8;
		}
		else if(max.equalsIgnoreCase("11"))
		{
			return 9;
		}
		else if(max.equalsIgnoreCase("null"))
		{
			return 10;
		}
		else 
		{
			return 10;
		}
		
	}
	
	public static Integer downKey (String max)
	{
		
		if(max.equalsIgnoreCase("2"))
		{
			return 11;
		}
		else if(max.equalsIgnoreCase("3"))
		{
			return  12;
		}
		else if(max.equalsIgnoreCase("4"))
		{
			return 13;
		}
		else if(max.equalsIgnoreCase("5"))
		{
			return  14;
		}
		else if(max.equalsIgnoreCase("6"))
		{
			return  15;
		}
		else if(max.equalsIgnoreCase("7"))
		{
			return  16;
		}
		else if(max.equalsIgnoreCase("8"))
		{
			return  17;
		}
		else if(max.equalsIgnoreCase("9"))
		{
			return  18;
		}
		else if(max.equalsIgnoreCase("10"))
		{
			return 19;
		}
		else if(max.equalsIgnoreCase("11"))
		{
			return 20;
		}
		else if(max.equalsIgnoreCase("null"))
		{
			return 21;
		}
		else 
		{
			return 21;
		}
	}
}
