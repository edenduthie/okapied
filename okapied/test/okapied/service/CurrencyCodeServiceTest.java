package okapied.service;

import java.util.Map;

import okapied.BaseTest;
import okapied.entity.CurrencyCode;
import okapied.util.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CurrencyCodeServiceTest extends BaseTest
{
    @Autowired CurrencyCodeService currencyCodeService;
    
    @Test
    public void testGetByCode()
    {
    	CurrencyCode currencyCode = Generator.currencyCode();
    	currencyCodeService.persist(currencyCode);
    	
    	CurrencyCode result = currencyCodeService.getByCode(currencyCode.getCode());
    	Assert.assertNotNull(result);
    	
    	currencyCodeService.delete(currencyCode);
    }
    
    @Test
    public void testGetByCodeNull()
    {
    	CurrencyCode currencyCode = Generator.currencyCode();
    	currencyCodeService.persist(currencyCode);
    	
    	CurrencyCode result = currencyCodeService.getByCode(currencyCode.getCode()+"yaya");
    	Assert.assertNull(result);
    	
    	currencyCodeService.delete(currencyCode);
    }
    
    @Test
    public void testGetCurrencyMap()
    {
    	CurrencyCode currencyCode = Generator.currencyCode();
    	currencyCodeService.persist(currencyCode);
    	
    	Map<String,CurrencyCode> map = currencyCodeService.getCurrencyMap();
    	Assert.assertTrue(map.containsKey(currencyCode.getCode()));
    	
    	currencyCodeService.delete(currencyCode);
    }
}
