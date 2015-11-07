package net.qwertysam.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class VersionsUtil
{
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
}
