package com.techyugadi.reactive.rxsse.sample;

import java.util.Properties;
import io.reactivex.Observable;
import com.launchdarkly.eventsource.MessageEvent;
import com.techyugadi.reactive.rxsse.SSEObservable;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class SimpleApp {
	
	public static void main(String[] args) throws Exception {
		
		String sseURL = "https://stream.wikimedia.org/v2/stream/recentchange";
		Properties sseProps = new Properties();
		sseProps.setProperty("sseURL", sseURL);
		
		SSEObservable sseObservable = new SSEObservable(sseProps);
		
		Observable<MessageEvent> observable = sseObservable.retrieveObservable();
		
		observable.subscribe(
				msg -> {
					System.out.println("RECEIVED MESSAGE: ");
					try (JsonReader jsonReader = 
							Json.createReader(new StringReader(msg.getData()))) {
				
						JsonObject jsonObject = jsonReader.readObject();
						JsonValue title = jsonObject.getValue("/title");
						JsonValue changeType = jsonObject.getValue("/type");
						System.out.println(changeType.toString() + " : " + title.toString());
			      
					}
				},
				err -> {
					System.out.println("RECEIVED ERROR:" + err.toString());
					err.printStackTrace();
				}
		);
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
	      public void run() {
	        System.out.println("Cleaning up SSE Resources");
	        sseObservable.cleanup();
	      }
	    });
		
		Thread.sleep(99000);
		System.exit(0);
		
	}

}
