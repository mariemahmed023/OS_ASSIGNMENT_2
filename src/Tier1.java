import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Tier1 {
    private final Queue<Process> queue = new ArrayDeque<>();
    private final int quantum;

    public Tier1(int quantum) {
        if (quantum <= 0) {
            throw new IllegalArgumentException("Quantum must be positive");
        }
        this.quantum = quantum;
    }

    public void add(Process p) {
        queue.add(p);
    }

    // looks at the first process without removing it
    public Process peek() {
        return queue.peek();
    }

    // removes the first process from the queue
    public Process take() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // how long the process runs now: the smaller of Q and remaining
    public int runTime(Process p) {
        return Math.min(quantum, p.getRemainingTime());
    }

    public String show() {
        if (queue.isEmpty()) {
            return "Tier 1: [Empty]";
        }
        List<String> items = new ArrayList<>();
        for (Process p : queue) {
            items.add(p.getId() + " (remaining: " + p.getRemainingTime()
                    + ", Priority: " + p.getPriority() + ")");
        }
        return "Tier 1: [" + String.join(", ", items) + "]";
    }
}
