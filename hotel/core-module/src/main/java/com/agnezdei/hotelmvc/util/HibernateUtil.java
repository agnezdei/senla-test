package com.agnezdei.hotelmvc.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

import com.agnezdei.hotelmvc.entity.Booking;
import com.agnezdei.hotelmvc.entity.Guest;
import com.agnezdei.hotelmvc.entity.GuestService;
import com.agnezdei.hotelmvc.entity.Room;
import com.agnezdei.hotelmvc.entity.Service;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;
    
    static {
        try {
            sessionFactory = new AnnotationConfiguration()
                .addPackage("com.agnezdei.hotelmvc.entity")
                .addAnnotatedClass(Guest.class)
                .addAnnotatedClass(Room.class)
                .addAnnotatedClass(Service.class)
                .addAnnotatedClass(Booking.class)
                .addAnnotatedClass(GuestService.class)
                .configure()
                .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Ошибка инициализации Hibernate: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    // Получить новую сессию
    public static Session openSession() throws HibernateException {
        return sessionFactory.openSession();
    }
    
    // Получить фабрику сессий
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    // Закрыть фабрику сессий
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
    
    // Вспомогательный метод для выполнения операции в транзакции
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
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    // Интерфейс для операции в транзакции
    @FunctionalInterface
    public interface TransactionOperation<T> {
        T execute(Session session) throws Exception;
    }
}