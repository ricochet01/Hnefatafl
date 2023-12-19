package hr.mperhoc.hnefatafl.jndi;

import hr.mperhoc.hnefatafl.exception.InvalidConfigKeyNameException;

import javax.naming.Context;
import javax.naming.NamingException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

public class Config {
    private static final Hashtable<String, String> environment;
    private static final String DIR_PATH = "file:E:/hnefatafl/cfg";
    private static final String FILENAME = "config.properties";

    static {
        environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
        environment.put(Context.PROVIDER_URL, DIR_PATH);
    }

    public static int readIntConfigValue(ConfigKey key) {
        String value = readStringConfigValue(key);
        return Integer.parseInt(value);
    }

    public static String readStringConfigValue(ConfigKey key) {
        try (InitialDirContextClosable context = new InitialDirContextClosable(environment)) {
            return readValueFromKey(context, key);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static String readValueFromKey(Context context, ConfigKey key) {
        try {
            Object obj = context.lookup(FILENAME);
            Properties props = new Properties();
            props.load(new FileReader(obj.toString()));

            String value = props.getProperty(key.getKeyName());
            if (value == null) {
                throw new InvalidConfigKeyNameException(
                        "The config key '" + key.getKeyName() + "' does not exist!");
            }

            return value;
        } catch (NamingException | IOException e) {
            throw new RuntimeException("The configuration key cannot be read!", e);
        }
    }
}
