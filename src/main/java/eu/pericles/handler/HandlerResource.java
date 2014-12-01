package eu.pericles.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/payloads")
public class HandlerResource {
	
	private static Map<String, Payload> mStore = new ConcurrentHashMap<String, Payload>();
	private static ExecutorService mService = Executors.newCachedThreadPool();
	private static Map<String, Future<String>> mJobs = new ConcurrentHashMap<String, Future<String>>();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> listPayloads()
	{
		List<Object> result = new ArrayList<Object>();
		for (Payload p : mStore.values())
		{
			result.add(p.asMap());
		}
		return result;
	}
	
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPayload(@PathParam("id") String id) 
    {
    	Payload payload = mStore.get(id);
    	if (payload != null)
    	{
    		return Response.ok(payload.asMap(), MediaType.APPLICATION_JSON).build();
    	}
    	else
    	{
    		return Response.status(Status.NOT_FOUND).build();
    	}
    	
    }
    
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePayload(@PathParam("id") String id) 
    {
    	Future<String> payload = mJobs.get(id);
    	if (payload != null)
    	{
    		
    		payload.cancel(true);
    		mStore.remove(id);
    		mJobs.remove(id);
    		return Response.ok().build();
    	}
    	else
    	{
    		return Response.status(Status.NOT_FOUND).build();
    	}
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postPayload(Map<String, Object> payloadJson)
    {
    	Payload payload = new Payload(payloadJson);
    	
    	if (mStore.putIfAbsent(payload.getUUID(), payload) == null)
    	{
    		mJobs.put(payload.getUUID(), mService.submit(payload));
    		return Response.status(201).entity(payload.asMap()).type(MediaType.APPLICATION_JSON).build();
    	}
    	else
    	{
    		// ID already exists - shouldn't happen very often
    		return Response.status(Status.FORBIDDEN).build();
    	}
    }
    
}
