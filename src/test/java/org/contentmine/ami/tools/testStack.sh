#! /bin/sh

# your path should include the /bin directory of the appassembler distrib, e.g.
# ami-forestplot => /Users/pm286/workspace/cmdev/normami/target/appassembler/bin/ami-forestplot

# edit this to your own directory
# STATA="/Users/pm286/projects/forestplots/stataforestplots"
# STATA="/Users/pm286/projects/forestplots/_stataok"
WORKSPACE=$HOME/workspace/
FOREST_TOP=$WORKSPACE/projects/forestplots
MID_DIR=test20190804
FOREST_MID=$FOREST_TOP/$MID_DIR
LOW_DIR=_stataok
FOREST_DIR=$FOREST_MID/$LOW_DIR

CPROJECT=$FOREST_DIR
CTREE_NAME=PMC6127950
#CTREE_NAME=PMC5882397
CTREE=$CPROJECT/$CTREE_NAME

echo CTREE $CTREE

while getopts p:t: option
do
case "${option}"
in
p) CPROJECT=${OPTARG};;
t) CTREE=${OPTARG};;
esac
done


# choose the first SOURCE to run a single CTree, the second to run a CProject (long). 
# Comment in the one you want
SOURCE=" -t $CTREE"
# SOURCE=" -p $CPROJECT"
echo $CTREE
ls $CTREE

# images 
RAW=raw
RAW230DS=raw_thr_230_ds
RAWS4230DS=raw_s4_thr_230_ds
#subimages

# regions of image
HEADER=header
BODY=body
LTABLE=ltable
RTABLE=rtable
SCALE=scale

HEADERS120D=${HEADER}"_s4_thr_120_ds"
LTABLES120D=${LTABLE}"_s4_thr_120_ds"
RTABLES120D=${RTABLE}"_s4_thr_120_ds"

SLEEP1=1
SLEEP5=5

# make project from a directory (CPROJECT) containing PDFs. 
# a no-op here as EuPMC has already done this

ami-makeproject -p $CPROJECT --rawfiletypes pdf

# convert PDFs to CTrees

ami-pdf $SOURCE

# image processing at 3 threshold levels (later will try to make this an AMI loop)

ami-image $SOURCE --sharpen sharpen4 --threshold 150 --despeckle true
ami-image $SOURCE --sharpen sharpen4 --threshold 230 --despeckle true
ami-image $SOURCE --sharpen sharpen4 --threshold 240 --despeckle true

echo "===============Finished AmiImage============="
sleep $SLEEP1

# run OCR both types

ami-ocr $SOURCE --gocr      /usr/local/bin/gocr      --extractlines gocr               --forcemake
ami-ocr $SOURCE --tesseract /usr/local/bin/tesseract --extractlines hocr --html false  --forcemake

echo "===============Finished AmiOcr============="
sleep $SLEEP1

# extract the pixels and project onto axes to get subimage regions
# further project the scale subimage (y(2)) to get the tick values 
# in this case do it for the threshold 230 version only
# the spreadsheet location (xsl) is hard coded into the distrib but it could be 
# more general.
# This *generates* raw_thr_230_ds/template.xml . its variables (e.f. $RAW.$HEADER) are specified 
# in the stylesheet and values computed from applying ami-pixel to the images

ami-pixel $SOURCE --projections --yprojection 0.8 --xprojection 0.5 \
                --minheight -1 --rings -1 --islands 0 \
			    --inputname $RAW230DS \
			    --subimage statascale y 2 delta 10 projection x \
			    --templateinput $RAW230DS/projections.xml \
			    --templateoutput template.xml \
			    --templatexsl /org/contentmine/ami/tools/stataTemplate.xsl

echo "===============Finished AmiPixel============="
sleep $SLEEP5

# use the generated template.xml in each CTree/*/image*/raw_thr_230_ds/ directory to segment the image
# this will create subimages $RAW.$HEADER, $RAW.$BODY.$LTABLE, raw.body.graph, $RAW.$BODY.$RTABLE and raw.scale
# these subimages will be written to *.png in the CTree/*/image* directory
			    
ami-forestplot $SOURCE --template $RAW230DS/template.xml

echo "===============Finished AmiForest============="
sleep $SLEEP5

#now re-run ami-image to enhance each subimage separately

ami-image $SOURCE --inputname $RAW.$HEADER --sharpen sharpen4 --threshold 120 --despeckle true
ami-image $SOURCE --inputname $RAW.$BODY.$LTABLE --sharpen sharpen4 --threshold 120 --despeckle true
ami-image $SOURCE --inputname $RAW.$BODY.$RTABLE --sharpen sharpen4 --threshold 120 --despeckle true

echo "===============Finished Sharpen Threshold============="
sleep $SLEEP5

# and rerun tesseract on each subimage (suspect Tesseract gets confused by the whole
# image including the graph and lines.

ami-ocr $SOURCE --inputname $RAW.$HEADERS120D      --tesseract /usr/local/bin/tesseract --extractlines hocr
ami-ocr $SOURCE --inputname $RAW.$BODY.$LTABLES120D --tesseract /usr/local/bin/tesseract --extractlines hocr
ami-ocr $SOURCE --inputname $RAW.$BODY.$RTABLES120D --tesseract /usr/local/bin/tesseract --extractlines hocr

echo "===============Finished Tesseract ============="
sleep $SLEEP5

ami-ocr $SOURCE --inputname $RAW.$HEADERS120D      --gocr /usr/local/bin/gocr --extractlines gocr
ami-ocr $SOURCE --inputname $RAW.$BODY.$LTABLES120D --gocr /usr/local/bin/gocr --extractlines gocr
ami-ocr $SOURCE --inputname $RAW.$BODY.$RTABLES120D --gocr /usr/local/bin/gocr --extractlines gocr

echo "===============Finished GOCR ============="
sleep $SLEEP5


