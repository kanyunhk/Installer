package net.qwertysam.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class DownloadUtil
{
	public static void downloadFile(String url, String outFile, JLabel labelToUpdate, JProgressBar progressBar)
	{
		downloadFile(false, url, outFile, labelToUpdate, progressBar);
	}

	public static void downloadFile(boolean newThread, String url, String outFile, JLabel labelToUpdate,
			JProgressBar progressBar)
	{
		if (newThread)
		{
			Runnable run = new Runnable()
			{
				@Override
				public void run()
				{
					downloadProcedure(url, outFile, labelToUpdate, progressBar);
				}
			};

			Thread t = new Thread(run);
			t.start();
		}
		else
		{
			downloadProcedure(url, outFile, labelToUpdate, progressBar);
		}
	}

	private static void downloadProcedure(String url, String outFile, JLabel labelToUpdate, JProgressBar progressBar)
	{
		try
		{
			File file = new File(outFile);
			URL urlObj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

			int fileSize = connection.getContentLength();
			float totalDataRead = 0;

			BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
			FileOutputStream out = new FileOutputStream(outFile);
			BufferedOutputStream buf = new BufferedOutputStream(out, 1024);

			System.out.println("[" + System.currentTimeMillis() + "]: Connected to file \"" + file.getName() + "\"");

			byte[] data = new byte[1024];
			int i = 0;
			while ((i = in.read(data, 0, 2014)) > 0)
			{
				totalDataRead++;
				buf.write(data, 0, i);

				// Updates the label text
				float percent = (totalDataRead / fileSize) * 100;
				progressBar.setValue((int) percent);
				labelToUpdate.setText("Downloading: " + file.getName() + " (" + (int) percent + ")");
			}

			buf.close();
			out.close();
			in.close();
			progressBar.setValue(100);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
