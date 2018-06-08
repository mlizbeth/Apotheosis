package io.valhala.tss.Inventory.backend;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.vaadin.v7.ui.components.calendar.event.BasicEvent;
@Table(name="shift")
@Entity
public class Shift extends BasicEvent implements Serializable, Cloneable {
	
	/*enum State {
		scheduled, approved, isDropping
	}*/
	@NotNull
	private Date start, end;
	@NotNull
	private String name, position;
	
	public Shift(Date start, Date end, String name, String position) {
		this.start = start;
		this.end = end;
		this.name = name;
		this.position = position;
	}
	
	public Shift() {}
	
	@Column(name="shift_start")
	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}
	
	@Column(name="shift_end")
	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	@Column(name="employee")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="area")
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}


}
