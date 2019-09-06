package org.contentmine.svg2xml.words;

import org.apache.log4j.Logger;

public class WordBuilderTest {

	private static final Logger LOG = Logger.getLogger(WordBuilderTest.class);

//	@Test
//	public void testWordList() {
//		GeometryBuilder geometryBuilder = new GeometryBuilder(SVGElement.readAndCreateSVG(new File(SVG2XMLFixtures.BUILDER_DIR, "image.g.8.2.svg")));
//		List<WordNew> wordList = geometryBuilder.getWordList();
//		Assert.assertNotNull(wordList);
//		Assert.assertEquals("words", 16, wordList.size());
//		Assert.assertEquals("words",  "[ Hag L,  Hla L,  Hpi L, 81,  Hag M,  Hla M,  Hpi M, 100,  Ssy L, 99,  Ssy M,  Nle L, 95,  Nle M,  Human L,  Human M]", wordList.toString());
//	}
//	
//
//	@Test
//	public void testWordList1() {
//		GeometryBuilder geometryBuilder = new GeometryBuilder(SVGElement.readAndCreateSVG(new File(SVG2XMLFixtures.BUILDER_DIR, "image.g.3.2a.svg")));
//		List<WordNew> wordList = geometryBuilder.getWordList();
//		Assert.assertNotNull(wordList);
//		Assert.assertEquals("words", 130, wordList.size());
//		Assert.assertEquals("words",  "[Luscinia  , *, Ficedula, *, Turdus, *, Mimus, *, Sturnus, *, 0.5, Troglodytes, *, Sitta, "
//				+ "Regulus, Zosterops, 0.77, *, 0.66, Leiothrix, 0.93, Phylloscopus, Pycnonotus, *, Donacobius, *, Acrocephalus, "
//				+ "0.73, 0.83, Hirundo, Parus, *, Icterus, *, Dendroica, *, Emberiza, *, Serinus, *, 0.71, Motacilla, *, 0.91, "
//				+ "Lonchura, Nectarinia, *, Promerops, Petroica, *, Eopsaltria, Ptiloris, *, Paradisaea, *, Manucodia, *, Pica, *, *, "
//				+ "Corvus, *, Cyanocorax, *, Dicrurus, Rhipidura, *, Vireo, 0.72 a, 5, *, Cyclarhis, 4, *, *, Oriolus, 3, c, Coracina, 0.78, "
//				+ "Toxorhamphus, 0.84, *, 6, Cnemophilus, b 0.94, Philesturnus, Orthonyx, *, Pomatostomus, 0.5, Lichenostomus, b, *, 2, "
//				+ "Amytornis #, *, Gerygone, Ailuroedus, *, *, Sericulus, a, 2, Menura, Tyrannus, *, Myiarchus, *, *, Camptostoma, *, "
//				+ "Onychorhynchus, *, 1, b, Manacus, *, *, Hypocnemis, *, *, Phlegopsis, 1a, Pitta, Acanthisitta, a, b, 1, 2]", 
//				wordList.toString());
//	}
//	
//	@Test
//	public void testWordListAllWords() {
//		GeometryBuilder geometryBuilder = new GeometryBuilder(SVGElement.readAndCreateSVG(new File(SVG2XMLFixtures.BUILDER_DIR, "image.g.3.2.svg")));
//		List<WordNew> wordList = geometryBuilder.getWordList();
//		Assert.assertNotNull(wordList);
//		Assert.assertEquals("words", 410, wordList.size());
//		Assert.assertEquals("words",  "[-, p, Luscinia  , (Muscicapidae), a, *, Ficedula, c, *, i, c, a, (Turdidae), Turdus, s, *, e, u, (Mimidae),"
//				+ " Mimus, d, i, *, M, o, Sturnus, (Sturnidae), *, 0.5, (Troglodytidae), Certhi-, Troglodytes, *, oidea, Sitta, (Sittidae), Regulus,"
//				+ " (Regulidae), Zosterops, (Zosteropidae), 0.77, *, 0.66, (Timaliidae), Leiothrix, a, 0.93, -, d, Phylloscopus, (Phylloscopidae),"
//				+ " i, a, i, r, v, e, l, Pycnonotus, (Pycnonotidae), e, d, y, i, *, s, (Donacobiidae), Donacobius, S, o, s, *, (Acrocephalidae),"
//				+ " Acrocephalus, a, 0.73, 0.83, P, Hirundo, (Hirundinidae), (Paridae), Parus, *, Icterus, (Icteridae), *, Dendroica, (Parulidae),"
//				+ " -, *, r, (Emberizidae), Emberiza, e, *, a, s, (Fringillidae), Serinus, e, *, s, 0.71, d, a, i, Motacilla, (Motacillidae), *,"
//				+ " P, o, s, 0.91, Lonchura, (Estrildidae), e, (Nectariniidae), Nectarinia, n, i, *, (Promeropidae), Promerops, c, Petroica, s,"
//				+ " (Petroicidae), *, Eopsaltria, O, Ptiloris, *, Paradisaea, (Paradisaeidae), *, ', Manucodia, a, *, e, Pica, *, d, *, i, (Corvidae),"
//				+ " Corvus, *, o, Cyanocorax, v, *, r, (Dicruridae), o, Dicrurus, C, Rhipidura, (Rhipiduridae),  , *, e, Vireo, r, 0.72 a, (Vireonidae),"
//				+ " 5, *, o, Cyclarhis, 4, *, c, *, ', Coracina, (Campephagidae), 0.78, Toxorhamphus, (Melanocharitidae), 0.84, *, 6, (Cnemophilidae),"
//				+ " Cnemophilus, b 0.94, Philesturnus, (Callaeidae), Orthonyx, (Orthonychidae), *, (Pomatostomidae), Pomatostomus, 0.5, Lichenostomus,"
//				+ " (Meliphagidae), b, *, 2, Amytornis #, (Maluridae), *, (Acanthizidae), Gerygone, Ailuroedus, *, (Ptilonorhynchidae), *, Sericulus, a,"
//				+ " 2, Menura, (Menuridae), Tyrannus, s, *, e, Myiarchus, (Tyrannidae), *, *, n, Camptostoma, *, i, (Tityridae), c, Onychorhynchus, *,"
//				+ " 1, b, s, (Pipridae), Manacus, *, *, o, Hypocnemis, b, *, (Thamnophilidae), *, Phlegopsis, 1, a, u, (Pittidae), Pitta, S, Acanthisitta,"
//				+ " (Acanthisittidae), 0.07, a, b, 1, 2, 3, 5, ambiguous, 4, 6, UVS          VS, –, ,, VS          UVS, ,, Figure, 2, Phylogenetic,"
//				+ " recontruction, of, SWS1, opsin, evolution., Majority, rule, (50%), consensus, tree, of, passerines, based, on, concatenated,"
//				+ " mitochondrial, cytochrome, b, and, ND2,, nuclear, myoglobin, intron, 2,, ODC, introns, 6, to, 7,, TGFβ, 2, intron, 5,, and,"
//				+ " protein-coding, nuclear, c-myc, exon, 3,, RAG-1, and, RAG-2, sequences, (>, 9, kbp),, inferred, by, Bayesian, inference,,"
//				+ " analysed, in, eight, partitions,, with, two, parrots, and, two, falconiforms, as, outgroup., Posterior, probabilities, given,"
//				+ " at, nodes,, *, indicating, ≥, 0.95., Long, Ailuroedus, branch, truncated., VS/UVS, optimisation, represented, by, violet, for,"
//				+ " VS,, black, for, UVS, and, dotted, for, ambiguous., Transitions, from, one, state, to, another, are, indicated, by, numbers;,"
//				+ " 1a, and, 1b,, and, 2a, and, 2b,, respectively,, represent, uncertainties, due, to, ambiguous, ancestral, state., a,, b, and, c,"
//				+ " refer, to, insignificantly, supported, nodes, discussed, in, the, text., #, Sister, clade,, genus, Malurus, (not, included),,"
//				+ " contains, both, VS, and, UVS, species, [40].]", 
//				wordList.toString());
//	}
//	
//	@Test
//	public void testHorizontalWordOrientation() {
//		GeometryBuilder geometryBuilder = new GeometryBuilder(SVGElement.readAndCreateSVG(new File(SVG2XMLFixtures.BUILDER_DIR, "image.g.3.2.svg")));
//		geometryBuilder.setTextOrientation(TextOrientation.ROT_0);
//		List<WordNew> wordList = geometryBuilder.getWordList();
//		Assert.assertNotNull(wordList);
//		Assert.assertEquals("words", 331, wordList.size());
//		Assert.assertEquals("words",  "[Luscinia  , (Muscicapidae), *, Ficedula, *, (Turdidae), Turdus, *, (Mimidae),"
//				+ " Mimus, *, Sturnus, (Sturnidae), *, 0.5, (Troglodytidae), Certhi-, Troglodytes, *, oidea, Sitta,"
//				+ " (Sittidae), Regulus, (Regulidae), Zosterops, (Zosteropidae), 0.77, *, 0.66, (Timaliidae),"
//				+ " Leiothrix, 0.93, Phylloscopus, (Phylloscopidae), Pycnonotus, (Pycnonotidae), *, (Donacobiidae),"
//				+ " Donacobius, *, (Acrocephalidae), Acrocephalus, 0.73, 0.83, Hirundo, (Hirundinidae), (Paridae), Parus,"
//				+ " *, Icterus, (Icteridae), *, Dendroica, (Parulidae), *, (Emberizidae), Emberiza, *, (Fringillidae),"
//				+ " Serinus, *, 0.71, Motacilla, (Motacillidae), *, 0.91, Lonchura, (Estrildidae), (Nectariniidae),"
//				+ " Nectarinia, *, (Promeropidae), Promerops, Petroica, (Petroicidae), *, Eopsaltria, Ptiloris, *,"
//				+ " Paradisaea, (Paradisaeidae), *, Manucodia, *, Pica, *, *, (Corvidae), Corvus, *, Cyanocorax, *,"
//				+ " (Dicruridae), Dicrurus, Rhipidura, (Rhipiduridae), *, Vireo, 0.72 a, (Vireonidae), 5, *, Cyclarhis,"
//				+ " 4, *, *, Coracina, (Campephagidae), 0.78, Toxorhamphus, (Melanocharitidae), 0.84, *, 6, (Cnemophilidae),"
//				+ " Cnemophilus, b 0.94, Philesturnus, (Callaeidae), Orthonyx, (Orthonychidae), *, (Pomatostomidae),"
//				+ " Pomatostomus, 0.5, Lichenostomus, (Meliphagidae), b, *, 2, Amytornis #, (Maluridae), *, (Acanthizidae),"
//				+ " Gerygone, Ailuroedus, *, (Ptilonorhynchidae), *, Sericulus, a, 2, Menura, (Menuridae), Tyrannus, *,"
//				+ " Myiarchus, (Tyrannidae), *, *, Camptostoma, *, (Tityridae), Onychorhynchus, *, 1, b, (Pipridae),"
//				+ " Manacus, *, *, Hypocnemis, *, (Thamnophilidae), *, Phlegopsis, 1, a, (Pittidae), Pitta, Acanthisitta,"
//				+ " (Acanthisittidae), 0.07, a, b, 1, 2, 3, 5, ambiguous, 4, 6, UVS          VS, –, ,, VS          UVS, ,,"
//				+ " Figure, 2, Phylogenetic, recontruction, of, SWS1, opsin, evolution., Majority, rule, (50%), consensus,"
//				+ " tree, of, passerines, based, on, concatenated, mitochondrial, cytochrome, b, and, ND2,, nuclear,"
//				+ " myoglobin, intron, 2,, ODC, introns, 6, to, 7,, TGFβ, 2, intron, 5,, and, protein-coding, nuclear,"
//				+ " c-myc, exon, 3,, RAG-1, and, RAG-2, sequences, (>, 9, kbp),, inferred, by, Bayesian, inference,,"
//				+ " analysed, in, eight, partitions,, with, two, parrots, and, two, falconiforms, as, outgroup., Posterior,"
//				+ " probabilities, given, at, nodes,, *, indicating, ≥, 0.95., Long, Ailuroedus, branch, truncated., VS/UVS,"
//				+ " optimisation, represented, by, violet, for, VS,, black, for, UVS, and, dotted, for, ambiguous.,"
//				+ " Transitions, from, one, state, to, another, are, indicated, by, numbers;, 1a, and, 1b,, and, 2a,"
//				+ " and, 2b,, respectively,, represent, uncertainties, due, to, ambiguous, ancestral, state., a,, b,"
//				+ " and, c, refer, to, insignificantly, supported, nodes, discussed, in, the, text., #, Sister, clade,,"
//				+ " genus, Malurus, (not, included),, contains, both, VS, and, UVS, species, [40].]",
//				wordList.toString());
//	}
//	
//	@Test
//	public void testVerticalWordOrientation() {
//		GeometryBuilder geometryBuilder = new GeometryBuilder(SVGElement.readAndCreateSVG(new File(SVG2XMLFixtures.BUILDER_DIR, "image.g.3.2.svg")));
//		geometryBuilder.setTextOrientation(TextOrientation.ROT_PI2);
//		List<WordNew> wordList = geometryBuilder.getWordList();
//		Assert.assertNotNull(wordList);
//		Assert.assertEquals("words", 10, wordList.size());
//		Assert.assertEquals("words",  "[Suboscines, Oscines, 'core Corvoidea', Passer-, Sylvi-, Muscicap-, oidea, oidea, oidea, Passerida]",
//				wordList.toString());
//	}
//	
//	@Test
//	public void testHorizontalScience() {
//		GeometryBuilder geometryBuilder = new GeometryBuilder(SVGElement.readAndCreateSVG(new File(SVG2XMLFixtures.BUILDER_DIR, "bloom-203-6-page3.svg")));
//		geometryBuilder.setTextOrientation(TextOrientation.ROT_0);
//		List<WordNew> wordList = geometryBuilder.getWordList();
//		Assert.assertNotNull(wordList);
//		Assert.assertEquals("words", 65, wordList.size());
//		Assert.assertEquals("words",  "[−10, 10, Synch + EC + self−Compton, 46, 10, Synch + EC + afterglow,"
//				+ " −11, 10, 45, 10, 14, 15, −12, 10, 10, 10, 44, 10, −13, 10, −13, 43, 10, 10, −14, 10, 42, 10,"
//				+ " −15, 10, −14, 10, 41, 10, −16, 10, 40, −15, 10, 10, −17, 4.0, 2.0, 1.0, 0.5, 10,"
//				+ "   Obs. wavelength (µm), 10, 12, 14, 16, 18, 20, 22, 24, 10, 10, 10, 10, 10, 10, 10, 10,"
//				+ " Observed frequency ν,  (Hz)]",
//				wordList.toString());
//	}
///**
//[REPORTS, −10, Fig., 1., Multiwavelength, spectral, en-, 10, ergy, distribution, of, Sw, 1644+57, at, t, +, 0, Synch + EC + selfν�Compton, 46, 10, 2.9, days., Our, radio-through-UV, mea-, Synch + EC + afterglow, −11, 10, surements, are, represented, by, solid, circles,, with, data, from, the, published, 45, 10, circulars, (20), represented, by, open, cir-, 14, 15, −12, 10, 10, 10, cles, (41)., X-ray, and, soft, gamma-ray, points, from, the, Swift, X-ray, Telescope, 44, 10, (XRT), and, Burst, Alert, Telescope, (un-, −13, 10, corrected, for, host-galaxy, absorption), −13, are, shown, as, black, crosses,, and, the, 43, 10, 10, Fermi, Large, Area, Telescope, gamma-, −14, 10, ray, upper, limit, (42), is, shown, at, the, 42, far, right., The, 90%, uncertainty, region, 10, of, a, power-law, fit, to, the, XRT, data, (with, −15, 10, −14, N, absorption, removed), is, represented, 10, H, 41, by, the, blue, bowtie., (Inset), The, same, 10, data, zoomed, in, on, the, opticalν�, near-IR, −16, 10, window., Overplotted, are, two, different, 40, multicomponent, models, for, the, SED, −15, 10, 10, (43), (Fig., 2)., The, orange, curve, shows, −17, 4.0, 2.0, 1.0, 0.5, 10, a, model, with, synchrotron,, synchrotron,   Obs. wavelength (ν�
//  char: 181; name: mu; f: Symbol; fn: FKKLBM+Symbol; e: Dictionary
// m), self-Compton,, and, external, Compton, 10, 12, 14, 16, 18, 20, 22, 24, (EC), contributions., The, purple, curve, 10, 10, 10, 10, 10, 10, 10, 10, shows, a, model, in, which, the, IR, emis-, Observed frequency ν�
//  char: 957; name: null; f: Symbol; fn: FKKLBM+Symbol; e: Dictionary
// ,  (Hz), sion, originates, from, a, compact, source, 14, of, synchrotron, emission, (ν-, 4, ν, 10, cm)., Both, models, require, moderate, extinction, (A, ν�, 3, to, 5, mag)., Additional, synchrotron, models, are, shown, in, fig., S3., The, model, SEDs, here, and, in, fig., S3, were, V, generated, using, the, computer, code, from, (44,, 45)., 204, 8, JULY, 2011, VOL, 333, SCIENCE, www.sciencemag.org] */
//	
//	@Test
//	public void testVerticalScience() {
//		GeometryBuilder geometryBuilder = new GeometryBuilder(SVGElement.readAndCreateSVG(new File(SVG2XMLFixtures.BUILDER_DIR, "bloom-203-6-page3.svg")));
//		geometryBuilder.setTextOrientation(TextOrientation.ROT_PI2);
//		List<WordNew> wordList = geometryBuilder.getWordList();
//		Assert.assertNotNull(wordList);
//		Assert.assertEquals("words", 16, wordList.size());
//		Assert.assertEquals("words",  "[−1 , −2, Specific flux ν, F, (erg s, cm, ), ν,  , −1, Specific luminosity ν,"
//				+ " L, (erg s, ), ν,  ]",
//				wordList.toString());
//	}
//	
//	@Test
//	public void testHorizontalScienceSmall() {
//		GeometryBuilder geometryBuilder = new GeometryBuilder(SVGElement.readAndCreateSVG(new File(SVG2XMLFixtures.BUILDER_DIR, "bloom-203-6-page3small.svg")));
//		geometryBuilder.setTextOrientation(TextOrientation.ROT_0);
//		List<WordNew> wordList = geometryBuilder.getWordList();
//		Assert.assertNotNull(wordList);
//		Assert.assertEquals("words", 15, wordList.size());
//		Assert.assertEquals("words",  "[14, 15, 10, 10, −13, 10, −14, 10, −15, 10, 4.0, 2.0, 1.0, 0.5,   Obs. wavelength (µm)]",
//				wordList.toString());
//	}
//	
//	@Test
//	public void testHorizontalScienceSmallSuscript() {
//		GeometryBuilder geometryBuilder = new GeometryBuilder(SVGElement.readAndCreateSVG(new File(SVG2XMLFixtures.BUILDER_DIR, "bloom-203-6-page3small.svg")));
//		geometryBuilder.setTextOrientation(TextOrientation.ROT_0);
//		HtmlElement htmlElement = geometryBuilder.createHtmlElement();
//		Assert.assertNotNull(htmlElement);
//		Assert.assertEquals("words",  "<div xmlns=\"http://www.w3.org/1999/xhtml\">10 <sup>14 </sup> 10 <sup>15</sup> 10 <sup>−13</sup> 10 <sup>−14</sup> 10 <sup>−15</sup> 4.0 2.0 1.0 0.5   Obs. wavelength ( µ m) </div>",
//				htmlElement.toXML());
//	}
//	
//	@Test
//	public void testVerticalScienceSmall() {
//		GeometryBuilder geometryBuilder = new GeometryBuilder(SVGElement.readAndCreateSVG(new File(SVG2XMLFixtures.BUILDER_DIR, "bloom-203-6-page3small.svg")));
//		geometryBuilder.setTextOrientation(TextOrientation.ROT_PI2);
//		List<WordNew> wordList = geometryBuilder.getWordList();
//		Assert.assertNotNull(wordList);
//		Assert.assertEquals("words", 7, wordList.size());
//		Assert.assertEquals("words",  "[−1, Specific luminosity ν, L, (erg s, ), ν,  ]",
//				wordList.toString());
//	}
//	
//	@Test
//	public void testVerticalScienceSmallSuscript() {
//		GeometryBuilder geometryBuilder = new GeometryBuilder(SVGElement.readAndCreateSVG(
//				new File(SVG2XMLFixtures.BUILDER_DIR, "bloom-203-6-page3small.svg")));
//		geometryBuilder.setTextOrientation(TextOrientation.ROT_PI2);
//		HtmlElement htmlElement = geometryBuilder.createHtmlElement();
//		Assert.assertNotNull(htmlElement);
//		Assert.assertEquals("words",  "<div xmlns=\"http://www.w3.org/1999/xhtml\">Specific luminosity  ν  L <sub>ν  </sub> (erg s <sup>−1</sup> ) </div>",
//				htmlElement.toXML());
//	}
		
}
