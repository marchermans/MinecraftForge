package net.minecraftforge.common.world.biome;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.biome.adaptions.AdaptationType;
import net.minecraftforge.common.world.biome.adaptions.IBiomeAdaptation;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ForgeBiomeAdaptation<R, A extends IBiomeAdaptation> {
    private final Function<R, R> resultConstructor;
    private final BiConsumer<R, A> remover;
    private final BiConsumer<R, A> adder;
    private final Table<AdaptationType, Holder<Biome>, List<A>> adaptations;

    public ForgeBiomeAdaptation(
            Function<R, R> resultConstructor,
            BiConsumer<R, A> remover,
            BiConsumer<R, A> adder,
            Collection<A> adaptations
    ) {
        this.resultConstructor = resultConstructor;
        this.remover = remover;
        this.adder = adder;
        this.adaptations = HashBasedTable.create();

        adaptations.forEach(a -> a.targetBiomes().forEach(biome -> {
            if (!this.adaptations.contains(a.adaptationType(), biome))
                this.adaptations.put(a.adaptationType(), biome, new ArrayList<>());

            Objects.requireNonNull(this.adaptations.get(a.adaptationType(), biome)).add(a);
        }));
    }


    public R apply(final Holder<Biome> biome, final R input) {
        if (!this.adaptations.columnMap().containsKey(biome))
            return input;

        final R copyOfInput = this.resultConstructor.apply(input);
        final Map<AdaptationType, List<A>> biomeAdaptations = this.adaptations.column(biome);

        biomeAdaptations.getOrDefault(AdaptationType.ADDITION, Collections.emptyList()).forEach(a -> this.adder.accept(copyOfInput, a));
        biomeAdaptations.getOrDefault(AdaptationType.REMOVAL, Collections.emptyList()).forEach(a -> this.remover.accept(copyOfInput, a));

        return copyOfInput;
    }
}
