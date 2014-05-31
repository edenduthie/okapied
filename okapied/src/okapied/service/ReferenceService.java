package okapied.service;

import java.util.List;

import okapied.dao.DAO;
import okapied.entity.CurrencyCode;
import okapied.entity.Flooring;
import okapied.entity.OkaOption;
import okapied.entity.PropertyType;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ReferenceService 
{
	DAO dao;
	
    public List<PropertyType> getAllPropertyTypes()
    {
    	return dao.getAll(PropertyType.class.getName());
    }
    
    public List<CurrencyCode> getAllCurrencyCodes()
    {
    	return dao.getAll(CurrencyCode.class.getName());
    }
    
    public List<Flooring> getAllFlooring()
    {
    	return dao.getAll(Flooring.class.getName());
    }
    
    public List<OkaOption> getAllOptions()
    {
    	return dao.getAll(OkaOption.class.getName());
    }

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}
}
