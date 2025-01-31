package tkk.epic.gui.hud.hotbar;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import tkk.epic.TkkEpic;
import tkk.epic.key.KeybindingsManager;

import java.util.Locale;

public class HotBar{
    public static final int SKILL_SIZE=16;
    private static final ResourceLocation COOLDOWN_TEX = new ResourceLocation("tkkepic", "textures/gui/cooldown.png");
    private static final ResourceLocation FRAME = new ResourceLocation("tkkepic", "textures/gui/default_frame.png");
    private static final ResourceLocation FRAME_ENABLE = new ResourceLocation("tkkepic", "textures/gui/enable_frame.png");
    private static final ResourceLocation FRAME_DISABLE = new ResourceLocation("tkkepic", "textures/gui/disable_frame.png");
    private static final ResourceLocation FRAME_READY = new ResourceLocation("tkkepic", "textures/gui/ready_frame.png");

    Minecraft mc = Minecraft.getInstance();

    public void onHudRender(GuiGraphics matrix, float v) {
        try {
            if (this.mc.options.renderDebug || this.mc.player.isSpectator())
                return;
            if (this.mc.player.isSpectator())
                return;
            int x = 0;
            int y = 0;
            if(HotBarManager.hotBarPos==0){
                y=this.mc.getWindow().getGuiScaledHeight()-20;
                x=0;

            }else if(HotBarManager.hotBarPos==1){
                x = 0;
                y = this.mc.getWindow().getGuiScaledHeight() / 2;
                for (int i=0;i<SKILL_SIZE;i++){
                    if(HotBarManager.spells[i].doRender){
                        y-=10;
                    }
                }

            }
            //renderHotbar(matrix, x, y);
            int temp=0;

            for (int i = 0; i < 16; i++) {
                RenderSystem.enableBlend();
                HotBarManager.Spell spell=HotBarManager.spells[i];
                if(spell!=null && spell.doRender){
                    int nowX=x;
                    int nowY=y;
                    if(HotBarManager.hotBarPos==0){
                        nowX+=temp*20;
                    }else if(HotBarManager.hotBarPos==1){
                        nowY+=temp*20;
                    }
                    String key;
                    if(i<8) {
                        key = cloc_translate(KeybindingsManager.getSpellKeybindingFromInt(i).keyBinding.getTranslatedKeyMessage()).toUpperCase(Locale.ROOT);
                    }else{
                        key="";
                    }
                    renderSpell(matrix,spell,nowX,nowY,key);
                    temp+=1;
                }

                //renderCurrentSpell(i, matrix);
                RenderSystem.disableBlend();
            }
            RenderSystem.disableBlend();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void renderSpell(GuiGraphics matrixStack,HotBarManager.Spell spell,int x,int y,String key){
        matrixStack.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        //RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (spell == null){
            return;
        }
        if(!spell.doRender){
            //return;
        }
        int xs = x;
        int ys = y;
        //渲染边框
        //this.mc.getTextureManager().bindForSetup(FRAME);
        //blit(matrixStack, xs, ys, 0.0F, 0.0F, 20, 20, 20, 20);
        matrixStack.blit(FRAME,xs,ys,0.0f,0.0f,20,20,20,20);
        //渲染激活
        if(spell.isEnable){
            //this.mc.getTextureManager().bindForSetup(FRAME_ENABLE);
            //blit(matrixStack, xs, ys, 0.0F, 0.0F, 20, 20, 20, 20);
            matrixStack.blit(FRAME_ENABLE, xs, ys, 0.0F, 0.0F, 20, 20, 20, 20);
        }

        //渲染图标
        //this.mc.getTextureManager().bindForSetup(spell.getIcon());
        //blit(matrixStack, xs+2, ys+2, 0.0F, 0.0F, 16, 16, 16, 16);
        matrixStack.blit(spell.getIcon(),  xs+2, ys+2, 0.0F, 0.0F, 16, 16, 16, 16);
        //渲染充能
        if(spell.mana>0){
            drawCooldown(spell.getPercent(spell.mana,spell.maxMana), matrixStack, xs, ys);
        }
        //渲染冷却
        if(spell.cooldown>1){
            drawCooldown(spell.getPercent(spell.cooldown,spell.maxCooldow), matrixStack, xs, ys);
            float cooldown= ((float)spell.cooldown)/20;
            renderScaledTextB(matrixStack, xs + 2, ys + 2, 0.6F, cooldown+"s", spell.mana>0?ChatFormatting.YELLOW:ChatFormatting.GREEN);
        }
        //渲染可用状态提示
        if(spell.cooldown==1){
            if(spell.mana==0){
                //this.mc.getTextureManager().bindForSetup(FRAME_READY);
                //blit(matrixStack, xs, ys, 0.0F, 0.0F, 20, 20, 20, 20);
                matrixStack.blit(FRAME_READY,  xs, ys, 0.0F, 0.0F, 20, 20, 20, 20);
            }
        }
        //渲染按键
        if (key.length() > 3){
            key = key.substring(0, 2);
        }
        renderScaledText(matrixStack, xs + 16, ys + 16, 1.0F, key, spell.mana>0?ChatFormatting.YELLOW:ChatFormatting.GREEN);
        //渲染额外信息
        if(spell.text!=null){renderScaledTextC(matrixStack, xs + 14, ys + 0, 0.7F, spell.text, ChatFormatting.GREEN);}
        //渲染禁用
        if(spell.isDisable){
            //this.mc.getTextureManager().bindForSetup(FRAME_DISABLE);
            //blit(matrixStack, xs, ys, 0.0F, 0.0F, 20, 20, 20, 20);
            matrixStack.blit(FRAME_DISABLE,  xs, ys, 0.0F, 0.0F, 20, 20, 20, 20);
        }

    }
    private void drawCooldown(float percent, GuiGraphics matrix, int x, int y) {
        //this.mc.getTextureManager().bindForSetup(COOLDOWN_TEX);
        //blit(matrix, x+2, y+2, 0.0F, 0.0F, 16, (int)(16.0F * percent), 16, 16);
        matrix.blit(COOLDOWN_TEX,  x+2, y+2, 0.0F, 0.0F, 16, (int)(16.0F * percent), 16, 16);
    }

    //com.robertx22.library_of_exile.utils.CLOC
    public static String translate(FormattedText s) {
        return I18n.get(s.getString()
                .replaceAll("%", "PERCENT"), new Object[0])
                .replaceAll("PERCENT", "%");
    }
    //com.robertx22.library_of_exile.utils.GuiUtils
    public static void renderScaledText(GuiGraphics matrix, int x, int y, float scale, String text, ChatFormatting format) {
        float antiScale = 1.0F / scale;
        //RenderSystem.scaled(scale, scale, scale);
        matrix.pose().scale(scale, scale, scale);
        double textWidthMinus = ((Minecraft.getInstance()).font.width(text) / 2.0F) * scale;
        (Minecraft.getInstance()).font.getClass();
        double textHeightMinus = 9.0D * scale / 2.0D;
        float xp = (float)(x - textWidthMinus);
        float yp = (float)(y - textHeightMinus);
        float xf = (float)(xp * antiScale);
        float yf = (float)(yp * antiScale);
        //(Minecraft.getInstance()).font.drawShadow(matrix, text, xf, yf, format.getColor().intValue());
        matrix.drawString((Minecraft.getInstance()).font, net.minecraft.network.chat.Component.literal(text).withStyle(format), (int)xf, (int)yf, format.getColor().intValue());

        matrix.pose().scale(antiScale, antiScale, antiScale);
    }
    //从左上算坐标
    public static void renderScaledTextB(GuiGraphics matrix, int x, int y, float scale, String text, ChatFormatting format) {
        float antiScale = 1.0F / scale;
        //RenderSystem.scaled(scale, scale, scale);
        matrix.pose().scale(scale, scale, scale);
        double textWidthMinus = ((Minecraft.getInstance()).font.width(text) / 2.0F) * scale;
        (Minecraft.getInstance()).font.getClass();
        double textHeightMinus = 9.0F * scale / 2.0D;
        //float xp = (float)(x - textWidthMinus);
        //float yp = (float)(y - textHeightMinus);
        float xf = (float)(x * antiScale);
        float yf = (float)(y * antiScale);
        //(Minecraft.getInstance()).font.drawShadow(matrix, text, xf, yf, format.getColor().intValue());
        matrix.drawString((Minecraft.getInstance()).font, net.minecraft.network.chat.Component.literal(text).withStyle(format), (int)xf, (int)yf, format.getColor().intValue());
        //RenderSystem.scaled(antiScale, antiScale, antiScale);
        matrix.pose().scale(antiScale, antiScale, antiScale);
    }
    //右往左
    public static void renderScaledTextC(GuiGraphics matrix, int x, int y, float scale, String text, ChatFormatting format) {
        float antiScale = 1.0F / scale;
        //RenderSystem.scaled(scale, scale, scale);
        matrix.pose().scale(scale, scale, scale);
        double textWidthMinus = ((Minecraft.getInstance()).font.width(text) / 2.0F) * scale;
        (Minecraft.getInstance()).font.getClass();
        double textHeightMinus = 9.0D * scale / 2.0D;
        float xp = (float)(x - textWidthMinus);
        //float yp = (float)(y - textHeightMinus);
        float xf = (float)(xp * antiScale);
        float yf = (float)(y * antiScale);
        //(Minecraft.getInstance()).font.drawShadow(matrix, text, xf, yf, format.getColor().intValue());
        //RenderSystem.scaled(antiScale, antiScale, antiScale);
        matrix.drawString((Minecraft.getInstance()).font, net.minecraft.network.chat.Component.literal(text).withStyle(format), (int)xf, (int)yf, format.getColor().intValue());
        matrix.pose().scale(antiScale, antiScale, antiScale);
    }
    //com.robertx22.library_of_exile.utils.CLOC
    public static String cloc_translate(FormattedText s) {
        return I18n.get(s.getString()
                .replaceAll("%", "PERCENT"), new Object[0])
                .replaceAll("PERCENT", "%");
    }
}
