<html>
<head>
<title>Authenticate using U2F</title>

<script src="/assets/u2f-api-1.1.js"></script>

<script>
var request = ${data};
setTimeout(function() {
    if (request.signRequests.length > 0) {
        u2f.sign(
            request.appId,
            request.challenge,
            request.signRequests,
            function(data) {
                if(data.errorCode) {
                    switch (data.errorCode) {
                        case 4:
                            alert("This device is not registered for this account.");
                            break;

                        default:
                            alert("U2F failed with error code: " + data.errorCode);
                    }
                } else {
                    document.getElementById('tokenResponse').value = JSON.stringify(data);
                    document.getElementById('u2f-form').submit();
                }
            }
        );
    }
}, 1000);
</script>

</head>
    <body>

    <#list data.getSignRequests() as signRequests>
      <p>Touch your U2F token to authenticate.</p>
          <form method="POST" id="u2f-form">
              <input type="hidden" name="tokenResponse" id="tokenResponse"/>
              <input type="hidden" name="username" id="username" value="${username}"/>
          </form>
        <#break>
    <#else>
        <p>No devices are registered for this account.</p>
    </#list>
    </body>
</html>
