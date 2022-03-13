package net.minecraftforge.common.world.biome;

import com.google.common.collect.*;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.util.holder.set.ForgeCombiningHolderSet;
import net.minecraftforge.common.util.holder.set.ForgeEntryRemovingHolderSet;
import net.minecraftforge.common.world.biome.adaptions.CarverAdaption;
import net.minecraftforge.common.world.biome.adaptions.FeatureAdaptation;

import java.util.*;

class ForgeBiomeAdaptations {
    public static ForgeBiomeAdaptation<Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>, CarverAdaption> carvers() {
        return carvers(new ArrayList<>());
    }

    public static ForgeBiomeAdaptation<Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>, CarverAdaption> carvers(List<CarverAdaption> carverAdaptions) {
        return new ForgeBiomeAdaptation<>(
                carverMap -> {
                    final Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> result = new HashMap<>();
                    carverMap.forEach((c, h) -> result.put(c, ForgeCombiningHolderSet.of(h)));
                    return result;
                },
                (carverMap, toRemove) -> {
                    carverMap.computeIfPresent(toRemove.carvingStage(), (step, current) -> ForgeEntryRemovingHolderSet.fromWithout(current, toRemove.carvers()));
                },
                (carverMap, toAdd) -> {
                    carverMap.computeIfPresent(toAdd.carvingStage(), (step, current) -> ForgeCombiningHolderSet.of(current, toAdd.carvers()));
                    carverMap.computeIfAbsent(toAdd.carvingStage(), (step) -> toAdd.carvers());
                },
                carverAdaptions
        );
    }

    public static ForgeBiomeAdaptation<List<HolderSet<PlacedFeature>>, FeatureAdaptation> features() {
        return features(Lists.newArrayList());
    }

    public static ForgeBiomeAdaptation<List<HolderSet<PlacedFeature>>, FeatureAdaptation> features(final List<FeatureAdaptation> featureAdaptations) {
        return new ForgeBiomeAdaptation<>(
                featureSetList -> {
                    final List<HolderSet<PlacedFeature>> result = new ArrayList<>();
                    featureSetList.forEach(set -> result.add(ForgeCombiningHolderSet.of(set)));
                    return result;
                },
                (featureSetList, toRemove) -> {
                    if (featureSetList.size() <= toRemove.decorationStep().ordinal())
                        return;

                    featureSetList.set(toRemove.decorationStep().ordinal(), ForgeEntryRemovingHolderSet.fromWithout(
                            featureSetList.get(toRemove.decorationStep().ordinal()),
                            toRemove.features()
                    ));
                },
                (featureSetList, toAdd) -> {
                    if (featureSetList.size() <= toAdd.decorationStep().ordinal()) {
                        for (int i = featureSetList.size(); i < toAdd.decorationStep().ordinal(); i++) {
                            featureSetList.add(HolderSet.direct());
                        }
                    }

                    featureSetList.set(toAdd.decorationStep().ordinal(), ForgeCombiningHolderSet.of(
                            featureSetList.get(toAdd.decorationStep().ordinal()),
                            toAdd.features()
                    ));
                },
                featureAdaptations
        );
    }
}
