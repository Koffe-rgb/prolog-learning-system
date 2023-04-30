package ru.psu.pls.teacher.pack;

import com.formdev.flatlaf.FlatLightLaf;
import ru.psu.pls.teacher.pack.view.loadgroup.LoadGroupView;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TeacherAppStarter {
    public static void main(String[] args) {
        FlatLightLaf.setup();

        LoadGroupView view = new LoadGroupView();
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                view.checkIfNeedSave(true);
            }
        });
        view.setVisible(true);
    }
}
