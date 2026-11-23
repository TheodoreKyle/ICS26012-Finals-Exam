import java.util.List;

public class DiskScheduleResult {
    private final String algorithmName;
    private final int initialHead;
    private final int diskSize;
    private final List<Integer> seekSequence;
    private final List<Integer> stepMovements;
    private final int totalHeadMovement;
    private final double averageSeekDistance;

    public DiskScheduleResult(String algorithmName, int initialHead, int diskSize,
                              List<Integer> seekSequence, List<Integer> stepMovements,
                              int totalHeadMovement, double averageSeekDistance) {
        this.algorithmName = algorithmName;
        this.initialHead = initialHead;
        this.diskSize = diskSize;
        this.seekSequence = seekSequence;
        this.stepMovements = stepMovements;
        this.totalHeadMovement = totalHeadMovement;
        this.averageSeekDistance = averageSeekDistance;
    }

    public String getAlgorithmName() { return algorithmName; }
    public int getInitialHead() { return initialHead; }
    public int getDiskSize() { return diskSize; }
    public List<Integer> getSeekSequence() { return seekSequence; }
    public List<Integer> getStepMovements() { return stepMovements; }
    public int getTotalHeadMovement() { return totalHeadMovement; }
    public double getAverageSeekDistance() { return averageSeekDistance; }
}