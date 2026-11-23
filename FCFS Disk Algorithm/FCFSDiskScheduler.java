/**
 * ICS26012 - Operating Systems
 * Module: First-Come, First-Served (FCFS) Disk Scheduling Algorithm
 * Owner: Wyn Icezel A. Vega
 */
import java.util.ArrayList;
import java.util.List;

public class FCFSDiskScheduler implements DiskScheduler {

    @Override
    public DiskScheduleResult schedule(List<Integer> requests, int initialHead, int diskSize, String direction) {
        if (diskSize <= 0) {
            throw new IllegalArgumentException("Disk size (N) must be greater than 0.");
        }
        if (initialHead < 0 || initialHead >= diskSize) {
            throw new IllegalArgumentException("Initial head position must be between 0 and " + (diskSize - 1) + ".");
        }
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Request queue cannot be empty.");
        }

        List<Integer> seekSequence = new ArrayList<>();
        List<Integer> stepMovements = new ArrayList<>();

        int currentPosition = initialHead;
        seekSequence.add(currentPosition);

        int totalHeadMovement = 0;

        for (int targetCylinder : requests) {
            if (targetCylinder < 0 || targetCylinder >= diskSize) {
                throw new IllegalArgumentException("Request cylinder " + targetCylinder +
                        " is out of bounds (0 to " + (diskSize - 1) + ").");
            }

            int distance = Math.abs(targetCylinder - currentPosition);
            totalHeadMovement += distance;

            stepMovements.add(distance);
            seekSequence.add(targetCylinder);

            currentPosition = targetCylinder;
        }

        int sumOfStepMovements = 0;
        for (int move : stepMovements) {
            sumOfStepMovements += move;
        }
        double averageSeekDistance = (double) sumOfStepMovements / stepMovements.size();

        return new DiskScheduleResult(
                "First-Come, First-Served (FCFS)",
                initialHead,
                diskSize,
                seekSequence,
                stepMovements,
                totalHeadMovement,
                averageSeekDistance
        );
    }
}