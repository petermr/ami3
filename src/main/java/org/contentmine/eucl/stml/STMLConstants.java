/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.eucl.stml;

import org.contentmine.eucl.euclid.EC;
import org.contentmine.eucl.xml.XMLConstants;

import nu.xom.XPathContext;

/**
 * 
 * <p>
 * Constants
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public interface STMLConstants extends XMLConstants {

	public enum Role {
		GROUP
		;
		private Role() {
			;
		}
	}
	
	/** list of identifiers.
	 * not exhaustive - string values can be used)
	 * @author pm286
	 *
	 */
	public enum Convention {
		/** CML */
		ATOMARRAY("atomArray"),
		
		/** authorities */
		/** not sure*/
		MET3D("3DMET"),
		/** Anatomical Therapeutic Chemical Classification System for drugs */
		ATC("ATC"),
		/** */
		BEILSTEIN("Beilstein"),
		/** Chemical Abstracts*/
		CAS("CAS"),
		/** Pubchem compound Id*/
		CID("CID"),
		/** Harvard */
		DRUGBANK("DrugBank"),
		/** EBI chemistry*/
		CHEBI("ChEBI"),
		/** European chemicals*/
		EINECS("EINECS"),
		/** */
		GMELIN("Gmelin"),
		/** */
		INCHI("InChI"),
		/** Int Union of Pure and Applied Chemistry */
		IUPAC("IUPAC"),
		/** Japan database*/
		KEGG("KEGG"),
		/** PubMed terminology*/
		MESH("MeSH"),
		/** PubChem*/
		PUBCHEM("PubChem"),
		/** Chemical labelling*/
		RTECS("RTECS"),
		/** SMILES line notation*/
		SMILES("SMILES"),
		
		/** other */
		EXTERNAL("external"),
		
		;
		
		
		/** */
		public final String v;
		private Convention(String s) {
			v = s;
		}
		/**
		 * equality to value
		 * @param s
		 * @return tru if match
		 */
		public boolean equals(String s) {
			return v == s;
		}
	}

    /** common units in chemistry */
    public enum Units {
    	/** mass*/
    	GRAM			("units:g"),
    	/** density*/
    	GRAM_PER_CMCUBED("units:g.cm-3"),
    	/** molarMass*/
    	GRAM_PER_MOLE("units:g.mol-1"),
    	/** volume */
    	CMCUBED			("units:cm3"),
    	/** volume */
    	ML				("units:ml"),
    	/** volume */
    	L				("units:l"),
    	/** amount */
    	MOL				("units:mol"),
    	/** amount */
    	MMOL			("units:mmol"),
    	;
        /** dewisott */
    	public final String value;
    	private Units(String s) {
    		value = s;
    	}
    	/**
    	 * @return string
    	 */
    	public String toString() {
    		return value;
    	}
    };
    
	
    
    /** element types */
    
    
    
    /** constant */
    String CMLXSD_ANNOTATION = "annotation";

    /** constant */
    String CMLXSD_ANY = "any";

    /** constant */
    String CMLXSD_APPINFO = "appinfo";

    /** constant */
    String CMLXSD_ATTRIBUTE = "attribute";

    /** constant */
    String CMLXSD_ATTRIBUTEGROUP = "attributeGroup";

    /** constant */
    String CMLXSD_BASE = "base";

    /** constant */
    String CMLXSD_CHOICE = "choice";

    /** constant */
    String CMLXSD_COMPLEXTYPE = "complexType";

    /** constant */
    String CMLXSD_DOCUMENTATION = "documentation";

    /** constant */
    String CMLXSD_ELEMENT = "element";

    /** constant */
    String CMLXSD_ENUMERATION = "enumeration";

    /** constant */
    String CMLXSD_EXTENSION = "extension";

    /** constant */
    String CMLXSD_ID = "id";

    /** constant */
    String CMLXSD_ITEMTYPE = "itemType";

    /** constant */
    String CMLXSD_LENGTH = "length";

    /** constant */
    String CMLXSD_LIST = "list";

    /** constant */
    String CMLXSD_MAXEXCLUSIVE = "maxExclusive";

    /** constant */
    String CMLXSD_MAXINCLUSIVE = "maxInclusive";

    /** constant */
    String CMLXSD_MINEXCLUSIVE = "minExclusive";

    /** constant */
    String CMLXSD_MININCLUSIVE = "minInclusive";

    /** constant */
    String CMLXSD_NAME = "name";

    /** constant */
    String CMLXSD_PATTERN = "pattern";

    /** constant */
    String CMLXSD_REF = "ref";

    /** constant */
    String CMLXSD_RESTRICTION = "restriction";

    /** constant */
    String CMLXSD_ROOT = "root";

    /** constant */
    String CMLXSD_SEQUENCE = "sequence";

    /** constant */
    String CMLXSD_SIMPLECONTENT = "simpleType";

    /** constant */
    String CMLXSD_SIMPLETYPE = "simpleType";

    /** constant */
    String CMLXSD_TEXT = "text";

    /** constant */
    String CMLXSD_TYPE = "type";

    /** constant */
    String CMLXSD_UNBOUNDED = "unbounded";

    /** constant */
    String CMLXSD_UNION = "union";

    /** constant */
    String CMLXSD_VALUE = "value";

    /** constant */
    String CMLXSD_ATTPREFIX = "_att_";

    /** constant */
    String CMLXSD_XMLCONTENT = "_xmlContent";

    /** CMLX prefix (cmlx) for experimentation and development
     */
    String CMLX_PREFIX = "cmlx";

    /** root of all CML URIs */
    String STML_NS_BASE = "http://www.xml-cml.org";

    /** cmlx namespace */
    String STMLX_NS = STML_NS_BASE+EC.U_S+"schema"+S_SLASH+CMLX_PREFIX;

    /**
     * namespace declaration for CMLx with prefix
     */
    String CMLX_XMLNS_PREFIX = XMLNS + S_COLON + CMLX_PREFIX + S_EQUALS + S_APOS
            + STMLX_NS + S_APOS;

    /** constant */
//    String CML = CML_NS;

    /**
     * cml dictionary namespace reserved
     */
    String DICT_NS = STML_NS_BASE+EC.U_S+"dict";

    /** CML prefix (cml) reserved: for several uses
     */
    String STML_PREFIX = "stm";

    /** CML prefix + colon  (cml:)
     */
    String CML_COLON = STML_PREFIX+S_COLON;

    /** CML prefix when used as element namespace
     */
    String C_E = CML_COLON;

    /** CML prefix when used as attribute value namespace
     */
    String C_A = CML_COLON;

/** constant */
    String WARNING_S = "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!";

/** constant */
String STML_NS = STML_NS_BASE+EC.U_S+"schema";

/**
 * namespace declaration for CML without prefix
 */
String STML_XMLNS = XMLNS + S_EQUALS + S_APOS + STML_NS + S_APOS;

/**
 * namespace declaration for CML with prefix
 */
String SML_XMLNS_PREFIX = XMLNS + S_COLON + STML_PREFIX + S_EQUALS + S_APOS
        + STML_NS + S_APOS;

/** XPathContext for CML.
 */
XPathContext STML_XPATH = new XPathContext(STML_PREFIX, STML_NS);

    
//    /**
//     * units dictionary namespace reserved
//     */
//    String UNIT_NS = _UNIT_NS+EC.U_S+"units";

//    /**
//     * siUnits dictionary namespace reserved
//     */
//    String SIUNIT_NS = _UNIT_NS+EC.U_S+"siUnits";

//    /**
//     * unnitTypes dictionary namespace reserved
//     */
//    String UNITTYPES_NS = _UNIT_NS+EC.U_S+"unitTypes";


    // ================== crystal ================

//    /**
//     * dictRef ids for 6 scalar children of crystal.
//     */
//    String CRYSTAL_DICT_REFS[] = { CML_PREFIX + S_COLON + "a",
//            CML_PREFIX + S_COLON + "b", CML_PREFIX + S_COLON + "c",
//            CML_PREFIX + S_COLON + "alpha", CML_PREFIX + S_COLON + "beta",
//            CML_PREFIX + S_COLON + "gamma" };
//
//    /**
//     * unit refs for 6 scalar children of crystal.
//     */
//    String[] CRYSTAL_DICT_UNITS = { CML_UNITS + S_COLON + "ang",
//            CML_UNITS + S_COLON + "ang", CML_UNITS + S_COLON + "ang",
//            CML_UNITS + S_COLON + "degree", CML_UNITS + S_COLON + "degree",
//            CML_UNITS + S_COLON + "degree" };

    // ======= test ==========
//    /**
//     * number of dictionaries. has to be altered every time new dictionaries are
//     * added.
//     */
//    int NDICT = 4;
//
//    /**
//     * number of units dictionaries. has to be altered every time new units
//     * dictionaries are added.
//     */
//    int NUNIT_DICT = 5;
//
//    /**
//     * number of unitType dictionaries. has to be altered every time new units
//     * dictionaries are added.
//     */
//    int NUNIT_TYPE_DICT = 1;
////	public final String value;

}