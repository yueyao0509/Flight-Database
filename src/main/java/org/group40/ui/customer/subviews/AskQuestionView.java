package org.group40.ui.customer.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.group40.AppContext;
import org.group40.ui.BaseView;
import org.group40.ui.customer.CustomerView;

public class AskQuestionView extends BaseView {

    private static final Color BG = new Color(200, 245, 220);

    private final JTextArea questionArea = new JTextArea(8, 40);

    public AskQuestionView(AppContext ctx) {
        super(ctx, BG);

        this.add(topBar("Ask a Question", "Back",
                () -> ctx.frame.showView(new CustomerView(ctx))), "North");

        this.add(buildForm(), "Center");
    }

    private JPanel buildForm() {
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setFont(questionArea.getFont().deriveFont(14f));

        JScrollPane scroll = new JScrollPane(questionArea);
        scroll.setPreferredSize(new Dimension(500, 200));

        JButton submitBtn = new JButton("Submit Question");
        submitBtn.addActionListener(e -> submitQuestion());

        var wrapper = new javax.swing.JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scroll, BorderLayout.CENTER);
        wrapper.add(submitBtn, BorderLayout.SOUTH);

        return wrapper;
    }

    private void submitQuestion() {
        String text = questionArea.getText().trim();

        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a question before submitting.");
            return;
        }

        try {
            String userID = ctx.currentUser;
            ctx.qaDAO.askQuestion(userID, text);

            JOptionPane.showMessageDialog(this, "Your question has been submitted!");

            questionArea.setText("");
            ctx.frame.showView(new CustomerView(ctx));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to submit question: " + ex.getMessage());
        }
    }
}
