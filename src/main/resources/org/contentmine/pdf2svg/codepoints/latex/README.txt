These were converted from CMAP files at:
http://mirror.ox.ac.uk/sites/ctan.org/macros/latex/contrib/cmap/
and
http://mirror.ox.ac.uk/sites/ctan.org/macros/latex/contrib/mmap/
(Many thanks to Ross Moore)
The *-m.cmap files appear to be syntactic variants using TeX characters/macros. They
provide redundancy checks.

The conversion code is org.xmlcml.pdf2svg.cmap.*

The CMAP files have explicit mappings from font codepoints to Unicode.
Characters without mappings are omitted.

The file unimathsymbols.txt has been slightly edited to add support for (almost) all 
TeX macros in the cmap files

One file (lmr.cmap) contained symbolic characters \big\# and \big\& and these
were converted to the normal characters.

The surrogates are converted to single integers. These give the correct code
in the fileformat.info site but may not display properly in some browsers.

Some codes produce 2 characters (e.g. character followed by negation (20D2)). Therefore
all output is as Strings rather than characters. These Strings should be accessed
with s.codePointAt(i) to manage surrogates properly.

Mappings to font names:
 - CMSY/10  may be to OMS files (oms.cmap.xml)


