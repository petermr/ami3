package org.contentmine.ami.tools.ferret;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import net.minidev.json.JSONObject;


public class Ferret_Example {
	static String FERRET_CONTAINER = "https://ferret-worker-pucbyp2omq-ue.a.run.app/";
    static String medrxiv_file = "org/contentmine/ami/tools/ferret/medrxiv_urls.fql";
    
	public static File getFileFromResources(String fileName) {

        ClassLoader classLoader = Ferret_Example.class.getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }
	
    
	public static void main(String[] args) throws IOException {

		String search_terms = "n95 masks";
		get_medrxiv_results(search_terms);	
}
	
	public static void get_medrxiv_results(String search_terms) throws MalformedURLException, IOException {
		String url = FERRET_CONTAINER;
		String medrxiv_url = "https://www.medrxiv.org/search/"+ URLEncoder.encode(search_terms, StandardCharsets.UTF_8);

        HttpURLConnection httpClient = (HttpURLConnection) new URL(FERRET_CONTAINER).openConnection();

        //add request header
        httpClient.setRequestMethod("POST");
        httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        httpClient.setRequestProperty("Content-Type", "application/json; utf-8");
        httpClient.setRequestProperty("Accept", "application/json");

        File medrxiv_fql = getFileFromResources(medrxiv_file);
        String content = new String(Files.readAllBytes(medrxiv_fql.toPath()));
        
        JSONObject data =new JSONObject();
        JSONObject params=new JSONObject();
		data.put("text", content);
	    params.put("url", medrxiv_url);
	    data.put("params", params);
	    // Send post request
        httpClient.setDoOutput(true);
       
        try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
            wr.writeBytes(data.toJSONString());
            wr.flush();
        }

        int responseCode = httpClient.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + data.toString());
        System.out.println("Response Code : " + responseCode);

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(httpClient.getInputStream()))) {

		            String line;
		            StringBuilder response = new StringBuilder();
		
		            while ((line = in.readLine()) != null) {
		                response.append(line);
            }

            //print result
            System.out.println(response.toString());
	}
	}
}