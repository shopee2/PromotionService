package shopee2.model;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.google.api.client.util.DateTime;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
@Component
public class Promotion {
	private int id;
	private String desc;
	private String productNo;
	private String forShopID;
	private String creatorID;
	private Boolean active;
	private String code;
	private Date startDate;
	private Date dueDate;
	private Double minimumPrice;
	private Double discountInPrice;
	private Double discountInPercent;
	private Boolean isFreeDelivery;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Double getMinimumPrice() {
		return minimumPrice;
	}

	public void setMinimumPrice(Double double1) {
		this.minimumPrice = double1;
	}

	public Double getDiscountInPrice() {
		return discountInPrice;
	}

	public void setDiscountInPrice(Double discountInPrice) {
		this.discountInPrice = discountInPrice;
	}

	public Double getDiscountInPercent() {
		return discountInPercent;
	}

	public void setDiscountInPercent(Double discountInPercent) {
		this.discountInPercent = discountInPercent;
	}

	public Boolean getIsFreeDelivery() {
		return isFreeDelivery;
	}

	public void setIsFreeDelivery(Boolean isFreeDelivery) {
		this.isFreeDelivery = isFreeDelivery;
	}

	public String getForShopID() {
		return forShopID;
	}

	public void setForShopID(String forShopID) {
		this.forShopID = forShopID;
	}

	public String getCreatorID() {
		return creatorID;
	}

	public void setCreatorID(String creatorID) {
		this.creatorID = creatorID;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	

}
