package org.group40.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.group40.AppContext;

public class HomeView extends BaseView {

    public HomeView(AppContext ctx) {
        super(ctx, new Color(230, 140, 140));

        JLabel welcome = new JLabel("Online Travel Reservation System", SwingConstants.CENTER);
        welcome.setFont(new Font("Lucida Sans", Font.BOLD, 24));
        add(centered(welcome), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttons.setOpaque(false);
        buttons.add(button("Register", () -> ctx.frame.showView(new RegisterView(ctx))));
        buttons.add(button("Login", () -> ctx.frame.showView(new LoginView(ctx))));
        add(buttons, BorderLayout.SOUTH);
    }
}