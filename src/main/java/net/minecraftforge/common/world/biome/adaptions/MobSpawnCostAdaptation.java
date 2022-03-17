package net.minecraftforge.common.world.biome.adaptions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.Map;

public record MobSpawnCostAdaptation(HolderSet<Biome> targetBiomes, AdaptationType adaptationType, Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> spawnCosts) implements IBiomeAdaptation {

    public static MapCodec<MobSpawnCostAdaptation> DIRECT_CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
            RegistryCodecs.homogeneousList(Registry.BIOME_REGISTRY).fieldOf("target").forGetter(MobSpawnCostAdaptation::targetBiomes),
            AdaptationType.CODEC.optionalFieldOf("type", AdaptationType.ADDITION).forGetter(MobSpawnCostAdaptation::adaptationType),
            Codec.simpleMap(Registry.ENTITY_TYPE.byNameCodec(), MobSpawnSettings.MobSpawnCost.CODEC, Registry.ENTITY_TYPE).fieldOf("spawn_costs").forGetter(MobSpawnCostAdaptation::spawnCosts)
    ).apply(builder, MobSpawnCostAdaptation::new));
}
