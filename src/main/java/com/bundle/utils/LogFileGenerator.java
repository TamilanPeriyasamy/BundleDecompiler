package com.bundle.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFileGenerator extends OutputStream {

	OutputStream[] outputStreams;
	public static String Current_Date_Time=null;
	public static int days_Count=0;
	public static File logFile=null;

	static FileOutputStream fout_ferr=null;

	public LogFileGenerator(OutputStream... outputStreams)
	{
		this.outputStreams= outputStreams; 
	}

	@Override
	public void write(int b) throws IOException
	{
		for (OutputStream out: outputStreams)
			out.write(b);			
	} 

	@Override
	public void write(byte[] b) throws IOException
	{
		for (OutputStream out: outputStreams)
			out.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		for (OutputStream out: outputStreams)
			out.write(b, off, len);
	}

	@Override
	public void flush() throws IOException
	{
		for (OutputStream out: outputStreams)
			out.flush();
	}

	@Override
	public void close() throws IOException
	{
		for (OutputStream out: outputStreams)
			out.close();
	}


	public static void toStartWriteLogFile(){

		try    
		{  
			boolean fileAppendMode=false;
			File logFileDir=new File(System.getProperty("user.dir")+File.separator+"Logs"+File.separator);
			if(!logFileDir.exists()){
				logFileDir.mkdirs();
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			Current_Date_Time=dateFormat.format(date);
			System.out.println("Current_Date_Time "+Current_Date_Time);
			String logFileName="Log_"+Current_Date_Time.substring(0, Current_Date_Time.indexOf(" "))+".txt";

			logFile=new File(logFileDir,logFileName);
			if(!logFile.exists()){ 
				logFile.createNewFile();	
			} 
			fout_ferr= new FileOutputStream(logFile.getAbsoluteFile(),fileAppendMode);

			PrintStream  stdout= new PrintStream(new LogFileGenerator(System.out, fout_ferr)); 
			PrintStream  stderr= new PrintStream(new LogFileGenerator(System.err, fout_ferr));
     
			System.setOut(stdout); 
			System.setErr(stderr);  

		}catch (IOException ex){ 

			ex.printStackTrace();  

		}  
	}
	
	public static void toStopWriteLogFile() throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		Current_Date_Time=dateFormat.format(date);
		System.out.println("Current_Date_Time "+Current_Date_Time);
		fout_ferr.close();
	}
}