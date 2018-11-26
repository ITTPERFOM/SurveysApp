package com.timetracker.business;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpMethods {

	public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

	OkHttpClient client = new OkHttpClient();

	public String post(String url, String json, String token) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder()
				.addHeader("Accept","application/json")
				.addHeader("Content-type","application/json")
				.addHeader("Authorization-Token",token)
				.url(url)
				.post(body)
				.build();
		try (Response response = client.newCall(request).execute()) {
			if(response.code() == 200){
				return response.body().string();
			}else{
			return "Error: No se pudo realizar conexion a servicio. Informacion:" + response.body().string();
			}
		}
	}
}