package io.valhala.tss.Inventory.view;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = TimesheetView.VIEW_NAME)
@SpringComponent
public class TimesheetView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "Timesheet";
	
	public TimesheetView() {
		
	}
}
