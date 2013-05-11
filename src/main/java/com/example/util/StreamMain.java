package com.example.util;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import com.rabbitmq.client.Channel;
import com.example.pushtopic.PushTopicFactory;
import com.example.rabbit.RabbitStream;
import com.force.sdk.streaming.client.ForceBayeuxClient;
import com.force.sdk.streaming.client.PushTopicManager;
import com.force.sdk.streaming.exception.ForceStreamingException;
import com.force.sdk.streaming.model.PushTopic;
import com.google.inject.Injector;

public class StreamMain {
	
	private static final String TOPICNAME = "AccountPush";
	private static final String QUEUENAME = "streaming";
	private static final String HOSTNAME= "localhost";
	
	public static void main(String[] args) throws ForceStreamingException, InterruptedException, Exception {
			
		   final Log log = LogFactory.getLog(StreamMain.class);
		
			final RabbitStream rabbitFactory = new RabbitStream();
			final Channel factoryChannel = rabbitFactory.createRabbitFactory(HOSTNAME, QUEUENAME);
		
			PushTopicFactory pushTopicFactory = new PushTopicFactory();
			Injector injector = pushTopicFactory.createInjector();
			ForceBayeuxClient client = pushTopicFactory.createClient(injector);
			PushTopicManager publicTopicManager = pushTopicFactory.createPushTopManager(injector);
			PushTopic createTopic = pushTopicFactory.createPushTopic(publicTopicManager, "NewAccountPushTopic", 27.0, "select Id, Name from Account", "New Push Topic");
	
			PushTopic topic = pushTopicFactory.getTopicByName(publicTopicManager, createTopic.getName()); 
			
			client.subscribeTo(topic, new ClientSessionChannel.MessageListener() 
			{   
				public void onMessage(ClientSessionChannel channel, Message message) 
				{
					try {
						rabbitFactory.basicPublish(factoryChannel, QUEUENAME, message.getJSON());
						log.info(message.getJSON());
					} catch (IOException e) {
						log.error(e);
						e.printStackTrace();
					} 
				}
			});
			
			
			/*Injector injector = Guice.createInjector(new ForceStreamingClientModule());
	        ForceBayeuxClient client = injector.getInstance(ForceBayeuxClient.class);
	        PushTopicManager pushTopicManager = injector.getInstance(PushTopicManager.class);
	        PushTopic topic = pushTopicManager.getTopicByName(TOPICNAME);
	        client.subscribeTo(topic, new ClientSessionChannel.MessageListener() {
	        
	        public void onMessage(ClientSessionChannel channel, Message message) 
	        {
					try {
							rabbitFactory.basicPublish(factoryChannel, QUEUENAME, message.getJSON());
							log.info(message.getJSON());
						} catch (IOException e) {
							log.error(e);
							e.printStackTrace();
						} 
	            }
	        });
	        */
	}

}
