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

package de.tsenger.animamea.pace;

import static de.tsenger.animamea.tools.Converter.bigIntToByteArray;
import static de.tsenger.animamea.tools.Converter.byteArrayToECPoint;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECPoint.Fp;

import de.tsenger.animamea.tools.HexString;

/**
 * PACE mit Elliptic Curve Diffie Hellman
 * 
 * @author Tobias Senger (tobias@t-senger.de)
 * 
 */

public class PaceECDH extends Pace {

	private ECPoint pointG = null;
	private ECCurve.Fp curve = null;
	private BigInteger nonce_s = null;
	
	private final SecureRandom randomGenerator = new SecureRandom();

	private BigInteger PCD_SK_x1 = null;
	private BigInteger PCD_SK_x2 = null;


	public PaceECDH(ECParameterSpec ecParameterSpec) {

		pointG = ecParameterSpec.getG();
		curve = (org.bouncycastle.math.ec.ECCurve.Fp) ecParameterSpec.getCurve();
		Random rnd = new Random();
		randomGenerator.setSeed(rnd.nextLong());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tsenger.animamea.pace.Pace#getX1(byte[])
	 */
	@Override
	public byte[] getX1(byte[] s) {
		nonce_s = new BigInteger(1, s);
		
		byte[] x1 = new byte[(curve.getFieldSize() / 8)];
		randomGenerator.nextBytes(x1);
		PCD_SK_x1 = new BigInteger(1, x1);
		
		ECPoint PCD_PK_X1 = pointG.multiply(PCD_SK_x1);
		
		return PCD_PK_X1.getEncoded();
	}
	

	/**
	 * Berechnet mit Hilfe des öffentlichen Schlüssels der Karte das erste
	 * Shared Secret P, den neuen Punkt G', sowie den zweiten öffentlichen
	 * Schlüssels des Terminals (X2 = x2 * G').
	 * 
	 * @param Y1
	 *            Erster öffentlicher Schlüssel der Karte
	 * @return Zweiter öffentlicher Schlüssel X2 des Terminals.
	 */
	private ECPoint getX2(ECPoint.Fp Y1) {
		
		ECPoint.Fp SharedSecret_P = (Fp) Y1.multiply(PCD_SK_x1);
		System.out.println("\nP: "+HexString.bufferToHex(SharedSecret_P.getEncoded()));
		ECPoint pointG_strich = pointG.multiply(nonce_s).add(SharedSecret_P);
		System.out.println("\nG_strich: "+HexString.bufferToHex(pointG_strich.getEncoded()));
		byte[] x2 = new byte[(curve.getFieldSize() / 8)];
		randomGenerator.nextBytes(x2);
		PCD_SK_x2 = new BigInteger(1, x2);
		
		ECPoint PCD_PK_X2 = pointG_strich.multiply(PCD_SK_x2);
		
		return PCD_PK_X2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tsenger.animamea.pace.Pace#getX2(byte[])
	 */
	@Override
	public byte[] getX2(byte[] Y1Bytes) {
		
		ECPoint.Fp Y1 = null;
		Y1 = (Fp) byteArrayToECPoint(Y1Bytes, curve);

		return getX2(Y1).getEncoded();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tsenger.animamea.pace.Pace#getSharedSecret_K(byte[])
	 */
	@Override
	public byte[] getSharedSecret_K(byte[] Y2) {
		
		ECPoint PICC_PK_Y2 = byteArrayToECPoint(Y2, curve);
		ECPoint.Fp K = (Fp) PICC_PK_Y2.multiply(PCD_SK_x2);
		return bigIntToByteArray(K.normalize().getXCoord().toBigInteger());
	}

}
