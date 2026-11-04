import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class ProcessIdFilter extends DocumentFilter {

    private static final String PREFIX = "P";

    @Override
    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
            throws BadLocationException {
        replace(fb, offset, 0, text, attr);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length)
            throws BadLocationException {
        replace(fb, offset, length, "", null);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        Document doc = fb.getDocument();
        String current = doc.getText(0, doc.getLength());

        String newText = current.substring(0, offset)
                + (text == null ? "" : text)
                + current.substring(offset + length);

        // Keep digits only, then always put the "P" back in front
        String digitsOnly = newText.replaceAll("[^0-9]", "");
        fb.replace(0, doc.getLength(), PREFIX + digitsOnly, attrs);
    }
}