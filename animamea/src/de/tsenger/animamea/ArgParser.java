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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author Dominik Klein
 *
 */
public class ArgParser {

	CommandLineParser parser = new GnuParser();
	Options options = new Options();

	public CommandLine parse(String args[]) throws ParseException {
		return this.parser.parse(this.options, args);		
	}

	public void displayHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Zernike", options );
	}

	@SuppressWarnings("static-access") public ArgParser() {	

		// @SuppressWarnings("fallthrough")
		options.addOption(OptionBuilder.withDescription("set new (six digit) pin, authenticate w/old pin, (certificates must be supplied, and chain must have right to change pin)")
				.hasArgs(2)
				.withLongOpt("set-pin-with-pin")
				.withArgName("oldpinval")
				.withArgName("newpinval")
				.create("spp"));
		options.addOption(OptionBuilder.withDescription("set new (six digit) pin, authenticate w can, (certificates must be supplied, and chain must have right to change pin)")
				.hasArgs(2)
				.withLongOpt("set-pin-with-can")
				.withArgName("can")
				.withArgName("newpinval")
				.create("spc"));		
		options.addOption(OptionBuilder.withDescription("filename of cvca certificate (card verifiable format)")
				.hasArg()
				.withLongOpt("with-cvca-cert")
				.withArgName("filename")
				.create("cvca"));
		options.addOption(OptionBuilder.withDescription("terminal id; default is 0.")
				.hasArg()
				.withLongOpt("terminal-id")
				.withArgName("terminalid")
				.create("tid"));
		options.addOption(OptionBuilder.withDescription("filename of program to start immediately before running pace")
				.hasArg()
				.withLongOpt("trigger-pace-start")
				.withArgName("filename")
				.create("ps"));
		options.addOption(OptionBuilder.withDescription("filename of program to start immediately after running pace")
				.hasArg()
				.withLongOpt("trigger-pace-finish")
				.withArgName("filename")
				.create("pf"));
		options.addOption(OptionBuilder.withDescription("filename of dv certificate (card verifiable format)")
				.hasArg()
				.withLongOpt("with-dv-cert")
				.withArgName("filename")
				.create("dv"));		
		options.addOption(OptionBuilder.withDescription("filename of terminal certificate (card verifiable format)")
				.hasArg()
				.withLongOpt("with-terminal-cert")
				.withArgName("filename")
				.create("tc"));
		options.addOption(OptionBuilder.withDescription("filename of terminal private key (pkcs8 format)")
				.hasArg()
				.withLongOpt("with-terminal-key")
				.withArgName("file")
				.create("tk"));
		options.addOption(OptionBuilder.withDescription("run pace protocol with pin")
				.hasArg()
				.withLongOpt("run-pace-pin")
				.withArgName("pinval")
				.create("rp"));
		options.addOption(OptionBuilder.withDescription("shows this help screen")
				.withLongOpt("help")
				.create("h"));		
	}
	
}