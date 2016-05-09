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

package de.tsenger.animamea;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import org.apache.log4j.Logger;

import de.tsenger.animamea.iso7816.SecureMessaging;
import de.tsenger.animamea.iso7816.SecureMessagingException;
import de.tsenger.animamea.tools.HexString;

/**
 * @author Tobias Senger (tobias@t-senger.de)
 * 
 */
public class AmCardHandler {

	private Card card = null;;
	private CardChannel channel = null;
	private SecureMessaging sm = null;
	private boolean connected = false;
	
	static Logger logger = Logger.getLogger(AmCardHandler.class);

	/**
	 * Sendet die übergebene CommandAPDU an die konnektierte Karte. 
	 * Falls SecureMessaging initilisiert und gesetzt ist wird die APDU vor dem
	 * Senden SM-geschützt. Die empfangende APDU wird SM befreit und zurückgegeben.
	 * @param capdu Plain Command-APDU
	 * @return plain Response-APDU
	 * @throws SecureMessagingException 
	 * @throws CardException 
	 */
	public ResponseAPDU transceive(CommandAPDU capdu) throws SecureMessagingException, CardException  {
		
		logger.debug("plain C-APDU:\n" + HexString.bufferToHex(capdu.getBytes()));
		
		if (sm != null)	{ 
			capdu = sm.wrap(capdu);
			byte[] capdu_long = capdu.getBytes();
			int len = capdu_long.length;
			if(capdu_long.length > 261 && capdu_long[4] == 0 && capdu_long[5] == 0 && capdu_long[len-1] == 0 && capdu_long[len-2] == 0) {
				System.out.println("caught a very long (encrypted) capdu");
				System.out.println("shrinking Lc to 1 byte and Le to 1 byte...");
				logger.debug("original C-APDU:\n"+ HexString.bufferToHex(capdu.getBytes()));
				logger.debug("removing zeros in Lc:\n");
				byte[] patchedBytes = new byte[capdu_long.length - 3];
				for(int i=0;i<4;i++) {
					patchedBytes[i] = capdu_long[i];
				}
				for(int i=4;i<patchedBytes.length;i++) {
					patchedBytes[i] = capdu_long[i+2];
				}
				logger.debug("patched plain:\n"+ HexString.bufferToHex(patchedBytes));
				capdu = new CommandAPDU(patchedBytes);
			}
			logger.debug("potected C-APDU:\n"+ HexString.bufferToHex(capdu.getBytes()));
		}
		
		ResponseAPDU resp = channel.transmit(capdu);

		if (sm != null) {
			logger.debug("potected R-APDU:\n"+ HexString.bufferToHex(resp.getBytes()));
			resp = sm.unwrap(resp);
		}
		
		logger.debug("plain R-APDU:\n"+ HexString.bufferToHex(resp.getBytes()));
		
		return resp;
	}
	

	/**
	 * Aktiviert das SecureMessaging für alle nachfolgenden transceive-Aufrufe.
	 * @param sm initialisiertes SecureMessaging-Objekt
	 */
	public void setSecureMessaging(SecureMessaging sm) {
		this.sm = sm;
	}

	/**
	 * Establish connection to terminal and card on terminal.
	 * 
	 * @param index
	 *            Number of the terminal to use
	 * @return connect Connection successfull ?
	 * @throws CardException
	 */
	public boolean connect(int index) throws CardException {
		
		/* Is a Reader connected we can access? */
		if (TerminalFactory.getDefault().terminals().list().size() == 0) {
			logger.error("No reader available");
			throw new CardException("No reader available");
		}

		/* Terminal we are working on */
		CardTerminal terminal = TerminalFactory.getDefault().terminals().list().get(index);

		/* Is a card present? */
		if (!terminal.isCardPresent()) {
			logger.error("No card available");
			throw new CardException("No card available");
		}

		card = terminal.connect("T=1");
		channel = card.getBasicChannel();
		connected = true;
		return connected;
	}

	public void disconnect() throws CardException {
		channel.close();
		card.disconnect(true);
		connected = false;
		
	}

	public byte[] getATR() {
		return card.getATR().getBytes();
	}

}
