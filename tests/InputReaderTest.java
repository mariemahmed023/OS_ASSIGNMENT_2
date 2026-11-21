
import java.util.List;

public class InputReaderTest {
    public static void main(String[] args) throws Exception {

        List<Process> processes =
            InputReader.readFromFile("processes.txt");

        System.out.println("Processes Loaded From File:");

        for (Process p : processes) {
            System.out.println(
                p.getId()
                + " | Arrival: " + p.getArrivalTime()
                + " | Burst: " + p.getBurstTime()
                + " | Priority: " + p.getPriority()
            );
        }
    }
}
