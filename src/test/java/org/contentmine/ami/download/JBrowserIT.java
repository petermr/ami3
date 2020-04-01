package org.contentmine.ami.download;

import org.junit.Assert;
import org.junit.Test;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
    

public class JBrowserIT {

	@Test
	/** from the developers
	 * 
	 */
	  public void testExample() {

	    // You can optionally pass a Settings object here,
	    // constructed using Settings.Builder
	    JBrowserDriver driver = new JBrowserDriver(Settings.builder().
	      timezone(Timezone.AMERICA_NEWYORK).build());

	    // This will block for the page load and any
	    // associated AJAX requests
	    driver.get("http://example.com");

	    // You can get status code unlike other Selenium drivers.
	    // It blocks for AJAX requests and page loads after clicks 
	    // and keyboard events.
	    System.out.println(driver.getStatusCode());

	    // Returns the page source in its current state, including
	    // any DOM updates that occurred after page load
	    System.out.println(driver.getPageSource());
	    
	    // Close the browser. Allows this thread to terminate.
	    driver.quit();
	  }
	
	@Test
	/** from the developers
	 * 
	 */
	  public void testCOS() {

	    JBrowserDriver driver = new JBrowserDriver(Settings.builder().
	      timezone(Timezone.AMERICA_NEWYORK).build());
	    /** this works with ChemrXiv
	     * 
	     */
	    driver.get("https://chemrxiv.org/?q=essential%20oils&searchMode=1");
	    String s = driver.getPageSource();
	    Assert.assertTrue("S ", s.length() > 1000);
	    
	    /** it fails with OSF
	     * because requires Java certificate
	     * (symptom is a null object)
	     */
//	    driver.get("https://osf.io/search/?q=coronavirus&filter=file&page=1");
	    driver.get("https://osf.io/search/?q=coronavirus");
	    /** this throws:
	     * org.openqa.selenium.NoSuchElementException: Element not found or does not exist.
For documentation on this error, please visit: https://www.seleniumhq.org/exceptions/no_such_element.html
Build info: version: '4.0.0-alpha-2', revision: 'f148142cf8', time: '2019-07-01T20:55:26'
System info: host: 'MacBook-Pro-3.local', ip: 'fe80:0:0:0:4b9:7009:b3f4:a5e8%en0', os.name: 'Mac OS X', os.arch: 'x86_64', os.version: '10.14.6', java.version: '1.8.0_60'
Driver info: driver.version: unknown
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	(this is due to a null object, due to security fail)

	     */
	    System.out.println("SC "+driver.getStatusCode());
	    System.out.println("SOU "+driver.getPageSource());
	    
	    /** Indonesia */
	    driver.get("https://osf.io/preprints/inarxiv/discover?q=climate%20change");
	    
	    s = driver.getPageSource();
	    System.out.println("IND "+s);
	    	
	    driver.quit();
	  }

}
