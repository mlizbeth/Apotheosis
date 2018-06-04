package io.valhala.tss.Inventory.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;


public class Menu extends CssLayout
{
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
		//menuItems.setPrimaryStyleName(VALO_MENUITEMS);
		menuPart.addComponent(menuItems);
		
		/*MenuBar logoutMenu = new MenuBar();
		logoutMenu.addItem("Logout", VaadinIcons.SIGN_OUT, new Command() {

			@Override
			public void menuSelected(MenuItem selectedItem) 
			{
				// TODO Auto-generated method stub
				
			}
			
		});
		
		logoutMenu.addStyleName("user-menu");
		menuPart.addComponent(logoutMenu);
		
        final Button showMenu = new Button("Menu", new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                if (menuPart.getStyleName().contains(VALO_MENU_VISIBLE)) {
                    menuPart.removeStyleName(VALO_MENU_VISIBLE);
                } else {
                    menuPart.addStyleName(VALO_MENU_VISIBLE);
                }
            }
        });
        

        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName(VALO_MENU_TOGGLE);
        showMenu.setIcon(VaadinIcons.MENU);
        menuPart.addComponent(showMenu);*/
		
		addComponent(menuPart);
		
		createNavigationButton("Home", DashboardView.VIEW_NAME, VaadinIcons.HOME);
		createNavigationButton("Inventory", InventoryView.VIEW_NAME, VaadinIcons.PACKAGE);
		createNavigationButton("Schedule", "Schedule", VaadinIcons.CALENDAR_O);
		createNavigationButton("Timeclock", "Time Clock", VaadinIcons.CALENDAR_CLOCK);
		createNavigationButton("Task List", "Task List", VaadinIcons.NOTEBOOK);
		
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
