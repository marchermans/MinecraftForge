package net.minecraftforge.common.util;

import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WeightedRandomListUtils {

    public static <E extends WeightedEntry> WeightedRandomList<E> clone(final WeightedRandomList<E> list, Function<E, E> entryCloner) {
        return WeightedRandomList.create(
                list.unwrap().stream().map(entryCloner).collect(Collectors.toList())
        );
    }

    public static <E extends WeightedEntry> WeightedRandomList<E> removeAll(final WeightedRandomList<E> list, WeightedRandomList<E> toRemove) {
        final List<E> toRemoveUnwrapped = toRemove.unwrap();
        return WeightedRandomList.create(
                list.unwrap().stream().filter(e -> !toRemoveUnwrapped.contains(e)).collect(Collectors.toList())
        );
    }

    public static <E extends WeightedEntry> WeightedRandomList<E> combine(final WeightedRandomList<E> list, WeightedRandomList<E> toAdd) {
        final List<E> toAddUnwrapped = toAdd.unwrap();
        return WeightedRandomList.create(
                Stream.concat(
                        list.unwrap().stream(),
                        toAddUnwrapped.stream()
                ).collect(Collectors.toList())
        );
    }


}
