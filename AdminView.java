package org.group40.ui.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.admin.subviews.FlightsByAirportView;
import org.group40.ui.admin.subviews.ManageUsersView;
import org.group40.ui.admin.subviews.MonthlySalesReportView;
import org.group40.ui.admin.subviews.MostActiveFlightsView;
import org.group40.ui.admin.subviews.ReservationsByCustomerView;
import org.group40.ui.admin.subviews.ReservationsByFlightView;
import org.group40.ui.admin.subviews.RevenueSummaryView;
import org.group40.ui.admin.subviews.TopCustomerView;

public class AdminView extends BaseView {

        public AdminView(AppContext ctx) {
                super(ctx, new Color(140, 180, 230));
                add(topBar("Admin Dashboard", "Logout", ctx::logout), BorderLayout.NORTH);
                add(buildButtonGrid(), BorderLayout.CENTER);
        }

        private JPanel buildButtonGrid() {
                JPanel grid = new JPanel(new GridLayout(4, 2, 10, 10));
                grid.setOpaque(false);

                grid.add(button("Manage Users",
                                () -> ctx.frame.showView(new ManageUsersView(ctx))));
                grid.add(button("Monthly Sales Report",
                                () -> ctx.frame.showView(new MonthlySalesReportView(ctx))));
                grid.add(button("Reservations by Flight",
                                () -> ctx.frame.showView(new ReservationsByFlightView(ctx))));
                grid.add(button("Reservations by Customer",
                                () -> ctx.frame.showView(new ReservationsByCustomerView(ctx))));
                grid.add(button("Revenue Summary",
                                () -> ctx.frame.showView(new RevenueSummaryView(ctx))));
                grid.add(button("Top Customer",
                                () -> ctx.frame.showView(new TopCustomerView(ctx))));
                grid.add(button("Most Active Flights",
                                () -> ctx.frame.showView(new MostActiveFlightsView(ctx))));
                grid.add(button("Flights by Airport",
                                () -> ctx.frame.showView(new FlightsByAirportView(ctx))));
                return grid;
        }
}
