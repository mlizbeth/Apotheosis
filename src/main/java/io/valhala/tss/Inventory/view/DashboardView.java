package io.valhala.tss.Inventory.view;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.Background;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataLabels;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.Pane;
import com.vaadin.addon.charts.model.PlotOptionsSolidgauge;
import com.vaadin.addon.charts.model.SeriesTooltip;
import com.vaadin.addon.charts.model.Stop;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
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
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringView(name=DashboardView.VIEW_NAME)
@SpringComponent
public class DashboardView extends VerticalLayout implements View {
	
	public static final String VIEW_NAME = "";
	private HorizontalLayout header, headerTools;
	private Button notifications, timeButton;
	private boolean clockedIn = true;
	private CssLayout dPanels = new CssLayout();
	
	public DashboardView() {
		//setSizeUndefined();
		Responsive.makeResponsive(this);
		addStyleName("dashboard-view");	
		dPanels.addStyleName("dashboard-panels");
		builderHeader();
		addComponent(dPanels);
		dPanels.addComponents(buildTimeClock());
		
		/*
		 * I want these panels to take up 50% without the interior
		 * component being messed up. so 2 panels per row.
		 */

		//buildWidgets();
		
	}

	private Component buildTimeClock() {
		Chart chart = new Chart();
		chart.setSizeFull();
        //chart.setWidth(500, Unit.PIXELS);

        final Configuration configuration = chart.getConfiguration();
        configuration.getChart().setType(ChartType.SOLIDGAUGE);

        configuration.getTitle().setText("Weekly Timesheet");

        Pane pane = new Pane();
        pane.setCenter("50%", "85%");
        pane.setSize("140%");
        pane.setStartAngle(-90);
        pane.setEndAngle(90);
        configuration.addPane(pane);

        configuration.getTooltip().setEnabled(false);

        Background bkg = new Background();
        bkg.setBackgroundColor(new SolidColor("#eeeeee"));
        bkg.setInnerRadius("60%");
        bkg.setOuterRadius("100%");
        bkg.setShape("arc");
        bkg.setBorderWidth(0);
        pane.setBackground(bkg);

        YAxis yaxis = configuration.getyAxis();
        yaxis.setLineWidth(0);
        yaxis.setTickInterval(5);
        yaxis.setTickWidth(0);
        yaxis.setMin(0);
        yaxis.setMax(60);
        yaxis.setTitle("");
        yaxis.getTitle().setY(-70);
        yaxis.setLabels(new Labels());
        yaxis.getLabels().setY(16);
        Stop stop1 = new Stop(0.1f, SolidColor.GREEN);
        Stop stop2 = new Stop(0.5f, SolidColor.YELLOW);
        Stop stop3 = new Stop(0.9f, SolidColor.RED);
        yaxis.setStops(stop1, stop2, stop3);

        PlotOptionsSolidgauge plotOptions = new PlotOptionsSolidgauge();
        plotOptions.setTooltip(new SeriesTooltip());
        plotOptions.getTooltip().setValueSuffix(" hours");
        DataLabels labels = new DataLabels();
        labels.setY(5);
        labels.setBorderWidth(0);
        labels.setUseHTML(true);
        labels.setFormat("<div style=\"text-align:center\"><span style=\"font-size:25px;\">{y}</span><br/>"
                + "                       <span style=\"font-size:12pxg\">hours</span></div>");
        plotOptions.setDataLabels(labels);
        configuration.setPlotOptions(plotOptions);

        final ListSeries series = new ListSeries("Weekly Timesheet", 15.34);
        configuration.setSeries(series);
        
        chart.drawChart(configuration);
        chart.setResponsive(true);
        Component panel = createWrapper(chart);
        return panel;
		
		
	}

	private Component buildSAAG() {
		TextArea notes = new TextArea("ATT");
		notes.setSizeFull();
		notes.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);
		Component panel = createWrapper(notes);
		return panel;
		
	}
	
	private Component createWrapper(final Component c) {
		final CssLayout slot = new CssLayout();
		slot.setWidth("100%");
		slot.addStyleName("dashboard-panel-slot");
		CssLayout card = new CssLayout();
		card.setWidth("100%");
		card.addStyleName(ValoTheme.LAYOUT_CARD);
		
		HorizontalLayout toolbar = new HorizontalLayout();
		toolbar.addStyleName("dashboard-panel-toolbar");
		toolbar.setWidth("100%");
		toolbar.setSpacing(false);
		
		Label title = new Label(c.getCaption());
		title.addStyleName(ValoTheme.LABEL_H4);
		title.addStyleName(ValoTheme.LABEL_COLORED);
		title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		c.setCaption(null);
		
		MenuBar tools = new MenuBar();
		tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
		MenuItem maximize = tools.addItem("", VaadinIcons.EXPAND, e -> {
			
		});
		
		MenuItem root = tools.addItem("", VaadinIcons.COG_O, e -> {
			
		});
		
		toolbar.addComponents(title, tools);
		toolbar.setExpandRatio(title, 1);
		toolbar.setComponentAlignment(title, Alignment.MIDDLE_LEFT);
		
		card.addComponents(toolbar, c);
		slot.addComponent(card);
		return slot;
	}

	private void builderHeader() {
		timeButton = new Button(VaadinIcons.USER_CLOCK);
		notifications = new Button(VaadinIcons.BELL);
		headerTools = new HorizontalLayout(timeButton,notifications);
		headerTools.addStyleName("toolbar");
		header = new HorizontalLayout(headerTools);
		header.setSpacing(true);
		headerTools.setSpacing(true);
		addComponent(header);
		header.addStyleName("viewheader");
		
		notifications.addClickListener(e -> {
			Notification.show("Not Implemented");
		});

		timeButton.addClickListener(e -> {
			if(!clockedIn) {
				Notification.show("Not Implemented");
			}
			else {
				Notification.show("Not Implemented");
			}
		});
		

		
	}

}
