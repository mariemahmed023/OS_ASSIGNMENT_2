import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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

    private static void updateAging(List<Process> tier2,
        Map<Process, Integer> tier2EntryTime,
        int time) {

            for (Process p : tier2) {
                int waitingTime = time - tier2EntryTime.get(p);
                int aging = waitingTime / 4;

                int newPriority = p.getInitialPriority() - aging;

                if (newPriority < 1) {
                newPriority = 1;
                }

                p.setPriority(newPriority);
            }
    }

    private static void printQueue(String name, Iterable<Process> queue) {
        System.out.print(name + ": ");
    
        if (!queue.iterator().hasNext()) {
            System.out.println("[Empty]");
            return;
        }
    
        for (Process p : queue) {
            System.out.print("[" + p.getId()
                    + " (remaining: " + p.getRemainingTime()
                    + ", Priority: " + p.getPriority() + ")] ");
        }
    
        System.out.println();
    }

    private static void printStatus(int time, Process p,
        boolean fromTier1,
        Deque<Process> tier1,
        List<Process> tier2) {

            System.out.println("\nTime: " + time);
            System.out.println("Process: " + p.getId()
            + " (Tier " + (fromTier1 ? 1 : 2) + ")");

            printQueue("Tier 1", tier1);
            printQueue("Tier 2", tier2);
        }


    public void run() {
        List<Process> runningProcesses = new ArrayList<>();
        for (Process p : processes) {
            runningProcesses.add(p.copy());
        }

        Deque<Process> tier1 = new ArrayDeque<>();
        List<Process> tier2 = new ArrayList<>();

        Map<Process, Integer> tier2EntryTime = new HashMap<>();

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

            updateAging(tier2, tier2EntryTime, time);

            Process next = pickNext(tier1, tier2);

            // Context switch between two different processes
            if (lastRan != null && lastRan != next && contextSwitchTime > 0) {
                System.out.println("Time " + time + ": Context Switch "
                        + lastRan.getId() + " -> " + next.getId());
                time += contextSwitchTime;
                nextIdx = admitArrivals(runningProcesses, nextIdx, time, tier1);
                updateAging(tier2, tier2EntryTime, time);
                next = pickNext(tier1, tier2);
            }


            // pickNext always prefers Tier 1 when it is not empty
            boolean fromTier1 = !tier1.isEmpty();

            printStatus(time, next, fromTier1, tier1, tier2);

            if (fromTier1) {
                tier1.pollFirst();
            } else {
                tier2.remove(next);
            }
            if (!fromTier1) {
                tier2EntryTime.remove(next);
            }

            next.setStartTime(time);
            int run = fromTier1
                    ? Math.min(quantum, next.getRemainingTime())
                    : next.getRemainingTime();

            System.out.println("Time " + time + ": Running " + next.getId()
                    + " (Tier " + (fromTier1 ? 1 : 2) + ") for " + run);

                    for (int i = 0; i < run; i++) {
                        if (i > 0) {
                            printStatus(time, next, fromTier1, tier1, tier2);
                        }
                    
                        next.setRemainingTime(next.getRemainingTime() - 1);
                        time++;
                    
                        nextIdx = admitArrivals(runningProcesses, nextIdx, time, tier1);
                        updateAging(tier2, tier2EntryTime, time);
                    }

            if (next.getRemainingTime() == 0) {
                next.setFinishTime(time);
                completed++;
                System.out.println("Time " + time + ": " + next.getId() + " completed");
            } else {
                tier2.add(next);
                tier2EntryTime.put(next, time);
            
                System.out.println("Time " + time + ": " + next.getId()
                        + " demoted to Tier 2");
            }

            lastRan = next;
        }


        System.out.println("\nProcess Metrics:");

        double totalWT = 0;
        double totalTAT = 0;

        for (Process p : runningProcesses) {
            int wt = p.getWaitingTime();
            int tat = p.getTurnaroundTime();
            int rt = p.getResponseTime();

            System.out.println(p.getId()
                    + " | CT: " + p.getFinishTime()
                    + " | TAT: " + tat
                    + " | WT: " + wt
                    + " | RT: " + rt);

            totalWT += wt;
            totalTAT += tat;
        }

        if (!runningProcesses.isEmpty()) {
            double avgWT = totalWT / runningProcesses.size();
            double avgTAT = totalTAT / runningProcesses.size();

            System.out.println("\nAverage Waiting Time: " + avgWT);
            System.out.println("Average Turnaround Time: " + avgTAT);
        }
    }
}









