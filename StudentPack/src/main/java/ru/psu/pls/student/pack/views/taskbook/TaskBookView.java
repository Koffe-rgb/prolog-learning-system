package ru.psu.pls.student.pack.views.taskbook;

import org.jpl7.*;
import ru.psu.pls.general.model.Task;
import ru.psu.pls.general.model.TaskGroup;
import ru.psu.pls.student.pack.model.TestLogger;
import ru.psu.pls.student.pack.model.Test;
import ru.psu.pls.student.pack.views.results.ResultsView;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TaskBookView extends JFrame {
    private JLabel lblTheme;
    private JLabel lblTaskName;
    private JLabel lblDate;
    private JTextPane txtDescription;
    private JTextPane txtInput;
    private JTextPane txtOutput;
    private JPanel rPane;
    private JButton btnOpen;
    private JButton btnStartTest;

    private final Task currentTask;
    private final TaskGroup currentTaskGroup;
    private final TestLogger testLogger = new TestLogger();

    private String solutionFilePath;
    private String taskFilePath;

    private static final String WORKING_DIRECTORY_PATH = "student_workspace";
    private static final String FAIL_SOLUTION_TEMPLATE =
            ":- module(solution, [ run/0 ]).\n\n" +
            "% get_arg(A) - получить аргумент в переменную A\n" +
            "% put_res(A) - отправить значение переменной A в результат\n\n" +
            "run :-\n" +
            "  % запишите решение задания тут\n" +
            "  fail.";

    public TaskBookView(TaskGroup taskGroup, Task task, File tempDir) throws HeadlessException {

        this.currentTaskGroup = taskGroup;
        this.currentTask = task;
        this.taskFilePath = (tempDir.getAbsolutePath() + File.separator +
                task.getName() + ".pl")
                .replace("\\", "/");

        setupJFrameOptions();
        setupUI(taskGroup.getName(), currentTask.getName());

        try {
            this.solutionFilePath = generateSolutionFile();
        } catch (IOException e) {
            // FIXME
            JOptionPane.showMessageDialog(rPane,
                    "При создании файла решения произошла ошибка.",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            //throw new RuntimeException(e);
        }
        initPrologEngine();
        startTests();
        showInOutData();

        btnStartTest.addActionListener(startTestsAL);
        btnOpen.addActionListener(openFileAL);
    }

    private void initPrologEngine() {
        // инициализация Prolog Engine
        Query workingDir = new Query(new Compound("working_directory",
                new Term[] {
                        new Variable("_"),
                        new Atom(WORKING_DIRECTORY_PATH, "string")
                }
        ));
        boolean workingDirStatus = workingDir.hasSolution();
        System.out.println("New working directory set : " + workingDirStatus);

        // начало консультации, загрузка стартовой точки программы
        Query consult = new Query("consult", new Atom("loader"));
        boolean consultStatus = consult.hasSolution();
        System.out.println("Consultation is possible : " + consultStatus);

        // передача ссылки на контроллер заданий в Prolog Engine
        JRef jref = new JRef(testLogger);
        Query passRef = new Query("pls_pass_java_obj_ref", jref );
        boolean passRefStatus = passRef.hasSolution();
        System.out.println("Object passed : " + passRefStatus);

        // загрузка контекста
        Query loadContext = new Query(new Compound("load_context",
                new Term[] {
                        new Atom(taskFilePath),
                        new Atom(solutionFilePath)
                }
        ));
        System.out.println("Context is loaded: " + loadContext.hasSolution());
    }

    private void startTests() {
        testLogger.clear();
        Query startTests = new Query("make, start_tests");
        System.out.println("Tests started: " + startTests.hasSolution());
    }

    private String generateSolutionFile() throws IOException {
        // создание папки для решения, если не существует
        String dirPath = WORKING_DIRECTORY_PATH + File.separator +
                currentTaskGroup.getName() + File.separator +
                currentTask.getName();
        File dir = Files.createDirectories(Paths.get(dirPath)).toFile();

        // создание файла решения, если не существует
        File solutionFile = new File(dir.getAbsolutePath(), "solution.pl");
        if ( ! Files.exists(solutionFile.toPath())) {
            Files.createFile(solutionFile.toPath());
            // запись в файл шаблона решения
            try (FileWriter fw = new FileWriter(solutionFile)) {
                fw.write(FAIL_SOLUTION_TEMPLATE);
            }
        }
        return solutionFile.getAbsolutePath().replace("\\", "/");
    }

    private void showInOutData() {
        Test test = testLogger.results().get(0);
        List<String> realInputs = test.getInfoArguments();
        List<String> expectedResults = test.getExpectedResults();

        txtInput.setText("Нет исходных данных");
        txtOutput.setText("Нет верного решения");

        StringBuilder inputDescription = new StringBuilder("Пример исходных данных:\n");
        for (int i = 0; i < realInputs.size(); ++i) {
            inputDescription.append(i + 1).append(") ").append(realInputs.get(i)).append("\n");
        }
        if ( ! realInputs.isEmpty())
            txtInput.setText(inputDescription.toString());

        StringBuilder outputDescription = new StringBuilder("Пример верного решения:\n");
        for (int i = 0; i < expectedResults.size(); ++i) {
            outputDescription.append(i + 1).append(") ").append(expectedResults.get(i)).append("\n");
        }
        if ( ! expectedResults.isEmpty())
            txtOutput.setText(outputDescription.toString());
    }

    private void setupUI(String theme, String taskName) {
        SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.setAlignment(sas, StyleConstants.ALIGN_JUSTIFIED);
        List<JTextPane> textComponents = Arrays.asList(txtDescription, txtInput, txtOutput);
        textComponents.forEach(textComponent -> {
//            textComponent.setCaret( new NoTextSelectionCaret( textComponent ) );
            textComponent.setEditable(false);
            textComponent.setText("");
            textComponent.setParagraphAttributes(sas, false);
        });

        this.lblTheme.setText(theme.toUpperCase(Locale.ROOT));
        this.lblTaskName.setText(taskName);
        this.txtDescription.setText(currentTask.getDescription());

        String dateTimeFormat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM HH:mm"));
        this.lblDate.setText(String.format("Дата, время: %s", dateTimeFormat));
    }

    private void setupJFrameOptions() {
        this.setContentPane(rPane);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("PLS: Выполнение задания");
        this.pack();
        this.setSize(800, 600);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }

    private final ActionListener startTestsAL = e -> {
        startTests();
        List<Test> results = testLogger.results();
        ResultsView resultsView = new ResultsView(this, results);
        resultsView.setVisible(true);
    };

    private final ActionListener openFileAL = e -> {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.edit(new File(solutionFilePath));
        } catch (IOException ex) {
            // FIXME
            throw new RuntimeException(ex);
        }
    };
}
