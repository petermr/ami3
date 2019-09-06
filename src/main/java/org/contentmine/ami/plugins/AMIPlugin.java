package org.contentmine.ami.plugins;

import java.util.Arrays;
import java.util.HashMap;

import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.norma.NAConstants;

public class AMIPlugin {

	
	private static final Logger LOG = Logger.getLogger(AMIPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
//	public final static String VERSION = "2.0.1"; // arbitrary
	public static final String ORG_XMLCML_AMI_PLUGIN = NAConstants.AMI_RESOURCE+ "/plugins/";
	public static final String ORG_XMLCML_AMI_CLASSNAME = "org.contentmine.ami.plugins";

	static Map<String, String> argProcessorNameByName = null;
	static {
		argProcessorNameByName = new HashMap<String, String>();
		argProcessorNameByName.put("gene", ORG_XMLCML_AMI_CLASSNAME+".gene.GeneArgProcessor");
		argProcessorNameByName.put("identifier", ORG_XMLCML_AMI_CLASSNAME+".identifier.IdentifierArgProcessor");
		argProcessorNameByName.put("regex", ORG_XMLCML_AMI_CLASSNAME+".regex.RegexArgProcessor");
		argProcessorNameByName.put("phylo", ORG_XMLCML_AMI_CLASSNAME+".phylo.PhyloTreeArgProcessor");
		argProcessorNameByName.put("sequence", ORG_XMLCML_AMI_CLASSNAME+".sequence.SequenceArgProcessor");
		argProcessorNameByName.put("species", ORG_XMLCML_AMI_CLASSNAME+".species.SpeciesArgProcessor");
		argProcessorNameByName.put("word", ORG_XMLCML_AMI_CLASSNAME+".word.WordArgProcessor");
	}

	public AMIPlugin() {
		new AMIArgProcessor().printVersion();
		// default - should be overridden
		this.argProcessor = null;
	}

//	private void writeVersion() {
//		new AMIArgProcessor().printVersion();
//	}

	protected DefaultArgProcessor argProcessor;

	public static void main(String[] args) {
		new AMIPlugin().run(args);
	}

	public void run(String[] args) {
		if (args.length > 0) {
			String plugin = args[0];
			String argProcessorName = argProcessorNameByName.get(plugin);
			if (argProcessorName == null) {
				throw new RuntimeException("Cannot find class for plugin: "+plugin);
			}
			LOG.trace("argProcessor: "+argProcessorName);
			Class<?> argProcessorClass = null;
			try {
				argProcessorClass = Class.forName(argProcessorName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("*******========******* "+"Cannot create class: "+argProcessorName, e);
			}
			DefaultArgProcessor  argProcessor = null;
			try {
				argProcessor = (DefaultArgProcessor) argProcessorClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Cannot instantiate class: "+argProcessorName, e);
			}
			LOG.trace(argProcessor);
			String[] args1 = preTruncateArgs(args);
			argProcessor.parseArgs(args1);
			argProcessor.runAndOutput();
		} else {
			LOG.error("Must give plugin to run: choose from: "+argProcessorNameByName.keySet());
		}
	}

	private String[] preTruncateArgs(String[] args) {
		int length1 = args.length - 1;
		String[] args1 = new String[length1];
		for (int i = 0; i < length1; i++) {
			args1[i] = args[i + 1];
		}
		LOG.trace("args1: "+Arrays.asList(args1));
		return args1;
	}

	public DefaultArgProcessor getArgProcessor() {
		return argProcessor;
	}

	/** delegate to argProcessor.runAndOutput().
	 * 
	 */
	public void runAndOutput() {
		DefaultArgProcessor argProcessor = (DefaultArgProcessor) this.getArgProcessor();
		argProcessor.runAndOutput();
	}
}
