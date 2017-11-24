import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;


public class ClientImplementationJSON {
    

	public static WebTarget config() {
    	ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget service = client.target(getBaseURI());
        System.out.println("Calling server URL:  " + getBaseURI() );
        return service;
    }
	
	public static String format(String jsonString) throws IOException {
		 ObjectMapper mapper = new ObjectMapper();
		 Object JSON = mapper.readValue(jsonString, Object.class);
		 String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(JSON);
	     return json;
}
	
	public static void main(String[] args) throws Exception {
        WebTarget service = config();
        String result;
        int count;
        int first_person_id;
        int last_person_id;
		
        
        System.out.println("\nAPPLICATION/JSON");
        
        // Step 3.1.
        System.out.println("\nRequest#3.1");
        
        Response servResponse = service.path("person").request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
        String response = servResponse.readEntity(String.class);
      
        JSONArray arrayResponse = new JSONArray(response);
        count = arrayResponse.length();
        first_person_id = (Integer) arrayResponse.getJSONObject(0).get("personId");
        last_person_id =  (Integer) arrayResponse.getJSONObject(count-1).get("personId");
        System.out.println("Total number of persons in the database: " + count);
        System.out.println("First person's id: " + first_person_id);
        System.out.println("Last person's id: " + last_person_id);
        
        if(count>4) {
        	result = "OK";
        }else {
        	result = "ERROR";
        }
        System.out.println("\nRequest#1:" + "\n"
        		+ "GET /person/ Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse.getStatus() + " " + servResponse.getStatusInfo() + "\n"
        		+ "Body: "  + "\n"
        		+ format(response) + "\n");	
        
          
        // Step 3.2.
        System.out.println("\nRequest#3.2");
        
        Response servResponse2 = service.path("person").path(String.valueOf(first_person_id)).request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
        String response2 = servResponse2.readEntity(String.class);
        
        JSONObject object2 = new JSONObject(response2);
        String firstPersonInitialFirstname = (String) object2.get("firstName");
        
        if (servResponse2.getStatus()==200 || servResponse2.getStatus()==202) {
        	result = "OK";
        } else {
        	result ="ERROR";
        }
        System.out.println("\nRequest#2:" + "\n"
        		+ "GET /person/" + first_person_id + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse2.getStatus() + " " + servResponse2.getStatusInfo() + "\n"
        		+ "Body: "+"\n" + format(response2) + "\n");	
       
        // Step 3.3.
		Object entity3 = "{\"personId\":1,\"lastName\":\"Sonkiya\",\"birthDate\":1510354800000,\"activitypreference\":[{\"activityId\":2,\"name\":\"Squash\",\"description\":\"Squash Court in Sanbapolis\",\"place\":\"Trento\",\"type\":\"Sport\",\"startdate\":1510354800000},{\"activityId\":3,\"name\":\"Jogging\",\"description\":\"Jogging on Golden Gate Bridge\",\"place\":\"San Francisco\",\"type\":\"Sport\",\"startdate\":1510354800000}],\"firstName\":\"Rohit\"}";
		
		System.out.println("\nRequest#3.3");
        
        Response servResponse3a = service.path("person").path(String.valueOf(first_person_id)).request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").put(Entity.json(entity3));
        Response servResponse3b = service.path("person").path(String.valueOf(first_person_id)).request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
        String response3b = servResponse3b.readEntity(String.class);
        
        JSONObject object3b = new JSONObject(response3b);
        String firstPersonUpdatedFirstName = (String) object3b.get("firstName");
        
        if (!firstPersonInitialFirstname.equals(firstPersonUpdatedFirstName)) {
        	result = "OK";
        }else {
        	result = "ERROR";
        }
        
        System.out.println("\nRequest#3:" + "\n"
        		
        		+ "PUT /person/" + first_person_id + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse3a.getStatus() + " " + servResponse3a.getStatusInfo() + "\n"
        		+ "Updated person: " + format(response3b));
       	
        // Step 3.4.
        Object entity4 = "{\"lastName\":\"Chopra\",\"birthDate\":1510354800000,\"activitypreference\":[{\"name\":\"Cycle\",\"description\":\"Cycle from Jaipur to Ajmer\",\"place\":\"Rajasthan\",\"type\":\"Sport\",\"startdate\":1510354800000}],\"firstName\":\"Priyanka\"}";
		
		System.out.println("\nRequest#3.4");
        
        Response servResponse4 = service.path("person").request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").post(Entity.json(entity4));
        String response4 = servResponse4.readEntity(String.class);
        
        JSONObject object4 = new JSONObject(response4);
        int personIdNew = (Integer) object4.get("personId");
        
        if (personIdNew>-1 && (servResponse4.getStatus() == 200 || servResponse4.getStatus() == 201 || servResponse4.getStatus() == 202)) {
        	result = "OK";
        }else {
        	result = "ERROR";
        }
       
        System.out.println("\nRequest #4:" + "\n"
        		+ "POST /person/ Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse4.getStatus() + " " + servResponse4.getStatusInfo() + "\n"
        		+ "Body: "+"\n" +format(response4));	
        System.out.println("\n");
        System.out.println("New person added with id: "+personIdNew);
       
      
        //3.5.
        System.out.println("\nRequest#3.5");
        
        Response servResponse5a = service.path("person").path(String.valueOf(personIdNew)).request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").delete();
        Response servResponse5b = service.path("person").path(String.valueOf(personIdNew)).request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
      
        if (servResponse5b.getStatus()==404) {
        	result = "OK";
        } else {
        	result ="ERROR";
        }
        System.out.println("\nRequest#5:" + "\n"
        		+ "DELETE /person/" + personIdNew + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse5a.getStatus() + " " + servResponse5a.getStatusInfo());
    
        // Step 3.6.
        System.out.println("\nRequest#3.6");
        
        Response servResponse6 = service.path("activity_types").request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
        String response6 = servResponse6.readEntity(String.class);

        JSONArray array6 = new JSONArray(response6);
        int countOfActivityType = array6.length();
      
        List<String> listOfActivityTypes = new ArrayList<String>();
        for (int i=0;i<countOfActivityType;i++) {
        	listOfActivityTypes.add(array6.get(i).toString());
        }
        if(countOfActivityType>2) {
        	result = "OK";
        }else {
        	result = "ERROR";
        }
        System.out.println("\nRequest#6:" + "\n"
        		+ "GET /activity_type/ Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse6.getStatus() + " " + servResponse6.getStatusInfo() + "\n"
        		+ "Body: "  + "\n"
        		+ format(response6) + "\n");
      
        // Step 3.7.
        System.out.println("\nRequest#3.7");
        
        int countTotal=0;
        int activityCount=0;
        int activityId=-1;
        String type = "";
        System.out.println("\nRequest#7a:" +" Activities for the first person: "+ "\n");
        for (int i=0;i<listOfActivityTypes.size();i++) {
        	Response servResponse7a = service.path("person").path(String.valueOf(first_person_id)).path(listOfActivityTypes.get(i).toUpperCase()).request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
        	String response7a = servResponse7a.readEntity(String.class);
        	JSONArray array7 = new JSONArray(response7a);
            activityCount = array7.length();          
            if(activityCount>0) {
            	result = "OK";
            	countTotal++;
            	activityId = (Integer) array7.getJSONObject(0).get("activityId");
            	type = (String) array7.getJSONObject(0).get("type");
            }else {
            	result = "ERROR";
            }
            
            System.out.println("GET /person/" + first_person_id + "/" +  listOfActivityTypes.get(i) + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON" + "\n"
            		+ "=> Result: " + result +  "\n"
            		+ "=> HTTP Status: " + servResponse7a.getStatus() + " " + servResponse7a.getStatusInfo() + "\n"
            		+ "Body: "  + "\n"
            		+ format(response7a) + "\n");
        }
        
        System.out.println("\nRequest#7b:" +" Activities for the last person: "+ "\n");
        for (int i=0;i<listOfActivityTypes.size();i++) {
        	Response servResponse7b = service.path("person").path(String.valueOf(last_person_id)).path(listOfActivityTypes.get(i).toUpperCase()).request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
        	String response7b = servResponse7b.readEntity(String.class);
        	JSONArray array7b = new JSONArray(response7b);
            activityCount = array7b.length();    
            if(activityCount>0) {
            	result = "OK";
            	countTotal++;
            }else {
            	result = "ERROR";
            }
            System.out.println("GET /person/" + last_person_id + "/" +  listOfActivityTypes.get(i) + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON" + "\n"
            		+ "=> Result: " + result +  "\n"
            		+ "=> HTTP Status: " + servResponse7b.getStatus() + " " + servResponse7b.getStatusInfo() + "\n"
            		+ "Body: "  + "\n"
            		+ format(response7b) + "\n");
        }
        if (countTotal>0) {
        	System.out.println("Request#7 response:");
        	System.out.println("Activity id saved: " + activityId + "\nActivity type saved: " + type);
        }else {
        	System.out.println("Request#7 response: ERROR");
        }
        	
        //3.8.
        System.out.println("\nRequest#3.8");
        
        Response servResponse8 = service.path("person").path(String.valueOf(first_person_id)).path(type.toUpperCase()).path(String.valueOf(activityId)).request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
        String response8 = servResponse8.readEntity(String.class);
        if (servResponse8.getStatus()==200) {
        	result = "OK";
        } else {
        	result ="ERROR";
        }
        System.out.println("\nRequest#8:" + "\n"
          		+ "GET /person/" + first_person_id + "/" + type + "/" + activityId + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse8.getStatus() + " " + servResponse8.getStatusInfo() + "\n"
        		+ "Body: "  + "\n"
        		+ format(response8) + "\n");
        

       
        // Step 3.9.
        
        
        int countActivityType = 0;
        int countActivityTypeChanged = 0;
        
        
        Object entity9 = "{\"name\":\"Swimming\",\"description\":\"Swimming in the river\",\"place\":\"Adige river\",\"type\":\"Sport\",\"startdate\":1514447400000}";
		
        Response servResponse9a = service.path("person").path(String.valueOf(first_person_id)).path("SPORT").request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
    	String response9a = servResponse9a.readEntity(String.class);
    	JSONArray array9a = new JSONArray(response9a);
    	countActivityType = array9a.length();  
 
        
		System.out.println("\nRequest#3.9");
        
        Response servResponse9 = service.path("person").path(String.valueOf(first_person_id)).path("SPORT").request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").post(Entity.json(entity9));
        String response9 = servResponse9.readEntity(String.class);
      
        Response servResponse9b = service.path("person").path(String.valueOf(first_person_id)).path("SPORT").request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
    	String response9b = servResponse9b.readEntity(String.class);
    	JSONArray array9b = new JSONArray(response9b);
    	countActivityTypeChanged = array9b.length();  
 
        
        if (countActivityType < countActivityTypeChanged) {
        	result = "OK";
        }else {
        	result = "ERROR";
        }
        int newActivityId = array9b.getJSONObject(countActivityTypeChanged-1).getInt("activityId");
        System.out.println("New Activity Id "+newActivityId);
        System.out.println("\nRequest #9:" + "\n"
        		+ "POST /person/" + first_person_id + "/SPORT" +" Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse9.getStatus() + " " + servResponse9.getStatusInfo() + "\n"
        		+ "Body: " + "\n" +format(response9));	
        
        // Step 3.10. 
        Object entity10 = "{\"activityId\":"+newActivityId+", \"name\":\"Swimming\",\"description\":\"Swimming in the river\",\"place\":\"Adige river\",\"type\":\"Travel\",\"startdate\":1514447400000}";		
        Response servResponse10a = service.path("person").path(String.valueOf(first_person_id)).path("SPORT").path(String.valueOf(newActivityId)).request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
        String response10a = servResponse10a.readEntity(String.class);
        JSONArray array10a = new JSONArray(response10a);
        String initialType = array10a.getJSONObject(0).getString("type");
      
        System.out.println("\nRequest#3.10");
        
        Response servResponse10 = service.path("person").path(String.valueOf(first_person_id)).path("TRAVEL").path(String.valueOf(newActivityId)).request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").put(Entity.json(entity10));
        String response10 = servResponse10.readEntity(String.class);

        Response servResponse10b = service.path("person").path(String.valueOf(first_person_id)).path("TRAVEL").path(String.valueOf(newActivityId)).request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
        String response10b = servResponse10b.readEntity(String.class);
        JSONArray array10b = new JSONArray(response10b);
        String modifiedType = array10b.getJSONObject(0).getString("type");
           
        System.out.println("Initial activity type: "+ initialType);
        System.out.println("Modified activity type: "+ modifiedType);
        
        if (!modifiedType.equals(initialType)) {
        	result = "OK";
        }else {
        	result = "ERROR";
        }
        System.out.println("\nRequest #10:" + "\n"
        		+ "PUT /person/" + first_person_id + "/TRAVEL" + "/" + newActivityId +" Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse10.getStatus() + " " + servResponse10.getStatusInfo() + "\n"
        		+ "Body: " + "\n" + format(response10));	        

        // Step 3.11. 
        System.out.println("\nRequest#3.11");
        
    	Response servResponse11 = service.path("person").path(String.valueOf(first_person_id)).path(type.toUpperCase()).queryParam("before", "2017-12-28T08:50:00").queryParam("after", "2017-10-10T00:00:00").request().accept(MediaType.APPLICATION_JSON).header("Content-type","application/json").get();
    	String response11 = servResponse11.readEntity(String.class);
    	System.out.println("Response"+format(response11));
    	JSONArray array11 = new JSONArray(response11);
        int countActivityInRange = array11.length();
        
        if(countActivityInRange>0) {
        	result = "OK";
        }else {
        	result = "ERROR";
        }
      
        System.out.println("\nRequest#11:" + "\n"
        		+ "GET /person/" + first_person_id + "/" +  type + "?before=2017-12-28T08:50:00&after=2017-10-10T00:00:00" + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON" + "\n"
        		+ "=> Result: " + result +  "\n" 
        		+ "=> HTTP Status: " + servResponse11.getStatus() + " " + servResponse11.getStatusInfo() + "\n"
        		+ "Body: "  + "\n"
        		+ format(response11) + "\n");
	}
    private static URI getBaseURI() {
        return UriBuilder.fromUri("https://activityperson.herokuapp.com").build();
    }
}