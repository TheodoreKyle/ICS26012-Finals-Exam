public class GanttSegment {
    String processId;
    int startTime;
    int endTime;

    public GanttSegment(String processId, int startTime, int endTime) {
        this.processId = processId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}