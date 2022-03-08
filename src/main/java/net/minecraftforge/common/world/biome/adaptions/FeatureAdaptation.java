package net.minecraftforge.common.world.biome.adaptions;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.slf4j.Logger;

public record FeatureAdaptation(HolderSet<Biome> targetBiomes, GenerationStep.Decoration decorationStep, HolderSet<PlacedFeature> features) implements IBiomeAdaptation {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static MapCodec<FeatureAdaptation> DIRECT_CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
            RegistryCodecs.homogeneousList(Registry.BIOME_REGISTRY).fieldOf("target").forGetter(FeatureAdaptation::targetBiomes),
            GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(FeatureAdaptation::decorationStep),
            PlacedFeature.LIST_CODEC.promotePartial(Util.prefix("Features: ", LOGGER::error)).fieldOf("features").forGetter(FeatureAdaptation::features)
    ).apply(builder, FeatureAdaptation::new));
}
