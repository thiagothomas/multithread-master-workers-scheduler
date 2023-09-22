import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Metrics {

    private Integer cpu;
    private Integer memory;

    public Metrics(int cpu, int memory) {
        this.cpu = cpu;
        this.memory = memory;
    }

    public Integer getCpu() {
        return cpu;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    @Override
    public String toString() {
        return "Metrics{" +
                "cpu=" + cpu +
                ", memory=" + memory +
                '}';
    }
}
