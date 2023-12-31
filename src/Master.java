import java.util.*;


public class Master extends Thread {

    private final TreeSet<Worker> workers;
    private final Deque<Pod> pods;

    public Master() {
        this.workers = new TreeSet<>((w1, w2) -> {
            Metrics m1 = w1.getMetrics();
            Metrics m2 = w2.getMetrics();
            if (!Objects.equals(m1.getCpu(), m2.getCpu())) {
                return m1.getCpu() - m2.getCpu();
            } else if (!Objects.equals(m1.getMemory(), m2.getMemory())){
                return m1.getMemory() - m2.getMemory();
            } else {
                return m1.getDisk() - m2.getDisk();
            }
        });
        pods = new ArrayDeque<>();
    }

    @Override
    public void run() {
//        System.out.println("ALL WORKERS:");
//        workers.forEach(System.out::println);
//
//        System.out.println("ALL PODS:");
//        pods.forEach(System.out::println);

        workers.forEach(Thread::start);

        while (true) {
            checkWorkers();
            Pod pod = null;

            if (!pods.isEmpty()) {
                pod = pods.pollFirst();
            }


            if (pod != null) {
                Worker neededWorker = new Worker("", new Metrics(pod.getMetrics().getCpu(), pod.getMetrics().getMemory(), pod.getMetrics().getDisk()));

                neededWorker = workers.ceiling(neededWorker);


                if (neededWorker != null && verifyWorkersMetrics(neededWorker, pod)) {
                    neededWorker.addPod(pod);
                    workers.remove(neededWorker);
                    workers.add(neededWorker);
                } else {
                    pods.add(pod);
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
        int disk = worker.getDisk();

        return pod.getMetrics().getCpu() <= cpu && pod.getMetrics().getMemory() <= mem && pod.getMetrics().getDisk() <= disk;
    }

    private void checkWorkers() {
        if (!FinishedWorkersUtil.isEmpty()) {

            FinishedWorkersUtil.getAndClear().forEach(w -> {
                workers.remove(w);
                workers.add(w);
            });

        }
    }

    public void addWorker(Worker worker) {
        workers.add(worker);
    }

    public void addPod(Pod pod) {
        pods.add(pod);
    }

}
