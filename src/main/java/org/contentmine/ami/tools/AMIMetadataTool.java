package org.contentmine.ami.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.lucene.util.packed.DirectMonotonicReader.Meta;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.metadata.AbstractMetadata.HtmlMetadataScheme;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
/**
 * 
 * @author pm286
 *
 */
/**
  <meta name="type" content="article"/>
  <meta name="category" content="article"/>

  // identifiers
  <meta name="HW.identifier" content="/biorxiv/early/2020/01/28/2020.01.28.923011.atom"/>
  
  <meta name="HW.pisa" content="biorxiv;2020.01.28.923011v1"/>

  // format
  <meta name="DC.Format" content="text/html"/>
  <meta name="DC.Language" content="en"/>
  <meta name="DC.Title" content="Potent binding of 2019 novel coronavirus spike protein by a SARS coronavirus-specific human monoclonal antibody"/>
  <meta name="citation_title" content="Potent binding of 2019 novel coronavirus spike protein by a SARS coronavirus-specific human monoclonal antibody"/>
  <meta name="DC.Identifier" content="10.1101/2020.01.28.923011"/>
  <meta name="DC.Date" content="2020-01-28"/>
  <meta name="article:published_time" content="2020-01-28"/>
  
  <meta name="DC.Publisher" content="Cold Spring Harbor Laboratory"/>
  <meta name="DC.Rights" content="© 2020, Posted by Cold Spring Harbor Laboratory. The copyright holder for this pre-print is the author. All rights reserved. The material may not be redistributed, re-used or adapted without the author's permission."/>
  <meta name="DC.AccessRights" content="restricted"/>
  <meta name="DC.Description" content="The newly identified 2019 novel coronavirus (2019-nCoV) has caused more than 800 laboratory-confirmed human infections, including 25 deaths, posing a serious threat to human health. Currently, however, there is no specific antiviral treatment or vaccine. Considering the relatively high identity of receptor binding domain (RBD) in 2019-nCoV and SARS-CoV, it is urgent to assess the cross-reactivity of anti-SARS-CoV antibodies with 2019-nCoV spike protein, which could have important implications for rapid development of vaccines and therapeutic antibodies against 2019-nCoV. Here, we report for the first time that a SARS-CoV-specific human monoclonal antibody, CR3022, could bind potently with 2019-nCoV RBD (KD of 6.3 nM). The epitope of CR3022 does not overlap with the ACE2 binding site within 2019-nCoV RBD. Therefore, CR3022 has the potential to be developed as candidate therapeutics, alone or in combination with other neutralizing antibodies, for the prevention and treatment of 2019-nCoV infections. Interestingly, some of the most potent SARS-CoV-specific neutralizing antibodies (e.g., m396, CR3014) that target the ACE2 binding site of SARS-CoV failed to bind 2019-nCoV spike protein, indicating that the difference in the RBD of SARS-CoV and 2019-nCoV has a critical impact for the cross-reactivity of neutralizing antibodies, and that it is still necessary to develop novel monoclonal antibodies that could bind specifically to 2019-nCoV RBD."/>
  <meta name="DC.Contributor" content="Xiaolong Tian"/>
  <meta name="DC.Contributor" content="Cheng Li"/>
  ...
  <meta name="article:section" content="New Results"/>
  
  <meta name="citation_abstract" lang="en" content="&lt;;h3&gt;;ABSTRACT&lt;;/h3&gt;; &lt;;p&gt;;The newly identified 2019 novel coronavirus (2019-nCoV) has caused more than 800 laboratory-confirmed human infections, including 25 deaths, posing a serious threat to human health. Currently, however, there is no specific antiviral treatment or vaccine. Considering the relatively high identity of receptor binding domain (RBD) in 2019-nCoV and SARS-CoV, it is urgent to assess the cross-reactivity of anti-SARS-CoV antibodies with 2019-nCoV spike protein, which could have important implications for rapid development of vaccines and therapeutic antibodies against 2019-nCoV. Here, we report for the first time that a SARS-CoV-specific human monoclonal antibody, CR3022, could bind potently with 2019-nCoV RBD (KD of 6.3 nM). The epitope of CR3022 does not overlap with the ACE2 binding site within 2019-nCoV RBD. Therefore, CR3022 has the potential to be developed as candidate therapeutics, alone or in combination with other neutralizing antibodies, for the prevention and treatment of 2019-nCoV infections. Interestingly, some of the most potent SARS-CoV-specific neutralizing antibodies (e.g., m396, CR3014) that target the ACE2 binding site of SARS-CoV failed to bind 2019-nCoV spike protein, indicating that the difference in the RBD of SARS-CoV and 2019-nCoV has a critical impact for the cross-reactivity of neutralizing antibodies, and that it is still necessary to develop novel monoclonal antibodies that could bind specifically to 2019-nCoV RBD.&lt;;/p&gt;;"/>
  <meta name="citation_journal_title" content="bioRxiv"/>
  <meta name="citation_publisher" content="Cold Spring Harbor Laboratory"/>
  <meta name="citation_publication_date" content="2020/01/01"/>
  <meta name="citation_mjid" content="biorxiv;2020.01.28.923011v1"/>
  <meta name="citation_id" content="2020.01.28.923011v1"/>
  <meta name="citation_public_url" content="https://www.biorxiv.org/content/10.1101/2020.01.28.923011v1"/>
  <meta name="citation_abstract_html_url" content="https://www.biorxiv.org/content/10.1101/2020.01.28.923011v1.abstract"/>
  <meta name="citation_full_html_url" content="https://www.biorxiv.org/content/10.1101/2020.01.28.923011v1.full"/>
  <meta name="citation_pdf_url" content="https://www.biorxiv.org/content/biorxiv/early/2020/01/28/2020.01.28.923011.full.pdf"/>
  <meta name="citation_doi" content="10.1101/2020.01.28.923011"/>
  <meta name="citation_num_pages" content="12"/>
  <meta name="citation_article_type" content="Article"/>
  <meta name="citation_section" content="New Results"/>
  <meta name="citation_firstpage" content="2020.01.28.923011"/>
  
  <meta name="citation_author" content="Xiaolong Tian"/>
  <meta name="citation_author_institution" content="MOE/NHC/CAMS Key Laboratory of Medical Molecular Virology, School of Basic Medical Sciences, Shanghai Medical College, Fudan University"/>
  ..
  <meta name="citation_author" content="Yanling Wu"/>
  <meta name="citation_author_institution" content="MOE/NHC/CAMS Key Laboratory of Medical Molecular Virology, School of Basic Medical Sciences, Shanghai Medical College, Fudan University"/>
  <meta name="citation_author_email" content="tlying@fudan.edu.cn"/>
  <meta name="citation_author_email" content="yanlingwu@fudan.edu.cn"/>
  <meta name="citation_author_email" content="yang_zhenlin@fudan.edu.cn"/>
  
  <meta name="citation_reference" content="Jasper F-W C, Kin-H K, Zheng Z, et al. Genomic characterization of the 2019 novel human-pathogenic coronavirus isolated from patients with acute respiratory disease in Wuhan, Hubei, China. Emerging Microbes &amp;; Infections. 2020."/>
  ...
  <meta name="citation_date" content="2020-01-28"/>
  <meta name="description" content="bioRxiv - the preprint server for biology, operated by Cold Spring Harbor Laboratory, a research and educational institution"/>
  <meta name="generator" content="Drupal 7 (http://drupal.org)"/>
  
  2. Configuring the meta-tags

If you're using repository or journal management software, such as Eprints, DSpace, Digital Commons 
or OJS, please configure it to export bibliographic data in HTML "<meta>" tags. Google Scholar 
supports Highwire Press tags (e.g., citation_title), Eprints tags (e.g., eprints.title), 
BE Press tags (e.g., bepress_citation_title), and PRISM tags (e.g., prism.title). Use Dublin Core
 tags (e.g., DC.title) as a last resort - they work poorly for journal papers because 
 Dublin Core doesn't have unambiguous fields for journal title, volume, issue, and page numbers. 
 To check that these tags are present, visit several abstracts and view their HTML source.

The title tag, e.g., citation_title or DC.title, must contain the title of the paper. 
Don't use it for the title of the journal or a book in which the paper was published, 
or for the name of your repository. This tag is required for inclusion in Google Scholar.

The author tag, e.g., citation_author or DC.creator, must contain the authors (and only the 
actual authors) of the paper. Don't use it for the author of the website or for contributors 
other than authors, e.g., thesis advisors. Author names can be listed either as "Smith, John"
 or as "John Smith". Put each author name in a separate tag and omit all affiliations, degrees,
  certifications, etc., from this field. At least one author tag is required for inclusion in 
  Google Scholar.

The publication date tag, e.g., citation_publication_date or DC.issued, must contain the date 
of publication, i.e., the date that would normally be cited in references to this paper from
 other papers. Don't use it for the date of entry into the repository - that should go into 
 citation_online_date instead. Provide full dates in the "2010/5/12" format if available; 
 or a year alone otherwise. This tag is required for inclusion in Google Scholar.

For journal and conference papers, provide the remaining bibliographic citation data in the 
following tags: citation_journal_title or citation_conference_title, citation_issn, citation_isbn, 
citation_volume, citation_issue, citation_firstpage, and citation_lastpage. 
Dublin Core equivalents are DC.relation.ispartof for journal and conference titles and the 
non-standard tags DC.citation.volume, DC.citation.issue, DC.citation.spage (start page), 
and DC.citation.epage (end page) for the remaining fields. Regardless of the scheme chosen, 
these fields must contain sufficient information to identify a reference to this paper from 
another document, which is normally all of: (a) journal or conference name, (b) volume and 
issue numbers, if applicable, and (c) the number of the first page of the paper in the volume 
(or issue) in question.

For theses, dissertations, and technical reports, provide the remaining bibliographic citation 
data in the following tags: citation_dissertation_institution, citation_technical_report_institution
 or DC.publisher for the name of the institution and citation_technical_report_number for the number 
 of the technical report. As with journal and conference papers, you need to provide sufficient 
 information to recognize a formal citation to this document from another article.

For all document types, the guiding principle is to present your article as it would normally 
be cited in the "References" section of another paper. E.g., citations to technical reports normally 
include their assigned numbers, so the number of the report should be present in some appropriate 
field. Likewise, the name of the journal should be written as "Transactions on Magic Realism" or 
"Trans. Mag. Real.", not as "Magic Realism, Transactions on" or "T12". Omission or unusual 
presentation of key bibliographic fields can lead to mis-identification of your articles.

All tag values are HTML attributes, so you must escape special characters appropriately. E.g., 
<meta name="citation_title" content="&quot;Andar com meus sapatos&quot; - uma an&#225;lise cr&#237;tica">. 
There's no need to escape characters that are written directly in your webpage's character 
encoding, such as Latin diacritics on a page in ISO-8859-1. However, you must still escape the 
quotes and the angle brackets.

The "<meta>" tags normally apply only to the exact page on which they're provided. If this page 
shows only the abstract of the paper and you have the full text in a separate file, e.g., in the 
PDF format, please specify the locations of all full text versions using citation_pdf_url or 
DC.identifier tags. The content of the tag is the absolute URL of the PDF file; for security reasons, 
it must refer to a file in the same subdirectory as the HTML abstract.

Failure to link the alternate versions together could result in the incorrect indexing of the PDF 
files, because these files would be processed as separate documents without the information contained 
in the meta tags.
Example:

<meta name="citation_title" content="The testis isoform of the phosphorylase kinase catalytic subunit (PhK-T) plays a critical role in regulation of glycogen mobilization in developing lung">
<meta name="citation_author" content="Liu, Li">
<meta name="citation_author" content="Rannels, Stephen R.">
<meta name="citation_author" content="Falconieri, Mary">
<meta name="citation_author" content="Phillips, Karen S.">
<meta name="citation_author" content="Wolpert, Ellen B.">
<meta name="citation_author" content="Weaver, Timothy E.">
<meta name="citation_publication_date" content="1996/05/17">
<meta name="citation_journal_title" content="Journal of Biological Chemistry">
<meta name="citation_volume" content="271">
<meta name="citation_issue" content="20">
<meta name="citation_firstpage" content="11761">
<meta name="citation_lastpage" content="11766">
<meta name="citation_pdf_url" content="http://www.example.com/content/271/20/11761.full.pdf">
Keep in mind that, regardless of the meta-tag scheme chosen, you need to provide at least three fields: 
(1) the title of the article, 
(2) the full name of at least the first author, and 
(3) the year of 
publication. Pages that don't provide any one of these three fields will be processed as if they had no 
meta tags at all. Likewise, all PDF files will be processed as if they had no meta tags at all, unless 
they're linked from the corresponding HTML abstracts using citation_pdf_url or DC.identifier tags. 
It works best to provide the meta-tags for all versions of your paper, not just for one of the versions.


 * @author pm286
 *
 */

@Command(
name = "metadata",
description = {
		"Manages metadata for both CProject and CTrees.",
		"Beacuse there are so many "
				+ "different places that metadata can come from and because there are several "
				+ "'standards', inconsistently used the class will change fairly frequently."
})
public class AMIMetadataTool extends AbstractAMITool {

	private static final Logger LOG = LogManager.getLogger(AMIMetadataTool.class);
//	interface ToolMethod {
//		public void runMe();
//	}
	
//	public enum Meta {
//		dc,
//		highwire,
//		;
//		
//		private Meta() {
//		}
//		
//	}

    @Option(names = {"--meta"},
    		arity = "1..*",
            description = "type or types from <meta> fields (in precedence order)")
    private List<Meta> metaList = new ArrayList<>();

	
    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIMetadataTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIMetadataTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMIMetadataTool().runCommands(new String[] {"--meta", "dc", "highwire"});
    }


    @Override
	protected boolean parseGenerics() {
		LOG.info("project         " + cProject);
		return true;
	}

    @Override
	protected void parseSpecifics() {
		LOG.info("meta            " + metaList);
	}
    
    @Override
    protected void runSpecifics() {
    	runTools();
    }

	private void runTools() {
		if (metaList.size() == 0) {
			LOG.warn("enum "+Arrays.asList(HtmlMetadataScheme.values()));
		} else {
			metaList.forEach(LOG::warn);
		}
	}

}
