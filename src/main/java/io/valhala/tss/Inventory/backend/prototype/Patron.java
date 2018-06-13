package io.valhala.tss.Inventory.backend.prototype;
import io.valhala.tss.Inventory.backend.prototype.Item;
import java.util.List;
public class Patron {
	private String name, email;
	private Long trinityID;
	private Item[] checkedOutItems;
	private List<Item> itemHistory;
	private boolean owesFines;
	private int finesOwed;
	public Patron() {

	}
}