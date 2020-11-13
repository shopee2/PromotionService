package shopee2.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
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

import shopee2.model.Promotion;
import shopee2.pormotionService.FirebaseInit;

@ComponentScan(basePackages="shopee2.pormotionService")
@RestController
public class Controller {
	
	@Autowired
	private FirebaseInit db;
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@RequestMapping("/service-instances/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(
			@PathVariable String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	}
	
	
	// store data to Promotion Object
	public List<Promotion> docToList(List<Promotion> promotionList, ApiFuture<QuerySnapshot> querySnapshot) throws InterruptedException, ExecutionException {
		for(DocumentSnapshot doc:querySnapshot.get().getDocuments()) {
			Promotion p = new Promotion();
			/*
			 * Parse TimeStamp to DateTime
			 * 
				new DateTime(doc.getTimestamp("startDate").getSeconds()*1000);
				System.out.println(new DateTime(new Long("1605891600000"), 420));
			*/
			p.setId(doc.getLong("id").intValue());
			p.setDesc(doc.getString("description"));
			p.setProductNo(doc.getString("productNo"));
			p.setForShopID(doc.getString("forShopID"));
			p.setCreatorID(doc.getString("creatorID"));
			p.setActive(doc.getBoolean("active"));
			p.setCode(doc.getString("code"));
			p.setStartDate(doc.getTimestamp("startDate").toDate());
			p.setDueDate(doc.getTimestamp("dueDate").toDate());
			p.setMinimumPrice(doc.getDouble("minimumPrice"));
			p.setDiscountInPrice(doc.getDouble("discountInPrice"));
			p.setDiscountInPercent(doc.getDouble("discountInPercent"));
			p.setIsFreeDelivery(doc.getBoolean("isFreeDelivery"));
				
			
			promotionList.add(p);
		}
		return promotionList;
	}
	
	
	
	
	@RequestMapping(value = "/promotions", method=RequestMethod.GET)
	public List<Promotion> getPromotion() throws InterruptedException, ExecutionException {
		List<Promotion> promotionList = new ArrayList<Promotion>();
		CollectionReference Promotion = db.getFireBase().collection("promotion");
		System.out.println("recieve data");
		ApiFuture<QuerySnapshot> querySnapshot= Promotion.get();
		
		promotionList = docToList(promotionList, querySnapshot);
		
		return promotionList;

	}
	
	@RequestMapping(value = "/promotions/freeDelivery", method=RequestMethod.GET)
	public List<Promotion> getPromotionFreeDelivery() throws InterruptedException, ExecutionException {
		List<Promotion> promotionList = new ArrayList<Promotion>();
		CollectionReference Promotion = db.getFireBase().collection("promotion");
		Query promotionSearch = Promotion.whereEqualTo("isFreeDelivery", true);
		System.out.println("recieve data");
		ApiFuture<QuerySnapshot> querySnapshot= promotionSearch.get();
		
		promotionList = docToList(promotionList, querySnapshot);
		
		return promotionList;

	}
	
	@RequestMapping(value = "/promotions/{code}", method=RequestMethod.GET)
	public String getPromotionCodeMatch(@PathVariable String code) throws InterruptedException, ExecutionException, JsonProcessingException {
		List<Promotion> promotionList = new ArrayList<Promotion>();
		CollectionReference Promotion = db.getFireBase().collection("promotion");
		Query promotionSearch = Promotion.whereEqualTo("code", code);
		System.out.println("recieve data");
		ApiFuture<QuerySnapshot> querySnapshot= promotionSearch.get();
		
		promotionList = docToList(promotionList, querySnapshot);
		Date curentDAte = new Date();
		//System.out.print(((Promotion)promotionList.get(0)).getStartDate().after(curentDAte));
		
		if(promotionList.size() == 0) {
			return "{"
					+"\"error\":\"Invaild code\""
					+ "}";
		}
		
		else if (!((Promotion)promotionList.get(0)).getStartDate().after(curentDAte) && !((Promotion)promotionList.get(0)).getDueDate().before(curentDAte)) {
			try {
				return new ObjectMapper().writeValueAsString(promotionList.get(0));
				//new ObjectMapper().writeValueAsString(Promotion)
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			return "{"
					+"\"error\":\"is code has expired\""
					+ "}";
		}
		
		
		return "";

	}

	
	@RequestMapping(value = "/promotions", method=RequestMethod.POST)
	public String addPromotion(@RequestBody Promotion promotion) throws InterruptedException, ExecutionException {
		
		System.out.println("start");
		//get all to check if duplicate code
		List<Promotion> promotionList = new ArrayList<Promotion>();
		CollectionReference PromotionGet= db.getFireBase().collection("promotion");
		Query promotionSearch = PromotionGet.whereEqualTo("code", promotion.getCode());
		System.out.println("recieve data");
		ApiFuture<QuerySnapshot> querySnapshot= promotionSearch.get();
		
		ApiFuture<QuerySnapshot> allPromotion= PromotionGet.orderBy("id", Query.Direction.DESCENDING).get();
		
		promotionList = docToList(promotionList, querySnapshot);
		
		Promotion p =  new Promotion();
		p.setId(allPromotion.get().getDocuments().get(0).getLong("id").intValue()+1);;
		p.setDesc(promotion.getDesc());
		p.setProductNo(promotion.getProductNo());
		p.setForShopID(promotion.getForShopID());
		p.setCreatorID(promotion.getCreatorID());
		p.setActive(promotion.getActive());
		p.setCode(promotion.getCode());
		p.setStartDate(promotion.getStartDate());
		p.setDueDate(promotion.getDueDate());
		p.setMinimumPrice(promotion.getMinimumPrice());
		p.setDiscountInPrice(promotion.getDiscountInPrice());
		p.setDiscountInPercent(promotion.getDiscountInPercent());
		p.setIsFreeDelivery(promotion.getIsFreeDelivery());
		
		
		//add
		if (promotionList.size() == 0) {
			PromotionGet.document(String.valueOf(p.getId())).set(p);
			return "success\n"+p.getId();
		}
		else {
			return "ERROR : This code has been used";
		}
	}
	@RequestMapping(value = "/promotions", method=RequestMethod.PATCH)
	public String updatePromotion(@RequestBody Promotion promotion, @RequestParam int id) throws InterruptedException, ExecutionException {		
		List<Promotion> promotionList = new ArrayList<Promotion>();
		CollectionReference PromotionGet= db.getFireBase().collection("promotion");
		Query promotionSearch = PromotionGet.whereEqualTo("id", id);
		List<Promotion> listCodeDupe = new ArrayList<Promotion>();
		
		System.out.println("recieve data");
		ApiFuture<QuerySnapshot> querySnapshot= promotionSearch.get();
		promotionList = docToList(promotionList, querySnapshot);
		
		Promotion p =  promotionList.get(0);
		p.setDesc(promotion.getDesc()!=null?promotion.getDesc():p.getDesc());
		p.setProductNo(promotion.getProductNo()!=null?promotion.getProductNo():p.getProductNo());
		p.setForShopID(promotion.getForShopID()!=null?promotion.getForShopID():p.getForShopID());
		p.setCreatorID(promotion.getCreatorID()!=null?promotion.getCreatorID():p.getCreatorID());
		p.setActive(promotion.getActive()!=null?promotion.getActive():p.getActive());
		p.setCode(promotion.getCode()!=null?promotion.getCode():p.getCode());
		p.setStartDate(promotion.getStartDate()!=null?promotion.getStartDate():p.getStartDate());
		p.setDueDate(promotion.getDueDate()!=null?promotion.getDueDate():p.getDueDate());
		p.setMinimumPrice(promotion.getMinimumPrice()!=null?promotion.getMinimumPrice():p.getMinimumPrice());
		p.setDiscountInPrice(promotion.getDiscountInPrice()!=null?promotion.getDiscountInPrice():p.getDiscountInPrice());
		p.setDiscountInPercent(promotion.getDiscountInPercent()!=null?promotion.getDiscountInPercent():p.getDiscountInPercent());
		p.setIsFreeDelivery(promotion.getIsFreeDelivery()!=null?promotion.getIsFreeDelivery():p.getIsFreeDelivery());
		/*
		PromotionGet.document(String.valueOf(id)).set(p);
		return "success\n";
		*/
		
		//add
		Query codeDupe = PromotionGet.whereGreaterThan("id", id).whereEqualTo("code", promotion.getCode());
		Query codeDupe2 = PromotionGet.whereLessThan("id", id).whereEqualTo("code", promotion.getCode());
		ApiFuture<QuerySnapshot> codeDupeGet = codeDupe.get();
		ApiFuture<QuerySnapshot> codeDupe2Get = codeDupe2.get();
		if (codeDupeGet.get().getDocuments().size() == 0 && codeDupe2Get.get().getDocuments().size() == 0) {
			PromotionGet.document(String.valueOf(id)).set(p);
			return "success\n";
		}
		else {
			return "ERROR : This code has been used";
		}
		
	}
	
	@RequestMapping(value = "/promotions", method = RequestMethod.DELETE)
	public String deletePromotion(@RequestParam String id) throws InterruptedException, ExecutionException {
		
		CollectionReference PromotionGet= db.getFireBase().collection("promotion");
		System.out.println("recieve data");
		ApiFuture<WriteResult> querySnapshot = PromotionGet.document(id).delete();
		
		return "success" + querySnapshot.get().getUpdateTime();
	}
}
