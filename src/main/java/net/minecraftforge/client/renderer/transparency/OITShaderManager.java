package net.minecraftforge.client.renderer.transparency;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "forge", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class OITShaderManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(OITShaderManager.class);
    private static final OITShaderManager INSTANCE = new OITShaderManager();

    public static OITShaderManager getInstance()
    {
        return INSTANCE;
    }

    private final Map<ShaderRegistration, OITShader> shaderMap = Maps.newHashMap();

    private OITShaderManager()
    {
    }

    void reset() {
        shaderMap.values().forEach(OITShader::close);
        shaderMap.clear();
    }

    void register(final ResourceProvider resourceProvider, final ShaderInstance shaderInstance, final VertexFormat.Mode mode) {

    }

    @Nullable
    public OITShader getShader(final ShaderInstance shader, final VertexFormat.Mode mode)
    {
        final ShaderRegistration registration = new ShaderRegistration(shader, mode);
        return shaderMap.computeIfAbsent(registration, reg -> {
            try {
                return new OITShader(Minecraft.getInstance().getResourceManager(), reg.shaderInstance(), reg.mode());
            } catch (final IOException e) {
                LOGGER.error("Failed to load shader {}", reg.shaderInstance.getName(), e);
                return null;
            }
        });
    }

    public boolean hasShader(final ShaderInstance shader, final VertexFormat.Mode mode)
    {
        return getShader(shader, mode) != null;
    }

    private record ShaderRegistration(ShaderInstance shaderInstance, VertexFormat.Mode mode) { }
}
