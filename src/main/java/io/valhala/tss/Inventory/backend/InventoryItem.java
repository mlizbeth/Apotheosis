package io.valhala.tss.Inventory.backend;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Table(name="items")
@Entity
public class InventoryItem implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long itemId;
	@NotNull
	private String itemName, itemType;
	@NotNull
	private long itemCode;
	@NotNull
	private int itemQuantity; //needed?
	//@NotNull
	//private boolean isAvailable;
	//@NotNull
	//private String note;
	
	public InventoryItem() {}
	
	public InventoryItem(String itemName, String itemType, long itemCode, int itemQuantity)
	{
		this.itemName = itemName;
		this.itemType = itemType;
		this.itemQuantity = itemQuantity;
		this.itemCode = itemCode;
	}

	@Column(name="item_id")
	public long getItemId() {return itemId;}
	public void setItemId(long itemId) {this.itemId = itemId;}
	@Column(name="item_name")
	public String getItemName() {return itemName;}
	public void setItemName(String itemName) {this.itemName = itemName;}
	@Column(name="item_type")
	public String getItemType() {return itemType;}
	public void setItemType(String itemType) {this.itemType = itemType;}
	@Column(name="item_code")
	public long getItemCode() {return itemCode;}
	public void setItemCode(long itemCode) {this.itemCode = itemCode;}
	@Column(name="item_quantity")
	public int getItemQuantity() {return itemQuantity;}
	public void setItemQuantity(int itemQuantity) {this.itemQuantity = itemQuantity;}
	

}
