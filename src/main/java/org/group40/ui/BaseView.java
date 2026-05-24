package org.group40.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.group40.AppContext;

public abstract class BaseView extends JPanel {
    protected static final Font MAIN_FONT = new Font("Lucida Sans", Font.BOLD, 18);
    protected static final Font SMALL_FONT = new Font("Lucida Sans", Font.PLAIN, 12);

    protected final AppContext ctx;

    protected BaseView(AppContext ctx, Color background) {
        this.ctx = ctx;
        setLayout(new BorderLayout());
        setBackground(background);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    // builds a top bar with a title on the left and a action button on the right
    protected JPanel topBar(String titleText, String rightButton, Runnable onRight) {
        JLabel title = new JLabel(titleText);
        title.setFont(MAIN_FONT);

        JButton btn = new JButton(rightButton);
        btn.setFont(SMALL_FONT);
        btn.addActionListener(e -> onRight.run());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(btn, BorderLayout.EAST);
        return top;
    }

    protected JButton button(String label, Runnable onClick) {
        JButton b = new JButton(label);
        b.setFont(MAIN_FONT);
        b.addActionListener(e -> onClick.run());
        return b;
    }

    protected JButton smallButton(String label, Runnable onClick) {
        JButton b = new JButton(label);
        b.setFont(SMALL_FONT);
        b.addActionListener(e -> onClick.run());
        return b;
    }

    protected JPanel centered(Component child) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(child); // no constraints = centered at preferred size
        return wrapper;
    }

    protected JPanel formPanel(int maxWidth) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.putClientProperty("formMaxWidth", maxWidth);
        form.putClientProperty("formNextRow", 0);
        return form;
    }

    protected void addRow(JPanel form, String labelText, Component field) {
        int row = (int) form.getClientProperty("formNextRow");
        JLabel label = new JLabel(labelText);
        label.setFont(MAIN_FONT);
        if (field instanceof JComponent jc)
            jc.setFont(MAIN_FONT);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = row;
        form.add(label, g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        form.add(field, g);

        form.putClientProperty("formNextRow", row + 1);
        lockFormSize(form);
    }

    protected void addFullRow(JPanel form, Component comp) {
        int row = (int) form.getClientProperty("formNextRow");
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = row;
        g.gridwidth = 2;
        g.insets = new Insets(12, 6, 6, 6);
        g.anchor = GridBagConstraints.CENTER;
        form.add(comp, g);
        form.putClientProperty("formNextRow", row + 1);
        lockFormSize(form);
    }

    private void lockFormSize(JPanel form) {
        int maxWidth = (int) form.getClientProperty("formMaxWidth");
        int height = form.getLayout().preferredLayoutSize(form).height;
        Dimension size = new Dimension(maxWidth, height);
        form.setPreferredSize(size);
        form.setMaximumSize(size);
    }

    protected <T> JScrollPane scrollableList(JList<T> list) {
        list.setFont(MAIN_FONT);
        return new JScrollPane(list);
    }

    protected JPanel searchBar(String labelText, JTextField field,
            String buttonLabel, Runnable onSearch) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(MAIN_FONT);
        field.setFont(MAIN_FONT);

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        bar.setOpaque(false);
        bar.add(lbl);
        bar.add(field);
        bar.add(smallButton(buttonLabel, onSearch));
        return bar;
    }

    protected JPanel dropdownBar(String labelText, JComboBox<String> dropdown,
            String buttonLabel, Runnable onSelect) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(MAIN_FONT);
        dropdown.setFont(MAIN_FONT);

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        bar.setOpaque(false);
        bar.add(lbl);
        bar.add(dropdown);
        bar.add(smallButton(buttonLabel, onSelect));
        return bar;
    }
}