cd /root/code/okapied/okapied; 
nohup java -classpath .:/root/code/okapied/okapied/build/WEB-INF/classes:/root/code/okapied/okapied/lib/* okapied.scheduler.UpdateExchangeRates &> /var/log/okapied_exchange_rates.log &