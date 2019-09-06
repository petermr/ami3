#! /bin/sh
# run complete stack over SPSS/Stata CTree/CProject

echo ========== RUN AMI STACK ==========


# === DEFAULTS ===

 CPROJECT="none"
 CTREE="none"
 TYPE="none"
 SCOPE="none"

# from "", clean
CLEAN=""

# overwrite with type, project, tree

while getopts ":p:s:t:v:y:hc" opt; do
  case ${opt} in
    c ) CLEAN=$OPTARG;;
    p ) 
        CPROJECT=$OPTARG
        ;;
    s ) 
        SCOPE=$OPTARG;;
    t ) 
       CTREE=$OPTARG
       ;;
    v )
        VERBOSITY=$OPTARG
        ;;
    y )
        TYPE=$OPTARG
        ;;
    h )
      echo "Usage:"
      echo "    -h                      Display this help message."
      echo "    -c                      clean"
      echo "    -p                      project (if scope==project)"
      echo "    -s                      scope (project or tree)"
      echo "    -t                      tree directory (if scope==tree)"
      echo "    -v                      verbosity (v or vv)"
      echo "    -y                      type (stata or spss)"
      exit 0
      ;;
   \? )
     echo "Invalid Option: -$OPTARG" 1>&2
     exit 1
     ;;
    : )
      echo "Invalid option: $OPTARG requires an argument" 1>&2
      ;;  
  esac
done

if [ $TYPE == stata ] || [  $TYPE == spss ] ; then
    echo "type " $TYPE;
else
   echo "bad type (requires stata or spss) " $TYPE;   exit 1
fi 

if [ "project" == ${SCOPE} ] 
then
  if [ x$CPROJECT == "x" ] 
  then
     echo "must give project for scope=project";
     exit 1;
  fi
elif [ "tree" == "tree" ] 
then
  if [ $CTREE == "" ] 
  then
     echo "must give tree for scope=tree";
     exit 1;
  fi
  
  echo CTREE $CTREE;
  
  if [ $CPROJECT != "" ] 
  then
      CTREE=${CPROJECT}/${CTREE} ;
  fi
  
else
   echo "bad scope (requires project or tree)" /${SCOPE}/ ;
   exit 1
fi 

echo project $CPROJECT
echo tree    $CTREE

#exit 0

#constants
EXTLINES_GOCR=" --extractlines gocr"
EXTLINES_HOCR=" --extractlines hocr"
TESSERACT=" --tesseract /usr/local/bin/tesseract"
GOCR=" --gocr /usr/local/bin/gocr"
GOCR_ALPHA2NUM=" o 0 O 0 d 0   e 2   q 4 A 4   s 5 S 5 $ 5   d 6 G 6  J 7   T 7   Z 2   a 4 a 0"
SHARP4=" --sharpen sharpen4 "
THRESHOLD=" --threshold "
TEMPLATE_XML=" --template template.xml"
FORCEMAKE=" --forcemake"
TESS_CMD="${TESSERACT} ${EXTLINES_HOCR} ${FORCEMAKE}"
GOCR_CMD="${GOCR} ${EXTLINES_GOCR} ${FORCEMAKE}"

# executables
AMI_FILTER="ami-filter" 
AMI_FOREST_PLOT="ami-forestplot" 
AMI_IMAGE="ami-image" 
AMI_OCR="ami-ocr"
AMI_PIXEL="ami-pixel"
AMI_PDF="ami-pdf"

if [ $TYPE == "spss" ]
then 
  TEMPLATE_XSL=spssTemplate1
  SUBIMAGE=""
  YPROJECT=0.4
  XPROJECT=0.7
  THRESH=150
  SHARPENED="s4_thr_${THRESH}_ds"
  RAW_LIST="raw.header.tableheads raw.header.graphheads raw.body.table raw.footer.summary raw.footer.scale"
  SHARP_LIST="raw.header.tableheads_${SHARPENED} raw.header.graphheads_${SHARPENED} raw.body.table_${SHARPENED}  raw.footer.summary_${SHARPENED} raw.footer.scale_${SHARPENED}"
  
elif [ $TYPE == "stata" ]
then
  TEMPLATE_XSL=stataTemplate1
  SUBIMAGE="--subimage statascale y LAST delta 10 projection x"
  YPROJECT=0.8
  XPROJECT=0.6
  THRESH=150
  SHARPENED="s4_thr_${THRESH}_ds"
  RAW_LIST="raw.header raw.body.ltable raw.body.rtable raw.scale"
  SHARP_LIST="raw.header_${SHARPENED} raw.body.ltable_${SHARPENED} raw.body.rtable_${SHARPENED} raw.scale_${SHARPENED}"
  
else
  echo "Must set type to 'spss' or 'stata'"
  exit 0
fi

THRESHOLD=" --threshold ${THRESH} "
DS=" --despeckle "
SHARPEN="${SHARP4} ${THRESHOLD} ${DS} "

# choose between tree and project
if [ $SCOPE == "project" ]
then 
  SCOPE=" -p ${CPROJECT} "
elif [ $SCOPE == "tree" ]
then
  SCOPE=" -t ${CTREE} "
fi

echo ============================= INPUT PARAMETERS ================================
echo CPROJECT    ${CPROJECT}
echo CTREE       ${CTREE}
echo SCOPE       ${SCOPE}
echo TYPE        ${TYPE}
echo ================================================================================

# initial and sharpeneed directories
RAW="raw"
SHARPBASE=${RAW}_${SHARPENED}

# START OF EXECUTION
# ==================

# parse PDFs and create images
${AMI_PDF} ${SCOPE}

#remove non-meaningful images
${AMI_FILTER} ${SCOPE}

#sharpen/threshold images 
${AMI_IMAGE} ${SCOPE} --inputname ${RAW} ${SHARPEN}

# sharpen then Tesseract OCR raw to find type of image (experimental)
${AMI_IMAGE} ${VERBOSITY} ${SCOPE} --inputnamelist ${RAW} ${SHARPEN}
${AMI_OCR} ${SCOPE} --inputnamelist ${SHARPBASE} ${TESS_CMD}

${AMI_SEARCH} ${VERBOSITY} ${SCOPE} --inputnamelist ${RAW} --basename 

#segment 
${AMI_PIXEL} ${SCOPE} \
     --projections --yprojection ${YPROJECT} --xprojection ${XPROJECT} --lines \
     --minheight -1 --rings -1 --islands 0 \
     --inputname ${SHARPBASE} ${SUBIMAGE} --templateinput ${SHARPBASE}/projections.xml \
     --templateoutput template.xml \
     --templatexsl /org/contentmine/ami/tools/${TEMPLATE_XSL}.xsl 

# segment the images using TYPE-specific lines
${AMI_FOREST_PLOT} ${SCOPE}  --inputname ${RAW} --segment --template ${SHARPBASE}/template.xml

# sharpen
${AMI_IMAGE} ${SCOPE} --inputnamelist ${RAW_LIST} ${SHARPEN}

# Tesseract OCR
${AMI_OCR} ${SCOPE} --inputnamelist ${SHARP_LIST} ${TESS_CMD}

# GOCR OCR
${AMI_OCR} ${SCOPE} --inputnamelist ${SHARP_LIST} ${GOCR_CMD}

# Forest plot - make tables from Tesseract OCR (not fully implemented)
${AMI_FOREST_PLOT} ${SCOPE} --inputnamelist ${SHARP_LIST} --table hocr/hocr.svg --tableType hocr

# Forest plot - make tables from GOCR OCR (not fully implemented)
${AMI_FOREST_PLOT} ${SCOPE} --inputnamelist ${SHARP_LIST} --table gocr/gocr.svg --tableType gocr

