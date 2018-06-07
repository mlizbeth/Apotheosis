package io.valhala.tss.Inventory.backend;

import java.time.ZonedDateTime;

import org.vaadin.addon.calendar.item.BasicItem;

public class Shift extends BasicItem {
	
	enum State {
		scheduled, approved, isDropping
	}
	
	private ZonedDateTime start, end;
	private String name;
	private String details;
	
	public Shift() {
		
	}

	public ZonedDateTime getStart() {
		return start;
	}

	public void setStart(ZonedDateTime start) {
		this.start = start;
	}

	public ZonedDateTime getEnd() {
		return end;
	}

	public void setEnd(ZonedDateTime end) {
		this.end = end;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}


}
