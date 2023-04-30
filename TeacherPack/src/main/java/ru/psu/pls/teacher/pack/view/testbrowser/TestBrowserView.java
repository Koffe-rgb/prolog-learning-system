package ru.psu.pls.teacher.pack.view.testbrowser;

import ru.psu.pls.general.model.Task;
import ru.psu.pls.teacher.pack.controller.StubCodeGenerator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class TestBrowserView extends JDialog {
    private JPanel rPanel;
    private JTextArea txtDefinition;
    private JButton btnGenerateStub;
    private JTextField txtTestName;
    private JList<String> listTests;
    private JButton btnAdd;
    private JButton btnDelete;
    private JLabel lblStatus;

    private final DefaultListModel<String> testListModel = new DefaultListModel<>();

    private Task task;
    private StubCodeGenerator codeGenerator;

    public TestBrowserView(Frame owner, String title, Task task) {
        super(owner);
        setupBasicUI(owner, title);

        listTests.setModel(testListModel);

        this.codeGenerator = new StubCodeGenerator(task);

        this.task = task;
        testListModel.clear();
        task.getTestNames().forEach(testListModel::addElement);

        this.txtDefinition.setText(task.getDescription());
        txtDefinition.setWrapStyleWord(true);
        txtDefinition.setLineWrap(true);
        txtDefinition.getDocument().addDocumentListener(changeDescriptionDL);

        setupListeners();
    }

    private void setupBasicUI(Frame owner, String title) {
        this.setContentPane(rPanel);
        this.setTitle(title);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setSize(600, 400);
        this.setModal(true);
        this.setLocationRelativeTo(owner);
    }

    private void setupListeners() {
        btnAdd.addActionListener(addTestNameAL);
        btnDelete.addActionListener(deleteTestNameAL);
        btnGenerateStub.addActionListener(generateStubCodeAL);
    }

    private final ActionListener addTestNameAL = e -> {
        lblStatus.setText("");

        String testNameToAdd = txtTestName.getText().trim();
        if (testNameToAdd.isEmpty()) return;

        boolean exist = task.getTestNames().stream().anyMatch(name -> name.equals(testNameToAdd));
        if (exist) {
            JOptionPane.showMessageDialog(TestBrowserView.this,
                    "Данное название уже занято, пожалуйста, выберите другое",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            txtTestName.selectAll();
            txtTestName.requestFocusInWindow();
            return;
        }

        String regexPattern = "^[a-z0-9_]{1,20}$";
        boolean matches = Pattern.compile(regexPattern)
                .matcher(testNameToAdd)
                .matches();
        if ( ! matches) {
            JOptionPane.showMessageDialog(TestBrowserView.this,
                    "Данное название недопустимо. Пожалуйста, используйте только " +
                            "строчные английские буквы, цифры и знак подчеркивания",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            txtTestName.selectAll();
            txtTestName.requestFocusInWindow();
            return;
        }

        task.getTestNames().add(testNameToAdd);
        testListModel.addElement(testNameToAdd);
        txtTestName.setText("");
        txtTestName.requestFocusInWindow();
    };

    private final ActionListener deleteTestNameAL = e -> {
        lblStatus.setText("");

        String selectedTestName = listTests.getSelectedValue();
        int idx = listTests.getSelectedIndex();
        if (selectedTestName == null) return;

        task.getTestNames().remove(selectedTestName);
        testListModel.removeElement(selectedTestName);
        listTests.setSelectedIndex(idx-1);
    };

    private final ActionListener generateStubCodeAL = e -> {
        lblStatus.setForeground(Color.green.darker());
        lblStatus.setText("Скопировано в буфер обмена!");

        task.setDescription(txtDefinition.getText().trim());

        String stubCode = codeGenerator.generateStubCode();

        StringSelection stringSelection = new StringSelection(stubCode);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    };

    private final DocumentListener changeDescriptionDL = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            task.setDescription(txtDefinition.getText().trim());
        }
    };
}
