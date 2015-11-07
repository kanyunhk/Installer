package net.qwertysam.gui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import net.qwertysam.util.ZipUtil;

public class Form1 extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1232567466340122367L;

	public static final int MAX_ENTRIES = 20;
	public static final int PANEL_SIZE_X = 300;
	public static final int PANEL_SIZE_Y = 150;

	private JFrame frame;
	private Container cont;

	private JButton start;
	private JProgressBar progress;
	private JLabel progressLabel;

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

		// The start button
		start = new JButton("Start");
		start.setBounds(0, 0, 120, 32);
		start.addActionListener(this);
		cont.add(start);

		// The progress bar
		progress = new JProgressBar(0, 100);
		progress.setBounds(10, PANEL_SIZE_Y - 54, PANEL_SIZE_X - 26, 15);
		cont.add(progress);

		// The progress bar label
		progressLabel = new JLabel("Click Start");
		progressLabel.setBounds(progress.getX(), progress.getY() - 18, 300, 20);
		cont.add(progressLabel);

		frame.add(cont);
		frame.setVisible(true);
	}

	public void setWorking(boolean isWorking)
	{
		this.isWorking = isWorking;

		if (isWorking)
		{
			start.setEnabled(false);
		}
		else
		{
			start.setEnabled(true);
		}
	}

	public boolean isWorking()
	{
		return isWorking;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(start))
		{
			setWorking(true);
			System.out.println("hnng");
			ZipUtil.unzipFile(true, "C:/Users/qwertysam/AppData/Roaming/.minecraft/versions/1.8/1.8.jar", "C:/Users/qwertysam/Desktop/temp", progressLabel, progress);
		}
	}
}
