import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {

    private Generator() {
    }

    public static List<Worker> generateRandomWorkers(int n) {
        Random random = new Random();

        List<Worker> workers = new ArrayList<>();

        String name;
        int cpu;
        int memory;
        for (int i = 1; i <= n; i++) {
            name = "Worker_" + i;
            cpu = random.nextInt(8) + 8;
            memory = random.nextInt(8) + 8;
            workers.add(new Worker(name, new Metrics(cpu, memory)));
        }

        return workers;
    }

    public static List<Pod> generateRandomPods(int n) {
        Random random = new Random();

        List<Pod> pods = new ArrayList<>();

        String name;
        int cpu;
        int memory;
        for (int i = 1; i <= n; i++) {
            name = "Pod_" + i;
            cpu = random.nextInt(7) + 1;
            memory = random.nextInt(7) + 1;
            pods.add(new Pod(name, new Metrics(cpu, memory)));
        }

        return pods;
    }
}
