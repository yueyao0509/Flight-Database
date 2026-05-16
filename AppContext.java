package org.group40;

import java.sql.Connection;

import org.group40.db.AlertDAO;
import org.group40.db.CatalogDAO;
import org.group40.db.FlightSearchDAO;
import org.group40.db.QADAO;
import org.group40.db.ReservationDAO;
import org.group40.db.TicketDAO;
import org.group40.db.UserDAO;
import org.group40.db.WaitlistDAO;
import org.group40.ui.LoginView;

public class AppContext {
    public final ProjectFrame frame;

    public final UserDAO userDAO;
    public final ReservationDAO reservationDAO;
    public final WaitlistDAO waitlistDAO;
    public final FlightSearchDAO flightSearchDAO;
    public final TicketDAO ticketDAO;
    public final QADAO qaDAO;
    public AlertDAO alertDAO;
    public final CatalogDAO catalogDAO;

    public boolean loggedIn = false;
    public String currentUser = "";
    public String currentRole = "";

    public AppContext(ProjectFrame frame, Connection con) {
        this.frame = frame;
        this.userDAO = new UserDAO(con);
        this.reservationDAO = new ReservationDAO(con);
        this.waitlistDAO = new WaitlistDAO(con);
        this.flightSearchDAO = new FlightSearchDAO(con);
        this.ticketDAO = new TicketDAO(con);
        this.qaDAO = new QADAO(con);
        this.alertDAO = new AlertDAO(con);
        this.catalogDAO = new CatalogDAO(con);

    }

    public void logout() {
        loggedIn = false;
        currentUser = "";
        currentRole = "";
        frame.showView(new LoginView(this));
    }
}