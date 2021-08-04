package de.lystx.hytoracloud.driver.utils.other;

import de.lystx.hytoracloud.driver.console.progressbar.ProgressBar;
import de.lystx.hytoracloud.driver.console.progressbar.ProgressBarStyle;
import de.lystx.hytoracloud.driver.utils.interfaces.Identifiable;
import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;
import org.apache.http.Header;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Utils {

    public static final String INTERNAL_RECEIVER = "InternalReceiver";
    public static final String PASTE_SERVER_URL_DOCUMENTS = "https://paste.labymod.net/documents";
    public static final String PASTE_SERVER_URL = "https://paste.labymod.net/";
    public static final String PASTE_SERVER_URL_RAW = "https://paste.labymod.net/raw/";

    //Http utils
    public static final String UTF_8 = "UTF-8";
    public static final String QUESTION_MARK = "?";
    public static final String AMPERSAND = "&";
    public static final String EQUALS = "=";
    public static final String GZIP = "gzip";
    public static final String SYSTEM_LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String PATH_SEPARATOR = "/";
    public static final String USER_AGENT = "rest-client/1.6.0";

    //Cloudflare utils
    public static final String CLOUDFLARE_API_BASE_URL = "https://api.cloudflare.com/client/v4/";

    private static void extractTypeArguments(Map<Type, Type> typeMap, Class<?> clazz) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (!(genericSuperclass instanceof ParameterizedType)) {
            return;
        }

        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] typeParameter = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();
        Type[] actualTypeArgument = parameterizedType.getActualTypeArguments();
        for (int i = 0; i < typeParameter.length; i++) {
            if(typeMap.containsKey(actualTypeArgument[i])) {
                actualTypeArgument[i] = typeMap.get(actualTypeArgument[i]);
            }
            typeMap.put(typeParameter[i], actualTypeArgument[i]);
        }
    }

    public static Path getCurrentPath() {

        return null;
    }


    /**
     * Creates an Object from scratch
     *
     * @param tClass the object class
     */
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

    public static URL getCurrentURL() {

        String s = Utils.class.getName();
        int i = s.lastIndexOf(".");
        s = s.substring(i + 1);
        s = s + ".class";
        return Utils.class.getResource(s);
    }

    /**
     * Logs and uploads to PasteServer
     *
     * @param text the content
     * @param raw if it should be uploaded to raw
     * @return link
     * @throws IOException
     */
    public static String uploadToHasteBin(String text, boolean raw) throws IOException {
        byte[] postData = text.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        URL url = new URL(Utils.PASTE_SERVER_URL_DOCUMENTS);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", "Hastebin Java Api");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.setUseCaches(false);

        String response = null;
        DataOutputStream wr;
        try {
            wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            response = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert response != null;
        if (response.contains("\"key\"")) {
            response = response.substring(response.indexOf(":") + 2, response.length() - 2);

            String postURL = raw ? Utils.PASTE_SERVER_URL_RAW : Utils.PASTE_SERVER_URL;
            response = postURL + response;
        }

        return response;
    }

    @SneakyThrows
    public static Enum<?> getEnumByName(Class<?> enumType, String name) {

        Method enumConstantDirectory = enumType.getClass().getDeclaredMethod("enumConstantDirectory");
        enumConstantDirectory.setAccessible(true);

        Map<String, Enum<?>> invoke = (Map<String, Enum<?>>) enumConstantDirectory.invoke(enumType);

        Enum<?> result = invoke.get(name);
        if (result != null) {
            return result;
        }
        if (name == null) {
            throw new NullPointerException("Name is null");
        }
        throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + "." + name);
    }


    public static InetSocketAddress getAddress(String address) throws Exception {
        String[] split = address.split(":");
        String hostname = Arrays.stream(Arrays.copyOfRange(split, 0, split.length - 1)).collect(Collectors.joining(":"));
        int port = Integer.parseInt(split[split.length-1]);
        return InetSocketAddress.createUnresolved(hostname, port);
    }

    public static int readVarInt(ByteBuf input) {
        return readVarInt(input, 5);
    }

    public static int readVarInt(ByteBuf input, int maxBytes) {
        int out = 0;
        int bytes = 0;
        byte in;
        while (true) {
            in = input.readByte();
            out |= (in & 0x7F) << (bytes++ * 7);
            if (bytes > maxBytes) throw new RuntimeException("VarInt too big");
            if ((in & 0x80) != 0x80) break;
        }
        return out;
    }

    public static void writeVarInt(int value, ByteBuf output) {
        int part;
        while (true) {
            part = value & 0x7F;
            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }
            output.writeByte(part);
            if (value == 0) break;
        }
    }

    public static void writeString(String s, ByteBuf buf) {
        byte[] b = s.getBytes();
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public static void writeVarShort(ByteBuf buf, int toWrite) {
        int low = toWrite & 0x7FFF;
        int high = (toWrite & 0x7F8000) >> 15;
        if (high != 0) low = low | 0x8000;
        buf.writeShort(low);
        if (high != 0) buf.writeByte(high);
    }

    public static Class<?> findSubClassParameterType(Object instance, Class<?> classOfInterest, int parameterIndex) {
        Map<Type, Type> typeMap = new HashMap<Type, Type>();
        Class<?> instanceClass = instance.getClass();
        while (classOfInterest != instanceClass.getSuperclass()) {
            extractTypeArguments(typeMap, instanceClass);
            instanceClass = instanceClass.getSuperclass();
            if (instanceClass == null) throw new IllegalArgumentException();
        }

        ParameterizedType parameterizedType = (ParameterizedType) instanceClass.getGenericSuperclass();
        Type actualType = parameterizedType.getActualTypeArguments()[parameterIndex];
        if (typeMap.containsKey(actualType)) {
            actualType = typeMap.get(actualType);
        }

        if (actualType instanceof Class) {
            return (Class<?>) actualType;
        } else if (actualType instanceof TypeVariable) {
            return browseNestedTypes(instance, (TypeVariable<?>) actualType);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static Class<?> browseNestedTypes(Object instance, TypeVariable<?> actualType) {
        Class<?> instanceClass = instance.getClass();
        List<Class<?>> nestedOuterTypes = new LinkedList<Class<?>>();
        for (
                Class<?> enclosingClass = instanceClass.getEnclosingClass();
                enclosingClass != null;
                enclosingClass = enclosingClass.getEnclosingClass()) {
            try {
                Field this$0 = instanceClass.getDeclaredField("this$0");
                Object outerInstance = this$0.get(instance);
                Class<?> outerClass = outerInstance.getClass();
                nestedOuterTypes.add(outerClass);
                Map<Type, Type> outerTypeMap = new HashMap<Type, Type>();
                extractTypeArguments(outerTypeMap, outerClass);
                for (Map.Entry<Type, Type> entry : outerTypeMap.entrySet()) {
                    if (!(entry.getKey() instanceof TypeVariable)) {
                        continue;
                    }
                    TypeVariable<?> foundType = (TypeVariable<?>) entry.getKey();
                    if (foundType.getName().equals(actualType.getName())
                            && isInnerClass(foundType.getGenericDeclaration(), actualType.getGenericDeclaration())) {
                        if (entry.getValue() instanceof Class) {
                            return (Class<?>) entry.getValue();
                        }
                        actualType = (TypeVariable<?>) entry.getValue();
                    }
                }
            } catch (NoSuchFieldException e) { /* this should never happen */ } catch (IllegalAccessException e) { /* this might happen */}

        }
        throw new IllegalArgumentException();
    }

    private static boolean isInnerClass(GenericDeclaration outerDeclaration, GenericDeclaration innerDeclaration) {
        if (!(outerDeclaration instanceof Class) || !(innerDeclaration instanceof Class)) {
            throw new IllegalArgumentException();
        }
        Class<?> outerClass = (Class<?>) outerDeclaration;
        Class<?> innerClass = (Class<?>) innerDeclaration;
        while ((innerClass = innerClass.getEnclosingClass()) != null) {
            if (innerClass == outerClass) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the percent of match of two strings
     *
     * @param s1 the string to compare
     * @param s2 the string to get compared
     * @param ignoreCase if strings should be lowercased
     * @return percent as double (1.0 = 100%, 0.94 = 94%)
     */
    public static double getPercentMatch(String s1, String s2, boolean ignoreCase) {

        if (ignoreCase) {
            s1 = s1.toLowerCase();
            s2 = s2.toLowerCase();
        }

        Set<String> nx = new HashSet<>(); //Set 1
        Set<String> ny = new HashSet<>(); //Set 2

        //String 1 match
        for (int i = 0; i < s1.length() - 1; i++) {
            char x1 = s1.charAt(i);
            char x2 = s1.charAt(i + 1);
            nx.add("" + x1 + x2);
        }

        //String 2 match
        for (int j = 0; j < s2.length() - 1; j++) {
            char y1 = s2.charAt(j);
            char y2 = s2.charAt(j+1);
            ny.add("" + y1 + y2);
        }

        //New set for the match
        Set<String> intersection = new HashSet<>(nx);
        intersection.retainAll(ny); //Removes all not containing elements

        return (2 * intersection.size()) / (nx.size() + ny.size());
    }

    /**
     * Clears the console screen
     */
    public static void clearConsole() {
        try {
            String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the dateformat for console
     *
     * @return format
     */
    public static SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat("hh:mm:ss");
    }


    /**
     * Downloads a file from a website
     *
     * @param search > URL
     * @param location > File to download to
     */
    public static void download(String search, File location, String task)  {
        InputStream inputStream;
        OutputStream outputStream;

        try {
            ProgressBar pb = new ProgressBar(task, 100, 1000, System.err, ProgressBarStyle.ASCII, "", 1, false, null, ChronoUnit.SECONDS, 0L, Duration.ZERO);
            URL url = new URL(search);
            String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", USER_AGENT);

            int contentLength = con.getContentLength();
            inputStream = con.getInputStream();

            outputStream = new FileOutputStream(location);
            byte[] buffer = new byte[2048];
            int length;
            int downloaded = 0;

            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                downloaded+=length;
                pb.stepTo((long) ((downloaded * 100L) / (contentLength * 1.0)));
            }
            pb.setExtraMessage("Cleaning up...");
            outputStream.close();
            inputStream.close();
            pb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Adds all objects to a string
     *
     * @param input the input to add as String
     * @return List with given objects
     */
    public static List<String> toStringList(List<?> input) {
        List<String> list = new LinkedList<>();

        for (Object o : input) {
            if (o instanceof Identifiable) {
                list.add(((Identifiable) o).getName());
                continue;
            }
            list.add(o.toString());
        }

        return list;
    }

    /**
     * Deletes a folder with content
     *
     * @param folder the folder
     */
    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();

        //some JVMs return null for empty dirs
        if(files != null) {
            for (File f: files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    /**
     * Does an operation until the list is empty and
     * then does another operation with the emptyConsumer
     *
     * @param list the list
     * @param listConsumer the consumer for every list item
     * @param emptyConsumer the consumer when list is empty
     * @param <T> the generic
     */
    public static <T> void doUntilEmpty(List<T> list, Consumer<T> listConsumer, Consumer<List<T>> emptyConsumer) {
        int i = list.size();
        for (T t : list) {
            listConsumer.accept(t);
            i--;
            if (i <= 0) {
                emptyConsumer.accept(list);
            }
        }
    }

    /**
     * Copies a resource from the resource folder to a location
     *
     * @param res the resource name
     * @param dest the location name
     * @param c the class
     * @throws IOException if something goes wrong
     */
    public static void copyResource(String res, String dest, Class<?> c) throws IOException {
        InputStream src = c.getResourceAsStream(res);
        Files.copy(src, Paths.get(dest), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Sets a field inside a class
     *
     * @param _class the class
     * @param instance the object
     * @param name the field name
     * @param value the value
     */
    @SneakyThrows
    public static void setField(Class<?> _class, Object instance, String name, Object value) {
        try {
            Field field = _class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a class exists
     *
     * @param name the name
     * @return boolean
     */
    public static boolean existsClass(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    //Base64 encoder

    // Mapping table from 6-bit nibbles to Base64 characters.
    private static final char[] CHARS = new char[64];
    private static final byte[] BYTES = new byte[128];

    static {
        int i = 0;
        for (char c = 'A'; c <= 'Z'; c++)
            CHARS[i++] = c;
        for (char c = 'a'; c <= 'z'; c++)
            CHARS[i++] = c;
        for (char c = '0'; c <= '9'; c++)
            CHARS[i++] = c;
        CHARS[i++] = '+';
        CHARS[i++] = '/';
    }

    static {
        Arrays.fill(BYTES, (byte) -1);
        for (int i = 0; i < 64; i++)
            BYTES[CHARS[i]] = (byte) i;
    }

    /**
     * Encodes a string into Base64 format. No blanks or line breaks are inserted.
     *
     * @param s A String to be encoded.
     * @return A String containing the Base64 encoded data.
     */
    public static String encodeString(String s) {
        return new String(encode(s.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Encodes a byte array into Base 64 format and breaks the output into lines of 76 characters. This method is compatible with
     * <code>sun.misc.BASE64Encoder.encodeBuffer(byte[])</code>.
     *
     * @param in An array containing the data bytes to be encoded.
     * @return A String containing the Base64 encoded data, broken into lines.
     */
    public static String encodeLines(byte[] in) {
        return encodeLines(in, 0, in.length, 76, SYSTEM_LINE_SEPARATOR);
    }

    /**
     * Encodes a byte array into Base 64 format and breaks the output into lines.
     *
     * @param in            An array containing the data bytes to be encoded.
     * @param iOff          Offset of the first byte in <code>in</code> to be processed.
     * @param iLen          Number of bytes to be processed in <code>in</code>, starting at <code>iOff</code>.
     * @param lineLen       Line length for the output data. Should be a multiple of 4.
     * @param lineSeparator The line separator to be used to separate the output lines.
     * @return A String containing the Base64 encoded data, broken into lines.
     */
    public static String encodeLines(byte[] in, int iOff, int iLen, int lineLen, String lineSeparator) {
        int blockLen = (lineLen * 3) / 4;
        if (blockLen <= 0)
            throw new IllegalArgumentException();
        int lines = (iLen + blockLen - 1) / blockLen;
        int bufLen = ((iLen + 2) / 3) * 4 + lines * lineSeparator.length();
        StringBuilder buf = new StringBuilder(bufLen);
        int ip = 0;
        while (ip < iLen) {
            int l = Math.min(iLen - ip, blockLen);
            buf.append(encode(in, iOff + ip, l));
            buf.append(lineSeparator);
            ip += l;
        }
        return buf.toString();
    }

    /**
     * Encodes a byte array into Base64 format. No blanks or line breaks are inserted in the output.
     *
     * @param in An array containing the data bytes to be encoded.
     * @return A character array containing the Base64 encoded data.
     */
    public static char[] encode(byte[] in) {
        return encode(in, 0, in.length);
    }

    /**
     * Encodes a byte array into Base64 format. No blanks or line breaks are inserted in the output.
     *
     * @param in   An array containing the data bytes to be encoded.
     * @param iLen Number of bytes to process in <code>in</code>.
     * @return A character array containing the Base64 encoded data.
     */
    public static char[] encode(byte[] in, int iLen) {
        return encode(in, 0, iLen);
    }

    /**
     * Encodes a byte array into Base64 format. No blanks or line breaks are inserted in the output.
     *
     * @param in   An array containing the data bytes to be encoded.
     * @param iOff Offset of the first byte in <code>in</code> to be processed.
     * @param iLen Number of bytes to process in <code>in</code>, starting at <code>iOff</code>.
     * @return A character array containing the Base64 encoded data.
     */
    public static char[] encode(byte[] in, int iOff, int iLen) {
        int oDataLen = (iLen * 4 + 2) / 3; // output length without padding
        int oLen = ((iLen + 2) / 3) * 4; // output length including padding
        char[] out = new char[oLen];
        int ip = iOff;
        int iEnd = iOff + iLen;
        int op = 0;
        while (ip < iEnd) {
            int i0 = in[ip++] & 0xff;
            int i1 = ip < iEnd ? in[ip++] & 0xff : 0;
            int i2 = ip < iEnd ? in[ip++] & 0xff : 0;
            int o0 = i0 >>> 2;
            int o1 = ((i0 & 3) << 4) | (i1 >>> 4);
            int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
            int o3 = i2 & 0x3F;
            out[op++] = CHARS[o0];
            out[op++] = CHARS[o1];
            out[op] = op < oDataLen ? CHARS[o2] : '=';
            op++;
            out[op] = op < oDataLen ? CHARS[o3] : '=';
            op++;
        }
        return out;
    }

    /**
     * Decodes a string from Base64 format. No blanks or line breaks are allowed within the Base64 encoded input data.
     *
     * @param s A Base64 String to be decoded.
     * @return A String containing the decoded data.
     * @throws IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static String decodeString(String s) {
        return new String(decode(s), StandardCharsets.UTF_8);
    }

    /**
     * Decodes a byte array from Base64 format and ignores line separators, tabs and blanks. CR, LF, Tab and Space characters are ignored in the input data. This method is
     * compatible with <code>sun.misc.BASE64Decoder.decodeBuffer(String)</code>.
     *
     * @param s A Base64 String to be decoded.
     * @return An array containing the decoded data bytes.
     * @throws IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static byte[] decodeLines(String s) {
        char[] buf = new char[s.length()];
        int p = 0;
        for (int ip = 0; ip < s.length(); ip++) {
            char c = s.charAt(ip);
            if (c != ' ' && c != '\r' && c != '\n' && c != '\t')
                buf[p++] = c;
        }
        return decode(buf, 0, p);
    }

    /**
     * Decodes a byte array from Base64 format. No blanks or line breaks are allowed within the Base64 encoded input data.
     *
     * @param s A Base64 String to be decoded.
     * @return An array containing the decoded data bytes.
     * @throws IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static byte[] decode(String s) {
        return decode(s.toCharArray());
    }

    /**
     * Decodes a byte array from Base64 format. No blanks or line breaks are allowed within the Base64 encoded input data.
     *
     * @param in A character array containing the Base64 encoded data.
     * @return An array containing the decoded data bytes.
     * @throws IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static byte[] decode(char[] in) {
        return decode(in, 0, in.length);
    }

    /**
     * Decodes a byte array from Base64 format. No blanks or line breaks are allowed within the Base64 encoded input data.
     *
     * @param in   A character array containing the Base64 encoded data.
     * @param iOff Offset of the first character in <code>in</code> to be processed.
     * @param iLen Number of characters to process in <code>in</code>, starting at <code>iOff</code>.
     * @return An array containing the decoded data bytes.
     * @throws IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static byte[] decode(char[] in, int iOff, int iLen) {
        if (iLen % 4 != 0)
            throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
        while (iLen > 0 && in[iOff + iLen - 1] == '=')
            iLen--;
        int oLen = (iLen * 3) / 4;
        byte[] out = new byte[oLen];
        int ip = iOff;
        int iEnd = iOff + iLen;
        int op = 0;
        while (ip < iEnd) {
            int i0 = in[ip++];
            int i1 = in[ip++];
            int i2 = ip < iEnd ? in[ip++] : 'A';
            int i3 = ip < iEnd ? in[ip++] : 'A';
            if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127)
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            int b0 = BYTES[i0];
            int b1 = BYTES[i1];
            int b2 = BYTES[i2];
            int b3 = BYTES[i3];
            if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0)
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            int o0 = (b0 << 2) | (b1 >>> 4);
            int o1 = ((b1 & 0xf) << 4) | (b2 >>> 2);
            int o2 = ((b2 & 3) << 6) | b3;
            out[op++] = (byte) o0;
            if (op < oLen)
                out[op++] = (byte) o1;
            if (op < oLen)
                out[op++] = (byte) o2;
        }
        return out;
    }

    //Response utils

    private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");

    /**
     * Parse out a charset from a content type header.
     *
     * @param contentType e.g. "text/html; charset=EUC-JP"
     * @return "EUC-JP", or null if not found. Charset is trimmed and uppercased.
     */
    public static String getCharsetFromContentType(String contentType) {
        if (contentType == null)
            return null;

        Matcher m = charsetPattern.matcher(contentType);
        if (m.find()) {
            return m.group(1).trim().toUpperCase();
        }
        return null;
    }

    public static byte[] readBytes(InputStream is) throws IOException {
        int len;
        int size = 8192;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1)
                bos.write(buf, 0, len);
            buf = bos.toByteArray();
        }
        return buf;
    }

    public static boolean isGzipped(Header contentEncoding) {
        if (contentEncoding != null) {
            String value = contentEncoding.getValue();
            if (value != null && Utils.GZIP.equals(value.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

}
