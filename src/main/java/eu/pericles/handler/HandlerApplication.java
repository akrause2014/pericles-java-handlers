package eu.pericles.handler;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.owlike.genson.ext.jaxrs.GensonJsonConverter;

public class HandlerApplication {

	public static void main(String[] args) 
	{
		URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build();
		ResourceConfig config = new ResourceConfig(HandlerResource.class);
		config.register(GensonJsonConverter.class);
		System.out.println("Starting HTTP server ...");
		JdkHttpServerFactory.createHttpServer(baseUri, config);
	}

}
