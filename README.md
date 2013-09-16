# Force.com Streaming API with Rabbit MQ

This is a Salesforce Streaming API java implementation push subcribed messages to Rabbit MQ queue.

##Step 1: Create PushTopic:

    PushTopicFactory pushTopicFactory = new PushTopicFactory();
    Injector injector = pushTopicFactory.createInjector();
    ForceBayeuxClient client = pushTopicFactory.createClient(injector);
    PushTopicManager publicTopicManager = pushTopicFactory.createPushTopManager(injector);
    PushTopic createTopic = pushTopicFactory.createPushTopic(publicTopicManager, "NewAccountPushTopic", 27.0, "select Id, Name from Account", "New Push Topic");
	
##Step 2: Create Rabbit Queue:

    final RabbitStream rabbitFactory = new RabbitStream();
    final Channel factoryChannel = rabbitFactory.createRabbitFactory(HOSTNAME, QUEUENAME);
		
## Step 3: Subscribe to the Topic and put publish to Rabbit Queue

    client.subscribeTo(topic, new ClientSessionChannel.MessageListener() {   
      public void onMessage(ClientSessionChannel channel, Message message) {
        try {
            rabbitFactory.basicPublish(factoryChannel, QUEUENAME, message.getJSON());
            log.info(message.getJSON());
        } catch (IOException e) {
            log.error(e);
            e.printStackTrace();
        } 
      }
    });
			
##Step 4: Putting it all together:

    private static final String TOPICNAME = "CaseStream";
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
		}


## Running the application locally

- On Linux/Mac:

        $ export FORCE_FORCEDATABASE_URL="force://<instance>.salesforce.com;user=<username>;password=<password+security_token>"
        OR
        Update forceDatbase.properties with your salesforce url, user and password.

Run:

    Run StreamMain.java

