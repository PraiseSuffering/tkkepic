package tkk.epic.gui.hud.hotbar;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import tkk.epic.TkkEpic;

import java.util.HashMap;

import static tkk.epic.gui.hud.hotbar.HotBar.SKILL_SIZE;

public class HotBarManager {
    public static final ResourceLocation ERROR_ICON=new ResourceLocation("tkkepic","textures/gui/error_icon.png");
    public static HashMap<String, ResourceLocation> SPELL_BIND_TEXTURES=new HashMap<>();
    public static Spell[] spells=new Spell[SKILL_SIZE];
    public static HotBar hotBar;
    public static int hotBarPos=0;
    public static class Spell{
        /*
        无(无额外渲染)
        激活(黄色边框)
        cd(黑色mc冷却覆盖)
        禁用(红色边框+黑覆盖)
        cd完毕(白色覆盖)
        * */
        //为false时禁止渲染且关闭发包
        public boolean doRender=false;

        //是否禁用，如果为true则渲染禁用,且关闭发包功能
        public boolean isDisable=false;
        //是否激活，如果为true则渲染激活
        public boolean isEnable=false;
        //冷却时间，为0时不渲染，1时渲染cd完毕(白色覆盖),>1时渲染黑色覆盖cd且,>1时关闭发包功能
        public int maxCooldow=0;
        public int cooldown=0;
        //魔力，不会自行恢复的cd，也没有cd完毕的特效，>1时关闭发包功能
        public int maxMana=0;
        public int mana=0;
        //渲染额外的字
        public String text="";
        //技能图标,如果未注册或null则错误图标
        public String spell_textures="";

        public float getPercent(int now,int need){
            return (float) now/(float) need;
        }
        public ResourceLocation getIcon(){
            if(!SPELL_BIND_TEXTURES.containsKey(this.spell_textures)){
                ResourceLocation rl=new ResourceLocation(this.spell_textures);
                SPELL_BIND_TEXTURES.put(this.spell_textures, rl);
            }

            return SPELL_BIND_TEXTURES.getOrDefault(this.spell_textures, ERROR_ICON);
        }

    }



    public static void managerRegister(IEventBus bus){
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(HotBarManager::registerOverlay);
            //TkkEpic.getInstance().broadcast("tkk managerRegister");
            for(int i=0;i<SKILL_SIZE;i++){
                Spell temp=new Spell();
                temp.doRender=false;
                spells[i]=temp;
            }
            hotBar=new HotBar();
            MinecraftForge.EVENT_BUS.register(HotBarManager.class);
        });
    }

    /*
    @SubscribeEvent
    public static void renderGameOverlayEvent(RenderGuiOverlayEvent.Post event){
        if (event.isCancelable()){
            return;
        }
        hotBar.onHudRender(event.getMatrixStack(), event.getPartialTick());

    }

     */
    @SubscribeEvent
    public static void registerOverlay(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "tkk.skill.hotbar", new IGuiOverlay() { // from class: com.robertx22.age_of_exile.mmorpg.event_registers.GuiOverlays.1
            public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
                hotBar.onHudRender(guiGraphics,partialTick);
            }
        });
    }
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e){
        if(e.phase==TickEvent.Phase.END){return;}
        for(Spell spell:spells){
            if(spell.cooldown>0){spell.cooldown-=1;}
        }
    }


}
