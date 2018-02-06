package NEO.Network.Rpc;

import java.io.*;
import java.net.*;

import NEO.IO.Json.*;

public class RpcClient
{
	private final URL url;
	
	public RpcClient(String url) throws MalformedURLException
	{
		this.url = new URL(url);
	}
	
	public JObject call(String method, JObject ...params) throws RpcException, IOException
	{
		JObject response = send(makeRequest(method, params));
		if (response.containsProperty("result"))
			return response.get("result");
		else if (response.containsProperty("error"))
			throw new RpcException((int)response.get("error").get("code").asNumber(), response.get("error").get("message").asString());
		else
			throw new IOException();
	}
	
	private static JObject makeRequest(String method, JObject[] params)
	{
		JObject request = new JObject();
		request.set("jsonrpc", new JString("2.0"));
		request.set("method", new JString(method));
		request.set("params", new JArray(params));
		request.set("id", new JNumber(Math.random()));
		return request;
	}
	
	private JObject send(JObject request) throws IOException
	{
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		try (OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream()))
		{
			w.write(request.toString());
		}
		try (InputStreamReader r = new InputStreamReader(connection.getInputStream()))
		{
			return JObject.parse(r);
		}
	}
}
