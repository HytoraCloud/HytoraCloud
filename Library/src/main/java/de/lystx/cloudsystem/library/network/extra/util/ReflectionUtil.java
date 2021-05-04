package de.lystx.cloudsystem.library.network.extra.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectionUtil {

    /**
     * Returns a list of class's for every generic parameter type
     * given by {@link Method#getGenericParameterTypes()} or {@link Class#getGenericInterfaces()}
     *
     * @param genericParameterTypes The genericParameterTypes mentioned above
     * @return The list of types as class's
     * @see #getGenericType(Type[])
     */
    public static List<Class<?>> getGenericTypes(Type[] genericParameterTypes) {
        List<Class<?>> l = new ArrayList<>();
        if(genericParameterTypes == null) return l;

        try {
            for(Type t : genericParameterTypes) {
                ParameterizedType pType = (ParameterizedType) t;
                for(Type type : pType.getActualTypeArguments()) {
                    l.add((Class<?>) type);
                }
            }
        }
        catch(Exception e) {
            // couldn't fetch them .. just returning an empty list ..
            return new ArrayList<>();
        }
        return l;
    }

    public static Class<?> getGenericType(Type[] genericParameterTypes) {
        List<Class<?>> l = getGenericTypes(genericParameterTypes);
        if(l.size() == 0) return Void.TYPE;
        return l.get(0);
    }

    /**
     * Similar to {@link #getGenericType(Type[])} but only for fields
     *
     * @param field The field
     * @return The class of the generic type
     */
    public static Class<?> getGenericType(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    /**
     * Checks a method for different conditions
     *
     * @param m           The method
     * @param beStatic    Method must be static?
     * @param bePublic    Method must be public?
     * @param returnType  Method must return ..?
     * @param annotations Method must have annotations..?
     * @param parameter   Method must have parameter..?
     * @return The result
     */
    public static boolean checkMethod(Method m, boolean beStatic, boolean bePublic, Class<?> returnType,
                                      Class<? extends Annotation>[] annotations, Class<?>[] parameter) {
        // check modifier
        if((beStatic != Modifier.isStatic(m.getModifiers()))
                || (bePublic != Modifier.isPublic(m.getModifiers()))) {
            return false;
        }

        // check return type
        if((returnType == null && !m.getReturnType().equals(Void.TYPE))
                || (returnType != null && !returnType.isAssignableFrom(m.getReturnType()))) {
            return false;
        }

        // check annotations
        for(Class<? extends Annotation> an : annotations) {
            if(!m.isAnnotationPresent(an)) {
                return false;
            }
        }

        // check parameter
        for(int i = 0; i < m.getParameters().length; i++) {
            if(i >= parameter.length) return false;
            Class<?> paramType = m.getParameters()[i].getType();
            if(!parameter[i].isAssignableFrom(paramType)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkMethod(Method m, Class<? extends Annotation> annotation, Class<?>[] parameter) {
        return checkMethod(m, false, true, null, new Class[]{annotation}, parameter);
    }

    /**
     * Checks if the given class has a constructor without parameter
     *
     * @param clazz The class
     * @return The result
     */
    public static boolean hasParameterlessConstructor(Class<?> clazz) {
        for(Constructor<?> constructor : clazz.getConstructors()) {
            if(constructor.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * A method to list a class object from a serialized string
     *
     * @param serializedString The serialized string
     * @param tClass           The class of the type
     * @param <T>              The type
     * @return The object
     */
    public static <T> T deserialize(String serializedString, String seperator, Class<T> tClass) {
        List<String> objects = StringUtil.split(serializedString, seperator);
        return getObjectFromContent(objects, tClass);
    }

    /**
     * Deserializes an object either with invoking the method fromString or simple deserialize strings to fields<br>
     * Similar to {@link #serialize(String, Object)} will this only work if the fields are primitive or uuid (or fromString exists)
     *
     * @param serializedString The serialized string
     * @param tClass           The type class
     * @param <T>              The type
     * @return The object
     * @see #serialize(String, Object)
     */
    public static <T> T deserialize(String serializedString, Class<T> tClass) {
        Method m = getMethod(tClass, "fromString");
        T object;

        if(m != null && Modifier.isStatic(m.getModifiers())) {
            object = (T) invokeMethod(m, null, serializedString);
        }
        else {
            object = deserialize(serializedString, StringUtil.SEPERATOR_2, tClass);
        }
        return object;
    }

    /**
     * Serialize given object. This will only work if the fields are primitive or UUID
     *
     * @param seperator The seperator
     * @param object    The object
     * @return The string
     * @see #deserialize(String, Class)
     */
    public static String serialize(String seperator, Object object) {
        return StringUtil.join(seperator, ReflectionUtil.getFieldObjects(object).toArray(new Object[]{}));
    }

    /**
     * Gets a class from given name
     *
     * @param name The name
     * @return The class
     */
    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        }
        catch(ClassNotFoundException e) {
            return ReflectionUtil.class;
        }
    }

    /**
     * Get object from content
     *
     * @param objects The objects to be inserted into class's fields
     * @param clazz   The clazz
     * @param <T>     The type
     * @return The result as object from type
     */
    public static <T> T getObjectFromContent(List<String> objects, Class<T> clazz) {
        Field[] fields = getFieldsNonStatic(clazz).toArray(new Field[]{});
        if(objects.size() != fields.length) {
            return null;
        }

        if(!ReflectionUtil.hasParameterlessConstructor(clazz)) return null;
        Object instance = ReflectionUtil.getInstance(clazz);
        for(int i = 0; i < objects.size(); i++) {
            Field f = fields[i];
            String s = objects.get(i);
            Class<?> fType = f.getType();

            Object obj;
            if(s.equals("null")) {
                obj = null;
            }
            else if(!fType.equals(String.class)) {
                if(fType.equals(List.class)) {
                    obj = safeCast((List<String>) StringUtil.fromStringifiedList(s));
                }
                else {
                    obj = safeCast(s, f);
                }
            }
            else {
                obj = s;
            }
            ReflectionUtil.setFieldObject(f, instance, obj);
        }
        return (T) instance;
    }

    /**
     * Get fields from class
     *
     * @param clazz The class
     * @return The list of fields
     */
    public static List<Field> getFields(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredFields());
    }

    public static List<Field> getFieldsNonStatic(Class<?> clazz) {
        List<Field> l = new ArrayList<>();
        for(Field f : clazz.getDeclaredFields()) {
            if(Modifier.isStatic(f.getModifiers())) continue;
            l.add(f);
        }
        return l;
    }

    /**
     * Casts a string to maybe a number which is behind the char array?<br>
     * Options are: Numerics, UUIDs, Lists or nothing
     *
     * @param s The string
     * @return The object
     */
    public static Object safeCast(String s) {
        return Validation.INTEGER.matches(s) ? Integer.valueOf(s)
                : Validation.LONG.matches(s) ? Long.valueOf(s)
                : Validation.DOUBLE.matches(s) ? Double.valueOf(s)
                : Validation.UNIQUEID.matches(s) ? UUID.fromString(s)
                : Validation.LIST.matches(s) ? safeCast(StringUtil.split(s.replace("[", "").replace("]", ""), ", "))
                : s.equals("null") ? null
                : s;
    }

    public static List<Object> safeCast(List<String> s) {
        List<Object> l = new ArrayList<>();
        if(!s.isEmpty() && !s.get(0).isEmpty()) s.forEach(s1 -> l.add(safeCast(s1)));
        return l;
    }

    /**
     * Casts the given string like {@link #safeCast(String)} but it minds the type (as class)<br>
     * This can be used to cast a string for a specific field
     *
     * @param s The string
     * @param c The class (type)
     * @return The casted object
     */
    public static Object safeCast(String s, Class<?> c) {
        if(Validation.NUMBER.matches(s)) {
            if(c.equals(Short.class) || c.equals(short.class)) return Short.valueOf(s);
            else if(c.equals(Integer.class) || c.equals(int.class)) return Integer.valueOf(s);
            else if(c.equals(Long.class) || c.equals(long.class)) return Long.valueOf(s);
        }
        else if(s.equalsIgnoreCase("true")
                || s.equalsIgnoreCase("false")) return Boolean.valueOf(s);
        else if(Enum.class.isAssignableFrom(c)) {
            for(Object e : c.getEnumConstants()) {
                if((e + "").equalsIgnoreCase(s)) {
                    return e;
                }
            }
        }
        else {
            return safeCast(s);
        }
        return s;
    }

    public static Object safeCast(String s, Field f) {
        return safeCast(s, f.getType());
    }

    /**
     * Get all fields from instance as object list
     *
     * @param instance The instance
     * @return The list
     */
    public static List<Object> getFieldObjects(Object instance) {
        List<Object> l = new ArrayList<>();

        for(Field f : getFieldsNonStatic(instance.getClass())) {
            Object obj = getFieldObject(f, instance);
            l.add(obj);
        }
        return l;
    }

    /**
     * Get parameter types of given class
     *
     * @param c    The class
     * @param name The name
     * @return The class types
     */
    public static Class<?>[] getParameterTypes(Class<?> c, String name) {
        Method m = getDeclaredMethod(c, name);
        if(m == null) return new Class<?>[]{};
        return m.getParameterTypes();
    }

    /**
     * Get method by name (declared)
     *
     * @param c    The class
     * @param name The name
     * @return The method
     */
    public static Method getMethod(Class<?> c, String name) {
        for(Method m : c.getMethods()) {
            if(m.getName().equals(name)) return m;
        }
        return null;
    }

    /**
     * Get method by name (declared)
     *
     * @param c    The class
     * @param name The name
     * @return The method
     */
    public static Method getDeclaredMethod(Class<?> c, String name) {
        for(Method m : c.getDeclaredMethods()) {
            if(m.getName().equals(name)) return m;
        }
        return null;
    }

    public static Method getDeclaredMethod(Class<?> c, String name, Class<?>[] parameterTypes) {
        try {
            return c.getMethod(name, parameterTypes);
        }
        catch(NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Invokes a method
     *
     * @param m        The method
     * @param instance The instance
     * @param val      The value
     * @return The object
     */
    public static Object invokeMethod(Method m, Object instance, Object... val) {
        try {
            if(val.length == 0) {
                return m.invoke(instance);
            }
            else {
                return m.invoke(instance, val);
            }
        }
        catch(IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    public static Object invokeMethod(String name, Class<?> c, Object instance, Object... val) {
        return invokeMethod(getDeclaredMethod(c, name, getParameterTypes(val)), instance, val);
    }

    /**
     * Get parameterTypes
     *
     * @param objects The objects
     * @return The class array (types)
     */
    public static Class<?>[] getParameterTypes(Object... objects) {
        List<Class<?>> types = new ArrayList<>();

        for(Object o : objects) {
            types.add(o.getClass());
        }
        return types.toArray(new Class<?>[]{});
    }

    /**
     * Get constructor with given parameter
     *
     * @param c         The class
     * @param parameter The parameter
     * @return The constructor
     */
    public static Constructor<?> getConstructor(Class<?> c, Class<?>... parameter) {
        try {
            return c.getConstructor(parameter);
        }
        catch(NoSuchMethodException e) {
            return c.getConstructors().length != 0 ? c.getConstructors()[0] : null;
        }
    }

    public static Constructor<?> getConstructor(Class<?> c, Object... parameter) {
        return getConstructor(c, getParameterTypes(parameter));
    }

    /**
     * Get an instance from given class
     *
     * @param c         The class
     * @param parameter The parameter
     * @return The instance
     */
    public static Object getInstance(Class<?> c, Object... parameter) {
        try {
            if(parameter.length == 0) {
                for (Constructor<?> constructor : c.getConstructors()) {
                    Object[] args = new Object[constructor.getParameters().length];
                    for (int i = 0; i < constructor.getParameterTypes().length; i++) {
                        final Class<?> parameterType = constructor.getParameterTypes()[i];
                        if (Number.class.isAssignableFrom(parameterType)) {
                            args[i] = -1;
                        } else if (parameterType.equals(boolean.class) || parameterType.equals(Boolean.class)) {
                            args[i] = false;
                        } else if (parameterType.equals(int.class) || parameterType.equals(double.class) || parameterType.equals(short.class) || parameterType.equals(long.class) || parameterType.equals(float.class)) {
                            args[i] = -1;
                        } else {
                            args[i] = null;
                        }
                    }
                    return constructor.newInstance(args);
                }
                return c.newInstance();
            }
            else {
                return getConstructor(c, parameter).newInstance(parameter);
            }
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Get the id of the field
     *
     * @param field The field
     * @param c     The class
     * @return The integer
     */
    public static int getFieldId(Field field, Class<?> c) {
        if(!field.getDeclaringClass().equals(c)) {
            return -1;
        }

        int i = 0;
        for(Field f : getFieldsNonStatic(c)) {
            if(f.getName().equals(field.getName())) return i;
            i++;
        }
        return i;
    }

    /**
     * Get field from id
     *
     * @param id       The id
     * @param instance Instance of class where the field is
     * @return The field
     */
    public static Field getFieldFromId(int id, Object instance) {
        return getFieldFromId(id, instance.getClass());
    }

    public static Field getFieldFromId(int id, Class<?> clazz) {
        List<Field> fields;

        int i = 0;
        for(Field f : (fields = getFieldsNonStatic(clazz))) {
            if(i == id) return f;
            i++;
        }
        if(fields.size() < 1) return null;
        return fields.get(0);
    }

    /**
     * Get all fields from given class
     *
     * @param fields The previous fields
     * @param type   The class
     * @return The map of fields
     */
    public static Map<String, Field> getAllFields(Map<String, Field> fields, Class<?> type) {
        for(Field field : type.getDeclaredFields()) {
            if(Modifier.isStatic(field.getModifiers())) continue;
            fields.put(field.getName(), field);
        }

        if(type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }

    public static Map<String, Field> getAllFields(Class<?> type) {
        return getAllFields(new HashMap<>(), type);
    }

    /**
     * Gets a field from given name out of given class
     *
     * @param name  The name of the field
     * @param clazz The class
     * @return The field
     */
    public static Field getField(String name, Class<?> clazz) {
        for(Field f : clazz.getDeclaredFields()) {
            if(f.getName().equals(name)) return f;
        }
        return null;
    }

    public static Field getFieldRecursively(String name, Class<?> clazz) {
        return getAllFields(clazz).get(name);
    }

    /**
     * Gets the object behind a field from given instance
     *
     * @param instance The instance
     * @param field    The field
     * @return The object
     */
    public static Object getFieldObject(Field field, Object instance) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        }
        catch(IllegalAccessException e) {
            return null;
        }
    }

    public static Object getFieldObject(int id, Object instance) {
        return getFieldObject(getFieldFromId(id, instance), instance);
    }

    /**
     * Set field object
     *
     * @param field    The field
     * @param val      The value
     * @param instance The instance
     * @return The result
     */
    public static boolean setFieldObject(Field field, Object instance, Object val) {
        try {
            field.setAccessible(true);
            field.set(instance, val);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    public static boolean setFieldObject(int id, Object instance, Object value) {
        return setFieldObject(getFieldFromId(id, instance), instance, value);
    }

    public static boolean setFieldObject(String name, Object instance, Object value) {
        return setFieldObject(getField(name, instance.getClass()), instance, value);
    }

}
