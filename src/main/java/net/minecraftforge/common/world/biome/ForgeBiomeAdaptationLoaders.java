package net.minecraftforge.common.world.biome;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.common.world.biome.adaptions.CarverAdaption;
import net.minecraftforge.common.world.biome.adaptions.FeatureAdaptation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ForgeBiomeAdaptationLoaders {
    public static List<CarverAdaption> loadCarvers(final ResourceManager manager, DynamicOps<? super JsonElement> registryOps) {
        return load(manager, "adaptations/biome/carvers", CarverAdaption.DIRECT_CODEC.codec(), registryOps);
    }

    public static List<FeatureAdaptation> loadFeatures(final ResourceManager manager, DynamicOps<? super JsonElement> registryOps) {
        return load(manager, "adaptations/biome/features", FeatureAdaptation.DIRECT_CODEC.codec(), registryOps);
    }

    private static <T> List<T> load(final ResourceManager manager, final String path, final Codec<T> codec, DynamicOps<? super JsonElement> registryOps){
        return manager.listResources(path, (file) -> file.endsWith(".json")).stream()
                .map(filePath -> LamdbaExceptionUtils.uncheck(() -> manager.getResource(filePath)))
                .map(resource -> LamdbaExceptionUtils.uncheck(resource::getInputStream))
                .map(inputStream -> {
                    BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    final DataResult<T> result = codec.parse(registryOps, JsonParser.parseReader(bufferedreader));
                    if (result.error().isPresent()) {
                        throw new IllegalStateException(result.error().get().message());
                    }
                    LamdbaExceptionUtils.uncheck(inputStream::close);
                    return result.result().orElseThrow();
                }).toList();
    }

}
