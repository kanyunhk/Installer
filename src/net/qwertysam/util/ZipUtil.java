package net.qwertysam.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class ZipUtil
{
	public static void unzipFile(String inFile, String outDir, JLabel labelToUpdate, JProgressBar progressBar)
	{
		unzipFile(false, inFile, outDir, labelToUpdate, progressBar);
	}

	public static void unzipFile(boolean newThread, String inFile, String outDir, JLabel labelToUpdate,
			JProgressBar progressBar)
	{
		if (newThread)
		{
			Runnable run = new Runnable()
			{
				@Override
				public void run()
				{
					unzipProcedure(inFile, outDir, labelToUpdate, progressBar);
				}
			};

			Thread t = new Thread(run);
			t.start();
		}
		else
		{
			unzipProcedure(inFile, outDir, labelToUpdate, progressBar);
		}
	}

	private static void unzipProcedure(String inFile, String outDir, JLabel labelToUpdate, JProgressBar progressBar)
	{
		try
		{
			byte[] buffer = new byte[1024];

			File dir = new File(outDir);
			if (!dir.exists()) dir.mkdir();

			ZipInputStream zis = new ZipInputStream(new FileInputStream(inFile));

			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null)
			{
				File newFile = new File(outDir + File.separator + entry.getName());

				labelToUpdate.setText("Unzipping: " + entry.getName());
				System.out.println("Unzipping: " + newFile.getAbsolutePath());

				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);
				int i = 0;
				while ((i = zis.read(buffer)) > 0)
				{
					fos.write(buffer, 0, i);
				}
				fos.close();
			}

			zis.closeEntry();
			zis.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		labelToUpdate.setText("Unzipping: Finished");
	}
}
