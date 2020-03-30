# AMI

AMI is an **operating system (toolkit)** to manage (scholarly) documents; download, aggregate, transform, search, filter, index, annotate, re-use and republish.  It caters for a wide range of (awful) inputs, creates de facto semantics, an ontology (based on Wikidata). It is the basis for high-level science/tech applications including chemistry (molecules, spectra, reaction), Forest plots (metaanalyses of trials), phylogenetic trees (useful fo virus mutations), geographic maps, and basic plots (x/y, scatter, etc.). At present it's written in Java , but it is fundamentally a definition of a declarative system, with commands, and defined data modules. 

AMI turns documents into knowledge. Or at least the input for knowledge.

### Note 
AMI has a 20-year history and has been modular in the past (`norma` , `cephis`, `pdf2svg` ...) but is now monolithic for ease of distribution and because the precise modularity is unclear. It might be broken up again into separate tools. 

## Installation

For a tutorial and a wide range of features see https://github.com/petermr/tigr2ess, which shows AMI being used on crop plants. 

UPDATE 20190122)
For simply running `AMI` (not building) use the repository [ami-jars](http://github.com/petermr/ami-jars). This repo will be updated frequently (at least till end 2019-02). If git is installed, a "git clone https://github.com/petermr/ami-jars.git" checks out the project.

There are two approaches:

### running jars on commandline

`ami-jars` provides an `uber-jar` ('jar-with-dependencies`) which can be run from the commandline:

```
java -jar <jar> -cp <classpath> <mainClass>
```

For this you need to know which main classes map onto the commands. 

### running `repo` and `bin` scripts

The distrib has two directories:

 * `repo` which contains all the required jar files. 
 * `bin` which contains all the scripts. Set your classpath to include this directory.

If you have the corect classpath, then exceuting `ami-pdf` on the commandline will run  the `ami-pdf` module. (Later we hope to make these submodules on the commandline.

## Building from source

Norma can be built with maven3 and requires Java 1.8 or greater. If you are building for the first time, or if your mods are minor you can skip the integration tests. The normal tests take a minute or two. To avoid all tests (which takes 20 secs or so):

```bash
mvn install -Dmaven.test.skip=true
```

will not run any tests.

## Building Docker image

You can also build a Docker image based:

```bash
docker build --tag ami3 .
```

Then you can run the container and execute commands in the container:

```bash
$ docker run --rm -it ami3
root@aa87395224cc:/workspace# ami-pdf --help
Usage: ami-pdf [OPTIONS]
Description
===========
Convert PDFs to SVG-Text, SVG-graphics and Images. Does not process images,
[...]
```

You can also directly run commands and exit the container rightaway and mount directories with data:

```bash
$ docker run --rm -it -v $(pwd):/workspace ami3 ami-image example.jpg
Usage: ami-image [OPTIONS]
Description
===========
        transforms image contents but only provides basic filtering (see ami-filter).
Services include

 identification of d
 [...]
```

## Contributing to development

If you're interested in contributing please take a look at: [CONTRIBUTING.md](CONTRIBUTING.md)

## *Everything after this is likely to be obsolete* 

## Input

Norma will convert legacy files into scholarly html. It converts files that are in a CProject structure. This enables it
to process multiple papers in a single run without overwriting files. It also keeps all the data from each paper together
in its own CTree. This includes metadata about the paper, images that may have been extracted from the paper and
supplementary files such as tables.

To convert a CTree full of NLM xml files such as those you might have downloaded from EuropePMC with getpapers you can run:
  ```
  norma --project <CProject folder> --input fulltext.xml --output scholarly.html --transform nlm2html
  ```

