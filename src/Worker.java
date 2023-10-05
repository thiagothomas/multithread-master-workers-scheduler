import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Worker extends Thread {

    private final String name;
    private final Metrics metrics;
    private final int initialCpu;
    private final int initialMemory;
    private final int initialDisk;

    private final Deque<Pod> pods;
    private final List<Pod> processingPods;
    private final ExecutorService executor;
    private boolean stop;
    private int row;

    public Worker(String name, Metrics metrics) {
        this.name = name;
        this.metrics = metrics;
        this.initialCpu = metrics.getCpu();
        this.initialMemory = metrics.getMemory();
        this.initialDisk = metrics.getDisk();
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
                }
            }

            if (pod != null) {
//                System.out.println(">>> Worker #" + getWorkerName() + " is working on POD #" + pod.getName() + "\n");
                processingPods.add(pod);
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
                    this.metrics.setDisk(this.metrics.getDisk() + finalPod.getMetrics().getDisk());
//                    String str = ">>> Worker #" + getWorkerName() + " finished working on POD #" + finalPod.getName() + "\n" +
//                            ">>> Worker #" + getWorkerName() + " updated Metrics - " + metrics + "\n";
//                    System.out.println(str);
                    FinishedWorkersUtil.add(this);
                    processingPods.remove(finalPod);
                    Monitor.setValueAt(((initialCpu - this.metrics.getCpu()) * 100) / initialCpu, row, TableUtil.CPU_COLUMN);
                    Monitor.setValueAt(((initialMemory - this.metrics.getMemory()) * 100) / initialMemory, row, TableUtil.MEMORY_COLUMN);
                    Monitor.setValueAt(((initialDisk - this.metrics.getDisk()) * 100) / initialDisk, row, TableUtil.DISK_COLUMN);
                    Monitor.setValueAt(getPodsNames(), row, TableUtil.PODS_COLUMN);
                }
            }
        });
    }

    public void finish() {
        this.stop = true;
    }

    public synchronized Metrics getMetrics() {
        return metrics;
    }

    public synchronized int getCpu() {
        return metrics.getCpu();
    }

    public synchronized int getMemory() {
        return metrics.getMemory();
    }

    public synchronized int getDisk() {
        return metrics.getDisk();
    }

    public synchronized void addPod(Pod pod) {
        int newCpu = this.metrics.getCpu() - pod.getMetrics().getCpu();
        int newMemory = this.metrics.getMemory() - pod.getMetrics().getMemory();
        int newDisk = this.metrics.getDisk() - pod.getMetrics().getDisk();
        this.metrics.setCpu(newCpu);
        this.metrics.setMemory(newMemory);
        this.metrics.setDisk(newDisk);

//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(String.format(">>>>>>>>>>>>>>>>> SCHEDULING POD %s to WORKER %S <<<<<<<<<<<<<<<<<\n", pod.getName(), this.getWorkerName()));
//        stringBuilder.append(">>> Worker #").append(getWorkerName()).append(" updated Metrics - ").append(metrics).append("\n");
//        System.out.println(stringBuilder);
        this.pods.add(pod);
        Monitor.setValueAt(((initialCpu - this.metrics.getCpu()) * 100) / initialCpu, row, TableUtil.CPU_COLUMN);
        Monitor.setValueAt(((initialMemory - this.metrics.getMemory()) * 100) / initialMemory, row, TableUtil.MEMORY_COLUMN);
        Monitor.setValueAt(((initialDisk - this.metrics.getDisk()) * 100) / initialDisk, row, TableUtil.DISK_COLUMN);
        Monitor.setValueAt(getPodsNames(), row, TableUtil.PODS_COLUMN);
    }

    public String getWorkerName() {
        return name;
    }

    public void setRow(int row) {
        this.row = row;
    }

    private synchronized List<String> getPodsNames ()
    {

        Set<String> names = new HashSet<>();
        Deque<Pod> _pods = getPods();
        List<Pod> _processingPods = getProcessingPods();
        _pods.forEach(pod -> {
            if (pod != null) {
                names.add(pod.getName());
            }
        });
        _processingPods.forEach(pod -> {
            if (pod != null) {
                names.add(pod.getName());
            }
        });

        return names.stream().toList();

    }

    public Deque<Pod> getPods() {
        return pods;
    }

    public List<Pod> getProcessingPods() {
        return processingPods;
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
