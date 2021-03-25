package de.lystx.cloudsystem.library.elements.list;

import de.lystx.cloudsystem.library.elements.interfaces.Acceptable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Promise<T> {

    private final CloudList<T> cloudList;
    private final Acceptable<T> acceptable;

    public CloudList<T> findAll() {
        CloudList<T> cloudList = new CloudList<>();
        for (T t : this.cloudList) {
            if (this.acceptable.isAccepted(t)) {
                cloudList.add(t).queue();
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
