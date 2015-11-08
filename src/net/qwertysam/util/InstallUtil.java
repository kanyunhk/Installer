package net.qwertysam.util;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import net.qwertysam.resource.IUpdatableFrame;

public class InstallUtil
{
	public static void installMod(IUpdatableFrame frame, JLabel labelToUpdate, JProgressBar progressBar)
	{
		frame.setWorking(true);

		Runnable run = new Runnable()
		{
			@Override
			public void run()
			{
				installProcedure(frame, labelToUpdate, progressBar);
			}
		};

		Thread t = new Thread(run);
		t.start();
	}

	private static void installProcedure(IUpdatableFrame frame, JLabel labelToUpdate, JProgressBar progressBar)
	{
		progressBar.setValue(0);

		FileUtil.moveFromThisJarToThatJar(ModFiles.MOD_FILES, DirUtil.getSelectedVersionJarPath());

		progressBar.setValue(100);

		labelToUpdate.setText("Finished Installing!");
		frame.setWorking(false);
	}
}
