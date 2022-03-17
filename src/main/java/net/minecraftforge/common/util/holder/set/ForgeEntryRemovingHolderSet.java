package net.minecraftforge.common.util.holder.set;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ForgeEntryRemovingHolderSet<T> extends HolderSet.ListBacked<T> {

    public static <G> HolderSet<G> fromWithout(HolderSet<G> current, HolderSet<G> toRemove) {
        return new ForgeEntryRemovingHolderSet<>(current, toRemove);
    }

    private final HolderSet<T> source;
    private final HolderSet<T> toRemove;

    private ForgeEntryRemovingHolderSet(HolderSet<T> source, HolderSet<T> toRemove) {
        this.source = source;
        this.toRemove = toRemove;
    }

    @Override
    public @NotNull Either<TagKey<T>, List<Holder<T>>> unwrap() {
        return Either.right(
                source.stream()
                        .filter(holder -> !toRemove.contains(holder))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public boolean contains(@NotNull Holder<T> holder) {
        return source.contains(holder) && !toRemove.contains(holder);
    }

    @Override
    protected @NotNull List<Holder<T>> contents() {
        return unwrap().right().orElseThrow();
    }
}
