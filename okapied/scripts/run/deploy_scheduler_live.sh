rm -rf /var/okapied/*
cp -r /root/code/okapied/okapied/*  /var/okapied
cd /var/okapied
mv /var/okapied/src/okapied/service/paypal_sdk_client.properties /var/okapied/src/okapied/service/paypal_sdk_client.properties.dev
mv /var/okapied/src/okapied/service/paypal_sdk_client.properties.live /var/okapied/src/okapied/service/paypal_sdk_client.properties
mv /var/okapied/src/okapied.properties /var/okapied/src/okapied.properties.dev
mv /var/okapied/src/okapied.properties.live /var/okapied/src/okapied.properties
rm -rf /var/okapied/test
rm -rf /var/okapied/testdata
rm -rf /var/okapied/dist
cd -
