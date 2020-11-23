package shopee2.controller;

import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.api.client.util.DateTime;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firestore.admin.v1.Index.QueryScope;
import com.google.gson.JsonObject;
import com.sun.research.ws.wadl.Response;

import shopee2.model.Promotion;
import shopee2.pormotionService.FirebaseInit;
import shopee2.pormotionService.PromotionService;

@CrossOrigin
@ComponentScan(basePackages="shopee2.pormotionService")
@RestController
public class Controller {
	
	@Autowired
	private FirebaseInit db;
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
	private PromotionService promotionService;
	
	@RequestMapping("/service-instances/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(
			@PathVariable String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	}
	
	
	
	
	@RequestMapping(value = "/promotions", method=RequestMethod.GET, produces = "application/json")
	public Map<String, Object> getPromotion() throws JsonProcessingException, InterruptedException, ExecutionException{
		Map<String, Object> status = promotionService.getPromotion();
		return status;
		

	}
	
	
	@RequestMapping(value = "/promotions/freeDelivery", method=RequestMethod.GET, produces = "application/json")
	public Map<String, Object> getPromotionFreeDelivery() throws InterruptedException, ExecutionException {
		Map<String, Object> status = promotionService.getPromotionFreeDelivery();
		return status;

	}
	
	@RequestMapping(value = "/promotions/id/{id}", method=RequestMethod.GET, produces = "application/json")
	public Map<String, Object> getPromotionFreeDelivery (@PathVariable int id ) throws InterruptedException, ExecutionException {
		Map<String, Object> status = promotionService.getPromotion(id);

		return status;
		


	}
	
	@RequestMapping(value = "/promotions/{code}", method=RequestMethod.GET, produces = "application/json")
	public Map<String, Object> getPromotionCodeMatch(@PathVariable String code) throws InterruptedException, ExecutionException, JsonProcessingException {
		Map<String, Object> status = promotionService.getPromotionCodeMatch(code);
		return status;

	}

	
	@RequestMapping(value = "/promotions", method=RequestMethod.POST, produces = "application/json")
	public Map<String, Object> addPromotion(@RequestBody Promotion promotion) throws InterruptedException, ExecutionException, JsonProcessingException {
		
		Map<String, Object> status = promotionService.addPromotion(promotion);
		return status;
	}
	@RequestMapping(value = "/promotions/edit/{id}", method=RequestMethod.PATCH, produces = "application/json")
	public Map<String, Object> updatePromotion(@RequestBody Promotion promotion, @PathVariable int id) throws InterruptedException, ExecutionException, JsonProcessingException {		
		Map<String, Object> status = promotionService.updatePromotion(promotion, id);
		return status;
		
		
	}
	
	@RequestMapping(value = "/promotions/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public Map<String, Object> deletePromotion(@PathVariable String id) throws InterruptedException, ExecutionException, JsonProcessingException {
		Map<String, Object> status = promotionService.deletePromotion(id);
		return status;
	}
}
