<!doctype html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Streaming Tests</title>
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
                  cometd.subscribe('/force/AccountPush', function(messages) {
                    $("#messages").append('<pre>' + JSON.stringify(messages, null, 4) + '</pre>');
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
  </head>
  <body>

    <div id="messages">messages should appear here</div>

  </body>
</html>
