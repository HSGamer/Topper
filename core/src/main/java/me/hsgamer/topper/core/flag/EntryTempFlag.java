package me.hsgamer.topper.core.flag;

import java.util.Objects;

public class EntryTempFlag {
    public static final EntryTempFlag NEED_SAVING = new EntryTempFlag("needSaving");
    public static final EntryTempFlag IS_SAVING = new EntryTempFlag("isSaving");
    public static final EntryTempFlag IS_UPDATING = new EntryTempFlag("isUpdating");

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
