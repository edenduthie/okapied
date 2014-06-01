package okapied.service;

import java.util.Map;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

import okapied.entity.CurrencyCode;
import okapied.util.Configuration;

import org.apache.log4j.Logger;

public class OkapiedParserCallback extends ParserCallback 
{
	public static final Logger log = Logger.getLogger(OkapiedParserCallback.class);
	
	ExchangeRatesService service;
	
	boolean inTd = false;
	CurrencyCode currentCode = null;
	int tagNumber = 0;
	int usdTagNo;
	
	Map<String,CurrencyCode> map;
	
	public OkapiedParserCallback(ExchangeRatesService service)
	{
		this.service = service;
		this.map = service.getCurrencyCodeService().getCurrencyMap();
		usdTagNo = Configuration.instance().getIntProperty("USD_TAG_NO");
	}
	
	@Override
	public void handleComment(char[] data,int pos)
	{
		
	}
	
	@Override
	public void handleEndTag(HTML.Tag t,int pos)
	{
//		System.out.println("End tag: " + t.toString());
		if( t==HTML.Tag.TD && inTd ) inTd = false;
	}
	
	@Override
	public void handleError(String errorMsg,int pos)
	{
//		System.out.println("Error: " + errorMsg);
	}
	
	@Override
	public void handleSimpleTag(HTML.Tag t,MutableAttributeSet a,int pos)
	{
//		System.out.println("Simple tag: " + toString().toString());
	}
	
	@Override
	public void handleStartTag(HTML.Tag t,MutableAttributeSet a,int pos)
	{
//		System.out.println("Start tag: " + t.toString());
		if( t==HTML.Tag.TD ) inTd = true;
		else inTd = false;
	}
	
	@Override
	public void handleText(char[] data,int pos)
	{
		StringBuffer text = new StringBuffer();
		text.append(data);
		String textString = text.toString().trim();
		
		if( inTd )
		{
			if( map.containsKey(textString) )
			{
				currentCode = map.get(textString);
				tagNumber = 0;
			}
			else
			{
				++tagNumber;
				if( (tagNumber == usdTagNo) && currentCode != null )
				{
					try
					{
					    Float usd = new Float(textString);
					    currentCode.setUsd(usd);
					    service.getCurrencyCodeService().update(currentCode);
					}
					catch( NumberFormatException e )
					{
						log.error(e);
						log.error("Failed to update currency: " + currentCode.getCode());
					}
					currentCode = null;
				}
			}
		}
	}
}
