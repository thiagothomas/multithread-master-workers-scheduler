public class Metrics {

    private Integer cpu;
    private Integer memory;
    private Integer disk;

    public Metrics(int cpu, int memory, Integer disk) {
        this.cpu = cpu;
        this.memory = memory;
        this.disk = disk;
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

    public Integer getDisk() {
        return disk;
    }

    public void setDisk(int disk) {
        this.disk = disk;
    }

    @Override
    public String toString() {
        return "Metrics{" +
                "cpu=" + cpu +
                ", memory=" + memory +
                ", disk=" + disk +
                '}';
    }
}
