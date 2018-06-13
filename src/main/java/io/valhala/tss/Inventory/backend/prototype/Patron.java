package io.valhala.tss.Inventory.backend.prototype;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
@Entity
@Table(name="patrons")
public class Patron {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@NotNull
	private long trinityID;
	@NotNull
	private String name, email;
	
	private Item[] checkedOutItems;
	private List<Item> itemHistory;
	private boolean owesFines;
	private int finesOwed;
	
	public Patron() {

	}
	//can ID redundancy be reduced? Their ID comes from a magnetic strip
	public long getId() {return id;}

	public void setId(long id) {this.id = id;}

	public long getTrinityID() {return trinityID;}

	public void setTrinityID(long trinityID) {this.trinityID = trinityID;}

	public String getName() {return name;}

	public void setName(String name) {this.name = name;}

	public String getEmail() {return email;}

	public void setEmail(String email) {this.email = email;}

	public Item[] getCheckedOutItems() {return checkedOutItems;}

	public void setCheckedOutItems(Item[] checkedOutItems) {this.checkedOutItems = checkedOutItems;}

	public List<Item> getItemHistory() {return itemHistory;}

	public void setItemHistory(List<Item> itemHistory) {this.itemHistory = itemHistory;}

	public boolean isOwesFines() {return owesFines;}

	public void setOwesFines(boolean owesFines) {this.owesFines = owesFines;}

	public int getFinesOwed() {return finesOwed;}

	public void setFinesOwed(int finesOwed) {this.finesOwed = finesOwed;}
}