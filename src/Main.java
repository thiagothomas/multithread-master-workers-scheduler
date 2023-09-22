import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Iniciando o escalonador...");

        InputStreamReader ir = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(ir);

        System.out.print("Quantos nodos Workers você deseja? ");
        Integer nWorkers = Integer.valueOf(reader.readLine());

        System.out.print("Quantos Pods você deseja criar? ");
        Integer nPods = Integer.valueOf(reader.readLine());

        Master master = new Master();

        List<Worker> workers = Generator.generateRandomWorkers(nWorkers);
        List<Pod> pods = Generator.generateRandomPods(nPods);

        workers.forEach(master::addWorker);
        pods.forEach(master::addPod);

        master.start();
    }
}