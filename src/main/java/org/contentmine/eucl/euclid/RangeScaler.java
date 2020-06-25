package org.contentmine.eucl.euclid;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class RangeScaler {

	private static final Logger LOG = LogManager.getLogger(RangeScaler.class);
private RealRange inputRange;
	private RealRange outputRange;
	private Double scaleToOutput;
	private Double constantToOutput;
	
	public RangeScaler() {
		
	}
	
	public void setInputRange(RealRange inputRange) {
		this.inputRange = inputRange;
		updateScale();
	}

	public void setOutputRange(RealRange outputRange) {
		this.outputRange = outputRange;
		updateScale();
	}

	private void updateScale() {
		if (inputRange != null && outputRange != null) {
			scaleToOutput = outputRange.getRange() / inputRange.getRange();
			constantToOutput = -1.0 * scaleToOutput * inputRange.getMin() + outputRange.getMin(); 
		}
	}

	public Double transformInputToOutput(double x) {
		updateScale();
		if (scaleToOutput != null) {
			return scaleToOutput * x + constantToOutput;
		} else {
			return null;
		}
	}
	
}
