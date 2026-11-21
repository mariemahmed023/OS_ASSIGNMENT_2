public class ProcessTest {
    public static void main(String[] args) {

        Process p1 = new Process("P1", 0, 4, 2);

        p1.setStartTime(0);
        p1.setFinishTime(9);
        p1.setRemainingTime(0);

        System.out.println("Process: " + p1.getId());
        System.out.println("Arrival Time: " + p1.getArrivalTime());
        System.out.println("Burst Time: " + p1.getBurstTime());
        System.out.println("Remaining Time: " + p1.getRemainingTime());

        System.out.println("Turnaround Time: " + p1.getTurnaroundTime());
        System.out.println("Waiting Time: " + p1.getWaitingTime());
        System.out.println("Response Time: " + p1.getResponseTime());
    }
}
