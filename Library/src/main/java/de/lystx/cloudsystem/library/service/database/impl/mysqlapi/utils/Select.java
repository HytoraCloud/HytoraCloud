package de.lystx.cloudsystem.library.service.database.impl.mysqlapi.utils;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Select {
    private String table = "";
    private String columns = "";
    private String filter = "";

    public Select() {}

    public Select(String table, String columns, String filter) {
        this.table = table;
        this.columns = columns;
        this.filter = filter;
    }

    public String getColumns() {
        if(columns == null) {
            return "*";
        } else {
            return columns;
        }
    }
}
