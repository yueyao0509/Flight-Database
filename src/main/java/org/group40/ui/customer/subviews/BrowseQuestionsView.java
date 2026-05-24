package org.group40.ui.customer.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.customer.CustomerView;

public class BrowseQuestionsView extends BaseView {

    private static final Color BG = new Color(200, 245, 220);

    private final DefaultTableModel model = new DefaultTableModel();
    private JTable table;

    public BrowseQuestionsView(AppContext ctx) {
        super(ctx, BG);

        this.add(topBar("Browse Q&A", "Back",
                () -> ctx.frame.showView(new CustomerView(ctx))), "North");

        this.add(buildTablePanel(), "Center");

        loadQuestions();
    }

    private JPanel  buildTablePanel() {
        model.setColumnIdentifiers(new Object[]{
            "Question ID", "Question", "Answer", "Asked At", "Answered At"
        });

        table = new JTable(model);
        table.setRowHeight(28);

        // Search bar
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchQuestions(searchField.getText().trim()));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadQuestions());

        var top = new JPanel();
        top.setOpaque(false);
        top.add(searchField);
        top.add(searchBtn);
        top.add(refreshBtn);

        var wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(top, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(table), BorderLayout.CENTER);

        return wrapper;
    }

    private void loadQuestions() {
        try {
            model.setRowCount(0);

            List<Map<String, Object>> list = ctx.qaDAO.getAllQuestions();

            for (Map<String, Object> q : list) {
                model.addRow(new Object[]{
                    q.get("questionID"),
                    q.get("questionText"),
                    q.get("answerText") == null ? "(Not answered yet)" : q.get("answerText"),
                    q.get("askedAt"),
                    q.get("answeredAt")
                });
            }

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No questions have been asked yet.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load questions: " + ex.getMessage());
        }
    }
    private void searchQuestions(String keyword) {
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a keyword to search.");
            return;
        }

        try {
            model.setRowCount(0);

            List<Map<String, Object>> list = ctx.qaDAO.searchQuestions(keyword);

            for (Map<String, Object> q : list) {
                model.addRow(new Object[]{
                    q.get("questionID"),
                    q.get("questionText"),
                    q.get("answerText") == null ? "(Not answered yet)" : q.get("answerText"),
                    q.get("askedAt"),
                    q.get("answeredAt")
                });
            }

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No results found for: " + keyword);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage());
        }
    }
}
