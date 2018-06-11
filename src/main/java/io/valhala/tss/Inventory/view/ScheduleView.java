package io.valhala.tss.Inventory.view;

import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.navigator.View;
import com.vaadin.server.Responsive;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.v7.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.v7.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.Calendar;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.v7.ui.components.calendar.CalendarComponentEvents.DateClickEvent;
import com.vaadin.v7.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.v7.ui.components.calendar.CalendarComponentEvents.WeekClick;
import com.vaadin.v7.ui.components.calendar.CalendarComponentEvents.WeekClickHandler;
import com.vaadin.v7.ui.components.calendar.event.BasicEvent;
import com.vaadin.v7.ui.components.calendar.event.BasicEventProvider;
import com.vaadin.v7.ui.components.calendar.event.CalendarEvent;
import com.vaadin.v7.ui.components.calendar.handler.BasicDateClickHandler;
import com.vaadin.v7.ui.components.calendar.handler.BasicWeekClickHandler;
import com.vaadin.v7.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import io.valhala.tss.Inventory.backend.Shift;
import io.valhala.tss.Inventory.backend.ShiftRepository;

@SpringView(name = ScheduleView.VIEW_NAME)
@SpringComponent
@SuppressWarnings("deprecation")
public class ScheduleView extends GridLayout implements View {
	private enum Mode {
		MONTH, WEEK, DAY;
	}
	
	@Autowired
	ShiftRepository repo;
	
	public static final String VIEW_NAME = "S2";
	private GregorianCalendar internalCalendar;
	private Calendar calendar;
	private Date currentMonthFirstDate;
	private final Label captionLabel = new Label("");
	private Button monthButton, weekButton, dayButton, nextButton, prevButton, addNewEvent, applyEventButton, deleteEventButton;
	private CheckBox hideWeekends, readOnly, disabled;
	private TextField captionField;
	private Window scheduleEventPopup;
	private final FormLayout scheduleEventFieldLayout = new FormLayout();
    private FieldGroup scheduleEventFieldGroup = new FieldGroup();
	private Mode viewMode = Mode.WEEK;
	private BasicEventProvider dataSource;
	private String calendarHeight, calendarWidth = null;
	private Integer firstHour, lastHour, firstDay, lastDay;
	private Locale defaultLocale = Locale.US;
	private boolean showWeeklyView, useSecondResolution;
	private DateField startDateField, endDateField;
	private Binder<CalendarEvent> scheduledEventBinder = new Binder<>();
	//private Shift mot = new Shift(getToday(), getToday(), "Maddie", "RCC");
	
	public ScheduleView() {
		setSizeFull();
		setMargin(true);
		setSpacing(true);
		Responsive.makeResponsive(this);
		firstHour = 9;
		lastHour = 20;
		init1();
	}
	
	@PostConstruct
	void init() {
		//I was able to save and retrieve items from the DB and add to calendar
		//but I lost the work :|
		//repo.save(mot); no identifier specified!
	}

	private void init1() {
		setLocale(Locale.getDefault());
		initCalendar();
		initLayout();
		addInitialEvents();
	}

	private void addInitialEvents() {
    	Date start = internalCalendar.getTime();
        Date end = (Date) start.clone();
        
        start.setHours(8);
        start.setMinutes(0);
        
        end.setHours(17);
        end.setMinutes(0);
        
        Shift test3 = new Shift(start,end,"Dalton","TSS-AV");
        Shift test4 = new Shift(start,end,"Connie", "RCC");
        Shift test5 = new Shift(start,end,"Ray","TSS-David?");
        Shift test6 = new Shift(start,end,"Some Dude", "TSS-Larry?");
        
        test3.setCaption(test3.getName());
        test3.setDescription(test3.getPosition());
        test3.setStyleName("color1");
        
        test4.setCaption(test4.getName());
        test4.setDescription(test4.getPosition());
        test4.setStyleName("color2");
        
        test5.setCaption(test5.getName());
        test5.setDescription(test5.getPosition());
        test5.setStyleName("color3");

        test6.setCaption(test6.getName());
        test6.setDescription(test6.getPosition());
        test6.setStyleName("color4");
       
        dataSource.addEvent(test3);
        dataSource.addEvent(test4);
        dataSource.addEvent(test5);
        dataSource.addEvent(test6);
	}

	private ComboBox createCalendarFormatSelect() {
		ComboBox temp = new ComboBox("Calendar Format");
		return temp;
	}

	private ComboBox createTimeZoneSelect() {
		ComboBox temp = new ComboBox("Timezone");
		return temp;
	}

	private ComboBox createLocaleSelect() {
		ComboBox temp = new ComboBox("Locale");
		return temp;
	}
	
    private CheckBox createCheckBox(String caption) {
        CheckBox cb = new CheckBox(caption);
        return cb;
    }

	private void initCalendar() {
		dataSource = new BasicEventProvider();
		calendar = new Calendar(dataSource);
		calendar.setLocale(getLocale());
		calendar.setImmediate(true);
		
		if(calendarWidth != null || calendarHeight != null) {
			if(calendarHeight != null) {calendar.setHeight(calendarHeight);}
			if(calendarWidth != null) {calendar.setWidth(calendarWidth);}
			else {calendar.setSizeFull();}
		}
		
		if(firstHour != null && lastHour != null) {
			calendar.setFirstVisibleHourOfDay(firstHour);
			calendar.setLastVisibleHourOfDay(lastHour);
		}
		
		if(firstDay != null && lastDay != null) {
			calendar.setFirstDayOfWeek(firstDay);
			calendar.setLastVisibleDayOfWeek(lastDay);
		}
		
		Date today = getToday();
		
		internalCalendar = new GregorianCalendar(getLocale());
		internalCalendar.setTime(today);
		calendar.getInternalCalendar().setTime(today);
		
		/* Calendar getStartDate (and getEndDate) has some strange logic
		 * which returns Monday of the current internal time if no start date
		 * has been set*/
		calendar.setStartDate(calendar.getStartDate());
		calendar.setEndDate(calendar.getEndDate());
		int rollAmount = internalCalendar.get(GregorianCalendar.DAY_OF_MONTH - 1);
		internalCalendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
		currentMonthFirstDate = internalCalendar.getTime();
		
		internalCalendar.setTime(today);
		updateCaptionLabel();
		addEventListeners();
	}

	private void updateCaptionLabel() {
		 DateFormatSymbols s = new DateFormatSymbols(getLocale());
	     String month = s.getShortMonths()[internalCalendar.get(GregorianCalendar.MONTH)];
	     captionLabel.setValue(month + " " + internalCalendar.get(GregorianCalendar.YEAR));
	}

	private void addEventListeners() {
		
		calendar.setHandler(new EventClickHandler() {

			@Override
			public void eventClick(EventClick event) {
				showEventPopup(event.getCalendarEvent(), false);
			}
		});
		
		calendar.setHandler(new BasicWeekClickHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void weekClick(WeekClick event) {
				super.weekClick(event);
				updateCaptionLabel();
				switchToWeekView();
			}
		});
		
		calendar.setHandler(new BasicDateClickHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void dateClick(DateClickEvent event) {
				super.dateClick(event);
				switchToDayView();
			}
		});
	}

	private void initLayout() {
		initNavigationButtons();
		initHideWeekendButton();
		initReadOnlyButton();
		initDisabledButton();
		initAddEventButton();
		
		HorizontalLayout h1 = new HorizontalLayout();
		h1.setWidth("100%");
		h1.setSpacing(true);
		h1.addComponent(prevButton);
		h1.addComponent(captionLabel);
		
		CssLayout group = new CssLayout();
		group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		group.addComponent(dayButton);
		group.addComponent(weekButton);
		group.addComponent(monthButton);
		h1.addComponent(group);
		
		h1.addComponent(nextButton);
		h1.setComponentAlignment(prevButton, Alignment.MIDDLE_LEFT);
		h1.setComponentAlignment(captionLabel, Alignment.MIDDLE_CENTER);
		h1.setComponentAlignment(group, Alignment.MIDDLE_CENTER);
		h1.setComponentAlignment(nextButton, Alignment.MIDDLE_RIGHT);
		
		//disable unless we are in month view.
		nextButton.setVisible(false);
		prevButton.setVisible(false);
		
		//monthButton.setVisible(viewMode == Mode.WEEK);
		//weekButton.setVisible(viewMode == Mode.DAY);
		
		HorizontalLayout controlPanel = new HorizontalLayout();
		controlPanel.setSpacing(true);
		controlPanel.setWidth("100%");
		//controlPanel.addComponent(localeSelect);
		//controlPanel.addComponent(tzselect);
		//controlPanel.addComponent(formatSelect);
		controlPanel.addComponent(hideWeekends);
		controlPanel.addComponent(readOnly);
		controlPanel.addComponent(disabled);
		controlPanel.addComponent(addNewEvent);
		
		//controlPanel.setComponentAlignment(tzselect, Alignment.MIDDLE_LEFT);
		//controlPanel.setComponentAlignment(formatSelect, Alignment.MIDDLE_LEFT);
		//controlPanel.setComponentAlignment(localeSelect, Alignment.MIDDLE_LEFT);
		controlPanel.setComponentAlignment(hideWeekends, Alignment.BOTTOM_LEFT);
		controlPanel.setComponentAlignment(readOnly, Alignment.BOTTOM_LEFT);
		controlPanel.setComponentAlignment(disabled, Alignment.BOTTOM_LEFT);
		controlPanel.setComponentAlignment(addNewEvent, Alignment.BOTTOM_RIGHT);
		
		addComponent(controlPanel);
		addComponent(h1);
		addComponent(calendar);
		/* calendar size is set properply and is responsive
		 * though we need to find a way to divide the cells vertically
		 * by day
		 * if the window is too small, the calendar no longer
		 * displays the line showing current time of day */
		calendar.setSizeFull(); 
		setRowExpandRatio(getRows() - 1, 1.0f);	
	}

	private void initAddEventButton() {
		addNewEvent = new Button("Add Event");
		addNewEvent.addStyleName("primary");
        addNewEvent.addStyleName(ValoTheme.BUTTON_SMALL);
        addNewEvent.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = -8307244759142541067L;

            @Override
            public void buttonClick(ClickEvent event) {
                Date start = getToday();
                start.setHours(8);
                start.setMinutes(0);
                start.setSeconds(0);

                Date end = getEndOfDay(internalCalendar, start);
                end.setHours(22);
                end.setMinutes(0);
                end.setSeconds(0);

                showEventPopup(createNewEvent(start, end), true);
            }
        });
	}
	
	private void showEventPopup(CalendarEvent event, boolean newEvent) {
		if(event == null) {return;}
		
		updateCalendarEventPopup(newEvent);
		updateCalendarEventForm(event);
		captionField.focus();
		
		if(!getUI().getWindows().contains(scheduleEventPopup)) {
			getUI().addWindow(scheduleEventPopup);
		}
	}
	
	private void updateCalendarEventPopup(boolean newEvent) {
		if(scheduleEventPopup == null) {createCalendarEventPopup();}
		if(newEvent) {scheduleEventPopup.setCaption("New event");}
		else {scheduleEventPopup.setCaption("Edit event");}
		
		deleteEventButton.setVisible(!newEvent);
		deleteEventButton.setEnabled(!calendar.isReadOnly());
		applyEventButton.setEnabled(!calendar.isReadOnly());
	}
	
	 private void updateCalendarEventForm(CalendarEvent event) {
		 BeanItem<CalendarEvent> item = new BeanItem<>(event);
	     scheduleEventFieldLayout.removeAllComponents();
	     scheduleEventFieldGroup = new FieldGroup();
	     initFormFields(scheduleEventFieldLayout, event.getClass());
	     scheduleEventFieldGroup.setBuffered(true);
	     scheduleEventFieldGroup.setItemDataSource(item);
	     scheduledEventBinder.readBean(event);
	  }
	 
	 
	 private void initFormFields(Layout formLayout, Class<? extends CalendarEvent> eventClass) {
		 	startDateField = createDateField("Shift starts");
	        endDateField = createDateField("Shift ends");
	        /* probably dont need this component */
	       // final CheckBox allDayField = createCheckBox("All-day");
	       /* allDayField.addValueChangeListener(event -> {
	            if (event.getValue()) {
	                setFormDateResolution(Resolution.DAY);
	            } else {
	                setFormDateResolution(Resolution.MINUTE);
	            }
	        }); */

	        captionField = createTextField("Employee");
	        captionField.setInputPrompt("Employee Name");
	        captionField.setRequired(true);
	        //final TextField whereField = createTextField("Where");
	        //whereField.setInputPrompt("Address or location");
	        final TextArea descriptionField = createTextArea("Notes");
	        descriptionField.setInputPrompt("");
	        descriptionField.setRows(3);
	        descriptionField.setRequired(false);

	        final ComboBox styleNameField = createStyleNameComboBox();
	        styleNameField.setInputPrompt("Assign a position");
	        styleNameField.setTextInputAllowed(false);

	        formLayout.addComponent(startDateField);
	        endDateField.setRequired(true);
	        startDateField.setRequired(true);
	        formLayout.addComponent(endDateField);
	        //formLayout.addComponent(allDayField);
	        formLayout.addComponent(captionField);
	        // captionField.setComponentError(new UserError("Testing error"));
	        /*if (eventClass == CalendarTestEvent.class) {
	            formLayout.addComponent(whereField);
	        }*/
	        formLayout.addComponent(descriptionField);
	        formLayout.addComponent(styleNameField);

	        scheduleEventFieldGroup.bind(startDateField, "start");
	        scheduleEventFieldGroup.bind(endDateField, "end");
	        scheduleEventFieldGroup.bind(captionField, "caption"); //employee 
	        scheduleEventFieldGroup.bind(descriptionField, "description"); //notes
	       /* if (eventClass == CalendarTestEvent.class) {
	            scheduleEventFieldGroup.bind(whereField, "where");
	        } */
	        scheduleEventFieldGroup.bind(styleNameField, "styleName");
	        //scheduledEventBinder.bind(allDayField, CalendarEvent::isAllDay, null);
	 }
	
	    private ComboBox createStyleNameComboBox() {
	        ComboBox s = new ComboBox("Area");
	        s.setRequired(true);
	        s.addContainerProperty("c", String.class, "");
	        s.setItemCaptionPropertyId("c");
	        Item i = s.addItem("color1");
	        i.getItemProperty("c").setValue("TSS-AV");
	        i = s.addItem("color2");
	        i.getItemProperty("c").setValue("RCC");
	        i = s.addItem("color3");
	        i.getItemProperty("c").setValue("TSS-Larry");
	        i = s.addItem("color4");
	        i.getItemProperty("c").setValue("TSS-David");
	        return s;
	    }
	 
	    private TextArea createTextArea(String caption) {
	        TextArea f = new TextArea(caption);
	        f.setNullRepresentation("");
	        return f;
	    }
	 
	    private TextField createTextField(String caption) {
	        TextField f = new TextField(caption);
	        f.setNullRepresentation("");
	        return f;
	    }
	 
	    private void setFormDateResolution(Resolution resolution) {
	        if (startDateField != null && endDateField != null) {
	            startDateField.setResolution(resolution);
	            endDateField.setResolution(resolution);
	        }
	    }
	    
	    private DateField createDateField(String caption) {
	        DateField f = new DateField(caption);
	        if (useSecondResolution) {f.setResolution(Resolution.SECOND);} 
	        else {f.setResolution(Resolution.MINUTE);}
	        return f;
	    }
	
	private void createCalendarEventPopup() {
		VerticalLayout layout = new VerticalLayout();
		//layout.setMargin(true);
		layout.setSpacing(true);
		
		scheduleEventPopup = new Window(null, layout);
		scheduleEventPopup.setWidth("300px");
		scheduleEventPopup.setModal(true);
		scheduleEventPopup.center();
		
		scheduleEventFieldLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
		scheduleEventFieldLayout.setMargin(false);
		layout.addComponent(scheduleEventFieldLayout);
		
		applyEventButton = new Button("Apply", new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    commitCalendarEvent();
                } catch (CommitException | ValidationException e) {
                    e.printStackTrace();
                }
            }
        });
		
		
        applyEventButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        Button cancel = new Button("Cancel", new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                discardCalendarEvent();
            }
        });
        deleteEventButton = new Button("Delete", new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                deleteCalendarEvent();
            }
        });
        deleteEventButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        scheduleEventPopup.addCloseListener(new Window.CloseListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void windowClose(Window.CloseEvent e) {
                discardCalendarEvent();
            }
        });

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        buttons.setWidth("100%");
        buttons.setSpacing(true);
        buttons.addComponent(deleteEventButton);
        buttons.addComponent(applyEventButton);
        buttons.setExpandRatio(applyEventButton, 1);
        buttons.setComponentAlignment(applyEventButton, Alignment.TOP_RIGHT);
        buttons.addComponent(cancel);
        layout.addComponent(buttons);

    }
	
	 private void commitCalendarEvent()
	            throws ValidationException, CommitException {
	        scheduleEventFieldGroup.commit();
	        BasicEvent event = getFormCalendarEvent();
	        scheduledEventBinder.writeBean(event);
	        if (event.getEnd() == null) {
	            event.setEnd(event.getStart());
	        }
	        if (!dataSource.containsEvent(event)) {
	            dataSource.addEvent(event);
	        }

	        getUI().removeWindow(scheduleEventPopup);
	    }
	 
	 private void deleteCalendarEvent() {
	        BasicEvent event = getFormCalendarEvent();
	        if (dataSource.containsEvent(event)) {
	            dataSource.removeEvent(event);
	        }
	        getUI().removeWindow(scheduleEventPopup);
	    }
	 
	 private void discardCalendarEvent() {
	        scheduleEventFieldGroup.discard();
	        scheduledEventBinder.readBean(getFormCalendarEvent());
	        getUI().removeWindow(scheduleEventPopup);
	    }
	 
	    @SuppressWarnings("unchecked")
	    private BasicEvent getFormCalendarEvent() {
	        BeanItem<CalendarEvent> item = (BeanItem<CalendarEvent>) scheduleEventFieldGroup
	                .getItemDataSource();
	        CalendarEvent event = item.getBean();
	        return (BasicEvent) event;
	    }
	
	//Default implementation
	private CalendarEvent createNewEvent(Date startDate, Date endDate) {
		BasicEvent event = new BasicEvent();
		event.setCaption("");
		event.setStart(startDate);
		event.setEnd(endDate);
		event.setStyleName("color1");
		return event;
	}

	private void initDisabledButton() {
		disabled = new CheckBox("Disabled"); 
	}

	private void initReadOnlyButton() {
		readOnly = new CheckBox("Read-Only");
		readOnly.addValueChangeListener(e -> {
			calendar.setReadOnly(readOnly.getValue());
		});
	}

	private void initHideWeekendButton() {
		hideWeekends = new CheckBox("Hide Weekends");
		hideWeekends.addValueChangeListener(e -> {
			setWeekendsHidden(hideWeekends.getValue());
		});
	}

	private void setWeekendsHidden(Boolean hidden) {
		if(hidden) {
			int firstDay = (GregorianCalendar.MONDAY - calendar.getFirstVisibleDayOfWeek() % 7);
			calendar.setFirstVisibleDayOfWeek(firstDay + 1);
			calendar.setLastVisibleDayOfWeek(firstDay + 4);
		}
		else {
			calendar.setFirstVisibleDayOfWeek(1);
			calendar.setLastVisibleDayOfWeek(7);
		}	
	}
	
	private void initNavigationButtons() {
		//monthButton = new Button("Month", e -> switchToMonthView());
		monthButton = new Button("Month", new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if(!prevButton.isVisible() && !nextButton.isVisible()) {
					prevButton.setVisible(true);
					nextButton.setVisible(true);
				}
				switchToMonthView();
			}
		});
		//weekButton = new Button("Week", e -> switchToWeekView());
		weekButton = new Button("Week", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(prevButton.isVisible() && nextButton.isVisible()) {
					prevButton.setVisible(false);
					nextButton.setVisible(false);
				}
				WeekClickHandler handler = (WeekClickHandler) calendar.getHandler(WeekClick.EVENT_ID);
				handler.weekClick(new WeekClick(calendar, internalCalendar.get(GregorianCalendar.WEEK_OF_YEAR), internalCalendar.get(GregorianCalendar.YEAR)));
			}
		});
		//dayButton = new Button("Day", e -> switchToDayView());
		dayButton = new Button("Day", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(prevButton.isVisible() && nextButton.isVisible()) {
					prevButton.setVisible(false);
					nextButton.setVisible(false);
				}
				BasicDateClickHandler handler = (BasicDateClickHandler) calendar.getHandler(DateClickEvent.EVENT_ID);
				handler.dateClick(new DateClickEvent(calendar, internalCalendar.getTime()));
			}
		});
		nextButton = new Button("Next", e -> handleNavClick(e));
		prevButton = new Button("Prev", e -> handleNavClick(e));
	}

	/* test code to see if we can implement all nav features in one method */
	private void handleNavClick(ClickEvent e) {
		switch(viewMode) {
		case MONTH:
			if(e.getButton() == nextButton) {roll(Mode.MONTH, 1);}
			else {roll(Mode.MONTH, -1);}
			break;
		case WEEK: 
			if(e.getButton() == nextButton) {roll(Mode.WEEK, 1);}
			else {roll(Mode.WEEK, -1);}
			break;
		case DAY:
			if(e.getButton() == nextButton) {roll(Mode.DAY, 1);}
			else {roll(Mode.DAY, -1);}
			break;
		}
	}
	
	private void roll(Mode viewMode, int direction) {
		if(viewMode == Mode.MONTH) {
			System.out.println("month if");
			internalCalendar.setTime(currentMonthFirstDate);
			internalCalendar.add(GregorianCalendar.MONTH, direction);
			resetCalendarTime(false);
			currentMonthFirstDate = internalCalendar.getTime();
			calendar.setStartDate(currentMonthFirstDate);
			
			updateCaptionLabel();
			
			internalCalendar.add(GregorianCalendar.MONTH, 1);
			internalCalendar.add(GregorianCalendar.DATE, -1);
			resetCalendarTime(true);	
		}
		if(viewMode == Mode.WEEK) {
			System.out.println("week if");
			internalCalendar.add(GregorianCalendar.WEEK_OF_YEAR, direction);
			internalCalendar.add(GregorianCalendar.DAY_OF_WEEK, internalCalendar.getFirstDayOfWeek());
			resetCalendarTime(false);
			resetInternalTime(true);
			internalCalendar.add(GregorianCalendar.DATE, 6);
			calendar.setEndDate(internalCalendar.getTime());
		}
		if(viewMode == Mode.DAY) {
			System.out.println("day if");
			internalCalendar.add(GregorianCalendar.DATE, direction);
			resetCalendarTime(false);
			resetCalendarTime(true);
		}
	}
	
	/* why do we need these 2 reset methods?*/ //first is calendar component
	//maybe change to updateCalendarTime and restCalendarTime
	private void resetCalendarTime(boolean x) {
		resetInternalTime(x);
		if(x) {calendar.setEndDate(internalCalendar.getTime());}
		else {
			calendar.setStartDate(internalCalendar.getTime());
			updateCaptionLabel();
		}
	}
	
	private void resetInternalTime(boolean x) {
		if(x) {
			internalCalendar.set(GregorianCalendar.HOUR_OF_DAY, internalCalendar.getMaximum(GregorianCalendar.HOUR_OF_DAY));
            internalCalendar.set(GregorianCalendar.MINUTE,
                    internalCalendar.getMaximum(GregorianCalendar.MINUTE));
            internalCalendar.set(GregorianCalendar.SECOND,
                    internalCalendar.getMaximum(GregorianCalendar.SECOND));
            internalCalendar.set(GregorianCalendar.MILLISECOND,
                    internalCalendar.getMaximum(GregorianCalendar.MILLISECOND));
        } else {
            internalCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
            internalCalendar.set(GregorianCalendar.MINUTE, 0);
            internalCalendar.set(GregorianCalendar.SECOND, 0);
            internalCalendar.set(GregorianCalendar.MILLISECOND, 0);
        }
	}

	private void switchToDayView() {viewMode = Mode.DAY;}

	private void switchToWeekView() {viewMode = Mode.WEEK;}

	private void switchToMonthView() {
		viewMode = Mode.MONTH;
		int rollAmount = internalCalendar.get(GregorianCalendar.DAY_OF_MONTH) -1;
		internalCalendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
		calendar.setStartDate(internalCalendar.getTime());
		updateCaptionLabel();
		internalCalendar.add(GregorianCalendar.MONTH, 1);
		internalCalendar.add(GregorianCalendar.DATE, -1);
		calendar.setEndDate(internalCalendar.getTime());
		internalCalendar.setTime(getToday());
	}

	private Date getToday() {return new Date();}
	
	//default implementation - to change
    private static Date getEndOfDay(java.util.Calendar calendar, Date date) {
        java.util.Calendar calendarClone = (java.util.Calendar) calendar
                .clone();

        calendarClone.setTime(date);
        calendarClone.set(java.util.Calendar.MILLISECOND,
                calendarClone.getActualMaximum(java.util.Calendar.MILLISECOND));
        calendarClone.set(java.util.Calendar.SECOND,
                calendarClone.getActualMaximum(java.util.Calendar.SECOND));
        calendarClone.set(java.util.Calendar.MINUTE,
                calendarClone.getActualMaximum(java.util.Calendar.MINUTE));
        calendarClone.set(java.util.Calendar.HOUR,
                calendarClone.getActualMaximum(java.util.Calendar.HOUR));
        calendarClone.set(java.util.Calendar.HOUR_OF_DAY,
                calendarClone.getActualMaximum(java.util.Calendar.HOUR_OF_DAY));

        return calendarClone.getTime();
    }
	
	//default implementation - to change
    private static Date getStartOfDay(java.util.Calendar calendar, Date date) {
        java.util.Calendar calendarClone = (java.util.Calendar) calendar.clone();

        calendarClone.setTime(date);
        calendarClone.set(java.util.Calendar.MILLISECOND, 0);
        calendarClone.set(java.util.Calendar.SECOND, 0);
        calendarClone.set(java.util.Calendar.MINUTE, 0);
        calendarClone.set(java.util.Calendar.HOUR, 0);
        calendarClone.set(java.util.Calendar.HOUR_OF_DAY, 0);

        return calendarClone.getTime();
    }
}
