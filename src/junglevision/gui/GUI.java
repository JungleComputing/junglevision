package junglevision.gui;

import javax.swing.JPanel;
import javax.swing.JMenuBar;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;

public class GUI extends JPanel {
	private final ButtonGroup ibisesGroup = new ButtonGroup();
	private final ButtonGroup locationsGroup = new ButtonGroup();
	private final ButtonGroup metricsGroup = new ButtonGroup();

	/**
	 * Create the panel.
	 */
	public GUI() {
		setLayout(new BorderLayout(0, 0));
		
		JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);
		
		JMenu mnMonitoring = new JMenu("Monitoring");
		menuBar.add(mnMonitoring);
		
		JCheckBoxMenuItem chckbxmntmEnableMonitoring = new JCheckBoxMenuItem("Enable Monitoring");
		chckbxmntmEnableMonitoring.setSelected(true);
		mnMonitoring.add(chckbxmntmEnableMonitoring);
		
		JCheckBoxMenuItem chckbxmntmLinkMonitoring = new JCheckBoxMenuItem("Link Monitoring");
		chckbxmntmLinkMonitoring.setSelected(true);
		mnMonitoring.add(chckbxmntmLinkMonitoring);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		JMenu mnLocations = new JMenu("Locations");
		mnView.add(mnLocations);
		
		JRadioButtonMenuItem rdbtnmntmCityscape = new JRadioButtonMenuItem("CityScape");
		locationsGroup.add(rdbtnmntmCityscape);
		rdbtnmntmCityscape.setSelected(true);
		mnLocations.add(rdbtnmntmCityscape);
		
		JRadioButtonMenuItem rdbtnmntmSphere = new JRadioButtonMenuItem("Sphere");
		locationsGroup.add(rdbtnmntmSphere);
		mnLocations.add(rdbtnmntmSphere);
		
		JRadioButtonMenuItem rdbtnmntmCircle = new JRadioButtonMenuItem("Circle");
		locationsGroup.add(rdbtnmntmCircle);
		mnLocations.add(rdbtnmntmCircle);
		
		JMenu mnIbises = new JMenu("Ibises");
		mnView.add(mnIbises);
		
		JRadioButtonMenuItem rdbtnmntmCityscape_1 = new JRadioButtonMenuItem("CityScape");
		ibisesGroup.add(rdbtnmntmCityscape_1);
		mnIbises.add(rdbtnmntmCityscape_1);
		
		JRadioButtonMenuItem rdbtnmntmSphere_1 = new JRadioButtonMenuItem("Sphere");
		ibisesGroup.add(rdbtnmntmSphere_1);
		rdbtnmntmSphere_1.setSelected(true);
		mnIbises.add(rdbtnmntmSphere_1);
		
		JRadioButtonMenuItem rdbtnmntmCircle_1 = new JRadioButtonMenuItem("Circle");
		ibisesGroup.add(rdbtnmntmCircle_1);
		mnIbises.add(rdbtnmntmCircle_1);
		
		JMenu mnMetrics = new JMenu("Metrics");
		mnView.add(mnMetrics);
		
		JRadioButtonMenuItem rdbtnmntmBars = new JRadioButtonMenuItem("Bars");
		rdbtnmntmBars.setSelected(true);
		metricsGroup.add(rdbtnmntmBars);
		mnMetrics.add(rdbtnmntmBars);
		
		JRadioButtonMenuItem rdbtnmntmTubes = new JRadioButtonMenuItem("Tubes");
		metricsGroup.add(rdbtnmntmTubes);
		mnMetrics.add(rdbtnmntmTubes);
		
		JRadioButtonMenuItem rdbtnmntmSpheres = new JRadioButtonMenuItem("Spheres");
		metricsGroup.add(rdbtnmntmSpheres);
		mnMetrics.add(rdbtnmntmSpheres);

	}

}
