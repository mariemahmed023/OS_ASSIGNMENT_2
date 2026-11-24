import java.util.ArrayList;
import java.util.List;

public class ACSTest {

    public static void main(String[] args) {

        // Test Case 1
        List<Process> tc1 = new ArrayList<>();
        tc1.add(new Process("P1", 0, 3, 2));
        tc1.add(new Process("P2", 1, 7, 3));
        tc1.add(new Process("P3", 3, 2, 1));

        ACS acs1 = new ACS(tc1, 3, 1);
        acs1.run();
    }
}