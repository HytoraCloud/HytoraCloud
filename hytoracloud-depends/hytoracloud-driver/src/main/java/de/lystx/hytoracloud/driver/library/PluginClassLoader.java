
package de.lystx.hytoracloud.driver.library;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class PluginClassLoader extends URLClassLoader {

    public static final Set<ClassLoader> CLASS_LOADERS = new CopyOnWriteArraySet<>();

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public PluginClassLoader(URL[] urls) {
        super(urls);
    }

    public void addToClassloaders() {
        CLASS_LOADERS.add(this);
    }

    public void addPath(Path path) {
        try {
            addURL(path.toUri().toURL());
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public void close() throws IOException {
        CLASS_LOADERS.remove(this);
        super.close();
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass0(name, resolve);
    }

    private Class<?> loadClass0(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ignored) {
            // Ignored: we'll try others
        }

        for (ClassLoader loader : CLASS_LOADERS) {
            if (loader != this) {
                try {
                    return loader.loadClass(name);
                } catch (ClassNotFoundException ignored) {
                    // We're trying others, safe to ignore
                }
            }
        }

        throw new ClassNotFoundException(name);
    }
}
