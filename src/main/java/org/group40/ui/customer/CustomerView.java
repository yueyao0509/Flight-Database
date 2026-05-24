package org.group40.ui.customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.customer.subviews.AskQuestionView;
import org.group40.ui.customer.subviews.BrowseQuestionsView;
import org.group40.ui.customer.subviews.CancelTicketView;
import org.group40.ui.customer.subviews.PastReservationsView;
import org.group40.ui.customer.subviews.SearchFlightsView;
import org.group40.ui.customer.subviews.UpcomingReservationsView;

public class CustomerView extends BaseView {

    public CustomerView(AppContext ctx) {
        super(ctx, new Color(230, 200, 140));
        add(topBar("Customer Dashboard", "Logout", ctx::logout), BorderLayout.NORTH);
        add(buildButtonGrid(), BorderLayout.CENTER);
    }

    private JPanel buildButtonGrid() {
        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 10));
        grid.setOpaque(false);

        grid.add(button("Search / Buy Flights", () -> ctx.frame.showView(new SearchFlightsView(ctx))));
        grid.add(button("Upcoming Reservations", () -> ctx.frame.showView(new UpcomingReservationsView(ctx))));
        grid.add(button("Past Reservations", () -> ctx.frame.showView(new PastReservationsView(ctx))));
        grid.add(button("Cancel Ticket", () -> ctx.frame.showView(new CancelTicketView(ctx))));
        grid.add(button("Browse Q&A", () -> ctx.frame.showView(new BrowseQuestionsView(ctx))));
        grid.add(button("Ask Question", () -> ctx.frame.showView(new AskQuestionView(ctx))));
        grid.add(button("Alerts", () -> ctx.frame.showView(new AlertsView(ctx))));

        return grid;
    }
}
