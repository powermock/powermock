package powermock.examples.partialmocking;

/**
 * Used to demonstrate PowerMock's ability to partial mock method calls.
 *
 * Created by Katharina Laube on 08.09.2014.
 */
public class StatusSender {

    EntityManager entityManager = new EntityManager();

    public boolean handleStatus(Status status){

        entityManager.persist(status);

        return sendStatus(status);
    }

    public boolean sendStatus(Status status){
        // code that will not be tested with handleStatus method
        // (i.e. send status via a topic)
        return false;
    }
}
