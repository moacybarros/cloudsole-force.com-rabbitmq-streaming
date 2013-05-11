package com.example.rabbit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitStream {
	
	public ConnectionFactory createRabbitConnection(String hostname)
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(hostname);
		return factory;
	}
	
	public Connection createConnection(ConnectionFactory rabbitFactory) throws IOException
	{
		Connection rabbitconnection = rabbitFactory.newConnection();
		return rabbitconnection;
	}
	
	public Channel createChannel(Connection rabbitConnection, String queueName) throws IOException
	{
		final Channel rabbitChannel = rabbitConnection.createChannel();
		rabbitChannel.queueDeclare(queueName, false, false, false, null);
		return rabbitChannel;
	}
	
	public void basicPublish(Channel rabbitChannel, String queueName, String message) throws IOException
	{
		rabbitChannel.basicPublish("", queueName, null, message.getBytes());
	}
	
	public QueueingConsumer consumeChannel(Channel rabbitChannel, String queueName) throws IOException
	{
	    final QueueingConsumer rabbitconsumer = new QueueingConsumer(rabbitChannel);
	    rabbitChannel.basicConsume(queueName, true, rabbitconsumer);
	    return rabbitconsumer;
	}
	

	public List<String> consumeQueue(QueueingConsumer rabbitConsumer) throws ShutdownSignalException, ConsumerCancelledException, InterruptedException
	{
		List<String> messageList = new ArrayList<String>();
		while (true) {
		      QueueingConsumer.Delivery delivery = rabbitConsumer.nextDelivery();
		      String message = new String(delivery.getBody());
		      messageList.add(message);
		}
	}
	
	public Channel createRabbitFactory(String hostName, String queueName) throws IOException
	{
		RabbitStream mainStream = new RabbitStream();
		return mainStream.createChannel(mainStream.createConnection(mainStream.createRabbitConnection(hostName)), queueName);
	}

}
