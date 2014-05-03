rm -rf /var/okapied/*
cp -r /root/code/okapied/okapied/*  /var/okapied
cd /var/okapied
mv /var/okapied/src/okapied.properties /var/okapied/src/okapied.properties.dev
mv /var/okapied/src/okapied.properties.prod /var/okapied/src/okapied.properties
rm -rf /var/okapied/test
rm -rf /var/okapied/testdata
rm -rf /var/okapied/dist
cd -
