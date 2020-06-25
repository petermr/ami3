package org.contentmine.ami.lookups;

import java.util.List;

import nu.xom.Element;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.eclipse.jetty.util.log.Log;
import org.contentmine.eucl.xml.XMLUtil;

/**
 * analyzes the XML returned by a Genbank search.
 * 
 * @author pm286
 *

 */
public class GenbankResultAnalyzer {

	private static final Logger LOG = LogManager.getLogger(GenbankResultAnalyzer.class);
public final static String GBSEQ_TAXON_XPATH = "//GBSet/GBSeq/GBSeq_feature-table/GBFeature/GBFeature_quals/GBQualifier/GBQualifier_value[starts-with(.,'taxon:')]";
	public final static String GBSEQ_ORGANISM_XPATH = "//GBSet/GBSeq/GBSeq_organism";
	public final static String ESEARCH_ID_XPATH = "//eSearchResult/IdList/Id";

	private Element gbseqElement;
	private Element taxonomyElement;

	public GenbankResultAnalyzer() {
	}
	
	/**
GBSet>
<GBSeq>
  <GBSeq_locus>EU379932</GBSeq_locus>
  <GBSeq_length>5066</GBSeq_length>
  <GBSeq_strandedness>double</GBSeq_strandedness>
  <GBSeq_moltype>DNA</GBSeq_moltype>
  <GBSeq_topology>linear</GBSeq_topology>
  <GBSeq_division>BCT</GBSeq_division>
  <GBSeq_update-date>05-MAY-2009</GBSeq_update-date>
  <GBSeq_create-date>24-FEB-2008</GBSeq_create-date>
  <GBSeq_definition>Synergistes sp. W5455 16S ribosomal RNA gene, 16S-23S ribosomal RNA intergenic spacer, 23S ribosomal RNA gene, 23S-5S ribosomal RNA intergenic spacer, and 5S ribosomal RNA gene, complete sequence</GBSeq_definition>
  <GBSeq_primary-accession>EU379932</GBSeq_primary-accession>
  <GBSeq_accession-version>EU379932.1</GBSeq_accession-version>
  <GBSeq_other-seqids>
    <GBSeqid>gb|EU379932.1|</GBSeqid>
    <GBSeqid>gi|167996909</GBSeqid>
  </GBSeq_other-seqids>
  <GBSeq_source>Pyramidobacter piscolens W5455</GBSeq_source>
  <GBSeq_organism>Pyramidobacter piscolens W5455</GBSeq_organism>
  <GBSeq_taxonomy>Bacteria; Synergistetes; Synergistia; Synergistales; Synergistaceae; Pyramidobacter</GBSeq_taxonomy>
  <GBSeq_references>
  
    <GBReference>
      <GBReference_reference>1</GBReference_reference>
      <GBReference_position>1..5066</GBReference_position>
      <GBReference_authors>
        <GBAuthor>Downes,J.</GBAuthor>
        <GBAuthor>Vartoukian,S.R.</GBAuthor>
        <GBAuthor>Dewhirst,F.E.</GBAuthor>
        <GBAuthor>Izard,J.</GBAuthor>
        <GBAuthor>Chen,T.</GBAuthor>
        <GBAuthor>Yu,W.H.</GBAuthor>
        <GBAuthor>Sutcliffe,I.C.</GBAuthor>
        <GBAuthor>Wade,W.G.</GBAuthor>
      </GBReference_authors>
      <GBReference_title>Pyramidobacter piscolens gen. nov., sp. nov., a member of the phylum 'Synergistetes' isolated from the human oral cavity</GBReference_title>
      <GBReference_journal>Int. J. Syst. Evol. Microbiol. 59 (PT 5), 972-980 (2009)</GBReference_journal>
      <GBReference_xref>
        <GBXref>
          <GBXref_dbname>doi</GBXref_dbname>
          <GBXref_id>10.1099/ijs.0.000364-0</GBXref_id>
        </GBXref>
      </GBReference_xref>
      <GBReference_pubmed>19406777</GBReference_pubmed>
    </GBReference>
    
    <GBReference>
      <GBReference_reference>2</GBReference_reference>
      <GBReference_position>1..5066</GBReference_position>
      <GBReference_authors>
        <GBAuthor>Dewhirst,F.E.</GBAuthor>
      </GBReference_authors>
      <GBReference_title>Direct Submission</GBReference_title>
      <GBReference_journal>Submitted (09-JAN-2008) Molecular Genetics, The Forsyth Institute, 140 The Fenway, Boston, MA 02115, USA</GBReference_journal>
    </GBReference>
  </GBSeq_references>
  <GBSeq_feature-table>
    <GBFeature>
      <GBFeature_key>source</GBFeature_key>
      <GBFeature_location>1..5066</GBFeature_location>
      <GBFeature_intervals>
        <GBInterval>
          <GBInterval_from>1</GBInterval_from>
          <GBInterval_to>5066</GBInterval_to>
          <GBInterval_accession>EU379932.1</GBInterval_accession>
        </GBInterval>
      </GBFeature_intervals>
      <GBFeature_quals>
        <GBQualifier>
          <GBQualifier_name>organism</GBQualifier_name>
          <GBQualifier_value>Pyramidobacter piscolens W5455</GBQualifier_value>
        </GBQualifier>
        <GBQualifier>
          <GBQualifier_name>mol_type</GBQualifier_name>
          <GBQualifier_value>genomic DNA</GBQualifier_value>
        </GBQualifier>
        <GBQualifier>
          <GBQualifier_name>isolate</GBQualifier_name>
          <GBQualifier_value>W5455</GBQualifier_value>
        </GBQualifier>
        <GBQualifier>
          <GBQualifier_name>db_xref</GBQualifier_name>
          <GBQualifier_value>taxon:352165</GBQualifier_value>
        </GBQualifier>
      </GBFeature_quals>
    </GBFeature>
    <GBFeature>
      <GBFeature_key>rRNA</GBFeature_key>
      <GBFeature_location>75..1596</GBFeature_location>
      <GBFeature_intervals>
        <GBInterval>
          <GBInterval_from>75</GBInterval_from>
          <GBInterval_to>1596</GBInterval_to>
          <GBInterval_accession>EU379932.1</GBInterval_accession>
        </GBInterval>
      </GBFeature_intervals>
      <GBFeature_quals>
        <GBQualifier>
          <GBQualifier_name>product</GBQualifier_name>
          <GBQualifier_value>16S ribosomal RNA</GBQualifier_value>
        </GBQualifier>
      </GBFeature_quals>
    </GBFeature>
    <GBFeature>
      <GBFeature_key>misc_RNA</GBFeature_key>
      <GBFeature_location>1597..1844</GBFeature_location>
      <GBFeature_intervals>
        <GBInterval>
          <GBInterval_from>1597</GBInterval_from>
          <GBInterval_to>1844</GBInterval_to>
          <GBInterval_accession>EU379932.1</GBInterval_accession>
        </GBInterval>
      </GBFeature_intervals>
      <GBFeature_quals>
        <GBQualifier>
          <GBQualifier_name>product</GBQualifier_name>
          <GBQualifier_value>16S-23S ribosomal RNA intergenic spacer</GBQualifier_value>
        </GBQualifier>
      </GBFeature_quals>
    </GBFeature>
    <GBFeature>
      <GBFeature_key>rRNA</GBFeature_key>
      <GBFeature_location>1845..4818</GBFeature_location>
      <GBFeature_intervals>
        <GBInterval>
          <GBInterval_from>1845</GBInterval_from>
          <GBInterval_to>4818</GBInterval_to>
          <GBInterval_accession>EU379932.1</GBInterval_accession>
        </GBInterval>
      </GBFeature_intervals>
      <GBFeature_quals>
        <GBQualifier>
          <GBQualifier_name>product</GBQualifier_name>
          <GBQualifier_value>23S ribosomal RNA</GBQualifier_value>
        </GBQualifier>
      </GBFeature_quals>
    </GBFeature>
    <GBFeature>
      <GBFeature_key>misc_RNA</GBFeature_key>
      <GBFeature_location>4819..4991</GBFeature_location>
      <GBFeature_intervals>
        <GBInterval>
          <GBInterval_from>4819</GBInterval_from>
          <GBInterval_to>4991</GBInterval_to>
          <GBInterval_accession>EU379932.1</GBInterval_accession>
        </GBInterval>
      </GBFeature_intervals>
      <GBFeature_quals>
        <GBQualifier>
          <GBQualifier_name>product</GBQualifier_name>
          <GBQualifier_value>23S-5S ribosomal RNA intergenic spacer</GBQualifier_value>
        </GBQualifier>
      </GBFeature_quals>
    </GBFeature>
    <GBFeature>
      <GBFeature_key>rRNA</GBFeature_key>
      <GBFeature_location>4992..5066</GBFeature_location>
      <GBFeature_intervals>
        <GBInterval>
          <GBInterval_from>4992</GBInterval_from>
          <GBInterval_to>5066</GBInterval_to>
          <GBInterval_accession>EU379932.1</GBInterval_accession>
        </GBInterval>
      </GBFeature_intervals>
      <GBFeature_quals>
        <GBQualifier>
          <GBQualifier_name>product</GBQualifier_name>
          <GBQualifier_value>5S ribosomal RNA</GBQualifier_value>
        </GBQualifier>
      </GBFeature_quals>
    </GBFeature>
  </GBSeq_feature-table>
  <GBSeq_sequence>taccttgacaagag...cacgcgccgatggt</GBSeq_sequence>
</GBSeq>

</GBSet>
	 * @param gbseqXML
	 */
	public void readGBSeq(String gbseqXML) {
		gbseqElement = XMLUtil.stripDTDAndParse(gbseqXML);
	}
	
	public String getTaxonFromGBSeq() {
		String taxon = null;
		if (gbseqElement != null) {
			List<Element> taxonElements = XMLUtil.getQueryElements(gbseqElement, GBSEQ_TAXON_XPATH);
			taxon = taxonElements.size() != 1 ? null : taxonElements.get(0).getValue();
		}
		return taxon;
	}
	
	public String getOrganismFromGBSeq() {
		String organism = null;
		if (gbseqElement != null) {
			List<Element> organismElements = XMLUtil.getQueryElements(gbseqElement, GBSEQ_ORGANISM_XPATH);
			String org = organismElements.size() != 1 ? null : organismElements.get(0).getValue();
			// only take first two words
			if (org != null) {
				String[] parts = org.split("\\s+");
				organism = parts.length == 1 ? parts[0] : parts[0]+" "+parts[1];
			}
		}
		return organism;
	}

	/** taxon search by name in taxonomy db
	<eSearchResult>
		<Count>1</Count>
		<RetMax>1</RetMax>
		<RetStart>0</RetStart>
		<IdList>
			<Id>638849</Id>
		</IdList>
		<TranslationSet />
		<TranslationStack>
			<TermSet>
				<Term>Pyramidobacter piscolens[All Names]</Term>
				<Field>All Names</Field>
				<Count>1</Count>
				<Explode>N</Explode>
			</TermSet>
			<OP>GROUP</OP>
		</TranslationStack>
		<QueryTranslation>Pyramidobacter piscolens[All Names]</QueryTranslation>
	</eSearchResult>
	 * @param taxonomyXML
	 */
	public void readEsearch(String taxonomyXML) {
		taxonomyElement = XMLUtil.stripDTDAndParse(taxonomyXML);
	}
	
	public String getIdFromEsearch() {
		String id = null;
		if (taxonomyElement != null) {
			List<Element> idElements = XMLUtil.getQueryElements(taxonomyElement, ESEARCH_ID_XPATH);
			id = idElements.size() != 1 ? null : idElements.get(0).getValue();
		}
		return id;
	}

}
