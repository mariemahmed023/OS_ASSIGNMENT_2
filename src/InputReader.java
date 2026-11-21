
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputReader {

    // Read processes entered manually
    public static List<Process> readFromConsole(Scanner scanner) {
        List<Process> processes = new ArrayList<>();

        System.out.print("Enter number of processes: ");
        int n = scanner.nextInt();

        if (n <= 0) {
            throw new IllegalArgumentException(
                "Number of processes must be positive");
        }

        for (int i = 0; i < n; i++) {
            System.out.println("\nProcess " + (i + 1));

            System.out.print("Process ID: ");
            String id = scanner.next();

            System.out.print("Arrival Time: ");
            int arrival = scanner.nextInt();

            System.out.print("Burst Time: ");
            int burst = scanner.nextInt();

            System.out.print("Priority: ");
            int priority = scanner.nextInt();

            processes.add(
                new Process(id, arrival, burst, priority)
            );
        }

        return processes;
    }

    // Read processes from a text file
    // Format: ID ArrivalTime BurstTime Priority
    public static List<Process> readFromFile(String filePath)
            throws FileNotFoundException {

        List<Process> processes = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] data = line.split("\\s+");

                if (data.length != 4) {
                    throw new IllegalArgumentException(
                        "Invalid process line: " + line);
                }

                String id = data[0];
                int arrival = Integer.parseInt(data[1]);
                int burst = Integer.parseInt(data[2]);
                int priority = Integer.parseInt(data[3]);

                processes.add(
                    new Process(id, arrival, burst, priority)
                );
            }
        }

        return processes;
    }
}
