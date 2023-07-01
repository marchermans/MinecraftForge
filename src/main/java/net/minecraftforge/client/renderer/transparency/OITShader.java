package net.minecraftforge.client.renderer.transparency;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;

public class OITShader extends ShaderInstance
{
    private Program geometryProgram = null;

    public OITShader(ResourceProvider resourceProvider, ResourceLocation name, VertexFormat vertexFormat, VertexFormat.Mode mode) throws IOException
    {
        super(resourceProvider, name, vertexFormat, new Context(mode));
    }

    public OITShader(ResourceProvider resourceProvider, ShaderInstance shader, final VertexFormat.Mode mode) throws IOException
    {
        this(resourceProvider, new ResourceLocation(shader.getName()), shader.getVertexFormat(), mode);
    }

    @Override
    protected void afterProgramCreation(@NotNull ResourceProvider resourceProvider, @NotNull ResourceLocation shaderLocation, IShaderContext context, @NotNull VertexFormat vertexFormat, @NotNull JsonObject shaderDeclaration, @NotNull Program vertexProgram, @NotNull Program fragmentProgram) throws IOException
    {
        super.afterProgramCreation(resourceProvider, shaderLocation, context, vertexFormat, shaderDeclaration, vertexProgram, fragmentProgram);
        if (!(context instanceof Context oitContext))
            throw new IllegalStateException("Context is not of type OITShader.Context");

        final String geometryShaderSource = OITShaders.GEOMETRY_SHADER.replace("%FORMAT%", oitContext.mode().name().toLowerCase(Locale.ROOT));

        this.geometryProgram = getOrCreate(resourceProvider, Program.Type.GEOMETRY, geometryShaderSource, this);
    }

    @Override
    public void attachToProgram()
    {
        super.attachToProgram();
        if (this.geometryProgram == null)
            throw new IllegalStateException("Geometry program not initialized. After program creation not called?");

        this.geometryProgram.attachToShader(this);
    }

    private record Context(VertexFormat.Mode mode) implements IShaderContext {}
}
