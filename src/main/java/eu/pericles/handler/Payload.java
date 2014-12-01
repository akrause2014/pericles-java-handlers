package eu.pericles.handler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Payload implements Callable<String>
{
	private List<String> mCommandWithParams;
	private volatile String mStatus;
	private String mUUID;
	private Map<String, Object> mJson;
	private String mCommand;
	private List<? extends Object> mParameters;
	
	public Payload(String command, List<? extends Object> parameters)
	{
		mCommand = command;
		mParameters = parameters;
		mCommandWithParams = new ArrayList<String>();
		mCommandWithParams.add(command);
		for (Object p : parameters)
		{
			mCommandWithParams.add(p.toString());
		}
		mUUID = UUID.randomUUID().toString();
		mStatus = "pending";
		mJson = new HashMap<String, Object>();
		mJson.put("id", mUUID.toString());
		mJson.put("cmd", command);
		mJson.put("params", parameters);
		mJson.put("status", mStatus);

	}

	public Payload(String command, String... parameters)
	{
		this(command, Arrays.asList(parameters));
	}
	
	@SuppressWarnings("unchecked")
	public Payload(Map<String, Object> payload) 
	{
		this((String)payload.get("cmd"), 
				payload.containsKey("params")? (List<Object>)payload.get("params") : Collections.emptyList());
	}

	public Map<String, Object> asMap() 
	{
		mJson.put("status", mStatus);
		return mJson;
	}
	
	public String getUUID() {
		return mUUID;
	}
	
	public String getStatus()
	{
		return mStatus;
	}
	
	@Override
	public String call() throws Exception
	{
		mStatus = "running";
		ProcessBuilder pb = new ProcessBuilder(mCommandWithParams);
		Process proc = null;
		try
		{
			proc = pb.start();
		}
		catch (Throwable e)
		{
			mStatus = "error: " + e.getMessage();
			return mStatus;
		}
		ExecutorService service = Executors.newFixedThreadPool(2);
		Future<String> stderr = service.submit(new StreamOutput(proc.getErrorStream()));
		Future<String> stdout = service.submit(new StreamOutput(proc.getInputStream()));
		try
		{
			String errors = stderr.get();
			mStatus = "error: " + errors;
		} 
		catch (Throwable e)
		{
			// just logging any errors here
			e.printStackTrace();
		}
		try
		{
			try
			{
				stdout.get();
			}
			catch (InterruptedException e)
			{
				// ignore
			}
			proc.waitFor();
			if (proc.exitValue() == 0)
			{
				mStatus = "completed";
			}
			else
			{
				mStatus = "error";
			}
		} 
		catch (ExecutionException e)
		{
			mStatus = "error: " + e.getCause().getMessage();
		}
		catch (Throwable e)
		{
			mStatus = "error: " + e.getMessage();
		}
//		System.out.println("Completed " + mUUID + " with status: " + mStatus);
		return mStatus;
	}

	@Override
	public String toString() 
	{
		StringBuilder result = new StringBuilder();
		result.append("Payload: { ");
		result.append("\n   id : ").append(mUUID.toString());
		result.append("\n   command : '").append(mCommand).append("'");
		result.append("\n   params: ").append(mParameters);
		result.append("\n   status: ").append(mStatus);
		result.append("\n}");
		return result.toString();
	}
	
	private static class StreamOutput implements Callable<String>
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


}
