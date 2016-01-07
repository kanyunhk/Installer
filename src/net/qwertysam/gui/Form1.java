package net.qwertysam.gui;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	public static final int PANEL_SIZE_Y = 154;

	private JFrame frame;
	private Container cont;

	private JButton start;
	private JButton changeDir;

	private JTextField mcDir;
	private JLabel mcDirLabel;

	private JComboBox<String> comboBox;
	private JLabel comboBoxLabel;
	private int comboBoxSelected = 0;
	private int previousBoxSelected = comboBoxSelected;

	private JLabel title;

	private boolean isWorking = false;

	private JFileChooser fc;

	public Form1()
	{
		// Instantiates the frame
		frame = new JFrame("MCM Mod Installer");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setSize(PANEL_SIZE_X, PANEL_SIZE_Y);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null); // Starts window in centre of screen
		
		// Instantiates the container
		cont = new Container();
		cont.setLayout(null);

		// The Minecraft dir display
		mcDir = new JTextField(1);
		mcDir.setBounds(10, 42, PANEL_SIZE_X - 26, 20);
		mcDir.setEditable(false); // Only allows for display
		cont.add(mcDir);

		// The Minecraft dir label
		mcDirLabel = new JLabel(".minecraft Directory");
		mcDirLabel.setBounds(mcDir.getX(), mcDir.getY() - 18, 300, 20);
		cont.add(mcDirLabel);

		// Options in the combobox
		comboBox = new JComboBox<String>();
		comboBox.addActionListener(this);
		comboBox.setBounds(10, 85, 132, 24);
		cont.add(comboBox);

		// The Combo box label
		comboBoxLabel = new JLabel("Version (Must be in 1.8.0)");
		comboBoxLabel.setBounds(comboBox.getX(), comboBox.getY() - 19, 300, 20);
		cont.add(comboBoxLabel);

		// The start button
		start = new JButton("Start");
		start.setBounds(254, 84, 80, 26);
		start.addActionListener(this);
		cont.add(start);

		// The change minecraft dir button
		changeDir = new JButton("Change Dir");
		changeDir.setBounds(149, 84, 98, 26);
		changeDir.addActionListener(this);
		cont.add(changeDir);
		
		title = new JLabel("MCM Staff Mod 2.1");
		title.setBounds(131, 10, 300, 20);
		title.setFont(Font.decode(mcDirLabel.getFont().getFamily() + "-22"));
		cont.add(title);

		// Create a file chooser
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		update();

		frame.add(cont);
		frame.setVisible(true);
	}

	@Override
	public void update()
	{
		mcDir.setText(DirUtil.getSelectedPath());

		comboBox.removeAllItems();
		List<String> versionEntries = VersionsUtil.getValidVersions();
		if (!versionEntries.isEmpty() && versionEntries != null)
		{
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
		}

		VersionsUtil.setSelectedVersion(comboBox.getItemAt(comboBoxSelected));

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
			boolean hasVersions = VersionsUtil.hasVersions();
			comboBox.setEnabled(hasVersions);
			start.setEnabled(hasVersions && comboBoxSelected >= 0);
			changeDir.setEnabled(true);
		}
	}

	@Override
	public boolean isWorking()
	{
		return isWorking;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (!isWorking())
		{
			if (e.getSource().equals(start) && start.isEnabled())
			{
				InstallUtil.installMod(this);
			}
			else if (e.getSource().equals(comboBox) && comboBox.isEnabled())
			{
				comboBoxSelected = comboBox.getSelectedIndex();
				update();
			}
			else if (e.getSource().equals(changeDir) && changeDir.isEnabled())
			{
				if (!DirUtil.getSelectedPath().equals(DirUtil.MC_DIR_NOT_FOUND))
				{
					fc.setSelectedFile(new File(DirUtil.getSelectedPath()));
				}
				int returnVal = fc.showOpenDialog(cont);

				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();
					System.out.println("[INFO] Selected Path: " + file.getAbsolutePath());
					DirUtil.setSelectedPath(file.getAbsolutePath());
					comboBoxSelected = 0;
					update();
				}
			}
		}
	}
}
