package io.valhala.tss.Inventory.backend;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

@Table(name="item")
@Entity 
public class InventoryItem implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 5592334329765505365L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long barcode;
	@NotNull
	private String name, type;
	@Nullable
	private String notes;
	@Nullable
	private String isAvailable, isLate;
	@Nullable
	@Temporal(TemporalType.TIMESTAMP)
	private Date checkOutDate, dueDate;
	
	public InventoryItem() {}

	public InventoryItem(Long barcode, String name, String type, String isAvailable) {
		this.barcode = barcode;
		this.name = name;
		this.type = type;
		this.isAvailable = isAvailable;
	}
	
	public InventoryItem(Long barcode, String name, String type, String notes, String isAvailable) {
		this.barcode = barcode;
		this.name = name;
		this.type = type;
		this.notes = notes;
		this.isAvailable = isAvailable;
	}
	
	public InventoryItem(Long barcode, String name, String type, String notes, String isAvailable, Date checkOutDate, Date dueDate, String isLate) {
		this.name = name;
		this.type = type;
		this.notes = notes;
		this.isAvailable = isAvailable;
		this.checkOutDate = checkOutDate;
		this.dueDate = dueDate;
		this.isLate = isLate;
		//this.currentPatron = currentPatron;
	}
	
	@Column(name="barcode")
	public Long getBarcode() {return barcode;}
	
	public void setBarcode(Long barcode) {this.barcode = barcode;}
	
	@Column(name="name")
	public String getName() {return name;}
	
	public void setName(String name) {this.name = name;}
	
	@Column(name="type")
	public String getType() {return type;}
	
	public void setType(String type) {this.type = type;}
	
	@Column(name="is_late")
	public String getisLate() {return isLate;}
	
	public void setLate(String isLate) {this.isLate = isLate;}
	
	@Column(name="availability")
	public String getisAvailable() {return isAvailable;}
	
	public void setAvailable(String isAvailable) {this.isAvailable = isAvailable;}
	
	@Column(name="notes") //bigtext?
	public String getNotes() {return notes;}
	
	public void setNotes(String notes) {this.notes = notes;}
	
	//here's where I am confused.. JSON?.. foreign key to another table
	//that contains all data relating to their Trinity ID?
	//public Patron getCurrentPatron() {return currentPatron;}
	
	//public void setCurrentPatron(Patron currentPatron) {this.currentPatron = currentPatron;}
	
	@Column(name="check_out_date", columnDefinition="DATETIME")
	public Date getCheckOutDate() {return checkOutDate;}
	
	public void setCheckOutDate(Date checkOutDate) {this.checkOutDate = checkOutDate;}
	
	@Column(name="due_date", columnDefinition="DATETIME")
	public Date getDueDate() {return dueDate;}
	
	public void setDueDate(Date dueDate) {this.dueDate = dueDate;}

}