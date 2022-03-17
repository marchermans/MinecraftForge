package net.minecraftforge.common.world.biome.adaptions;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.slf4j.Logger;

import java.util.Map;

public record SpawnersAdaptation(HolderSet<Biome> targetBiomes, AdaptationType adaptationType, Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> spawners) implements IBiomeAdaptation {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static MapCodec<SpawnersAdaptation> DIRECT_CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
            RegistryCodecs.homogeneousList(Registry.BIOME_REGISTRY).fieldOf("target").forGetter(SpawnersAdaptation::targetBiomes),
            AdaptationType.CODEC.optionalFieldOf("type", AdaptationType.ADDITION).forGetter(SpawnersAdaptation::adaptationType),
            Codec.simpleMap(MobCategory.CODEC, WeightedRandomList.codec(MobSpawnSettings.SpawnerData.CODEC).promotePartial(Util.prefix("Spawn data: ", LOGGER::error)), StringRepresentable.keys(MobCategory.values())).fieldOf("spawners").forGetter(SpawnersAdaptation::spawners)
    ).apply(builder, SpawnersAdaptation::new));
}
