package net.minecraftforge.common.world.biome;

import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderSet;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.biome.adaptions.CarverAdaption;
import net.minecraftforge.common.world.biome.adaptions.FeatureAdaptation;
import net.minecraftforge.event.async.OnWorldRegistriesLoadedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ForgeBiomeAdaptationManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ForgeBiomeAdaptationManager INSTANCE = new ForgeBiomeAdaptationManager();

    private ForgeBiomeAdaptation<Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>, CarverAdaption> carvers = ForgeBiomeAdaptations.carvers();
    private ForgeBiomeAdaptation<List<HolderSet<PlacedFeature>>, FeatureAdaptation> features = ForgeBiomeAdaptations.features();


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
    }

    public ForgeBiomeAdaptation<Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>, CarverAdaption> getCarvers() {
        return carvers;
    }

    public ForgeBiomeAdaptation<List<HolderSet<PlacedFeature>>, FeatureAdaptation> getFeatures() {
        return features;
    }

    private void setCarvers(ForgeBiomeAdaptation<Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>, CarverAdaption> carvers) {
        this.carvers = carvers;
    }

    private void setFeatures(ForgeBiomeAdaptation<List<HolderSet<PlacedFeature>>, FeatureAdaptation> features) {
        this.features = features;
    }
}
