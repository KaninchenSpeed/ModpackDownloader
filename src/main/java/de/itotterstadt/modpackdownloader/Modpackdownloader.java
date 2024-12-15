package de.itotterstadt.modpackdownloader;

import com.mojang.logging.LogUtils;
import de.itotterstadt.modpackdownloader.config.Config;
import de.itotterstadt.modpackdownloader.config.ConfigReader;
import de.itotterstadt.modpackdownloader.updater.Updater;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.IOException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Modpackdownloader.MOD_ID)
public class Modpackdownloader {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "modpackdownloader";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public Modpackdownloader() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) throws IOException {
            Config config = new ConfigReader().readConfig();
            Updater updater = new Updater(config.metaUrl, config);

            LOGGER.info("checking for updates");
            try {
                updater.checkForUpdates();
                LOGGER.info("checked for updates");
            } catch (IOException err) {
                err.printStackTrace();
                LOGGER.error("could not check for updates");
            }
        }
    }
}
