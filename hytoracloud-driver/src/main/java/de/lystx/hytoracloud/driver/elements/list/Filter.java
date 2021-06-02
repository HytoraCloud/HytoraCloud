package de.lystx.hytoracloud.driver.elements.list;

import com.google.common.collect.Iterators;
import de.lystx.hytoracloud.driver.elements.interfaces.Acceptable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class Filter<T> implements Iterable<T> {

    private final CloudList<T> list;

    public Filter(CloudList<T> list) {
        this.list = list;
    }

    public Filter(List<T> tList) {
        CloudList<T> ts;
        if (tList == null) {
            ts = new CloudList<>();
        } else {
            CloudList<T> cloudList = new CloudList<>();
            for (T t : new LinkedList<>(tList)) {
                cloudList.add(t).queue();
            }
            ts = cloudList;
        }
        this.list = ts;
    }

    /**
     * Transforms this list into an array
     * @return
     */
    public T[] toArray() {
        T[] t = (T[]) new Object[Iterators.size(this.list.iterator())];
        int i = 0;
        for (T t1 : this.list) {
            t[i] = t1;
            i++;
        }
        return t;
    }

    public Filter<T> sort(Comparator<T> comparator) {
        List<T> tList = this.list.toJavaList();
        tList.sort(comparator);
        CloudList<T> newList = new CloudList<>((T[]) tList.toArray(new Object[0]));
        return newList.filter();
    }

    /**
     * Filters for a value that starts with
     * the given String
     * @return T
     */
    public CloudList<T> startsWith(String string) {
        CloudList<T> tCloudList = new CloudList<>();
        for (T t : this.list) {
            if (t instanceof String) {
                if (((String) t).startsWith(string)) {
                    tCloudList.add(t).queue();
                }
            } else {
                if (t.toString().startsWith(string)) {
                    tCloudList.add(t).queue();
                }
            }
        }
        return tCloudList;
    }

    public CloudList<T> contains(String s) {
        CloudList<T> tCloudList = new CloudList<>();
        for (T t : this.list) {
            if (t instanceof String) {
                if (((String) t).toLowerCase().contains(s.toLowerCase())) {
                    tCloudList.add(t).queue();
                }
            } else {
                if (t.toString().toLowerCase().contains(s.toLowerCase())) {
                    tCloudList.add(t).queue();
                }
            }
        }
        return tCloudList;
    }

    /**
     * Raw Method to filter value
     * @param acceptable
     * @return
     */
    public Promise<T> find(Acceptable<T> acceptable) {
        return new Promise<>(this.list, acceptable);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.list.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        this.list.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return this.list.spliterator();
    }
}
