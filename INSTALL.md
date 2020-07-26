# Installing/running AMI

## Building from source
See [BUILDING.md](BUILDING.md).
The rest of this document relates to running the prebuilt package.

## Downloading
As of July 24, 2020, binary distributions of AMI are hosted on GitHub Packages.
Obtain the latest version here: https://github.com/petermr/ami3/packages/

This is the [Packages](https://github.com/petermr/ami3/packages) link on the project page right-hand side.

![Packages link on project page right-hand side](doc/img/project-packages.png)

The `ami` project currently only publishes one package.

![The ami package](doc/img/packages.png)


Click on the `org.contentmine.ami3` link to see the package details.
Find the `.bz2` or `.zip` distribution archive under the **Assets** heading on the right-hand side of the page. The link will look something like [`ami3-2020.07.25_09.02.10-distribution.zip`]().

![View package details](doc/img/package-details.png)


Download the archive and unzip it somewhere. The result will look something like this:

```
.
└── ami3-2020.07.25_09.02.10
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
        ├── ami3-2020.07.25_09.02.10.jar
        ├── ...
```

## Dependencies
Some `ami` commands invoke external programs.
These must be installed separately (although they may be included if you are running `ami` in a Docker container).

* `tesseract` for character recognition in bitmaps ([tesseract-ocr GitHub project](https://github.com/tesseract-ocr/tesseract), expected to be installed in `/usr/local/bin/tesseract`). Used by `ami-image`, `ami ocr`, `ami forest`.
* `gocr` which is an alternative for character recognition ([GOCR sourceforge project](http://jocr.sourceforge.net/), expected to be installed in `/usr/local/bin/gocr`). Used by `ami ocr`.
* `grobid` to convert PDF streams to HTML ([GROBID docs](https://grobid.readthedocs.io/en/latest/)). Used by `ami grobid`.
* `latexml` and `latexmlpost` to converts TeX input to HTML 5 ([LaTeXML home](https://dlmf.nist.gov/LaTeXML/))
* `curl` and other unix-like utilities. On Windows, [git for windows](https://gitforwindows.org/) includes `curl`. You may also be interested in Windows Subsystem for Linux. Used by `ami download`.

The `getpapers` program is often used together with `ami`: 
* `getpapers` gets metadata, fulltexts or fulltext URLs of papers matching a search query ([ContentMine getpapers GitHub project](https://github.com/ContentMine/getpapers))

## Running AMI

The distribution has two directories:

 * `bin` which contains the launcher scripts for running `ami` commands.
 * `repo` which contains all the required jar library files. 

Good starting points are the `ami` and `amidict` commands.
Try the online help to get an overview of the available options and subcommands:

```bash
ami3-2020.07.25_09.02.10/bin/ami --help
```

This will print the usage help to the console.

Another useful option is `--version` (or `-V`) to display version information:

```bash
ami3-2020.07.25_09.02.10/bin/ami --version
```

## Path
 * edit your `PATH` to include this directory. There are many tutorials on this (e.g. 
   - Windows (https://www.howtogeek.com/118594/how-to-edit-your-system-path-for-easy-command-line-access/)
   - Unix (https://kb.iu.edu/d/acar).
   If you haven't done this before, find a friend who has...
   My path looks like a bit like: 

```
  /Users/pm286/workspace/cmdev/normami/appassembler/bin:/usr/local/n/versions/node/6.2.1/bin:/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin:/usr/bin/node:/opt/X11/bin:/bin:/usr/local/scala/bin:/usr/local/spark/bin
  ```
  Only the first chunk is relevant to `ami`. Note that on Windows you need `;` instead of `:` and the slashes go the other way:

```
C:\Program Files;C:\Winnt;C:\Winnt\System32;C:\Program Files\normami
```


## test/run 
 * If you have edited your path correctly, then type:
```
ami-pdf
```
should run the module `ami-pdf` and give its `help` Alternatively type 
```
which ami-pdf
```
which should reply something like:
```
/some/where/bin/ami-pdf
```
If either of these gives an error or blank you have probably got the path wrong.



