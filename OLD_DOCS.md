# cephis

Cephis has the following sorts of examples:

* extended Unit tests ("Integration Tests")
* projects decribed on ContentMine's "discuss" platform

## Integration Tests
Many `cephis` tests take a small problem and produce output and are referred to as *Integration Tests* (ITs). ITs have a special role in Maven and can be run or omitted as required. Typically many ITs:

 * Take a "long" time to run. All tests > 5 sec on my machine will be ITs. This means that the other tests act as more frequent Regression tests.
 * depend on complex files and documents. This means they can be fragile over time and different platforms. A faling IT may occasionally be a fragile test rather than a fundamental problem. For example file listing order is not determined and although I am retrofitting sorting it isn't universal.
 * are used in an exploratory manner. This coincides with [new approaches to TDD](https://herbertograca.com/2018/08/27/distillation-of-tdd-where-did-it-all-go-wrong/). Write the tasks addressing the generation of real results (query->results), get the hack working, then gradually coalesce into classes, and, when necessary, write tests for those.
 * Occaasionally require "closed" resources such as paywalled scholarly articles. We cannot post these openly (Copyright) and these will fail for other developers. This doesn't matter.
 
## Projects
We do most projects as Open Notebook Science and have several successful projects on our discussion forum. Some are run from `norma` or `ami` but are mainly based on `cephis` technology. Examples:

* [OCR of 50-year old articles](http://discuss.contentmine.org/t/extracting-structured-text-from-ocred-bitmaps/641)
* [text mining with dictionaries](http://discuss.contentmine.org/t/analysis-of-articles-through-dictionaries-ami-obesity/660)
* [OCR of very difficult ancient documents](http://discuss.contentmine.org/t/extracting-science-from-early-scientific-documents/613)
* [transformation with stylesheets](http://discuss.contentmine.org/t/on-adding-a-new-transformer-to-norma/492) (`norma` uses `cephis`)
* [extraction and clipping of tables](http://discuss.contentmine.org/t/cm-ucl-ii-semantic-content-enhancement-of-table-data/396)
* [extracting data from vectors-based plots](http://discuss.contentmine.org/t/extracting-data-from-tilburg-funnel-plot-diagrams/386)

There are many more - feel free to read the `discuss` and update this page.




 

