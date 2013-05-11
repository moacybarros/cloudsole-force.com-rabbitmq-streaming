# Force.com Streaming API tests with Jetstream and Srping MVC

This is a template for a web application that uses Spring MVC and Jetstream to stablish a proxy to utilize the Force.com Streaming API.


## Step 1: Create an Object

The first step is to create an InvoiceStatement object. After you create a PushTopic and subscribe to it, you’ll get notifications when an InvoiceStatement record is created or updated. You’ll create the object with the user interface.

  1. Click Your Name > Setup > Create > Objects.

  2. Click New Custom Object and fill in the custom object definition.
    * In the Label field, type Invoice Statement.
    * In the Plural Label field, type Invoice Statements.
    * Select Starts with vowel sound.
    * In the Record Name field , type Invoice Number.
    * In the Data Type field , select Auto Number.
    * In the Display Format field, type INV-{0000}.
    * In the Starting Number field, type 1.

  3. Click Save.

  4. Add a Status field.
    1. Scroll down to the Custom Fields & Relationships related list and click New.
    2. For Data Type, select Picklist and click Next.
    3. In the Field Label field, type Status.
    4. Type the following picklist values in the box provided, with each entry on its own line.


      Open
      Closed
      Negotiating
      Pending

    5. Select the checkbox for Use first value as default value.
    6. Click Next.
    7. For field-level security, select Read Only and then click Next.
    8. Click Save & New to save this field and create a new one.


  5. Now create an optional Description field.
    1. In the Data Type field, select Text Area and click Next.
    2. In the Field Label and Field Name fields, enter Description.
    3. Click Next, accept the defaults, and click Next again.
    4. Click Save to go the detail page for the Invoice Statement object.


  Your InvoiceStatement object should now have two custom fields.


## Step 2: Create a PushTopic

Use the System Log to create the PushTopic record that contains a SOQL query. Events notifications are generated for updates that match the query. Alternatively, you can also use Workbench to create a PushTopic.

  1. Select Your Name > Developer Console.
  2. On the Logs tab, click Execute.
  3. In the Enter Apex Code window, paste in the following Apex code, and click Execute.

    PushTopic pushTopic = new PushTopic();

    pushTopic.Name = 'InvoiceStatementUpdates';

    pushtopic.Query = 'SELECT Id, Name, Status\_\_c, Description\_\_c FROM Invoice\_Statement\_\_c';

    pushTopic.ApiVersion = 24.0;

    pushTopic.NotifyForOperations = 'All';

    pushTopic.NotifyForFields = 'Referenced';

    insert pushTopic;

Because NotifyForOperations is set to All, Streaming API evaluates records that are created or updated and generates a notification if the record matches the PushTopic query. Because NotifyForFields is set to Referenced, Streaming API will use fields in both the SELECT clause and the WHERE clause to generate a notification. Whenever the fields Name, Status__c, or Description__c are updated, a notification will be generated on this channel.


## Step 3: Subscribe to the PushTopic Channel


Add this to your view:


    <script src="/resources/js/jquery-1.7.1.min.js"></script>
    <script src="/resources/js/json2.js"></script>
    <script src="/resources/js/cometd.js"></script>
    <script src="/resources/js/jquery.cometd.js"></script>
    <script type="text/javascript">
      (function($) {
        var cometd = $.cometd;

        $(document).ready(function() {
          function _metaHandshake(handshake) {
            if (handshake.successful === true) {
              cometd.batch(function() {
                  cometd.subscribe('/force/InvoiceStatementUpdates', function(messages) {
                    $("#messages").append('<pre>' + JSON.stringify(messages) + '</pre>');
                  });
              });
            }
          }

          // Disconnect when the page unloads
          $(window).unload(function() {
              cometd.disconnect(true);
          });

          var cometURL = location.protocol + "//" + location.host + "/cometd";
          cometd.configure({
              url: cometURL
          });

          cometd.addListener('/meta/handshake', _metaHandshake);

          cometd.handshake();
        });
      })(jQuery);
    </script>


## Running the application locally

- On Linux/Mac:

        $ export FORCE_FORCEDATABASE_URL="force://<instance>.salesforce.com;user=<username>;password=<password+security_token>"

- On Windows:

        $ set FORCE_FORCEDATABASE_URL="force://<instance>.salesforce.com;user=<username>;password=<password+security_token>"

Build with:

    $ mvn clean install

Then run it with:

    $ java -jar target/dependency/webapp-runner.jar target/*.war


## Running on Heroku

Clone this project locally:

    $ git clone https://github.com/dangt85/template-sfdc-java-springmvc.git

Create a new app on Heroku (make sure you have the [Heroku Toolbelt](http://toolbelt.heroku.com) installed):

    $ heroku login
    $ heroku create -s cedar

Add config params for authenticating to Salesforce.com (replace the values with the ones from the Remote Access definition on Salesforce.com):

    $ heroku config:add FORCE_FORCEDATABASE_URL="force://<instance>.salesforce.com;user=<username>;password=<password+security_token>"

Upload the app to Heroku:

    $ git push heroku master

Open the app in your browser:

    $ heroku open
