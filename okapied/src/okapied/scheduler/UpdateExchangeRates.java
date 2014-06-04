package okapied.scheduler;

import java.io.IOException;

import okapied.service.ExchangeRatesService;
import okapied.util.Configuration;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class UpdateExchangeRates
{
	static Logger log = Logger.getLogger(UpdateExchangeRates.class);
	
	ExchangeRatesService exchangeRatesService;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		UpdateExchangeRates updater = new UpdateExchangeRates();
		long millis = new Double(Math.random()*Configuration.instance().getIntProperty("RANDOM_MILLIS_TO_WAIT_EXCHANGE")).longValue();
		System.out.println("Waiting: " + ((double) millis)/(1000*60) + " minutes");
		try 
		{
			Thread.sleep(millis);
		} 
		catch (InterruptedException e) 
		{
			log.error(e);
		}
		updater.run();
	}
	
	public void run()
	{
		log.info("Updating Exchange Rates");
		try 
		{
			exchangeRatesService.updateExchangeRates();
			log.info("Finished updating exchange rates");
		} 
		catch (IOException e) 
		{
			log.error("Error downloading exchange rate info");
			log.error(e);
		}
	}
	
	public UpdateExchangeRates()
	{
		String appContextPath = "WebContent/WEB-INF/applicationContext.xml";
		ApplicationContext appCon = new FileSystemXmlApplicationContext(appContextPath);
		setExchangeRatesService((ExchangeRatesService)appCon.getBean("exchangeRatesService"));
	}

	public ExchangeRatesService getExchangeRatesService() {
		return exchangeRatesService;
	}

	public void setExchangeRatesService(ExchangeRatesService exchangeRatesService) {
		this.exchangeRatesService = exchangeRatesService;
	}

}
