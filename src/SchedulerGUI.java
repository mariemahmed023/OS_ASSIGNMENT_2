import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import  java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SchedulerGUI extends JFrame{
   private JTable inputTable;
   private DefaultTableModel inputModel;
   private JTextField txtQuantum, txtCs , txtPid, txtArrival, txtBurst, txtPriority;
   private JComboBox<String> cmbAlgorithm;

   private JTextArea logArea;
   private JLabel lblCurrentStep, lblAvgTAT, lblAvgWT, lblAvgRT;
   private  JTable metricsTable;
   private  DefaultTableModel metricsModel;

   private final List<String> simulationBlocks = new ArrayList<>();
   private int currentBlockIndex = 0;

   public SchedulerGUI(){
      setTitle("CS241 - Cloud-Cluster CPU Scheduling Simulator");
      setSize(1150 , 760);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocationRelativeTo(null);
      setLayout(new BorderLayout(10, 10));

      try{
         for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
            if("Nimbus".equals(info.getName())){
               UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      }
      catch (Exception ignored) {}

      add(createTopPanel() , BorderLayout.NORTH);

      JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel() , createRightPanel());
      mainSplit.setDividerLocation(410);
      add(mainSplit , BorderLayout.CENTER);

      loadTestCase1();
   }

   private  JPanel createTopPanel(){
      JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12 , 10));
      panel.setBorder(new TitledBorder("Configuration & Test Cases"));
      panel.setBackground(new Color(238, 242, 247));

      panel.add(new JLabel("Scheduler:"));
      cmbAlgorithm = new JComboBox<>(new String[]{
              "Adaptive Container Scheduler (ACS)",
              "Preemptive Shortest-Job First (SJF)"
      });
      cmbAlgorithm.addActionListener(e -> {
         boolean isACS = cmbAlgorithm.getSelectedIndex() == 0;
         txtQuantum.setEnabled(isACS);
      });
      panel.add(cmbAlgorithm);

      panel.add(new JLabel("Quantum (Q):"));
      txtQuantum = new JTextField("3" , 4);
      panel.add(txtQuantum);

      panel.add(new JLabel("Context Switch (CS):"));
      txtCs = new JTextField("0" , 4);
      panel.add(txtCs);

      JButton btnTC1 = new JButton("Test Case 1");
      btnTC1.addActionListener(e -> loadTestCase1());
      panel.add(btnTC1);

      JButton btnTC2 = new JButton("Test Case 2");
      btnTC2.addActionListener(e -> loadTestCase2());
      panel.add(btnTC2);

      JButton btnImport = new JButton("Load File (.txt)");
      btnImport.addActionListener(e -> loadFromFileUsingTeamReader());
      panel.add(btnImport);

      return panel;
   }

   private  JPanel createLeftPanel(){
      JPanel panel = new JPanel(new BorderLayout(8,8));
      panel.setBorder(new EmptyBorder(5, 10, 10, 5));

      JPanel formPanel = new JPanel(new GridLayout(3,4,6,6));
      formPanel.setBorder(new TitledBorder("Add Process Manually"));

      txtPid = new JTextField("P4");
      txtArrival = new JTextField("0");
      txtBurst = new JTextField("5");
      txtPriority = new JTextField("1");

      formPanel.add(new JLabel("Process ID:"));
      formPanel.add(txtPid);
      formPanel.add(new JLabel("Arrival Time:"));
      formPanel.add(txtArrival);
      formPanel.add(new JLabel("Burst Time:"));
      formPanel.add(txtBurst);
      formPanel.add(new JLabel("Priority:"));
      formPanel.add(txtPriority);

      JButton btnAdd = new JButton("+ Add Process");
      btnAdd.setBackground(new Color(46,125,50));
      btnAdd.setForeground(Color.WHITE);
      btnAdd.addActionListener(e -> addManualprocess());


      JButton btnClear = new JButton("Clear Table");
      btnClear.setBackground(new Color(198,40,40));
      btnClear.setForeground(Color.WHITE);
      btnClear.addActionListener(e ->{
         inputModel.setRowCount(0);
         txtPid.setText("P1");
      });
      formPanel.add(btnAdd);
      formPanel.add(btnClear);
      panel.add(formPanel,BorderLayout.NORTH);

      inputModel = new DefaultTableModel(new String[]{"ID" , "Arrival" , "Burst" , "Priority"} , 0);
      inputTable = new JTable(inputModel);
      inputTable.setRowHeight(24);
      JScrollPane tableScroll = new JScrollPane(inputTable);
      tableScroll.setBorder(new TitledBorder("Input Processes Queue"));
      panel.add(tableScroll, BorderLayout.CENTER);

      JButton btnRun = new JButton("▶ RUN SIMULATION");
      btnRun.setFont(new Font("SansSerif" , Font.BOLD,15));
      btnRun.setBackground(new Color(21,101,192));
      btnRun.setForeground(Color.WHITE);
      btnRun.setPreferredSize(new Dimension(0 , 48));
      btnRun.addActionListener(e -> runSimulation());
      panel.add(btnRun, BorderLayout.SOUTH);

      return panel;
   }

   private JPanel createRightPanel(){
      JPanel panel = new JPanel(new BorderLayout(8,8));
      panel.setBorder(new EmptyBorder(5,5,10,10));

      JPanel timelinePanel = new JPanel(new BorderLayout(5,5));
      timelinePanel.setBorder(new TitledBorder("Execution Timeline & Tier States (Heuristic Demotion Matrix"));

      logArea = new JTextArea();
      logArea.setFont(new Font("Monospaced" , Font.PLAIN,13));
      logArea.setEditable(false);
      logArea.setMargin(new Insets(8,8,8,8));
      timelinePanel.add(new JScrollPane(logArea) , BorderLayout.CENTER);

      JPanel stepBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10,4));
      lblCurrentStep = new JLabel("Progress: 0 / 0");
      lblCurrentStep.setFont(new Font("SansSerif" , Font.BOLD, 12));

      JButton btnResetSteps = new JButton("⟳Reset");
      JButton btnNextStep = new JButton("Next Step ⏭");
      JButton btnShowAll = new JButton("Show Full Output ⏯");

      btnResetSteps.addActionListener(e -> resetStepView());
      btnNextStep.addActionListener(e->showNextBlock());
      btnShowAll.addActionListener(e->showAllBlocks());

      stepBar.add(lblCurrentStep);
      stepBar.add(btnResetSteps);
      stepBar.add(btnNextStep);
      stepBar.add(btnShowAll);
      timelinePanel.add(stepBar, BorderLayout.SOUTH);

      JPanel metricsPanel = new JPanel(new BorderLayout(5,5));
      metricsPanel.setBorder(new TitledBorder("Scheduling Analysis & Performance Metrics"));
      metricsPanel.setPreferredSize(new Dimension(0,240));

      metricsModel = new DefaultTableModel(
              new  String[]{"Process" , "Finish Time(CT)" , "Turnaround (TAT)","Waiting (WT)" , "Response (RT)"},0
      );
      metricsTable = new JTable(metricsModel);
      metricsTable.setRowHeight(24);
      metricsPanel.add(new JScrollPane(metricsTable) , BorderLayout.CENTER);

      JPanel avgBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 6));
      avgBar.setBackground(new Color(232,245,233));

      lblAvgTAT = new JLabel("Average Turnaround Time: -");
      lblAvgTAT.setFont(new Font("SansSerif" , Font.BOLD,13));
      lblAvgWT = new JLabel("Average Waiting Time: -");
      lblAvgWT.setFont(new Font("SansSerif" , Font.BOLD,13));
      lblAvgRT = new JLabel("Average Response Time: -");
      lblAvgRT.setFont(new Font("SansSerif" , Font.BOLD,13));

      avgBar.add(lblAvgTAT);
      avgBar.add(lblAvgWT);
      avgBar.add(lblAvgRT);
      metricsPanel.add(avgBar, BorderLayout.SOUTH);

      JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,timelinePanel , metricsPanel);
      rightSplit.setDividerLocation(390);
      panel.add(rightSplit,BorderLayout.CENTER);

      return panel;
   }

   private void addManualprocess(){
      try{
         String id = txtPid.getText().trim();
         int arr = Integer.parseInt(txtArrival.getText().trim());
         int burst = Integer.parseInt(txtBurst.getText().trim());
         int prio = Integer.parseInt(txtPriority.getText().trim());

         if(id.isEmpty() || arr < 0 || burst <= 0){
            throw new IllegalArgumentException("Invalid values");
         }
         inputModel.addRow(new Object[]{id ,arr , burst , prio});
         txtPid.setText("P" + (inputModel.getRowCount() + 1));
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this, "Please enter valid values (Arrival >= 0 , Burst > 0).",
                 "Input Error" , JOptionPane.ERROR_MESSAGE);

      }
   }

   private void loadTestCase1(){
      inputModel.setRowCount(0);
      txtQuantum.setText("3");
      txtCs.setText("0");
      inputModel.addRow(new Object[]{"P1" , 0 ,4,2});
      inputModel.addRow(new Object[]{"P2" , 1 , 7 , 3});
      inputModel.addRow(new Object[]{"P3" , 3 , 2 , 1});
      txtPid.setText("P4");
   }

   private void loadTestCase2(){
      inputModel.setRowCount(0);
      txtQuantum.setText("3");
      txtCs.setText("1");
      inputModel.addRow(new Object[]{"P1" , 0 ,5,4});
      inputModel.addRow(new Object[]{"P2" , 1 , 6 , 2});
      inputModel.addRow(new Object[]{"P3" , 4 , 4 , 3});
      txtPid.setText("P4");
   }

   private void loadFromFileUsingTeamReader(){
      JFileChooser chooser = new JFileChooser();
      if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
         File file = chooser.getSelectedFile();

         try {
            List<Process> loaded = InputReader.readFromFile(file.getAbsolutePath());
            inputModel.setRowCount(0);
            for(Process p : loaded){
               inputModel.addRow(new Object[]{
                       p.getId(), p.getArrivalTime(), p.getBurstTime(), p.getInitialPriority()
               });
            }
            txtPid.setText("P" + (inputModel.getRowCount() + 1));
         }
         catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error reading file:\n" + ex.getMessage() + "\nExpected format per line : ID Arrival Burst Priority",
                    "File Error" , JOptionPane.ERROR_MESSAGE);
         }
      }
   }

   private void runSimulation(){
      if (inputModel.getRowCount() == 0){
         JOptionPane.showMessageDialog(this,"Please add at least one process!" ,"No Processes" , JOptionPane.WARNING_MESSAGE);
         return;
      }
      int q,cs;
      try{
         q = Integer.parseInt(txtQuantum.getText().trim());
         cs = Integer.parseInt(txtCs.getText().trim());
      }
      catch (NumberFormatException ex) {
         JOptionPane.showMessageDialog(this,"Quantum and Context Switch must be integers!" , "Input Error" , JOptionPane.ERROR_MESSAGE);
         return;
      }

      List<Process> processList = new ArrayList<>();
      for(int  i = 0 ; i < inputModel.getRowCount() ; i++){
         String id = inputModel.getValueAt(i , 0).toString();
         int arr = Integer.parseInt(inputModel.getValueAt(i,1).toString());
         int burst = Integer.parseInt(inputModel.getValueAt(i,2).toString());
         int prio =  Integer.parseInt(inputModel.getValueAt(i,3).toString());
         processList.add(new Process(id,arr,burst,prio));
      }

      PrintStream originalOut = System.out;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      try {
         if(cmbAlgorithm.getSelectedIndex() == 0){
            ACS acs = new ACS(processList, q, cs);
            acs.run();
         }
         else{
            PreemptiveSJF sjf = new PreemptiveSJF(processList,cs);
            sjf.run();
         }
      }
      catch (Exception ex) {
         System.setOut(originalOut);
         JOptionPane.showMessageDialog(this,"Simulation Error: " + ex.getMessage(), "Error" , JOptionPane.ERROR_MESSAGE);
         return;
      }
      finally {
         System.setOut(originalOut);
      }
      String fullOutput = baos.toString();
      parseAndDisplayOutput(fullOutput);
   }
   private  void parseAndDisplayOutput(String output){
      simulationBlocks.clear();
      metricsModel.setRowCount(0);
      String[] parts = output.split("(?m)^(?:Process Metrics:|Scheduling Metrics:)");
      String timelinePart = parts[0].trim();
      String metricsPart = parts.length > 1 ? parts[1].trim() : "";

      String[] rawSteps = timelinePart.split("(?=\\nTime[:\\s]+\\d+)");
      for(String step:rawSteps){
         if(!step.trim().isEmpty()){
            simulationBlocks.add(step.trim());
         }
      }
      currentBlockIndex = 0;
      logArea.setText("");
      showAllBlocks();

      if(!metricsPart.isEmpty()){
         double totalRT = 0;
         int count = 0;

         String[] lines = metricsPart.split("\\R");
         Pattern rowPattern = Pattern.compile("^(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)$");

         for(String line:lines){
            line = line.trim();
            Matcher m = rowPattern.matcher(line);
            if(m.matches()){
               String pid = m.group(1);
               int ct = Integer.parseInt(m.group(2));
               int tat = Integer.parseInt(m.group(3));
               int wt = Integer.parseInt(m.group(4));
               int rt = Integer.parseInt(m.group(5));

               metricsModel.addRow(new Object[]{pid,ct,tat,wt,rt});
               totalRT += rt;
               count++;
            }
            else if(line.startsWith("Average Waiting Time:")){
               lblAvgWT.setText(line);
            }
            else if (line.startsWith("Average Turnaround Time:")){
               lblAvgTAT.setText(line);
            }
            else if(line.startsWith("Average Response Time:")){
               lblAvgRT.setText(line);
            }
         }

         if(cmbAlgorithm.getSelectedIndex() == 0 && count > 0){
            lblAvgRT.setText(String.format("Average Response Time: %.2f", totalRT / count));
         }
      }
   }

   private  void resetStepView(){
      currentBlockIndex = 0;
      logArea.setText("");
      lblCurrentStep.setText("Progress : 0 / " + simulationBlocks.size() + "  ");
   }

   private void showNextBlock(){
      if(currentBlockIndex < simulationBlocks.size()){
         if(currentBlockIndex > 0){
            logArea.append("\n-------------------------------------------------\n");
         }
         logArea.append(simulationBlocks.get(currentBlockIndex));
         currentBlockIndex++;
         lblCurrentStep.setText("Progress: " + currentBlockIndex + " / " + simulationBlocks.size() + "  ");
      }
   }

   private void showAllBlocks(){
      while (currentBlockIndex < simulationBlocks.size()){
         showNextBlock();
      }
   }

   public static void main(String[] args){
      SwingUtilities.invokeLater(() -> new SchedulerGUI().setVisible(true));
   }
}
