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
		progressBar.setValue(0);

		// If the version is default, make a copy before moving the mod files.
		if (VersionsUtil.isVersionDefault(VersionsUtil.getSelectedVersion()))
		{
			String newSelectedVersion = VersionsUtil.getSelectedVersion() + "-MCM-2";
			String newVersionPath = DirUtil.getVersionsPath() + DirUtil.SEP + newSelectedVersion;
			
			DirUtil.copyFolder(DirUtil.getSelectedVersionPath(), newVersionPath);
			
			VersionsUtil.setSelectedVersion(newSelectedVersion);
			
			File json = null;
			
			for(File file : FileUtil.getFiles(DirUtil.getSelectedVersionPath()))
			{
				String newName = "ERR.or";
				
				if (file.getName().contains(".jar"))
				{
					newName = VersionsUtil.getSelectedVersion() + ".jar";
				}
				if (file.getName().contains(".json"))
				{
					newName = VersionsUtil.getSelectedVersion() + ".json";
					json = new File(DirUtil.getSelectedVersionPath() + DirUtil.SEP + newName);
				}
				
				FileUtil.renameFile(DirUtil.getSelectedVersionPath() + DirUtil.SEP + file.getName(), DirUtil.getSelectedVersionPath() + DirUtil.SEP + newName);
			}
			
			if (json == null)
			{
				System.out.println("[CRITIAL] Could not locate version json.");
			}
			
			JsonUtil.formatJson(json);
			
		}
		
		JarUtil.moveFromThisJarToThatJar(ModFiles.MOD_FILES, DirUtil.getSelectedVersionJarPath());

		progressBar.setValue(100);
		
		labelToUpdate.setText("Finished Installing!");
		frame.setWorking(false);
	}
}
