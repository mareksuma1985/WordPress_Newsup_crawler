package test;

import java.io.*;
import java.util.Date;
import java.util.Properties;

/* @author Crunchify.com */

public class GetPropertyValues {
    InputStream inputStream;

    public String[] getPropValues() throws IOException {
        String[] result = {null, null, null};
        try {
            Properties prop = new Properties();
            File propFileName = new File("src/main/resources/config.properties");
            inputStream = new FileInputStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            result[0] = prop.getProperty("baseURL");
            result[1] = prop.getProperty("openExternal");
            /** add other properties to read here */
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
        return result;
    }
}