package org.contentmine.eucl.xml;

import java.io.IOException;
import java.io.StringReader;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Text;

public class CDataFactory {

	    private static final Text PROTOTYPE;  // this is really an instance of CDATASection
	    
	    static
	    {
	        Text temp = null;
	        
	        try
	        {
	            // XOM preserves existing CDATA's so start with a doc that has one
	            String docWithCDATA = "<root><![CDATA[prototype]]></root>";
	            
	            Builder builder = new Builder();
	            Document document = builder.build(new StringReader(docWithCDATA));
	            
	            // grab the resulting CDATASection and keep it around as a prototype
	            temp = (Text) document.getRootElement().getChild(0);
	            temp.detach();
	        }
	        catch (IOException e)
	        {
	            // not worried about IOExceptions just reading a string
	        }
	        catch (ParsingException e)
	        {
	            // already know this document is valid and will parse
	        }
	        
	        PROTOTYPE = temp;
	    }
	    
	    public static Text makeCDATASection(String value)
	    {
	        // use copy and setValue to get a brand new CDATA section
	        Text result = (Text) PROTOTYPE.copy();
	        result.setValue(value);
	        return result;
	    }
}
