import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ACS {
    private final List<Process> processes;
    private final int quantum;
    private final int contextSwitch;
    private final Tier1 tier1;
    private final Tier2 tier2 = new Tier2();

    private List<Process> waiting; // processes that have not arrived yet
    private int time;

    public ACS(List<Process> processes, int quantum, int contextSwitch) {
        if (processes == null) {
            throw new IllegalArgumentException("Process list cannot be null");
        }
        if (contextSwitch < 0) {
            throw new IllegalArgumentException("Context switch cannot be negative");
        }
        if (quantum <= 0) {
            throw new IllegalArgumentException("Quantum must be positive");
        }
        this.processes = new ArrayList<>(processes);
        this.quantum = quantum;
        this.contextSwitch = contextSwitch;
        this.tier1 = new Tier1(quantum);
    }

    // moves every process that has arrived into Tier 1
    private void addArrivals() {
        while (!waiting.isEmpty()
                && waiting.get(0).getArrivalTime() <= time) {
            tier1.add(waiting.remove(0));
        }
    }

    private void printStatus(Process p, boolean fromTier1) {
        System.out.println("\nTime: " + time);
        System.out.println("Process: " + p.getId()
                + " (Tier " + (fromTier1 ? 1 : 2) + ")");
        System.out.println(tier1.show());
        System.out.println(tier2.show());
    }

    public void run() {
        // clean copies (input order), and a second list sorted by arrival
        List<Process> all = new ArrayList<>();
        for (Process p : processes) {
            all.add(p.copy());
        }
        waiting = new ArrayList<>(all);
        waiting.sort(Comparator.comparingInt(Process::getArrivalTime));

        int completed = 0;
        Process last = null;
        time = 0;

        System.out.println("ACS - Q = " + quantum + ", CS = " + contextSwitch);

        while (completed < all.size()) {
            addArrivals();

            // nothing ready: CPU idle until the next arrival
            if (tier1.isEmpty() && tier2.isEmpty()) {
                time = waiting.get(0).getArrivalTime();
                last = null;
                continue;
            }

            tier2.age(time);

            boolean fromTier1 = !tier1.isEmpty();
            Process p;

            if (fromTier1) {
                p = tier1.take();
            } else {
                p = tier2.take();
            }

            printStatus(p, fromTier1);

            // context switch between two different processes
            if (last != null && last != p && contextSwitch > 0) {
                System.out.println("Time " + time + ": Context switch "
                        + last.getId() + " -> " + p.getId());
                time += contextSwitch;
                addArrivals();
                tier2.age(time);
            }

            p.setStartTime(time); // only the first run is recorded
            int run = fromTier1 ? tier1.runTime(p) : p.getRemainingTime();

            System.out.println("Time " + time + ": Running " + p.getId()
                    + " (Tier " + (fromTier1 ? 1 : 2) + ") for " + run);

            // run one time unit at a time
            for (int i = 0; i < run; i++) {
                if (i > 0) {
                    printStatus(p, fromTier1);
                }
                p.setRemainingTime(p.getRemainingTime() - 1);
                time++;
                addArrivals();
                tier2.age(time);
            }

            if (p.getRemainingTime() == 0) {
                p.setFinishTime(time);
                completed++;
                System.out.println("Time " + time + ": " + p.getId() + " completed");
            } else {
                tier2.add(p, time); // used the whole Q: demoted
                System.out.println("Time " + time + ": " + p.getId()
                        + " demoted to Tier 2");
            }

            last = p;
        }

        printMetrics(all);
    }

    private void printMetrics(List<Process> all) {
        System.out.println("\nProcess Metrics:");

        double totalWT = 0;
        double totalTAT = 0;

        for (Process p : all) {
            System.out.println(p.getId()
                    + " | CT: " + p.getFinishTime()
                    + " | TAT: " + p.getTurnaroundTime()
                    + " | WT: " + p.getWaitingTime()
                    + " | RT: " + p.getResponseTime());
            totalWT += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();
        }

        System.out.printf("%nAverage Waiting Time: %.2f%n", totalWT / all.size());
        System.out.printf("Average Turnaround Time: %.2f%n", totalTAT / all.size());
    }
}









