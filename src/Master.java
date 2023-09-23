import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.TreeSet;


public class Master extends Thread {

    private final TreeSet<Worker> workers;
    private final Deque<Pod> pods;

    public Master() {
        this.workers = new TreeSet<>((w1, w2) -> {
            Metrics m1 = w1.getMetrics();
            Metrics m2 = w2.getMetrics();
            if (!Objects.equals(m1.getCpu(), m2.getCpu())) {
                return m1.getCpu() - m2.getCpu();
            } else {
                return m1.getMemory() - m2.getMemory();
            }
        });
        pods = new ArrayDeque<>();
    }

    @Override
    public void run() {
        System.out.println("ALL WORKERS:");
        workers.forEach(System.out::println);

        System.out.println("ALL PODS:");
        pods.forEach(System.out::println);

        workers.forEach(Thread::start);

        while (true) {
            checkWorkers();
            Pod pod = null;

            synchronized (pods) {
                if (!pods.isEmpty()) {
                    pod = pods.pollFirst();
                }
            }

            if (pod != null) {
                Worker neededWorker = new Worker("", new Metrics(pod.getMetrics().getCpu(), pod.getMetrics().getMemory()));

                synchronized (workers) {
                    neededWorker = workers.ceiling(neededWorker);
                }

                if (neededWorker != null && verifyWorkersMetrics(neededWorker, pod)) {
                    neededWorker.addPod(pod);
                    synchronized (workers) {
                        workers.remove(neededWorker);
                        workers.add(neededWorker);
                    }
                } else {
                    synchronized (pods) {
                        pods.add(pod);
                    }
                }
            } else {
                System.out.println(">>>>>>>>>>>>>>>>> FINISHED SCHEDULING ALL PODS <<<<<<<<<<<<<<<<<");
                workers.forEach(Worker::finish);
                break;
            }
        }
    }

    private boolean verifyWorkersMetrics(Worker worker, Pod pod) {
        int cpu = worker.getCpu();
        int mem = worker.getMemory();

        return pod.getMetrics().getCpu() <= cpu && pod.getMetrics().getMemory() <= mem;
    }

    private void checkWorkers() {
        if (!FinishedWorkersUtil.isEmpty()) {
            synchronized (workers) {
                FinishedWorkersUtil.getAndClear().forEach(w -> {
                    workers.remove(w);
                    workers.add(w);
                });
            }
        }
    }

    public void addWorker(Worker worker) {
        workers.add(worker);
    }

    public void addPod(Pod pod) {
        pods.add(pod);
    }

}
