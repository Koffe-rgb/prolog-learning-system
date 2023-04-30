import com.formdev.flatlaf.FlatLightLaf;
import org.jpl7.Atom;
import org.jpl7.JRef;
import org.jpl7.Query;
import org.jpl7.Term;
import ru.psu.pls.student.pack.model.TestLogger;
import ru.psu.pls.student.pack.views.results.ResultsView;

import javax.swing.*;

public class QueriesTestApp {
    public static void main(String[] args) {

        // инициализация
        // новая рабочая папка
        Query wd = new Query("working_directory(_, './student_workspace').");
        System.out.println("New working directory set : " + wd.hasSolution());

        // начало консультации
        Query consult = new Query( "consult", new Term[] { new Atom("loader.pl") });
        System.out.println("Consult is possible : " + consult.hasSolution());

        TestLogger testLogger = new TestLogger();
        JRef jref = new JRef(testLogger);

        Query passRef = new Query("pls_pass_java_obj_ref", new Term[] { jref });
        System.out.println("Object passed : " + passRef.hasSolution());

        Query loadContext = new Query("load_context(" +
                "'C:/Users/Nickolay/Desktop/PLS/PrologLearningSystem/student_workspace/task/task.pl'," +
                "'C:/Users/Nickolay/Desktop/PLS/PrologLearningSystem/student_workspace/solution/solution.pl')");
        loadContext.hasSolution();

        for (int i = 0; i < 5; i++) {
            Query runTests = new Query("make, start_tests");
            runTests.hasSolution();
        }

        // представить результаты
        FlatLightLaf.setup();
        JDialog view = new ResultsView(null, testLogger.results());
        view.setVisible(true);

        System.out.println("----------------------------------------");
        testLogger.print();
    }
}
