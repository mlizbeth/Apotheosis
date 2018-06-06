package io.valhala.tss.Inventory.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.ui.NumberField;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.UserError;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import io.valhala.tss.Inventory.backend.InventoryItem;
import io.valhala.tss.Inventory.backend.ItemRepository;

@SuppressWarnings("unchecked")
@SpringView(name = InventoryView.VIEW_NAME)
@SpringComponent
public class InventoryView extends HorizontalLayout implements View {

	@Autowired
	ItemRepository repo;
	//filter should request focus when a barcode is scanned.... or capture the scan code and then apply the search
	public static final String VIEW_NAME = "Inventory"; //InventoryView
	private TextField filter, barField, nameField, descField, typeField;
	private Button addItem, genBar, applyBtn, printBtn;
	private ComboBox filterMode;
	private HorizontalLayout toolBar, winSplit;
	private VerticalLayout winLeft, winRight, left;
	private Window barWindow;
	private Grid<InventoryItem> iList = new Grid<>(InventoryItem.class);
	private editPanel editForm = new editPanel();
	private Label preview;
	private String qrBuilder;
	private boolean searchByName = true;

	public InventoryView() {
		setResponsive(true);
		setSizeFull();
		Responsive.makeResponsive(this);
		initComponents();
	}

	private void initComponents() {
	
		filterMode = new ComboBox();
		filterMode.setItems("Filter by Barcode", "Filter by Name");
		filterMode.setValue("Filter by Name");
		filterMode.setEmptySelectionAllowed(false);
		
		filter = new TextField();
		filter.setWidth("100%");
		filter.setPlaceholder("TextField");
		filter.setMaxLength(20);

		barField = new TextField("Barcode");
		barField.setDescription("Barcode will be generated");
		barField.setIcon(VaadinIcons.QUESTION_CIRCLE_O);
		barField.setReadOnly(false);

		nameField = new TextField("Item Name");
		nameField.setRequiredIndicatorVisible(true);

		descField = new TextField("Item Description");
		descField.setRequiredIndicatorVisible(true);

		typeField = new TextField("Item Type");
		typeField.setRequiredIndicatorVisible(true);

		addItem = new Button("Add Item");
		genBar = new Button("New Barcode");
		applyBtn = new Button("Apply to new item");
		printBtn = new Button("Print");

		preview = new Label();

		toolBar = new HorizontalLayout(filterMode, filter, genBar, addItem);
		left = new VerticalLayout(toolBar, iList);
		left.setSizeFull();
		winSplit = new HorizontalLayout();
		winLeft = new VerticalLayout();
		winRight = new VerticalLayout();
		

		winRight.addComponent(preview);
		winLeft.addComponents(barField, nameField, descField, typeField, applyBtn);
		barWindow = new Window(null, winSplit);
		winSplit.addComponents(winLeft, winRight);
		
		addComponents(left, editForm);
	

		initPreferences();

	}

	private void initPreferences() {
		
		editForm.setVisible(false);
		
		toolBar.setWidth("100%");
		toolBar.setExpandRatio(filter, 1);
		
		left.setSizeFull();
		left.setExpandRatio(iList, 1);
		
		iList.setColumns("itemName", "itemType", "itemCode", "itemQuantity");
		iList.setSizeFull();
		iList.setSelectionMode(Grid.SelectionMode.SINGLE);
		
		addItem.addStyleName(ValoTheme.BUTTON_PRIMARY);
		
		winSplit.setSpacing(true);
		
		winLeft.setSpacing(true);
		
		barWindow.setResponsive(true);
		barWindow.setWidth("500px");
		barWindow.setHeight("500px");
		barWindow.setModal(true);
		barWindow.center();
		barWindow.setResizable(false);
		
		preview.setWidth("100px");
		preview.setHeight("100px");

		initListeners();
	}


	private void initListeners() {

		filterMode.addValueChangeListener(e -> {
			if(filterMode.getValue().equals("Filter by Barcode")) {
				searchByName = false;
			}
			else {
				searchByName = true;
			}
		});
		
		filter.addValueChangeListener(e -> {
			if(e.getValue().equals("")) {
				iList.setItems(repo.findAll());
			}
			else {
			refresh();
			}
		});
		
		addItem.addClickListener(e -> {
			editForm.setVisible(true);
			editForm.setItem(new InventoryItem());
		});
		

		barField.addValueChangeListener(e -> {
			makePreview();
		});

		nameField.addValueChangeListener(e -> {
			makePreview();
		});

		typeField.addValueChangeListener(e -> {
			makePreview();
		});

		descField.addValueChangeListener(e -> {
			makePreview();
		});

		applyBtn.addClickListener(e -> {
			
		});

		barWindow.addCloseListener(e -> {
			barField.clear();
			nameField.clear();
			descField.clear();
			typeField.clear();
		});

		genBar.addClickListener(e -> {
			if(!getUI().getWindows().contains(barWindow)) {
				getUI().addWindow(barWindow);
			}
		});

		iList.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() == null) {
				editForm.setVisible(false);
			}
			else {
				editForm.setItem(e.getValue());
			}
		});
	}
		
	private void refresh() 
	{

		if(!searchByName) {
			if(filter.getErrorMessage() != null && filter.getValue().matches("[^(A-Z)^(a-z)]+")) {
				filter.setComponentError(null);
			}
			if(filter.getValue().matches("([0-9])+")) { 
				//save the value to a variable.. but its need to be a long
			}
			else {
				filter.setComponentError(new UserError("Entry must be numeric. Alternatively you can swap filter modes."));
			}
			//Long.parseLong(Long.valueOf(temp)); ?
			//iList.setItems(repo.findAllByItemCodeContains(Long.valueOf(temp)));	
		}
		else {
			iList.setItems(repo.findAllByItemNameContainsIgnoreCase(filter.getValue()));
		}
	}

	private void makePreview() {
		winRight.removeAllComponents();
		qrBuilder = "";
		if(!barField.getValue().equals("")) {
			System.out.print(barField.getValue());
			qrBuilder += barField.getValue() + "\n";
		}
		if(!nameField.getValue().equals("")) {
			qrBuilder += nameField.getValue() + "\n"; //dont remember if it replaces or appends the value
		}
		if(!typeField.getValue().equals("")) {
			qrBuilder += typeField.getValue() + "\n";
		}
		if(!descField.getValue().equals("")) {
			qrBuilder += descField.getValue() + "\n";
		}
		StreamSource imgSource = new BarcodeStream();
		StreamResource res = new StreamResource(imgSource, "test.jpg");
		winRight.addComponent(new Image("Preview", res));

	}

	@PostConstruct
	void init() {
		iList.setItems(repo.findAll());
	}
	

	private class editPanel extends FormLayout {
		private InventoryItem item;
		private TextField itemName, itemType, itemCode, itemQuantity;
		private Button save, delete, cancel;
		private TextField itemId;

		private Binder<InventoryItem> binder = new Binder<>(InventoryItem.class); 
		
		public editPanel() {
			initEditConf();
			initEditLayout();
			setSizeUndefined();
			Responsive.makeResponsive(this);
			binder.bindInstanceFields(this);
			
		}

		private void initEditLayout() {
			HorizontalLayout options = new HorizontalLayout(save, delete, cancel);
			options.setSpacing(true);
			addComponents(itemId, itemName, itemType, itemCode, itemQuantity, options);
		}

		private void initEditConf() {
			save = new Button("Save");
			delete = new Button("Delete");
			cancel = new Button("Cancel");
			itemId = new TextField("Item ID");
			itemId.setEnabled(false);
			itemName = new TextField("Name");
			itemName.setMaxLength(64);
			itemType = new TextField("Type");
			itemType.setMaxLength(64);
			itemCode = new TextField("Barcode");
			itemCode.setMaxLength(20);
			itemQuantity = new TextField("Quantity");
			itemQuantity.setMaxLength(5);
			
			cancel.addClickListener(e -> this.cancel());
			save.addClickListener(e -> this.save());
			delete.addClickListener(e -> this.delete());
			
			/*
			 * Need to validate the rest of the fields, and test that this validates correctly.
			 * Check for overflows
			 * use a boolean to check if fields are strings, then dont allow saving
			 */

			//binder.forMemberField(itemName).withValidator((string -> string != null && !string.isEmpty()), "Values cannot be empty or contain only numbers");
			//binder.forMemberField(itemType).withValidator(string -> string != null && !string.isEmpty(), "Values cannot be empty or contain only numbers");
			binder.forMemberField(itemCode).withValidator(string -> string != null && !string.isEmpty(), "Values cannot be empty" ).withConverter(new StringToLongConverter("input should be an integer")).withValidator(Long -> Long > 0, "Input must be positive");
			binder.forMemberField(itemQuantity).withValidator(string -> string != null && !string.isEmpty(), "Values cannot be empty" ).withConverter(new StringToIntegerConverter("input should be an integer")).withValidator(integer -> integer >= 0, "Input must be positive");
			binder.forMemberField(itemId).withValidator(string -> string != null && !string.isEmpty(), "Values cannot be empty" ).withConverter(new StringToLongConverter("input should be an integer")).withValidator(Long -> Long > 0, "Input must be positive");

			
		}
		
		private void delete() {
			repo.delete(item); //need to automagically refresh the grid check for null
			Page.getCurrent().reload();
			//confirmation for deletion/administrator override for deletion.
			//refresh
			//set form to visible
		}

		private void save() {
			//if(itemName.getValue().matches("^[0-9]*$") || itemType.getValue().matches("^[0-9]*$")) 
			
			
			//if(!binder.isValid()) {
			//	Notification.show("Check your input. Item Name and Type must not be only numbers and Quantity/Code must not be only letters");
			//}
			//else {
				repo.save(item);
			//}	
			//repo.save(item); //need to automagically refresh the grid
			//Page.getCurrent().reload();
			//Validate input
		}

		private void cancel() {
			setItem(null);
			editForm.setVisible(false);
		}
		
		public void setItem(InventoryItem item) {
			//editPanel.setVisible(true);
			setVisible(true);
			this.item = item;
			binder.setBean(item);
		}
	}

	private class BarcodeStream implements StreamSource {
		private QRCodeWriter qrw;
		private BitMatrix bm;
		private ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
		
		
		public BarcodeStream() {
			qrw = new QRCodeWriter();
		}

		@Override
		public InputStream getStream() {
			try {
				bm = qrw.encode(qrBuilder, BarcodeFormat.QR_CODE, 100, 100);
				ImageIO.write(MatrixToImageWriter.toBufferedImage(bm), "jpg", imagebuffer);
				return new ByteArrayInputStream(imagebuffer.toByteArray());
			} catch (IOException | WriterException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

}


