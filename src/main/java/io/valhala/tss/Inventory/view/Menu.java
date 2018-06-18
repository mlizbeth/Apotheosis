package io.valhala.tss.Inventory.view;

import com.vaadin.client.ui.menubar.MenuBar;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;


public class Menu extends CssLayout
{

	private static final long serialVersionUID = 2245014338713756386L;
	private CssLayout menuItems, menuPart;
	private final HorizontalLayout menuHeader;
	private Label title;
	
	public Menu()
	{
		setPrimaryStyleName(ValoTheme.MENU_ROOT);
		menuPart = new CssLayout();
		menuPart.addStyleName(ValoTheme.MENU_PART);
		menuHeader = new HorizontalLayout();
		menuHeader.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		menuHeader.addStyleName(ValoTheme.MENU_TITLE);
		title = new Label("Trinity \"Apotheosis\" Dashboard");
		title.addStyleName(ValoTheme.LABEL_H3);
		title.setSizeUndefined();
		menuHeader.addComponent(title); //also need logo
		menuPart.addComponent(menuHeader);
		menuItems = new CssLayout();
		menuItems.setPrimaryStyleName("valo-menuitems");
		menuPart.addComponent(menuItems);
		
		addComponent(menuPart);
		
		createNavigationButton("Home", DashboardView.VIEW_NAME, VaadinIcons.HOME);
		createNavigationButton("Inventory", InventoryView.VIEW_NAME, VaadinIcons.PACKAGE);
		createNavigationButton("Schedule", ScheduleView.VIEW_NAME, VaadinIcons.CALENDAR_O);
		createNavigationButton("Resources", ResourceView.VIEW_NAME, VaadinIcons.NOTEBOOK);
		createNavigationButton("Pickup/Drop Shifts", "#", VaadinIcons.ANCHOR);
		
	}
	
	private void createNavigationButton(String caption, final String viewName, Resource icon)
	{
		Button b = new Button(caption);
		b.addClickListener(event -> getUI().getNavigator().navigateTo(viewName));
		b.setPrimaryStyleName(ValoTheme.MENU_ITEM);
		b.setIcon(icon);
		menuItems.addComponent(b);
	}

}
