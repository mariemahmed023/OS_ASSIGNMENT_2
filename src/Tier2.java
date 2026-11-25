import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tier2 {
    private final List<Process> list = new ArrayList<>();
    private final Map<Process, Integer> entryTime = new HashMap<>();

    public void add(Process p, int time) {
        list.add(p);
        entryTime.put(p, time);
    }

    // best = smallest priority number;
    public Process peek() {
        Process best = list.get(0);
        for (Process p : list) {
            if (p.getPriority() < best.getPriority()) {
                best = p;
            }
        }
        return best;
    }

    public Process take() {
        Process best = peek();
        list.remove(best);
        entryTime.remove(best);
        return best;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    // Aging: every 4 units of waiting lowers the priority number by 1
    public void age(int time) {
        for (Process p : list) {
            int aging = (time - entryTime.get(p)) / 4;
            p.setPriority(Math.max(1, p.getInitialPriority() - aging));
        }
    }

    public String show() {
        if (list.isEmpty()) {
            return "Tier 2: [Empty]";
        }
        List<String> items = new ArrayList<>();
        for (Process p : list) {
            items.add(p.getId() + " (remaining: " + p.getRemainingTime()
                    + ", Priority: " + p.getPriority() + ")");
        }
        return "Tier 2: [" + String.join(", ", items) + "]";
    }
}
