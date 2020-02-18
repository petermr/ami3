package org.contentmine.ami.download;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.junit.Assert;
import org.junit.Test;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;

public class CacertsTest {

	private static final String TRUSTSTORE_JKS = "truststore.jks";
	private static final String DEFAULT_PASSWD = "changeit";

	@Test
	public void testSSL() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, KeyManagementException {
	/*
	 * 	You could implement this using a X509TrustManager.
		Obtain an SSLContext with
	*/	
		SSLContext ctx = SSLContext.getInstance("TLS");
		KeyStore keyStore = KeyStore.getInstance("JKS");
		FileInputStream in = new FileInputStream(
				"/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/security/cacerts");
		keyStore.load(in, DEFAULT_PASSWD.toCharArray());
	
		/**
		Then initialize it with your custom X509TrustManager by using SSLContext#init. The SecureRandom and the KeyManager[] 
		may be null. The latter is only useful if you perform client authentication, if in your scenario only the 
		server needs to authenticate you don't need to set it.
		From this SSLContext, get your SSLSocketFactory using SSLContext#getSocketFactory and proceed as planned.
		As concerns your X509TrustManager implementation, it could look like this:
	*/
//		ctx.init(km, tm, random);
//	    keyStore.addCertificate(MATCHING_HOST, PORT1, mCert3);
//
//	    X509TrustManager trustManager = TrustManagerFactory.get(MATCHING_HOST, PORT1);
//	    trustManager.checkServerTrusted(new X509Certificate[] { mCert3, mCaCert }, "authType");
		TrustManager[] tmArray = createTrustManagers(keyStore);
//		TrustManager[] tmArray = new TrustManager[] {createTrustManager1(keyStore)};
//		TrustManager[] tmArray = new TrustManager[] {tm};
		ctx.init(null, tmArray, null);
		
		
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
//			    driver.get("https://osf.io/search/?q=coronavirus&filter=file&page=1");
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
	
    public void checkClientTrusted(X509Certificate[] chain,
                    String authType)
                    throws CertificateException {
        //do nothing, you're the client
    }

    public X509Certificate[] getAcceptedIssuers() {
        //also only relevant for servers
    	return null;
    }

    public void checkServerTrusted(X509Certificate[] chain,
                    String authType)
                    throws CertificateException {
        /* chain[chain.length -1] is the candidate for the
         * root certificate. 
         * Look it up to see whether it's in your list.
         * If not, ask the user for permission to add it.
         * If not granted, reject.
         * Validate the chain using CertPathValidator and 
         * your list of trusted roots.
         */
    }

	/**
	Edit:

	Ryan was right, I forgot to explain how to add the new root to the existing ones. 
	Let's assume your current KeyStore of trusted roots was derived from cacerts 
	(the 'Java default trust store' that comes with your JDK, located under jre/lib/security).
	 I assume you loaded that key store (it's in JKS format) with KeyStore#load(InputStream, char[]).
*/
	    
	/**
	The default password to cacerts is "changeit" if you haven't, well, changed it.
	Then you may add addtional trusted roots using KeyStore#setEntry. You can omit the ProtectionParameter 
	(i.e. null), the KeyStore.Entry would be a TrustedCertificateEntry that takes the new root as parameter 
	to its constructor.
*/
//	KeyStore.Entry newEntry = new KeyStore.TrustedCertificateEntry(newRoot);
//	ks.setEntry("someAlias", newEntry, null);
/**
	If you'd like to persist the altered trust store at some point, you may achieve this with 
	*/
//	KeyStore#store(OutputStream, char[].
			/**
*/
    /** create TrustManager 
     * @throws KeyManagementException 
     * @throws KeyStoreException 
     * @throws NoSuchAlgorithmException */
    
    private TrustManager[] createTrustManagers(KeyStore keyStore) throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
    	TrustManager[] trustManagers;
	    String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
	    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(defaultAlgorithm);
	    trustManagerFactory.init(keyStore);
	    trustManagers = trustManagerFactory.getTrustManagers();
	    SSLContext sslContext = SSLContext.getInstance("TLS");
	    sslContext.init(null, trustManagers, null);
	    TrustManager trustManager = (X509TrustManager) trustManagers[0];
//	    X509Certificate[] acceptedIssuers = trustManager.getAcceptedIssuers();
//	    for (X509Certificate acceptedIssuer : acceptedIssuers) {
//	      logger.info("installed cert details: subject={} issuer={}",
//	          acceptedIssuer.getSubjectX500Principal(), acceptedIssuer.getIssuerX500Principal());
//	    }
	    return trustManagers;
    }
    
    private X509TrustManager createTrustManager1(KeyStore keyStore) throws NoSuchAlgorithmException, KeyStoreException,
    CertificateException, IOException, KeyManagementException {
    	TrustManagerFactory tmf = TrustManagerFactory
    		    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
    		// Using null here initialises the TMF with the default trust store.
    		tmf.init((KeyStore) null);

    		// Get hold of the default trust manager
    		X509TrustManager defaultTm = null;
    		for (TrustManager tm : tmf.getTrustManagers()) {
    		    if (tm instanceof X509TrustManager) {
    		        defaultTm = (X509TrustManager) tm;
    		        break;
    		    }
    		}

    		FileInputStream myKeys = new FileInputStream(TRUSTSTORE_JKS);

    		// Do the same with your trust store this time
    		// Adapt how you load the keystore to your needs
    		KeyStore myTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    		myTrustStore.load(myKeys, DEFAULT_PASSWD.toCharArray());

    		myKeys.close();

    		tmf = TrustManagerFactory
    		    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
    		tmf.init(myTrustStore);

    		// Get hold of the default trust manager
    		X509TrustManager myTm = null;
    		for (TrustManager tm : tmf.getTrustManagers()) {
    		    if (tm instanceof X509TrustManager) {
    		        myTm = (X509TrustManager) tm;
    		        break;
    		    }
    		}

    		// Wrap it in your own class.
    		final X509TrustManager finalDefaultTm = defaultTm;
    		final X509TrustManager finalMyTm = myTm;
    		X509TrustManager customTm = new X509TrustManager() {
    		    @Override
    		    public X509Certificate[] getAcceptedIssuers() {
    		        // If you're planning to use client-cert auth,
    		        // merge results from "defaultTm" and "myTm".
    		        return finalDefaultTm.getAcceptedIssuers();
    		    }

    		    @Override
    		    public void checkServerTrusted(X509Certificate[] chain,
    		            String authType) throws CertificateException {
    		        try {
    		            finalMyTm.checkServerTrusted(chain, authType);
    		        } catch (CertificateException e) {
    		            // This will throw another CertificateException if this fails too.
    		            finalDefaultTm.checkServerTrusted(chain, authType);
    		        }
    		    }

    		    @Override
    		    public void checkClientTrusted(X509Certificate[] chain,
    		            String authType) throws CertificateException {
    		        // If you're planning to use client-cert auth,
    		        // do the same as checking the server.
    		        finalDefaultTm.checkClientTrusted(chain, authType);
    		    }
    		};


    		SSLContext sslContext = SSLContext.getInstance("TLS");
    		sslContext.init(null, new TrustManager[] { customTm }, null);

    		// You don't have to set this as the default context,
    		// it depends on the library you're using.
    		SSLContext.setDefault(sslContext);
    		return myTm;
    }
}
