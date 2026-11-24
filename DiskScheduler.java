import java.util.List;

public interface DiskScheduler {
    DiskScheduleResult schedule(List<Integer> requests, int initialHead, int diskSize, String direction);
}