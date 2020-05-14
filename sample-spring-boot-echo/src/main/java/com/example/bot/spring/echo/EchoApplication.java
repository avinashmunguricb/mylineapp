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
import java.util.*;

@SpringBootApplication
@LineMessageHandler
public class EchoApplication {
	String fullMessage = "";
	String loginAccessToken = null;
	Map<String, String> conversationPerUserMap = new HashMap<>();
	
	/*static final String USERNAME     = "dirish.bhaugeerutty@salesforcedev.com";
    static final String PASSWORD     = "salesforceDev2018!!!sio3s5t7ahENh3zToFKUL5mzU";
    static final String LOGINURL     = "https://login.salesforce.com";
    static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    static final String CLIENTID     = "3MVG9mIli7ewofGvDL7SHxI25zTKnJ.bGQw.FdgZHK0GYD4h4nYFUsVtXMFBTYQvCwRi2zzJMnBDJ.XAVBCj4";
    static final String CLIENTSECRET = "2949080712338965300";*/
	
	/*static final String USERNAME     = "saahir@intnet.mu.mytrailhead";
    static final String PASSWORD     = "ShahTrailhead_0068JiYLAVj0rR0l5umJ9qks3UM";
    static final String LOGINURL     = "https://login.salesforce.com";
    static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    static final String CLIENTID     = "3MVG9fTLmJ60pJ5LGPsq1bC62J9xkMwmkWqhBCllwqD7oLpqFThCbmvShnkWqMp6ZYB6HBp.gRjuKiQWeRhPr";
    static final String CLIENTSECRET = "3160755402294534733";*/
	
	static final String USERNAME     = "avinashdev@bfl.com";
    static final String PASSWORD     = "Icb2020!hTwCecjLy5CgNIzySYM5ADVi";
    static final String LOGINURL     = "https://login.salesforce.com";
    static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    static final String CLIENTID     = "3MVG9tzQRhEbH_K3zybNqWflnBsO7fCdlj.OEPy0Fr4_UFecxynREpXkTsd7.kHoWjLl04zHeOlElFWBuZBU0";
    static final String CLIENTSECRET = "32A054E23EB9B521297338EEDAEA9AAC0C7A0B442B4B24E16172A7B1B38AB2E6";
	
	
    private static String REST_ENDPOINT = "/services/data" ;
    private static String API_VERSION = "/v43.0" ;
    private static String baseUri;
    private static Header oauthHeader;
    private static Header prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
    private static String leadId ;
    private static String contactId ;
    
	public static void main(String[] args) {
		SpringApplication.run(EchoApplication.class, args);
	}

	@EventMapping
	public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
		System.out.println("event: " + event);
		String senderName = "Sender";
		
		String originalMessageText = event.getMessage().getText();
		String replyBotMessage = botReplies(originalMessageText);
		final String followedUserId = event.getSource().getUserId();
		
		LineMessagingClient client = LineMessagingClient
		        .builder("QbB2chPapgv5in9vOaYU1lSkostcLtnOclfmtRhkiucpGQERPD3jzHehiAW+9urDtgVu3ra0MGc+GXNgffshy5xcEdCJFVQTiRFdq8EfMEiNRtDg71X1oUx1cpnbAuMPtlWVSydhqiizYZ9jJ5OETgdB04t89/1O/w1cDnyilFU=")
		        .build();
		UserProfileResponse userProfileResponse = null;
		try {
		    userProfileResponse = client.getProfile(followedUserId).get();
		    senderName = userProfileResponse.getDisplayName();
		} catch (InterruptedException | ExecutionException e) {
		    e.printStackTrace();
		}
		
		if(conversationPerUserMap.containsKey(followedUserId)) {
			fullMessage = conversationPerUserMap.get(followedUserId);
		}
		
		fullMessage += senderName + " : " + originalMessageText + "\n" + "Bot : " + replyBotMessage + "\n\n";
		conversationPerUserMap.put(followedUserId, fullMessage);
		
		if(originalMessageText.toLowerCase().equalsIgnoreCase("end")) {
			sendToSalesforce(followedUserId, conversationPerUserMap.get(followedUserId));
			replyBotMessage += "\n\n" + conversationPerUserMap.get(followedUserId);
			fullMessage = "";
			conversationPerUserMap.put(followedUserId, fullMessage);
		}
		
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
		mapA.put("Fever", "Pren 2 panadol al dormi.");
		mapA.put("Headache", "Met de l'huile dans latet.");
		mapA.put("Medical3", "The medical 3 test");
		mapA.put("Medical4", "The medical 4 test");
		mapA.put("Medical5", "The medical 5 test");
		mapA.put("Medical6", "The medical 6 test");
		mapA.put("Medical7", "The medical 7 test");
		mapA.put("Thank you", "Your welcome");
		mapA.put("END", "---Session End---");
		
		for(String myKey : mapA.keySet()) {
			botReplyMessage += myKey+"\n";
			if(originalMessage.toLowerCase().contains(myKey.toLowerCase())) {
				botReplyMessage = mapA.get(myKey);
				break;
			}
		}		
		return botReplyMessage;
	}
	
	public void sendToSalesforce(String lineUserId, String conversation) {
		
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
        
        createContact(lineUserId, conversation);
 
        // release connection
        httpPost.releaseConnection();
	}
	
	// Create Contact using REST HttpPost
    public static void createContact(String lineUserId, String conversation) {
        System.out.println("\n_______________ contact INSERT _______________");
 
        String uri = baseUri + "/sobjects/Contact/";
        try {
 
            //create the JSON object containing the new contact details.
            JSONObject contact = new JSONObject();
            contact.put("FirstName", "James");
            contact.put("LastName", "Bale");
            contact.put("LineExternalId__c", lineUserId);
 
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
                createAttachment(contactId, conversation);
                
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
    
    // Insert attachment
    public static void createAttachment(String contactId, String conversation) {
 
        String uri = baseUri + "/sobjects/Attachment/";
        try {
        	// Encode data on your side using BASE64
        	String encoded = Base64.getEncoder().encodeToString(conversation.getBytes());   
			
            JSONObject attm = new JSONObject();
            attm.put("Name", "MyConversation.txt");
            attm.put("Body", encoded);
            attm.put("parentId", contactId);
 
            //Construct the objects needed for the request
            HttpClient httpClient = HttpClientBuilder.create().build();
 
            HttpPost httpPost = new HttpPost(uri);
            httpPost.addHeader(oauthHeader);
            httpPost.addHeader(prettyPrintHeader);
            // The message we are going to post
            StringEntity body = new StringEntity(attm.toString(1));
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
