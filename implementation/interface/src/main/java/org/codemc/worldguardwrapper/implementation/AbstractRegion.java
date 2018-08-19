package org.codemc.worldguardwrapper.implementation;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractRegion {

    private String id;

    public abstract Object getFlag(String name);

    public abstract Map<String, Object> getFlags();

    public abstract int getPriority();
    
}