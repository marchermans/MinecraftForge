package net.minecraftforge.common.world.biome;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.biome.adaptions.IBiomeAdaptation;

import java.util.List;
import java.util.function.Function;

public record ForgeBiomeAdaptation<R, A extends IBiomeAdaptation>(
        Function<List<A>, R> toResultMapper, List<A> adaptations) {

    public R get(final Holder<Biome> biome) {
        return toResultMapper.apply(
                adaptations.stream().filter(a -> a.targetBiomes().contains(biome)).toList()
        );
    }
}
