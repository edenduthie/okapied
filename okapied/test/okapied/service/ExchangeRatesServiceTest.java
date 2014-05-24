package okapied.service;

import java.io.IOException;

import okapied.BaseTest;
import okapied.entity.CurrencyCode;
import okapied.util.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ExchangeRatesServiceTest extends BaseTest
{
    @Autowired ExchangeRatesService exchangeRatesService;
    
    @Test
    public void testUpdateExchangeRatesUSD() throws IOException
    {
    	CurrencyCode currencyCode = Generator.currencyCode();
    	exchangeRatesService.getCurrencyCodeService().persist(currencyCode);
    	
    	exchangeRatesService.updateExchangeRates();
    	
    	CurrencyCode usd = exchangeRatesService.getCurrencyCodeService().getByCode(currencyCode.getCode());
    	Assert.assertNotNull(usd);
    	Assert.assertEquals(usd.getUsd(),1.0f);
    	
    	exchangeRatesService.getCurrencyCodeService().delete(currencyCode);
    }
}
