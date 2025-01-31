package tkk.epic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tkk.epic.block.TkkEpicBlocks;
import tkk.epic.block.entity.SkillWorkbenchBlockEntity;
import tkk.epic.block.entity.TkkEpicBlockEntitys;
import tkk.epic.capability.CapabilityEventLoader;
import tkk.epic.command.AiModuleCommand;
import tkk.epic.command.ReloadCommand;
import tkk.epic.config.ConfigManager;
import tkk.epic.gameasset.AiModules;
import tkk.epic.gui.TkkMenuType;
import tkk.epic.gui.hud.hotbar.HotBarManager;
import tkk.epic.gui.screen.SkillWorkbenchScreen;
import tkk.epic.item.CreativeTab;
import tkk.epic.item.TkkEpicItems;
import tkk.epic.key.KeyEventLoader;
import tkk.epic.key.KeybindingsManager;
import tkk.epic.modCompat.CompatManager;
import tkk.epic.network.TkkEpicNetworkManager;
import tkk.epic.particle.TkkParticles;
import tkk.epic.particle.client.loader.VanillaTrailParticleDataLoader;
import tkk.epic.skill.EquipSkillHandle;
import tkk.epic.skill.SkillItemType;
import tkk.epic.skill.SkillManager;

import java.io.File;

@Mod(TkkEpic.MODID)
public class TkkEpic {
    //epicfight 20.9.5 2024/11/2
    public static final String MODID = "tkkepic";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static File MOD_DIR= new File(FMLPaths.GAMEDIR.get().toFile().toString()+"//TkkEpicNpc");
    public static MinecraftServer Server;
    private static TkkEpic instance;

    public static TkkEpic getInstance() {
        return instance;
    }

    public TkkEpic(){
        instance=this;
        ConfigManager.regConfig();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::doCommandStuff);
        bus.addListener(this::doClientStuff);
        CapabilityEventLoader.reg(bus);
        MinecraftForge.EVENT_BUS.register(AiModules.class);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(TkkEpic.class);
        TkkEpicBlocks.BLOCKS.register(bus);
        TkkEpicItems.ITEMS.register(bus);
        TkkEpicBlockEntitys.BLOCK_ENTITIES.register(bus);
        TkkMenuType.MENUS.register(bus);
        CreativeTab.TABS.register(bus);
        bus.addListener(tkk.epic.gameasset.Animations::registerAnimations);
        bus.addListener(tkk.epic.gameasset.AiModules::register);

        JSPluginManager.INSTANCE.loaderJSPlugin();
        SkillItemType.reg();
        MinecraftForge.EVENT_BUS.register(SkillWorkbenchBlockEntity.class);
        HotBarManager.managerRegister(bus);
        MinecraftForge.EVENT_BUS.register(SkillManager.class);
        MinecraftForge.EVENT_BUS.addListener(this::command);
        EquipSkillHandle.regEvent();
        TkkParticles.reg(bus);
        CompatManager.initCompats();
        //new TkkGameLib();
        //new Enr();
    }
    public void doCommandStuff(FMLCommonSetupEvent event){
        event.enqueueWork(TkkEpicNetworkManager::registerPackets);
    }
    public void doClientStuff(final FMLClientSetupEvent event){
        MinecraftForge.EVENT_BUS.register(KeyEventLoader.class);
        KeybindingsManager.keybindingsRegister();
    }
    @SubscribeEvent
    public void setAboutToStart(ServerAboutToStartEvent event) {
        Server = event.getServer();
    }
    @SubscribeEvent
    public void onCommandRegistry(final RegisterCommandsEvent event) {
        //VerifyAnimationsCommand.register(event.getDispatcher());
    }
    private void command(final RegisterCommandsEvent event) {
        ReloadCommand.register(event.getDispatcher());
        AiModuleCommand.register(event.getDispatcher());
    }
    public void broadcast(String message){
        TkkEpic.LOGGER.log(Level.ERROR,message);
        if(Server!=null){
            PlayerList playerList=Server.getPlayerList();
            playerList.broadcastSystemMessage(Component.literal(message),false);
        }
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            // 在客户端执行的代码
            Minecraft.getInstance().gui.getChat().addMessage(Component.literal("[Client] "+message));
        });
    }
    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(TkkMenuType.SKILL_WORKBENCH_MENU.get(), SkillWorkbenchScreen::new);
        }
        @SubscribeEvent
        public static void registerResourcepackReloadListnerEvent(final RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(VanillaTrailParticleDataLoader.INSTANCE);
        }
    }
}
