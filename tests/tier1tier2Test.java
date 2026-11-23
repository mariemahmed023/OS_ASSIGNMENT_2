void main() {
    // Test Case 1
    List<Process> tc1 = new ArrayList<>();
    tc1.add(new Process("P1", 0, 1, 2));
    tc1.add(new Process("P2", 3, 7, 3));
    tc1.add(new Process("P3", 5, 5, 1));

    ACS acs1 =
            new ACS(tc1, 3, 1);
    acs1.run();

}