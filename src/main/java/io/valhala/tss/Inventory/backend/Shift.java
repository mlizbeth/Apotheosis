package io.valhala.tss.Inventory.backend;

import java.util.Date;
import com.vaadin.v7.ui.components.calendar.event.BasicEvent;

public class Shift extends BasicEvent {
	
	enum State {
		scheduled, approved, isDropping
	}
	
	private Date start, end;
	private String name, position;
	
	public Shift(Date start, Date end, String name, String position) {
		this.start = start;
		this.end = end;
		this.name = name;
		this.position = position;
	}
	
	public Shift() {}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}


}
