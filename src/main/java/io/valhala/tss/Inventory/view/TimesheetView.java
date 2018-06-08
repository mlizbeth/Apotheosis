package io.valhala.tss.Inventory.view;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;

/*
 * TODO
 * Student View: They can see how many hours they have worked, clock in/out
 * where should i put the drop shift requests
 * 
 * Admin View: They can view who is working, how many hours they have,
 * how many hours were worked this week vs last, how much money over budget for this week 
 * and last. labor cost for each day of current week, attendance notices
 * https://demo.vaadin.com/charts/
 */
@SpringView(name = TimesheetView.VIEW_NAME)
@SpringComponent
public class TimesheetView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "Timesheet";
	
	public TimesheetView() {
		
	}
}
