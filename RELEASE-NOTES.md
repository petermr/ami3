# ami v2021.09.27_15.49.34
## Summary


- [#67] Fixed NullPointer in EPMCResult.json dues to getInteger() assuming field exists


# ami v2020.08.09_09.54.10
## Summary

AMISummary 

## Changes in this Release
- ami summary uses globs to extract leaf nodes into List<File>. 
- leafNode lists can be aggregated as a subtree (directories are preserved) or flattened into single directory for interfacing to machine-earning tools
- at alpha level.


# ami v2020.08.07_07.13.18
## Summary

AMI summary: Aggregating sections over corpus

## Changes in this Release
- ami `summary` accepts --glob to identify (sub)sections
- ami `summary` creates a new toplevel directory `_summary` with aggregated subtrees. The
directory structure tries to mirror the original structures. See AMISummaryTest for
example.
 


# ami v2020.08.06_07.01.42
## Summary

Enhancement to Dictionaries 

## Changes in this Release
- amidict can submit SPARQL queries to Wikidata
- amidict translates wikidataAltLabel to synonyms
- DSL for transforming dictionaries (EXTRACT, DELETE, etc.)


# ami v2020.08.03_12.52.50
## Summary

Fixed bug in `ami section --extract

## Changes in this Release
- changed `table` to `tab` in `org.contentmine.ami.tools.AMISectionTool.FloatType`
- continued to add tests and checking results
- [#58] Ensure ANSI colors when running `ami` in Windows Command Prompt.


# ami v2020.07.31_13.04.35
## Summary

Added WikidataSparql queries to `amidict`

## Changes in this Release
- Added WikidataSparql queries to `amidict`
- slowly linking `picocli` help to Wiki


# ami v2020.07.31_12.59.11
## Summary

Addition of `amidict create` from Wikidata SPARQL

## Changes in this Release
- added submission of Wikidata queries the retrieve dictionary items and their labels
- added `AbstractAMITest.writeOutputAndCompare(` XML comparison test (mainly regression test)


# ami v2020.07.28_20.33.15
## Summary

This is a template and should be replaced by actual release notes...

## Changes in this Release
- [#57] Add RELEASE-NOTES.md in the distribution
- Second Change


# ami v2020.07.27_01.47.40
## Summary

The workflow has been improved to automatically create a GitHub Release with Release Notes when the `release.bash` script is run.

No user-oriented changes in this release.

## Changes in this Release
* [#55] Publish GitHub Release from `release.bash` script
* DOC update BUILDING.md documentation and associated image files
* CLEAN remove outdated GitHub Action workflow files; remove outdated comments from workflow configs


# ami v2020.07.27_00.54.23
## Summary

The workflow has been improved to automatically create a GitHub Release with Release Notes when the `release.bash` script is run.

No user-oriented changes in this release.

## Changes in this Release
* [#55] Publish GitHub Release from `release.bash` script
* DOC update BUILDING.md documentation and associated image files
* CLEAN remove outdated GitHub Action workflow files; remove outdated comments from workflow configs


# v2020.07.26_11.27.57

## Summary

This release is another test release for the combined Create Release and Publish Packages action.

# v2020.07.26_11.19.44

## Summary

This release is another test release to verify that the Publish package to GitHub Packages GitHub Action can be triggered by the Create Release GitHub Action.


# v2020.07.26_11.04.35

## Summary

This release is a test release to verify the GitHub Action that automatically creates a GitHub Release when a tag is pushed whose tag name starts with 'v'.
