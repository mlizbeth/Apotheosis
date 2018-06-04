/*
 * @Author: Madeline Kotara
 */
package io.valhala.tss.Inventory.view;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;


@SpringUI
@SpringViewDisplay
@Title("Inventory Manager")
@Theme("mytheme")
public class VDisplay extends UI implements ViewDisplay {

	private Panel viewContainer = new Panel();
	private Menu menu = new Menu();
	private HorizontalLayout root = new HorizontalLayout();

	public static void main(String[] args) {}

	@Override
	public void showView(View view) {
		viewContainer.setContent((Component) view);
	}

	@Override
	protected void init(VaadinRequest request) {
		Responsive.makeResponsive(this);
		setContent(root);
		root.setSizeFull();
		root.addComponent(menu);
		root.setSpacing(false);
		root.addComponent(viewContainer);
		root.setExpandRatio(viewContainer, 1.0f);
		viewContainer.setSizeFull();
		showView(new DashboardView());
		
	}
}
