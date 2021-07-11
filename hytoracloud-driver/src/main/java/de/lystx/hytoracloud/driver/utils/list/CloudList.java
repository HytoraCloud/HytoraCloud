package de.lystx.hytoracloud.driver.utils.list;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class CloudList<T> implements Iterable<T> {

    private T[] arrayList;
    private int elementsInArray;

    private CloudList<ListQueueAction<T>> pendingQueues;

    public CloudList() {
        this(99999);
    }

    private CloudList(boolean queue) {
        if (!queue) {
            return;
        }
        this.create(99999, true);

    }

    @SafeVarargs
    public CloudList(T... args) {
        this();
        for (T arg : args) {
            this.add(arg);
        }
    }

    public CloudList(int size) {
        this.create(size, false);
    }

    public void create(int size, boolean queue) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size of CloudList must be bigger than 0");
        }
        this.arrayList = (T[]) new Object[size];
        this.elementsInArray = 0;
        if (!queue) {
            this.pendingQueues = new CloudList<>(true);
        }
    }

    public ListQueueAction<T> add(T t) {
        final ListQueueAction<T> queueAction = new ListQueueAction<>(this, listQueueAction -> {
            if (checkIfArrayFull()) {
                copyArray(0, "double");
            }

            this.arrayList[this.elementsInArray] = t;
            this.elementsInArray++;
            if (this.pendingQueues != null) {
                this.pendingQueues.remove(listQueueAction);
            }
        });
        if (this.pendingQueues != null) {
            this.pendingQueues.add(queueAction).queue();
        }
        return queueAction;
    }

    public void queueAll() {
        this.queueAll(false);
    }
    public void queueAll(boolean debug) {
        int count = 0;
        for (ListQueueAction<T> pendingQueue : this.pendingQueues) {
            if (debug) System.out.println("[CloudList<" + this.getGenericClass().getSimpleName() + ">] Fetching QueueEntry #" + count);
            count++;
            pendingQueue.queue();
        }
    }

    public void add(int index, T t) {
        if (checkIfArrayFull()) {
            copyArray(0, "double");
        }

        if (index >= this.arrayList.length) {
            throw new IndexOutOfBoundsException("Can't add value to CloudList at index " + index + " because provided Index was bigger than arrayList length");
        }

        T temp = this.arrayList[index];
        arrayList[index] = t;

        T temp2;

        for (int i = index; i < this.arrayList.length - 1; i++) {
            temp2 = arrayList[i + 1];
            arrayList[i + 1] = temp;
            temp = temp2;
        }

        copyArray(0, "");
        this.elementsInArray++;
    }

    public T get(int index) {
        T element;
        try {
            element = this.arrayList[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("Can't find element at index " + index + "!");
        }

        return element;
    }

    public void merge(Iterable<T> list) {
        for (T t : list) {
            this.add(t).queue();
        }
    }

    public int size() {
        return this.elementsInArray;
    }

    public boolean isEmpty() {
        return this.elementsInArray == 0;
    }

    public boolean contains(T ob) {
        return find(ob) >= 0;
    }

    public int find (T n) {
        for (int i = 0; i < this.arrayList.length; i++) {
            if (n.equals(this.arrayList[i])) {
                return i;
            }
        }

        return -1;
    }

    public void remove (T n) {
        for (int i = 0; i < this.elementsInArray; i++) {
            if (n.equals(this.arrayList[i])) {
                this.arrayList[i] = null;
                this.elementsInArray--;
                copyArray(0, "");
                break;
            }
        }
    }

    private boolean checkIfArrayFull() {
        return this.arrayList.length == this.elementsInArray;
    }

    private void copyArray(int size, String action) {
        size = increaseArraySize(size, action);

        T[] tempArray = (T[]) new Object[size];

        int tempElement = 0;

        for (int i = 0; i < this.arrayList.length; i++, tempElement++) {
            if (this.arrayList[i] == null) {
                tempElement--;
                continue;
            }

            tempArray[tempElement] = this.arrayList[i];
        }

        this.arrayList = null;
        this.arrayList = (T[]) new Object[tempArray.length];
        this.arrayList = tempArray;
    }

    private int increaseArraySize(int size, String action) {
        if (action.equals("double")) {
            size = this.arrayList.length * 2;
        } else {
            size = this.arrayList.length + size;
        }

        return size;
    }

    public List<T> toJavaList() {
        List<T> list = new LinkedList<>();
        for (int i = 0; i < this.size(); i++) {
            list.add(get(i));
        }
        return list;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.toJavaList().iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        for (T t : this) {
            action.accept(t);
        }
    }

    @Override
    public Spliterator<T> spliterator() {
        return this.toJavaList().spliterator();
    }

    public Filter<T> filter() {
        return new Filter<>(this);
    }

    public Class<T> getGenericClass() {
        Class<T> tClass = (Class<T>) Object.class;
        for (T t : this) {
            tClass = (Class<T>) t.getClass();
            break;
        }
        return tClass;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (T t : this) {
            stringBuilder.append(t).append(", ");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}