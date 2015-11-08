package net.qwertysam.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class VersionsUtil
{
	public static final List<String> DEFAULT_VERSIONS = new ArrayUtil<String>().toList(new String[]{
			"1.8",
	
	});
	
	public static List<String> getValidVersions()
	{
		List<String> versions = new ArrayList<String>();

		File mcDir = new File(DirUtil.getVersionsPath());

		FilenameFilter dirsOnly = new FilenameFilter()
		{
			@Override
			public boolean accept(File current, String name)
			{
				return new File(current, name).isDirectory();
			}
		};

		if (mcDir.isDirectory())
		{
			for (File dir : mcDir.listFiles(dirsOnly))
			{
				boolean hasJar = false;
				boolean hasJson = false;

				// Validates the version to see if it has a jar and json matching the dir name
				for (String fileName : dir.list())
				{
					if (fileName.contains(dir.getName() + ".jar")) hasJar = true;
					if (fileName.contains(dir.getName() + ".json")) hasJson = true;
				}

				if (hasJar && hasJson)
				{
					versions.add(dir.getName());
				}
			}
		}

		return versions;
	}

	public static boolean hasVersions()
	{
		return !VersionsUtil.getValidVersions().isEmpty();
	}

	private static String selectedVersion = null;
	
	public static String getSelectedVersion()
	{
		return selectedVersion;
	}

	public static void setSelectedVersion(String version)
	{
		selectedVersion = version;
	}
	
	public static boolean isVersionDefault(String version)
	{
		for (String defVersion : DEFAULT_VERSIONS)
		{
			if (version.equals(defVersion)) return true;
		}
		return false;
	}
}
