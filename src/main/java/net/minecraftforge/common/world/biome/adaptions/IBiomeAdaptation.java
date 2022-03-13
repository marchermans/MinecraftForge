package net.minecraftforge.common.world.biome.adaptions;

import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

/**
 * Represents a modification which can be applied to a set of biomes.
 */
public interface IBiomeAdaptation {

    /**
     * The tag describing the list of biomes which are adapted by this adaptation.
     *
     * @return The tag which defines which biomes to apply this adaptation to.
     */
    HolderSet<Biome> targetBiomes();

    /**
     * Defines the type of adaptation that is applied.
     *
     * @return The adaptation type.
     */
    AdaptationType adaptationType();
}
