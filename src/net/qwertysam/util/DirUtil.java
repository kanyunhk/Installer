package net.qwertysam.util;

import java.io.File;

public class DirUtil
{
	private static final String USERNAME = System.getProperty("user.name");
	public static final String SEP = File.separator;
	private static final String WINDOWS_7_PATH = "C:" + SEP + "Users" + SEP + USERNAME + SEP + "AppData" + SEP
			+ "Roaming" + SEP + ".minecraft";
	private static final String WINDOWS_XP_PATH = "C:" + SEP + "Documents and Settings" + SEP + USERNAME + SEP
			+ "Application Data" + SEP + ".minecraft";
	private static final String LINUX_PATH = "~" + SEP + ".minecraft";
	private static final String MAC_PATH = "~" + SEP + "Library" + SEP + "Application Support" + SEP + "minecraft"; // TODO find out what it is

	private static String selectedPath = null;
	private static String selectedVersion = null;

	public static final String MC_DIR_NOT_FOUND = "NOT FOUND";
	
	private static String getDefaultMinecraftPath()
	{
		switch (OsUtil.getOSType())
		{
			case WINDOWS:
				if (new File(WINDOWS_7_PATH).exists()) return WINDOWS_7_PATH;
				else if (new File(WINDOWS_XP_PATH).exists()) return WINDOWS_XP_PATH;
				break;
			case OSX:
				if (new File(MAC_PATH).exists()) return MAC_PATH;
				break;
			case LINUX:
				if (new File(LINUX_PATH).exists()) return LINUX_PATH;
				break;
			default:
				break;
		}
		return MC_DIR_NOT_FOUND;
	}

	public static String getSelectedPath()
	{
		if (selectedPath == null)
		{
			setSelectedPath(getDefaultMinecraftPath());
		}

		return selectedPath;
	}

	public static String getSelectedVersionPath()
	{
		return formatPath(getVersionsPath() + SEP + selectedVersion);
	}

	public static String getTempDirPath()
	{
		return formatPath(getSelectedPath() + SEP + "work_temp");
	}

	public static String getSelectedVersionJarPath()
	{
		return formatPath(getSelectedVersionPath() + SEP + getSelectedVersion() + ".jar");
	}

	public static String getSelectedVersion()
	{
		return selectedVersion;
	}

	public static void setSelectedVersion(String version)
	{
		selectedVersion = version;
	}

	public static String getVersionsPath()
	{
		return getSelectedPath() + SEP + "versions";
	}

	public static void setSelectedPath(String path)
	{
		selectedPath = formatPath(path);
	}

	public static String formatPath(String path)
	{
		// Replaces any formatting with the requred formatting
		path = path.replace("\\", SEP);
		path = path.replace("/", SEP);

		// Removes any separators from the end of a path
		if (path.endsWith(SEP))
		{
			path = path.substring(0, path.length() - 1);
		}

		return path;
	}

	public static boolean deleteDir(File file)
	{
		if (file.isDirectory())
		{
			String[] children = file.list();
			for (int i = 0; i < children.length; i++)
			{
				boolean success = deleteDir(new File(file, children[i]));
				if (!success)
				{
					return false;
				}
			}
		}
		return file.delete();
	}
}
