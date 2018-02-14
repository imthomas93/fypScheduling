package fypScheduling;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

public class GUI extends JFrame implements ActionListener {

	GeneticAlgorithm ga;
	StimulatedAnnealing sa;
	private final String title = "FYP Scheduler";
	private String[] dataSet = {"Test", "Small", "Medium", "Large"};
	
	// GUI components
	JPanel mainPanel, sub1, sub2, main1, main2;
	static JTextArea textbox;
	JScrollPane scrollV;
	JButton loadDB, viewFac, viewFacAllocation, viewCourses, viewEvents, viewGrouping, viewVenues, viewHistory, facAllocation, courseAllocation;
	JComboBox inputDataDDB;
	JTextField mutationProbabilityTextField;
	JTextField crossoverProbabilityTextField;
	JTextField populationSizeTextField;
	JTextField selectionSizeTextField;
	JTextField aplhaTextField;
	JTextField temperatureTextField;


	public GUI() {
		ga = new GeneticAlgorithm();
		sa = new StimulatedAnnealing();
		init();
	
	}
	
	private void init() {
		mainPanel = new JPanel(new BorderLayout());
		sub1 = new JPanel();
		sub2 = new JPanel();
		
		// Buttons
		loadDB = new JButton("Setup");
		viewFac = new JButton("View Faculty");
		viewFacAllocation = new JButton("View Faculty Allocation");
		viewCourses = new JButton("View Courses");
		viewEvents = new JButton("View Events");
		viewGrouping = new JButton("View Groupings");
		viewVenues = new JButton("View Venues");
		viewHistory = new JButton("View History");
		facAllocation = new JButton("Faculty Allocation Setup");
		courseAllocation = new JButton("Course Allocation Setup");
	   
		// Drop Down List
		inputDataDDB = new JComboBox(dataSet);
	    inputDataDDB.setSelectedIndex(0);
	    
	   	// GUI Console 
		textbox	= new JTextArea(50,50);
	   	textbox.setEditable(false);
	   	DefaultCaret caret = (DefaultCaret)textbox.getCaret();
	    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	   	scrollV = new JScrollPane(textbox);	
	   	scrollV.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	  	    

	   	// Field
	   	aplhaTextField = new JTextField("0.99", 30);
	   	temperatureTextField = new JTextField("100", 30);
	    mutationProbabilityTextField = new JTextField("50", 30);
	    crossoverProbabilityTextField = new JTextField("50", 30);
	    populationSizeTextField = new JTextField("100", 30);
	    selectionSizeTextField = new JTextField("30", 30);	
	   
	    // Panel Layout
	   	sub1 = new JPanel(new GridLayout(7,2,0,0)); 
	    sub1.add(new JLabel("Input file DropdownBox"));
	    sub1.add(inputDataDDB);
	    sub1.add(new JLabel("Alpha Value"));
	    sub1.add(aplhaTextField);
	    sub1.add(new JLabel("Initial Temperature"));
	    sub1.add(temperatureTextField);
	    sub1.add(new JLabel("Mutation probability"));
	    sub1.add(mutationProbabilityTextField);
	    sub1.add(new JLabel("Crossover probability"));
	    sub1.add(crossoverProbabilityTextField);
	    sub1.add(new JLabel("Population size"));    
	    sub1.add(populationSizeTextField);
	    sub1.add(new JLabel("Culled population size"));    
	    sub1.add(selectionSizeTextField);
	    
	   	sub2 = new JPanel(new GridLayout(5,2,10,10));
	   	sub2.add(loadDB);
	   	sub2.add(viewCourses);
	   	sub2.add(viewFac);
	   	sub2.add(viewFacAllocation);
	   	sub2.add(viewGrouping);
	   	sub2.add(viewVenues);
	   	sub2.add(viewHistory);
	   	sub2.add(viewEvents);
	   	sub2.add(facAllocation);
	   	sub2.add(courseAllocation);
	   	
	   	viewCourses.setEnabled(false);
	   	viewFac.setEnabled(false);
	   	viewFacAllocation.setEnabled(false);
	   	viewGrouping.setEnabled(false);
	   	viewVenues.setEnabled(false);
	   	viewHistory.setEnabled(false);
	   	viewEvents.setEnabled(false);
	   	facAllocation.setEnabled(false);
	   	courseAllocation.setEnabled(false);

	   	
	   	main1 = new JPanel(new GridLayout(2,1));
	   	main1.add(sub1, BorderLayout.CENTER);
	   	main1.add(sub2, BorderLayout.SOUTH);
	   	main1.setSize(150, 100);
	   	
	   	main2 = new JPanel(new GridLayout(1,1,0,0));
	   	main2.add(scrollV);
	   	

	   	mainPanel = new JPanel(new BorderLayout());
	   	mainPanel.add(main1, BorderLayout.WEST);
	   	mainPanel.add(main2, BorderLayout.CENTER);
	   	
	   	add(mainPanel);
	   	setTitle(title);
	   	setSize(1500, 500);
	    setLocationRelativeTo(null);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    
	    
	    // setup button action listener
	    loadDB.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent event) {
	  		sa.loadDatabase(inputDataDDB.getSelectedItem().toString());
	  		loadDB.setEnabled(false);
	  		sa.printSetupConfig();
		   	viewCourses.setEnabled(true);
		   	viewFac.setEnabled(true);
		   	viewFacAllocation.setEnabled(true);
		   	viewGrouping.setEnabled(true);
		   	viewVenues.setEnabled(true);
		   	viewEvents.setEnabled(true);
		   	viewHistory.setEnabled(true);
		   	facAllocation.setEnabled(true);
		   	courseAllocation.setEnabled(false);
		   	textbox.append("Database from SQL Loaded!");
	      }
	    });	
	    
	    viewCourses.addActionListener(new ActionListener() {
		      @Override
		      public void actionPerformed(ActionEvent event) {
		    	  	textbox.setText(null);
		    	  	String text = sa.printCourses();
		    	  	textbox.append(text);
		      }
		    });	
	    
	    viewFac.addActionListener(new ActionListener() {
		      @Override
		      public void actionPerformed(ActionEvent event) {
		    	  	textbox.setText(null);
		    	  	String text = sa.printFacultyPreference();
		    	  	textbox.append(text);
		      }
		    });
	    
	    
	    viewFacAllocation.addActionListener(new ActionListener() {
		      @Override
		      public void actionPerformed(ActionEvent event) {
		    	  	textbox.setText(null);
		    	  	String text = sa.printFaculty();
		    	  	textbox.append(text);
		      }
		    });
	    
	    viewGrouping.addActionListener(new ActionListener() {
		      @Override
		      public void actionPerformed(ActionEvent event) {
		    	  	textbox.setText(null);
		    	  	String text = sa.printGrouping();
		    	  	textbox.append(text);
		      }
		    });
	    
	    viewVenues.addActionListener(new ActionListener() {
		      @Override
		      public void actionPerformed(ActionEvent event) {
		    	  	textbox.setText(null);
		    	  	String text = sa.printVenues();
		    	  	textbox.append(text);
		      }
		    });
	    
	    viewHistory.addActionListener(new ActionListener() {
		      @Override
		      public void actionPerformed(ActionEvent event) {
		    	  	textbox.setText(null);
		    	  	String text = sa.printHistory();
		    	  	textbox.append(text);
		      }
		    });
	    
	    viewEvents.addActionListener(new ActionListener() {
		      @Override
		      public void actionPerformed(ActionEvent event) {
		    	  	textbox.setText(null);
		    	  	String text = sa.printEvents();
		    	  	textbox.append(text);
		      }
		    });
	    
	    viewEvents.addActionListener(new ActionListener() {
		      @Override
		      public void actionPerformed(ActionEvent event) {
		    	  	textbox.setText(null);
		    	  	String text = sa.printHistory();
		    	  	textbox.append(text);
		      }
		    });
	    
	    facAllocation.addActionListener(new ActionListener() {
		      @Override
		      public void actionPerformed(ActionEvent event) {
		    	  	sa.setAlpha(Double.parseDouble(aplhaTextField.getText()));
		    	  	sa.setTemperture(Double.parseDouble(temperatureTextField.getText()));

		    	  	sa.runSAAlgo();
		 
		    	  	facAllocation.setEnabled(false);
		    	  	viewEvents.setEnabled(true);
		    	  	courseAllocation.setEnabled(true);
		      }
		    });
	   
	    courseAllocation.addActionListener(new ActionListener() {
		      @Override
		      public void actionPerformed(ActionEvent event) {
		    	  	ga.setMutationProb(Integer.parseInt(mutationProbabilityTextField.getText()));
				ga.setCrossoverProb(Integer.parseInt(crossoverProbabilityTextField.getText()));
				ga.setPopulationSize(Integer.parseInt(populationSizeTextField.getText()));
				ga.loadDatabase(sa);
		    	  	courseAllocation.setEnabled(false);
		    	  	
		  		Timetable bestTimetable = ga.generateTimetable();
				ga.printTimetable(bestTimetable);
		      }
		    });
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
