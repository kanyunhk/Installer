package net.qwertysam.util;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class InstallUtil
{
	public static void installMod(JLabel labelToUpdate, JProgressBar progressBar)
	{
		Runnable run = new Runnable()
		{
			@Override
			public void run()
			{
				installProcedure(labelToUpdate, progressBar);
			}
		};

		Thread t = new Thread(run);
		t.start();
	}
	
	private static void installProcedure(JLabel labelToUpdate, JProgressBar progressBar)
	{
		
	}
}
