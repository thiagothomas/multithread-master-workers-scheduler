import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Worker extends Thread {

    private final String name;
    private final Metrics metrics;

    private transient final Deque<Pod> pods;
    private final List<Pod> processingPods;
    private transient final ExecutorService executor;
    private boolean stop;

    public Worker(String name, Metrics metrics) {
        this.name = name;
        this.metrics = metrics;
        this.pods = new ArrayDeque<>();
        executor = Executors.newThreadPerTaskExecutor(Executors.defaultThreadFactory());
        processingPods = new ArrayList<>();
        this.stop = false;
    }

    @Override
    public void run() {
        while (!stop || !processingPods.isEmpty() || !pods.isEmpty()) {
            Pod pod = null;

            synchronized (pods) {
                if (!pods.isEmpty()) {
                    pod = pods.pollFirst();
                    processingPods.add(pod);
                }
            }

            if (pod != null) {
                System.out.println(">>> Worker #" + getWorkerName() + " is working on POD #" + pod.getName() + "\n");

                executePod(pod);
            }
        }
    }

    private void executePod(Pod finalPod) {
        executor.submit(() -> {
            try {
                finalPod.execute();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                synchronized (this) {
                    this.metrics.setCpu(this.metrics.getCpu() + finalPod.getMetrics().getCpu());
                    this.metrics.setMemory(this.metrics.getMemory() + finalPod.getMetrics().getMemory());
                    String str = ">>> Worker #" + getWorkerName() + " finished working on POD #" + finalPod.getName() + "\n" +
                            ">>> Worker #" + getWorkerName() + " updated Metrics - " + metrics + "\n";
                    System.out.println(str);
                    FinishedWorkersUtil.add(this);
                    processingPods.remove(finalPod);
                }
            }
        });
    }

    public void finish() {
        this.stop = true;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void addPod(Pod pod) {
        synchronized (this) {
            int newCpu = this.metrics.getCpu() - pod.getMetrics().getCpu();
            int newMemory = this.metrics.getMemory() - pod.getMetrics().getMemory();
            this.metrics.setCpu(newCpu);
            this.metrics.setMemory(newMemory);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format(">>>>>>>>>>>>>>>>> SCHEDULING POD %s to WORKER %S <<<<<<<<<<<<<<<<<\n", pod.getName(), this.getWorkerName()));
            stringBuilder.append(">>> Worker #").append(getWorkerName()).append(" updated Metrics - ").append(metrics).append("\n");
            System.out.println(stringBuilder);
            this.pods.add(pod);
        }
    }

    public String getWorkerName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("==================== WORKER ====================\n");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject json = new JsonObject();
        json.addProperty("id", this.name);
        json.addProperty("pods", this.pods.toString());
        json.add("metrics", gson.toJsonTree(this.metrics));
        json.add("pods", gson.toJsonTree(this.processingPods));

        stringBuilder.append(gson.toJson(json));
        stringBuilder.append("\n================================================");
        return stringBuilder.toString();
    }

}
