# Problems

## bugs
Cephis and ami have bugs - all software does. These include:

 * obsolete code
 * undocumented parameters or actions
 * placeholders
 * unknown unknowns
 * problems in upstream libraries
 
 please be patient and file ISSUEs. I hope to respond with an explanation  
 
 
## documents
Since the corpora we read are highly varied and many documents are poor quality and / or technically broken there are many warnings and errors. 
Please file them a ISSUEs.

## out of memory in PDF conversion

```
cTree: Fauzan03
[1][.1][2][.1][3][.1][4][.1][5][.1][6][.1][7][.1][8][.1][9][.1][10][.1][11][.1][12][.1][13][.1][14][.1][15][.1][16][.1][17][.1][18][.1]
[19][.1][20][.1][21][.1][22][.1][23][.1][24][.1][25][.1][26][.1][27][.1][28][.1][29][.1][30][.1][31][.1][32][.1][33][.1][34][.1][35]
[.1][36][.1][37][.1][38][.1][39][.1][40][.1][41][.1][42][.1][43][.1][44][.1][45][.1][46][.1][47][.1][48]
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at java.awt.image.DataBufferInt.<init>(DataBufferInt.java:75)
  ...
	at org.contentmine.ami.AMIProcessor.convertPDFsToProject(AMIProcessor.java:178)
	at org.contentmine.ami.AMIProcessorPDF.runPDF(AMIProcessorPDF.java:34)
	at org.contentmine.ami.AMIProcessorPDF.main(AMIProcessorPDF.java:27)
```

Sorry - we can't process this document at present - 
