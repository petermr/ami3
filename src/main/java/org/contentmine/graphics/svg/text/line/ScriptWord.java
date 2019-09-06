package org.contentmine.graphics.svg.text.line;

import java.util.ArrayList;
import java.util.List;

import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.text.structure.TextAnalyzer;
import org.contentmine.graphics.svg.text.structure.TextStructurer;

/** 
 * A word in a ScriptLine
 * 
 * moved from svg2xml
 * 
 * @author pm286
 */
public class ScriptWord extends ScriptLine {
	
	private List<String> characterList;

	public ScriptWord(int nLines) {
		super(new TextStructurer((TextAnalyzer) null));
		textLineList = new ArrayList<TextLine>();
		for (int i = 0; i < nLines; i++) {
			textLineList.add(new TextLine());
		}
		textStructurer.setTextLines(textLineList);
		textStructurer.setTextCharacters(new ArrayList<SVGText>());
	}
	
	public void add(SVGText character, int line) {
		ensureCharacterList();
		if (line >= 0 && line < textLineList.size()) {
			textLineList.get(line).add(character);
		}
		characterList.add(character.getText());
		textStructurer.getTextList().add(character);
	}
	
	private void ensureCharacterList() {
		if (characterList == null) {
			characterList = new ArrayList<String>();
		}
	}

	@Override
	public String summaryString() {
		StringBuilder sb = new StringBuilder();
		ensureCharacterList();
		for (String s : characterList) {
			sb.append(s);
		}
		return sb.toString();
	}
}
