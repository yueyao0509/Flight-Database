package org.group40.ui.rep;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.rep.subviews.AnswerQuestionsView;
import org.group40.ui.rep.subviews.EditReservationView;
import org.group40.ui.rep.subviews.MakeReservationView;
import org.group40.ui.rep.subviews.ManageAircraftView;
import org.group40.ui.rep.subviews.ManageAirportsView;
import org.group40.ui.rep.subviews.ManageFlightsView;
import org.group40.ui.rep.subviews.WaitlistView;
import org.group40.ui.admin.subviews.FlightsByAirportView;


public class RepView extends BaseView {

        public RepView(AppContext ctx) {
                super(ctx, new Color(230, 200, 140));
                add(topBar("Representative Dashboard", "Logout", ctx::logout), BorderLayout.NORTH);
                add(buildButtonGrid(), BorderLayout.CENTER);
        }

        private JPanel buildButtonGrid() {
                JPanel grid = new JPanel(new GridLayout(4, 2, 10, 10));
                grid.setOpaque(false);

                grid.add(button("Make Reservation",
                                () -> ctx.frame.showView(new MakeReservationView(ctx))));
                grid.add(button("Edit Reservation",
                                () -> ctx.frame.showView(new EditReservationView(ctx))));
                grid.add(button("Manage Flights",
                                () -> ctx.frame.showView(new ManageFlightsView(ctx))));
                grid.add(button("Manage Airports",
                                () -> ctx.frame.showView(new ManageAirportsView(ctx))));
                grid.add(button("Manage Aircrafts",
                                () -> ctx.frame.showView(new ManageAircraftView(ctx))));
                grid.add(button("View Waitlist",
                                () -> ctx.frame.showView(new WaitlistView(ctx))));
                grid.add(button("Answer Questions",
                                () -> ctx.frame.showView(new AnswerQuestionsView(ctx))));
                grid.add(button("Flights by Airport",
                                () -> ctx.frame.showView(new FlightsByAirportView(ctx))));
                return grid;
        }
}
