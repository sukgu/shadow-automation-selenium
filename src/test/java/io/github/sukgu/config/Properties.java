package io.github.sukgu.config;

import static java.lang.System.err;

public class Properties {
    // origin:
    // https://github.com/TsvetomirSlavov/wdci/blob/master/code/src/main/java/com/seleniumsimplified/webdriver/manager/EnvironmentPropertyReader.java
    private static String getPropertyEnv(String name, String defaultValue) {
        String value = System.getProperty(name);
        if (debug) {
            err.println("system property " + name + " = " + value);
        }
        if (value == null || value.length() == 0) {
            value = System.getenv(name);
            if (debug) {
                err.println("system env " + name + " = " + value);
            }
            if (value == null || value.length() == 0) {
                value = defaultValue;
                if (debug) {
                    err.println("default value  = " + value);
                }
            }
        }
        return value;
    }

    public static String browser = getPropertyEnv("BROWSER", getPropertyEnv("webdriver.driver", "chrome"));
    public static final boolean debug = Boolean
            .parseBoolean(getPropertyEnv("DEBUG", "false"));
}
