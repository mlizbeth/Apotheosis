package io.valhala.tss.Inventory.backend;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
@Entity
@Table(name="patron")
public class Patron {
	@Id
	private Long id;
	@NotNull
	private String name, email;
	@NotNull
	private boolean owesFines;
	@Nullable
	private int finesOwed;

	
	public Patron() {}
	
	public Patron(Long id, String name, String email, boolean owesFines) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.owesFines = owesFines;
	}
	
	public Patron(Long id, String name, String email, InventoryItem[] checkedOutItems, List<InventoryItem> itemHistory, boolean owesFines, int finesOwed) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.owesFines = owesFines;
		this.finesOwed = finesOwed;
	}
	
	@Column(name="p_id")
	public long getId() {return id;}

	public void setId(Long id) {this.id = id;}
	
	@Column(name="p_name")
	public String getName() {return name;}

	public void setName(String name) {this.name = name;}
	
	@Column(name="p_email")
	public String getEmail() {return email;}

	public void setEmail(String email) {this.email = email;}
	
	@Column(name="p_owes_fines")
	public boolean isOwesFines() {return owesFines;}

	public void setOwesFines(boolean owesFines) {this.owesFines = owesFines;}
	
	@Column(name="p_fines_owed")
	public int getFinesOwed() {return finesOwed;}

	public void setFinesOwed(int finesOwed) {this.finesOwed = finesOwed;}
	
}