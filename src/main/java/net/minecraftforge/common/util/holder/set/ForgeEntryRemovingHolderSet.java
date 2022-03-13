package net.minecraftforge.common.util.holder.set;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ForgeEntryRemovingHolderSet<T> extends HolderSet.ListBacked<T> {

    public static <G> HolderSet<G> fromWithout(final HolderSet<G> source, Holder<G>... toExclude) {
        if (toExclude.length == 0)
            return source;

        if (source instanceof HolderSet.ListBacked<G> listBacked) {
            final List<Holder<G>> copyOfSource = listBacked.stream().collect(Collectors.toList());
            copyOfSource.removeAll(List.of(toExclude));
            return new ForgeEntryRemovingHolderSet<>(copyOfSource);
        }

        final Either<TagKey<G>, List<Holder<G>>> unwrapped = source.unwrap();
        if (unwrapped.left().isPresent() || unwrapped.right().isEmpty()) {
            throw new IllegalArgumentException("Can not use referenced holder set for a removal filtered set.");
        }

        final List<Holder<G>> copyOfSource = new ArrayList<>(unwrapped.right().get());
        copyOfSource.removeAll(List.of(toExclude));
        return new ForgeEntryRemovingHolderSet<>(copyOfSource);
    }

    public static <G> HolderSet<G> fromWithout(final HolderSet<G> source, HolderSet<G> toExclude) {
        if (toExclude.size() == 0)
            return source;

        if (source instanceof HolderSet.ListBacked<G> listBacked) {
            final List<Holder<G>> copyOfSource = listBacked.stream().collect(Collectors.toList());
            copyOfSource.removeAll(toExclude.stream().toList());
            return new ForgeEntryRemovingHolderSet<>(copyOfSource);
        }

        final Either<TagKey<G>, List<Holder<G>>> unwrapped = source.unwrap();
        if (unwrapped.left().isPresent() || unwrapped.right().isEmpty()) {
            throw new IllegalArgumentException("Can not use referenced holder set for a removal filtered set.");
        }

        final List<Holder<G>> copyOfSource = new ArrayList<>(unwrapped.right().get());
        copyOfSource.removeAll(toExclude.stream().toList());
        return new ForgeEntryRemovingHolderSet<>(copyOfSource);
    }


    private final List<Holder<T>> resultingSet;

    private ForgeEntryRemovingHolderSet(List<Holder<T>> resultingSet) {
        this.resultingSet = resultingSet;
    }

    @Override
    public Either<TagKey<T>, List<Holder<T>>> unwrap() {
        return null;
    }

    @Override
    public boolean contains(Holder<T> holder) {
        return resultingSet.contains(holder);
    }

    @Override
    protected List<Holder<T>> contents() {
        return resultingSet;
    }
}
