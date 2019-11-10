package com.bundle.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//import com.lakeba.compilation.ui.MainWindow;

public class ProcessResultReader extends Thread
{
	final InputStream is;
	final String type;
	final StringBuilder sb;
	//final MainWindow win;

	public ProcessResultReader( final InputStream is,  String type)
	{
		this.is = is;
		this.type = type;
		this.sb = new StringBuilder();
		//win = mwin;
	}

	public void run()
	{
		try 
		{
			final InputStreamReader isr = new InputStreamReader(is);
			final BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
			{
				this.sb.append(line).append("\n");
				System.out.println(type+line);
			}
		}
		catch (final IOException ioe)
		{
			System.err.println(ioe.getMessage());
			System.out.println("Exceptin in "+type);
			throw new RuntimeException(ioe);
		}
	}

	@Override
	public String toString()
	{
		return this.sb.toString();
	}
}