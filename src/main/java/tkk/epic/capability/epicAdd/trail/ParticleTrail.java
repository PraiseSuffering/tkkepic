package tkk.epic.capability.epicAdd.trail;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleTrail {
    public final Vec3 start;
    public final Vec3 end;
    public final String joint;
    public final float startTime;
    public final float endTime;
    public final float fadeTime;
    public final int interpolateCount;
    public final int trailLifetime;
    public final boolean hand;
    public final boolean scalePos;

    public final ResourceLocation particle;
    public final String args;
    public final float spaceBetween;
    public final float speed;
    public final double dist;
    public final int count;
    public ParticleOptions iParticleData;

    public ParticleTrail(Vec3 start,Vec3 end,String joint, float startTime, float endTime, float fadeTime, int interpolateCount, int trailLifetime,boolean hand, boolean scalePos, ResourceLocation particle, String args, float spaceBetween, float speed, double dist, int count) {
        this.start=start;
        this.end=end;
        this.joint = joint;
        this.startTime = startTime;
        this.endTime = endTime;
        this.fadeTime = fadeTime;
        this.interpolateCount = interpolateCount;
        this.trailLifetime = trailLifetime;
        this.hand = hand;
        this.scalePos=scalePos;

        this.particle = particle;
        this.args = args;
        this.spaceBetween = spaceBetween;
        this.speed = speed;
        this.dist = dist;
        this.count = count;
    }

    public final ParticleOptions getIParticleData() throws Exception {
        if(iParticleData!=null){return iParticleData;}
        ParticleType a= ForgeRegistries.PARTICLE_TYPES.getValue(particle);
        if (a==null){
            throw new Exception("§c tkk.epic.capability.epicAdd.trail.ParticleTrail.getIParticleData Error:§f unknown particle "+particle);
        }
        try {
            ParticleOptions ipd=a.getDeserializer().fromCommand(a,new StringReader(args));
            iParticleData=ipd;
            return ipd;
        } catch (CommandSyntaxException e) {
            throw new Exception("§c tkk.epic.capability.epicAdd.trail.ParticleTrail.getIParticleData Error:§f "+particle+" arg:"+args+" exception:"+e);
        }
    }
}
