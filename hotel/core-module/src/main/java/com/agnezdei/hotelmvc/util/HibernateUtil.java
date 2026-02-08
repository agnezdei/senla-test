package com.agnezdei.hotelmvc.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.GuestService;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.Service;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;
    
    static {
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();
            
            MetadataSources sources = new MetadataSources(registry);
            
            sources.addAnnotatedClass(Guest.class);
            sources.addAnnotatedClass(Room.class);
            sources.addAnnotatedClass(Service.class);
            sources.addAnnotatedClass(Booking.class);
            sources.addAnnotatedClass(GuestService.class);
            
            Metadata metadata = sources.getMetadataBuilder().build();
            
            sessionFactory = metadata.getSessionFactoryBuilder().build();
            
        } catch (Throwable ex) {
            System.err.println("Ошибка инициализации Hibernate: " + ex);
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static Session openSession() {
        return sessionFactory.openSession();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
    
    public static <T> T executeInTransaction(TransactionOperation<T> operation) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();
            
            T result = operation.execute(session);
            
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    @FunctionalInterface
    public interface TransactionOperation<T> {
        T execute(Session session) throws Exception;
    }
}