# AMI-STEM 

A universal scientific search engine based on semantic documents and dictionaries.

**NOTE: the commands are being updated to use conventional argument flags (`-` and `--`) so this document will be superseded.**


## Installation

AMI-STEM is provided as 
  
  * a single JAR file (with dependencies). This can be downoaded and run from your commandline. Needs JRE 8 or later. Not yet tested.
  * an `appassembler` tree with `bin` files for both Linux/MACOSX and Windows `*.bat`. The AMI options are runnable from the commandline.
  
### appassembler download

Choose an area in your disk where you want to download the software. I have a `projects` subdirectory / folder and the same for `software`. Try to keep them separate.

The appassembler tree is provided in `https://github.com/petermr/normami/blob/master/ami20180904.zip`. 

Github allows you to download it by 
 * clicking the link 
 * clicking Download 

On MACOSX this puts the file in `Downloads` . Copy/move this to your `software` folder and unzip it. Your directories should look like:
```
ami20180904
├── ami20180904.zip
└── appassembler
    ├── bin
    │   ├── ami-all
    │   ├── ami-all.bat
    │   ├── ami-frequencies
    │   ├── ami-frequencies.bat
    │   ├── ami-gene
    │   ├── ami-gene.bat
    │   ├── ami-identifier
    │   ├── ami-identifier.bat
    │   ├── ami-regex
    │   ├── ami-regex.bat
    │   ├── ami-search
    │   ├── ami-search.bat
    │   ├── ami-sequence
    │   ├── ami-sequence.bat
    │   ├── ami-species
    │   ├── ami-species.bat
    │   ├── ami-word
    │   ├── ami-word.bat
    │   ├── cmine
    │   ├── cmine.bat
    │   ├── contentMine
    │   ├── contentMine.bat
    │   ├── cproject
    │   ├── cproject.bat
    │   ├── makeProject
    │   ├── makeProject.bat
    │   ├── norma
    │   └── norma.bat
    └── repo
        ├── Saxon-HE-9.6.0-3.jar
        ├── asm-1.0.2.jar
        ├── asm-3.3.1.jar
        ├── calibration-0.17.jar
        ├── cephis-0.1-SNAPSHOT.jar
        ├── commons-codec-1.10.jar
        [... and 100 more]
```
(The commands are being constantly updated, so there may be more)

## appassembler

## getting started

### ami-all 
```
ami-all
```
lists all the `ami-*` commands:
```
$ ami-all
contentMine          exist???
cproject             runs "cproject [args]
makeProject          runs - -makeProject (\\1)/fulltext.pdf - -fileFilter .*/(.*)\\.pdf"
norma                norma [args]
                            but also runs makeProject (FIX this)
ami-all              lists AMI commands (ami-*)
ami-dictionaries     edit/create AMI dictionaries
ami-search-cooccur   run AMI searches and co-occurrence
ami-pdf              convert PDF into SVG and extract images
ami-xml              ???
cmine                
ami-frequencies      
ami-gene             
ami-identifier       
ami-regex            
ami-search           
ami-sequence         
ami-species          
ami-word    
```
This list reflects the `pom.xml` file on your machine so is up-to-date for you.

### ami-dictionaries

`ami-dictionaries` displays the help screen and a list of directories on the system.
```
$ ami-dictionaries 
Dictionary processor
    dictionaries are normally added as arguments to search (e.g. ami-search-cooccur project [dictionary [dictionary ...]]

list of dictionaries taken from AMI dictionary list:
    animaltest          auxin               bioactivity         braincognition      brainparts          
    cochrane            compchem            country             crispr              crystal             
    dinogenera          disease             distributions       diterpene           drugs               
    edge.amphibian      edge                edgebirds           elements            epidemic            
    eurofunders         funders             geologicalage       illegaldrugs        indianresearch      
    inn                 insecticide         instrumentManufacturinvasive            magnetism           
    monoterpene         nal                 neurotransmitter    nmrspectroscopy     noncommunicable     
    obesity             organization        p450                pectin              photosynth          
    phytochemicals      plantDevelopment    plantparts          poverty             refugeeUNHCR        
    sesquiterpene       socialmedia         solvents            statistics          synbio              
    taylor_social       terpenesynthase     tf                  transgender         triterpene          
    tropicalVirus       wetlands            wildlife            

also hardcoded functions (which resolve abbreviations):

    gene    (relies on font/style) 
    species (resolves abbreviations) 
```
(The dictionaries are being constantly updated)

for indivdidual dictionaries and their content (first 20 lines) use 
```ami-dictionaries FULL [dictionaryname]
```
```
$ ami-dictionaries full plantparts

Dictionary: plantparts
entries: 50
aerial parts
balsam
bark
berries
branch
calyx
capitula
cone
corolla
dry leaf
female flower
floral bud
flower
fresh leaf
fruit
gall
gum
herb
husk
inflorescence
```

## "make" functionality

`AMI` has an evolving dependency system like `make`, so that if a resource has been created it should not be necessary to rerun the command creating it. Thus for a co-occurrence analysis the prequisites are:

* form a `CProject` from raw files (PDF or other downloaded material) (`makeProject`)
* convert to semantic form (e.g. HTML from PDF) (`ami-pdf`)
* search (`ami-search`)
* co-occurrence

Since the intermediate result are held permanently on disk we only have to run those where files are missing or out-of-date.
Thus:
`ami-search-cooccur` makes `ami-search` which makes `ami-pdf` which makes `makeProject`


## search
```
ami-search-cooccur marchantia plantparts
```
will run the AMI software on the `marchantia` project with the `plantparts` dictionary.

