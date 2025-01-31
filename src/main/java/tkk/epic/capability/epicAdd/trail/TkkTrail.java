package tkk.epic.capability.epicAdd.trail;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tkk.epic.TkkEpic;
import tkk.epic.particle.TkkParticles;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.client.model.ItemSkin;

public class TkkTrail {
    public final Vec3 start;
    public final Vec3 end;
    public final String joint;
    public final float startTime;
    public final float endTime;
    public final float fadeTime;
    public final float r;
    public final float g;
    public final float b;
    public final int interpolateCount;
    public final int trailLifetime;
    public final String texturePath;
    public final boolean hand;

    public final boolean scalePos;

    public TkkTrail(Vec3 start, Vec3 end, String joint, float startTime, float endTime, float fadeTime, float r, float g, float b, int interpolateCount, int trailLifetime, String texturePath, boolean hand,boolean scalePos){

        this.start = start;
        this.end = end;
        this.joint = joint;
        this.startTime = startTime;
        this.endTime = endTime;
        this.fadeTime = fadeTime;
        this.r = r;
        this.g = g;
        this.b = b;
        this.interpolateCount = interpolateCount;
        this.trailLifetime = trailLifetime;
        this.texturePath = texturePath;
        this.hand = hand;
        this.scalePos=scalePos;
    }


    @OnlyIn(Dist.CLIENT)
    public TrailInfo buildTrailInfo(ItemSkin itemSkin){
        TrailInfo.Builder builder = new TrailInfo.Builder();
        if(this.scalePos && itemSkin!=null && itemSkin.trailInfo().start!=null && itemSkin.trailInfo().end!=null){
            builder.startPos(itemSkin.trailInfo().start.multiply(start));
            builder.endPos(itemSkin.trailInfo().end.multiply(end));
        }else {
            builder.startPos(start);
            builder.endPos(end);
        }
        builder.joint(joint);
        builder.time(startTime,endTime);
        builder.fadeTime(fadeTime);
        builder.r(r);
        builder.g(g);
        builder.b(b);
        builder.interpolations(interpolateCount);
        builder.lifetime(trailLifetime);
        if(!texturePath.equals("")){builder.texture(texturePath);};
        builder.itemSkinHand((hand)? InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND);
        builder.type(TkkParticles.SWING_TRAIL.get());
        return builder.create();
    }
}
