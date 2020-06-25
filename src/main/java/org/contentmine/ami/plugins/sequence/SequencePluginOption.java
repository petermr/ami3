package org.contentmine.ami.plugins.sequence;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIPluginOption;

public class SequencePluginOption extends AMIPluginOption {

	private static final Logger LOG = LogManager.getLogger(SequencePluginOption.class);
public static final String TAG = "sequence";

	public SequencePluginOption() {
		super(TAG);
	}
	
	public SequencePluginOption(List<String> options) {
		super(TAG, options);
	}
	
//	public SequencePluginOption(List<String> options, List<String> flags) {
//		super(TAG, options, flags);
//	}
	
	public void run() {
		String cmd = "--project "+projectDir+" -i scholarly.html --sq.sequence --sq.type "+optionString;
		new SequenceArgProcessor(cmd).runAndOutput();
	}



}
