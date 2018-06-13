package io.valhala.tss.Inventory.backend.prototype;
import io.valhala.tss.Inventory.backend.prototype.Patron;
import java.util.List;
public class Item {
	private long barcode, id;
	private String name, type, notes;
	private Patron lastPatron, currentPatron;
	private Patron[] patronHistory;
	private Date checkOutDate, dueDate;
	private boolean isLate, isAvailable;
	public Item() {

	}
}