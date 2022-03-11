package net.minecraftforge.common.world.biome;

import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderSet;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.biome.adaptions.CarverAdaption;
import net.minecraftforge.common.world.biome.adaptions.FeatureAdaptation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ForgeBiomeAdaptationManager implements PreparableReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ForgeBiomeAdaptationManager INSTANCE = new ForgeBiomeAdaptationManager();

    private ForgeBiomeAdaptation<Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>>, CarverAdaption> carvers = ForgeBiomeAdaptations.carvers();
    private ForgeBiomeAdaptation<List<HolderSet<PlacedFeature>>, FeatureAdaptation> features = ForgeBiomeAdaptations.features();

    @Override
    public @NotNull CompletableFuture<Void> reload(
      final @NotNull PreparationBarrier stage,
      final @NotNull ResourceManager resourceManager,
      final @NotNull ProfilerFiller preparationsProfiler,
      final @NotNull ProfilerFiller reloadProfiler,
      final @NotNull Executor backgroundExecutor,
      final @NotNull Executor gameExecutor
    ) {
        LOGGER.info("Reloading biome adaptions");

        final CompletableFuture<List<CarverAdaption>> carverLoaders = ForgeBiomeAdaptationLoaders.loadCarvers(resourceManager, backgroundExecutor);
        final CompletableFuture<List<FeatureAdaptation>> featureLoaders = ForgeBiomeAdaptationLoaders.loadFeatures(resourceManager, backgroundExecutor);

        return CompletableFuture.allOf(
                carverLoaders.thenAccept(carverAdaptions -> setCarvers(ForgeBiomeAdaptations.carvers(carverAdaptions))),
                featureLoaders.thenAccept(featureAdaptations -> setFeatures(ForgeBiomeAdaptations.features(featureAdaptations)))
        ).thenCompose(stage::wait);
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
