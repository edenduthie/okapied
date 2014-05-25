package okapied.entity;

import java.util.Collection;

import okapied.BaseTest;
import okapied.dao.DAO;
import okapied.service.AccountService;
import okapied.util.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.testng.annotations.Test;

public class PropertyTest extends BaseTest
{
	@Autowired
	DAO dao;
	
	@Autowired
	AccountService accountService;
	
    @Test
    public void testSaveProperty()
    {
    	property = Generator.property();
    	saveProperty();
    	removeProperty();
    }
}
