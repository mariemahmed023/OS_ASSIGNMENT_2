
public class Process {

    private final String id;
    private final int arrivalTime;
    private final int burstTime;
    private final int initialPriority;

    private int remainingTime;
    private int priority;
    private int startTime;
    private int finishTime;

    public Process(String id, int arrivalTime,
                   int burstTime, int priority) {

        if (id == null || id.isBlank()
                || arrivalTime < 0 || burstTime <= 0) {
            throw new IllegalArgumentException(
                "Invalid process data");
        }

        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.initialPriority = priority;

        this.remainingTime = burstTime;
        this.priority = priority;
        this.startTime = -1;
        this.finishTime = -1;
    }

    public String getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getInitialPriority() {
        return initialPriority;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        if (remainingTime < 0) {
            throw new IllegalArgumentException(
                "Remaining time cannot be negative");
        }
        this.remainingTime = remainingTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        if (this.startTime == -1) {
            this.startTime = startTime;
        }
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getTurnaroundTime() {
        return finishTime - arrivalTime;
    }

    public int getWaitingTime() {
        return getTurnaroundTime() - burstTime;
    }

    public int getResponseTime() {
        return startTime - arrivalTime;
    }

    public Process copy() {
        return new Process(
            id, arrivalTime, burstTime, initialPriority
        );
    }
}
