mysqldump --user=okapied --password=mippy666 okapied > /data/okapied.sql
cd /data
rm -rf /data/okapied.tar.gz
tar -czvf /data/okapied.tar.gz okapied.sql
rm -rf /data/okapied.sql