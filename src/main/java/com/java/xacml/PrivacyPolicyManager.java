package com.java.xacml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

import com.sun.xacml.Indenter;
import com.sun.xacml.Policy;
import com.sun.xacml.Rule;
import com.sun.xacml.Target;
import com.sun.xacml.TargetMatch;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.combine.CombiningAlgFactory;
import com.sun.xacml.combine.OrderedPermitOverridesRuleAlg;
import com.sun.xacml.combine.RuleCombiningAlgorithm;
import com.sun.xacml.cond.Apply;
import com.sun.xacml.cond.Function;
import com.sun.xacml.cond.FunctionFactory;
import com.sun.xacml.cond.FunctionTypeException;
import com.sun.xacml.ctx.Result;

public class PrivacyPolicyManager {
	private static final Logger LOG = Logger.getLogger(PrivacyPolicyManager.class);
	
	public PrivacyPolicyManager() {
	}
	
	public void start() {
	}

	public void destroy() {
	}
	
	public Policy createPrivacyPolicy() throws URISyntaxException, UnknownIdentifierException, FunctionTypeException {
		// --- Create policy identifier and policy description
		URI policyId = new URI("MyPolicy");
		String description = "This AccessPolicy applies to any account at secf.com  accessing file:///D:/Documents/Administrator/Desktop/Project Plan.html.";

		// --- Rule combining algorithm for the Policy
		// OrderedPermitOverridesRuleAlg: rules are evaluated in the order in which they are specified in the policy
		URI combiningAlgId = new URI(OrderedPermitOverridesRuleAlg.algId);
		CombiningAlgFactory factory = CombiningAlgFactory.getInstance();
		RuleCombiningAlgorithm combiningAlg = (RuleCombiningAlgorithm) (factory.createAlgorithm(combiningAlgId));

		// Create the target for the policy
		Target policyTarget = createPolicyTarget();

		// Create the rules for the policy
		List<Rule> ruleList = createPolicyRules();

		// Generate the policy
		Policy policy =  new Policy(policyId, combiningAlg, description, policyTarget, ruleList);

		return policy;
	}
	
	private Target createPolicyTarget() throws URISyntaxException, UnknownIdentifierException, FunctionTypeException {
		List<List<TargetMatch>> subjects = new ArrayList<List<TargetMatch>>();
		List<List<TargetMatch>> resources = new ArrayList<List<TargetMatch>>();
		List<List<TargetMatch>> actions = new ArrayList<List<TargetMatch>>();
		// Attributes of Subject type
		// Multiple subject attributes can be specified. In this
		// case only one is being defined.
		List<TargetMatch> subject = new ArrayList<TargetMatch>();
		// Attributes of resource type
		// Multiple resource attributes can be specified. In this
		// case only one is being defined.
		List<TargetMatch> resource = new ArrayList<TargetMatch>();
		List<TargetMatch> action = new ArrayList<TargetMatch>();
		
		// get the factory that handles Target functions
		FunctionFactory factory = FunctionFactory.getTargetInstance();
		
		// -- 1 Subject
		// Match function for the subject-id attribute
		// get an instance of the right function for matching subject attributes
		Function subjectFunction = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match");
		
		URI subjectDesignatorType = new URI("urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name");
		URI subjectDesignatorId = new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
		StringAttribute subjectValue = new StringAttribute("mverma@secf.com");
		
		AttributeDesignator subjectDesignator = new AttributeDesignator(
						AttributeDesignator.SUBJECT_TARGET, // the <SubjectAttributeDesignator /> tag
						subjectDesignatorType,
						subjectDesignatorId,
						false);
		TargetMatch subjectMatch = new TargetMatch(
				TargetMatch.SUBJECT, // the <SubjectMatch></SubjectMatch> tag
				subjectFunction,
				subjectDesignator,
				subjectValue);
		subject.add(subjectMatch);

		
		// -- 1 Ressource
		// Match function for the resource-id attribute
		// Get an instance of the right function for matching resource attribute
		Function resourceFunction = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:anyURI-equal");
		
		URI resourceDesignatorType = new URI("http://www.w3.org/2001/XMLSchema#anyURI");
		URI resourceDesignatorId = new URI("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
		AnyURIAttribute resourceValue = new AnyURIAttribute(new URI("file:///D:/Documents/Administrator/Desktop/ProjectPlan.html"));
		
		AttributeDesignator resourceDesignator = new AttributeDesignator(
				AttributeDesignator.RESOURCE_TARGET, // the <ResourceAttributeDesignator /> tag
				resourceDesignatorType,
				resourceDesignatorId,
				false);
		TargetMatch resourceMatch = new TargetMatch(
				TargetMatch.RESOURCE, // the <ResourceMatch></ResourceMatch> tag
				resourceFunction,
				resourceDesignator,
				resourceValue);
		resource.add(resourceMatch);
		
		

		// -- 1 Action
		Function actionFunction = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:string-equal");
		
		URI actionDesignatorType = new URI("http://www.w3.org/2001/XMLSchema#string");
		URI actionDesignatorId = new URI("urn:oasis:names:tc:xacml:1.0:action:action-id");
		StringAttribute actionValue = new StringAttribute("open");
		
		AttributeDesignator actionDesignator = new AttributeDesignator(
				AttributeDesignator.ACTION_TARGET, // the <ActionAttributeDesignator /> tag
				actionDesignatorType,
				actionDesignatorId,
				false);
		TargetMatch actionMatch = new TargetMatch(
				TargetMatch.ACTION, // the <ActionMatch></ActionMatch> tag
				actionFunction,
				actionDesignator,
				actionValue);
		action.add(actionMatch);
		

		// -- Put the subject and resource sections into their lists
		subjects.add(subject);
		resources.add(resource);
		actions.add(action);
		
		// add 2ème action
		StringAttribute actionValue2 = new StringAttribute("close");
		TargetMatch actionMatch2 = new TargetMatch(
				TargetMatch.ACTION, // the <ActionMatch></ActionMatch> tag
				actionFunction,
				actionDesignator,
				actionValue2);
		List<TargetMatch> action2 = new ArrayList<TargetMatch>();
		action2.add(actionMatch2);
		actions.add(action2);

		// Create and return the new target. No action type
		// attributes have been specified in the target
//		return new Target(subjects, resources, actions);
		return new Target(null, null, null);
	}

	private List<Rule> createPolicyRules() throws URISyntaxException, UnknownIdentifierException, FunctionTypeException {
		// Step 1: Define the identifier for the rule
		URI ruleId = new URI("ProjectPlanAccessRule");
		String ruleDescription = "Rule for accessing project plan";

		// Step 2: Define the effect of the rule
		int effect = Result.DECISION_PERMIT;

		// Step 3: Get the target for the rule
		Target target = createRuleTarget();

		// Step 4: Get the condition for the rule
		Apply condition = createRuleCondition();

		// Step 5: Create the rule
		Rule openRule = new Rule(ruleId, effect,ruleDescription, target, condition);
//		Rule openRule = new Rule(ruleId, effect,ruleDescription, target, null);

		// Create a list for the rules and add the rule to it
		List<Rule> ruleList = new ArrayList<Rule>();
		ruleList.add(openRule);
		
		// --- rule 2
		URI ruleId2 = new URI("R2");
		// Step 3: Get the target for the rule
		Target target2 = createRuleTarget2();

		// Step 4: Get the condition for the rule
		Apply condition2 = null;

		// Step 5: Create the rule
		Rule openRule2 = new Rule(ruleId2, effect,ruleDescription, target2, condition2);
		ruleList.add(openRule2);
		return ruleList;
	}

	private Target createRuleTarget() throws URISyntaxException, UnknownIdentifierException, FunctionTypeException {
		List<List<TargetMatch>> subjects = new ArrayList<List<TargetMatch>>();
		List<List<TargetMatch>> resources = new ArrayList<List<TargetMatch>>();
		List<List<TargetMatch>> actions = new ArrayList<List<TargetMatch>>();
		// Attributes of Subject type
		// Multiple subject attributes can be specified. In this
		// case only one is being defined.
		List<TargetMatch> subject = new ArrayList<TargetMatch>();
		// Attributes of resource type
		// Multiple resource attributes can be specified. In this
		// case only one is being defined.
		List<TargetMatch> resource = new ArrayList<TargetMatch>();
		List<TargetMatch> action = new ArrayList<TargetMatch>();
		
		// get the factory that handles Target functions
		FunctionFactory factory = FunctionFactory.getTargetInstance();
		
		// -- 1 Subject
		// Match function for the subject-id attribute
		// get an instance of the right function for matching subject attributes
		Function subjectFunction = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match");
		
		URI subjectDesignatorType = new URI("urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name");
		URI subjectDesignatorId = new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
		StringAttribute subjectValue = new StringAttribute("mverma@secf.com");
		
		AttributeDesignator subjectDesignator = new AttributeDesignator(
						AttributeDesignator.SUBJECT_TARGET, // the <SubjectAttributeDesignator /> tag
						subjectDesignatorType,
						subjectDesignatorId,
						false);
		TargetMatch subjectMatch = new TargetMatch(
				TargetMatch.SUBJECT, // the <SubjectMatch></SubjectMatch> tag
				subjectFunction,
				subjectDesignator,
				subjectValue);
		subject.add(subjectMatch);

		
		// -- 1 Ressource
		// Match function for the resource-id attribute
		// Get an instance of the right function for matching resource attribute
		Function resourceFunction = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:anyURI-equal");
		
		URI resourceDesignatorType = new URI("http://www.w3.org/2001/XMLSchema#anyURI");
		URI resourceDesignatorId = new URI("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
		AnyURIAttribute resourceValue = new AnyURIAttribute(new URI("file:///D:/Documents/Administrator/Desktop/ProjectPlan.html"));
		
		AttributeDesignator resourceDesignator = new AttributeDesignator(
				AttributeDesignator.RESOURCE_TARGET, // the <ResourceAttributeDesignator /> tag
				resourceDesignatorType,
				resourceDesignatorId,
				false);
		TargetMatch resourceMatch = new TargetMatch(
				TargetMatch.RESOURCE, // the <ResourceMatch></ResourceMatch> tag
				resourceFunction,
				resourceDesignator,
				resourceValue);
		resource.add(resourceMatch);
		
		

		// -- 1 Action
		Function actionFunction = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:string-equal");
		
		URI actionDesignatorType = new URI("http://www.w3.org/2001/XMLSchema#string");
		URI actionDesignatorId = new URI("urn:oasis:names:tc:xacml:1.0:action:action-id");
		StringAttribute actionValue = new StringAttribute("open");
		
		AttributeDesignator actionDesignator = new AttributeDesignator(
				AttributeDesignator.ACTION_TARGET, // the <ActionAttributeDesignator /> tag
				actionDesignatorType,
				actionDesignatorId,
				false);
		TargetMatch actionMatch = new TargetMatch(
				TargetMatch.ACTION, // the <ActionMatch></ActionMatch> tag
				actionFunction,
				actionDesignator,
				actionValue);
		action.add(actionMatch);
		

		// -- Put the subject and resource sections into their lists
		subjects.add(subject);
		resources.add(resource);
		actions.add(action);

		// Create and return the new target. No action type
		// attributes have been specified in the target
		return new Target(subjects, resources, actions);
	}
	
	private Target createRuleTarget2() throws URISyntaxException, UnknownIdentifierException, FunctionTypeException {
		List<List<TargetMatch>> subjects = new ArrayList<List<TargetMatch>>();
		List<List<TargetMatch>> resources = new ArrayList<List<TargetMatch>>();
		List<List<TargetMatch>> actions = new ArrayList<List<TargetMatch>>();
		// Attributes of Subject type
		// Multiple subject attributes can be specified. In this
		// case only one is being defined.
		List<TargetMatch> subject = new ArrayList<TargetMatch>();
		// Attributes of resource type
		// Multiple resource attributes can be specified. In this
		// case only one is being defined.
		List<TargetMatch> resource = new ArrayList<TargetMatch>();
		List<TargetMatch> action = new ArrayList<TargetMatch>();
		
		// get the factory that handles Target functions
		FunctionFactory factory = FunctionFactory.getTargetInstance();
		
		// -- 1 Subject
		// Match function for the subject-id attribute
		// get an instance of the right function for matching subject attributes
		Function subjectFunction = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match");
		
		URI subjectDesignatorType = new URI("urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name");
		URI subjectDesignatorId = new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
		StringAttribute subjectValue = new StringAttribute("mverma2@secf.com");
		
		AttributeDesignator subjectDesignator = new AttributeDesignator(
						AttributeDesignator.SUBJECT_TARGET, // the <SubjectAttributeDesignator /> tag
						subjectDesignatorType,
						subjectDesignatorId,
						false);
		TargetMatch subjectMatch = new TargetMatch(
				TargetMatch.SUBJECT, // the <SubjectMatch></SubjectMatch> tag
				subjectFunction,
				subjectDesignator,
				subjectValue);
		subject.add(subjectMatch);

		
		// -- 1 Ressource
		// Match function for the resource-id attribute
		// Get an instance of the right function for matching resource attribute
		Function resourceFunction = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:anyURI-equal");
		
		URI resourceDesignatorType = new URI("http://www.w3.org/2001/XMLSchema#anyURI");
		URI resourceDesignatorId = new URI("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
		AnyURIAttribute resourceValue = new AnyURIAttribute(new URI("file:///D:/Documents/Administrator/Desktop/ProjectPlan1.html"));
		
		AttributeDesignator resourceDesignator = new AttributeDesignator(
				AttributeDesignator.RESOURCE_TARGET, // the <ResourceAttributeDesignator /> tag
				resourceDesignatorType,
				resourceDesignatorId,
				false);
		TargetMatch resourceMatch = new TargetMatch(
				TargetMatch.RESOURCE, // the <ResourceMatch></ResourceMatch> tag
				resourceFunction,
				resourceDesignator,
				resourceValue);
		resource.add(resourceMatch);
		
		

		// -- 1 Action
		Function actionFunction = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:string-equal");
		
		URI actionDesignatorType = new URI("http://www.w3.org/2001/XMLSchema#string");
		URI actionDesignatorId = new URI("urn:oasis:names:tc:xacml:1.0:action:action-id");
		StringAttribute actionValue = new StringAttribute("open");
		
		AttributeDesignator actionDesignator = new AttributeDesignator(
				AttributeDesignator.ACTION_TARGET, // the <ActionAttributeDesignator /> tag
				actionDesignatorType,
				actionDesignatorId,
				false);
		TargetMatch actionMatch = new TargetMatch(
				TargetMatch.ACTION, // the <ActionMatch></ActionMatch> tag
				actionFunction,
				actionDesignator,
				actionValue);
		action.add(actionMatch);
		

		// -- Put the subject and resource sections into their lists
		subjects.add(subject);
		resources.add(resource);
		actions.add(action);
		
		// add 2ème action
		StringAttribute actionValue2 = new StringAttribute("close");
		TargetMatch actionMatch2 = new TargetMatch(
				TargetMatch.ACTION, // the <ActionMatch></ActionMatch> tag
				actionFunction,
				actionDesignator,
				actionValue2);
		List<TargetMatch> action2 = new ArrayList<TargetMatch>();
		action2.add(actionMatch2);
		actions.add(action2);

		// Create and return the new target. No action type
		// attributes have been specified in the target
		return new Target(subjects, resources, actions);
	}
	
	private Apply createRuleCondition() throws URISyntaxException, UnknownIdentifierException, FunctionTypeException {
		List conditionArgs = new ArrayList();
		List<AttributeDesignator> applyArgs = new ArrayList<AttributeDesignator>();

		
		// Define the name and type of the attribute
		// to be used in the condition
		URI designatorType = new URI("http://www.w3.org/2001/XMLSchema#string");
		URI designatorId = new URI("group");
		
		// Pick the function that the condition uses
		FunctionFactory factory = FunctionFactory.getConditionInstance();
		Function conditionFunction = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:string-equal");

		// Choose the function to pick one of the
		// multiple values returned by AttributetDesignator
		factory = FunctionFactory.getGeneralInstance();
		Function applyFunction = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:string-one-and-only");

		// Create the AttributeDesignator
		AttributeDesignator designator = new AttributeDesignator(
						AttributeDesignator.SUBJECT_TARGET,
						designatorType,
						designatorId,
						false);
		applyArgs.add(designator);

		// Create the Apply object and pass it the
		// function and the AttributeDesignator. The function
		// picks up one of the multiple values returned by the
		// AttributeDesignator
		Apply apply = new Apply(applyFunction, applyArgs, false);

		// Add the new apply element to the list of inputs
		// to the condition along with the AttributeValue
		conditionArgs.add(apply);

		StringAttribute value = new StringAttribute("owner");
		conditionArgs.add(value);

		// Finally, create and return the condition
		return new Apply(conditionFunction, conditionArgs, true);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLine cmd = getCommandLine(args);
//		String server = cmd.hasOption('s') ? cmd.getOptionValue('s') : defaultServer;

		PrivacyPolicyManager test = null;
        try {
        	test = new PrivacyPolicyManager();
        	test.start();
        	
        	LOG.info("Create policy");
        	Policy policy = test.createPrivacyPolicy();
        	// Display the policy on the std out
    		policy.encode(System.out, new Indenter());
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
		formatter.printHelp("java -jar testxacml-bin.jar [-s server] [-u username] [-p password] [-r resource] [-d] ", options);
	}
}
