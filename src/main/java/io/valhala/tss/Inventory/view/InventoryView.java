package io.valhala.tss.Inventory.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.UserError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
//import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.themes.ValoTheme;
import io.valhala.tss.Inventory.backend.InventoryItem;
import io.valhala.tss.Inventory.backend.ItemRepository;



//This class is nearly finished
//Still need all field validation, some changes to the entity class
//new entity classes: user (isAdmin?), patron
//fix search logic
//scanning/checkout/checkin features
//checkOutTo field if not available!
@SuppressWarnings("unchecked")
@SpringView(name = InventoryView.VIEW_NAME)
@SpringComponent
public class InventoryView extends HorizontalLayout implements View {
	@Autowired
	ItemRepository repo;
	//filter should request focus when a barcode is scanned.... or capture the scan code and then apply the search
	public static final String VIEW_NAME = "Inventory";
	private TextField filter;
	private Button addItem;
	private ComboBox<String> filterMode, aOps;
	private HorizontalLayout toolBar;
	private VerticalLayout left;
	private Grid<InventoryItem> iList = new Grid<>(InventoryItem.class);
	private editPanel editForm = new editPanel();
	private String qrBuilder;
	private boolean searchByName = true;

	public InventoryView() {
		setResponsive(true);
		setSizeFull();
		Responsive.makeResponsive(this);
		initComponents();
	}

	private void initComponents() {
	
		filterMode = new ComboBox<String>();
		filterMode.setItems("Filter by Barcode", "Filter by Name", "Filter by Patron");
		filterMode.setValue("Filter by Name");
		filterMode.setEmptySelectionAllowed(false);
		
		filter = new TextField();
		filter.setWidth("100%");
		filter.setPlaceholder("TextField");
		filter.setMaxLength(20);

		aOps = new ComboBox<>();
		aOps.setItems("Catalog", "Check In", "Check Out", "Renew");
		aOps.setValue("Catalog");
		aOps.setEmptySelectionAllowed(false);
			
		addItem = new Button("Add item");
		toolBar = new HorizontalLayout(filterMode, filter, aOps, addItem);
		left = new VerticalLayout(toolBar, iList);
		
		addComponents(left, editForm);

		initPreferences();

	}

	private void initPreferences() {
		
		editForm.setVisible(false);
		
		toolBar.setWidth("100%");
		toolBar.setExpandRatio(filter, 1);
		
		left.setSizeFull();
		left.setExpandRatio(iList, 1);
		
		iList.setColumns("name", "type", "barcode");
		iList.setSizeFull();
		iList.setSelectionMode(Grid.SelectionMode.SINGLE);

		
		addItem.addStyleName(ValoTheme.BUTTON_PRIMARY);

		initListeners();
	}


	private void initListeners() {

		aOps.addValueChangeListener(e -> {
			//System.out.println("Test");
		});
		
		filterMode.addValueChangeListener(e -> {
			if(filterMode.getValue().equals("Filter by Barcode")) {
				searchByName = false;
				//System.out.println("searchByName: " + searchByName);
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

		iList.asSingleSelect().addValueChangeListener(e -> {
			if(aOps.getValue().equals("Catalog")) {
				if(e.getValue() == null) {
					editForm.setVisible(false);
				}
				else {
					editForm.setItem(e.getValue());
				}
			}
			
			if(aOps.getValue().equals("Check In")) {
				
			}
			
			if(aOps.getValue().equals("Check Out")) {
				
			}
			
			if(aOps.getValue().equals("Renew")) {
				
			}
		});
	}
	//([0-9])+
	private void refresh() {

		if(!searchByName) {
			if(filter.getValue().matches("[0-9]+")) {
				if(filter.getComponentError() != null) {
					filter.setComponentError(null);
				}
				//iList.setItems(repo.findAllByBarcodeContains(Long.valueOf(filter.getValue()))); // value [%VALUE%] does not match [java.lang.Long (n/a)]
				iList.setItems(repo.findByBarcode(Long.valueOf(filter.getValue())));
				
			}
			else {
				filter.setComponentError(new UserError("Entry must be numeric. Alternatively you can swap filter modes."));
			}
		}
		else {
			iList.setItems(repo.findAllByNameContainsIgnoreCase(filter.getValue()));
		}
	}

	@PostConstruct
	void init() {
		iList.setItems(repo.findAll());
	}
	
	private class renewPopup extends Window {
		//Scan item
		//show item details
		//Scan MSR
		//show patron details?
		//Show when checked out and when due
		//Apply new due date
		//update DB
	}
	
	private class checkOutPopup extends Window {
		
	}
	
	private class checkInPopup extends Window {
		
	}
	

	private class editPanel extends FormLayout {
		private InventoryItem item;
		private TextField itemName, itemType, itemBarcode;
		private DateField checkOutDate, dueDate;
		private Button save, delete, cancel;
		private ComboBox<String> isAvailable, isLate;
		private TextArea notes;
		private VerticalLayout genLayout;

		private Binder<InventoryItem> binder = new Binder<>(InventoryItem.class); 
		
		
		private void addListeners() {
			
			isAvailable.addValueChangeListener(e -> {
				System.out.println("Test"); //still not editable with a listener
				//and still not editable by explicitly calling setEnabled(true), setReadOnly(false);
			});
			
			notes.addValueChangeListener(e -> {
				makePreview();
			});
			
			itemBarcode.addValueChangeListener(e -> {
				makePreview();
			});
			
			itemType.addValueChangeListener(e -> {
				makePreview();
			});
			
			itemName.addValueChangeListener(e -> {
				makePreview();
			});
		}
		
		private void makePreview() {
			genLayout.removeAllComponents();
			qrBuilder = "";
			if(!itemBarcode.getValue().equals("")) {
				qrBuilder += itemBarcode.getValue() + "\n";
			}
			if(!itemName.getValue().equals("")) {
				qrBuilder += itemName.getValue() + "\n"; 
			}
			if(!itemType.getValue().equals("")) {
				qrBuilder += itemType.getValue() + "\n";
			}
			if(!notes.getValue().equals("")) {
				qrBuilder += notes.getValue() + "\n";
			}
			StreamSource imgSource = new BarcodeStream();
			StreamResource res = new StreamResource(imgSource, "qrcode.jpg");
			genLayout.addComponent(new Image("", res));
		}

		
		public editPanel() {
			genLayout = new VerticalLayout();
			initEditConf();
			initEditLayout();
			addListeners();
			setSizeUndefined();
			Responsive.makeResponsive(this);
			binder.bindInstanceFields(this);
		}

		private void initEditLayout() {
			HorizontalLayout options = new HorizontalLayout(save, delete, cancel);
			options.setSpacing(true);
			addComponents(itemBarcode, itemName, itemType, isAvailable, isLate, checkOutDate, dueDate, notes, genLayout, options);
		}

		private void initEditConf() {
			save = new Button("Save");
			delete = new Button("Delete");
			cancel = new Button("Cancel");
			itemBarcode = new TextField("Barcode");
			itemBarcode.setDescription("Barcode will be generated");
			itemBarcode.setIcon(VaadinIcons.QUESTION_CIRCLE_O);
			itemBarcode.setEnabled(false);
			itemName = new TextField("Name");
			itemName.setRequiredIndicatorVisible(true);
			itemName.setMaxLength(64);
			itemType = new TextField("Type");
			itemType.setRequiredIndicatorVisible(true);
			itemType.setMaxLength(64);
			isAvailable = new ComboBox<String>("Availability");
			isAvailable.setItems("True", "False"); //should be managed by sys too
			isAvailable.setEmptySelectionAllowed(false);
			isAvailable.setTextInputAllowed(false);
			isLate = new ComboBox<String>("Overdue");
			isLate.setItems("True", "False");
			isLate.setDescription("Value is managed by the system");
			isLate.setIcon(VaadinIcons.QUESTION_CIRCLE_O);
			isLate.setEmptySelectionAllowed(false);
			isLate.setTextInputAllowed(false);
			checkOutDate = new DateField("Checkout Date");
			dueDate = new DateField("Date Due");
			notes = new TextArea("Notes");

			cancel.addClickListener(e -> this.cancel());
			save.addClickListener(e -> this.save());
			delete.addClickListener(e -> this.delete());
			

			binder.forMemberField(checkOutDate).withConverter(new LocalDateToDateConverter());
			binder.forMemberField(dueDate).withConverter(new LocalDateToDateConverter());
			binder.forMemberField(itemName).asRequired().withValidator((string -> string != null && !string.isEmpty()), "Values cannot be empty").bind("name");
			binder.forMemberField(itemType).asRequired().withValidator((string -> string != null && !string.isEmpty()), "Values cannot be empty").bind("type");
			binder.forMemberField(itemBarcode).withConverter(new StringToLongConverter(itemBarcode.getValue())).bind("barcode");
			binder.forMemberField(isAvailable).asRequired().bind("isAvailable");
			binder.forMemberField(isLate).asRequired().bind("isLate");
		}
		
		private void delete() {
			repo.delete(item); 
			Page.getCurrent().reload(); //confirmation for deletion/administrator override for deletion.
		}

		private void save() {
			//if(itemName.getValue().matches("^[0-9]*$") || itemType.getValue().matches("^[0-9]*$")) 
			
			
			//if(!binder.isValid()) {
			//	Notification.show("Check your input. Item Name and Type must not be only numbers and Quantity/Code must not be only letters");
			//}
			//else {
				//repo.save(item);
			//}	
			repo.save(item); //need to automagically refresh the grid
			Page.getCurrent().reload();
			//Validate input
		}

		private void cancel() {
			setItem(null);
			editForm.setVisible(false);
		}
		
		public void setItem(InventoryItem item) {
			setVisible(true);
			this.item = item;
			try {
				binder.setBean(item);
			} catch(NullPointerException e) {} 
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


