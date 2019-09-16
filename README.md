# AMI

(The command `norma` is being obsoleted and most commands will be of the form `ami-*`)

**Note.** The commandline syntax is being migrated. See [AMI-STEM](./AMI-STEM.md)) and more recent docs [AMI-DOCS](./ami-docs/AMI.md)

A tool to convert a variety of inputs into normalized, tagged, XHTML (with embedded/linked SVG and PNG where
appropriate). The initial emphasis is on scholarly publications but much of the technology is general.

## Universal Search Tool (Citizen STEM Search; AMI-STEM)

This is a bundle of all `norma` and `ami` functionality to transform PDFs and XML into structured semantic HTML. It's alpha (2018-09) and we have 4-6 testers each with different projects. This runs on a simple commandline ; see AMI-STEM page for more details.

## Installation

For a simple introduction and a description of how to install binaries of the software please see: [here](http://contentmine.github.io)

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

