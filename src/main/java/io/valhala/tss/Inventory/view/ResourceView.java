package io.valhala.tss.Inventory.view;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = ResourceView.VIEW_NAME)
@SpringComponent
public class ResourceView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "Resources";
	
	public ResourceView() {
		
	}
	
}

