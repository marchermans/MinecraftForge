package net.minecraftforge.common.world.biome;

import com.google.common.collect.*;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.ForgeCombiningHolderSet;
import net.minecraftforge.common.world.biome.adaptions.CarverAdaption;
import net.minecraftforge.common.world.biome.adaptions.FeatureAdaptation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ForgeBiomeAdaptations {
    public static ForgeBiomeAdaptation<Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>, CarverAdaption> carvers() {
        return carvers(new ArrayList<>());
    }

    public static ForgeBiomeAdaptation<Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>, CarverAdaption> carvers(List<CarverAdaption> carverAdaptions) {
        return new ForgeBiomeAdaptation<>(adaptationsToConvert -> {
            final Map<GenerationStep.Carving, List<HolderSet<ConfiguredWorldCarver<?>>>> resultingCarvers = Maps.newHashMap();

            adaptationsToConvert.forEach(a -> {
                resultingCarvers.computeIfAbsent(a.carvingStage(), (s) -> new ArrayList<>()).add(a.carvers());
            });

            final ImmutableMap.Builder<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> resultBuilder = ImmutableMap.builder();
            resultingCarvers.forEach((carving, holders) -> resultBuilder.put(carving, new ForgeCombiningHolderSet<>(holders)));

            return resultBuilder.build();
        }, carverAdaptions);
    }

    public static ForgeBiomeAdaptation<List<HolderSet<PlacedFeature>>, FeatureAdaptation> features() {
        return features(Lists.newArrayList());
    }

    public static ForgeBiomeAdaptation<List<HolderSet<PlacedFeature>>, FeatureAdaptation> features(final List<FeatureAdaptation> featureAdaptations) {
        return new ForgeBiomeAdaptation<>(adaptationsToConvert -> {
            final Multimap<Integer, HolderSet<PlacedFeature>> featuresByStages = HashMultimap.create();

            adaptationsToConvert.forEach(a -> featuresByStages.put(a.decorationStep().ordinal(), a.features()));

            final int maxStep = featuresByStages.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);

            final List<HolderSet<PlacedFeature>> result = new ArrayList<>();
            for (int i = 0; i <= maxStep; i++) {
                result.add(new ForgeCombiningHolderSet<>(featuresByStages.get(i)));
            }

            return result;
        }, featureAdaptations);
    }
}
