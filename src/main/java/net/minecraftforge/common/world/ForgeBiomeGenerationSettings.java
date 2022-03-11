package net.minecraftforge.common.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.ForgeCombiningHolderSet;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.world.biome.ForgeBiomeAdaptationManager;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ForgeBiomeGenerationSettings extends BiomeGenerationSettings {

    private Biome biome;

    private final Lazy.Resettable<Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>> adaptedCarvers = Lazy.resettableOf(
            () -> ForgeBiomeAdaptationManager.INSTANCE.getCarvers().get(biome.getHolder())
    );
    private final Lazy.Resettable<List<HolderSet<PlacedFeature>>> adaptedFeatures = Lazy.resettableOf(
            () -> ForgeBiomeAdaptationManager.INSTANCE.getFeatures().get(biome.getHolder())
    );

    private final Lazy.Resettable<Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>> carversProxy = Lazy.resettableOf(
            () -> {
                final Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> result = new HashMap<>(super.getActiveCarvers());
                adaptedCarvers.get().forEach((carving, holders) -> result.merge(carving, holders, ForgeCombiningHolderSet::combine));
                return result;
            }
    );
    private final Lazy.Resettable<List<HolderSet<PlacedFeature>>> featuresProxy = Lazy.resettableOf(
            () -> {
                final List<HolderSet<PlacedFeature>> result = new ArrayList<>();
                final List<HolderSet<PlacedFeature>> predefinedFeatures = super.getActiveFeatures();

                final int maxLayer = Math.max(predefinedFeatures.size(), adaptedFeatures.get().size());
                for (int i = 0; i < maxLayer; i++) {
                    final List<HolderSet<PlacedFeature>> layerSets = new ArrayList<>();

                    if (i < predefinedFeatures.size()) {
                        layerSets.add(predefinedFeatures.get(i));
                    }

                    if (i < adaptedFeatures.get().size()) {
                        layerSets.add(adaptedFeatures.get().get(i));
                    }

                    result.add(new ForgeCombiningHolderSet<>(layerSets));
                }

                return result;
            }
    );
    private final Lazy.Resettable<List<ConfiguredFeature<?, ?>>> flowerFeaturesProxy = Lazy.resettableOf(
            () -> featuresProxy.get().stream()
                    .flatMap(HolderSet::stream)
                    .map(Holder::value)
                    .flatMap(PlacedFeature::getFeatures)
                    .filter((configuredFeature) -> configuredFeature.feature() == Feature.FLOWER)
                    .collect(ImmutableList.toImmutableList())
    );
    private final Lazy.Resettable<Set<PlacedFeature>> featureSetProxy = Lazy.resettableOf(
            () -> featuresProxy.get().stream()
                    .flatMap(HolderSet::stream)
                    .map(Holder::value)
                    .collect(Collectors.toSet())
    );
    private final Lazy.Resettable<Set<GenerationStep.Carving>> carversViewProxy = Lazy.resettableOf(
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
    public Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> getActiveCarvers() {
        return carversProxy.get();
    }

    @Override
    public List<HolderSet<PlacedFeature>> getActiveFeatures() {
        return featuresProxy.get();
    }

    @Override
    public Supplier<List<ConfiguredFeature<?, ?>>> getActiveFlowerFeatures() {
        return flowerFeaturesProxy;
    }

    @Override
    public Supplier<Set<PlacedFeature>> getActiveFeatureSet() {
        return featureSetProxy;
    }

    @Override
    public Set<GenerationStep.Carving> getActiveCarversView() {
        return carversViewProxy.get();
    }
}
