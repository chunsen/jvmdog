package jvmdog.agent;

import java.net.URL;
import java.net.URLClassLoader;

public class DogClassLoader extends URLClassLoader{
    public DogClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
