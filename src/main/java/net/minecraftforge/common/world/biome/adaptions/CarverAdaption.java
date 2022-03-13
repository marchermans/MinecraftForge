package net.minecraftforge.common.world.biome.adaptions;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import org.slf4j.Logger;

public record CarverAdaption(HolderSet<Biome> targetBiomes, AdaptationType adaptationType, GenerationStep.Carving carvingStage, HolderSet<ConfiguredWorldCarver<?>> carvers) implements IBiomeAdaptation {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static MapCodec<CarverAdaption> DIRECT_CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
            RegistryCodecs.homogeneousList(Registry.BIOME_REGISTRY).fieldOf("target").forGetter(CarverAdaption::targetBiomes),
            AdaptationType.CODEC.optionalFieldOf("type", AdaptationType.ADDITION).forGetter(CarverAdaption::adaptationType),
            GenerationStep.Carving.CODEC.fieldOf("step").forGetter(CarverAdaption::carvingStage),
            ConfiguredWorldCarver.LIST_CODEC.promotePartial(Util.prefix("Carver: ", LOGGER::error)).fieldOf("carvers").forGetter(CarverAdaption::carvers)
    ).apply(builder, CarverAdaption::new));
}
