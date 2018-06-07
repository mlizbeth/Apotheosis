package io.valhala.tss.Inventory.view;

import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.vaadin.navigator.View;
import com.vaadin.server.Responsive;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.v7.ui.Calendar;
import com.vaadin.v7.ui.components.calendar.CalendarComponentEvents.DateClickEvent;
import com.vaadin.v7.ui.components.calendar.CalendarComponentEvents.WeekClick;
import com.vaadin.v7.ui.components.calendar.CalendarComponentEvents.WeekClickHandler;
import com.vaadin.v7.ui.components.calendar.event.BasicEventProvider;
import com.vaadin.v7.ui.components.calendar.handler.BasicDateClickHandler;
import com.vaadin.v7.ui.components.calendar.handler.BasicWeekClickHandler;


@SuppressWarnings("deprecation")
@SpringView(name = ScheduleView.VIEW_NAME)
@SpringComponent
public class ScheduleView extends GridLayout implements View {
	
	private enum Mode {
		MONTH, WEEK, DAY;
	}
	
	public static final String VIEW_NAME = "Schedule";
	private GregorianCalendar internalCalendar;
	private Calendar calendarComponent;
	private Date currentMonthFirstDate;
	private final Label captionLabel = new Label("");
	private Button monthButton, weekButton, dayButton, nextButton, prevButton;
	private TextField captionField;
	private Mode viewMode = Mode.WEEK;
	private String calendarHeight, calendarWidth = null;
	private Integer firstHour, lastHour, firstDay, lastDay;
	private BasicEventProvider dataSource;

	
	
	public ScheduleView() {
		setSizeFull();
		Responsive.makeResponsive(this);
		firstHour = 7;
		lastHour = 22;
		
		initCalendar();

	}



	private void initCalendar() {
		dataSource = new BasicEventProvider();
		calendarComponent = new Calendar(dataSource);
		calendarComponent.setImmediate(true);
		Date today = getToday();
		
		if(calendarWidth != null || calendarHeight != null)
		{
			if(calendarHeight != null) 
			{
				calendarComponent.setHeight(calendarHeight);
			}
			if(calendarWidth != null)
			{
				calendarComponent.setWidth(calendarWidth);
			}
			else
			{
				calendarComponent.setSizeFull();
			}
		}
		
		if(firstHour != null && lastHour != null)
		{
			calendarComponent.setFirstVisibleHourOfDay(firstHour);
			calendarComponent.setLastVisibleHourOfDay(lastHour);
		}
		
		if(firstDay != null && lastDay != null)
		{
			calendarComponent.setFirstDayOfWeek(firstDay);
			calendarComponent.setLastVisibleDayOfWeek(lastDay);
}
		
		internalCalendar = new GregorianCalendar(Locale.US);
		internalCalendar.setTime(today);
		calendarComponent.getInternalCalendar().setTime(today);
		
		calendarComponent.setStartDate(calendarComponent.getStartDate());
		calendarComponent.setEndDate(calendarComponent.getEndDate());
		int rollAmount = internalCalendar.get(GregorianCalendar.DAY_OF_MONTH - 1);
		internalCalendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
		currentMonthFirstDate = internalCalendar.getTime();
		internalCalendar.setTime(today);
		
		updateCaptionLabel();
		addListeners();
		initLayout();
		
	}
	

	private void updateCaptionLabel() {
		DateFormatSymbols s = new DateFormatSymbols(Locale.US);
		String month = s.getShortMonths()[internalCalendar.get(GregorianCalendar.MONTH)];
		captionLabel.setValue(month + " " + internalCalendar.get(GregorianCalendar.YEAR));
	}



	private void addListeners() {
		
		calendarComponent.setHandler(new BasicWeekClickHandler() {
			
			@Override
			public void weekClick(WeekClick event) {
				super.weekClick(event);
				updateCaptionLabel();
				switchToWeekView();
			}
		});
		
		calendarComponent.setHandler(new BasicDateClickHandler() {
			
			@Override
			public void dateClick(DateClickEvent event) {
				
				super.dateClick(event);
				switchToDayView();
			}
		});
		
	}
	
	private void initLayout() {
		initNavigationButtons();
		
		HorizontalLayout h1 = new HorizontalLayout();
		h1.setWidth("100%");
		h1.setSpacing(true);
		h1.addComponents(prevButton, nextButton, captionLabel);
		
		CssLayout group = new CssLayout();
		group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		group.addComponents(dayButton, weekButton, monthButton);
		h1.addComponent(group);
		
		h1.setComponentAlignment(prevButton, Alignment.MIDDLE_LEFT);
		h1.setComponentAlignment(captionLabel, Alignment.MIDDLE_CENTER);
		h1.setComponentAlignment(group, Alignment.MIDDLE_CENTER);
		h1.setComponentAlignment(nextButton, Alignment.MIDDLE_RIGHT);
		
		//HorizontalLayout controlPanel = new HorizontalLayout();
		//controlPanel.setSpacing(true);
		//controlPanel.setWidth("100%");
		
		addComponents(h1, calendarComponent);
		calendarComponent.setSizeFull();
		setRowExpandRatio(getRows() - 1, 1.0f);
		
	}



	private void initNavigationButtons() 
	{
		monthButton = new Button("Month", new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				switchToMonthView();
			}
		});
		
		weekButton = new Button("Week", new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				WeekClickHandler handler = (WeekClickHandler) calendarComponent.getHandler(WeekClick.EVENT_ID);
				handler.weekClick(new WeekClick(calendarComponent, internalCalendar.get(GregorianCalendar.WEEK_OF_YEAR), internalCalendar.get(GregorianCalendar.YEAR)));
				
			}
			
		});
		
		dayButton = new Button("Day", new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				BasicDateClickHandler handler = (BasicDateClickHandler) calendarComponent.getHandler(DateClickEvent.EVENT_ID);
				handler.dateClick(new DateClickEvent(calendarComponent, internalCalendar.getTime()));
				
			}
			
		});
		nextButton = new Button("Next", e -> handleNavClick(e));
		prevButton = new Button("Prev", e -> handleNavClick(e));
	}



	private void handleNavClick(ClickEvent e) {
		switch(viewMode) {
		case MONTH:
			if(e.getButton() == nextButton) {
				roll(Mode.MONTH, 1);
			}
			else {
				roll(Mode.MONTH, -1);
			}
			break;
		case WEEK:
			if(e.getButton() == nextButton) {
				roll(Mode.WEEK, 1);
			}
			else {
				roll(Mode.WEEK, -1);
			}
			break;
		case DAY:
			if(e.getButton() == nextButton) {
				roll(Mode.DAY, 1);
			}
			else {
				roll(Mode.DAY, -1);
			}
			break;
		}
	}
	
	private void roll(Mode viewMode, int direction) {
		
		if(viewMode == Mode.MONTH) {
			System.out.println("Month if");
			internalCalendar.setTime(currentMonthFirstDate);
			internalCalendar.add(GregorianCalendar.MONTH, direction);
			resetCalendarTime(false);
			currentMonthFirstDate = internalCalendar.getTime();
			calendarComponent.setStartDate(currentMonthFirstDate);
			
			updateCaptionLabel();
			
			internalCalendar.add(GregorianCalendar.MONTH, 1);
			internalCalendar.add(GregorianCalendar.DATE, -1);
			resetCalendarTime(true);
		}
		if(viewMode == Mode.WEEK) {
			System.out.println("Week if");
			internalCalendar.add(GregorianCalendar.WEEK_OF_YEAR, direction);
			internalCalendar.add(GregorianCalendar.DAY_OF_WEEK, internalCalendar.getFirstDayOfWeek());
			resetCalendarTime(false);
			resetInternalTime(true);
			internalCalendar.add(GregorianCalendar.DATE, 6);
			calendarComponent.setEndDate(internalCalendar.getTime());
		}
		if(viewMode == Mode.DAY) {
			System.out.println("day if");
			internalCalendar.add(GregorianCalendar.DATE, direction);
			resetCalendarTime(false);
			resetCalendarTime(true);
		}
	}
	
	
	private void resetCalendarTime(boolean x) {
		resetInternalTime(x);
		if(x) {
			calendarComponent.setEndDate(internalCalendar.getTime());
		}
		else {
			calendarComponent.setStartDate(internalCalendar.getTime());
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
        } 
		else {
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
		int rollAmount = internalCalendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
		calendarComponent.setStartDate(internalCalendar.getTime());
		updateCaptionLabel();
		internalCalendar.add(GregorianCalendar.MONTH, 1);
		internalCalendar.add(GregorianCalendar.DATE, -1);
		calendarComponent.setEndDate(internalCalendar.getTime());
	}

	private static Date getStartOfDay(java.util.Calendar calendar, Date date) {
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

	private Date getToday() {
		return new Date();
	}

	
}
