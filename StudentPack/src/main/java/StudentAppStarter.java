import com.formdev.flatlaf.FlatLightLaf;
import ru.psu.pls.student.pack.views.loader.LoaderView;

public class StudentAppStarter {
    public static void main(String[] args) {
        FlatLightLaf.setup();

        LoaderView loadTaskView = new LoaderView();
        loadTaskView.setVisible(true);
    }
}
