package org.codemc.worldguardwrapper.flags;

import org.bukkit.entity.Player;

import java.util.*;

/**
 * A flag that stores a set of values of the sub flag's type.
 */
public class SetFlag<T> extends AbstractWrappedFlag<Set<T>> {

    private AbstractWrappedFlag<T> subFlag;

    public SetFlag(String name, AbstractWrappedFlag<T> subFlag) {
        this(name, new HashSet<>(), subFlag);
    }

    @SuppressWarnings("unchecked")
    public SetFlag(String name, Set<T> defaultValue, AbstractWrappedFlag<T> subFlag) {
        super(name, (Class<Set<T>>) defaultValue.getClass(), defaultValue);
        this.subFlag = subFlag;
    }

    /**
     * Get the type of values stored in this flag.
     *
     * @return The stored flag type.
     */
    public AbstractWrappedFlag<T> getSubType() {
        return subFlag;
    }

    @Override
    public Set<T> deserialize(Object o) {
        if (o instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) o;
            Set<T> items = new HashSet<T>();

            for (Object sub : collection) {
                T item = subFlag.deserialize(sub);
                if (item != null) {
                    items.add(item);
                }
            }

            return items;
        } else {
            return null;
        }
    }

    @Override
    public Object serialize(Set<T> o) {
        List<Object> list = new ArrayList<Object>();
        for (T item : o) {
            list.add(subFlag.serialize(item));
        }

        return list;
    }

    @Override
    public Set<T> parse(Player player, String userInput) {
        if (userInput.isEmpty()) {
            return new HashSet<>();
        } else {
            Set<T> items = new HashSet<>();

            for (String str : userInput.split(",")) {
                items.add(subFlag.parse(player, str));
            }

            return items;
        }
    }

}
