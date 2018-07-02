package com.techyugadi.reactive.rxsse;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

import com.launchdarkly.eventsource.MessageEvent;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;

import java.net.URI;
import java.util.Properties;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSEObservable {
	
	private static final Logger log = 
			LoggerFactory.getLogger(SSEObservable.class);
	
	private String sseURL;
	private String completionString;
	
	EventSource eventSource;
	
	public SSEObservable(Properties sseProps) throws ConfigurationException {
		
		if (sseProps != null) {
			sseURL = sseProps.getProperty("sseURL");
			if (sseURL == null) {
				throw new ConfigurationException(
						"URL for server sent events must be specified");
			}
			
			completionString = sseProps.getProperty("completionString");
			
		} else {
			throw new ConfigurationException(
					"Missing Configuration Properties");
		}
	}
	
	public Observable<MessageEvent> retrieveObservable() {
		
		Observable<MessageEvent> observable = 
		    Observable.create(emitter -> {
						
		    	EventHandler listener = new EventHandler() {
							
		    		@Override
		    		public void onOpen() throws Exception {
		    		  log.info("onOpen");
		    		}

		    		@Override
		    		public void onClosed() throws Exception {
		    			log.info("onClosed");
		    		}

		    		@Override
		    		public void onMessage(String event, 
		    				MessageEvent messageEvent) throws Exception {
		    		  log.info("onMessage");
		    		  emitter.onNext(messageEvent);
		    		}

		    		@Override
		    		public void onComment(String comment) throws Exception {
		    			log.info("onComment: " + comment);
		    			if (comment != null && completionString != null &&
		    					Charset.forName("ISO-8859-1").newEncoder()
		    											   .canEncode(comment) &&
		    					comment.indexOf(completionString) != -1)
		    				emitter.onComplete();
		    		}

		    		@Override
		    		public void onError(Throwable t) {
		    			log.info("onError: " + t);
		    			emitter.onError(t);
		    		}
		    		
				};
				
			    EventSource.Builder builder = 
			    		new EventSource.Builder(listener, URI.create(sseURL));

			    if (eventSource == null)
			    	eventSource = builder.build();
			    eventSource.start();
						
		});
		
		return observable;
		
	}
	
	public Flowable<MessageEvent> retrieveFlowable(
			BackpressureStrategy strategy) {

		return retrieveObservable().toFlowable(strategy);

	}
	
	public void cleanup() {
		
		if (eventSource == null)
			eventSource.close();
		
	}

}
