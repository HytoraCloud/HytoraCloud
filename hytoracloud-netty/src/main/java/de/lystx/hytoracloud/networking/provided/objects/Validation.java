package de.lystx.hytoracloud.networking.provided.objects;

import lombok.Getter;

import java.util.regex.Pattern;

public enum Validation {

    INSTANCE_NAME("([A-Z0-9]{3,26})"),
    DEFAULT_NAME("([0-9A-Z_]{1,48})*"),
    SIMPLE_NAME("([A-Z_ÄÖÜ]{3,48})*"),
    SIMPLE_STRING("[^¶Þþ]*"),
    NORMAL_STRING("[^;#$¶Þþ]*"),
    COLOR("(&[0-9A-FK-OR])"),
    PERMISSION("[bs*][:]([^.*][-]?[0-9_.A-Za-z*]+[*a-zA-Z]|[*])|[*]"),
    PLAYERNAME("[a-zA-Z_0-9]{2,16}"),
    UNIQUEID("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"),
    BINARY_UNIQUEID("(\\{\"\\$binary\":\"[0-9a-z=+]{24}\",\"\\$type\":\"3\"})"),
    BINARY_UNIQUEID_JSON("(\\{\"[0-9a-z]*\":\\{\"\\$binary\":\"[0-9a-z=+/]{24}\",\"\\$type\":\"3\"}})"),
    INTEGER("[-]?[0-9]{1,10}"),
    LONG("[-]?[0-9]{11,20}"),
    NUMBER("[-]?[0-9]{1,20}(.[0-9]+)?"),
    DOUBLE("[-]?[0-9]{1,20}[.][0-9]+"),
    LIST("\\[([^,]*, )*[^,]*\\]"),
    IP("(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])"
            + "|([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}|"
            + "((http://|https://)?(www.)?(([a-zA-Z0-9-]){2,}\\.){1,4}([a-zA-Z]){2,6}(/([a-zA-Z-_/\\.0-9#:?=&;,]*)?)?)"),
    TIME("[0-9]*[dhms]"),
    RAM("[0-9]*(K|M){1}"),
    MESSAGE_COMP_EVENT_PART("[0-9]?\"[^\"]*\",?"),
    MESSAGE_COMP_EVENT("\\$\\{(" + MESSAGE_COMP_EVENT_PART.getRawRegex() + ",?){3}}\\$"),;

    @Getter
    private Pattern pattern;
    private String regex;

    Validation(String regex) {
        this.regex = regex;

        try {
            this.pattern = Pattern.compile(getRegex());
        }
        catch(Exception e) {
            //
        }
    }

    public String getRawRegex() {
        return regex;
    }

    public String getRegex() {
        return "^(?i)" + regex + "$";
    }

    public boolean matches(String s) {
        return s != null && pattern.matcher(s).matches();
    }

    public static Validation from(int id) {
        if(id < 0 || id > values().length) return Validation.SIMPLE_STRING;
        return Validation.values()[id];
    }

}
