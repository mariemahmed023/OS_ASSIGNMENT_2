import java.util.ArrayList;
import java.util.List;

public class ACSTest {

    public static void main(String[] args) {

        // Test Case 2
        List<Process> tc1 = new ArrayList<>();
        tc1.add(new Process("P1", 0, 5, 4));
        tc1.add(new Process("P2", 1, 6, 2));
        tc1.add(new Process("P3", 4, 4, 3));

        ACS acs1 = new ACS(tc1, 3, 1);
        acs1.run();
    }
}