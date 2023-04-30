package ru.psu.pls.student.pack.views.results;

import ru.psu.pls.student.pack.model.Test;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.List;

public class ResultsView extends JDialog {
    private JPanel rPane;
    private JList<Test> lstTests;
    private JTextPane tpLog;
    private JButton btnClose;

    public ResultsView(Frame owner, List<Test> testData) {
        super(owner);
        initUI(owner);
        setupJLists(testData);
        btnClose.addActionListener(e -> dispose());
    }

    private void setupJLists(List<Test> testData) {
        DefaultListModel<Test> testDefaultListModel = new DefaultListModel<>();
        testData.forEach(testDefaultListModel::addElement);

        this.lstTests.setModel(testDefaultListModel);
        this.lstTests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.lstTests.addListSelectionListener(new TestsListSelectionChangedListener(this));
        this.tpLog.setEditable(false);
    }

    private void initUI(Frame owner) {
        this.setContentPane(rPane);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("PLS: Результаты тестов");
        this.pack();
        this.setSize(480, 360);
        this.setResizable(false);
        this.setModal(true);
        this.setLocationRelativeTo(owner);
    }

    private static class TestsListSelectionChangedListener implements ListSelectionListener {
        private final ResultsView view;

        public TestsListSelectionChangedListener(ResultsView view) {
            this.view = view;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            Test selectedTest = view.lstTests.getSelectedValue();
            String status = (selectedTest.isComplete())
                    ? "успешно"
                    : "ошибка";

            view.tpLog.setText("");
            insertText("=== [ ИСХОДНЫЕ ДАННЫЕ ] ===", selectedTest.getInfoArguments());
            insertText("=== [ ОЖИДАЕМЫЕ РЕЗУЛЬТАТЫ ] ===", selectedTest.getExpectedResults());
            insertText("=== [ ПОЛУЧЕННЫЕ РЕЗУЛЬТАТЫ ] ===", selectedTest.getRealResults());
        }

        private void insertText(String header, List<String> data) {
            StyledDocument document = view.tpLog.getStyledDocument();

            SimpleAttributeSet simpleText = new SimpleAttributeSet();

            SimpleAttributeSet headerStyle = new SimpleAttributeSet();
            StyleConstants.setAlignment(headerStyle, StyleConstants.ALIGN_CENTER);

            SimpleAttributeSet justifyStyle = new SimpleAttributeSet();
            StyleConstants.setAlignment(justifyStyle, StyleConstants.ALIGN_JUSTIFIED);

            try {
                document.setParagraphAttributes(document.getLength(), document.getLength(), headerStyle, false);
                document.insertString(document.getLength(), header + "\n", simpleText);

                document.setParagraphAttributes(document.getLength(), document.getLength(), justifyStyle, false);
                for (int i = 0; i < data.size(); ++i) {
                    String str = String.format("%d) %s; %n", i+1, data.get(i));
                    document.insertString(document.getLength(), str, simpleText);
                }
                document.insertString(document.getLength(), "\n", simpleText);

            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
