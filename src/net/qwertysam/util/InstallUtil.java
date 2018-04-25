package net.qwertysam.util;

import java.io.File;

import net.qwertysam.resource.IUpdatableFrame;

public class InstallUtil
{
	public static void installMod(final IUpdatableFrame frame)
	{
		frame.setWorking(true);

		Runnable run = new Runnable()
		{
			@Override
			public void run()
			{
				installProcedure(frame);
			}
		};

		Thread t = new Thread(run);
		t.start();
	}

	private static void installProcedure(IUpdatableFrame frame)
	{
		// If the version is default, make a copy before moving the mod files.
		if (VersionsUtil.isVersionDefault(VersionsUtil.getSelectedVersion()))
		{
			String newSelectedVersion = VersionsUtil.getSelectedVersion() + "-MCM-2-3";
			String newVersionPath = DirUtil.getVersionsPath() + DirUtil.SEP + newSelectedVersion;

			DirUtil.copyFolder(DirUtil.getSelectedVersionPath(), newVersionPath);

			VersionsUtil.setSelectedVersion(newSelectedVersion);
		}

		File json = null;

		for (File file : FileUtil.getFiles(DirUtil.getSelectedVersionPath()))
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

			FileUtil.renameFile(DirUtil.getSelectedVersionPath() + DirUtil.SEP + file.getName(),
					DirUtil.getSelectedVersionPath() + DirUtil.SEP + newName);
		}

		if (json == null)
		{
			System.out.println("[CRITIAL] Could not locate version json.");
		}
		else
		{
			JsonUtil.formatJsonID(json, VersionsUtil.getSelectedVersion());
			JsonUtil.formatJsonInheritance(json);
			JsonUtil.formatJsonDownloads(json);
		}

		// Creates inheritance files
		String newDir = DirUtil.getVersionsPath() + DirUtil.SEP + JsonUtil.INHERITANCE;
		File dir = new File(newDir);
		dir.mkdir();

		File vanillaJson = new File(DirUtil.getVersionsPath() + DirUtil.SEP + "1.12.2" + DirUtil.SEP + "1.12.2.json");
		if (vanillaJson.exists()) // Use the original 1.12.2 file if it can
		{
			File moddedJson = new File(newDir + DirUtil.SEP + "MCM-1.12.2-JSON.json");
			
			JsonUtil.formatJsonDownloads(vanillaJson, moddedJson);
			JsonUtil.formatJsonID(moddedJson, JsonUtil.INHERITANCE);
		}
		else // Else use the one packages in this jar. MAY CAUSE ERRORS IF MINECRAFT UPDATE RENDERS THIS INCOMPATABLE
		{
			JarUtil.moveFromThisJarToPath("/MCM-1.12.2-JSON.json", newDir);
		}

		JarUtil.moveFromThisJarToThatJar(ModFiles.MOD_FILES, DirUtil.getSelectedVersionJarPath());

		frame.setWorking(false);
	}
}
