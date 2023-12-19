package hr.mperhoc.hnefatafl.jndi;

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class InitialDirContextClosable extends InitialDirContext implements AutoCloseable {
    public InitialDirContextClosable(Hashtable<?, ?> environment) throws NamingException {
        super(environment);
    }
}
