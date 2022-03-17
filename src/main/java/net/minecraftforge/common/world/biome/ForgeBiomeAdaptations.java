package net.minecraftforge.common.world.biome;

import com.google.common.collect.*;
import net.minecraft.core.HolderSet;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.util.WeightedRandomListUtils;
import net.minecraftforge.common.util.holder.set.ForgeCombiningHolderSet;
import net.minecraftforge.common.util.holder.set.ForgeEntryRemovingHolderSet;
import net.minecraftforge.common.world.biome.adaptions.CarverAdaption;
import net.minecraftforge.common.world.biome.adaptions.FeatureAdaptation;
import net.minecraftforge.common.world.biome.adaptions.MobSpawnCostAdaptation;
import net.minecraftforge.common.world.biome.adaptions.SpawnersAdaptation;

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

    public static ForgeBiomeAdaptation<Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>>, SpawnersAdaptation> spawners() {
        return spawners(Lists.newArrayList());
    }

    public static ForgeBiomeAdaptation<Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>>, SpawnersAdaptation> spawners(List<SpawnersAdaptation> spawners) {
        return new ForgeBiomeAdaptation<>(
                spawnerMap -> {
                    final Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> result = new HashMap<>();
                    spawnerMap.forEach((c, h) -> result.put(c, WeightedRandomListUtils.clone(h, spawnerData -> new MobSpawnSettings.SpawnerData(spawnerData.type, spawnerData.getWeight(), spawnerData.minCount, spawnerData.maxCount))));
                    return result;
                },
                (spawnerMap, toRemove) -> toRemove.spawners().forEach((mobCategory, spawnerDataWeightedRandomList) -> spawnerMap.computeIfPresent(mobCategory, (containedCategory, current) -> WeightedRandomListUtils.removeAll(current, spawnerDataWeightedRandomList))),
                (spawnerMap, toAdd) -> {
                    toAdd.spawners().forEach((mobCategory, spawnerDataWeightedRandomList) -> {
                        spawnerMap.computeIfPresent(mobCategory, (currentCategory, current) -> WeightedRandomListUtils.combine(current, spawnerDataWeightedRandomList));
                        spawnerMap.computeIfAbsent(mobCategory, (currentCategory) -> spawnerDataWeightedRandomList);
                    });
                },
                spawners
        );
    }

    public static ForgeBiomeAdaptation<Map<EntityType<?>, MobSpawnSettings.MobSpawnCost>, MobSpawnCostAdaptation> mobSpawnCosts() {
        return mobSpawnCosts(Lists.newArrayList());
    }

    public static ForgeBiomeAdaptation<Map<EntityType<?>, MobSpawnSettings.MobSpawnCost>, MobSpawnCostAdaptation> mobSpawnCosts(List<MobSpawnCostAdaptation> mobSpawnCostAdaptations) {
        return new ForgeBiomeAdaptation<>(
                mobSpawnCostMap -> {
                    final Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> result = new HashMap<>();
                    mobSpawnCostMap.forEach((type, costs) -> result.put(type, new MobSpawnSettings.MobSpawnCost(costs.getEnergyBudget(), costs.getCharge())));
                    return result;
                },
                (mobSpawnCostMap, toRemove) -> {
                    toRemove.spawnCosts().forEach(mobSpawnCostMap::remove);
                },
                (mobSpawnCostMap, toAdd) -> {
                    mobSpawnCostMap.putAll(toAdd.spawnCosts());
                },
                mobSpawnCostAdaptations
        );
    }
}
