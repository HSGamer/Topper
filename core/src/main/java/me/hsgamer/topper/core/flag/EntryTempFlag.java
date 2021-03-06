package me.hsgamer.topper.core.flag;

import java.util.Objects;

public class EntryTempFlag {
    public final String name;

    public EntryTempFlag(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntryTempFlag)) return false;
        EntryTempFlag that = (EntryTempFlag) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
