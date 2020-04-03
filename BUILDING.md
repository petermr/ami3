# building

`normami` requires Maven to build.
It relies on a parent POM which should be used to edit resources uniformly (e.g. Java version, maven plugins, etc.).
It has `cephis` as a dependency - if `cephis` is edited it must be recompiled and installed and then `normami` must be re-installed

## POM file
The POM file depends on a parent. 
```
    <parent>
        <groupId>org.contentmine</groupId>
        <artifactId>cm-parent</artifactId>
        <version>7.1.0</version>
    </parent>
```

The instructions in the POM include:

### jar-with-dependencies
The POM can generate a single Java JAR which contains all the upstream libraries so it is the only thing that has to be downloaded. There are deliberately many entry points. To run it:
```
java -jar <version>-jar-with-dependencies -cp <jar-location> <mainClass>
```
There are many main class entry points - the pom will show those mapped onto commands.

### appassembler
This generates platform-independent scripts (UNIX, MACOSX, Windows) which run the different functions. The scripts are initially located in
```
some/where/normami/target/appassembler/bin
```
and you probably want to copy them to your library directories.

Current entry points - these are being explanded
```
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
(This needs updating and will be).

## versioning
Currently all versions are `SNAPSHOTS` and treated a such by Maven. 

# commands

## full build

Make sure `cephis` is sync'ed with the git repo, rebuilt and reinstalled.

This will clean, build, test and install `normami` in your personal `.m2` repository.
```
mvn clean install
```
This takes 1-2 inutes as no test is greater than 5 secs.
It does not run the Integration tests. For that we have

```
mvn clean install -DskipITs=false
```
if you wish to skip the tests run:
```
mvn install -Dmaven.test.skip=true
```



