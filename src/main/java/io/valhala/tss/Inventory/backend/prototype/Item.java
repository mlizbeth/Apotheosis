package io.valhala.tss.Inventory.backend.prototype;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

@Table(name="item")
@Entity 
public class Item implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long barcode;
	@NotNull
	private String name, type;
	@NotNull
	private boolean isAvailable;
	@Nullable
	private boolean isLate;
	@Nullable
	private String notes;
	private Patron currentPatron;
	@Nullable
	private Date checkOutDate, dueDate;
	
	public Item() {}
	
	public Item(Long barcode, String name, String type, boolean isAvailable) {
		this.barcode = barcode;
		this.name = name;
		this.type = type;
		this.isAvailable = isAvailable;
	}
	
	public Item(Long barcode, String name, String type, String notes, boolean isAvailable) {
		this.barcode = barcode;
		this.name = name;
		this.type = type;
		this.notes = notes;
		this.isAvailable = isAvailable;
	}
	
	public Item(Long barcode, String name, String type, String notes, boolean isAvailable, Date checkOutDate, Date dueDate, boolean isLate, Patron currentPatron) {
		this.barcode = barcode;
		this.name = name;
		this.type = type;
		this.notes = notes;
		this.isAvailable = isAvailable;
		this.checkOutDate = checkOutDate;
		this.dueDate = dueDate;
		this.isLate = isLate;
		this.currentPatron = currentPatron;
	}
	
	@Column(name="barcode")
	public long getBarcode() {return barcode;}
	
	public void setBarcode(long barcode) {this.barcode = barcode;}
	
	@Column(name="name")
	public String getName() {return name;}
	
	public void setName(String name) {this.name = name;}
	
	@Column(name="type")
	public String getType() {return type;}
	
	public void setType(String type) {this.type = type;}
	
	@Column(name="is_late")
	public boolean isLate() {return isLate;}
	
	public void setLate(boolean isLate) {this.isLate = isLate;}
	
	@Column(name="availability")
	public boolean isAvailable() {return isAvailable;}
	
	public void setAvailable(boolean isAvailable) {this.isAvailable = isAvailable;}
	
	@Column(name="note") //bigtext?
	public String getNotes() {return notes;}
	
	public void setNotes(String notes) {this.notes = notes;}
	
	//here's where I am confused.. JSON?.. foreign key to another table
	//that contains all data relating to their Trinity ID?
	public Patron getCurrentPatron() {return currentPatron;}
	
	public void setCurrentPatron(Patron currentPatron) {this.currentPatron = currentPatron;}
	
	@Column(name="check_out_date")
	public Date getCheckOutDate() {return checkOutDate;}
	
	public void setCheckOutDate(Date checkOutDate) {this.checkOutDate = checkOutDate;}
	
	@Column(name="due_date")
	public Date getDueDate() {return dueDate;}
	
	public void setDueDate(Date dueDate) {this.dueDate = dueDate;}

	@Override
	public String toString() {
		return "Item [barcode=" + barcode + ", " + (name != null ? "name=" + name + ", " : "")
				+ (type != null ? "type=" + type + ", " : "") + "isAvailable=" + isAvailable + ", isLate=" + isLate
				+ ", " + (notes != null ? "notes=" + notes + ", " : "")
				+ (currentPatron != null ? "currentPatron=" + currentPatron + ", " : "")
				+ (checkOutDate != null ? "checkOutDate=" + checkOutDate + ", " : "")
				+ (dueDate != null ? "dueDate=" + dueDate : "") + "]";
	}
}