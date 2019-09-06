# Installing/running AMI

## building from code
see [building](BUILDING.md). The rest of this document relates to running the prebuilt downloaded jars ("ami-jars").

## downloading
AMI has two main approaches for downloading/running (`jar-with-dependencies` and `repo`/`bin`. Both are contained in the [AMI JAR repository](http://github.com/petermr/ami-jars) as `ami<date>` directories (e.g. `ami20190115/`). We shall describe the `repo`/`bin` approach.

## git
 * You require `git` installed on your machine and should know how to `clone` a repository. 
 * Decide where you want to put `ami` (e.g. `$HOME/Software/ami` (where `$HOME` is your home directory) or in your unix libraries e.g. `/usr/local/bin`).
 * clone the repo into this area. GitHub has a button ("Clone or download") which exposes the URL:
 ```
 https://github.com/petermr/ami-jars.git
 ```
Go to your AMI directory and type:
```
git clone https://github.com/petermr/ami-jars.git
```
This should download about 100 files, with the structure:
```
.
├── LICENSE
├── README.md
├── ami20190109
│   ├── ami20190109-jar-with-dependencies.jar
│   ├── bin
│   │   ├── ami-all
│   │   ├── ami-all.bat
... snipped for clarity
│   │   ├── makeProject
│   │   ├── makeProject.bat
│   │   ├── norma
│   │   └── norma.bat
│   └── repo
│       ├── Saxon-HE-9.6.0-3.jar
│       ├── activation-1.1.1.jar
... snipped
│       ├── xz-1.4.jar
│       └── zip4j-1.3.2.jar
```
The actual names (based on dates) will changed as we add more releases.

## path
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

### test/run 
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
If either of these gives an error or blank you have probaably got the path wrong.



