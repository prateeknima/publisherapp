
package com.merative.publisher;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;
import java.util.concurrent.atomic.AtomicLong;

public class UserKeyGeneratorService implements ValueGenerator<String> {

    private static final AtomicLong SEQUENCE = new AtomicLong(100000);
    private AtomicLong counter = new AtomicLong();

    @Override
    public String generateValue(Session session, Object owner) {
        long nextValue = SEQUENCE.getAndIncrement();
        if (nextValue > 999999) {
            throw new HibernateException("There are no values left for generating a new key.");
        }
        return "KEY-" + nextValue;
    }
}