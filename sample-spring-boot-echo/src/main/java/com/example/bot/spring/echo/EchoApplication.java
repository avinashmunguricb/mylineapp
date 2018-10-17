/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring.echo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.TreeMap;
import java.util.WeakHashMap;
import com.linecorp.bot.client.LineMessagingClientImpl;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.profile.UserProfileResponse;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;

@SpringBootApplication
@LineMessageHandler
public class EchoApplication {
	public static void main(String[] args) {
		SpringApplication.run(EchoApplication.class, args);
	}

	@EventMapping
	public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
		System.out.println("event: " + event);
		String returnMessage = "test";
		StringBuilder stringBuilder = new StringBuilder();
		String messageText = event.getMessage().getText();
		String tagName = " ==>Saahir ";
		stringBuilder.append(messageText);
		stringBuilder.append(tagName);
		
		String originalMessageText = event.getMessage().getText();
		String replyBotMessage = botReplies(originalMessageText);
		final String followedUserId = event.getSource().getUserId();
		
		LineMessagingClient client = LineMessagingClient
		        .builder("KvPdRVx9Zhye3c74GhvJ2u6HtyUgJFuZSKU22wD8IfodRKhBsw4fdkSey0q/xsa/VuPBrA9shefDEn49yb4xo8Yy6sPF1izTfsgnfmm1aU4hrZgBOQasXMZwHvRdFcvupcGeFxZd1/JeVrWd6V54QwdB04t89/1O/w1cDnyilFU=")
		        .build();
		UserProfileResponse userProfileResponse = null;
		try {
		    userProfileResponse = client.getProfile(followedUserId).get();
		    returnMessage = userProfileResponse.getDisplayName();
		} catch (InterruptedException | ExecutionException e) {
		    e.printStackTrace();
		}

		//System.out.println(userProfileResponse.getUserId());
		//System.out.println(userProfileResponse.getDisplayName());
		//System.out.println(userProfileResponse.getPictureUrl());
		
		
		sendToSalesforce();
		
		
		return new TextMessage(replyBotMessage);
	}

	@EventMapping
	public void handleDefaultMessageEvent(Event event) {
		
		System.out.println("event: " + event);
	}
	
	public String botReplies(String originalMessage) {
		String botReplyMessage = "Command not recognized !\nHere are a list of keywords : \n";
		Map<String, String> mapA = new HashMap<>();
		mapA.put("Hello", "Hello, How can i help you ?");
		mapA.put("Document", "The document is in drawer 5");
		mapA.put("Thank you", "Your welcome");
		
		for(String myKey : mapA.keySet()) {
			botReplyMessage += myKey+"\n";
			if(originalMessage.toLowerCase().contains(myKey.toLowerCase())) {
				botReplyMessage = mapA.get(myKey);
				break;
			}
		}		
		return botReplyMessage;
	}
	
	public void sendToSalesforce() {
		
		String USERNAME     = "saahir@intnet.mu.mytrailhead";
	    String PASSWORD     = "ShahTrailhead_000KHB1uHYpTtlhUCcOCNbU9BVar";
	    String LOGINURL     = "https://login.salesforce.com";
	    String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
	    String CLIENTID     = "3MVG9fTLmJ60pJ5LGPsq1bC62J9xkMwmkWqhBCllwqD7oLpqFThCbmvShnkWqMp6ZYB6HBp.gRjuKiQWeRhPr";
	    String CLIENTSECRET = "3160755402294534733";
		
	    String REST_ENDPOINT = "/services/data" ;
	    String API_VERSION = "/v43.0" ;
	    String baseUri;
	    Header oauthHeader;
	    Header prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
	    String contactId ;
	    
		HttpClient httpclient = HttpClientBuilder.create().build();
		 
        // Assemble the login request URL
        // Assemble the login request URL
        String loginURL = LOGINURL +
                          GRANTSERVICE +
                          "&client_id=" + CLIENTID +
                          "&client_secret=" + CLIENTSECRET +
                          "&username=" + USERNAME +
                          "&password=" + PASSWORD;
 
        // Login requests must be POSTs
        HttpPost httpPost = new HttpPost(loginURL);
        HttpResponse response = null;
 
        try {
            // Execute the login POST request
            response = httpclient.execute(httpPost);
        } catch (ClientProtocolException cpException) {
            cpException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
 
        // verify response is HTTP OK
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            System.out.println("Error authenticating to Force.com: "+statusCode);
            // Error is in EntityUtils.toString(response.getEntity())
            return;
        }
 
        String getResult = null;
        try {
            getResult = EntityUtils.toString(response.getEntity());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
 
        JSONObject jsonObject = null;
        String loginAccessToken = null;
        String loginInstanceUrl = null;
 
        try {
            jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
            loginAccessToken = jsonObject.getString("access_token");
            loginInstanceUrl = jsonObject.getString("instance_url");
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
 
        baseUri = loginInstanceUrl + REST_ENDPOINT + API_VERSION ;
        oauthHeader = new BasicHeader("Authorization", "OAuth " + loginAccessToken) ;
        System.out.println("oauthHeader1: " + oauthHeader);
        System.out.println("\n" + response.getStatusLine());
        System.out.println("Successful login");
        System.out.println("instance URL: "+loginInstanceUrl);
        System.out.println("access token/session ID: "+loginAccessToken);
        System.out.println("baseUri: "+ baseUri);        
 
        // Run codes to query, isnert, update and delete records in Salesforce using REST API
        //createLeads();
        
        createContact();
 
        // release connection
        httpPost.releaseConnection();
	}
	
	// Create Contact using REST HttpPost
    public void createContact() {
        System.out.println("\n_______________ contact INSERT _______________");
 
        String uri = baseUri + "/sobjects/Contact/";
        try {
 
            //create the JSON object containing the new contact details.
            JSONObject contact = new JSONObject();
            contact.put("FirstName", "James");
            contact.put("LastName", "Bale");
 
            System.out.println("JSON for contact record to be inserted:\n" + contact.toString(1));
 
            //Construct the objects needed for the request
            HttpClient httpClient = HttpClientBuilder.create().build();
 
            HttpPost httpPost = new HttpPost(uri);
            httpPost.addHeader(oauthHeader);
            httpPost.addHeader(prettyPrintHeader);
            // The message we are going to post
            StringEntity body = new StringEntity(contact.toString(1));
            body.setContentType("application/json");
            httpPost.setEntity(body);
 
            //Make the request
            HttpResponse response = httpClient.execute(httpPost);
 
            //Process the results
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 201) {
                String response_string = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(response_string);
                // Store the retrieved contact id to use when we update the contact.
                contactId = json.getString("id");
                System.out.println("New contact id from response: " + contactId);
            } else {
                System.out.println("Insertion unsuccessful. Status code returned is " + statusCode);
            }
        } catch (JSONException e) {
            System.out.println("Issue creating JSON or processing results");
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    } 
}
