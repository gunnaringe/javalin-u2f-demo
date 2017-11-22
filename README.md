# Javalin U2F Demo

This is based on the demo Java implementation by Yubico

# U2F requires https connection to work

U2F will not work if the site is not hosted on HTTPS.

You could test it on localhost by adding a https proxy.
```shell
sudo apt install stunnel
cd stunnel; stunnel stunnel.conf
```