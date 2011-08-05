package com.java.xacml;

import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.Indenter;
import com.sun.xacml.PDP;
import com.sun.xacml.PDPConfig;
import com.sun.xacml.Policy;
import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.RFC822NameAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.ctx.Subject;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.impl.FilePolicyModule;

public class PDPManager {
	private static final Logger LOG = Logger.getLogger(PDPManager.class);
	
	public PDPManager() {
	}
	
	public void start() {
	}

	public void destroy() {
	}
	
	public ResponseCtx evaluate(RequestCtx request, String filePolicy) throws URISyntaxException {
		// Step 1: Create a FilePolicyModule (which is a sub-PolicyFinderModule) and initialize it
    	FilePolicyModule filePolicyModule = new FilePolicyModule();
		filePolicyModule.addPolicy(filePolicy);
		Set<PolicyFinderModule> policyModules = new HashSet<PolicyFinderModule>();
    	policyModules.add(filePolicyModule);
    	PolicyFinder policyFinder = new PolicyFinder();
    	policyFinder.setModules(policyModules);
    	
    	// Step 2: Create the PDP
    	PDP pdp = new PDP(new PDPConfig(null, policyFinder, null));
    	// Step 3: Evaluate the request. Generate the response.
    	ResponseCtx response = pdp.evaluate(request);
    	Set<Result> results = response.getResults();
    	for(Result result : results) {
    		LOG.info(Result.DECISION_PERMIT+"=="+result.getDecision());
    	}
    	return response;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Step 1: get parameters
		CommandLine cmd = getCommandLine(args);
		
		String requestFile = cmd.hasOption('r') ? cmd.getOptionValue('r') : "";
		String accessPolicy = cmd.hasOption('a') ? cmd.getOptionValue('a') : "";
		
		

		PDPManager test = null;
        try {
        	test = new PDPManager();
        	test.start();
        	
        	LOG.info("Create PDP");
        	// Get policies
    		String[] policyFiles = cmd.getArgs();
    		// Create a PolicyFinderModule and initialize it
        	// Use the sample FilePolicyModule, which is configured using the policies from the command line
        	FilePolicyModule filePolicyModule = new FilePolicyModule();
        	PrivacyPolicyManager policy = new PrivacyPolicyManager();
        	args[0] = "test";
        	args[1] = policy.createPrivacyPolicy().toString();
        	for (int i = 1; i < args.length; i++) {
        		LOG.info(args[i]);
        		policyFiles[i-1] = args[i];
        		filePolicyModule.addPolicy(policyFiles[i-1]);
        	}
        	// Step 2: Set up the PolicyFinder that this PDP will use
        	PolicyFinder policyFinder = new PolicyFinder();
        	Set<FilePolicyModule> policyModules = new HashSet<FilePolicyModule>();
        	policyModules.add(filePolicyModule);
        	policyFinder.setModules(policyModules);

        	// Step 4: Create the PDP
        	PDP pdp = new PDP(new PDPConfig(null, policyFinder, null));

        	//  Get the request send by the PEP
//        	RequestCtx request = RequestCtx.getInstance(new FileInputStream(requestFile));
        	RequestManager pep = new RequestManager();
        	RequestCtx request = pep.createRequest();

        	// Step 5: Evaluate the request. Generate the response.
        	ResponseCtx response = pdp.evaluate(request);

        	// Display the output on std out
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
		options.addOption("s", true, "the request");
		options.addOption("a", true, "the access policy");
		options.addOption("p", false, "policies if needed");
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
		formatter.printHelp("java -jar createpdp-bin.jar [-r request] [-a AccessPolicy] [-p policies]", options);
	}
}
