package eu.pericles.handler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class StreamOutput implements Callable<String>
{

	private InputStream mInput;

	public StreamOutput(InputStream input)
	{
		mInput = input;
	}
	
	@Override
	public String call() throws Exception 
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(mInput));
		String line = null;
		StringBuilder result = new StringBuilder();
		while ((line = reader.readLine()) != null)
		{
			result.append(line);
			result.append("\n");
		}
		return result.toString();
	}
	
}
