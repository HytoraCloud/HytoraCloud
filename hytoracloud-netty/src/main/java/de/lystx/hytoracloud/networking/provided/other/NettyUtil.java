package de.lystx.hytoracloud.networking.provided.other;

import de.lystx.hytoracloud.networking.exceptions.NetworkGatewayOutputException;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class NettyUtil {
    /**
     * Epoll = pipeline improvement at linux
     */
    private static boolean epoll = false;

    static {
        if(!System.getProperty("os.name").toLowerCase().contains("win")) {
            epoll = Epoll.isAvailable();

            if(!epoll) {
                System.err.println("Despite being on Unix epoll is not working, falling back to NIO. (" + Epoll.unavailabilityCause().getMessage() + ")");
            }
        }
    }

    /**
     * Checks if the current thread is an async moo pool task
     */
    public static void checkAsyncTask() {
        Thread currentThread = Thread.currentThread();
        if(currentThread.getName().equals("main")
                // I decided to not allow the nioEventLoopGroup as "async", because it could block
                // netty sending/receiving packets
                || currentThread.getName().startsWith("nioEventLoopGroup")) {
            throw new NetworkGatewayOutputException(NetworkGatewayOutputException.Type.WRONG_THREAD, currentThread.getName());
        }
    }

    /**
     * Gets the channel for the server<br>
     * If epoll is available then choose the epoll type of channel
     *
     * @return The serverChannel
     */
    public static Class<? extends ServerChannel> getServerChannel() {
        return epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    /**
     * Gets the channel for the client<br>
     * If epoll is available then choose the epoll type of channel
     *
     * @return The serverChannel
     */
    public static Class<? extends Channel> getChannel() {
        return epoll ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    /**
     * Gets the event loop group<br>
     * If epoll is available then choose the epoll type of event loop group
     *
     * @return The serverChannel
     */
    public static EventLoopGroup getEventLoopGroup() {
        return epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    /**
     * ==================================
     *   Reflections
     * ==================================
     */

    public static List<Field> getFieldsNonStatic(Class<?> clazz) {
        List<Field> l = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            if(Modifier.isStatic(f.getModifiers())) continue;
            l.add(f);
        }
        return l;
    }

    public static Field getFieldFromId(int id, Class<?> clazz) {
        List<Field> fields;

        int i = 0;
        for (Field f : (fields = getFieldsNonStatic(clazz))) {
            if (i == id) {
                return f;
            }
            i++;
        }
        if (fields.size() < 1) {
            return null;
        }
        return fields.get(0);
    }


    public static <T> T getInstance(Class<T> tClass) {
        try {
            Constructor<?> constructor;

            try {
                List<Constructor<?>> constructors = Arrays.asList(tClass.getDeclaredConstructors());

                constructors.sort(Comparator.comparingInt(Constructor::getParameterCount));

                constructor = constructors.get(constructors.size() - 1);
            } catch (Exception e) {
                constructor = null;
            }


            //Iterates through all Constructors to create a new Instance of the Object
            //And to set all values to null, -1 or false
            T object = null;
            if (constructor != null) {
                Object[] args = new Object[constructor.getParameters().length];
                for (int i = 0; i < constructor.getParameterTypes().length; i++) {
                    final Class<?> parameterType = constructor.getParameterTypes()[i];
                    if (Number.class.isAssignableFrom(parameterType)) {
                        args[i] = -1;
                    } else if (parameterType.equals(boolean.class) || parameterType.equals(Boolean.class)) {
                        args[i] = false;
                    } else if (parameterType.equals(int.class) || parameterType.equals(double.class) || parameterType.equals(short.class) || parameterType.equals(long.class) || parameterType.equals(float.class) || parameterType.equals(byte.class)) {
                        args[i] = -1;
                    } else if (parameterType.equals(Integer.class) || parameterType.equals(Double.class) || parameterType.equals(Short.class) || parameterType.equals(Long.class) || parameterType.equals(Float.class) || parameterType.equals(Byte.class)) {
                        args[i] = -1;
                    } else {
                        args[i] = null;
                    }
                }
                object = (T) constructor.newInstance(args);
            }

            if (object == null) {
                object = tClass.newInstance();
            }

            return object;
        } catch (Exception e) {
            return null;
        }
    }
}
