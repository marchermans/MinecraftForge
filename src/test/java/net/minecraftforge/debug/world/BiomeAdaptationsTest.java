package net.minecraftforge.debug.world;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.resource.PathResourcePack;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.function.Consumer;

@Mod(BiomeAdaptationsTest.MOD_ID)
public class BiomeAdaptationsTest
{
    static final String MOD_ID  = "biome_loading_event_test";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final boolean ENABLED = true;

    public BiomeAdaptationsTest()
    {
        LOGGER.info("BiomeAdaptationsTest loaded");

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerPacks);
    }

    public void registerPacks(final AddPackFindersEvent event) {
        if (!ENABLED) return;

        if (event.getPackType() != PackType.SERVER_DATA) return;

        registerSubPack(event::addRepositorySource, "gold_in_plains");
    }

    private void registerSubPack(final Consumer<RepositorySource> addPackCallback, final String name) {
        try
        {
            var resourcePath = ModList.get().getModFileById(MOD_ID).getFile().findResource("biome_adaptations/packs/" + name);
            var pack = new PathResourcePack(ModList.get().getModFileById(MOD_ID).getFile().getFileName() + ":" + resourcePath, resourcePath);
            var metadataSection = pack.getMetadataSection(PackMetadataSection.SERIALIZER);
            if (metadataSection != null)
            {
                addPackCallback.accept((packConsumer, packConstructor) ->
                                         packConsumer.accept(packConstructor.create(
                                           "builtin/" + name, new TextComponent(name), false,
                                           () -> pack, metadataSection, Pack.Position.BOTTOM, PackSource.BUILT_IN, false)));
            }
        }
        catch(IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
