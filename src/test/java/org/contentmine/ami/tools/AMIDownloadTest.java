package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.download.AbstractMetadataEntry;
import org.contentmine.ami.tools.download.HitList;
import org.contentmine.ami.tools.download.biorxiv.BiorxivDownloader;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlLi;
import org.contentmine.graphics.html.HtmlUl;
import org.contentmine.html.util.WebDriverXom;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import nu.xom.Element;

/** test OCR.
 * 
 * @author pm286
 *
 */
public class AMIDownloadTest extends AbstractAMITest {
	private static final String CHROMEDRIVER = "/usr/local/bin/chromedriver";
	public static final Logger LOG = Logger.getLogger(AMIDownloadTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static File DOWNLOAD_DIR = new File(SRC_TEST_TOOLS, "download");
	private static File BIORXIV_DIR = new File(DOWNLOAD_DIR, "biorxiv");
	private static File CLIMATE_DIR = new File(BIORXIV_DIR, "climate");
	
    private Object first;
    private Object second;

    private List<Object> list;
    

	@Test
	/** 
	 * run query
	 * inline this is 
	ami download -p target/biorxiv --site biorxiv --query coronavirus --pagesize 1 --pages 1 1 --fulltext pdf html --resultset raw clean
	 */
	public void testBiorxivSmall() throws Exception {
		
		File target = new File("target/biorxiv1");
		if (target.exists()) {FileUtils.deleteDirectory(target);}
		MatcherAssert.assertThat(target+" does not exist", !target.exists());
		String args = 
				"-p " + target
				+ " download"
				+ " --site biorxiv" // the type of site 
				+ " --query coronavirus" // the query
				+ " --pagesize 1" // size of remote pages (may not always work)
				+ " --pages 1 1" // number of pages
				+ " --fulltext pdf html"
				+ " --resultset raw clean"
//				+ " --limit 500"  // total number of downloaded results
			;
		AMI.execute(args);
		Assert.assertTrue("target exists", target.exists());
		// check for reserved and non-reserved child files
		long fileCount0 = Files.walk(target.toPath(), AbstractMetadata.CPROJECT_DEPTH)
				.sorted()
				.peek(System.out::println)
				.count();
		Assert.assertEquals("files", 1, fileCount0);
		Assert.assertEquals("directory", 1, fileCount0);
		
		long fileCount = Files.walk(target.toPath(), AbstractMetadata.CTREE_DEPTH)
			.sorted()
			.count();
		Assert.assertEquals("files", 3, fileCount); // project metadata and 2 CTrees
		// files only
		fileCount = Files.walk(target.toPath(), AbstractMetadata.CTREE_CHILD_DEPTH)
				.sorted()
				.filter(f -> !f.toFile().isDirectory())
				.peek(System.out::println)
				.count();
		Assert.assertEquals("files", 8, fileCount); 
		fileCount = Files.walk(target.toPath())
				.sorted()
				.filter(f -> f.toFile().isDirectory()) // biorxiv1/, __metadata/ and 1 ctree
				.count();
		Assert.assertEquals("files", 3, fileCount);
	}

	@Test
	/** 
	 * run query
	 * VERY long
	 */
	/**
	 * fails around
	 * ...
running [curl, -X, GET, https://www.biorxiv.org/content/10.1101/798546v1]
.skipping (existing) hitList: target/biorxiv/10_1101_798546v1
skipping existing : abstract.html
skipping existing : fulltext.html
skipping existing : fulltext.pdf
skipped: 10_1101_2020_03_17_995209v1
running [curl, -X, GET, https://www.biorxiv.org/content/10.1101/2020.03.17.995209v1]
.skipping (existing) hitList: target/biorxiv/10_1101_2020_03_17_995209v1
skipping existing : abstract.html
skipping existing : fulltext.html
skipping existing : fulltext.pdf
skipped: 10_1101_862573v2
running [curl, -X, GET, https://www.biorxiv.org/content/10.1101/862573v2]
[Fatal Error] :341:633: The reference to entity "amp" must end with the ';' delimiter.
<341/633>badline > <div id="hw-article-author-popups-node1073120" style="display: none;"><div class="author-tooltip-0"><div class="author-tooltip-name">Ivan S. Kholodilov </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>1</span><span class='nlm-institution'>“Chumakov Institute of Poliomyelitis and Viral Encephalitides” FSBSI “Chumakov FSC R[[D IBP RAS”</span>, Moscow, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp]]gs_type=author&amp;;author%5B0%5D=Ivan%2BS.%2BKholodilov%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Kholodilov%20IS&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link"><a href="/search/author1%3AIvan%2BS.%2BKholodilov%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li><li class="author-orcid-link last"><a href="http://orcid.org/0000-0002-3764-7081" target="_blank" class="" data-icon-position="" data-hide-link-title="0">ORCID record for Ivan S. Kholodilov</a></li></ul></div><div class="author-tooltip-1"><div class="author-tooltip-name">Alexander G. Litov </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>1</span><span class='nlm-institution'>“Chumakov Institute of Poliomyelitis and Viral Encephalitides” FSBSI “Chumakov FSC R[[D IBP RAS”</span>, Moscow, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp]]gs_type=author&amp;;author%5B0%5D=Alexander%2BG.%2BLitov%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Litov%20AG&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link"><a href="/search/author1%3AAlexander%2BG.%2BLitov%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li><li class="author-orcid-link last"><a href="http://orcid.org/0000-0002-6086-3655" target="_blank" class="" data-icon-position="" data-hide-link-title="0">ORCID record for Alexander G. Litov</a></li></ul></div><div class="author-tooltip-2"><div class="author-tooltip-name">Alexander S. Klimentov </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>1</span><span class='nlm-institution'>“Chumakov Institute of Poliomyelitis and Viral Encephalitides” FSBSI “Chumakov FSC R[[D IBP RAS”</span>, Moscow, <span class='nlm-country'>Russia</span></div><div class='author-affiliation'><span class='nlm-sup'>2</span><span class='nlm-institution'>Federal Research Centre for Epidemiology and Microbiology named after the honorary academician N.F. Gamaleya of the Ministry of Health of the Russian Federation</span>, Gamaleya street 18, Moscow 123098, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp]]gs_type=author&amp;;author%5B0%5D=Alexander%2BS.%2BKlimentov%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Klimentov%20AS&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link"><a href="/search/author1%3AAlexander%2BS.%2BKlimentov%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li><li class="author-orcid-link last"><a href="http://orcid.org/0000-0002-6472-3828" target="_blank" class="" data-icon-position="" data-hide-link-title="0">ORCID record for Alexander S. Klimentov</a></li></ul></div><div class="author-tooltip-3"><div class="author-tooltip-name">Oxana A. Belova </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>1</span><span class='nlm-institution'>“Chumakov Institute of Poliomyelitis and Viral Encephalitides” FSBSI “Chumakov FSC R[[D IBP RAS”</span>, Moscow, <span class='nlm-country'>Russia</span></div><div class='author-affiliation'><span class='nlm-sup'>3</span><span class='nlm-institution'>Martsinovsky Institute of Medical Parasitology, Tropical and Vector Borne Diseases, Sechenov University</span>, Moscow, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp]]gs_type=author&amp;;author%5B0%5D=Oxana%2BA.%2BBelova%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Belova%20OA&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link"><a href="/search/author1%3AOxana%2BA.%2BBelova%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li><li class="author-orcid-link last"><a href="http://orcid.org/0000-0002-9040-0774" target="_blank" class="" data-icon-position="" data-hide-link-title="0">ORCID record for Oxana A. Belova</a></li></ul></div><div class="author-tooltip-4"><div class="author-tooltip-name">Alexandra E. Polienko </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>1</span><span class='nlm-institution'>“Chumakov Institute of Poliomyelitis and Viral Encephalitides” FSBSI “Chumakov FSC R[[D IBP RAS”</span>, Moscow, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp]]gs_type=author&amp;;author%5B0%5D=Alexandra%2BE.%2BPolienko%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Polienko%20AE&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link"><a href="/search/author1%3AAlexandra%2BE.%2BPolienko%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li><li class="author-orcid-link last"><a href="http://orcid.org/0000-0003-1585-8571" target="_blank" class="" data-icon-position="" data-hide-link-title="0">ORCID record for Alexandra E. Polienko</a></li></ul></div><div class="author-tooltip-5"><div class="author-tooltip-name">Nikolai A. Nikitin </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>4</span><span class='nlm-institution'>Faculty of Biology, Lomonosov MSU</span>, Lenin Hills, 1/12, Moscow 119234, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp;;gs_type=author&amp;;author%5B0%5D=Nikolai%2BA.%2BNikitin%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Nikitin%20NA&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link"><a href="/search/author1%3ANikolai%2BA.%2BNikitin%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li><li class="author-orcid-link last"><a href="http://orcid.org/0000-0001-9626-2336" target="_blank" class="" data-icon-position="" data-hide-link-title="0">ORCID record for Nikolai A. Nikitin</a></li></ul></div><div class="author-tooltip-6"><div class="author-tooltip-name">Alexey M. Shchetinin </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>2</span><span class='nlm-institution'>Federal Research Centre for Epidemiology and Microbiology named after the honorary academician N.F. Gamaleya of the Ministry of Health of the Russian Federation</span>, Gamaleya street 18, Moscow 123098, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp;;gs_type=author&amp;;author%5B0%5D=Alexey%2BM.%2BShchetinin%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Shchetinin%20AM&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link"><a href="/search/author1%3AAlexey%2BM.%2BShchetinin%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li><li class="author-orcid-link last"><a href="http://orcid.org/0000-0003-1842-3899" target="_blank" class="" data-icon-position="" data-hide-link-title="0">ORCID record for Alexey M. Shchetinin</a></li></ul></div><div class="author-tooltip-7"><div class="author-tooltip-name">Anna Y. Ivannikova </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>1</span><span class='nlm-institution'>“Chumakov Institute of Poliomyelitis and Viral Encephalitides” FSBSI “Chumakov FSC R[[D IBP RAS”</span>, Moscow, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp]]gs_type=author&amp;;author%5B0%5D=Anna%2BY.%2BIvannikova%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Ivannikova%20AY&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link"><a href="/search/author1%3AAnna%2BY.%2BIvannikova%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li><li class="author-orcid-link last"><a href="http://orcid.org/0000-0001-7698-7487" target="_blank" class="" data-icon-position="" data-hide-link-title="0">ORCID record for Anna Y. Ivannikova</a></li></ul></div><div class="author-tooltip-8"><div class="author-tooltip-name">Lesley Bell-Sakyi </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>5</span><span class='nlm-institution'>Department of Infection Biology, Institute of Infection and Global Health, University of Liverpool</span>, Liverpool L3 5RF, <span class='nlm-country'>UK</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp;;gs_type=author&amp;;author%5B0%5D=Lesley%2BBell-Sakyi%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Bell-Sakyi%20L&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link"><a href="/search/author1%3ALesley%2BBell-Sakyi%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li><li class="author-orcid-link last"><a href="http://orcid.org/0000-0002-7305-0477" target="_blank" class="" data-icon-position="" data-hide-link-title="0">ORCID record for Lesley Bell-Sakyi</a></li></ul></div><div class="author-tooltip-9"><div class="author-tooltip-name">Alexander S. Yakovlev </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>1</span><span class='nlm-institution'>“Chumakov Institute of Poliomyelitis and Viral Encephalitides” FSBSI “Chumakov FSC R[[D IBP RAS”</span>, Moscow, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp]]gs_type=author&amp;;author%5B0%5D=Alexander%2BS.%2BYakovlev%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Yakovlev%20AS&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link"><a href="/search/author1%3AAlexander%2BS.%2BYakovlev%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li><li class="author-orcid-link last"><a href="http://orcid.org/0000-0002-1833-6122" target="_blank" class="" data-icon-position="" data-hide-link-title="0">ORCID record for Alexander S. Yakovlev</a></li></ul></div><div class="author-tooltip-10"><div class="author-tooltip-name">Sergey V. Bugmyrin </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>6</span><span class='nlm-institution'>Institute of Biology of Karelian Research Centre of the Russian Academy of Sciences (IB KarRC RAS)</span>, Petrozavodsk, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp;;gs_type=author&amp;;author%5B0%5D=Sergey%2BV.%2BBugmyrin%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Bugmyrin%20SV&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link"><a href="/search/author1%3ASergey%2BV.%2BBugmyrin%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li><li class="author-orcid-link last"><a href="http://orcid.org/0000-0001-5285-6933" target="_blank" class="" data-icon-position="" data-hide-link-title="0">ORCID record for Sergey V. Bugmyrin</a></li></ul></div><div class="author-tooltip-11"><div class="author-tooltip-name">Liubov A. Bespyatova </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>6</span><span class='nlm-institution'>Institute of Biology of Karelian Research Centre of the Russian Academy of Sciences (IB KarRC RAS)</span>, Petrozavodsk, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp;;gs_type=author&amp;;author%5B0%5D=Liubov%2BA.%2BBespyatova%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Bespyatova%20LA&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link last"><a href="/search/author1%3ALiubov%2BA.%2BBespyatova%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li></ul></div><div class="author-tooltip-12"><div class="author-tooltip-name">Larissa V. Gmyl </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>1</span><span class='nlm-institution'>“Chumakov Institute of Poliomyelitis and Viral Encephalitides” FSBSI “Chumakov FSC R[[D IBP RAS”</span>, Moscow, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp]]gs_type=author&amp;;author%5B0%5D=Larissa%2BV.%2BGmyl%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Gmyl%20LV&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link last"><a href="/search/author1%3ALarissa%2BV.%2BGmyl%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li></ul></div><div class="author-tooltip-13"><div class="author-tooltip-name">Svetlana V. Luchinina </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>7</span><span class='nlm-institution'>Russian Federal Service for Surveillance on Consumer Rights Protection and Human Wellbeing</span>, Yelkin str., 73, Chelyabinsk 454091, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp;;gs_type=author&amp;;author%5B0%5D=Svetlana%2BV.%2BLuchinina%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Luchinina%20SV&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link last"><a href="/search/author1%3ASvetlana%2BV.%2BLuchinina%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li></ul></div><div class="author-tooltip-14"><div class="author-tooltip-name">Anatoly P. Gmyl </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>1</span><span class='nlm-institution'>“Chumakov Institute of Poliomyelitis and Viral Encephalitides” FSBSI “Chumakov FSC R[[D IBP RAS”</span>, Moscow, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp]]gs_type=author&amp;;author%5B0%5D=Anatoly%2BP.%2BGmyl%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Gmyl%20AP&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link last"><a href="/search/author1%3AAnatoly%2BP.%2BGmyl%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li></ul></div><div class="author-tooltip-15"><div class="author-tooltip-name">Vladimir A. Gushchin </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>2</span><span class='nlm-institution'>Federal Research Centre for Epidemiology and Microbiology named after the honorary academician N.F. Gamaleya of the Ministry of Health of the Russian Federation</span>, Gamaleya street 18, Moscow 123098, <span class='nlm-country'>Russia</span></div><div class='author-affiliation'><span class='nlm-sup'>4</span><span class='nlm-institution'>Faculty of Biology, Lomonosov MSU</span>, Lenin Hills, 1/12, Moscow 119234, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp;;gs_type=author&amp;;author%5B0%5D=Vladimir%2BA.%2BGushchin%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Gushchin%20VA&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link"><a href="/search/author1%3AVladimir%2BA.%2BGushchin%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li><li class="author-orcid-link last"><a href="http://orcid.org/0000-0002-9397-3762" target="_blank" class="" data-icon-position="" data-hide-link-title="0">ORCID record for Vladimir A. Gushchin</a></li></ul></div><div class="author-tooltip-16"><div class="author-tooltip-name">Galina G. Karganova </div><div class="author-tooltip-affiliation"><span class="author-tooltip-text"><div class='author-affiliation'><span class='nlm-sup'>1</span><span class='nlm-institution'>“Chumakov Institute of Poliomyelitis and Viral Encephalitides” FSBSI “Chumakov FSC R[[D IBP RAS”</span>, Moscow, <span class='nlm-country'>Russia</span></div><div class='author-affiliation'><span class='nlm-sup'>8</span><span class='nlm-institution'>Institute for Translational Medicine and Biotechnology, Sechenov University</span>, Bolshaya Pirogovskaya st, 2, Moscow, 119991, <span class='nlm-country'>Russia</span></div></span></div><ul class="author-tooltip-find-more"><li class="author-tooltip-gs-link first"><a href="/lookup/google-scholar?link_type=googlescholar&amp]]gs_type=author&amp;;author%5B0%5D=Galina%2BG.%2BKarganova%2B" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on Google Scholar</a></li><li class="author-tooltip-pubmed-link"><a href="/lookup/external-ref?access_num=Karganova%20GG&amp;;link_type=AUTHORSEARCH" target="_blank" class="" data-icon-position="" data-hide-link-title="0">Find this author on PubMed</a></li><li class="author-site-search-link"><a href="/search/author1%3AGalina%2BG.%2BKarganova%2B" rel="nofollow" class="" data-icon-position="" data-hide-link-title="0">Search for this author on this site</a></li><li class="author-orcid-link"><a href="http://orcid.org/0000-0002-8901-6206" target="_blank" class="" data-icon-position="" data-hide-link-title="0">ORCID record for Galina G. Karganova</a></li><li class="author-corresp-email-link last"><span>For correspondence: 
e=googlescholar&amp]]gs_type=author&amp;
CANNOT PARSE:
<!DOCTYPE html>
<html lang="en" dir="ltr" 
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:mml="http:
1394887 [main] ERROR org.contentmine.ami.tools.download.AbstractMetadataEntry  - Cannot parse: java.lang.RuntimeException: nu.xom.ParsingException: The reference to entity "amp" must end with the ';' delimiter. at line 341, column 633
1394887 [main] ERROR org.contentmine.ami.tools.download.AbstractMetadataEntry  - Cannot parse: java.lang.RuntimeException: nu.xom.ParsingException: The reference to entity "amp" must end with the ';' delimiter. at line 341, column 633
.skipped: 10_1101_455568v1
running [curl, -X, GET, https://www.biorxiv.org/content/10.1101/455568v1]
.skipping (existing) hitList: target/biorxiv/10_1101_455568v1
skipping existing : abstract.html
skipping existing : fulltext.html
skipping existing : fulltext.pdf

likely to be edited frequently while debugging!

	 * @throws Exception
	 */
	public void testBiorxivIT() throws Exception {
		String biorxiv = "target/biorxiv";
		String args = "-p " + "target"
				+ " clean"
				+ " biorxiv/"; // TODO verify that this works as desired
		AMI.execute(args);
		
		args = 
				"-p " + biorxiv
				+ " download"
				+ " --site biorxiv"
				+ " --query coronavirus"
				+ " --pagesize 100"
//				+ " --pagesize 4"
				+ " --pages 1 10"
//				+ " --pages 1 3"
				+ " --fulltext html"
				+ " --limit 2000"
			;
		AMI.execute(args);
// first creates the hitLists		

//		File file962688v1 = new File(biorxiv, "10_1101_2020_02_24_962688v1");
//		Assert.assertTrue(""+file962688v1, file962688v1.exists());
//		Assert.assertTrue(""+file962688v1, new File(file962688v1, "abstract.html").exists());
//		Assert.assertTrue(""+file962688v1, new File(file962688v1, "landingPage.html").exists());
//		Assert.assertTrue(""+file962688v1, new File(file962688v1, "hitList.html").exists());
//		Assert.assertTrue(""+file962688v1, new File(file962688v1, "scrapedMetadata.html").exists());
//		Assert.assertTrue(""+file962688v1, new File(file962688v1, "fulltext.html").exists());
//		Assert.assertTrue(""+file962688v1, new File(file962688v1, "fulltext.pdf").exists());
		
		System.out.println("exited normally");
		
/**
 * problem comes at		
 *   
 */
	}

	/**
	 * Test the hitLists. These are wrong...
	 * 
	 * Query: aardvark%20sort%3Arelevance-rank%20numresults%3A10
URL https://www.biorxiv.org/search/aardvark%20sort%3Arelevance-rank%20numresults%3A10
runing curl :https://www.biorxiv.org/search/aardvark%20sort%3Arelevance-rank%20numresults%3A10 to target/biorxiv/aardvark/__metadata/hitList1.html
wrote hitList: /Users/pm286/workspace/cmdev/ami3/target/biorxiv/aardvark/__metadata/hitList1.clean.html
metadataEntries 10
calculating hits NYI
Results 10
runing curl :https://www.biorxiv.org/search/aardvark%20sort%3Arelevance-rank%20numresults%3A10 to target/biorxiv/aardvark/__metadata/hitList2.html
wrote hitList: /Users/pm286/workspace/cmdev/ami3/target/biorxiv/aardvark/__metadata/hitList2.clean.html
metadataEntries 1
Results 1
runing curl :https://www.biorxiv.org/search/aardvark%20sort%3Arelevance-rank%20numresults%3A10 to target/biorxiv/aardvark/__metadata/hitList3.html
wrote hitList: /Users/pm286/workspace/cmdev/ami3/target/biorxiv/aardvark/__metadata/hitList3.clean.html
metadataEntries 10
Results 10
runing curl :https://www.biorxiv.org/search/aardvark%20sort%3Arelevance-rank%20numresults%3A10 to target/biorxiv/aardvark/__metadata/hitList4.html
wrote hitList: /Users/pm286/workspace/cmdev/ami3/target/biorxiv/aardvark/__metadata/hitList4.clean.html
metadataEntries 10
Results 10
[target/biorxiv/aardvark/__metadata/hitList1.clean.html, target/biorxiv/aardvark/__metadata/hitList2.clean.html, target/bi

gives:
-rw-r--r--@ 1 pm286  staff  23952 19 Apr 18:29 __metadata/hitList4.clean.html
-rw-r--r--@ 1 pm286  staff  23952 19 Apr 18:29 __metadata/hitList3.clean.html
-rw-r--r--@ 1 pm286  staff   3028 19 Apr 18:27 __metadata/hitList2.clean.html
-rw-r--r--@ 1 pm286  staff  26641 19 Apr 18:27 __metadata/hitList1.clean.html

hitList2 and hitList3 are the wrong way round.

	 *
	 */
	@Test
	public void testSmallMultipageDownload() {
		String args;
		String biorxiv = "target/biorxiv/aardvark";
        args = "-p " + "target"
				+ " clean"
				+ " biorxiv/";
		AMI.execute(args);
		
		args = 
				"-p " + biorxiv +""
				+ " download"
				+ " --site biorxiv"
				+ " --query aardvark"
				+ " --pagesize 10"
				+ " --pages 1 4"        // should leave page 4 blank
				+ " --fulltext html"
				+ " --limit 2000"
			;
		AMIDownloadTool amiDownload = AMI.execute(AMIDownloadTool.class, args);
//		AMIDownloadTool amiDownload = AMI.execute(args);

	}
	
	@Test
	public void testMedrxivDownload() {
		String args;
		String biorxiv = "target/medrxiv/ebola";
        args = "-p " + "target"
				+ " clean"
				+ " medrxiv/";
		AMI.execute(args);
		
		args = 
				"-p " + biorxiv +""
				+ " download"
				+ " --site medrxiv"
				+ " --query \"ebola AND n95\""
				+ " --pagesize 20"
				+ " --pages 1 4"        
				+ " --fulltext pdf"
				+ " --limit 2000"
			;
		AMIDownloadTool amiDownload = AMI.execute(AMIDownloadTool.class, args);

	}

	@Test
	public void testHalDownload() {
		String args;
		String target = "target/hal/ebola";
        args = "-p " + target
				+ " clean"
				+ " hal/";
		AMI.execute(args);
		
		args = 
				"-p " + target +""
				+ " download"
				+ " --site hal"
				+ " --query ebola"
				+ " --pagesize 20"
				+ " --pages 1 4"        
				+ " --fulltext pdf"
				+ " --limit 2000"
			;
		AMIDownloadTool amiDownload = AMI.execute(AMIDownloadTool.class, args);

	}

	@Test
	/** 
	 * run query
	 */
	@Ignore
	public void testBiorxivClimate() throws Exception {
		String args = 
				"-p target/biorxiv/climate"
				+ " download"
				+ " --site biorxiv"
				+ " --query climate change"
				+ " --metadata metadata"
				+ " --rawfiletypes html"
				+ " --pagesize 10"
				+ " --pages 1 3"
				+ " --limit 100"
			;
		AMI.execute(args);
// I think this is an outdated Assert.
//		Assert.assertTrue(new File("target/biorxiv/climate/metadata/page1.html").exists());
//		these should work
		Assert.assertTrue(new File("target/biorxiv/climate/__metadata/hitList3.html").exists());
		Assert.assertTrue(new File("target/biorxiv/climate/10_1101_2019_12_16_878348v1/landingPage.html").exists());
		Assert.assertTrue(new File("target/biorxiv/climate/10_1101_2019_12_16_878348v1/rawFullText.html").exists());
		Assert.assertTrue(new File("target/biorxiv/climate/10_1101_2019_12_16_878348v1/scholarly.html").exists());
		Assert.assertTrue(new File("target/biorxiv/climate/10_1101_2019_12_16_878348v1/scrapedMetadata.html").exists());
	}

	// extract fulltext with div[class~="fulltext-view"]
	
	
	/** to test that we can run curl from java
	 * 
	 * @throws Exception
	 */
	@Test 
	public void testCurlREST() throws Exception {
			
		String result = runCurlProcess("https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=coronavirus");
		System.out.println("EBI "+result);

		result = runCurlProcess("https://www.biorxiv.org/search/coronavirus");
		System.out.println("BIOX "+result);
		
//		https://www.biorxiv.org/search/coronavirus%20numresults%3A75%20sort%3Arelevance-rank

	}

//	@Test 
//	/** downloads a single curlPair
//	 * 
//	 * @throws Exception
//	 */
//	public void testCurlDownloader() throws Exception {
//			
//		File downloadDir = new File("target/biorxiv/");
//		CurlDownloader curlDownloader = new CurlDownloader();
//		String fileroot = "10.1101/850289v1";
//		
//		CurlPair curlPair = new BiorxivDownloader().createLandingPageCurlPair(downloadDir, fileroot);
//		curlDownloader.addCurlPair(curlPair);
//
//		String result = curlDownloader.run();
//		System.out.println("BIOX ["+result+"]");
//		Assert.assertTrue(curlPair.getFile().getAbsoluteFile()+" exists", curlPair.getFile().exists());
//
//	}
	

//	@Test 
//	/** download multiple URLs in a single run.
//	 * Still appears to run each sequentially so relatively little performanace gain,
//	 * but maybe worthwhile.
//	 * 
//	 * @throws Exception
//	 */
//	public void testCurlDownloaderMultiple() throws Exception {
//		File downloadDir = new File("target/biorxiv/");
//		CurlDownloader curlDownloader = new CurlDownloader();
//		// these are verbatim from the hitList file
//		String[] fileroots = {
//			       "/content/10.1101/2020.01.24.917864v1",
//			       "/content/10.1101/850289v1",
//			       "/content/10.1101/641399v2",
//			       "/content/10.1101/844886v1",
//			       "/content/10.1101/709089v1",
//			       "/content/10.1101/823724v1",
//			       "/content/10.1101/827196v1",
//			       "/content/10.1101/823930v1",
//			       "/content/10.1101/821561v1",
//			       "/content/10.1101/819326v1",
//			      };
//		for (String fileroot : fileroots) {
//			curlDownloader.addCurlPair(new BiorxivDownloader().createLandingPageCurlPair(downloadDir, fileroot));
//		}
//		
//		curlDownloader.setTraceFile("target/trace.txt");
//		curlDownloader.setTraceTime(true);
//		String result = curlDownloader.run();
//		LOG.debug("result ["+result+"]");
//
//	}



	@Test
	/**
	 * 
	 */
	public void testCreateUnpopulatedCTreesFromHitList() throws IOException {
		File targetDir = new File("target/biorxiv/climate");
		CMineTestFixtures.cleanAndCopyDir(CLIMATE_DIR, targetDir);
		
		CProject cProject = new CProject(targetDir);
		File metadataDir = cProject.getOrCreateExistingMetadataDir();
		/** reads existing hitList file to create object */
		HitList hitList = new BiorxivDownloader().setCProject(cProject).createHitList(new File(metadataDir, "hitList1.clean.html"));
		// result set had default 10 entries
		List<AbstractMetadataEntry> metadataEntryList = hitList.getMetadataEntryList();
		Assert.assertEquals("metadata", 10, +metadataEntryList.size());
		// metadata directory had 3 results sets, each raw and clean
		Assert.assertEquals(6, metadataDir.listFiles().length);
		// remove all existing CTrees for the test
		cProject.cleanAllTrees();
		Assert.assertEquals(0,  cProject.getOrCreateCTreeList().size());
		// this is the __metadata directory
		Assert.assertEquals(1,  cProject.getDirectory().listFiles().length);
		// create trees from result set
		hitList.createCTrees(cProject);
		Assert.assertEquals("Ctree count", 10, cProject.getOrCreateCTreeList().size());
		
		
	}
	
//	@Test
//	/**
//	 * as above, but download landing pages
//	 */
//	public void testCreateCTreeLandingPagesFromHitListIT() throws IOException {
//		File targetDir = new File("target/biorxiv/climate");
//		CMineTestFixtures.cleanAndCopyDir(CLIMATE_DIR, targetDir);
//		
//		CProject cProject = new CProject(targetDir).cleanAllTrees();
//		File metadataDir = cProject.getOrCreateExistingMetadataDir();
//		AbstractDownloader biorxivDownloader = new BiorxivDownloader().setCProject(cProject);
//		HitList hitList = biorxivDownloader.createHitList(new File(metadataDir, "hitList1.clean.html"));
//		List<String> fileroots = hitList.getCitationLinks();
//		CurlDownloader curlDownloader = new CurlDownloader();
//		for (String fileroot : fileroots) {
//			curlDownloader.addCurlPair(biorxivDownloader.createLandingPageCurlPair(cProject.getDirectory(), fileroot));
//		}
//		
//		curlDownloader.setTraceFile("target/trace.txt");
//		curlDownloader.setTraceTime(true);
//		String result = curlDownloader.run();
//		LOG.debug("result ["+result+"]");
//
////		Assert.assertEquals("Ctree count", 10, cProject.getOrCreateCTreeList().size());
//		
//	}
	
	@Test
	/** issues a search  and turns results into hitList
	 * 
	 * LONG 68 s
	 */
	public void testBiorxivSearchHitListIT() throws IOException {
		File targetDir = new File("target/biorxiv/testsearch4");
		FileUtils.deleteQuietly(targetDir);
		CProject cProject = new CProject(targetDir).cleanAllTrees();
		cProject.cleanAllTrees();
		String args = 
				"-p " + cProject.toString()
				+ " download"
				+ " --site biorxiv"
				+ " --query climate change"
				+ " --metadata __metadata"
				+ " --rawfiletypes html"
				+ " --pagesize 4"
				+ " --pages 1 1"
				+ " --limit 4"
				+ " --resultset hitList1.clean.html"
			;
		AMIDownloadTool downloadTool = AMI.execute(AMIDownloadTool.class, args);
		Assert.assertTrue(new File(targetDir, "__metadata/hitList1.html").exists());
		Assert.assertTrue(new File(targetDir, "__metadata/hitList1.clean.html").exists());
		CTreeList cTreeList = new CProject(targetDir).getOrCreateCTreeList();
		Assert.assertEquals(4, cTreeList.size());
		File directory0 = cTreeList.get(0).getDirectory();
		Assert.assertTrue(new File(directory0, "landingPage.html").exists());
		Assert.assertTrue(new File(directory0, "rawFullText.html").exists());
		Assert.assertTrue(new File(directory0, "scholarly.html").exists());
		Assert.assertTrue(new File(directory0, "scrapedMetadata.html").exists());
	}
	
	@Test
	public void testSections() {
		File projectDir = new File(DOWNLOAD_DIR,  "testsearch4");
		Assert.assertTrue(projectDir.toString(), projectDir.exists());
		String command = ""
				+ "-p "+projectDir+""
				+ " section"
				;
		//AMISectionTool sectionTool = new AMISectionTool().runCommands(command);
		AMI.execute(command);
	}

	@Test
	@Ignore // why is this here?
	public void testSearch() {
		File projectDir = new File(DOWNLOAD_DIR,  "testsearch99");
		Assert.assertTrue(projectDir.toString(), projectDir.exists());
		String command = ""
				+ "-p "+projectDir+""
				+ " search"
				+ " --dictionary country disease funders species"
				;
//		AMISearchTool searchTool = new AMISearchTool();
//		searchTool.runCommands(command);
		AMI.execute(command);
	}

	@Test
	/** issues a search  and turns results into hitList
	 * 
	 * LONG 60
	 */
	public void testBiorxivSearchHitListLargeIT() throws IOException {
		int pagesize = 3;
		int pages = 2;
		File targetDir = new File("target/biorxiv/testsearch" + pagesize);
		FileUtils.deleteQuietly(targetDir);
		CProject cProject = new CProject(targetDir).cleanAllTrees();
		cProject.cleanAllTrees();
		cProject.getOrCreateExistingMetadataDir();
		String args = 
				"-p " + cProject.toString()
				+ " download"
				+ " --site biorxiv"
				+ " --query climate change"
				+ " --metadata __metadata"
// filetypes to download				
				+ " --rawfiletypes html pdf"
				+ " --pagesize " + pagesize
				+ " --pages 1 " + pages
//				+ " --limit " + (pagesize * pages)
//				+ " --resultset hitList1.clean.html"
			;
//		AMIDownloadTool downloadTool = new AMIDownloadTool();
//		downloadTool.runCommands(args);
		AMI.execute(args);
	}

	
	@Test
	/** issues a search  and turns results into hitList
	 * 
	 */
	@Ignore // HTML DTD problem 
	public void testHALSearchHitList() throws IOException {
		File targetDir = new File("target/hal/testsearch4");
		FileUtils.deleteQuietly(targetDir);
		CProject cProject = new CProject(targetDir).cleanAllTrees();
		cProject.cleanAllTrees();
		String args = 
				"-p " + cProject.toString()
				+ " download"
				+ " --site hal"
				+ " --query permafrost"
				+ " --metadata __metadata"
				+ " --rawfiletypes html"
				+ " --pagesize 4"
				+ " --pages 1 1"
				+ " --limit 4"
				+ " --resultset hitList1.clean.html"
			;
		AMIDownloadTool downloadTool = AMI.execute(AMIDownloadTool.class, args);
		Assert.assertTrue(new File(targetDir, "__metadata/hitList1.html").exists());
		Assert.assertTrue(new File(targetDir, "__metadata/hitList1.clean.html").exists());
		CTreeList cTreeList = new CProject(targetDir).getOrCreateCTreeList();
		Assert.assertEquals(4, cTreeList.size());
		File directory0 = cTreeList.get(0).getDirectory();
		Assert.assertTrue(new File(directory0, "landingPage.html").exists());
		Assert.assertTrue(new File(directory0, "rawFullText.html").exists());
		Assert.assertTrue(new File(directory0, "scholarly.html").exists());
		Assert.assertTrue(new File(directory0, "scrapedMetadata.html").exists());
//		https://hal-sde.archives-ouvertes.fr/search/index/?q=permafrost&submit=&docType_s%5B%5D=ART&docType_s%5B%5D=COMM&docType_s%5B%5D=OUV&docType_s%5B%5D=COUV&docType_s%5B%5D=DOUV&docType_s%5B%5D=OTHER&docType_s%5B%5D=UNDEFINED&docType_s%5B%5D=REPORT&docType_s%5B%5D=THESE&docType_s%5B%5D=HDR&docType_s%5B%5D=LECTURE&submitType_s%5B%5D=file
			
	}
	
	// ============== Scielo =========
	/** extract result set. Very messy. seems that each result consists of two tables,
	 * the second table contains another table.
	 * 
   <center>
    <table width="600" border="0" cellpadding="0" cellspacing="0">
     <tbody>
      <tr>
       <td>
        <hr width="600"/>
        <font face="Verdana" size="1">
         <b>1 / 280</b>
        </font>
       </td>
      </tr>
     </tbody>
    </table>
   </center>
   <center>
    <table width="600" border="0" cellpadding="0" cellspacing="0">
     <tbody>
      <tr>
       <td align="left" width="115" valign="top" rowspan="6">
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
         <tbody>
          <tr>
           <td width="28%">
            <input type="checkbox" name="listChecked" value="^m13555628^h4"/>
           </td>
           <td width="72%">
            <font face="verdana" size="1">
             <i>select</i>
            </font>
           </td>
          </tr>
          <tr>
           <td width="28%">
            <input type="image" name="toprint^m13555628" src="/iah/I/image/toprint.gif" border="0"/>
           </td>
           <td width="72%">
            <font face="verdana" size="1">
             <i>to print</i>
            </font>
           </td>
          </tr>
         </tbody>
        </table>
       </td>
       <td width="485">
        <!-- formato de apresentacao da base -->
        <table>
         <tbody>
== BIB == <tr>
           <td width="15%"> </td>
           <td>
            <font class="isoref" size="-1">Salgueiro, João Hipólito Paiva de Britto et al. 
             <font class="negrito" size="-1">Influence of oceanic-atmospheric interactions on extreme events of daily rainfall in the Sub-basin 39 located in Northeastern Brazil</font>. 
== BIB ==    <i>RBRH</i>, Dec 2016, vol.21, no.4, p.685-693. ISSN 2318-0331
             <br/>
            </font>
            <div align="left">
             <font class="isoref" size="-1">
              <font face="Symbol" color="#000080" size="1">·</font>
*==ABSTR EN== <a class="isoref" href="http://www.scielo.br/scielo.php?script=sci_abstract&amp;pid=S2318-03312016000400685&amp;lng=en&amp;nrm=iso&amp;tlng=en">abstract in english</a>
             </font> | 
             <a class="isoref" href="http://www.scielo.br/scielo.php?script=sci_abstract&amp;pid=S2318-03312016000400685&amp;lng=en&amp;nrm=iso&amp;tlng=pt">portuguese</a>
             <font face="Symbol" color="#000080" size="1">·</font>
*==URL EN==  <a class="isoref" href="http://www.scielo.br/scielo.php?script=sci_arttext&amp;pid=S2318-03312016000400685&amp;lng=en&amp;nrm=iso">text in english</a>
            </div>
           </td>
          </tr>
         </tbody>
        </table>
       </td>
      </tr>
     </tbody>
    </table>
   </center>
*/

	
	/** Download next page(s) ...
        <input type="image" name="Page1" src="/iah/I/image/1red.gif" width="6" height="15" border="0"/>
        <input type="image" name="Page2" src="/iah/I/image/2.gif" width="6" height="15" border="0"/>
...
        <input type="image" name="Page10" src="/iah/I/image/1.gif" width="6" height="15" border="0"/>
        <input type="image" name="Page10" src="/iah/I/image/0.gif" width="6" height="15" border="0"/>
        <input type="image" name="Page11" src="/iah/I/image/right.gif" border="0" width="17" height="17"/>
        <input type="image" name="Page28" src="/iah/I/image/last.gif" border="0" width="17" height="17"/>
	 */
	
	@Test 
	public void testCreateHitList() {
		File hitListClean1 = new File("src/test/resources/org/contentmine/ami/tools/download/scielo/hitList1.mid.html");
		Assert.assertTrue("hitList1.mid", hitListClean1.exists());
		Element hitList1mid = XMLUtil.parseQuietlyToRootElement(hitListClean1);
/**
  <center>
    <table width="600" border="0" cellpadding="0" cellspacing="0">
     <tbody>
      <tr>
       <td align="left" width="115" valign="top" rowspan="6">
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
         <tbody>
          <tr>
           <td width="28%">
            <input type="checkbox" name="listChecked" value="^m13555628^h4"/>
           </td>
           <td width="72%">
           
                      <td width="28%">
            <input type="checkbox" name="listChecked" value="^m13554408^h5"/>
           </td>

*/
//		tbody xmlns="">
//		   <tr>
//		    <td width="15%"> </td>
//		    <td>
//		     <font class="isoref" 
		List<Element> biblioList = XMLUtil.getQueryElements(hitList1mid, ".//tbody/tr/td//.[font[@class='negrito']]");
		Assert.assertEquals("biblio", 10, biblioList.size());
		Element hitListUl = new HtmlUl();
		for (Element biblio : biblioList) {
			HtmlLi li = new HtmlLi();
			hitListUl.appendChild(li);
			biblio.detach();
			li.appendChild(biblio);
		}
		XMLUtil.writeQuietly(hitListUl, new File("target/scielo/ul.html"), 1);
		
		System.out.println("B "+biblioList.size());
		
	}

//	https://www.infoq.com/articles/headless-selenium-browsers/
		

	@Test
	public void testAMISearch() {
		File testSearch3Dir = new File(DOWNLOAD_DIR, "testsearch3");
		Assert.assertTrue(testSearch3Dir.exists());
		CProject cProject = new CProject(testSearch3Dir);
		String cmd = ""
				+ "-p " + cProject + ""
				+ " search"
				+ " --dictionary country"
				+ "";
		//new AMISearchTool().runCommands(cmd);
		AMI.execute(cmd);
		CTree cTree = cProject.getCTreeByName("10_1101_2020_01_12_903427v1");
		Assert.assertTrue(cTree.getDirectory().exists());
	}


	@Test
	@Ignore // too long
	public void testDownloadAndSearchLongIT() {
		File testSearch3Dir = new File(DOWNLOAD_DIR, "testsearch50");
		CProject cProject = new CProject(testSearch3Dir);
		int pagesize = 50;
		int pages = 1;
		String args = 
				"-p " + cProject.toString()
				+ " download"
				+ " --site biorxiv"
				+ " --query climate change"
				+ " --metadata __metadata"
				+ " --rawfiletypes html"
				+ " --pagesize " + pagesize
				+ " --pages 1 " + pages
			;
		AMI.execute(args);
		String cmd = ""
				+ "-p " + cProject + ""
				+ " search"
				+ " --dictionary country disease funders"
				+ "";
		//new AMISearchTool().runCommands(cmd);
		AMI.execute(cmd);
//		CTree cTree = cProject.getCTreeByName("10_1101_2020_01_12_903427v1");
//		Assert.assertTrue(cTree.getDirectory().exists());
	}

	@Test
	public void testChromeDriver() {
		
		String chromeDriverPath = CHROMEDRIVER ;
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
		WebDriver driver = new ChromeDriver(options);	
		driver.get("http://europepmc.org/article/MED/32211289");
		WebElement webElement = driver.findElement(By.xpath("/*"));
		Element root = new WebDriverXom().createXomElement(webElement);
		System.out.println(root.toXML());
		XMLUtil.writeQuietly(root, new File("target/junk.xml"), 1);
	}
	


	// ====private====
	
	private String runCurlProcess(String url) throws IOException {
		String[] command = new String[] {"curl", "-X", "GET", url};
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();
		String result = String.join("\n", IOUtils.readLines(process.getInputStream(), CMineUtil.UTF8_CHARSET));
		int exitCode = process.exitValue();
		return result;
	}
	

}
