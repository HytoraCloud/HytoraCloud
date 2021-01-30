package de.lystx.cloudsystem.library.service.database.impl.mysqlapi.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@Getter
public class Result {
    private final List<Row> rows;

    public Result() {
        this.rows = new LinkedList<>();
    }

    public void addrow(Row row) {
        rows.add(row);
    }

}
