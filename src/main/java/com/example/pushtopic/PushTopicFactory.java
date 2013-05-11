package com.example.pushtopic;

import com.force.sdk.streaming.client.ForceBayeuxClient;
import com.force.sdk.streaming.client.ForceStreamingClientModule;
import com.force.sdk.streaming.client.PushTopicManager;
import com.force.sdk.streaming.exception.ForceStreamingException;
import com.force.sdk.streaming.model.PushTopic;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class PushTopicFactory {
	
	public Injector createInjector()
	{
		Injector injector = Guice.createInjector(new ForceStreamingClientModule());
		return injector;
	}

	public ForceBayeuxClient createClient(Injector injector)
	{
		  ForceBayeuxClient client = injector.getInstance(ForceBayeuxClient.class);
		  return client;
	}
	
	public PushTopicManager createPushTopManager(Injector injector)
	{
		PushTopicManager pushTopicManager = injector.getInstance(PushTopicManager.class);
		return pushTopicManager;
	}
	
	public PushTopic getTopicByName(PushTopicManager pushTopicManager, String topicName) throws ForceStreamingException
	{
		PushTopic topic = pushTopicManager.getTopicByName(topicName);
		return topic;
	}
	
	public PushTopic createPushTopic(PushTopicManager pushTopicManager, String name, Double apiVersion, String query, String description)
	{
		PushTopic createTopic = pushTopicManager.createPushTopic(new PushTopic(name, apiVersion, query, description));
		return createTopic;
	}
	
	public PushTopic pushTopicFactory(String topicName) throws ForceStreamingException
	{
		PushTopicFactory pushTopicFactory = new PushTopicFactory();
		Injector injector = pushTopicFactory.createInjector();
		pushTopicFactory.createClient(injector);
		PushTopicManager publicTopicManager = pushTopicFactory.createPushTopManager(injector);
		
		return pushTopicFactory.getTopicByName(publicTopicManager, topicName); 
	}
	
	public ForceBayeuxClient createPushTopicClientFactory()
	{
		PushTopicFactory pushTopicClientFactory = new PushTopicFactory();
		ForceBayeuxClient fbClient = pushTopicClientFactory.createClient(pushTopicClientFactory.createInjector());
		return fbClient;
	}
	
}
