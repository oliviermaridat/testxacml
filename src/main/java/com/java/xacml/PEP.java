package com.java.xacml;

import java.io.FileOutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

import com.sun.xacml.Indenter;
import com.sun.xacml.Policy;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;

public class PEP {
	private static final Logger LOG = Logger.getLogger(PEP.class);
	
    public PEP() {
	}
	
	public void start() {
	}

	public void destroy() {
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLine cmd = getCommandLine(args);
//		String server = cmd.hasOption('s') ? cmd.getOptionValue('s') : defaultServer;

		PEP test = null;
        try {
        	test = new PEP();
        	test.start();
        	
        	LOG.info("Create Request");
        	// Create the new request.
        	RequestManager requestManager = new RequestManager();
        	RequestCtx request = requestManager.createRequest();
        	request.encode(System.out, new Indenter());
        	
        	LOG.info("Retrieve Privacy Policy");
        	// Retrieve PrivacyPolicy for this action
        	PrivacyPolicyManager privacyPolicyManager = new PrivacyPolicyManager();
        	Policy privacyPolicy = privacyPolicyManager.createPrivacyPolicy();
        	privacyPolicy.encode(System.out, new Indenter());
        	String fileNamePrivacyPolicy = "testpolicy";
        	FileOutputStream filePrivacyPolicy = new FileOutputStream(fileNamePrivacyPolicy);
        	privacyPolicy.encode(filePrivacyPolicy, new Indenter());
        	
        	LOG.info("Evaluate to create Response");
        	// Evaluate the Request to the PrivacyPolicy by the PDP
        	PDPManager pdpManager = new PDPManager();
        	ResponseCtx response = pdpManager.evaluate(request, fileNamePrivacyPolicy);
        	response.encode(System.out, new Indenter());
        }
        catch (Exception e) {
        	e.printStackTrace();
		}
        finally {
        	if (null != test) {
        		test.destroy();
        	}
        }
	}
	
	protected static CommandLine getCommandLine(String[] args) {
		Options options = new Options();
//		options.addOption("s", true, "server where the account exists (defaults to "+defaultServer+")");
//		options.addOption("d", false, "enable debug (default: false)");
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			printHelp(options);
			System.exit(1);
		}

		if (cmd.hasOption('h')) {
			printHelp(options);
			System.exit(0);
		}
		return cmd;
	}
	
	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar createpep-bin.jar [-s server] [-u username] [-p password] [-r resource] [-d] ", options);
	}
}
