package net.minecraftforge.common.util.holder.set;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ForgeCombiningHolderSet<T> extends HolderSet.ListBacked<T> {

    @SafeVarargs
    public static <G> HolderSet<G> of(HolderSet<G> holders, HolderSet<G>... holders2) {
        final List<HolderSet<G>> holderSets = new ArrayList<>();
        holderSets.add(holders);
        holderSets.addAll(List.of(holders2));

        return new ForgeCombiningHolderSet<G>(holderSets);
    }

    private final List<HolderSet<T>> others;

    private final Supplier<List<Holder<T>>> contents = Lazy.of(() -> unwrap().right().orElseThrow());

    public ForgeCombiningHolderSet(Collection<HolderSet<T>> others) {
        this.others = Lists.newArrayList(others);
    }

    public ForgeCombiningHolderSet(List<HolderSet<T>> others) {
        this.others = others;
    }

    @Override
    public @NotNull Either<TagKey<T>, List<Holder<T>>> unwrap() {
        return Either.right(
                others.stream()
                        .map(HolderSet::unwrap)
                        .flatMap(unwrapped -> unwrapped
                                .right()
                                .orElseThrow(() ->
                                        new IllegalStateException("Can not unwrap a holder which is tag based: " +
                                                unwrapped.left().map(TagKey::toString).orElse("UNKNOWN TAG")))
                                .stream()
                        )
                        .toList());
    }

    @Override
    public boolean contains(@NotNull Holder<T> p_205799_) {
        return contents().contains(p_205799_);
    }

    @Override
    protected @NotNull List<Holder<T>> contents() {
        return this.contents.get();
    }

    @Override
    public String toString() {
        return "ForgeCombiningHolderSet{" +
                "others=" + others +
                '}';
    }
}
