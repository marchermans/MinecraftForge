package net.minecraftforge.common.world.biome.adaptions;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.GenerationStep;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum AdaptationType implements StringRepresentable {
    ADDITION,
    REMOVAL;

    public static final Codec<AdaptationType> CODEC = StringRepresentable.fromEnum(AdaptationType::values, AdaptationType::byName);
    private static final Map<String, AdaptationType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(AdaptationType::getSerializedName, (p_64205_) -> {
        return p_64205_;
    }));

    @Override
    public String getSerializedName() {
        return this.name().toUpperCase();
    }

    @Nullable
    public static AdaptationType byName(String p_64207_) {
        return BY_NAME.get(p_64207_);
    }
}
