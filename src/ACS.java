import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

public class ACS {
    private final List<Process> processes;
    private final int quantum;
    private final int contextSwitchTime;

    public ACS(List<Process> processes, int quantum, int contextSwitchTime) {
        if (processes == null) {
            throw new IllegalArgumentException("Process list cannot be null");
        }
        if (contextSwitchTime < 0) {
            throw new IllegalArgumentException("Context switch time cannot be negative");
        }
        if (quantum <= 0) {
            throw new IllegalArgumentException("Quantum time must be positive");
        }
        this.processes = new ArrayList<>(processes);
        this.contextSwitchTime = contextSwitchTime;
        this.quantum = quantum;
    }


    // Moves every process that has arrived by 'time' into Tier 1
    private static int admitArrivals(List<Process> runningProcesses, int nextIdx,
                                     int time, Deque<Process> tier1) {
        while (nextIdx < runningProcesses.size()
                && runningProcesses.get(nextIdx).getArrivalTime() <= time) {
            tier1.addLast(runningProcesses.get(nextIdx));
            nextIdx++;
        }
        return nextIdx;
    }

    // Tier 1 first; otherwise the smallest priority number in Tier 2
    // (ties go to whoever entered Tier 2 first)
    private static Process pickNext(Deque<Process> tier1, List<Process> tier2) {
        if (!tier1.isEmpty()) {
            return tier1.peekFirst();
        }
        Process best = tier2.get(0);
        for (Process p : tier2) {
            if (p.getPriority() < best.getPriority()) {
                best = p;
            }
        }
        return best;
    }

    public void run() {
        List<Process> runningProcesses = new ArrayList<>();
        for (Process p : processes) {
            runningProcesses.add(p.copy());
        }

        Deque<Process> tier1 = new ArrayDeque<>();
        List<Process> tier2 = new ArrayList<>();

        int time = 0;
        int completed = 0;
        int nextIdx = 0;
        Process lastRan = null;

        System.out.println("ACS - Q = " + quantum + ", CS = " + contextSwitchTime);

        while (completed < runningProcesses.size()) {
            nextIdx = admitArrivals(runningProcesses, nextIdx, time, tier1);

            // Nothing ready: jump to the next arrival
            if (tier1.isEmpty() && tier2.isEmpty()) {
                time = runningProcesses.get(nextIdx).getArrivalTime();
                lastRan = null;
                continue;
            }

            Process next = pickNext(tier1, tier2);

            // Context switch between two different processes
            if (lastRan != null && lastRan != next && contextSwitchTime > 0) {
                System.out.println("Time " + time + ": Context Switch "
                        + lastRan.getId() + " -> " + next.getId());
                time += contextSwitchTime;
                nextIdx = admitArrivals(runningProcesses, nextIdx, time, tier1);
                next = pickNext(tier1, tier2);
            }

            // pickNext always prefers Tier 1 when it is not empty
            boolean fromTier1 = !tier1.isEmpty();
            if (fromTier1) {
                tier1.pollFirst();
            } else {
                tier2.remove(next);
            }

            next.setStartTime(time);
            int run = fromTier1
                    ? Math.min(quantum, next.getRemainingTime())
                    : next.getRemainingTime();

            System.out.println("Time " + time + ": Running " + next.getId()
                    + " (Tier " + (fromTier1 ? 1 : 2) + ") for " + run);

            time += run;
            next.setRemainingTime(next.getRemainingTime() - run);
            nextIdx = admitArrivals(runningProcesses, nextIdx, time, tier1);

            if (next.getRemainingTime() == 0) {
                next.setFinishTime(time);
                completed++;
                System.out.println("Time " + time + ": " + next.getId() + " completed");
            } else {
                tier2.add(next);
                System.out.println("Time " + time + ": " + next.getId()
                        + " demoted to Tier 2");
            }

            lastRan = next;
        }

        System.out.println("\nAll processes completed!");
    }
}









