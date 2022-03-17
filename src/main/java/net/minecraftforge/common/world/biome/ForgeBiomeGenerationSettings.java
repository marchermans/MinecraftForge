package net.minecraftforge.common.world.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.util.holder.set.ForgeCombiningHolderSet;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.world.biome.ForgeBiomeAdaptationManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ForgeBiomeGenerationSettings extends BiomeGenerationSettings {

    private Biome biome;

    private final Lazy<Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>> carversProxy = Lazy.concurrentOf(
            () -> ForgeBiomeAdaptationManager.INSTANCE.getCarvers().apply(biome.getHolder(), super.getActiveCarvers())
    );
    private final Lazy<List<HolderSet<PlacedFeature>>> featuresProxy = Lazy.concurrentOf(
            () -> ForgeBiomeAdaptationManager.INSTANCE.getFeatures().apply(biome.getHolder(), super.getActiveFeatures())
    );
    private final Lazy<List<ConfiguredFeature<?, ?>>> flowerFeaturesProxy = Lazy.concurrentOf(
            () -> featuresProxy.get().stream()
                    .flatMap(HolderSet::stream)
                    .map(Holder::value)
                    .flatMap(PlacedFeature::getFeatures)
                    .filter((configuredFeature) -> configuredFeature.feature() == Feature.FLOWER)
                    .collect(ImmutableList.toImmutableList())
    );
    private final Lazy<Set<PlacedFeature>> featureSetProxy = Lazy.concurrentOf(
            () -> featuresProxy.get().stream()
                    .flatMap(HolderSet::stream)
                    .map(Holder::value)
                    .collect(Collectors.toSet())
    );
    private final Lazy<Set<GenerationStep.Carving>> carversViewProxy = Lazy.concurrentOf(
            () -> java.util.Collections.unmodifiableSet(carversProxy.get().keySet())
    );

    public ForgeBiomeGenerationSettings(
            BiomeGenerationSettings generationSettings,
            Biome biome
    ) {
        super(generationSettings.getActiveCarvers(), generationSettings.getActiveFeatures());
        this.biome = biome;
    }

    @Override
    public @NotNull Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> getActiveCarvers() {
        return carversProxy.get();
    }

    @Override
    public @NotNull List<HolderSet<PlacedFeature>> getActiveFeatures() {
        return featuresProxy.get();
    }

    @Override
    public @NotNull Supplier<List<ConfiguredFeature<?, ?>>> getActiveFlowerFeatures() {
        return flowerFeaturesProxy;
    }

    @Override
    public @NotNull Supplier<Set<PlacedFeature>> getActiveFeatureSet() {
        return featureSetProxy;
    }

    @Override
    public @NotNull Set<GenerationStep.Carving> getActiveCarversView() {
        return carversViewProxy.get();
    }
}
