package okapied.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import javax.swing.text.html.HTMLEditorKit;

import okapied.util.HTMLParse;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ExchangeRatesService 
{
	public static final Logger log = Logger.getLogger(ExchangeRatesService.class);
	
	CurrencyCodeService currencyCodeService;

	public void updateExchangeRates() throws IOException
	{
		System.setProperty("http.agent", "");
		
		Calendar now = Calendar.getInstance();
		String baseUrl = "http://www.xe.com/ict/?";
		String urlString = "basecur=USD&historical=false&" 
			+ "month=" + (now.get(Calendar.MONTH) + 1)
		    + "&day=" + now.get(Calendar.DAY_OF_MONTH)
		    + "&year=" + now.get(Calendar.YEAR)
		    + "&sort_by=code&image.x=49&image.y=7&image=Submit";
		
//		String encodedUrl = URLEncoder.encode(urlString,Configuration.instance().getStringProperty("ENCODING"));
		URL url = new URL(baseUrl + urlString);
		URLConnection conn = url.openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		
//		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//		StringBuffer sb = new StringBuffer();
//		String line;
//		while ((line = rd.readLine()) != null)
//		{
//		    sb.append(line);
//		}
//		rd.close();
//		String result = sb.toString();
//		System.out.println(result);
		
		InputStreamReader reader = new InputStreamReader(conn.getInputStream());
		HTMLEditorKit.Parser parse = new HTMLParse().getParser();
		parse.parse(reader,new OkapiedParserCallback(this),true);
	}

	public CurrencyCodeService getCurrencyCodeService() {
		return currencyCodeService;
	}

	public void setCurrencyCodeService(CurrencyCodeService currencyCodeService) {
		this.currencyCodeService = currencyCodeService;
	}
}
