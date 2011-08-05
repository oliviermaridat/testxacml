package com.java.xacml;

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
import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.RFC822NameAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Subject;

public class RequestManager {
	private static final Logger LOG = Logger.getLogger(RequestManager.class);
	
	public RequestManager() {
	}
	
	public void start() {
	}

	public void destroy() {
	}
	
	public RequestCtx createRequest() throws URISyntaxException {
		// Create the new request.
    	// Environment hashset is empty
    	return new RequestCtx(setupSubjects(), setupResource(), setupAction(), new HashSet<Object>());
	}
	
	public Set<Subject> setupSubjects() throws URISyntaxException {
		Set<Subject> subjects = new HashSet<Subject>();
		Set<Attribute> attributes = new HashSet<Attribute>();

		// Create the subject section with two attributes, the first with
		// the subject's identity...
		URI subjectId = new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
		RFC822NameAttribute value = new RFC822NameAttribute("mverma2@secf.com");
		attributes.add(new Attribute(subjectId, null, null, value));
		
		// ...and the second with the subject's group membership
		URI groupId = new URI("group");
		StringAttribute stringAttribValue = new StringAttribute("owner");
		attributes.add(new Attribute(groupId,null,null,stringAttribValue));

		// Bundle the attributes in a subject with the default category
		subjects.add(new Subject(attributes));
		return subjects;
	}

	public Set<Attribute> setupResource() throws URISyntaxException {
		Set<Attribute> resource = new HashSet<Attribute>();

		// The resource being requested
		AnyURIAttribute value = new AnyURIAttribute(new URI("file:///D:/Documents/Administrator/Desktop/ProjectPlan1.html"));

		// Create the resource using a standard, required identifier for
		// the resource being requested
		resource.add(new Attribute(new URI(EvaluationCtx.RESOURCE_ID), null, null, value));
		return resource;
	}
	
	public Set<Attribute> setupAction() throws URISyntaxException {
		Set<Attribute> action = new HashSet<Attribute>();

		// This is a standard URI that can optionally be used to specify
		// the action being requested
		URI actionId = new URI("urn:oasis:names:tc:xacml:1.0:action:action-id");

		// Create the action
		action.add(new Attribute(actionId, null, null, new StringAttribute("open")));
		return action;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLine cmd = getCommandLine(args);
//		String server = cmd.hasOption('s') ? cmd.getOptionValue('s') : defaultServer;

		RequestManager test = null;
        try {
        	test = new RequestManager();
        	test.start();
        	
        	LOG.info("Create PEP");
        	// Create the new request.
        	// Environment hashset is empty
        	RequestCtx request = test.createRequest();

        	// Encode the request and print it to standard out
        	request.encode(System.out, new Indenter());
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
