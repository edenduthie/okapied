package okapied.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import okapied.dao.DAO;
import okapied.entity.CurrencyCode;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CurrencyCodeService 
{
	public DAO dao;
	
	/**
	 * If a currency with the given code exists in the database, it is returned, otherwise null 
	 * is returned.
	 */
	public CurrencyCode getByCode(String code)
	{
		String queryString = "from CurrencyCode where code=:currencyCode";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("currencyCode",code);
		List<CurrencyCode> results = query.getResultList();
		if( results.size() > 0 ) return results.get(0);
		else return null;
	}
	
	public Map<String,CurrencyCode> getCurrencyMap()
	{
		Map<String,CurrencyCode> map = new HashMap<String,CurrencyCode>();
		
		String queryString = "from CurrencyCode";
		Query query = dao.getEntityManager().createQuery(queryString);
		List<CurrencyCode> results = query.getResultList();

        for( CurrencyCode code : results )
        {
        	map.put(code.getCode(),code);
        }
		
		return map;
	}
	
	public void update(CurrencyCode currencyCode)
	{
		dao.update(currencyCode);
	}
	
	public void persist(CurrencyCode code)
	{
		dao.persist(code);
	}
	
	public void delete(CurrencyCode code)
	{
		dao.delete(CurrencyCode.class.getName(), "id", code.getId());
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}
}
