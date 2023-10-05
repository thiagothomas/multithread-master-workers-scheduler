import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Random;

public class Pod {

    private final String name;
    private final Metrics metrics;

    public Pod(String name, Metrics metrics) {
        this.name = name;
        this.metrics = metrics;
    }

    public void execute() throws InterruptedException {
        Random random = new Random();
//        System.out.println("> POD #" + getName() + " - EXECUTING...\n");
        Thread.sleep(random.nextInt(7000) + 3000);
//        System.out.println("> POD #" + getName() + " - FINISHED...\n");
    }

    public Metrics getMetrics() {
        return metrics;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("--------------------- POD ----------------------\n");

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        stringBuilder.append(gson.toJson(this));
        stringBuilder.append("\n------------------------------------------------");
        return stringBuilder.toString();
    }

    public String getName() {
        return name;
    }
}
