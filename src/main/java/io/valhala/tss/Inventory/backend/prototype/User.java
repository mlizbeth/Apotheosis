package io.valhala.tss.Inventory.backend.prototype;
import io.valhala.tss.Inventory.backend.prototype.Shift;
import java.util.List;
public class User {
	private String role;
	private long trinityID;
	private String email;
	private Shift shift;
	private List<Shift> weeklyShifts; //or array of shifts?
	public User() {

	}
}