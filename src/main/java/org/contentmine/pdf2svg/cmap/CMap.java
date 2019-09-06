package org.contentmine.pdf2svg.cmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.contentmine.font.CodePoint;
import org.contentmine.font.CodePointSet;
import org.contentmine.font.UnicodePoint;

import nu.xom.Document;
import nu.xom.Serializer;

/** simple hack to read PS files.
 * NOT a complete implementation
 * Initially for reading CMAP files
 * 
 * @author pm286
 *
 */
public class CMap {

	private static final String BEGINCIDCHAR = "begincidchar";
	private static final String ENDCIDCHAR = "endcidchar";
	private static final String BEGINCIDRANGE = "begincidrange";
	private static final String ENDCIDRANGE = "endcidrange";
	private static final String BEGINBFCHAR = "beginbfchar";
	private static final String ENDBFCHAR = "endbfchar";
	private static final String BEGINBFRANGE = "beginbfrange";
	private static final String ENDBFRANGE = "endbfrange";
	private static final String BEGINCODESPACERANGE = "begincodespacerange";
	private static final String ENDCODESPACERANGE = "endcodespacerange";
	
	private static final String BEGIN_RESOURCE_C_MAP = "BeginResource: CMap (";
	private static final String BEGINCMAP = "begincmap";
	private static final String ENDCMAP = "endcmap";

	private static final Logger LOG = Logger.getLogger(CMap.class);

	public static final String PERCENT2 = "%%";
	public static final String PERCENT = "%";
	private static final String ADOBE3CMAP = "!PS-Adobe-3.0 Resource-CMap";
	private static final String VERSION = "Version: ";
	private static final String BEGIN = "begin";

	private List<String> tokenLineList;
	private List<String> tokenList;
	private Stack<String> tokenStack;
	private boolean adobe3Cmap = false;
	private boolean eof = false;

	private boolean ignore;
	private boolean inCmap;
	private String cmapName;
	private String version;
	private ArrayList<String> codespaceRangeList;
	private ArrayList<String> bfRangeList;
	private ArrayList<String> bfCharList;
	private ArrayList<String> cidRangeList;
	private ArrayList<String> cidCharList;
	
	private ArrayList<RangeEntry> codespaceRangeEntryList;
	private ArrayList<RangeEntry> bfRangeEntryList;
	private ArrayList<RangeEntry> cidRangeEntryList;
	private ArrayList<CharMapEntry> bfCharEntryList;
	private ArrayList<CharMapEntry> cidCharEntryList;
	private int rangeLow;
	private int rangeHigh;
	private Map<Integer, CMapPoint> cmapPointMap;
	private String inputPath;
	
	public static Set<String> texMacroSet = new HashSet<String>();
	static {
		Object a = TexCharacterSet.MILDE_SET; // to ensure loading
		addStrings("\\Gamma \\Delta \\Theta \\Kappa \\Xi \\Pi \\Sigma \\Upsilon \\Phi \\Psi \\Omega \\alpha \\beta \\gamma \\delta \\epsilon " +
				"\\zeta \\eta \\theta \\iota \\kappa \\lambda \\mu \\nu \\xi \\pi \\rho \\sigma \\tau \\upsilon \\phi \\chi \\psi " +
				"\\omega \\varepsilon \\vargamma \\varpi \\varrho \\varsigma \\varphi \\leftharpoonup \\leftharpoondown \\rightharpoonup" +
				" \\rightharpoondown \\lhook \\rhook \\triangleleft \\triangleright \\oldstyle{0} \\oldstyle{1} \\oldstyle{2} " +
				"\\oldstyle{3} \\oldstyle{4} \\oldstyle{5} \\oldstyle{6} \\oldstyle{7} \\oldstyle{8} \\oldstyle{9} \\star \\partial " +
				"\\flat \\natural \\sharp \\smile \\frown \\ell \\imath \\jmath \\wp \\vec{} \\t{}");
		
		addStrings("\\bfitGamma \\bfitDelta \\bfitKambda \\bfitXi \\bfitPi \\bfitSigma \\bfitUpsilon \\bfitPhi \\bfitPsi " +
				"\\bfitOmega \\bfitalpha \\bfitbeta \\bfitgamma \\bfitdelta \\bfitepsilon \\bfitzeta \\bfiteta \\bfittheta \\bfitiota " +
				"\\bfitkappa \\bfitlambda \\bfitmu \\bfitnu \\bfitxi \\bfitpi \\bfitrho \\bfitsigma \\bfittau \\bfitupsilon \\bfitphi " +
				"\\bfitchi \\bfitpsi \\bfitomega \\bfitvarepsilon \\bfitvargamma \\bfitvarpi \\bfitvarrho \\bfitvarsigma \\bfitvarphi " +
				"\\leftharpoonup \\leftharpoondown \\rightharpoonup \\rightharpoondown \\lhook \\rhook \\triangleleft \\triangleright " +
				"\\oldstyle{0} \\oldstyle{1} \\oldstyle{2} \\oldstyle{3} \\oldstyle{4} \\oldstyle{5} \\oldstyle{6} \\oldstyle{7} " +
				"\\oldstyle{8} \\oldstyle{9} \\star \\bfpartial \\bfitA \\bfitB \\bfitC \\bfitD \\bfitE \\bfitF \\bfitG \\bfitH " +
				"\\bfitI \\bfitJ \\bfitK \\bfitL \\bfitM \\bfitN \\bfitO \\bfitP \\bfitQ \\bfitR \\bfitS \\bfitT \\bfitU \\bfitV " +
				"\\bfitW \\bfitX \\bfitY \\bfitZ \\flat \\natural \\sharp \\smile \\frown \\ell \\bfita \\bfitb \\bfitc \\bfitd " +
				"\\bfite \\bfitf \\bfitg \\bfith \\bfiti \\bfitj \\bfitk \\bfitl \\bfitm \\bfitn \\bfito \\bfitp \\bfitq \\bfitr " +
				"\\bfits \\bfitt \\bfitu \\bfitv \\bfitw \\bfitx \\bfity \\bfitz \\imath \\jmath \\wp \\vec{} \\t{}");
		
		addStrings("\\cdot \\times \\ast \\div \\diamond \\pm \\mp \\oplus \\ominus \\otimes \\oslash \\odot \\bigcirc \\circ " +
				"\\bullet \\asymp \\equiv \\subseteq \\supseteq \\leq \\geq \\preceq \\succeq \\sim \\approx \\subset \\supset " +
				"\\ll \\gg \\prec \\succ \\leftarrow \\rightarrow \\uparrow \\downarrow \\updownarrow \\nearrow \\searrow \\simeq " +
				"\\Leftarrow \\Rightarrow \\Uparrow \\Downarrow \\leftrightarrow \\nwarrow \\swarrow \\propto \\prime \\infty " +
				"\\in \\ni \\bigtriangleup \\bigtriangledown \\not \\mapsto \\forall \\exists \\neg \\emptyset \\Re \\Im \\top " +
				"\\bot \\aleph \\mathcal{A} \\mathcal{B} \\mathcal{C} \\mathcal{D} \\mathcal{E} \\mathcal{F} \\mathcal{G} " +
				"\\mathcal{H} \\mathcal{I} \\mathcal{J} \\mathcal{K} \\mathcal{L} \\mathcal{M} \\mathcal{N} \\mathcal{O} " +
				"\\mathcal{P} \\mathcal{Q} \\mathcal{R} \\mathcal{S} \\mathcal{T} \\mathcal{U} \\mathcal{V} \\mathcal{W} " +
				"\\mathcal{X} \\mathcal{Y} \\mathcal{Z} \\cup \\cap \\uplus \\wedge \\vee \\vdash \\dashv \\lfloor \\rfloor " +
				"\\lceil \\rceil \\{ \\} \\langle \\rangle \\| \\updownarrow \\Updownarrow \\setminus \\wr \\surd \\amalg " +
				"\\nabla \\int \\sqcup \\sqcap \\sqsubseteq \\sqsupseteq \\S \\dagger \\ddagger \\P \\clubsuit \\diamondsuit " +
				"\\heartsuit \\spadesuit \\leftarrow \\cdot \\times \\ast \\div \\diamond \\pm \\mp \\oplus " +
				"\\ominus ￿ ￿ \\otimes \\oslash \\odot \\bigcirc \\circ \\bullet \\asymp \\equiv \\subseteq \\supseteq \\leq \\geq " +
				"\\preceq \\succeq \\sim \\approx \\subset \\supset \\ll \\gg \\prec \\succ \\leftarrow \\spadesuit");
		
		addStrings("\\sansGamma \\sansDelta \\sansTheta \\sansLambda \\sansXi \\sansPi \\sansSigma \\sansUpsilon \\sansPhi \\sansPsi " +
				"\\sansOmega ffi ffl ff fi fl " +
				"{\\i} {\\j} \\v \\u \\r \\c {\\ss} {\\ae} {\\oe} {\\e} {\\AE} {\\OE} {\\O} SS " +
				"\\sanszero \\sansone \\sanstwo \\sansthree \\sansfour \\sansfive \\sanssix \\sansseven \\sanseight \\sansnine" +
				"\\sansA \\sansB \\sansC \\sansD \\sansE \\sansF \\sansG \\sansH \\sansI \\sansJ \\sansK \\sansL \\sansM \\sansN " +
				"\\sansO \\sansP \\sansQ \\sansR \\sansS \\sansT \\sansU \\sansV \\sansW \\sansX \\sansY \\sansZ \\sansa \\sansb " +
				"\\sansc \\sansd \\sanse \\sansf \\sansg \\sansh \\sansi \\sansj \\sansk \\sansl \\sansm \\sansn \\sanso \\sansp " +
				"\\sansq \\sansr \\sanss \\sanst \\sansu \\sansv \\sansw \\sansx \\sansy \\sansz \\sansGamma \\sansDelta \\sansTheta " +
				"\\sansLambda \\sansXi \\sansPi \\sansSigma \\sansUpsilon \\sansPhi \\sansPsi ￿ ￿ \\sansOmega {\\i} {\\j} \\v \\u \\r \\c {\\ss} " +
				"{\\ae} {\\ee} {\\o} {\\AE} {\\OE} {\\O}￿");
		// cannot yet resolve Pisymbol{}
		addStrings("￿\\Pisymbol{psy}{33} \\Pisymbol{psy}{34} \\Pisymbol{psy}{35} \\Pisymbol{psy}{36} \\Pisymbol{psy}{37} \\Pisymbol{psy}{38} " +
				"\\Pisymbol{psy}{39} \\Pisymbol{psy}{40} \\Pisymbol{psy}{41} \\Pisymbol{psy}{42} \\Pisymbol{psy}{43} \\Pisymbol{psy}{44} " +
				"\\Pisymbol{psy}{45} \\Pisymbol{psy}{46} \\Pisymbol{psy}{47} \\Pisymbol{psy}{48} \\Pisymbol{psy}{49} \\Pisymbol{psy}{50} " +
				"\\Pisymbol{psy}{51} \\Pisymbol{psy}{52} \\Pisymbol{psy}{53} \\Pisymbol{psy}{54} \\Pisymbol{psy}{55} \\Pisymbol{psy}{56} " +
				"\\Pisymbol{psy}{57} \\Pisymbol{psy}{58} \\Pisymbol{psy}{59} \\Pisymbol{psy}{60} \\Pisymbol{psy}{61} \\Pisymbol{psy}{62} " +
				"\\Pisymbol{psy}{63} \\Pisymbol{psy}{64} \\Pisymbol{psy}{65} \\Pisymbol{psy}{66} \\Pisymbol{psy}{67} \\Pisymbol{psy}{68} " +
				"\\Pisymbol{psy}{69} \\Pisymbol{psy}{70} \\Pisymbol{psy}{71} \\Pisymbol{psy}{72} \\Pisymbol{psy}{73} \\Pisymbol{psy}{74} " +
				"\\Pisymbol{psy}{75} \\Pisymbol{psy}{76} \\Pisymbol{psy}{77} \\Pisymbol{psy}{78} \\Pisymbol{psy}{79} \\Pisymbol{psy}{80} " +
				"\\Pisymbol{psy}{81} \\Pisymbol{psy}{82} \\Pisymbol{psy}{83} \\Pisymbol{psy}{84} \\Pisymbol{psy}{85} \\Pisymbol{psy}{86} " +
				"\\Pisymbol{psy}{87} \\Pisymbol{psy}{88} \\Pisymbol{psy}{89} \\Pisymbol{psy}{90} \\Pisymbol{psy}{91} \\Pisymbol{psy}{92} " +
				"\\Pisymbol{psy}{93} \\Pisymbol{psy}{94} \\Pisymbol{psy}{95} \\Pisymbol{psy}{96} \\Pisymbol{psy}{97} \\Pisymbol{psy}{98} " +
				"\\Pisymbol{psy}{99} \\Pisymbol{psy}{100} \\Pisymbol{psy}{101} \\Pisymbol{psy}{102} \\Pisymbol{psy}{103} \\Pisymbol{psy}{104} " +
				"\\Pisymbol{psy}{105} \\Pisymbol{psy}{106} \\Pisymbol{psy}{107} \\Pisymbol{psy}{108} \\Pisymbol{psy}{109} \\Pisymbol{psy}{110} " +
				"\\Pisymbol{psy}{111} \\Pisymbol{psy}{112} \\Pisymbol{psy}{113} \\Pisymbol{psy}{114} \\Pisymbol{psy}{115} \\Pisymbol{psy}{116} " +
				"\\Pisymbol{psy}{117} \\Pisymbol{psy}{118} \\Pisymbol{psy}{119} \\Pisymbol{psy}{120} \\Pisymbol{psy}{121} \\Pisymbol{psy}{122} " +
				"\\Pisymbol{psy}{123} \\Pisymbol{psy}{124} \\Pisymbol{psy}{125} \\Pisymbol{psy}{126} \\Pisymbol{psy}{160} \\Pisymbol{psy}{161} " +
				"\\Pisymbol{psy}{162} \\Pisymbol{psy}{163} \\Pisymbol{psy}{164} \\Pisymbol{psy}{165} \\Pisymbol{psy}{166} \\Pisymbol{psy}{167} " +
				"\\Pisymbol{psy}{168} \\Pisymbol{psy}{169} \\Pisymbol{psy}{170} \\Pisymbol{psy}{171} \\Pisymbol{psy}{172} \\Pisymbol{psy}{173} " +
				"\\Pisymbol{psy}{174} \\Pisymbol{psy}{175} \\Pisymbol{psy}{176} \\Pisymbol{psy}{177} \\Pisymbol{psy}{178} \\Pisymbol{psy}{179} " +
				"\\Pisymbol{psy}{180} \\Pisymbol{psy}{181} \\Pisymbol{psy}{182} \\Pisymbol{psy}{183} \\Pisymbol{psy}{184} \\Pisymbol{psy}{185} " +
				"\\Pisymbol{psy}{186} \\Pisymbol{psy}{187} \\Pisymbol{psy}{188} \\Pisymbol{psy}{189} \\Pisymbol{psy}{190} \\Pisymbol{psy}{191} " +
				"\\Pisymbol{psy}{192} \\Pisymbol{psy}{193} \\Pisymbol{psy}{194} \\Pisymbol{psy}{195} \\Pisymbol{psy}{196} \\Pisymbol{psy}{197} " +
				"\\Pisymbol{psy}{198} \\Pisymbol{psy}{199} \\Pisymbol{psy}{200} \\Pisymbol{psy}{201} \\Pisymbol{psy}{202} \\Pisymbol{psy}{203} " +
				"\\Pisymbol{psy}{204} \\Pisymbol{psy}{205} \\Pisymbol{psy}{206} \\Pisymbol{psy}{207} \\Pisymbol{psy}{208} \\Pisymbol{psy}{209} " +
				"\\Pisymbol{psy}{210} \\Pisymbol{psy}{211} \\Pisymbol{psy}{212} \\Pisymbol{psy}{213} \\Pisymbol{psy}{214} \\Pisymbol{psy}{215} " +
				"\\Pisymbol{psy}{216} \\Pisymbol{psy}{217} \\Pisymbol{psy}{218} \\Pisymbol{psy}{219} \\Pisymbol{psy}{220} \\Pisymbol{psy}{221} " +
				"\\Pisymbol{psy}{222} \\Pisymbol{psy}{223} \\Pisymbol{psy}{224} \\Pisymbol{psy}{225} \\Pisymbol{psy}{226} \\Pisymbol{psy}{227} " +
				"\\Pisymbol{psy}{228} \\Pisymbol{psy}{229} \\Pisymbol{psy}{230} \\Pisymbol{psy}{231} \\Pisymbol{psy}{232} \\Pisymbol{psy}{233} " +
				"\\Pisymbol{psy}{234} \\Pisymbol{psy}{235} \\Pisymbol{psy}{236} \\Pisymbol{psy}{237} \\Pisymbol{psy}{238} \\Pisymbol{psy}{239} ￿" +
				"\\Pisymbol{psy}{241} \\Pisymbol{psy}{242} \\Pisymbol{psy}{243} \\Pisymbol{psy}{244} \\Pisymbol{psy}{245} \\Pisymbol{psy}{246} " +
				"\\Pisymbol{psy}{247} \\Pisymbol{psy}{248} \\Pisymbol{psy}{249} \\Pisymbol{psy}{250} \\Pisymbol{psy}{251} \\Pisymbol{psy}{252} " +
				"\\Pisymbol{psy}{253} \\Pisymbol{psy}{254}");
		// cannot yet resolve ding{}
		addStrings("\\ding{33} \\ding{34} \\ding{35} \\ding{36} \\ding{37} \\ding{38} \\ding{39} \\ding{40} \\ding{41} \\ding{42} \\ding{43} " +
				"\\ding{44} \\ding{45} \\ding{46} \\ding{47} \\ding{48} \\ding{49} \\ding{50} \\ding{51} \\ding{52} \\ding{53} \\ding{54} " +
				"\\ding{55} \\ding{56} \\ding{57} \\ding{58} \\ding{59} \\ding{60} \\ding{61} \\ding{62} \\ding{63} \\ding{64} \\ding{65} " +
				"\\ding{66} \\ding{67} \\ding{68} \\ding{69} \\ding{70} \\ding{71} \\ding{72} \\ding{73} \\ding{74} \\ding{75} \\ding{76} " +
				"\\ding{77} \\ding{78} \\ding{79} \\ding{80} \\ding{81} \\ding{82} \\ding{83} \\ding{84} \\ding{85} \\ding{86} \\ding{87} " +
				"\\ding{88} \\ding{89} \\ding{90} \\ding{91} \\ding{92} \\ding{93} \\ding{94} \\ding{95} \\ding{96} \\ding{97} \\ding{98} " +
				"\\ding{99} \\ding{100} \\ding{101} \\ding{102} \\ding{103} \\ding{104} \\ding{105} \\ding{106} \\ding{107} \\ding{108} " +
				"\\ding{109} \\ding{110} \\ding{111} \\ding{112} \\ding{113} \\ding{114} \\ding{115} \\ding{116} \\ding{117} \\ding{118} " +
				"\\ding{119} \\ding{120} \\ding{121} \\ding{122} \\ding{123} \\ding{124} \\ding{125} \\ding{126} \\ding{161} \\ding{162} " +
				"\\ding{163} \\ding{164} \\ding{165} \\ding{166} \\ding{167} \\ding{168} \\ding{169} \\ding{170} \\ding{171} \\ding{172} " +
				"\\ding{173} \\ding{174} \\ding{175} \\ding{176} \\ding{177} \\ding{178} \\ding{179} \\ding{180} \\ding{181} \\ding{182} " +
				"\\ding{183} \\ding{184} \\ding{185} \\ding{186} \\ding{187} \\ding{188} \\ding{189} \\ding{190} \\ding{191} \\ding{192} " +
				"\\ding{193} \\ding{194} \\ding{195} \\ding{196} \\ding{197} \\ding{198} \\ding{199} \\ding{200} \\ding{201} \\ding{202} " +
				"\\ding{203} \\ding{204} \\ding{205} \\ding{206} \\ding{207} \\ding{208} \\ding{209} \\ding{210} \\ding{211} \\ding{212} " +
				"\\ding{213} \\ding{214} \\ding{215} \\ding{216} \\ding{217} \\ding{218} \\ding{219} \\ding{220} \\ding{221} \\ding{222} " +
				"\\ding{223} \\ding{224} \\ding{225} \\ding{226} \\ding{227} \\ding{228} \\ding{229} \\ding{230} \\ding{231} \\ding{232} " +
				"\\ding{233} \\ding{234} \\ding{235} \\ding{236} \\ding{237} \\ding{238} \\ding{239} ￿ \\ding{241} \\ding{242} \\ding{243} " +
				"\\ding{244} \\ding{245} \\ding{246} \\ding{247} \\ding{248} \\ding{249} \\ding{250} \\ding{251} \\ding{252} \\ding{253} " +
				"\\ding{254}");
		addStrings("\\H \\Pisymbol{psy}{33} \\bffrakA \\bffrakB \\bffrakC \\bffrakD \\bffrakE \\bffrakF \\bffrakG \\bffrakH \\bffrakI " +
				"\\bffrakJ \\bffrakK \\bffrakL \\bffrakM \\bffrakN \\bffrakO \\bffrakP \\bffrakQ \\bffrakR \\bffrakS \\bffrakT \\bffrakU " +
				"\\bffrakV \\bffrakW \\bffrakX \\bffrakY \\bffrakZ \\bffraka \\bffrakb \\bffrakc \\bffrakd \\bffrake \\bffrakf \\bffrakg " +
				"\\bffrakh \\bffraki \\bffrakj \\bffrakk \\bffrakl \\bffrakm \\bffrakn \\bffrako \\bffrakp \\bffrakq \\bffrakr \\bffraks " +
				"\\bffrakt \\bffraku \\bffrakv \\bffrakw \\bffrakx \\bffraky \\bffrakz \\bffrak{d} \\bffrak{f} \\bffrak{g} \\bffrak{k} " +
				"\\bffrak{t} \\bffrak{u} \\c \\frakA \\frakB \\frakC \\frakD \\frakE \\frakF \\frakG \\frakH \\frakI \\frakJ \\frakK " +
				"\\frakL \\frakM \\frakN \\frakO \\frakP \\frakQ \\frakR \\frakS \\frakT \\frakU \\frakV \\frakW \\frakX \\frakY " +
				"\\frakZ \\fraka \\frakb \\frakc \\frakd \\frake \\frakf \\frakg \\frakh \\fraki \\frakj \\frakk \\frakl \\frakm " +
				"\\frakn \\frako \\frakp \\frakq \\frakr \\fraks \\frakt \\fraku \\frakv \\frakw \\frakx \\fraky \\frakz \\frak{d} " +
				"\\frak{f} \\frak{g} \\frak{k} \\frak{t} \\frak{u} \\itDelta \\itGamma \\itLambda \\itOmega \\itPhi \\itPi \\itPsi " +
				"\\itSigma \\itTheta \\itUpsilon \\itXi \\r \\sansA \\sansB \\sansC \\sansD \\sansDelta \\sansE \\sansF \\sansG " +
				"\\sansGamma \\sansH \\sansI \\sansJ \\sansK \\sansL \\sansLambda \\sansM \\sansN \\sansO \\sansOmega \\sansP " +
				"\\sansPhi \\sansPi \\sansPsi \\sansQ \\sansR \\sansS \\sansSigma \\sansT \\sansTheta \\sansU \\sansUpsilon \\sansV " +
				"\\sansW \\sansX \\sansXi \\sansY \\sansZ \\sansa \\sansb \\sansc \\sansd \\sanse \\sanseight \\sansf \\sansfive " +
				"\\sansfour \\sansg \\sansh \\sansi \\sansj \\sansk \\sansl \\sansm \\sansn \\sansnine \\sanso \\sansone \\sansp " +
				"\\sansq \\sansr \\sanss \\sansseven \\sanssix \\sanst \\sansthree \\sanstwo \\sansu \\sansv \\sansw \\sansx \\sansy " +
				"\\sansz \\sanszero \\ttA \\ttB \\ttC \\ttD \\ttE \\ttF \\ttG \\ttH \\ttI \\ttJ \\ttK \\ttL \\ttM \\ttN \\ttO \\ttP " +
				"\\ttQ \\ttR \\ttS \\ttT \\ttU \\ttV \\ttW \\ttX \\ttY \\ttZ \\tta \\ttb \\ttc \\ttd \\tte \\tteight \\ttf \\ttfive " +
				"\\ttfour \\ttg \\tth \\tti \\ttj \\ttk \\ttl \\ttm \\ttn \\ttnine \\tto \\ttone \\ttp \\ttq \\ttr \\tts \\ttseven " +
				"\\ttsix \\ttt \\ttthree \\tttwo \\ttu \\ttv \\ttw \\ttx \\tty \\ttz \\ttzero \\u \\v \\xxA \\xxB \\xxC \\xxD " +
				"\\xxDelta \\xxE \\xxF \\xxG \\xxGamma \\xxH \\xxI \\xxJ \\xxK \\xxL \\xxLambda \\xxM \\xxN \\xxO \\xxOmega \\xxP " +
				"\\xxPhi \\xxPi \\xxPsi \\xxQ \\xxR \\xxS \\xxSigma \\xxT \\xxTheta \\xxU \\xxUpsilon \\xxV \\xxW \\xxX \\xxXi " +
				"\\xxY \\xxZ \\xxa \\xxb \\xxc \\xxd \\xxe \\xxeight \\xxf \\xxfive \\xxfour \\xxg \\xxh \\xxi \\xxj \\xxk \\xxl " +
				"\\xxm \\xxn \\xxnine \\xxo \\xxone \\xxp \\xxq \\xxr \\xxs \\xxseven \\xxsix \\xxt \\xxthree \\xxtwo \\xxu \\xxv " +
				"\\xxw \\xxx \\xxy \\xxz \\xxzero ffi ffl {\\AE} {\\OE} {\\O} {\\ae} {\\ee} {\\e} {\\i} {\\j} {\\oe} {\\o} {\\ss}");
		
			addStrings("\\Lambda \\Pisymbol{psy}{241} \\bfDelta \\bfGamma \\bfLambda \\bfOmega \\bfPhi \\bfPi \\bfPsi " +
				"\\bfSigma \\bfTheta \\bfUpsilon \\bfXi \\bfsansA \\bfsansB \\bfsansC \\bfsansD \\bfsansDelta \\bfsansE \\bfsansF \\bfsansG " +
				"\\bfsansGamma \\bfsansH \\bfsansI \\bfsansJ \\bfsansK \\bfsansL \\bfsansLambda \\bfsansM \\bfsansN \\bfsansO \\bfsansOmega " +
				"\\bfsansP \\bfsansPhi \\bfsansPi \\bfsansPsi \\bfsansQ \\bfsansR \\bfsansS \\bfsansSigma \\bfsansT \\bfsansTheta \\bfsansU " +
				"\\bfsansUpsilon \\bfsansV \\bfsansW \\bfsansX \\bfsansXi \\bfsansY \\bfsansZ \\bfsansa \\bfsansb \\bfsansc \\bfsansd " +
				"\\bfsanse \\bfsanseight \\bfsansf \\bfsansfive \\bfsansfour \\bfsansg \\bfsansh \\bfsansi \\bfsansj \\bfsansk \\bfsansl " +
				"\\bfsansm \\bfsansn \\bfsansnine \\bfsanso \\bfsansone \\bfsansp \\bfsansq \\bfsansr \\bfsanss \\bfsansseven \\bfsanssix " +
				"\\bfsanst \\bfsansthree \\bfsanstwo \\bfsansu \\bfsansv \\bfsansw \\bfsansx \\bfsansy \\bfsansz \\bfsanszero \\bfw \\bfx " +
				"\\bfy \\bfz " +
				"\\itA \\itB \\itC \\itD \\itE \\itF \\itG \\itH \\itI \\itJ \\itK \\itL \\itM \\itN \\itO \\itP \\itQ \\itR \\itS \\itT " +
				"\\itU \\itV \\itW \\itX \\itY \\itZ \\ita \\itb \\itc \\itd \\ite \\itf \\itg \\ith \\iti \\itj \\itk \\itl \\itm \\itn \\ito \\itp " +
				"\\itq \\itr \\its \\itt \\itu \\itv \\itw \\itx \\ity " +
				"\\itz \\mathrm{A} \\mathrm{B} \\mathrm{C} \\mathrm{D} \\mathrm{E} " +
				"\\mathrm{F} \\mathrm{G} \\mathrm{H} \\mathrm{I} \\mathrm{J} \\mathrm{K} \\mathrm{L} \\mathrm{M} \\mathrm{N} \\mathrm{O} " +
				"\\mathrm{P} \\mathrm{Q} \\mathrm{R} \\mathrm{S} \\mathrm{T} \\mathrm{U} \\mathrm{V} \\mathrm{W} \\mathrm{X} \\mathrm{Y} " +
				"\\mathrm{Z} \\mathrm{a} \\mathrm{b} \\mathrm{c} \\mathrm{d} \\mathrm{e} \\mathrm{f} \\mathrm{g} \\mathrm{h} \\mathrm{i} " +
				"\\mathrm{j} \\mathrm{k} \\mathrm{l} \\mathrm{m} \\mathrm{n} \\mathrm{o} \\mathrm{p} \\mathrm{q} \\mathrm{r} \\mathrm{s} " +
				"\\mathrm{t} \\mathrm{u} \\mathrm{v} \\mathrm{w} \\mathrm{x} \\mathrm{y} \\mathrm{z} ffi ffl {\\AE} {\\OE} {\\O} {\\ae} " +
				"{\\ee} {\\e} {\\i} {\\j} {\\oe} {\\o} {\\ss} ");
		addStrings("\\bfA \\bfB \\bfC \\bfD \\bfE \\bfF \\bfG \\bfH \\bfI \\bfJ " +
				"\\bfK \\bfL \\bfM \\bfN \\bfO \\bfP \\bfQ \\bfR \\bfS \\bfT \\bfU \\bfV \\bfW \\bfX \\bfY \\bfZ \\bfa \\bfb " +
				"\\bfc \\bfd \\bfe \\bff \\bffive \\bffour \\bfg \\bfh \\bfi \\bfitLambda \\bfitTheta \\bfj \\bfk \\bfl \\bfm " +
				"\\bfn \\bfnine \\bfo \\bfone \\bfp \\bfq \\bfr \\bfs \\bfscrA \\bfscrB \\bfscrC \\bfscrD \\bfscrE \\bfscrF " +
				"\\bfscrG \\bfscrH \\bfscrI \\bfscrJ \\bfscrK \\bfscrL \\bfscrM \\bfscrN \\bfscrO \\bfscrP \\bfscrQ \\bfscrR " +
				"\\bfscrS \\bfscrT \\bfscrU \\bfscrV \\bfscrW \\bfscrX \\bfscrY \\bfscrZ \\bfsix \\bft \\bftwo \\bfu \\bfv " +
				"\\bfzero \\scrA \\scrB \\scrC \\scrD \\scrE \\scrF \\scrG \\scrH \\scrI \\scrJ \\scrK \\scrL \\scrM \\scrN " +
				"\\scrO \\scrP \\scrQ \\scrR \\scrS \\scrT \\scrU \\scrV \\scrW \\scrX \\scrY \\scrZ \\big\\# \\big\\& " +
				"\\bfthree \\bfseven \\bfeight \\bfnabla");
	};
	
	/** unrecognised:
 \H
 \Pisymbol{psy}{33}
 ...
 \Pisymbol{psy}{126}
 \Pisymbol{psy}{160}
 ...
 \Pisymbol{psy}{254}
 \bffrakA ...  \bffrakZ
 \bffraka ... \bffrakz
 \bffrak{d}
 \bffrak{f}
 \bffrak{g}
 \bffrak{k}
 \bffrak{t}
 \bffrak{u}
 \bfsansA ... \bfsansDelta ... \bfsansE ... \bfsansan ... \bfsanseight ... \bfsansz
 \c
 \ding{33} ... \ding{254}
 \frakA ... \frakZ  \fraka ... \frakz
 \frak{d}
 \frak{f}
 \frak{g}
 \frak{k}
 \frak{t}
 \frak{u}
 \itDelta
 \itGamma
 \itLambda
 \itOmega
 \itPhi
 \itPi
 \itPsi
 \itSigma
 \itTheta
 \itUpsilon
 \itXi
 \r
 \sansA
 \sansZ
 \sansa
 \sansz
 \sanszero
 \ttA
 \ttB
 \ttzero
 \v
 \xxA
 \xxB
 \xxz
 \xxzero
 ffi
 ffl
 {\AE}
 {\OE}
 {\O}
 {\ae}
 {\ee}
 {\e}
 {\i}
 {\j}
 {\oe}
 {\o}
 {\ss}

	 */

	public CMap() {
		
	}
	
	private static void addStrings(String string) {
		String[] ss = string.split("\\s+");
		for (String s : ss) {
			texMacroSet.add(s.trim());
		}
	}

	public static List<CMap> read(File file, FileFilter fileFilter) throws IOException {
		if (file == null || !file.exists()) {
			throw new RuntimeException("File is null or does not exist: "+file);
		}
		if (file.isDirectory()) {
			return readDirectory(file, fileFilter);
		} else {
			List<CMap> cmapList = new ArrayList<CMap>();
			cmapList.add(CMap.readFile(file));
			return cmapList;
		}
	}
	
	private static List<CMap> readDirectory(File file, FileFilter fileFilter) {
		List<CMap> cmapList = new ArrayList<CMap>();
		File[] files = file.listFiles(fileFilter);
		if (files != null) {
			for (File file0 : files) {
				try {
					if (!file0.isDirectory()) {
						System.out.println("==="+file0+"===");
						CMap cmap = readFile(file0);
						cmapList.add(cmap);
					}
				} catch (Exception e) {
					e.printStackTrace();
					LOG.info("Not a CMAP file: "+file0+" "+e);
				}
			}
		}
		return cmapList;
	}
	
	public static CMap readFile(File file) throws IOException {
		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read: "+file, e);
		}
		CMap cmap = read(new BufferedReader(fr));
		cmap.setInputPath(file.getPath());
		return cmap;
	}
	
	public void setInputPath(String path) {
		this.inputPath = path;
	}

	public String getInputPath() {
		return this.inputPath;
	}

	public static CMap read(BufferedReader br) throws IOException {
		CMap cmap = new CMap();
		String line = null;
		int lineNumber = 0;
		while ((line = br.readLine()) != null) {
			if (lineNumber++ ==0) {
				cmap.readPercent(line);
				if (!cmap.adobe3Cmap) {
					return null;
				}
			} else if (line.startsWith(PERCENT2)) {
				cmap.readPercent2(line.substring(PERCENT2.length()));
			} else if (line.startsWith(PERCENT)) {
				cmap.readPercent(line.substring(PERCENT.length()));
			} else {
				// trim comments (crude)
				if (line.contains(PERCENT)) {
					line = line.substring(0, line.indexOf(PERCENT));
				}
				cmap.addTokenLine(line);
			}
		}
		cmap.processLines();
		cmap.processLists();
		cmap.removeNulls();
		cmap.resolveTexMacros();
//		cmap.debugMap();
		return cmap;
	}
	
	private void resolveTexMacros() {
		Integer[] keys = getSortedCMapPoints();
		for (Integer key : keys) {
			CMapPoint codepoint = cmapPointMap.get(key);
			Integer decimal = codepoint.getValue();
			String code = codepoint.getStringRepresentation();
			if (decimal == null && code != null && !code.equals("")) {
				TexCharacter texCharacter = TexCharacterSet.getMildeSet().getUnicodeMathOrLatex(code);
				if (texCharacter == null) {
					// may be unnecessary
					texCharacter = tryVariantPrefixes0(code);
					texCharacter = tryVariantPrefixes(code);
				}
				if (texCharacter != null) {
					codepoint.setValue(texCharacter.getUnicodePoint());
				} else {
					LOG.debug("(0x"+Integer.toHexString(key)+"/"+key+") Cannot interpret as TeX macro (maybe character anyway) "+code);
				}
			}
		}
	}

	private TexCharacter tryVariantPrefixes0(String code) {
		TexCharacter texCharacter = null;
		if (texCharacter == null && 
				code.startsWith("\\big\\")) {
			String mCode = "\\"+code.substring(5);
			texCharacter = TexCharacterSet.getMildeSet().getUnicodeMathOrLatex(mCode);
		}
		return texCharacter;
	}
	/** 
	 * @param code
	 * @param texCharacter
	 * @return
	 */
	private TexCharacter tryVariantPrefixes(String code) {
		TexCharacter texCharacter = null;
		// 	 * some codes appear to be \itFoo instead of \mitFoo and need converting so insert "m"
		if (texCharacter == null && 
				code.startsWith("\\bf") ||
				code.startsWith("\\it") ||
				code.startsWith("\\scr") ||
				code.startsWith("\\tt")) {
			String mCode = "\\m"+code.substring(1);
			texCharacter = TexCharacterSet.getMildeSet().getUnicodeMathOrLatex(mCode);
		}
		// some sans are not present in Unicode so guess bold sans
		// \\sansGamma => \\mbfsansGamma
		if (texCharacter == null && 
				code.startsWith("\\sans")) {
			String mCode = "\\mbf"+code.substring(1);
			texCharacter = TexCharacterSet.getMildeSet().getUnicodeMathOrLatex(mCode);
		}
		// convert \\xxFoo => \\Foo
		if (texCharacter == null && 
				code.startsWith("\\xx")) {
			String mCode = "\\"+code.substring(3);
			texCharacter = TexCharacterSet.getMildeSet().getUnicodeMathOrLatex(mCode);
		}
		// convert \\xxA => A
		if (texCharacter == null && 
				code.startsWith("\\xx")) {
			String mCode = code.substring(3,4);
			texCharacter = TexCharacterSet.getMildeSet().getUnicodeMathOrLatex(mCode);
		}
		// mathrm{X} => X
		if (texCharacter == null && 
				code.startsWith("\\mathrm{")) {
			String mCode = code.substring(8,9);
			texCharacter = TexCharacterSet.getMildeSet().getUnicodeMathOrLatex(mCode);
		}
		// \\oldstyle{0} => 0
		if (texCharacter == null && 
				code.startsWith("\\oldstyle{")) {
			String mCode = code.substring(10,11);
			texCharacter = TexCharacterSet.getMildeSet().getUnicodeMathOrLatex(mCode);
		}
		// \\bfone => one
		if (texCharacter == null && 
				code.startsWith("\\bf")) {
			String mCode = "\\"+code.substring(3);
			texCharacter = TexCharacterSet.getMildeSet().getUnicodeMathOrLatex(mCode);
		}
		// \\frakA => \\mfrakA
		if (texCharacter == null && 
				code.startsWith("\\frak")) {
			String mCode = "\\m"+code.substring(1);
			texCharacter = TexCharacterSet.getMildeSet().getUnicodeMathOrLatex(mCode);
		}
		// \\frak{A} => \\mfrakA
		if (texCharacter == null && 
				code.startsWith("\\frak{")) {
			String mCode = "\\mfrak"+code.substring(6,7);
			texCharacter = TexCharacterSet.getMildeSet().getUnicodeMathOrLatex(mCode);
		}
		// \\bffrak{A} => \\mfrakA
		if (texCharacter == null && 
				code.startsWith("\\bffrak{")) {
			String mCode = "\\mfrak"+code.substring(8,9);
			texCharacter = TexCharacterSet.getMildeSet().getUnicodeMathOrLatex(mCode);
		}
/**
		emptyset varnothing
		surd sqrt
		ffi
		ffl
 */
		return texCharacter;
	}

	private void removeNulls() {
		Integer[] keys = getSortedCMapPoints();
		for (Integer key : keys) {
			CMapPoint codepoint = cmapPointMap.get(key);
			Integer decimal = codepoint.getValue();
			if (decimal != null && decimal.intValue() == 0) {
				cmapPointMap.remove(key);
			}
		}
	}

	public void debugMap() {
		Integer[] keyList = getSortedCMapPoints();
		for (Integer ii : keyList) {
			CMapPoint cmapPoint = cmapPointMap.get(ii);
			System.out.println("DBG> "+ii+" "+cmapPoint.getStringRepresentation()+" "+cmapPoint.getHexStringOfValue()+" "+cmapPoint.getValue()+" "+cmapPoint.getCharOfValue());
		}
		System.out.println();
	}

	private Integer[] getSortedCMapPoints() {
		Set<Integer> keys = cmapPointMap.keySet();
		Integer[] keyList = keys.toArray(new Integer[0]);
		Arrays.sort(keyList);
		return keyList;
	}

	private void processLines() {
		if (cmapName == null) {
			LOG.debug("No CMAP title");
			return;
		}
		ensureTokenList();
		for (String line : tokenLineList) {
			String[] tokens = line.split("[\\s\\n\\t]+");
			for (String token : tokens) {
				tokenStack.push(token);
				if (token.startsWith("begin")) {
					beginList(token);
				} else if (token.startsWith("end")) {
					endList(token);
				} else if (token.startsWith("<<")) {
					ignore = true;
				} else if (token.startsWith(">>")) {
					ignore = false;
				} else if (ignore) { // skip in <<...>>
				} else if (!inCmap) { // skip outside cmap
				} else {
					add(token);
				}
			}
		}
	}
	
	private void add(String token) {
		if (token.startsWith("(") || token.startsWith("[") ||  token.startsWith("{")) {
			throw new RuntimeException("Cannot do brackets "+token);
		} else if (codespaceRangeList != null) {
			codespaceRangeList.add(token);
		} else if (bfRangeList != null) {
			bfRangeList.add(token);
		} else if (bfCharList != null) {
			bfCharList.add(token);
		} else if (cidRangeList != null) {
			cidRangeList.add(token);
		} else if (cidCharList != null) {
			cidCharList.add(token);
		}
	}
	
	private void beginList(String token) {
		if (token.equals(BEGIN)) {
		} else if (token.equals(BEGINCMAP)) {
			inCmap = true;
		} else if (token.equals(BEGINCODESPACERANGE)) {
			codespaceRangeList = new ArrayList<String>();
		} else if (token.equals(BEGINBFRANGE)) {
			bfRangeList = new ArrayList<String>();
		} else if (token.equals(BEGINBFCHAR)) {
			bfCharList = new ArrayList<String>();
		} else if (token.equals(BEGINCIDRANGE)) {
			cidRangeList = new ArrayList<String>();
		} else if (token.equals(BEGINCIDCHAR)) {
			cidCharList = new ArrayList<String>();
		} else {
			LOG.error("UNKNOWN ***** "+token);
		}
	}

	private void endList(String token) {
		if (token.equals(ENDCMAP)) {
			inCmap=false;
		} else if (codespaceRangeList != null && token.equals(ENDCODESPACERANGE)) {
			processCodespaceRange();
		} else if (bfRangeList != null && token.equals(ENDBFRANGE)) {
			processBfRange();
		} else if (bfCharList != null && token.equals(ENDBFCHAR)) {
			processBfChar();
		} else if (cidRangeList != null && token.equals(ENDCIDRANGE)) {
			processCidRange();
		} else if (cidCharList != null && token.equals(ENDCIDCHAR)) {
			processCidChar();
		}
	}

	private void processCodespaceRange() {
		ensureCodespaceRangeEntryList(); 
		processRange0(codespaceRangeList, codespaceRangeEntryList);
		codespaceRangeList = null;
	}

	private void ensureCodespaceRangeEntryList() {
		if (codespaceRangeEntryList == null) {
			codespaceRangeEntryList = new ArrayList<RangeEntry>();
		}
	}

	private void processBfRange() {
		ensureBfRangeEntryList(); 
		processRange(bfRangeList, bfRangeEntryList);
		bfRangeList = null;
	}

	private void processCidRange() {
		ensureCidRangeEntryList(); 
		processRange(cidRangeList, cidRangeEntryList);
		cidRangeList = null;
	}

	private void processRange0(List<String> rangeList, List<RangeEntry> rangeEntryList) {
		if (rangeList.size() %2 != 0) {
			throw new RuntimeException("Range must be multiple of 2");
		}
		for (int i = 0; i < rangeList.size(); i+= 2) {
			RangeEntry rangeEntry = new RangeEntry(rangeList.get(i),rangeList.get(i+1));
			rangeEntryList.add(rangeEntry);
		}
	}

	private void processRange(List<String> rangeList, List<RangeEntry> rangeEntryList) {
		if (rangeList.size() %3 != 0) {
			throw new RuntimeException("Range must be multiple of 3");
		}
		for (int i = 0; i < rangeList.size(); i+= 3) {
			RangeEntry rangeEntry = new RangeEntry(rangeList.get(i),rangeList.get(i+1),rangeList.get(i+2));
			rangeEntryList.add(rangeEntry);
		}
	}

	private void ensureBfRangeEntryList() {
		if (bfRangeEntryList == null) {
			bfRangeEntryList = new ArrayList<RangeEntry>();
		}
	}

	private void processBfChar() {
		ensureBfCharEntryList();
		addChar(bfCharList, bfCharEntryList);
		bfCharList = null;
	}

	private void processCidChar() {
		ensureCidCharEntryList();
		addChar(cidCharList, cidCharEntryList);
		cidCharList = null;
	}

	private void addChar(List<String> charList, List<CharMapEntry> charEntryList) {
		for (int i = 0; i < charList.size(); i += 2) {
			CharMapEntry bfChar = new CharMapEntry(charList.get(i), charList.get(i+1));
			charEntryList.add(bfChar);
		}
	}

	private void ensureBfCharEntryList() {
		if (bfCharEntryList == null) {
			bfCharEntryList = new ArrayList<CharMapEntry>();
		}
	}

	private void ensureCidCharEntryList() {
		if (cidCharEntryList == null) {
			cidCharEntryList = new ArrayList<CharMapEntry>();
		}
	}

	private void ensureCidRangeEntryList() {
		if (cidRangeEntryList == null) {
			cidRangeEntryList = new ArrayList<RangeEntry>();
		}
	}


	private void addTokenLine(String line) {
		ensureTokenLineList();
		if (!line.startsWith(PERCENT)) {
			tokenLineList.add(line);
		}
	}
	
	private void ensureTokenLineList() {
		if (tokenLineList == null) {
			tokenLineList = new ArrayList<String>();
		}
	}
	
	private void ensureTokenList() {
		if (tokenList == null) {
			tokenList = new ArrayList<String>();
		}
		if (tokenStack == null) {
			tokenStack = new Stack<String>();
		}
	}
	
	private void readPercent(String line) {
		if (line.trim().length() > 0) {
			line = line.substring(1);
			if (adobe3Cmap) {
				return;  // comment
			} else if (line.equals(ADOBE3CMAP)) {
				adobe3Cmap  = true;
			} else {
				throw new RuntimeException("currently only CMap supported: "+line+"/"+ADOBE3CMAP);
			}
		}
	}

	private void readPercent2(String line) {
		if (!adobe3Cmap) {
			throw new RuntimeException("not a CMap file: "+line);
		}
		if (line.equals("DocumentNeededResources: ProcSet (CIDInit)")) {
		} else if (line.equals("IncludeResource: ProcSet (CIDInit)")) {
		} else if (line.startsWith(BEGIN_RESOURCE_C_MAP)) {
			line =line.substring(BEGIN_RESOURCE_C_MAP.length());
			cmapName = line.substring(0,line.length()-1).trim();
		} else if (line.startsWith(VERSION)) {
			line =line.substring(VERSION.length());
			version = line.substring(0,line.length()-1).trim();
		} else if (line.startsWith("Copyright")) {
		} else if (line.startsWith("Title")) {
		} else if (line.equals("EndComments")) {
		} else if (line.equals("EndResource")) {
		} else if (line.equals("EOF")) {
			eof  = true;
		} else {
			LOG.error("Unknown %% "+line);
		}
	}
	
	public void processLists() {
		cmapPointMap = new HashMap<Integer, CMapPoint>();
		processCodespace();
		addBfRange();
		addBfChars();
		addCidRange();
		addCidChars();
	}

	private void processCodespace() {
		if (codespaceRangeEntryList == null) {
			throw new RuntimeException("No codespaceRangeEntryList");
		}
		if (codespaceRangeEntryList.size() == 1) {
			rangeLow = codespaceRangeEntryList.get(0).getLow();
			rangeHigh = codespaceRangeEntryList.get(0).getHigh();
		}
		// fill with NULL
		for (Integer i = rangeLow; i <= rangeHigh; i++) {
			cmapPointMap.put(i, CMapPoint.NULL);
		}
	}

	private void addBfRange() {
		addRange(bfRangeEntryList);
	}

	private void addCidRange() {
		addRange(cidRangeEntryList);
	}

	private void addRange(List<RangeEntry> rangeEntryList) {
		if (rangeEntryList != null) {
			for (RangeEntry range : rangeEntryList) {
				int lowx = range.getLow();
				int highx = range.getHigh();
				if (lowx < rangeLow || highx > rangeHigh) {
					throw new RuntimeException("Range outside codespaceRange");
				}
				generateAndAddChars(range, lowx, highx);
			}
		}
	}

	private void generateAndAddChars(RangeEntry range, int lowx, int highx) {
		Integer unicodex = range.getUnicode();
		String code = range.getCode();
		
		for (Integer i = lowx; i <= highx; i++) {
			int offset = i - lowx;
			if (!cmapPointMap.containsKey(i)) {
				throw new RuntimeException("Outside range");
			}
			CMapPoint cmapPoint = (unicodex != null) ? new CMapPoint(unicodex + offset) : new CMapPoint(code, offset);
			cmapPointMap.put(i, cmapPoint);
		}
	}

	private void addBfChars() {
		addChars(bfCharEntryList);
	}

	private void addCidChars() {
		addChars(cidCharEntryList);
	}

	private void addChars(List<CharMapEntry> charList) {
		if (charList != null) {
			for (CharMapEntry charEntry: charList) {
				LOG.trace("entry "+charEntry);
				Integer orig = charEntry.getOriginal();
				Integer unicode = charEntry.getUnicode();
				String code = charEntry.getCode();
				LOG.trace("> "+code+" "+unicode);
				CMapPoint cmapPoint = null;
				if (unicode != null) {
					cmapPoint = new CMapPoint(unicode);
				} else {
					try {
					cmapPoint = new CMapPoint(code);
					} catch (Exception e) {
						throw new RuntimeException("cannot parse serial: "+orig, e);
					}
					LOG.trace("fromCode: "+cmapPoint.getStringRepresentation());
				}
				cmapPoint.setName(charEntry.getName());
				cmapPointMap.put(orig, cmapPoint);
			}
		}
	}

	public String debug() {
		String s = "";
		if (codespaceRangeEntryList != null) {
			if (codespaceRangeEntryList.size() == 1) {
				s += "codespaceRangeEntryList: "+codespaceRangeEntryList.get(0)+"\n";
			}
		}
		if (bfRangeEntryList != null) {
			s += "bfRangeEntryList: "+bfRangeEntryList.size()+"\n";
		}
		if (bfCharEntryList != null) {
			s += "bfCharEntryList: "+bfCharEntryList.size()+"\n";
		}
		if (cidRangeEntryList != null) {
			s += "cidRangeEntryList: "+cidRangeEntryList.size()+"\n";
		}
		if (cidCharEntryList != null) {
			s += "cidCharEntryList: "+cidCharEntryList.size()+"\n";
		}
		return s;
	}

	private void write(File outputFile) throws IOException {
		CodePointSet codePointSet = createCodePointSet();
//		Serializer serializer = new NumericSerializer(new FileOutputStream(outputFile), "UTF-8");
		Serializer serializer = new Serializer(new FileOutputStream(outputFile), "UTF-8");
		serializer.setIndent(1);
//		serializer.setUnicodeNormalizationFormC(true);
		serializer.write(new Document(codePointSet));
		LOG.debug("wrote "+outputFile);
	}

	private CodePointSet createCodePointSet() {
		CodePointSet codePointSet = new CodePointSet();
		codePointSet.setEncoding(cmapName);
		codePointSet.setId(cmapName.toLowerCase());
		Integer[] keyList = getSortedCMapPoints();
		for (Integer ii : keyList) {
			CMapPoint cmapPoint = cmapPointMap.get(ii);
			Integer codePointValue = cmapPoint.getValue();
			String stringRep = cmapPoint.getStringRepresentation();
			CodePoint codePoint = null;
			if (codePointValue != null) {
				codePoint = new CodePoint(ii, "<"+codePointValue+">");
				codePoint.setUnicodePoint(new UnicodePoint(codePointValue));
				codePoint.setName(cmapPoint.getName());
			} else { 
				codePoint = new CodePoint(ii, stringRep); 
			}
			codePoint.setName(cmapPoint.getName());
			codePoint.setStringRepresentation(cmapPoint.getStringRepresentation());
			codePointSet.add(codePoint);
		}
		return codePointSet;
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Read a CMAP (postscript) file");
			System.out.println("CMap <filename>");
			System.exit(0);
		}
		try {
			String filename = args[0];
			List<CMap> cmapList = CMap.read(new File(filename), new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getPath().endsWith(".cmap");
				}
			});
			for (CMap cmap : cmapList) {
				String path = null;
				try {
					path = cmap.getInputPath();
					File outputPath = new File(path+".xml");
					cmap.write(outputPath);
				} catch (Exception e) {
					LOG.debug("Error in writing file: "+path + e);
				}
			}
		} catch (Exception e) {
			LOG.debug("Error: "+args[0] + e);
		}
		
	}

}
