# Creating Results

## WordResultElement

### modules

per-word result

WordResultElement(ResultElement) // copy
WordResultElement(title) 
getCount()
getWord()
getLength()
// attributes
setWord(wordvalue)
setCount(frequency)
setLength(length of word)

### creation stack

$
WordResultElement.<init>(String) line: 26	
WordCollectionFactory.getWordLengths(Multiset<Integer>) line: 216	
WordCollectionFactory.createWordLengthsResultsElement() line: 210	
WordCollectionFactory.extractWords() line: 75	
WordArgProcessor.runExtractWords(ArgumentOption) line: 187	

## WordResultsElement

### creation stack

WordResultsElement.<init>(String) line: 30	
WordCollectionFactory.getWordLengths(Multiset<Integer>) line: 214	
WordCollectionFactory.createWordLengthsResultsElement() line: 210	
WordCollectionFactory.extractWords() line: 75	
WordArgProcessor.runExtractWords(ArgumentOption) line: 187	

see:
	private WordResultsElement getWordLengths(Multiset<Integer> lengthSet) {
		WordResultsElement lengthsElement = new WordResultsElement(LENGTHS);
		for (Entry<Integer> entry : lengthSet.entrySet()) {
			WordResultElement lengthElement = new WordResultElement(LENGTH);
			lengthElement.setLength(entry.getElement().intValue());
			lengthElement.setCount(entry.getCount());
			lengthsElement.appendChild(lengthElement);
		}
		return lengthsElement;
	}
