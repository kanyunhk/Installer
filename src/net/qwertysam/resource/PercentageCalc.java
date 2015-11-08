package net.qwertysam.resource;

import javax.swing.JProgressBar;

public class PercentageCalc
{
	private float current;
	private float max;
	private final int total;

	private final int barPriorValue;

	// The value of the bar prior to being modified by this
	private JProgressBar progressBar;

	/**
	 * @param totalPercentAllocated the weight of this percentage
	 */
	public PercentageCalc(int totalPercentAllocated, JProgressBar progressBar)
	{
		current = 0;
		max = 0;
		total = totalPercentAllocated;
		this.progressBar = progressBar;
		barPriorValue = progressBar.getValue();
	}

	public void setMaxPercent(int maxPercent)
	{
		max = maxPercent;
	}

	public void addPercent()
	{
		addPercent(1);
	}

	public void addPercent(int num)
	{
		current += num;
		updateProgressBar();
	}

	int previousPercent = 0;

	public void updateProgressBar()
	{
		if (totalPercentage() > previousPercent)
		{
			previousPercent = totalPercentage();
			progressBar.setValue(barPriorValue + totalPercentage());
		}
	}

	public int totalPercentage()
	{
		if (max == 0) return 0;
		else
		{
			return (int) ((current / max) * total);
		}
	}
}
