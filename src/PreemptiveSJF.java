
import java.util.ArrayList;
import java.util.List;

public class PreemptiveSJF {

    private final List<Process> processes;
    private final int contextSwitchTime;

    
public PreemptiveSJF(List<Process> processes, int contextSwitchTime) {
    if (processes == null) {
        throw new IllegalArgumentException(
            "Process list cannot be null"
        );
    }

    if (contextSwitchTime < 0) {
        throw new IllegalArgumentException(
            "Context switch time cannot be negative"
        );
    }

    this.processes = new ArrayList<>(processes);
    this.contextSwitchTime = contextSwitchTime;
}


    
    
public void run() {
    int currentTime = 0;
    int completed = 0;
    Process previousProcess = null;

    // Start with fresh copies of all processes
    List<Process> original = new ArrayList<>(processes);
    processes.clear();

    for (Process p : original) {
        processes.add(p.copy());
    }

    System.out.println("Preemptive SJF Scheduling:");

    while (completed < processes.size()) {

        Process shortest = findShortestProcess(currentTime);

        // No process is ready yet
        if (shortest == null) {
            System.out.println("Time " + currentTime + ": CPU Idle");
            previousProcess = null;
            currentTime++;
            continue;
        }

  


if (previousProcess != null && previousProcess != shortest) {

    if (contextSwitchTime > 0) {
        System.out.println(
            "Time " + currentTime + ": Context Switching"
        );

        currentTime += contextSwitchTime;
        shortest = findShortestProcess(currentTime);
    }
}



        // Record the first time the process runs
        
        if (shortest.getStartTime() == -1) {
            shortest.setStartTime(currentTime);
        }


        System.out.println(
            "Time " + currentTime + ": Running " + shortest.getId()
        );

        // Execute for one time unit
        shortest.setRemainingTime(
            shortest.getRemainingTime() - 1
        );

        previousProcess = shortest;

        currentTime++;

        // Check if the process has finished
        if (shortest.getRemainingTime() == 0) {
            shortest.setFinishTime(currentTime);
            completed++;

            System.out.println(
                "Time " + currentTime + ": "
                + shortest.getId() + " completed"
            );
        }
    }

    System.out.println("\nAll processes completed!");

System.out.println("\nScheduling Metrics:");

System.out.printf("%-10s %-10s %-10s %-10s %-10s%n",
        "Process", "CT", "TAT", "WT", "RT");


double totalTurnaround = 0;
double totalWaiting = 0;
double totalResponse = 0;

for (Process p : processes) {
    
System.out.printf("%-10s %-10d %-10d %-10d %-10d%n",
        p.getId(),
        p.getFinishTime(),
        p.getTurnaroundTime(),
        p.getWaitingTime(),
        p.getResponseTime());

    totalTurnaround += p.getTurnaroundTime();
    totalWaiting += p.getWaitingTime();
    totalResponse += p.getResponseTime();
}

if (!processes.isEmpty()) {
    System.out.printf("\nAverage Turnaround Time: %.2f%n",
            totalTurnaround / processes.size());
    System.out.printf("Average Waiting Time: %.2f%n",
            totalWaiting / processes.size());
    System.out.printf("Average Response Time: %.2f%n",
            totalResponse / processes.size());
}

}



    private Process findShortestProcess(int currentTime) {
        Process shortest = null;

        for (Process p : processes) {

            // Only consider arrived and unfinished processes
            if (p.getArrivalTime() <= currentTime
                    && p.getRemainingTime() > 0) {

                if (shortest == null
                        || p.getRemainingTime()
                           < shortest.getRemainingTime()) {
                    shortest = p;
                }
            }
        }

        return shortest;
    }

}
