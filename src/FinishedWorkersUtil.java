import java.util.HashSet;
import java.util.Set;

public class FinishedWorkersUtil {

    private static final Set<Worker> finishedWorkers = new HashSet<>();

    public static synchronized Set<Worker> getAndClear() {
        Set<Worker> workers = new HashSet<>(finishedWorkers);
        finishedWorkers.clear();
        return workers;
    }

    public static synchronized void add(Worker worker) {
        finishedWorkers.add(worker);
    }

    public static synchronized void remove(Worker worker) {
        finishedWorkers.remove(worker);
    }

    public static synchronized boolean isEmpty() {
        return finishedWorkers.isEmpty();
    }
}
