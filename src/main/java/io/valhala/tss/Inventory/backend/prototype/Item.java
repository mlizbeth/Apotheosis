package io.valhala.tss.Inventory.backend.prototype;
import java.io.Serializable;
//@Table(name="items")
//@Entity 
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
public class Item implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@NotNull
	private long barcode;
	@NotNull
	private String name, type;
	@NotNull
	private boolean isLate, isAvailable; //can we have isLate only be an option if its not available?
	
	/* These fields are allowed to be null */
	private String notes;
	private Patron currentPatron;
	private Patron[] patronHistory;
	private Date checkOutDate;
	
	@NotNull
	private Date dueDate;
	
	public Item() {

	}
	
	@Column(name="item_id")
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	@Column(name="item_barcode")
	public long getBarcode() {return barcode;}
	public void setBarcode(long barcode) {this.barcode = barcode;}
	@Column(name="item_name")
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	@Column(name="item_type")
	public String getType() {return type;}
	public void setType(String type) {this.type = type;}
	@Column(name="is_late")
	public boolean isLate() {return isLate;}
	public void setLate(boolean isLate) {this.isLate = isLate;}
	@Column(name="is_available")
	public boolean isAvailable() {return isAvailable;}
	public void setAvailable(boolean isAvailable) {this.isAvailable = isAvailable;}
	@Column(name="notes") //bigtext?
	public String getNotes() {return notes;}
	public void setNotes(String notes) {this.notes = notes;}
	//here's where I am confused.. JSON?
	public Patron getCurrentPatron() {return currentPatron;}
	public void setCurrentPatron(Patron currentPatron) {this.currentPatron = currentPatron;}
	//confused here too
	public Patron[] getPatronHistory() {return patronHistory;}
	public void setPatronHistory(Patron[] patronHistory) {this.patronHistory = patronHistory;}
	@Column(name="check_out_date")
	public Date getCheckOutDate() {return checkOutDate;}
	public void setCheckOutDate(Date checkOutDate) {this.checkOutDate = checkOutDate;}
	@Column(name="due_date")
	public Date getDueDate() {return dueDate;}
	public void setDueDate(Date dueDate) {this.dueDate = dueDate;}
}