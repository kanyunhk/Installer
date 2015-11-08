package net.qwertysam.util;

import java.io.File;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import net.qwertysam.resource.IUpdatableFrame;
import net.qwertysam.resource.PercentageCalc;

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
		
		File tempDir = new File(DirUtil.getTempDirPath());
		if (!tempDir.exists()) tempDir.mkdir();

		// Unzips selected jar
		ZipUtil.unzipFile(DirUtil.getSelectedVersionJarPath(), DirUtil.getTempDirPath(), labelToUpdate, new PercentageCalc(40, progressBar));

		// Keeps a backup of the original jar
		labelToUpdate.setText("Storing old jar for backups...");
		FileUtil.renameFile(DirUtil.getSelectedVersionJarPath(), DirUtil.getSelectedVersionJarPath() + ".backup");
		progressBar.setValue(progressBar.getValue() + 2);
		
		// Deletes the META-INF directory
		labelToUpdate.setText("Deleting META-INF");
		if (!DirUtil.deleteDir(new File(DirUtil.getTempDirPath() + DirUtil.SEP + "META-INF")))
			System.out.println("[CRITICAL] Failed to delete the META-INF directory.");
		progressBar.setValue(progressBar.getValue() + 2);

		// Brings the mod files from inside the jar to the temp folder
		labelToUpdate.setText("Delivering mod files");
		FileUtil.getFileFromInsideJar("/mod/mod_files.zip", DirUtil.getTempDirPath());
		progressBar.setValue(progressBar.getValue() + 4);
		
		// Unzips the mod files to the temp directory
		labelToUpdate.setText("Extracting mod files");
		ZipUtil.unzipFile(DirUtil.getTempDirPath() + DirUtil.SEP + "mod_files.zip", DirUtil.getTempDirPath(),
				labelToUpdate, new PercentageCalc(7, progressBar));

		// Deletes the mod zip before recompressing the jar
		labelToUpdate.setText("Deleting mod files archive");
		new File(DirUtil.getTempDirPath() + DirUtil.SEP + "mod_files.zip").delete();
		progressBar.setValue(progressBar.getValue() + 2);

		// Zips selected jar
		ZipUtil.zipFile(FileUtil.getFiles(DirUtil.getTempDirPath()), DirUtil.getSelectedVersionJarPath(), labelToUpdate,
				new PercentageCalc(38, progressBar));

		// Deletes the temp directory
		labelToUpdate.setText("Deleting the temporary directory");
		if (!DirUtil.deleteDir(tempDir)) System.out.println("[CRITICAL] Failed to delete the temporary directory.");
		
		progressBar.setValue(100);
		
		labelToUpdate.setText("Finished Installing!");
		frame.setWorking(false);
	}
}
