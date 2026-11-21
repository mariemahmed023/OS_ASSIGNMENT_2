
import java.util.ArrayList;
import java.util.List;

public class SJFTest {
    public static void main(String[] args) {

        List<Process> processes = new ArrayList<>();


processes.add(new Process("P1", 0, 5, 2));
processes.add(new Process("P2", 1, 3, 1));
processes.add(new Process("P3", 2, 1, 3));




        PreemptiveSJF scheduler = new PreemptiveSJF(processes, 2);

        scheduler.run();
    }
}
