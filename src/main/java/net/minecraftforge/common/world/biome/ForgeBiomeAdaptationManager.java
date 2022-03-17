package net.minecraftforge.common.world.biome;

import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderSet;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.biome.adaptions.CarverAdaption;
import net.minecraftforge.common.world.biome.adaptions.FeatureAdaptation;
import net.minecraftforge.common.world.biome.adaptions.MobSpawnCostAdaptation;
import net.minecraftforge.common.world.biome.adaptions.SpawnersAdaptation;
import net.minecraftforge.event.async.OnWorldRegistriesLoadedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public class ForgeBiomeAdaptationManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ForgeBiomeAdaptationManager INSTANCE = new ForgeBiomeAdaptationManager();

    private ForgeBiomeAdaptation<Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>, CarverAdaption> carvers = ForgeBiomeAdaptations.carvers();
    private ForgeBiomeAdaptation<List<HolderSet<PlacedFeature>>, FeatureAdaptation> features = ForgeBiomeAdaptations.features();

    private ForgeBiomeAdaptation<Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>>, SpawnersAdaptation> spawners = ForgeBiomeAdaptations.spawners();
    private ForgeBiomeAdaptation<Map<EntityType<?>, MobSpawnSettings.MobSpawnCost>, MobSpawnCostAdaptation> mobSpawnCosts = ForgeBiomeAdaptations.mobSpawnCosts();
    //Map<EntityType<?>, MobSpawnCost>
    @SubscribeEvent
    public void reload(final OnWorldRegistriesLoadedEvent event)
    {
        LOGGER.info("Reloading biome adaptions");
        event.withAsyncTask(configurator ->
                configurator.async(() -> ForgeBiomeAdaptationLoaders.loadCarvers(event.getResourceManager(), event.getRegistryOps()))
                        .sync(carvers -> setCarvers(ForgeBiomeAdaptations.carvers(carvers))));
        event.withAsyncTask(configurator ->
                configurator.async(() -> ForgeBiomeAdaptationLoaders.loadFeatures(event.getResourceManager(), event.getRegistryOps()))
                        .sync(features -> setFeatures(ForgeBiomeAdaptations.features(features))));
        event.withAsyncTask(configurator ->
                configurator.async(() -> ForgeBiomeAdaptationLoaders.loadSpawners(event.getResourceManager(), event.getRegistryOps()))
                        .sync(spawners -> setSpawners(ForgeBiomeAdaptations.spawners(spawners))));
        event.withAsyncTask(configurator ->
                configurator.async(() -> ForgeBiomeAdaptationLoaders.loadMobSpawnCosts(event.getResourceManager(), event.getRegistryOps()))
                        .sync(mobSpawnCosts -> setMobSpawnCosts(ForgeBiomeAdaptations.mobSpawnCosts(mobSpawnCosts))));
    }

    public ForgeBiomeAdaptation<Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>, CarverAdaption> getCarvers() {
        return carvers;
    }

    public ForgeBiomeAdaptation<List<HolderSet<PlacedFeature>>, FeatureAdaptation> getFeatures() {
        return features;
    }

    public ForgeBiomeAdaptation<Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>>, SpawnersAdaptation> getSpawners() {
        return spawners;
    }

    public ForgeBiomeAdaptation<Map<EntityType<?>, MobSpawnSettings.MobSpawnCost>, MobSpawnCostAdaptation> getMobSpawnCosts() {
        return mobSpawnCosts;
    }

    private void setCarvers(ForgeBiomeAdaptation<Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>, CarverAdaption> carvers) {
        this.carvers = carvers;
    }

    private void setFeatures(ForgeBiomeAdaptation<List<HolderSet<PlacedFeature>>, FeatureAdaptation> features) {
        this.features = features;
    }

    private void setSpawners(ForgeBiomeAdaptation<Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>>, SpawnersAdaptation> spawners) {
        this.spawners = spawners;
    }

    private void setMobSpawnCosts(ForgeBiomeAdaptation<Map<EntityType<?>, MobSpawnSettings.MobSpawnCost>, MobSpawnCostAdaptation> mobSpawnCosts) {
        this.mobSpawnCosts = mobSpawnCosts;
    }
}
