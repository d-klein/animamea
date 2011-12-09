/**
 *  Copyright 2011, Tobias Senger
 *  
 *  This file is part of animamea.
 *
 *  Animamea is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Animamea is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License   
 *  along with animamea.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.tsenger.animamea.asn1;

import java.io.IOException;

import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;

/**
 * @author Tobias Senger (tobias@t-senger.de)
 * 
 */
public class DiscretionaryData {

	private DERApplicationSpecific dData = null;
	private byte[] data = null;
	

	public DiscretionaryData(byte[] data) throws IOException {
		this.data = data.clone();
		DEROctetString der = new DEROctetString(data);
		dData = new DERApplicationSpecific(false, 0x13, der);
	}

	public DiscretionaryData(byte data) throws IOException {
		this.data = new byte[]{data};
		DERInteger der = new DERInteger(data);
		dData = new DERApplicationSpecific(false, 0x13, der);
	}

	public DERObject toASN1Object() {
		return dData;
	}

	public byte[] getEncoded() {
		return dData.getDEREncoded();
	}
	
	public byte[] getData() {
		return data;
	}
	

}
