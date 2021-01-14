package de.lystx.cloudsystem.library.service.network.connection.channel.base;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Identifier {

    private final String id;

    public Identifier(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
