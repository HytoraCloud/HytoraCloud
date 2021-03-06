package de.lystx.hytoracloud.driver.utils.list;

import de.lystx.hytoracloud.driver.commons.interfaces.Acceptable;
import lombok.AllArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class Promise<T> {

    private final CloudList<T> cloudList;
    private final Acceptable<T> acceptable;

    public List<T> findAll() {
        List<T> cloudList = new LinkedList<>();
        for (T t : this.cloudList) {
            if (this.acceptable.isAccepted(t)) {
                cloudList.add(t);
            }
        }
        return cloudList;
    }

    public Optional<T> findAny() {
        for (T t : this.findAll()) {
            if (this.acceptable.isAccepted(t)) {
                return new Optional<>(this, t, null);
            }
        }
        return new Optional<>(this, null, null);
    }

    public Optional<T> findFirst() {
        return new Optional<>(this, this.findAll().get(0), null);
    }
}
