package org.contentmine.norma.image.ocr;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.Real2;

public class GridExtractor {

	public enum Coord {
		X,
		Y,
	};
	
	private static final Logger LOG = LogManager.getLogger(GridExtractor.class);
private Real2 deltaXY;
	
	public GridExtractor(Real2 deltaXY) {
		this.deltaXY = deltaXY;
	}

	public void deduceGrid(List<HOCRLabel> potentialLabels) {
		List<LabelRow> xLabelRowList = findWithSame(potentialLabels, Coord.X);
		for (LabelRow row : xLabelRowList) {
			LOG.trace(">X> "+row);
		}
		List<LabelRow> yLabelRowList = findWithSame(potentialLabels, Coord.Y);
		for (LabelRow row : yLabelRowList) {
			LOG.trace(">Y> "+row);
		}
	}

	private List<LabelRow> findWithSame(List<HOCRLabel> potentialLabels, Coord XY) {
		List<LabelRow> labelRowList = new ArrayList<LabelRow>();
		for (int i = 0; i < potentialLabels.size() - 1; i++) {
			HOCRLabel label0 = potentialLabels.get(i);
			for (int j = i + 1; j  < potentialLabels.size(); j++) {
				HOCRLabel label1 = potentialLabels.get(j);
				if (label0.hasSame(label1, deltaXY, XY)) {
//					LOG.debug(label0+"; "+label1);
					boolean added = false;
					for (int k = 0; k < labelRowList.size(); k++) {
						LabelRow labelRow = labelRowList.get(k);
						if (labelRow.contains(label0)) {
							labelRow.add(label1);
							added = true;
							break;
						} else if (labelRow.contains(label1)) {
							labelRow.add(label0);
							added = true;
							break;
						}
					}
					if (!added) {
						LabelRow labelRow = new LabelRow();
						labelRow.add(label0);
						labelRow.add(label1);
						labelRowList.add(labelRow);
					}
				}
			}
		}
//		for (LabelRow row : labelRowList) {
//			LOG.debug(">> "+row);
//		}
		return labelRowList;
	}

}
