package org.click.media.rnn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by liuyichao on 16/4/6.
 */
public class ArgsTools {

    public static Properties mkPropertiesFromArg(String[] args) {
        Properties properties = new Properties();
        for(String arg:args){
            if(arg.startsWith("-")){
                String[] property = arg.substring(1).trim().split("=");
                if (property.length == 2){
                    if(property[0].equals("prop")){
                        Properties propProperties  = new Properties();

                        try {
                            propProperties.load(new FileInputStream(property[1]));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        properties.putAll(propProperties);
                    }else {
                        properties.put(property[0],property[1]);
                    }
                }
            }
        }
        return properties;
    }

}
