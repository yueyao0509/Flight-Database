package org.group40.ui.rep.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.rep.RepView;

public class AnswerQuestionsView extends BaseView {
    private static final Color BG = new Color(250, 230, 180);

    private final DefaultTableModel model = new DefaultTableModel() {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private JTable table;
    private final JTextArea answerArea = new JTextArea(4, 40);

    public AnswerQuestionsView(AppContext ctx) {
        super(ctx, BG);
        add(topBar("Answer Questions", "Back",
                () -> ctx.frame.showView(new RepView(ctx))), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildAnswerPanel(), BorderLayout.SOUTH);
        loadQuestions();
    }

    private JPanel buildCenter() {
        model.setColumnIdentifiers(new Object[] {
                "Question ID", "Customer ID", "Question", "Asked At"
        });
        table = new JTable(model);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
    
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(new JScrollPane(table), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildAnswerPanel() {
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);

        JButton submitBtn = new JButton("Submit Answer");
        submitBtn.setFont(SMALL_FONT);
        submitBtn.addActionListener(e -> submitAnswer());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(SMALL_FONT);
        refreshBtn.addActionListener(e -> loadQuestions());

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(submitBtn);
        buttons.add(refreshBtn);

        JPanel south = new JPanel(new BorderLayout(6, 6));
        south.setOpaque(false);
        south.add(new JScrollPane(answerArea), BorderLayout.CENTER);
        south.add(buttons, BorderLayout.EAST);
        return south;
    }

    private void loadQuestions() {
        try {
            model.setRowCount(0);
            List<Map<String, Object>> list = ctx.qaDAO.getUnansweredQuestions();
            for (Map<String, Object> q : list) {
                model.addRow(new Object[] {
                        q.get("questionID"),
                        q.get("userID"),
                        q.get("questionText"),
                        q.get("askedAt")
                });
            }
            if (list.isEmpty())
                JOptionPane.showMessageDialog(this, "No unanswered questions.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load questions: " + ex.getMessage());
        }
    }

    private void submitAnswer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a question first.");
            return;
        }
        String answer = answerArea.getText().trim();
        if (answer.isBlank()) {
            JOptionPane.showMessageDialog(this, "Answer cannot be empty.");
            return;
        }
        String questionID = model.getValueAt(row, 0).toString();
        try {
            ctx.qaDAO.answerQuestion(questionID, answer);
            answerArea.setText("");
            loadQuestions();
            JOptionPane.showMessageDialog(this, "Answer submitted.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to submit answer: " + ex.getMessage());
        }
    }
}
