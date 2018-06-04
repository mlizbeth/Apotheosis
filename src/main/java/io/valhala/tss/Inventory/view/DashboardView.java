package io.valhala.tss.Inventory.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Responsive;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = DashboardView.VIEW_NAME)
@SpringComponent
public class DashboardView extends Panel implements View {
	public static final String VIEW_NAME = "";
	private HorizontalLayout root;
	private Label titleLabel;
	private CssLayout dashboardPanels;
	private VerticalLayout contentLayout;
	
	//login first
	//arrive at dashboard view
	//shows the modules you have access to as a user
	
	public DashboardView() {
		setSizeFull();
		root = new HorizontalLayout();
		contentLayout = new VerticalLayout();
		root.setSizeFull();
		setContent(root);
		Responsive.makeResponsive(this);
		root.addComponent(contentLayout);
		contentLayout.addComponent(buildHeader());
		Component content = buildWigets();
		contentLayout.addComponent(content);
		contentLayout.setExpandRatio(content, 1.0f);
	}

	private Component buildWigets() {
		dashboardPanels = new CssLayout();
		return dashboardPanels;
	}

	private Component buildHeader() {
		HorizontalLayout header = new HorizontalLayout();
		header.setSpacing(true);
		titleLabel = new Label("Trinity University - Operation: Defunct Sierra");
		HorizontalLayout tools = new HorizontalLayout(new Button(VaadinIcons.BELL_O), new Button(VaadinIcons.EDIT));
		tools.setSpacing(true);
		header.addComponent(tools);
		tools.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
		header.setExpandRatio(tools, 1.0f);
		return header;
		
	}

}
