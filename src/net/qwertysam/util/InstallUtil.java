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
		
		// Deletes the META-INF directory
		if (!DirUtil.deleteDir(new File(DirUtil.getTempDirPath() + DirUtil.SEP + "META-INF"))) System.out.println("[CRITICAL] Failed to delete the META-INF directory.");
		
		// Brings the mod files from inside the jar to the temp folder
		FileUtil.getFileFromInsideJar("/mod/mod_files.zip", DirUtil.getTempDirPath());
		
		// Unzips the mod files to the temp directory
		ZipUtil.unzipFile(DirUtil.getTempDirPath() + DirUtil.SEP + "mod_files.zip", DirUtil.getTempDirPath(), labelToUpdate, progressBar);
		
		// Deletes the mod zip before recompressing the jar
		new File(DirUtil.getTempDirPath() + DirUtil.SEP + "mod_files.zip").delete();
		
		// Zips selected jar 
		ZipUtil.zipFile(FileUtil.getFiles(DirUtil.getTempDirPath()), DirUtil.getSelectedVersionJarPath(), labelToUpdate,
				progressBar);
		
		// Deletes the temp directory
		if (!DirUtil.deleteDir(tempDir)) System.out.println("[CRITICAL] Failed to delete the temporary directory.");
		
		labelToUpdate.setText("Finished Installing!");
		frame.setWorking(false);
	}
}
