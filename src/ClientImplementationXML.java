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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;





public class ClientImplementationXML {
    

	public static WebTarget config() {
    	ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget service = client.target(getBaseURI());
        System.out.println("Calling server URL: " + getBaseURI());
        return service;
    }
	
	public static Document loadXMLFromString(String xml) throws Exception
	{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xml));
	    return builder.parse(is);
	}
	
	public static String format(String unformattedXml) throws Exception {
        try {
            final Document document = loadXMLFromString(unformattedXml);

            OutputFormat outputFormat = new OutputFormat(document);
            outputFormat.setLineWidth(65);
            outputFormat.setIndenting(true);
            outputFormat.setIndent(2);
            Writer writer = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(writer, outputFormat);
            serializer.serialize(document);

            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	
	public static void main(String[] args) throws Exception {
        WebTarget service = config();
        String result;
        int count;
        int first_person_id;
        int last_person_id;
		
        System.out.println("\nAPPLICATION/XML");
        // Step 3.1.
        System.out.println("\nRequest#3.1");
        
        Response servResponse = service.path("person").request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").get();
        String response = servResponse.readEntity(String.class);
        Document document1 = loadXMLFromString(response);
        NodeList personIds = document1.getElementsByTagName("personId");
        count = personIds.getLength();
        first_person_id = Integer.parseInt(personIds.item(0).getTextContent());
        last_person_id =  Integer.parseInt(personIds.item(count-1).getTextContent());
        System.out.println("Total number of persons in the database: " + count);
        System.out.println("First person's ID: " + first_person_id);
        System.out.println("Last person's ID: " + last_person_id);
        
        if(count>4) {
        	result = "OK";
        }else {
        	result = "ERROR";
        }
        System.out.println("\nRequest#1:" + "\n"
        		+ "GET /person/ Accept: APPLICATION/XML Content-Type: APPLICATION/XML" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse.getStatus() + " " + servResponse.getStatusInfo() + "\n"
        		+ "Body:"  + "\n"
        		+ format(response) + "\n");	
        
       
   
        // Step 3.2.
        System.out.println("\nRequest#3.2");
        
        Response servResponse2 = service.path("person").path(String.valueOf(first_person_id)).request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").get();
        String response2 = servResponse2.readEntity(String.class);
        Document document2 = loadXMLFromString(response2);
        NodeList initialFirstnames = document2.getElementsByTagName("firstName");
        String firstPersonInitialFirstname = initialFirstnames.item(0).getTextContent();
        if (servResponse2.getStatus()==200 || servResponse2.getStatus()==202) {
        	result = "OK";
        } else {
        	result ="ERROR";
        }
        System.out.println("\nRequest#2:" + "\n"
        		+ "GET /person/" + first_person_id + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse2.getStatus() + " " + servResponse2.getStatusInfo() + "\n"
        		+ "Body: "+"\n" + format(response2) + "\n");	

        // Step 3.3.
		Object entity3 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
				"<person>\n" + 
				"    <preferences>\n" + 
				"        <activitypreference>\n" + 
				"            <description>Squash Court in Sanbapolis</description>\n" + 
				"            <activityId>2</activityId>\n" + 
				"            <name>Squash</name>\n" + 
				"            <place>Trento</place>\n" + 
				"            <startdate>2017-11-11T00:00:00+01:00</startdate>\n" + 
				"            <type>SPORT</type>\n" + 
				"        </activitypreference>\n" + 
				"        <activitypreference>\n" + 
				"            <description>Jogging on Golden Gate Bridge</description>\n" + 
				"            <activityId>3</activityId>\n" + 
				"            <name>Jogging</name>\n" + 
				"            <place>San Francisco</place>\n" + 
				"            <startdate>2017-11-11T00:00:00+01:00</startdate>\n" + 
				"            <type>SPORT</type>\n" + 
				"        </activitypreference>\n" + 
				"    </preferences>\n" + 
				"    <birthDate>2017-11-11T00:00:00+01:00</birthDate>\n" + 
				"    <firstName>Parul</firstName>\n" + 
				"    <lastName>Sonkiya</lastName>\n" +
				"    <personId>1</personId>\n" + 			 
				"</person>";
		
		System.out.println("\nRequest#3.3");
        
        Response servResponse3a = service.path("person").path(String.valueOf(first_person_id)).request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").put(Entity.xml(entity3));
        Response servResponse3b = service.path("person").path(String.valueOf(first_person_id)).request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").get();
        String response3b = servResponse3b.readEntity(String.class);
        Document document3 = loadXMLFromString(response3b);
        NodeList firstnames = document3.getElementsByTagName("firstName");
        String firstPersonUpdatedFirstName = firstnames.item(0).getTextContent();
        
        if (!firstPersonInitialFirstname.equals(firstPersonUpdatedFirstName)) {
        	result = "OK";
        }else {
        	result = "ERROR";
        }
        
        System.out.println("\nRequest #3:" + "\n"
        		+ "PUT /person/" + first_person_id + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse3a.getStatus() + " " + servResponse3a.getStatusInfo() + "\n"
        		+ "Body: "+"\n" + format(response3b));	
        		
        // Step 3.4.
		Object entity4 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
				"<person>\n" + 
				"    <preferences>\n" + 
				"        <activitypreference>\n" + 
				"            <description>Cycle from Jaipur to Ajmer</description>\n" + 
				"            <name>Cycle</name>\n" + 
				"            <place>Rajasthan</place>\n" + 
				"            <startdate>2017-11-11T00:00:00+01:00</startdate>\n" + 
				"            <type>SPORT</type>\n" + 
				"        </activitypreference>\n" + 
				"    </preferences>\n" + 
				"    <birthDate>2017-10-11T00:00:00+01:00</birthDate>\n" + 
				"    <firstName>Priyanka</firstName>\n" + 
				"    <lastName>Chopra</lastName>\n" + 
				"</person>";
		
		System.out.println("\nRequest#3.4");
        
        Response servResponse4 = service.path("person").request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").post(Entity.xml(entity4));
        String response4 = servResponse4.readEntity(String.class);
        Document document4 = loadXMLFromString(response4);
        NodeList newPerson = document4.getElementsByTagName("personId");
        int newPersonId = Integer.parseInt(newPerson.item(0).getTextContent());
        
        if (newPersonId>-1 && (servResponse4.getStatus() == 200 || servResponse4.getStatus() == 201 || servResponse4.getStatus() == 202)) {
        	result = "OK";
        }else {
        	result = "ERROR";
        }
       
        System.out.println("\nRequest#4:" + "\n"
        		+ "POST /person/ Accept: APPLICATION/XML Content-Type: APPLICATION/XML" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse4.getStatus() + " " + servResponse4.getStatusInfo() + "\n"
        		+ "Body: " +"\n"+format(response4));	
        System.out.println("New person added with id: "+newPersonId);
       
        
        //3.5.
        System.out.println("\nRequest#3.5");
        
        Response servResponse5a = service.path("person").path(String.valueOf(newPersonId)).request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").delete();
        Response servResponse5b = service.path("person").path(String.valueOf(newPersonId)).request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").get();
      
        if (servResponse5b.getStatus()==404) {
        	result = "OK";
        } else {
        	result ="ERROR";
        }
        System.out.println("\nRequest#5:" + "\n"
        		+ "DELETE /person/" + newPersonId + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse5a.getStatus() + " " + servResponse5a.getStatusInfo());
        
        // Step 3.6.
        System.out.println("\nRequest#3.6");
        
        Response servResponse6 = service.path("activity_types").request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").get();
        String response6 = servResponse6.readEntity(String.class);
        Document document6 = loadXMLFromString(response6);
        NodeList activityTypes = document6.getElementsByTagName("activity_type");
        int countOfActivityType = activityTypes.getLength();
        List<String> listOfActivityTypes = new ArrayList<String>();
        for (int i=0;i<countOfActivityType;i++) {
        	listOfActivityTypes.add(activityTypes.item(i).getTextContent());
        }
        
        if(countOfActivityType>2) {
        	result = "OK";
        }else {
        	result = "ERROR";
        }
        System.out.println("\nRequest#6:" + "\n"
        		+ "GET /activity_type/ Accept: APPLICATION/XML Content-Type: APPLICATION/XML" + "\n"
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
        System.out.println("\nRequest#7a:" + " Activities for the first person: "+"\n");
        for (int i=0;i<listOfActivityTypes.size();i++) {        	
        	Response servResponse7a = service.path("person").path(String.valueOf(first_person_id)).path(listOfActivityTypes.get(i)).request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").get();
        	String response7 = servResponse7a.readEntity(String.class);
            Document document7a = loadXMLFromString(response7);
            NodeList listActivitiesWithType = document7a.getElementsByTagName("activity");
            activityCount = listActivitiesWithType.getLength();
            if(activityCount>0) {
            	result = "OK";
            	countTotal++;            	
            	activityId = Integer.parseInt(document7a.getElementsByTagName("activity").item(0).getChildNodes().item(5).getTextContent());
            	type = document7a.getElementsByTagName("activity").item(0).getChildNodes().item(4).getTextContent();
            }else {
            	result = "ERROR";
            }
            
            System.out.println("GET /person/" + first_person_id + "/" +  listOfActivityTypes.get(i) + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML" + "\n"
            		+ "=> Result: " + result +  "\n"
            		+ "=> HTTP Status: " + servResponse7a.getStatus() + " " + servResponse7a.getStatusInfo() + "\n"
            		+ "Body: "  + "\n"
            		+ format(response7) + "\n");
        }
        System.out.println("\nRequest#7b:" + " Activities for the last person: "+"\n");
        for (int i=0;i<listOfActivityTypes.size();i++) {
        	Response servResponse7b = service.path("person").path(String.valueOf(last_person_id)).path(listOfActivityTypes.get(i)).request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").get();
        	String response7b = servResponse7b.readEntity(String.class);
            Document document7b = loadXMLFromString(response7b);
            NodeList listActivitiesWithType = document7b.getElementsByTagName("activity");
            activityCount = listActivitiesWithType.getLength();
            if(activityCount>0) {
            	result = "OK";
            	countTotal++;
            }else {
            	result = "ERROR";
            }
            System.out.println( "GET /person/" + last_person_id + "/" +  listOfActivityTypes.get(i) + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML" + "\n"
            		+ "=> Result: " + result +  "\n"
            		+ "=> HTTP Status: " + servResponse7b.getStatus() + " " + servResponse7b.getStatusInfo() + "\n"
            		+ "Body: "  + "\n"
            		+ format(response7b) + "\n");
        }
        if (countTotal>0) {
        	System.out.println("Request#7 response:"+"\n");
        	System.out.println("Activity id saved: " + activityId + "\nActivity type saved: " + type);
        }else {
        	System.out.println("Request#7 response: ERROR");
        }
        
        //3.8.
        System.out.println("\nRequest#3.8");
        
        Response servResponse8 = service.path("person").path(String.valueOf(first_person_id)).path(type).path(String.valueOf(activityId)).request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").get();
        String response8 = servResponse8.readEntity(String.class);
        if (servResponse8.getStatus()==200) {
        	result = "OK";
        } else {
        	result ="ERROR";
        }
        System.out.println("\nRequest#8:" + "\n"
        		+ "GET /person/" + first_person_id + "/" + type + "/" + activityId + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse8.getStatus() + " " + servResponse8.getStatusInfo() + "\n"
        		+ "Body: "  + "\n"
        		+ format(response8) + "\n");
        
        int countActivityType = 0;
        int countActivityTypeChanged = 0;
        
        // Step 3.9.
        Object entity9 = 
        		"<activity>\n" + 
        		"<description>Swimming in the river</description>\n" + 
        		"<name>Swimming</name>\n" + 
        		"<place>Adige river</place>\n" + 
        		"<startdate>2017-12-28T08:50:00+01:00</startdate>\n" + 
        		"<type>SPORT</type>\n" + 
        		"</activity>";
		Response servResponse9a = service.path("person").path(String.valueOf(first_person_id)).path("SPORT").request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").get();
    	String response9a = servResponse9a.readEntity(String.class);
        Document document9a = loadXMLFromString(response9a);
        NodeList initialActivitiesType = document9a.getElementsByTagName("activity");
        countActivityType = initialActivitiesType.getLength();
		System.out.println("\nRequest#3.9");
        
        Response servResponse9 = service.path("person").path(String.valueOf(first_person_id)).path("SPORT").request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").post(Entity.xml(entity9));
        String response9 = servResponse9.readEntity(String.class);
        
        Response servResponse9b = service.path("person").path(String.valueOf(first_person_id)).path("SPORT").request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").get();
    	String response9b = servResponse9b.readEntity(String.class);
        Document document9b = loadXMLFromString(response9b);
        NodeList modifiedActivitiesType = document9b.getElementsByTagName("activity");
        countActivityTypeChanged = modifiedActivitiesType.getLength();       
        
        if (countActivityType < countActivityTypeChanged) {
        	result = "OK";
        }else {
        	result = "ERROR";
        }
        int newActivityId = Integer.parseInt(document9b.getElementsByTagName("activity").item(2).getChildNodes().item(5).getTextContent());
        System.out.println("New Activity Id: "+newActivityId);
        System.out.println("\nRequest#9:" + "\n"
        		+ "POST /person/" + first_person_id + "/SPORT" +" Accept: APPLICATION/XML Content-Type: APPLICATION/XML" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse9.getStatus() + " " + servResponse9.getStatusInfo() + "\n"
        		+ "Body: " + "\n" +format(response9));	
        
        // Step 3.10. 
        Object entity10 = 
        		"<activity>\n" + 
        				"<description>Swimming in the river</description>\n" + 
        				"<activityId>"+newActivityId + "</activityId>\n"+
        				"<name>Swimming</name>\n" + 
        				"<place>Adige river</place>\n" + 
        				"<startdate>2017-12-28T08:50:00+01:00</startdate>\n" + 
        				"<type>TRAVEL</type>\n" + 
        				"</activity>";
        Response servResponse10a = service.path("person").path(String.valueOf(first_person_id)).path("SPORT").path(String.valueOf(newActivityId)).request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").get();
        String response10a = servResponse10a.readEntity(String.class);
        Document document10a = loadXMLFromString(response10a);
        String initialType = document10a.getElementsByTagName("activity").item(0).getChildNodes().item(4).getTextContent();
       
        System.out.println("\nRequest#3.10");
        
        Response servResponse10 = service.path("person").path(String.valueOf(first_person_id)).path("TRAVEL").path(String.valueOf(newActivityId)).request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").put(Entity.xml(entity10));
        String response10 = servResponse10.readEntity(String.class);

        Response servResponse10b = service.path("person").path(String.valueOf(first_person_id)).path("TRAVEL").path(String.valueOf(newActivityId)).request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").get();
        String response10b = servResponse10b.readEntity(String.class);
        Document document10b = loadXMLFromString(response10b);
        String modifiedType = document10b.getElementsByTagName("activity").item(0).getChildNodes().item(4).getTextContent();       
        System.out.println("Initial activity type: "+ initialType);
        System.out.println("Modified activity type: "+ modifiedType);
        
        if (!modifiedType.equals(initialType)) {
        	result = "OK";
        }else {
        	result = "ERROR";
        }
        System.out.println("\nRequest#10:" + "\n"
        		+ "PUT /person/" + first_person_id + "/TRAVEL" + "/" + newActivityId +" Accept: APPLICATION/XML Content-Type: APPLICATION/XML" + "\n"
        		+ "=> Result: " + result +  "\n"
        		+ "=> HTTP Status: " + servResponse10.getStatus() + " " + servResponse10.getStatusInfo() + "\n"
        		+ "Body: " + "\n" +format(response10));	        


        // Step 3.11. 
        System.out.println("\nRequest#3.11");
        
    	Response servResponse11 = service.path("person").path(String.valueOf(first_person_id)).path(type).queryParam("before", "2017-12-28T08:50:00").queryParam("after", "2017-10-10T08:50:00").request().accept(MediaType.APPLICATION_XML).header("Content-type","application/xml").get();
    	String response11 = servResponse11.readEntity(String.class);
        Document document11 = loadXMLFromString(response11);
        NodeList activitiesInRange = document11.getElementsByTagName("activity");
        int countActivityInRange = activitiesInRange.getLength();
        if(countActivityInRange>0) {
        	result = "OK";
        }else {
        	result = "ERROR";
        }
        System.out.println("\nRequest#11:" + "\n"
        		+ "GET /person/" + first_person_id + "/" +  type + "?before=2017-12-28T08:50:00&after=2017-10-10T00:00:00" + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML" + "\n"
        		+ "=> Result: " + result +  "\n" 
        		+ "=> HTTP Status: " + servResponse11.getStatus() + " " + servResponse11.getStatusInfo() + "\n"
        		+ "Body: "  + "\n"
        		+ format(response11) + "\n");

	}
    private static URI getBaseURI() {
    	 return UriBuilder.fromUri("https://activityperson.herokuapp.com").build();
    }
}
