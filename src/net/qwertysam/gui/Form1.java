package net.qwertysam.gui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import net.qwertysam.resource.IUpdatableFrame;
import net.qwertysam.util.DirUtil;
import net.qwertysam.util.InstallUtil;
import net.qwertysam.util.VersionsUtil;

public class Form1 extends JFrame implements ActionListener, IUpdatableFrame
{
	private static final long serialVersionUID = 1232567466340122367L;

	public static final int MAX_ENTRIES = 20;
	public static final int PANEL_SIZE_X = 350;
	public static final int PANEL_SIZE_Y = 162;

	private JFrame frame;
	private Container cont;

	private JButton start;
	private JButton changeDir;

	private JProgressBar progress;
	private JLabel progressLabel;

	private JTextField mcDir;
	private JLabel mcDirLabel;

	private JComboBox<String> comboBox;
	private JLabel comboBoxLabel;
	private int comboBoxSelected = 0;
	private int previousBoxSelected = comboBoxSelected;

	private boolean isWorking = false;

	public Form1()
	{
		// Instantiates the frame
		frame = new JFrame("MCM Mod Installer");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setSize(PANEL_SIZE_X, PANEL_SIZE_Y);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null); // Starts window in center of screen

		// Instantiates the container
		cont = new Container();
		cont.setLayout(null);

		// The Minecraft dir display
		mcDir = new JTextField(1);
		mcDir.setBounds(10, 18, PANEL_SIZE_X - 26, 20);
		mcDir.setEditable(false); // Only allows for display
		cont.add(mcDir);

		// The Minecraft dir label
		mcDirLabel = new JLabel(".minecraft Directory");
		mcDirLabel.setBounds(mcDir.getX(), mcDir.getY() - 18, 300, 20);
		cont.add(mcDirLabel);

		// Options in the combobox
		comboBox = new JComboBox<String>();
		comboBox.addActionListener(this);
		comboBox.setBounds(10, 59, 90, 28);
		cont.add(comboBox);

		// The Combo box label
		comboBoxLabel = new JLabel("Version");
		comboBoxLabel.setBounds(comboBox.getX(), comboBox.getY() - 18, 300, 20);
		cont.add(comboBoxLabel);

		// The start button
		start = new JButton("Start");
		start.setBounds(240, 56, 94, 32);
		start.addActionListener(this);
		cont.add(start);

		// The change minecraft dir button
		changeDir = new JButton("Change Dir");
		changeDir.setBounds(110, 56, 120, 32);
		changeDir.addActionListener(this);
		cont.add(changeDir);

		// The progress bar
		progress = new JProgressBar(0, 100);
		progress.setBounds(10, PANEL_SIZE_Y - 54, PANEL_SIZE_X - 26, 15);
		cont.add(progress);

		// The progress bar label
		progressLabel = new JLabel("Click Start");
		progressLabel.setBounds(progress.getX(), progress.getY() - 18, 300, 20);
		cont.add(progressLabel);

		update();

		frame.add(cont);
		frame.setVisible(true);
	}

	@Override
	public void update()
	{
		mcDir.setText(DirUtil.getSelectedPath());

		comboBox.removeAllItems();
		for (String entry : VersionsUtil.getValidVersions())
		{
			comboBox.addItem(entry);
		}

		// If there is an error, revert to the previously working index
		if (comboBoxSelected < 0)
		{
			comboBoxSelected = previousBoxSelected;
		}

		comboBox.setSelectedIndex(comboBoxSelected);

		DirUtil.setSelectedVersion(comboBox.getItemAt(comboBoxSelected));

		updateEnabilityOfButtons();
	}

	@Override
	public void setWorking(boolean isWorking)
	{
		this.isWorking = isWorking;

		updateEnabilityOfButtons();
	}

	public void updateEnabilityOfButtons()
	{
		if (isWorking)
		{
			comboBox.setEnabled(false);
			start.setEnabled(false);
			changeDir.setEnabled(false);
		}
		else
		{
			comboBox.setEnabled(true);
			start.setEnabled(true);
			changeDir.setEnabled(true);
		}
	}
	
	@Override
	public boolean isWorking()
	{
		return isWorking;
	}

	private int wait = 0;

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (wait > 1 && !isWorking() && e.getID() == ActionEvent.ACTION_PERFORMED)
		{
			if (e.getSource().equals(start) && start.isEnabled())
			{
				InstallUtil.installMod(this, progressLabel, progress);
			}
			else if (e.getSource().equals(comboBox) && comboBox.isEnabled())
			{
				comboBoxSelected = comboBox.getSelectedIndex();
				update();
			}
			else if (e.getSource().equals(changeDir) && changeDir.isEnabled())
			{

			}
		}
		else
		{
			wait++;
		}
	}
}
