package okapied.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

/**
 * To be used as the JPADialect in the entityManagerFactory declaration in applicationContext.xml
 * It allows us to specify the isolation level of the transaction per method using the Transaction
 * annotation and isolation=
 * 
 * @author eduthie
 */
public class HibernateExtendedJpaDialect extends HibernateJpaDialect {

    @Override
    public Object beginTransaction(EntityManager entityManager,
            TransactionDefinition definition) throws PersistenceException,
            SQLException, TransactionException {

        Session session = (Session) entityManager.getDelegate();
        DataSourceUtils.prepareConnectionForTransaction(session.connection(), definition);
        if( definition.getIsolationLevel() >= 0)
        {
            session.connection().setTransactionIsolation(definition.getIsolationLevel());
        }
        entityManager.getTransaction().begin();
        return prepareTransaction(entityManager, definition.isReadOnly(), definition.getName());
    }

}
