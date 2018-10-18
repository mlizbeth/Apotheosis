package io.valhala.tss.Inventory.view;

import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import io.valhala.tss.Inventory.backend.InventoryItem;
import io.valhala.tss.Inventory.backend.ItemRepository;
import io.valhala.tss.Inventory.backend.Patron;
import io.valhala.tss.Inventory.backend.PatronRepository;

//https://vaadin.com/forum/thread/14338571
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
	/**
	 * 
	 */
	private static final long serialVersionUID = -7607698814348085434L;
	@Autowired
	ItemRepository repo;
	@Autowired
	PatronRepository pr;
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
		filterMode.setTextInputAllowed(false);
		
		filter = new TextField();
		filter.setWidth("100%");
		filter.setPlaceholder("TextField");
		filter.setMaxLength(20);

		aOps = new ComboBox<>();
		aOps.setItems("Catalog", "Check In", "Check Out", "Renew");
		aOps.setValue("Catalog");
		aOps.setEmptySelectionAllowed(false);
		aOps.setTextInputAllowed(false);
			
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
			if(e.getValue().equals("Check Out")) {
				CheckOutWindow w = new CheckOutWindow();
				UI.getCurrent().addWindow(w);;
			}
			if(e.getValue().equals("Check In")) {
				CheckInWindow w = new CheckInWindow();
				UI.getCurrent().addWindow(w);
			}
		});
		
		filterMode.addValueChangeListener(e -> {
			if(filterMode.getValue().equals("Filter by Barcode")) {
				searchByName = false;
			}
			else {
				searchByName = true;
			}
			if(filterMode.getValue().equals("Filter by Patron")) {
				
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
				if(!(repo.findByBarcode(Long.valueOf(filter.getValue())) == null)) {
					iList.setItems(repo.findByBarcode(Long.valueOf(filter.getValue())));
				}				
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
	
	private class CheckInWindow extends Window {
		/*
		 * Scan item, mark item as available, clear due date and checkout dates
		 */
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 6175316451169030895L;
		private VerticalLayout root = new VerticalLayout();
		private Label info = new Label("Please scan the item barcode");
		private TextField iField = new TextField("");
		
		public CheckInWindow() {
			setContent(root);
			this.setHeight("300px");
			this.setWidth("350px");
			this.setResizable(false);
			this.setModal(true);
			this.center();
			iField.setPlaceholder("Item Barcode");
			root.addComponent(info);
			info.setSizeUndefined();
			root.setComponentAlignment(info, Alignment.TOP_CENTER);
			root.addComponent(iField);
			iField.setSizeUndefined();
			root.setComponentAlignment(iField, Alignment.MIDDLE_CENTER);
			iField.focus();
			addListener();
		}
		
		private void addListener() {
			iField.addValueChangeListener(e -> {
				if(iField.getValue() != "") {
					if(repo.existsById(Long.parseLong(iField.getValue()))) {
						InventoryItem temp = repo.findByBarcode(Long.parseLong(iField.getValue()));
						temp.setisAvailable("True");
						temp.setCheckOutDate(null);
						temp.setDueDate(null);
						repo.save(temp);
						Page.getCurrent().reload();
						/*
						 * If Due date is past the current checkin date mark item as overdue
						 */
					}
				}
			});
		}
	}
	
	private class NewPatronWindow extends Window {
		/**
		 * 
		 */
		private static final long serialVersionUID = -9130796652422301265L;
		private TextField idField, nameField, emailField, classField;
		private Button submit = new Button("Submit");
		private Label info = new Label("New Patron Record");
		private VerticalLayout root = new VerticalLayout();
		
		public NewPatronWindow(String patronId) {
			System.out.println(patronId);
			setContent(root);
			this.setHeight("300px");
			this.setWidth("350px");
			this.setResizable(false);
			this.setModal(true);
			this.center();
			info.setSizeUndefined();
			root.addComponent(info);
			root.setComponentAlignment(info, Alignment.TOP_CENTER);
			idField = new TextField();
			idField.setValue(patronId);
			idField.setEnabled(false);
			root.addComponent(idField);
			root.setComponentAlignment(idField, Alignment.MIDDLE_CENTER);
			nameField = new TextField();
			nameField.setPlaceholder("Name");
			root.addComponent(nameField);
			root.setComponentAlignment(nameField, Alignment.MIDDLE_CENTER);
			emailField = new TextField();
			emailField.setPlaceholder("Email Address");
			root.addComponent(emailField);
			root.setComponentAlignment(emailField, Alignment.MIDDLE_CENTER);
			classField = new TextField();
			classField.setPlaceholder("Classifcation"); //combobox?
			root.addComponent(classField);
			root.setComponentAlignment(classField, Alignment.MIDDLE_CENTER);
			root.addComponent(submit);
			root.setComponentAlignment(submit, Alignment.MIDDLE_CENTER);
			addListener();
		}
		
		private void addListener() {
			submit.addClickListener(e -> {
				Patron temp = new Patron(Long.parseLong(idField.getValue()), nameField.getValue(), emailField.getValue(),classField.getValue());
				pr.save(temp);
				this.close();
			});
		}
	}
	
	private class CheckOutWindow extends Window {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8597921460191289609L;
		private VerticalLayout root = new VerticalLayout();
		private Label info = new Label("Please Swipe the Patron's Tiger Card");
		private TextField hacky = new TextField("");
		private TextField iField = new TextField("");
		private String id;
		private Button submit = new Button("Submit");
		private DateField due = new DateField();
		private boolean validID;
		
		public CheckOutWindow() {
			setContent(root);
			this.setHeight("300px");
			this.setWidth("350px");
			this.setResizable(false);
			this.setModal(true);
			this.center();
			hacky.setPlaceholder("Patron ID");
			iField.setPlaceholder("Item Barcode");
			root.addComponent(info);
			info.setSizeUndefined();
			root.setComponentAlignment(info, Alignment.TOP_CENTER);
			root.addComponent(hacky);
			hacky.setSizeUndefined();
			root.setComponentAlignment(hacky, Alignment.MIDDLE_CENTER);
			root.addComponent(iField);
			iField.setSizeUndefined();
			root.setComponentAlignment(iField, Alignment.MIDDLE_CENTER);
			root.addComponent(due);
			due.setSizeUndefined();
			root.setComponentAlignment(due, Alignment.BOTTOM_CENTER);
			root.addComponent(submit);
			root.setComponentAlignment(submit, Alignment.BOTTOM_CENTER);
			submit.setStyleName(ValoTheme.BUTTON_PRIMARY);
			hacky.setMaxLength(11);
			addListener();
			iField.setEnabled(false);
			due.setEnabled(false);
			submit.setEnabled(false);
			hacky.focus();
		}
		
		private void verifyDates() {
			
			/*
			 * Some very hack-y logic to confirm the item's due date is being set for a date in the future.
			 */
			
			final String delimiter = "-";
			int[] cNum, dNum;
			LocalDate now = LocalDate.now();
			LocalDate dueDate = due.getValue();
			cNum = new int[3];
			cNum[0] = Integer.parseInt(now.toString().substring(0, now.toString().indexOf(delimiter)));
			cNum[1] = Integer.parseInt(now.toString().substring(now.toString().indexOf("-") + 1, now.toString().lastIndexOf("-")));
			cNum[2] = Integer.parseInt(now.toString().substring(now.toString().lastIndexOf("-") + 1));

			dNum = new int[3];
			dNum[0] = Integer.parseInt(dueDate.toString().substring(0, dueDate.toString().indexOf(delimiter)));
			dNum[1] = Integer.parseInt(dueDate.toString().substring(dueDate.toString().indexOf("-") + 1, dueDate.toString().lastIndexOf("-")));
			dNum[2] = Integer.parseInt(dueDate.toString().substring(dueDate.toString().lastIndexOf("-") + 1));

			
			if(dNum[0] >= cNum[0]) {
				if(dNum[1] >= cNum[1]) {
					if(dNum[2] > cNum[2]) {
						submit.setEnabled(true);
					}
					else { submit.setEnabled(false); }
				}
			}
		}
		
		private void addListener() {
			hacky.addValueChangeListener(e -> {
				
				if(hacky.getValue().matches("(;[0-9]+[?])") && hacky.getValue().length() == 11) {
					id = hacky.getValue().substring(hacky.getValue().indexOf(";") + 1, hacky.getValue().indexOf("?") - 2);
					validID = true;
				}
				else if(hacky.getValue().length() == 7) {
					id = hacky.getValue();
					validID = true;
				}
				if(validID) {
					if(pr.findByid(Long.parseLong(id)) != null) {
						hacky.setValue(id);
						hacky.setEnabled(false);
						iField.setEnabled(true);
						iField.focus();
						info.setValue("Please scan barcode");
					}
					else {
						//create a new patron record
						NewPatronWindow w = new NewPatronWindow(id);
						UI.getCurrent().addWindow(w);
						hacky.setEnabled(false);
						w.addCloseListener(event -> {
							iField.setEnabled(true);
						});
					}
				}
				else {
					//invalid format exception
				}
			});
			
			iField.addValueChangeListener(e -> {
				info.setValue("Please select a due date");
				//Check barcode against DB to make sure it is valid and can be checked out
				InventoryItem temp;
				if(!(iField.getValue().equals(""))) {
					if(repo.findByBarcode(Long.parseLong(iField.getValue())) != null) {
						temp = repo.findByBarcode(Long.parseLong(iField.getValue()));
						if(temp.getisAvailable().equals("False")) {
							System.out.println("Checked out");
						}
						else { 
							due.setEnabled(true);
							iField.setReadOnly(true);
						}
					}
				}
			});
			
			due.addValueChangeListener(e -> {
				verifyDates();
			});
			
			//YYYY-MM-DD
			submit.addClickListener(e -> {
				
				Date temp = new Date();
				Patron p = new Patron();
				p = pr.findByid(Long.parseLong(id));
				//commit the changes
				InventoryItem t = repo.findByBarcode(Long.parseLong(iField.getValue())); 
				t.setisAvailable("False");
				t.setCheckOutDate(temp);
				t.setcurrentPatron(p.getId());
				t.setDueDate(Date.from(due.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
				repo.save(t);
				Page.getCurrent().reload();
			});
			
			this.addCloseListener(e -> {
				aOps.setValue("Catalog");
			});
		}
	}

	private class editPanel extends FormLayout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3974948106917619006L;
		private InventoryItem item;
		private TextField itemName, itemType, itemBarcode, currentP;
		private DateField checkOutDate, dueDate;
		private Button save, delete, cancel;
		private ComboBox<String> isAvailable, isLate;
		private TextArea notes;
		private VerticalLayout genLayout;

		private Binder<InventoryItem> binder = new Binder<>(InventoryItem.class); 
		
		
		private void addListeners() {
			
			isAvailable.addValueChangeListener(e -> {
				
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
			addComponents(itemBarcode, itemName, itemType, isAvailable, isLate, checkOutDate, dueDate, notes, currentP, genLayout, options);
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
			isAvailable.setReadOnly(true);
			isLate = new ComboBox<String>("Overdue");
			isLate.setItems("True", "False");
			isLate.setDescription("Value is managed by the system");
			isLate.setIcon(VaadinIcons.QUESTION_CIRCLE_O);
			isLate.setReadOnly(true);
			checkOutDate = new DateField("Checkout Date");
			checkOutDate.setReadOnly(true);
			dueDate = new DateField("Date Due");
			dueDate.setReadOnly(true);
			notes = new TextArea("Notes");
			currentP = new TextField("Checked out to");

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
			binder.forMemberField(currentP).withConverter(new StringToLongConverter(currentP.getValue())).bind("currentPatron");
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
		/**
		 * 
		 */
		private static final long serialVersionUID = -6066440273668672243L;
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


