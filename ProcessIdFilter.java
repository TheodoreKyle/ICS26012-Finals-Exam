import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class ProcessIdFilter extends DocumentFilter {

    private static final String PREFIX = "P";

    private final int maxDigits;
    private final Runnable onInvalid;
    private final Runnable onValid;

    public ProcessIdFilter(int maxDigits, Runnable onInvalid, Runnable onValid) {
        this.maxDigits = maxDigits;
        this.onInvalid = onInvalid;
        this.onValid = onValid;
    }

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

        String incoming = (text == null) ? "" : text.replace(PREFIX, "");
        String incomingDigits = digitsOf(incoming);
        boolean rejected = incomingDigits.length() != incoming.length();

        String before = digitsOf(current.substring(0, offset));
        String after = digitsOf(current.substring(offset + length));

        int room = maxDigits - before.length() - after.length();
        if (incomingDigits.length() > room) {
            incomingDigits = incomingDigits.substring(0, Math.max(room, 0));
            rejected = true;
        }

        fb.replace(0, doc.getLength(), PREFIX + before + incomingDigits + after, attrs);

        if (rejected) {
            onInvalid.run();
        } else {
            onValid.run();
        }
    }

    private String digitsOf(String text) {
        return text.replaceAll("[^0-9]", "");
    }
}