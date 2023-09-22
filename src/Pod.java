import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Pod {

    private final String name;
    private final Metrics metrics;

    public Pod(String name, Metrics metrics) {
        this.name = name;
        this.metrics = metrics;
    }

    public void execute() throws InterruptedException {
        System.out.println("> POD #" + getName() + " - EXECUTING...\n");
        Thread.sleep(4000);
        System.out.println("> POD #" + getName() + " - FINISHED...\n");
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

    private void printInicioExecucao() {
        StringBuilder stringBuilder = new StringBuilder("--------------------- POD ----------------------\n");

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        stringBuilder.append(gson.toJson(this));
        stringBuilder.append("\n------------------- EXECUTING ------------------");
        System.out.println(stringBuilder);
    }

    private void printFimExecucao() {
        StringBuilder stringBuilder = new StringBuilder("--------------------- POD ----------------------\n");

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        stringBuilder.append(gson.toJson(this));
        stringBuilder.append("\n------------------- FINISHED -------------------");
        System.out.println(stringBuilder);
    }

    public String getName() {
        return name;
    }
}
