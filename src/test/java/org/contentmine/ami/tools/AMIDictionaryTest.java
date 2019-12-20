package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIDictionaryTool;
import org.contentmine.ami.tools.AMIDictionaryTool.DictionaryFileFormat;
import org.contentmine.ami.tools.AbstractAMITool;
import org.contentmine.norma.NAConstants;
import org.junit.Test;

import junit.framework.Assert;

/** tests AMIDictinary
 * 
 * @author pm286
 *
 */
public class AMIDictionaryTest extends AbstractAMITest {
	private static final Logger LOG = Logger.getLogger(AMIDictionaryTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final File TARGET = new File("target");
	public static final File DICTIONARY_DIR = new File(TARGET, "dictionary");
	

	@Test
	public void testHelp() {
		String args = "--help";
		AMIDictionaryTool.main(args);
	}
	
	@Test
	public void testListSome() {
		String args =
				"display " +
				" --directory src/main/resources/org/contentmine/ami/plugins/dictionary " +
           " --dictionary " + "country crispr disease"
				;
		AMIDictionaryTool.main(args);
	}
	
	@Test
	public void testCreateFungicideTerms() {
		String args =
				"create " +
				" --directory /Users/pm286/ContentMine/dictionary/dictionaries/chem " +
           " --dictionary " + "fungicides2 " +
           " --outformats html,xml " +
           " --terms " + "Abamectin Actinomycin Alexidine Amikacin Amphotericin B Ampicillin"
           		+ " Anidulafungin Antimycin Aureobasidin Azithromycin Azoxystrobin Aztreonam"
           		+ " Bacitracin Benomyl Benznidazole Bifonazole Bleomycin Boscalid"
           		+ " Brassinin Brefeldin Calcofluor White Camptothecin Captan carbapenems Carbendazim Carbenicillin"
           		+ " Carboxin Caspofungin Cefotaxime Ceftazidime Ceftriaxone Cefuroxime Cefuroximel Cephalexin"
           		+ " Cercosporamide Chloramphenicol Chlorothalonil Ciprofloxacin Closantel Colistin Copper "
           		+ " Copper_sulphate Cycloheximide Cyclosporine Cyproconazole Daptomycin Diethofencarb"
           		+ " Difenoconazole Diniconazole Doxycycline Eflornithine Emamectin Epoxyconazole"
           		+ " Ethambutol Farnesol Fenarimol Fenhexamid Fenpropidin Fluconazole Flucytosine"
           		+ " Fludioxonil Flutriafol Gentamicin Gramicidin Hydrogen_peroxide Hygromycin"
           		+ " Hymexazol Imipenem Iprodione Isoniazid Itraconazole Ketoconazole Latrunculin"
           		+ " Leptomycin B Lincomycin Linezolid Mancozeb Mecillinam Meropenem Micafungin"
           		+ " Miconazole Miltefosine Moxifloxacin Myriocin Naftifine Nalidixic_acid Neomycin"
           		+ " Nifurtimox Nikkomycin Nisin nitrofurantoin Norfloxacin Novobiocin Nystatin"
           		+ " Oligomycin Oxacillin Oxolinic_acid Oxytetracycline Paromomycin Penicillin"
           		+ " Pentamidine Phenamacril phosphomycin Plumbagin Polymyxin_B1 posaconazole"
           		+ " Prochloraz Propiconazole Pyrimethanil Rapamycin Resveratrol Rifampicin"
           		+ " Rifampin Rose Bengal Rotenone Salicylhydroxamic Sordarin Spectinomycin"
           		+ " Spiroxamine Streptomycin sulfate Strobilurin Sulbactam Tebuconazole"
           		+ " Teicoplanin Telithromycin terbinafine Tetracycline Thiabendazole Thiophanate-methyl"
           		+ " Tiamulin Ticarcillin Tigecycline Tobramycin Triadimefon Trichostatin Triclabendazole"
           		+ " Triclosan Tricyclazole Tridemorph Trimethoprim Tunicamycin Tylosin Valinomycin Vancomycin"
           		+ " Verapamil Vinclozolin Virginiamycin Voriconazole"
				;
		AMIDictionaryTool.main(args);
	}
	
//		@Test
//		public void testCreateWheatVarieties() {
//			String args =
//					"create " +
//					"--directory /Users/pm286/ContentMine/dictionary/dictionaries/plants " +
//           " --dictionary " + "wheatcultivars " +
//           " --outformats html,xml " +
//           " --terms Access Alchemy Ambrosia Amplify Arran Asagai Atlanta Bantam Battalion Belgrade Beluga Benedict Bennington Bentley Biscay Britannia Brompton Brunel Buchan Butler Buzzer Carlton Cassius Chardonnay Charger Chatsworth Chilton Chronicle Claire Cocoon Conqueror Consort Contender Cordiale Coronation Costello Cougar Crusoe Cubanita Dart Deben Defender Delphi Denman Dickens Dickson Director Dover Dunston Duxford Edmunds Einstein Elation Elicit Energise Equinox Evolution Exeter Fastnet Freiston Fugue Gallant Gatsby Gladiator Glasgow Gleam Goldengun Goodwill Grafton Graham Gravitas Gulliver Hardwicke Havana Hereford Hereward Heritage Horatio Humber Hurley Hyperion Icebreaker Icon Invicta Istabraq JB Diego Jorvik KWS Barrel KWS Basset KWS Bonham KWS Cashel KWS Cleveland KWS Crispin KWS Croft KWS Curlew KWS Dali KWS Evoke KWS Extase KWS Firefly KWS Gator KWS Gymnast KWS Horizon KWS Jackal KWS Kerrin KWS Kielder KWS Lili KWS Luther KWS Podium KWS Quartz KWS Rowan KWS Santiago KWS Saxtead KWS Silverstone KWS Siskin KWS Solo KWS Sterling KWS Target KWS Tempo KWS Trinity KWS Yaris KWS Zyatt Ketchum Kingdom Kipling LG Bletchley LG Cassidy LG Detroit LG Generation LG Interstellar LG Jigsaw LG Motown LG Rhythm LG Sabertooth LG Skyscraper LG Spotlight LG Sundance Lancaster Lear Leeds Limerick Macro Madrigal Malacca Marksman Marlowe Marston Mascot Monterey Monty Mosaic Moulton Musketeer Myriad Napier Nijinsky Oakley Option Orbit Panacea Panorama Pennant Phlebas Piranha QI Qplus Quest RGT Adventure RGT Conversion RGT Gravity RGT Illustrious RGT Knightsbridge RGT Marlborough RGT Paddington RGT Universe RGT Westminster Reflection Relay Revelation Riband Richmond Robigus Rocky Ruskin SW Tataros SY Epson SY Loki SY Medea Sahara Savannah Savello Scorpion 25 Scout Senator Shabras Shamrock Sherlock Shire Shogun Skyfall Smuggler Soissons Solace Solstice Spyder Steadfast Stigg Stratosphere Tanker Tellus Timber Torch Torphins Trident Tuxedo Twister Vector Velocity Verso Viscount Walpole Warlock 24 Warrior Weaver Welford Wizard Xi19 Zebedee",
//
//					;
//			AMIDictionaryTool.main(args);
//		}

//		@Test
//		public void testCreateWheatResistanceGenes() {
//			String args =
//					"create " +
//					"--directory /Users/pm286/ContentMine/dictionary/dictionaries/plants " +
//           " --dictionary " + "wheatrustresist " +
//           " --outformats html,xml " +
//           " --terms " +
////					"Yr1(2AL)","Yr2(7B)","Yr3a-c(1B)","Yr4a-b(6B)","Yr6(7BS)","Yr7(2BL)","Yr11","Yr12","Yr13","Yr14","Yr16(2D)","Yr18(7DS)","Yr19(5B)","Yr20(6D)","Yr21(1B)","Yr22(4D)","Yr23(6D)","Yr24(1BS)","Yr25(1D)","Yr27+Lr13(2BS)","Yr29+Lr46(1BL)","Yr30+Sr2+Lr27(3BS)","Yr31(2BS)","Yr32(2BS)","Yr41(2B)"
//					"Yr1","Yr2","Yr3a-c","Yr4a-b","Yr5","Yr6","Yr7","Yr11","Yr12","Yr13","Yr14","Yr16","Yr18","Yr19","Yr20","Yr21","Yr22","Yr23","Yr24","Yr25","Yr27","Lr13","Yr29","Lr46","Yr30","Yr31","Yr32","Yr41"
//
//					;
//			AMIDictionaryTool.main(args);
//		}


	@Test
	public void testWikipediaTables() throws IOException {
		String dict = " chem.protpredict";
		String args =
			"create " +
           " --input https://en.wikipedia.org/wiki/List_of_protein_structure_prediction_software " +
           " --informat wikitable " +
           " --namecol Name " +
			"--linkcol Name " +
           " --urlcol Link " +
           " --outformats xml,json,html " +
           " --dictionary " +dict
			;
		AbstractAMITool amiDictionary = new AMIDictionaryTool();
		amiDictionary.runCommands(args);
//		XMLUtil.debug(amiDictionary.getSimpleDictionary(), new File(DICTIONARY_DIR, dict+".html"), 1);
		
	}
	
	@Test
	public void testWikipediaTables2() throws IOException {
		String dict = "soc.socialnetwork";
		String args =
			"create " +
           " --input https://en.wikipedia.org/wiki/List_of_social_networking_websites " +
           " --informat wikitable " +
           " --namecol Name " +
			"--linkcol Name " +
           " --outformats xml,json,html " +
           " --dictionary socialnetwork";
		AbstractAMITool amiDictionary = new AMIDictionaryTool();
		amiDictionary.runCommands(args);
	}
	
	@Test
	public void testWikipediaPage() throws IOException {
		String dict = "chem.proteinStructure";
		String args =
			"create " +
           " --input https://en.wikipedia.org/wiki/Protein_structure " +
           " --informat wikipage " +
           " --outformats xml,json,html " +
           " --dictionary " +dict;
		new AMIDictionaryTool().runCommands(args);
	}

	@Test
	// LONG
	public void testWikipediaPageAedesIT() throws IOException {
		String dict = "animal.aedes";
		String args =
			"create " +
           " --input https://en.wikipedia.org/wiki/Aedes_aegypti " +
           " --informat wikipage " +
		   " --dictionary " +dict +
           " --outformats xml,json,html " +
           " --directory " +DICTIONARY_DIR.toString()
			;
		new AMIDictionaryTool().runCommands(args);
	}

	@Test
	// LONG
	public void testWikipediaChildhoodObesityIT() throws IOException {
		String dict = "med.childhoodobesity";
		String col = "Condition";
		String args =
			"create " +
           " --input https://en.wikipedia.org/wiki/Childhood_obesity " +
           " --informat wikitable" +
           " --namecol " + col +
		   " --dictionary " + dict +
           " --outformats xml,json,html " +
           " --directory " +DICTIONARY_DIR.toString()
			;
		// ami-dictionaries create -i https://en.wikipedia.org/wiki/Aedes_aegypti --informat wikipage --hreftext --dictionary aedes0 --outformats xml --directory ~/ContentMine/dictionary/
		new AMIDictionaryTool().runCommands(args);
	}

	@Test
	public void testWikipediaPageReindeerIT() throws IOException {
		String dict = "animal.reindeer";
		String args =
			"create " +
           " --input https://en.wikipedia.org/wiki/Category:Reindeer " +
			"--informat wikipage " +
			"--outformats xml,json,html " +
           " --dictionary " + dict +
           " --directory " +DICTIONARY_DIR.toString()
			;
		new AMIDictionaryTool().runCommands(args);
	}
	@Test
	// LONG
	public void testWikipediaPageMonoterpenesIT() throws IOException {
		String dict = "chem.monoterpenes";
		String args =
			"create " +
           " --input https://en.wikipedia.org/wiki/Category:Monoterpenes " +
			"--informat wikipage " +
			"--outformats xml,json,html " +
           " --dictionary " + dict +
           " --directory " +DICTIONARY_DIR.toString()
			;
		new AMIDictionaryTool().runCommands(args);
	}

	@Test
	public void testWikipediaNTDs() throws IOException {
		String dict = "med.ntd";
		String whoCol = ".*WHO.*CDC.*";
		String args =
			"create " +
           " --input https://en.wikipedia.org/wiki/Neglected_tropical_diseases " +
           " --informat wikitable " +
           " --namecol " +whoCol +
           " --linkcol " +whoCol +
           " --base http://en.wikipedia.org " +
           " --outformats xml,json,html " +
           " --dictionary " +dict +
           " --directory " +DICTIONARY_DIR.toString()
			;
		new AMIDictionaryTool().runCommands(args);
	}
	
	@Test
	public void testWikipediaNTDPLOS() throws IOException {
		String dict = "med.ntd1";
		String searchCol = "PLOS.*";
		String args =
			"create " +
           " --input https://en.wikipedia.org/wiki/Neglected_tropical_diseases " +
           " --informat wikitable " +
           " --namecol " +searchCol +
           " --linkcol " +searchCol +
           " --dictionary " + dict +
           " --outformats xml,json,html " +
           " --directory " +DICTIONARY_DIR.toString()
			;
		new AMIDictionaryTool().runCommands(args);
	}
	
	@Test
	public void testWikipediaHumanInsectVectorsIT() throws IOException {
		String dict = "animal.insectvectorshuman";
		String args =
			"create " +
           " --input https://en.wikipedia.org/wiki/Category:Insect_vectors_of_human_pathogens " +
           " --informat wikicategory " +
           " --dictionary " +dict +
           " --outformats xml,json,html " +
           " --directory " +DICTIONARY_DIR.toString()
			;
		new AMIDictionaryTool().runCommands(args);
	}
	
	
	@Test
	public void testWikipediaOcimumIT() throws IOException {
		String dict = "plants.ocimumten";
		String args =
			"create " +
           " --input https://en.wikipedia.org/wiki/Ocimum_tenuiflorum " +
           " --informat wikipage " +
           " --hreftext " + // currently needed to enforce use of names
			"--dictionary " +dict +
           " --outformats xml,json,html " +
           " --directory " +DICTIONARY_DIR.toString()
			;
		new AMIDictionaryTool().runCommands(args);
	}
	
	@Test
	public void testCreateFromTerms() {
		String dict = "phys.crystalsystem";
		String args =
			"create " +
           " --terms cubic,tetragonal,hexagonal,trigonal,orthorhombic,monoclinic,triclinic " +
           " --dictionary " +dict +
           " --directory " +DICTIONARY_DIR.toString();
		new AMIDictionaryTool().runCommands(args);
	}
	
	@Test
	public void testReadMammalsCSV() throws IOException {
		File mammalsCSV = new File(NAConstants.PLUGINS_DICTIONARY_DIR, "EDGEMammalsSmall.csv");
		
		String dict = "animal.edgemammals";
		String args =
			"create " +
           " --input " +mammalsCSV.getAbsolutePath() +
           " --informat csv " +
           " --termcol Species " +
           " --namecol Common names " +
           " --hrefcols IUCN Red List link " +
           " --datacols ED Score,GE Score " +
           " --dictionary " +dict +
           " --outformats xml,html,json " +
           " --directory " +DICTIONARY_DIR.toString() +
           " --booleanquery"
			;
		new AMIDictionaryTool().runCommands(args);
		
	}

	@Test
	public void testWikipediaConservation1() throws IOException {
		String dict = "bio.conservation";
		String args =
			"create " +
           " --hreftext " + // currently needed to enforce use of names
			"--input https://en.wikipedia.org/wiki/Conservation_biology " +
           " --informat wikipage " +
           " --dictionary " +dict +
           " --directory target/dictionary " +
           " --outformats " +DictionaryFileFormat.xml.toString() +
           " --log4j org.contentmine.ami.lookups.WikipediaDictionary INFO " +
           " --log4j org.contentmine.norma.input.html.HtmlCleaner INFO"
			;
		new AMIDictionaryTool().runCommands(args);
	}

	@Test
	public void testWikipediaIndianSpice() throws IOException {
		String dict = "plants.spice";
		String searchCol = "Standard English";
		String args =
			"create " +
           " --input https://en.wikipedia.org/wiki/List_of_Indian_spices " +
           " --informat wikitable " +
           " --namecol " +searchCol +
           " --dictionary " +dict +
           " --outformats xml,json,html " +
           " --directory " +DICTIONARY_DIR.toString()
			;
		new AMIDictionaryTool().runCommands(args);
	}
	
	@Test
	public void testWikipediaPlantVirus() throws IOException {
		String dict = "plants.virus";
		String args =
			"create " +
           " --hreftext " + // currently needed to enforce use of names
			"--input https://en.wikipedia.org/wiki/Plant_virus " +
           " --informat wikipage " +
           " --dictionary " +dict +
           " --outformats html,json,xml " +
           " --wikilinks"
			;
		new AMIDictionaryTool().runCommands(args);
	}

	@Test
	public void testCreateVirusesFromTerms() {
		String dict = "plants.viruses";
		String args =
			"create " +
           " --terms Cucumovirus,Tymovirus,Bromovirus,Potexvirus,Ilarvirus,Nepovirus,Carmovirus,Potyvirus,Potyvirus,"
					+ "Badnavirus,Tymovirus,Tobravirus,Closterovirus,Necrovirus,TNsatV-like satellite,Nepovirus,Nepovirus,"
					+ "Nepovirus,Ilarvirus,Comovirus,Dianthovirus,Carlavirus,Sobemovirus,Caulimovirus,Enamovirus,Cytorhabdovirus,"
					+ "Ophiovirus,Cocadviroid,Aureusvirus,Ilarvirus,Bromovirus,Tungrovirus,Waikavirus,Sobemovirus,Alphacryptovirus,"
					+ "Potyvirus,Potexvirus,Tospovirus,Cavemovirus,Potyvirus,Carlavirus,Mastrevirus,Petuvirus,Potexvirus,Carmovirus,"
					+ "Carmovirus,Nucleorhabdovirus,Ampelovirus " +
           " --dictionary " +dict +
           " --directory " +DICTIONARY_DIR.toString() +
           " --wikilinks wikidata"
;
		new AMIDictionaryTool().runCommands(args);
	}
	
	@Test
	public void testTranslateJSONtoXML() {
		String args =
			"translate " +
           " --directory src/test/resources/org/contentmine/ami/dictionary " +
           " --dictionary alliaceae.json buxales.json " +
           " --outformats xml"
		;
		new AMIDictionaryTool().runCommands(args);
	}
	
	@Test
	public void testTranslateJSONtoXMLAbsolute() {
		String args =
			"translate" +
           " --dictionary src/test/resources/org/contentmine/ami/dictionary/alliaceae.json " +
			                "src/test/resources/org/contentmine/ami/dictionary/buxales.json " +
           " --outformats xml"
		;
		new AMIDictionaryTool().runCommands(args);
	}
	
	@Test
	public void testTranslateJSONtoXMLAbsoluteWikidata() {
		String args =
			"translate " +
           " --dictionary src/test/resources/org/contentmine/ami/dictionary/alliaceae.json " +
			                "src/test/resources/org/contentmine/ami/dictionary/buxales.json " +
           " --outformats xml " +
           " --wikilinks wikidata wikipedia"
		;
		new AMIDictionaryTool().runCommands(args);
	}
	
	@Test
	public void testWikidataLookup() {
		String dict = "plants.misc";
		String args =
			"create " +
           " --terms Buxus sempervirens " +
           " --dictionary " + dict +
           " --directory " + DICTIONARY_DIR.toString() +
           " --wikilinks wikidata"
			;
		new AMIDictionaryTool().runCommands(args);
	}

	@Test
	public void testAmbarishBugWikipediaDictionaryCreation() {
		String dict = "plants.misc";
		String args =
			"create " +
           " --terms Buxus sempervirens " +
           " --dictionary " +dict +
           " --directory " + DICTIONARY_DIR.toString() +
           " --wikilinks wikidata"
			;
		new AMIDictionaryTool().runCommands(args);
	}

	@Test
	public void testListOfRiceVarieties() {
		String args = "create"
				+ " --input https://en.wikipedia.org/wiki/List_of_rice_varieties"
				+ " --informat wikipage --hreftext --dictionary ricevarieties --outformats xml,json,html";
		new AMIDictionaryTool().runCommands(args);
	}
	
	@Test
	public void testWikipediaPageOcimum() {
		String args = "create"
				+ " --informat wikipage"
				+ " --hreftext"
				+ " --input https://en.wikipedia.org/wiki/Ocimum_tenuiflorum"
				+ " --dictionary otenuiflorum"
				+ " --directory mydictionaries"
				+ " --outformats xml,html";
		new AMIDictionaryTool().runCommands(args);

	}
	@Test
	public void testDictionarySearch() {
		String args = "search"
				+ " --dictionary "+CEV+"/dictionary/compound/compound.xml"
//				+ " --search thymol carvacrol"
				+ " --searchfile "+CEV_SEARCH+"/oil186/__tables/compound_set.txt"
				+ "";
		new AMIDictionaryTool().runCommands(args);

	}

	
}
