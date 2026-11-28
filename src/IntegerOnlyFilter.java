import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class IntegerOnlyFilter extends DocumentFilter { 
    @Override
    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
            throws BadLocationException {
        if (text == null) {
            return;
        }
        super.insertString(fb, offset, keepDigitsOnly(text), attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (text == null) {
            super.replace(fb, offset, length, null, attrs);
            return;
        }
        super.replace(fb, offset, length, keepDigitsOnly(text), attrs);
    }

    private String keepDigitsOnly(String text) {
        return text.replaceAll("[^0-9]", "");
    }
}