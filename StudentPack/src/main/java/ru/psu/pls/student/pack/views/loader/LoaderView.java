package ru.psu.pls.student.pack.views.loader;

import org.jpl7.JPLException;
import ru.psu.pls.general.model.Task;
import ru.psu.pls.general.model.TaskGroup;
import ru.psu.pls.student.pack.model.TypeConverter;
import ru.psu.pls.student.pack.views.ext.NoTextSelectionCaret;
import ru.psu.pls.student.pack.views.taskbook.TaskBookView;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.format.DateTimeFormatter;

public class LoaderView extends JFrame {
    private JPanel rPane;
    JButton btnLoad;
    JButton btnBrowse;
    JTextField txtGroupName;
    JComboBox<Task> cmbTask;
    private JTextArea txtDescription;
    private JLabel lbAuthor;
    private JLabel lbDateCreationUpdate;

    private final DefaultComboBoxModel<Task> comboBoxModel = new DefaultComboBoxModel<>();

    private final TypeConverter typeConverter = new TypeConverter();
    private TaskGroup currentTaskGroup;
    private File tempDirectoryFile;

    public LoaderView() throws HeadlessException {
        setupJFrameOptions();
        setupUI();

        btnBrowse.addActionListener(browseAL);
        btnLoad.addActionListener(loadAL);
    }

    private void setupJFrameOptions() {
        this.setContentPane(rPane);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("PLS: Выбор группы заданий");
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }

    private void setupUI() {
        txtGroupName.setEditable(false);
        txtGroupName.setCaret(new NoTextSelectionCaret(txtGroupName));
        txtGroupName.setText("");

        btnLoad.setEnabled(false);
        cmbTask.setModel(comboBoxModel);
        cmbTask.setEnabled(false);

        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setEditable(false);
        txtDescription.setCaret(new NoTextSelectionCaret(txtDescription));
        txtDescription.setText("");
    }

    private static void recursiveDeleteOnExit(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                file.toFile().deleteOnExit();
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                dir.toFile().deleteOnExit();
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private final ActionListener browseAL = e -> {
        JFileChooser chooser = new JFileChooser("./");
        chooser.setDialogTitle("Загрузить группу заданий");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter("Группа заданий PLS", "tg"));

        int dialogResult = chooser.showOpenDialog(LoaderView.this);
        if (dialogResult != JFileChooser.APPROVE_OPTION)
            return;

        // получаем путь до группы заданий
        String taskGroupPath = chooser.getSelectedFile()
                .getAbsolutePath()
                .replace("\\", "/");

        try {
            // создаем временную папку, в которую распакуем группу заданий
            tempDirectoryFile = Files.createTempDirectory("ppllss").toFile();

            // распаковка группы заданий и регистрация всех файлов в папке на удаление после закрытие программы
            currentTaskGroup = typeConverter.decompress(taskGroupPath, tempDirectoryFile.getAbsolutePath());
            recursiveDeleteOnExit(tempDirectoryFile.toPath());

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(rPane,
                    "При открытии файла группы заданий произошла ошибка.",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // работа с элементами управления
        txtGroupName.setText(currentTaskGroup.getName());
        txtDescription.setText(currentTaskGroup.getDescription());
        lbAuthor.setText("Автор: " + currentTaskGroup.getAuthor());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        lbDateCreationUpdate.setText(currentTaskGroup.getCreationDateTime().format(dtf) +
                " / " + currentTaskGroup.getUpdateDateTime().format(dtf));

        currentTaskGroup.getTasks().forEach(comboBoxModel::addElement);
        if ( ! currentTaskGroup.getTasks().isEmpty()) {
            btnLoad.setEnabled(true);
            cmbTask.setEnabled(true);
        }
        this.pack();
    };

    private final ActionListener loadAL = e -> {
        try {
            Task task = (Task) comboBoxModel.getSelectedItem();
            TaskBookView taskBookView = new TaskBookView(currentTaskGroup, task, tempDirectoryFile);
            taskBookView.setVisible(true);
        }
        catch (JPLException ex) {
            JOptionPane.showMessageDialog(rPane,
                    "При открытии файла задания произошла ошибка.",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        this.dispose();
    };
}
