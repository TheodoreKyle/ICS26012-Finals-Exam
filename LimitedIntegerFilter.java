import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter; 

public class LimitedIntegerFilter extends DocumentFilter {

    private final int maxLength;
    private final Runnable onInvalid;
    private final Runnable onValid;

    public LimitedIntegerFilter(int maxLength, Runnable onInvalid, Runnable onValid) {
        this.maxLength = maxLength;
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
        super.remove(fb, offset, length);
        onValid.run();
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        String incoming = (text == null) ? "" : text;
        String digits = incoming.replaceAll("[^0-9]", "");
        boolean rejected = !digits.equals(incoming);

        int room = maxLength - (fb.getDocument().getLength() - length);
        if (digits.length() > room) {
            digits = digits.substring(0, Math.max(room, 0));
            rejected = true;
        }

        super.replace(fb, offset, length, digits, attrs);

        if (rejected) {
            onInvalid.run();
        } else {
            onValid.run();
        }
    }
}