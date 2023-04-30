package ru.psu.pls.teacher.pack.view.loadgroup;

import ru.psu.pls.general.model.Task;
import ru.psu.pls.general.model.TaskGroup;
import ru.psu.pls.teacher.pack.controller.PackageManager;
import ru.psu.pls.teacher.pack.view.testbrowser.TestBrowserView;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class LoadGroupView extends JFrame {
    private JPanel rPanel;

    private JTextField txtName;
    private JTextArea txtDescription;
    private JTextField txtAuthor;
    private JLabel lblCreationDate;
    private JLabel lblUpdateDate;
    private JButton btnSave;
    private JButton btnCancel;

    private JList<Task> listTasks;
    private JButton btnAdd;
    private JButton btnDelete;
    private JButton btnOpen;

    // массив компонентов для быстрого редактирования
    private final JComponent[] activeElements = new JComponent[] { txtName, txtDescription, txtAuthor, btnSave, btnCancel, btnAdd, btnDelete, btnOpen };

    // модель отображения заданий на список
    private final DefaultListModel<Task> taskListModel = new DefaultListModel<>();

    private boolean needSave;

    private final PackageManager packageManager = new PackageManager();
    private TaskGroup currentTaskGroup;

    public LoadGroupView() {
        setupBasicUIOptions();
        enableActiveElements(false);
        makeMenu();
        setupListeners();

        listTasks.setModel(taskListModel);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        this.needSave = false;
    }

    private void setupBasicUIOptions() {
        this.setContentPane(rPanel);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("PLS: Обозреватель группы заданий");
        this.pack();
        this.setSize(600, 470);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }

    private void enableActiveElements(boolean isEnabled) {
        for (JComponent component : activeElements) {
            component.setEnabled(isEnabled);
        }
    }

    private void makeMenu() {
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        makeFileMenu(menuBar);
//        makeSettingsMenu(menuBar);
    }

    private void makeFileMenu(JMenuBar menuBar) {
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);

        JMenuItem createMI = new JMenuItem("Создать новую группу заданий");
        createMI.addActionListener(createTaskGroupAL);

        JMenu saveSubMenu = new JMenu("Сохранить...");
        JMenuItem saveChangesMI = new JMenuItem("Сохранить изменения");
        saveChangesMI.addActionListener(saveTaskGroupChangesAL);
        JMenuItem saveAsTypeMI = new JMenuItem("Сохранить изменения и упаковать");
        saveAsTypeMI.addActionListener(saveAsTypeTaskGroupAL);

        JMenuItem loadFromFileMI = new JMenuItem("Загрузить из директории группы");
        loadFromFileMI.addActionListener(loadFromFileTaskGroupAL);

        // Структура меню Файл
        fileMenu.add(createMI);
        fileMenu.add(saveSubMenu);
        saveSubMenu.add(saveChangesMI);
        saveSubMenu.add(saveAsTypeMI);
        fileMenu.add(new JToolBar.Separator());
        fileMenu.add(loadFromFileMI);
    }

    private void makeSettingsMenu(JMenuBar menuBar) {
        JMenu settingsMenu = new JMenu("Настройки");
        menuBar.add(settingsMenu);

        JMenuItem changeWorkingDir = new JMenuItem("Изменить рабочую директорию");

        settingsMenu.add(changeWorkingDir);
    }

    private void setupListeners() {
        btnSave.addActionListener(saveTaskGroupChangesAL);
        btnCancel.addActionListener(cancelTaskGroupChangesAL);

        txtName.getDocument().addDocumentListener(taskGroupTextAttributesChangedDL);
        txtAuthor.getDocument().addDocumentListener(taskGroupTextAttributesChangedDL);
        txtDescription.getDocument().addDocumentListener(taskGroupTextAttributesChangedDL);

        btnAdd.addActionListener(addTaskAL);
        btnDelete.addActionListener(deleteTaskAL);
        btnOpen.addActionListener(openTaskAttributesAL);
    }

    public boolean checkIfNeedSave(boolean onClosing) {
        if (needSave) {
            int dialog = JOptionPane.showConfirmDialog(LoadGroupView.this,
                    "Хотите сохранить изменения?",
                    "Есть несохраненные изменения",
                    (onClosing ? JOptionPane.YES_NO_OPTION : JOptionPane.YES_NO_CANCEL_OPTION),
                    JOptionPane.QUESTION_MESSAGE
            );
            if (dialog == JOptionPane.YES_OPTION) {
                saveCurrentTaskGroup();
            }
            else
                return dialog == JOptionPane.CANCEL_OPTION;
        }
        return false;
    }

    private void saveCurrentTaskGroup() {
        String newName = txtName.getText().trim();
        String newAuthor = txtAuthor.getText().trim();
        String newDesc = txtDescription.getText().trim();

        currentTaskGroup.setName(newName);
        currentTaskGroup.setAuthor(newAuthor);
        currentTaskGroup.setDescription(newDesc);

        try {
            packageManager.saveTaskGroupChanges(currentTaskGroup);
            needSave = false;
        } catch (IOException e) {
            // FIXME
            throw new RuntimeException(e);
        }
    }

    private boolean checkIfDirExists(String dirName) {
        if (dirName == null || dirName.trim().isEmpty())
            return false;

        String workingDirectory = this.packageManager.getWorkingDirectory();

        boolean exists = Files.exists(Paths.get(workingDirectory + File.separator + dirName));

        if (exists) {
            JOptionPane.showMessageDialog(LoadGroupView.this,
                    "Данное название уже занято, пожалуйста, выберите другое",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    private boolean checkIfTaskExists(String taskName) {
        if (taskName == null || taskName.trim().isEmpty())
            return false;
        boolean exists = currentTaskGroup.getTasks()
                .stream()
                .anyMatch(task -> task.getName().equals(taskName));
        if (exists) {
            JOptionPane.showMessageDialog(LoadGroupView.this,
                    "Данное название уже занято, пожалуйста, выберите другое",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    private boolean isGoodName(String name) {
        if (name == null || name.trim().isEmpty())
            return false;
        String regexPattern = "^[a-z0-9_]{1,255}$";
        Pattern pattern = Pattern.compile(regexPattern, Pattern.UNICODE_CHARACTER_CLASS);
        return pattern.matcher(name).matches();
    }

    private void showCurrentTaskGroup() {
        txtName.setText(currentTaskGroup.getName());
        txtAuthor.setText(currentTaskGroup.getAuthor());
        txtDescription.setText(currentTaskGroup.getDescription());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy [HH]:[mm]");
        lblCreationDate.setText(currentTaskGroup.getCreationDateTime().format(formatter));
        lblUpdateDate.setText(currentTaskGroup.getUpdateDateTime().format(formatter));
        taskListModel.clear();
        currentTaskGroup.getTasks().forEach(taskListModel::addElement);
    }

    private boolean isTaskGroupNull() {
        if (currentTaskGroup == null) {
            JOptionPane.showMessageDialog(LoadGroupView.this,
                    "Нечего сохранять",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    private void loseFocus() {
        lblCreationDate.requestFocusInWindow();
    }

    private final ActionListener createTaskGroupAL = e -> {
        if (checkIfNeedSave(false))
            return;

        String groupName;
        boolean goodName;
        do {
            groupName = JOptionPane.showInputDialog(rPanel,
                            "Введите название группы:",
                            "Добавить группу заданий",
                            JOptionPane.QUESTION_MESSAGE);
            if (groupName == null)
                return;

            groupName = groupName.trim();
            goodName = isGoodName(groupName);

            if ( ! goodName) {
                JOptionPane.showMessageDialog(LoadGroupView.this,
                        "Данное название недопустимо. Пожалуйста, используйте только " +
                                "строчные английские буквы, цифры и знак подчеркивания",
                        "Ошибка",
                        JOptionPane.WARNING_MESSAGE);
            }

        } while ( ! goodName || checkIfDirExists(groupName));

        try {
            this.currentTaskGroup = packageManager.createTaskGroup(groupName, "", "");
            enableActiveElements(true);
            showCurrentTaskGroup();
            needSave = false;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(rPanel,
                    "При создании директории группы произошла ошибка. " +
                            "Возможно, название директории содержит недопустимые символы",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    };

    private final ActionListener saveTaskGroupChangesAL = e -> {
        if (isTaskGroupNull()) return;
        loseFocus();
        saveCurrentTaskGroup();
        showCurrentTaskGroup();
        needSave = false;
    };

    private final ActionListener saveAsTypeTaskGroupAL = e -> {
        if (isTaskGroupNull()) return;
        if (checkIfNeedSave(false)) return;

        loseFocus();

        try {
            packageManager.saveAsType(currentTaskGroup);
            needSave = false;
        } catch (IOException ex) {
            // FIXME
            throw new RuntimeException(ex);
        }
    };

    private final ActionListener cancelTaskGroupChangesAL = e -> {
        txtName.setText(currentTaskGroup.getName());
        txtAuthor.setText(currentTaskGroup.getAuthor());
        txtDescription.setText(currentTaskGroup.getDescription());
        needSave = false;
    };

    private final ActionListener loadFromFileTaskGroupAL = e -> {
        if (checkIfNeedSave(false))
            return;

        loseFocus();

        JFileChooser chooser = new JFileChooser(packageManager.getWorkingDirectory());
        chooser.setDialogTitle("Загрузить группу заданий");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        int dialog = chooser.showOpenDialog(LoadGroupView.this);
        if (dialog == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            try {
                currentTaskGroup = packageManager.openTaskGroup(path);
                showCurrentTaskGroup();
                enableActiveElements(true);
                needSave = false;
            } catch (IOException ex) {
                // FIXME
                throw new RuntimeException(ex);
            }
        }
    };

    private final ActionListener addTaskAL = e -> {
        String taskName;
        boolean goodName;
        do {
            taskName = JOptionPane.showInputDialog(rPanel,
                    "Введите название задания:",
                    "Добавить задание",
                    JOptionPane.QUESTION_MESSAGE);
            if (taskName == null)
                return;
            goodName = isGoodName(taskName);

            if ( ! goodName) {
                JOptionPane.showMessageDialog(LoadGroupView.this,
                        "Данное название недопустимо. Пожалуйста, используйте только " +
                                "строчные английские буквы, цифры и знак подчеркивания",
                        "Ошибка",
                        JOptionPane.WARNING_MESSAGE);
            }

        } while ( ! goodName || checkIfTaskExists(taskName));

        try {
            Task task = packageManager.createTask(taskName, currentTaskGroup);
            taskListModel.addElement(task);
            needSave = false;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(rPanel,
                    "При создании файла задания произошла ошибка. " +
                            "Возможно, название задания содержит недопустимые символы",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    };

    private final ActionListener deleteTaskAL = e -> {
        Task selectedTask = listTasks.getSelectedValue();
        if (selectedTask == null)
            return;

        int dialog = JOptionPane.showConfirmDialog(LoadGroupView.this,
                "Вы уверены, что хотите удалить задание?",
                "Удалить задание",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (dialog == JOptionPane.YES_OPTION) {

            try {
                packageManager.deleteTask(selectedTask, currentTaskGroup);
                taskListModel.removeElement(selectedTask);
                needSave = false;
            } catch (IOException ex) {
                // FIXME
                throw new RuntimeException(ex);
            }
        }
    };

    private final ActionListener openTaskAttributesAL = e -> {
        Task selectedTask = listTasks.getSelectedValue();
        if (selectedTask == null)
            return;

        TestBrowserView view = new TestBrowserView(LoadGroupView.this,
                currentTaskGroup.getName(),
                selectedTask);
        view.setVisible(true);

        try {
            packageManager.saveTaskGroupChanges(currentTaskGroup);
            showCurrentTaskGroup();
            needSave = false;
        } catch (IOException ex) {
            // FIXME
            throw new RuntimeException(ex);
        }
    };

    private final DocumentListener taskGroupTextAttributesChangedDL = new DocumentListener() {
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
            needSave = true;
        }
    };
}
