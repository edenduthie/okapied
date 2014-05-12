package okapied.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import okapied.entity.Photo;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DAO {
	EntityManager em;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
	   this.em = entityManager;
    }
    
    public EntityManager getEntityManager()
    {
    	return em;
    }
    
    public void persist(Object o)
    {
    	em.persist(o);
    }
    
    public void create(Object o)
    {
    	update(o);
    }
    
    public void update(Object o)
    {
    	em.merge(o);
    }
    
    public Object get(String className, String idField, Object id)
    {
    	String queryString = "FROM " + className + " where " + idField +
    	   "=:id";
    	Query query = em.createQuery(queryString);
    	query.setParameter("id",id);
    	return query.getSingleResult();
    }
    
    public void remove(Object o)
    {
    	em.remove(o);
    }
    
    public void deleteAll(String className)
    {
    	String queryString = "DELETE FROM " + className;
    	Query query = em.createQuery(queryString);
    	query.executeUpdate();
    }
    
    public List getAll(String className)
    {
    	String queryString = "from " + className;
    	Query query = em.createQuery(queryString);
    	return query.getResultList();
    }
    
    public void delete(String className, String idField, Object id)
    {
    	String queryString = "FROM " + className + " where " + idField +
 	       "=:id";
 	    Query query = em.createQuery(queryString);
 	    query.setParameter("id",id);
 	    Object result = query.getSingleResult();
 	    em.remove(result);
    }
    
    public void addDelete(Object o)
    {
    	em.persist(o);
    	em.remove(o);
    }
}
