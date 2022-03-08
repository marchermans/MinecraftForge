package net.minecraftforge.common.world.biome;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.common.world.biome.adaptions.CarverAdaption;
import net.minecraftforge.common.world.biome.adaptions.FeatureAdaptation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ForgeBiomeAdaptationLoaders {
    public static CompletableFuture<List<CarverAdaption>> loadCarvers(final ResourceManager manager, final Executor executor) {
        return load(manager, executor, "adaptations/biome/carvers", CarverAdaption.DIRECT_CODEC.codec());
    }

    public static CompletableFuture<List<FeatureAdaptation>> loadFeatures(final ResourceManager manager, final Executor executor) {
        return load(manager, executor, "adaptations/biome/features", FeatureAdaptation.DIRECT_CODEC.codec());
    }

    private static <T> CompletableFuture<List<T>> load(final ResourceManager manager, final Executor executor, final String path, final Codec<T> codec){
        return CompletableFuture.supplyAsync(() -> manager.listResources(path, (file) -> file.endsWith(".json")).stream()
                .map(filePath -> LamdbaExceptionUtils.uncheck(() -> manager.getResource(filePath)))
                .map(resource -> LamdbaExceptionUtils.uncheck(resource::getInputStream))
                .map(inputStream -> {
                    BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    final T result = codec.parse(JsonOps.INSTANCE, JsonParser.parseReader(bufferedreader)).result().orElseThrow();
                    LamdbaExceptionUtils.uncheck(inputStream::close);
                    return result;
                }).toList(), executor);
    }

}
