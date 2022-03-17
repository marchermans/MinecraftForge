
package net.minecraftforge.common.world.biome;

import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.util.Lazy;

import java.util.Map;
import java.util.Set;

public class ForgeMobSpawnSettings extends MobSpawnSettings {

    private Biome biome;

    private final Lazy<Map<MobCategory, WeightedRandomList<SpawnerData>>> spawnersProxy = Lazy.concurrentOf(
            () -> ForgeBiomeAdaptationManager.INSTANCE.getSpawners().apply(biome.getHolder(), super.getActiveSpawners())
    );

    private final Lazy<Map<EntityType<?>, MobSpawnCost>> mobSpawnCosts = Lazy.concurrentOf(
            () -> ForgeBiomeAdaptationManager.INSTANCE.getMobSpawnCosts().apply(biome.getHolder(), super.getActiveMobSpawnCosts())
    );


   public ForgeMobSpawnSettings(MobSpawnSettings generationSettings,
                                    Biome biome) {
        super(generationSettings.getCreatureProbability(), generationSettings.getActiveSpawners(), generationSettings.getActiveMobSpawnCosts());
        this.biome = biome;
    }

    @Override
    public Map<MobCategory, WeightedRandomList<SpawnerData>> getActiveSpawners() {
        return super.getActiveSpawners();
    }

    @Override
    public Map<EntityType<?>, MobSpawnCost> getActiveMobSpawnCosts() {
        return super.getActiveMobSpawnCosts();
    }

    @Override
    public Set<MobCategory> getActiveTypesView() {
        return super.getActiveTypesView();
    }

    @Override
    public Set<EntityType<?>> getActiveCostView() {
        return super.getActiveCostView();
    }
}

