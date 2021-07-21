package de.lystx.hytoracloud.driver.utils;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class StringCreator implements Iterable<String> {

    /**
     * The cached lines
     */
    private final List<String> lines;

    public StringCreator() {
        this.lines = new ArrayList<>();
    }

    public StringCreator(String... args) {
        this();
        for (String arg : args) {
            this.append(arg).append(" ");
        }
    }

    @SneakyThrows
    public StringCreator(File file) {
        this.lines = new ArrayList<>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
    }

    /**
     * Appends a String to this creator
     *
     * @param line the line to add
     * @return current creator
     */
    public StringCreator append(String line) {
        this.lines.add(line);
        this.lines.add("\n");
        return this;
    }

    /**
     * Appends a line without a new automatic line
     *
     * @param line the line to add
     * @return current creator
     */
    public StringCreator singleAppend(String line) {
        this.lines.add(line);
        return this;
    }

    /**
     * Saves it to a File
     *
     * @param file the file to save
     */
    public void save(File file) {
        try (PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true)) {
            for (String line : lines) {
                w.print(line);
            }
            w.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Turns this creator to an array
     *
     * @return array
     */
    public String[] toArray() {
        return this.lines.toArray(new String[0]);
    }

    /**
     * Converts this to a String
     *
     * @return string
     */
    public String toString() {
        String s = "";
        for (String line : this.lines) {
            s = s + line;
        }
        return s;
    }

    public String build() {
        return toString();
    }

    public String create() {
        return toString();
    }

    public List<String> getLines() {
        return lines;
    }

    @NotNull
    @Override
    public Iterator<String> iterator() {
        return this.lines.iterator();
    }

    public void clear() {
        this.lines.clear();
    }
}
