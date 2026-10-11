package org.echanneling.designpattern.factory;

/** Product interface used by ProfileFactory. */
public interface ManagedProfile {
    int getId();
    String getDisplayName();
    String getType();
}
