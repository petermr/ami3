[![GitHub Action Build Status](https://github.com/petermr/ami3/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)](https://github.com/petermr/ami3/actions) 


# AMI

AMI is a **toolkit** to manage (scholarly) documents; download, aggregate, transform, search, filter, index, annotate, re-use and republish.  It caters for a wide range of (awful) inputs, creates de facto semantics, an ontology (based on Wikidata). It is the basis for high-level science/tech applications including chemistry (molecules, spectra, reaction), Forest plots (metaanalyses of trials), phylogenetic trees (useful fo virus mutations), geographic maps, and basic plots (x/y, scatter, etc.). AMI is written in Java, and is designed to be a declarative system, with commands and data modules. 

AMI turns documents into knowledge. Or at least the input for knowledge.

## Installation

As of July 24, 2020, binary distributions of AMI are hosted on GitHub Packages.
Obtain the latest version here: https://github.com/petermr/ami3/packages/

* Select the latest version, click on the associated `org.contentmine.ami3` link
* Find the `.bz2` or `.zip` distribution archive under the **Assets** heading on the right-hand side of the page. The link will look something like [`ami3-2020.07.24_07.23.42-distribution.zip`]().
* Download the archive and unzip it somewhere. The result will look something like this:

```
.
└── ami3-2020.07.24_07.23.42
    ├── LICENSE
    ├── README.md
    ├── bin
    │   ├── ami
    │   ├── ami-all
    │   ├── ami-all.bat
    │   ├── ...
    │   ├── ami.bat
    │   ├── amidict
    │   ├── amidict.bat
    │   ├── pman
    │   └── pman.bat
    └── repo
        ├── ...
        ├── ami3-2020.07.24_07.23.42.jar
        ├── ...
```

## Running AMI

The distribution has two directories:

 * `bin` which contains the launcher scripts for running `ami` commands.
 * `repo` which contains all the required jar library files. 

The `bin` folder has many launcher scripts.
One goal of ongoing development is to reduce this to a smaller number and make the functionality available as subcommands for the `ami` top-level command.

Good starting points are the `ami` and `amidict` commands.
Try the online help to get an overview of the available options and subcommands:

```bash
cd ami3-2020.07.24_07.23.42/bin/ami --help
```

This will print the usage help to the console.
As of this writing, that looks something like this:

```bash
ami3-2020.07.24_07.23.42/bin/ami --help
Usage: ami [OPTIONS] COMMAND

`ami` is a command suite for managing (scholarly) documents: download, aggregate, transform, search, filter, index,
annotate, re-use and republish.
It caters for a wide range of inputs (including some awful ones), and creates de facto semantics and an ontology (based
on Wikidata).
`ami` is the basis for high-level science/tech applications including chemistry (molecules, spectra, reaction), Forest
plots (metaanalyses of trials), phylogenetic trees (useful for virus mutations), geographic maps, and basic plots (x/y,
scatter, etc.).

Parameters:
===========
      [@<filename>...]       One or more argument files containing options.
Options:
========
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.
CProject Options:
  -p, --cproject=DIR         The CProject (directory) to process. This can be (a) a child directory of cwd (current
                               working directory (b) cwd itself (use -p .) or (c) an absolute filename. No defaults.
                               The cProject name is the basename of the file.
  -r, --includetree=DIR...   Include only the CTrees in the list. (only works with --cproject). Currently must be
                               explicit but we'll add globbing later.
  -R, --excludetree=DIR...   Exclude the CTrees in the list. (only works with --cproject). Currently must be explicit
                               but we'll add globbing later.
CTree Options:
  -t, --ctree=DIR            The CTree (directory) to process. This can be (a) a child directory of cwd (current
                               working directory, usually cProject) (b) cwd itself, usually cTree (use -t .) or (c) an
                               absolute filename. No defaults. The cTree name is the basename of the file.
  -b, --includebase=PATH...  Include child files of cTree (only works with --ctree). Currently must be explicit or with
                               trailing percent for truncated glob.
  -B, --excludebase=PATH...  Exclude child files of cTree (only works with --ctree). Currently must be explicit or with
                               trailing percent for truncated glob.
General Options:
  -i, --input=FILE           Input filename (no defaults)
  -n, --inputname=PATH       User's basename for inputfiles (e.g. foo/bar/<basename>.png) or directories. By default
                               this is often computed by AMI. However some files will have variable names (e.g. output
                               of AMIImage) or from foreign sources or applications
  -L, --inputnamelist=PATH...
                             List of inputnames; will iterate over them, essentially compressing multiple commands into
                               one. Experimental.
  -f, --forcemake            Force 'make' regardless of file existence and dates.
  -N, --maxTrees=COUNT       Quit after given number of trees; null means infinite.
  -o, --output=output        Output filename (no defaults)
Logging Options:
  -v, --verbose              Specify multiple -v options to increase verbosity. For example, `-v -v -v` or `-vvv`. We
                               map ERROR or WARN -> 0 (i.e. always print), INFO -> 1 (-v), DEBUG -> 2 (-vv)
      --log4j=CLASS=LEVEL[,CLASS=LEVEL...]
                             Customize logging configuration. Format: <classname>=<level>; sets logging level of class;
                               e.g. org.contentmine.ami.lookups.WikipediaDictionary=INFO
                             This option may be specified multiple times and accepts multiple values.
Commands:
=========
  assert               Makes assertions about objects created by AMI.
  clean                Cleans specific files or directories in project.
  display              Displays files in CTree.
  download             Downloads content from remote site.
  dummy                Minimal AMI Tool for editing into more powerful classes.
  figure               creates Figures from primitives (e.g. adds XML captions to figures).experimental.
  filter               FILTERs images (initally from PDFimages), but does not transform the contents.
  forest               Analyzes ForestPlot images.
  graphics             Transforms graphics contents (often from PDF/SVG).
  grobid               Runs grobid.
  image                Transforms image contents but only provides basic filtering (see ami-filter).
  lucene               Runs Lucene (words and search) Experimental
  makeproject          Processes a directory (CProject) containing files (e.g.*.pdf, *.html, *.xml) to be made into
                         CTrees.
  metadata             Manages metadata for both CProject and CTrees.
  ocr                  Extracts text from OCR and (NYI) postprocesses HOCR output to create HTML.
  pdfbox               Convert PDFs to SVG-Text, SVG-graphics and Images.
  pixel                Analyzes bitmaps - both binary (black/white), but may be oligochrome.
  regex                Searches with regex.
  search               Searches text (and maybe SVG).
  section              Splits XML files into sections using XPath.
  summary              Summarizes the specified dictionaries, genes, species and words.
  svg                  Takes raw SVG from PDF2SVG and converts into structured HTML and higher graphics primitives.
  table                Writes cProject or cTree to summary table.
  transform            Runs XSLT transformation on XML (NYFI).
  words                Analyzes word frequencies.
  help                 Displays help information about the specified command
  generate-completion  Generate bash/zsh completion script for ami.
```

## Tutorial

For a tutorial and a wide range of features see https://github.com/petermr/tigr2ess, which shows AMI being used on crop plants. 
The documentation that resulted from Peter's Tigr3ess workshop in Delhi may be useful. For example:

* [Quick overview](https://github.com/petermr/tigr2ess/blob/master/search/ami.md)
* [AMI installation](https://github.com/petermr/tigr2ess/tree/master/installation) - for Windows, Unix, MacOS. Detailed steps with screenshots.
* [AMI getpapers tutorial](https://github.com/petermr/tigr2ess/blob/master/getpapers/TUTORIAL.md)
* [AMI search tutorial](https://github.com/petermr/tigr2ess/blob/master/search/TUTORIAL.md)
* [AMI dictionaries tutorial](https://github.com/petermr/tigr2ess/blob/master/dictionaries/TUTORIAL.md)
* [AMI clean](https://github.com/petermr/tigr2ess/blob/master/installation/WORKING.md) to clean an AMI corpus and start again, without deleting the files you downloaded

Also, see the wiki of the ami3 project: https://github.com/petermr/ami3/wiki

Finally, the wiki of the [openVirus project](https://github.com/petermr/openvirus/) is a good source of information: https://github.com/petermr/openvirus/wiki



## Building from source

If you're interested in building the binaries from source please take a look at: [BUILDING.md](BUILDING.md).


## Contributing to development

If you're interested in contributing please take a look at: [CONTRIBUTING.md](CONTRIBUTING.md).


## Note 
AMI has a 20-year history and has been modular in the past (`norma` , `cephis`, `pdf2svg` ...) but is now monolithic for ease of distribution and because the precise modularity is unclear. It might be broken up again into separate tools. 
