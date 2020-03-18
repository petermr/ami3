package org.contentmine.norma.download;

import javax.ws.rs.client.Client;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

public class EPMCDownloadTest {
	private static final Logger LOG = Logger.getLogger(EPMCDownloadTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Client client;
	private WebTarget target;

	/**
	 * fails with 
	 * javax.ws.rs.ProcessingException: RESTEASY004655: Unable to invoke request
	at org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine.invoke(ApacheHttpClient4Engine.java:321)
	at org.jboss.resteasy.client.jaxrs.internal.ClientInvocation.invoke(ClientInvocation.java:439)
	at org.jboss.resteasy.client.jaxrs.internal.ClientInvocation.invoke(ClientInvocation.java:460)
	at org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder.get(ClientInvocationBuilder.java:189)
	at org.contentmine.norma.download.EPMCDownloadTest.testClient(EPMCDownloadTest.java:31)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
	at org.junit.runners.BlockJUnit4ClassRunner.runNotIgnored(BlockJUnit4ClassRunner.java:79)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:71)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:49)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:193)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:52)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:191)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:42)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:184)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:236)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:86)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)
Caused by: javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
	at sun.security.ssl.Alerts.getSSLException(Alerts.java:192)
	at sun.security.ssl.SSLSocketImpl.fatal(SSLSocketImpl.java:1949)
	at sun.security.ssl.Handshaker.fatalSE(Handshaker.java:302)
	at sun.security.ssl.Handshaker.fatalSE(Handshaker.java:296)
	at sun.security.ssl.ClientHandshaker.serverCertificate(ClientHandshaker.java:1506)
	at sun.security.ssl.ClientHandshaker.processMessage(ClientHandshaker.java:216)
	at sun.security.ssl.Handshaker.processLoop(Handshaker.java:979)
	at sun.security.ssl.Handshaker.process_record(Handshaker.java:914)
	at sun.security.ssl.SSLSocketImpl.readRecord(SSLSocketImpl.java:1062)
	at sun.security.ssl.SSLSocketImpl.performInitialHandshake(SSLSocketImpl.java:1375)
	at sun.security.ssl.SSLSocketImpl.startHandshake(SSLSocketImpl.java:1403)
	at sun.security.ssl.SSLSocketImpl.startHandshake(SSLSocketImpl.java:1387)
	at org.apache.http.conn.ssl.SSLConnectionSocketFactory.createLayeredSocket(SSLConnectionSocketFactory.java:396)
	at org.apache.http.conn.ssl.SSLConnectionSocketFactory.connectSocket(SSLConnectionSocketFactory.java:355)
	at org.apache.http.impl.conn.DefaultHttpClientConnectionOperator.connect(DefaultHttpClientConnectionOperator.java:142)
	at org.apache.http.impl.conn.PoolingHttpClientConnectionManager.connect(PoolingHttpClientConnectionManager.java:373)
	at org.apache.http.impl.execchain.MainClientExec.establishRoute(MainClientExec.java:394)
	at org.apache.http.impl.execchain.MainClientExec.execute(MainClientExec.java:237)
	at org.apache.http.impl.execchain.ProtocolExec.execute(ProtocolExec.java:185)
	at org.apache.http.impl.execchain.RetryExec.execute(RetryExec.java:89)
	at org.apache.http.impl.execchain.RedirectExec.execute(RedirectExec.java:110)
	at org.apache.http.impl.client.InternalHttpClient.doExecute(InternalHttpClient.java:185)
	at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:83)
	at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:56)
	at org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine.invoke(ApacheHttpClient4Engine.java:317)
	... 27 more
Caused by: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
	at sun.security.validator.PKIXValidator.doBuild(PKIXValidator.java:387)
	at sun.security.validator.PKIXValidator.engineValidate(PKIXValidator.java:292)
	at sun.security.validator.Validator.validate(Validator.java:260)
	at sun.security.ssl.X509TrustManagerImpl.validate(X509TrustManagerImpl.java:324)
	at sun.security.ssl.X509TrustManagerImpl.checkTrusted(X509TrustManagerImpl.java:229)
	at sun.security.ssl.X509TrustManagerImpl.checkServerTrusted(X509TrustManagerImpl.java:124)
	at sun.security.ssl.ClientHandshaker.serverCertificate(ClientHandshaker.java:1488)
	... 47 more
Caused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
	at sun.security.provider.certpath.SunCertPathBuilder.build(SunCertPathBuilder.java:146)
	at sun.security.provider.certpath.SunCertPathBuilder.engineBuild(SunCertPathBuilder.java:131)
	at java.security.cert.CertPathBuilder.build(CertPathBuilder.java:280)
	at sun.security.validator.PKIXValidator.doBuild(PKIXValidator.java:382)
	... 53 more


	 */
	@Test
	@Ignore // not yet debugged
	public void testClient() {
	    client = ClientBuilder.newClient();
	    target = client.target("https://www.ebi.ac.uk/europepmc/webservices/rest/search?")
	       .queryParam("query", "malaria")
	       .queryParam("format", "xml");
	    String q ="malaria";
	    String result = target.queryParam("query", q)
            .request(MediaType.APPLICATION_JSON)
            .get(String.class);
            ;
	    LOG.debug(result);
	}

	/**
https://alvinalexander.com/java/java-apache-httpclient-restful-client-examples
	 */
	@Test
	public void testHTTPClient() {
	    String epmc = "https://www.ebi.ac.uk/europepmc";
		String empcSearch = (epmc + "/webservices/rest/search?");
	    String q ="marchantia";
	    String restUrl = empcSearch+"query="+q;
	    HttpClientBuilder builder = HttpClientBuilder.create();
	    HttpClient client =  builder.build();
        try {
          // specify the host, protocol, and port
//            HttpHost target = new HttpHost("weather.yahooapis.com", 80, "http");
//            HttpHost target = new HttpHost("www.ebi.ac.uk", 80, "https");
// curl -k "https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=marchantia"
        	// "https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=marchantia&format=xml&pageSize=1000&cursorMark=AoIIPrpWuSgyMzc1NDgxNQ=="
        	
        HttpHost target = new HttpHost("www.ebi.ac.uk", 443, "https");
          
          // specify the get request
        String requestString = "/europepmc/webservices/rest/search?";
        requestString += "query=marchantia";
//        HttpGet getRequest = new HttpGet("/forecastrss?p=80020&u=f");
        HttpGet getRequest = new HttpGet(requestString);

        System.out.println("executing request to " + target);

        HttpResponse httpResponse = client.execute(target, getRequest);
        HttpEntity entity = httpResponse.getEntity();

        System.out.println("----------------------------------------");
        System.out.println(httpResponse.getStatusLine());
        Header[] headers = httpResponse.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            System.out.println(headers[i]);
        }
      System.out.println("----------------------------------------");

          if (entity != null) {
            System.out.println(EntityUtils.toString(entity));
          }

        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          // When HttpClient instance is no longer needed,
          // shut down the connection manager to ensure
          // immediate deallocation of all system resources
//          httpclient.getConnectionManager().shutdown();
        }
      }
	
	public void testEPMC() {

//		a typical Java code to call the service would be like the following:

//		import org.springframework.http.HttpEntity;
//		import org.springframework.http.HttpHeaders;
//		import org.springframework.http.HttpMethod;
//		import org.springframework.http.MediaType;
//		import org.springframework.http.ResponseEntity;
//		import org.springframework.util.LinkedMultiValueMap;
//		import org.springframework.util.MultiValueMap;
//		import org.springframework.web.client.RestTemplate;

		// SPRING example
//			RestTemplate restTemplate = new RestTemplate();
//			
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//			params.add("query", "paracetamol");
//			params.add("sort", "FIRST_PDATE_D desc");
//			params.add("resultType", "core");
//			params.add("pageSize", "50");
//			params.add("format", "xml");
//
//	        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<MultiValueMap<String,String>>(params, headers);
//	               
//	        ResponseEntity<String> response = null;
//
//	        String url="https://www.ebi.ac.uk/europepmc/webservices/rest/searchPOST";	
//			response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//			
//			System.out.println(response.getBody().toString());
				
		}
//	}

}
