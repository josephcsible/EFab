package mcjty.efab.proxy;

import com.google.common.util.concurrent.ListenableFuture;
import mcjty.efab.EFab;
import mcjty.efab.blocks.ModBlocks;
import mcjty.efab.compat.botania.BotaniaSupportSetup;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.efab.items.ModItems;
import mcjty.efab.network.EFabMessages;
import mcjty.efab.recipes.RecipeManager;
import mcjty.efab.recipes.StandardRecipes;
import mcjty.lib.McJtyLib;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.concurrent.Callable;

public abstract class CommonProxy {

    public static File modConfigDir;
    private Configuration mainConfig;

    public void preInit(FMLPreInitializationEvent e) {
        McJtyLib.preInit(e);

        RecipeManager.init();
        GeneralConfig.preInit(e);

        modConfigDir = e.getModConfigurationDirectory();
        mainConfig = new Configuration(new File(modConfigDir.getPath(), "efab.cfg"));

        readMainConfig();

        SimpleNetworkWrapper network = PacketHandler.registerMessages(EFab.MODID, "efab");
        EFabMessages.registerNetworkMessages(network);

        ModItems.init();
        ModBlocks.init();
        if (EFab.botania) {
            BotaniaSupportSetup.preInit();
        }
    }

    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(GeneralConfiguration.CATEGORY_GENERAL, "General settings");

            GeneralConfiguration.init(cfg);
        } catch (Exception e1) {
            FMLLog.log(Level.ERROR, e1, "Problem loading config file!");
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(EFab.instance, new GuiProxy());
//        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
//        ModRecipes.init();
    }

    public void postInit(FMLPostInitializationEvent e) {
        mainConfig = null;
        StandardRecipes.init();
        if (EFab.botania) {
            BotaniaSupportSetup.postInit();
        }
        File file = new File(modConfigDir.getPath(), "efab_recipes.json");
        StandardRecipes.readRecipesConfig(file);
    }

    public World getClientWorld() {
        throw new IllegalStateException("This should only be called from client side");
    }

    public EntityPlayer getClientPlayer() {
        throw new IllegalStateException("This should only be called from client side");
    }

    public <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule) {
        throw new IllegalStateException("This should only be called from client side");
    }

    public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
        throw new IllegalStateException("This should only be called from client side");
    }

}
