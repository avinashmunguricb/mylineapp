package salesforce_rest;
 
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

public class SendToSalesforce {
	
	static final String USERNAME     = "saahir@intnet.mu.mytrailhead";
    static final String PASSWORD     = "ShahTrailhead_000KHB1uHYpTtlhUCcOCNbU9BVar";
    static final String LOGINURL     = "https://login.salesforce.com";
    static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    static final String CLIENTID     = "3MVG9fTLmJ60pJ5LGPsq1bC62J9xkMwmkWqhBCllwqD7oLpqFThCbmvShnkWqMp6ZYB6HBp.gRjuKiQWeRhPr";
    static final String CLIENTSECRET = "3160755402294534733";
	
    private static String REST_ENDPOINT = "/services/data" ;
    private static String API_VERSION = "/v43.0" ;
    private static String baseUri;
    private static Header oauthHeader;
    private static Header prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
    private static String leadId ;
    private static String contactId ;

    
    public static void main(String[] args) {
    	 
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
	
    // Create Leads using REST HttpPost
    public static void createLeads() {
        System.out.println("\n_______________ Lead INSERT _______________");
 
        String uri = baseUri + "/sobjects/Lead/";
        try {
 
            //create the JSON object containing the new lead details.
            JSONObject lead = new JSONObject();
            lead.put("FirstName", "REST API");
            lead.put("LastName", "Lead");
            lead.put("Company", "trial company");
 
            System.out.println("JSON for lead record to be inserted:\n" + lead.toString(1));
 
            //Construct the objects needed for the request
            HttpClient httpClient = HttpClientBuilder.create().build();
 
            HttpPost httpPost = new HttpPost(uri);
            httpPost.addHeader(oauthHeader);
            httpPost.addHeader(prettyPrintHeader);
            // The message we are going to post
            StringEntity body = new StringEntity(lead.toString(1));
            body.setContentType("application/json");
            httpPost.setEntity(body);
 
            //Make the request
            HttpResponse response = httpClient.execute(httpPost);
 
            //Process the results
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 201) {
                String response_string = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(response_string);
                // Store the retrieved lead id to use when we update the lead.
                leadId = json.getString("id");
                System.out.println("New Lead id from response: " + leadId);
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
    
    // Create Contact using REST HttpPost
    public static void createContact() {
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
