package com.microservices.servicediscovery;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netflix.ribbon.proxy.annotation.Var;

@SpringBootApplication
@EnableEurekaServer
@RestController
@Controller
public class App {
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }
    @RequestMapping(value="/instances", method = RequestMethod.GET)
    @ResponseBody
    private String hello() {
    	 
    	final String uri = "http://localhost:8761/eureka/apps";
    	ArrayList<InstanceDetail> instanceDetailList = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        JsonObject resultJson = (JsonObject) JsonParser.parseString(result);
        HashMap<String, String> instanceHash= new HashMap<>();
        instanceHash.put("SERVICE1", null);
        instanceHash.put("SERVICE2", null);
        instanceHash.put("SERVICE3", null);
        instanceHash.put("SERVICE4", null);
        instanceHash.put("SERVICE5", null);

        
        JsonArray instances =  ((JsonObject) resultJson.get("applications")).getAsJsonArray("application");
		for (int i=0; i<instances.size(); i++) {
			JsonObject instance = (JsonObject) instances.get(i);
			//System.out.print(instance);
			JsonObject instanceDetail = (JsonObject) instance.getAsJsonArray("instance").get(0);
			//System.out.print(instanceDetail);
			//System.out.println("app : "+instanceDetail.get("app").toString());
			//System.out.println("status : "+instanceDetail.get("status").toString());
			//instanceDetailList.add(new InstanceDetail(instanceDetail.get("app").toString(), instanceDetail.get("status").toString()));
			instanceHash.put(instanceDetail.get("app").toString().replace("\"", ""), instanceDetail.get("status").toString());
			
			
		}
		//System.out.println(instanceHash.get("SERVICE1"));
		System.out.println(instanceHash.toString());
		String html = "<html>\n" + 
				"<header>"
				+	"    <meta charset=\"UTF-8\">\n" + 
				"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" + 
				"    <title>Service Instance</title>\n" + 
				"    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css\">\n" + 
				"    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>\n" + 
				"    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js\"></script>"
				+"</header>\n"
				+ "<body>\n" 
				+ "<div class=\"container\">\n" + 
				"        <div class=\"row\">\n" + 
				"            <div class=\"col-sm-5\" style=\"height: 200px;background-color: "+(instanceHash.get("SERVICE1") != null ?"green":"red")+";padding: 20px;border: 3px solid black;margin: 20px;\">\n" + 
				"            <h1 class='text-center'>Service 1</h1>\n" + 
				"            <h3 class='text-center'>status : "+(instanceHash.get("SERVICE1") != null ?"UP":"DOWN")+"</h3>\n" + 
				"            </div>\n" + 
				"            <div class=\"col-sm-5\" style=\"height: 200px;background-color: "+(instanceHash.get("SERVICE2") != null ?"green":"red")+";padding: 20px;border: 3px solid black;margin: 20px;\">\n" + 
				"            <h1 class='text-center'>Service 2</h1>\n" + 
				"            <h3 class='text-center'>status : "+(instanceHash.get("SERVICE2") != null ?"UP":"DOWN")+"</h3>\n" + 
				"            </div>\n" + 
				"            <div class=\"col-sm-5\" style=\"height: 200px;background-color: "+(instanceHash.get("SERVICE3") != null ?"green":"red")+";padding: 20px;border: 3px solid black;margin: 20px;\">\n" + 
				"            <h1 class='text-center'>Service 3</h1>\n" + 
				"            <h3 class='text-center'>status : "+(instanceHash.get("SERVICE3") != null ?"UP":"DOWN")+"</h3>\n" + 
				"            </div>\n" + 
				"            <div class=\"col-sm-5\" style=\"height: 200px;background-color: "+(instanceHash.get("SERVICE4") != null ?"green":"red")+";padding: 20px;border: 3px solid black;margin: 20px;\">\n" + 
				"            <h1 class='text-center'>Service 4</h1>\n" + 
				"            <h3 class='text-center'>status : "+(instanceHash.get("SERVICE4") != null ?"UP":"DOWN")+"</h3>\n" + 
				"            </div>\n" + 
				"            <div class=\"col-sm-5\" style=\"height: 200px;background-color: "+(instanceHash.get("SERVICE5") != null ?"green":"red")+";padding: 20px;border: 3px solid black;margin: 20px;\">\n" + 
				"            <h1 class='text-center'>Service 5</h1>\n" + 
				"            <h3 class='text-center'>status : "+(instanceHash.get("SERVICE5") != null ?"UP":"DOWN")+"</h3>\n" + 
				"            </div>\n" + 
				"            \n" + 
				"        </div>\n" + 
				"    </div>"
				+ "</body>\n" 
			+ "</html>";
		
		return html;
		
        
    }
}