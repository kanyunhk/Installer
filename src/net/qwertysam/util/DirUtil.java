package net.qwertysam.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DirUtil
{
	private static final String USERNAME = System.getProperty("user.name");
	public static final String SEP = File.separator;
	private static final String WINDOWS_7_PATH = "C:" + SEP + "Users" + SEP + USERNAME + SEP + "AppData" + SEP
			+ "Roaming" + SEP + ".minecraft";
	private static final String WINDOWS_XP_PATH = "C:" + SEP + "Documents and Settings" + SEP + USERNAME + SEP
			+ "Application Data" + SEP + ".minecraft";
	private static final String LINUX_PATH = SEP + "home" + SEP + USERNAME + SEP + ".minecraft";
	private static final String MAC_PATH = SEP + "Users" + SEP + USERNAME + SEP + "Library" + SEP + "Application Support" + SEP + "minecraft";

	private static String selectedPath = null;
	

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
		return formatPath(getVersionsPath() + SEP + VersionsUtil.getSelectedVersion());
	}

	public static String getSelectedVersionJarPath()
	{
		return formatPath(getSelectedVersionPath() + SEP + VersionsUtil.getSelectedVersion() + ".jar");
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
	
	public static void copyFolder(String inDir, String outDir)
	{
		File inFile = new File(inDir);
		File outFile = new File(outDir);
		
		try
		{
			if (inFile.isDirectory())
			{

				// if directory not exists, create it
				if (!outFile.exists())
				{
					outFile.mkdir();
				}

				// list all the directory contents
				String files[] = inFile.list();

				for (String file : files)
				{
					// construct the src and dest file structure
					File srcFile = new File(inFile, file);
					File destFile = new File(outFile, file);
					// recursive copy
					copyFolder(srcFile.getPath(), destFile.getPath());
				}
			}
			else
			{
				// if file, then copy it
				// Use bytes stream to support all file types
				InputStream in = new FileInputStream(inFile);
				OutputStream out = new FileOutputStream(outFile);
				
				byte[] buffer = new byte[1024];

				
				int length;
				// copy the file content in bytes
				while ((length = in.read(buffer)) > 0)
				{
					out.write(buffer, 0, length);
				}

				in.close();
				out.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
