LaTeX package:  mmap.sty -- CMAP support for math in PDF

Copyright (c) 2008  Ross Moore <ross@maths.mq.edu.au>

 mmap package -- include CMap resources for math symbols into PDF
   to make "search" and "copy-n-paste" functions work properly

  -m.cmap  CMap uses ascii strings for the macro-names
  .cmap    CMap uses the Unicode code-point of the symbols

You may distribute and/or modify this program under the terms of LPPL
the program consists of mmap.sty and .cmap files:

  {t1,lmr,ly1,oml,oms,omx,ot1,ulasy,umsa,umsb,ueuf,upsy,upzd}.cmap
  {oml{b,m}it,omsb,ot1{rbxit,rbxn,rmit,rmn,ssbxn,ssmn,ttmn}}.cmap

  {t1,oml,oms,omx,ot1,ueuf,ueufb,ulasy,umsa,umsb,upsy,upzd}-m.cmap
  {oml{b,m}it,omsb,ot1{rbxit,rbxn,rmit,rmn,ssbxn,ssmn,ttmn}}-m.cmap

Usage:
  put \usepackage{mmap}  to load CMAP resources from -m.cmap files.
  Or put \usepackage[noTeX]{mmap}  to load CMAP resources from .cmap files.
  Or put \usepackage[useTeX]{mmap} to load CMAP resources from -m.cmap files.

IMPORTANT:  place this immediately after the \documentclass line
     to ensure that all fonts are checked for appropriate resources.

TODO:
   add *.cmap files for other font encodings (contributions are welcome):
     TS1, OT2, IL2, ...
   support dvips?
_________________________________________________________________________

Now there's another way to convert from LaTeX to Word.
Simply copy/paste from the PDF, then adjust super-/sub-scripts,
fractions, etc.
_________________________________________________________________________

Cheers,

	Ross

