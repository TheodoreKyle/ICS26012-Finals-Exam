import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GanttChartPanel extends JPanel {
    private List<GanttSegment> segments = new ArrayList<>();

    public void setSegments(List<GanttSegment> segments) {
        this.segments = segments;
        
        if (segments == null || segments.isEmpty()) return;

        int calculatedWidth = 0;
        
        for (GanttSegment seg : segments) {
            int duration = seg.endTime - seg.startTime;
            if (duration > 0) {
                int effectiveDuration = Math.min(duration, 5); 
                calculatedWidth += effectiveDuration * 45;
            }
        }
        
        int finalWidth = Math.max(800, calculatedWidth + 60);
        
        setPreferredSize(new Dimension(finalWidth, 100));
        
        revalidate(); 
        repaint();
    }

    public void clear() {
        this.segments.clear();
        setPreferredSize(new Dimension(800, 100)); 
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (segments == null || segments.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int paddingX = 30;
        int startY = 15;
        int barHeight = 45;

        FontMetrics fm = g2.getFontMetrics();
        
        int currentX = paddingX; 

        for (int i = 0; i < segments.size(); i++) {
            GanttSegment seg = segments.get(i);
            int duration = seg.endTime - seg.startTime;
            
            if (duration <= 0) {
                continue;
            }
            
            int effectiveDuration = Math.min(duration, 5);
            int width = effectiveDuration * 45;

            if (seg.processId.equals("Idle")) {
                g2.setColor(Color.WHITE);
            } else {
                g2.setColor(new Color(235, 235, 235)); 
            }
            
            g2.fillRect(currentX, startY, width, barHeight);
            g2.setColor(Color.BLACK);
            g2.drawRect(currentX, startY, width, barHeight);

            if (!seg.processId.equals("Idle")) {
                g2.setFont(new Font("Arial", Font.PLAIN, 16));
                g2.setColor(Color.BLACK);
                int textWidth = fm.stringWidth(seg.processId);
                int textX = currentX + (width - textWidth) / 2;
                int textY = startY + (barHeight - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(seg.processId, textX, textY);
            }

            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            String startTimeStr = String.valueOf(seg.startTime);
            g2.drawString(startTimeStr, currentX - fm.stringWidth(startTimeStr) / 2, startY + barHeight + 20);

            if (i == segments.size() - 1) {
                String endTimeStr = String.valueOf(seg.endTime);
                g2.drawString(endTimeStr, currentX + width - fm.stringWidth(endTimeStr) / 2, startY + barHeight + 20);
            }
            
            currentX += width; 
        }
    }
}