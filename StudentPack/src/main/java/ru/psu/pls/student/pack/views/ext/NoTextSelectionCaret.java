package ru.psu.pls.student.pack.views.ext;

import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class NoTextSelectionCaret extends DefaultCaret {

    public NoTextSelectionCaret(JTextComponent textComponent) {
        this.setBlinkRate( textComponent.getCaret().getBlinkRate() );
        textComponent.setHighlighter(null);
    }

    @Override
    public int getMark() {
        return getDot();
    }
}
