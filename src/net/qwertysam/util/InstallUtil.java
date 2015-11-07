package net.qwertysam.util;

import java.io.File;

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
		File tempDir = new File(DirUtil.getTempDirPath());
		if (!tempDir.exists()) tempDir.mkdir();

		// Unzips selected jar
		ZipUtil.unzipFile(DirUtil.getSelectedVersionJarPath(), DirUtil.getTempDirPath(), labelToUpdate,
				progressBar);
		
		// Keeps a backup of the original jar
		FileUtil.renameFile(DirUtil.getSelectedVersionJarPath(), DirUtil.getSelectedVersionJarPath() + ".backup");
		
		// Zips selected jar 
		ZipUtil.zipFile(FileUtil.getFiles(DirUtil.getTempDirPath()), DirUtil.getSelectedVersionJarPath(), labelToUpdate,
				progressBar);
		
		// Deleted the temp directory
		if (!DirUtil.deleteDir(tempDir)) System.out.println("[CRITICAL] Failed to delete the temporary directory.");
		
		frame.setWorking(false);
	}
}
