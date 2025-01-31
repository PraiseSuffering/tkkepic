package tkk.epic.capability.epicAdd.trail;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class TkkCustomTrail {
    public ArrayList<TkkTrail> readyTrails;
    public ArrayList<ParticleTrail> readyParticleTrails;
    public boolean readyDoVanillaTrail;

    public final ArrayList<TkkTrail> trails;
    public ArrayList<ParticleTrail> particleTrails;
    public boolean doVanillaTrail;

    public TkkCustomTrail(){
        trails=new ArrayList<>();
        particleTrails=new ArrayList<>();
        doVanillaTrail=true;
    }

    public void addTrail(Vec3 start, Vec3 end, String joint, float startTime, float endTime, float fadeTime, float r, float g, float b, int interpolateCount, int trailLifetime, String texturePath, boolean hand,boolean overwriteItemSkin) {
        trails.add(new TkkTrail(start,end,joint,startTime,endTime,fadeTime,r/255,g/255,b/255,interpolateCount,trailLifetime,texturePath,hand,overwriteItemSkin));
    }
    public void addParticleTrail(Vec3 start, Vec3 end, String joint, float startTime, float endTime, float fadeTime, int interpolateCount, int trailLifetime, boolean hand, boolean scalePos, ResourceLocation particle, String args, float spaceBetween, float speed, double dist, int count){
        particleTrails.add(new ParticleTrail(start,end,joint,startTime,endTime,fadeTime,interpolateCount,trailLifetime,hand,scalePos,particle,args,spaceBetween,speed,dist,count));
    }
    public void setDoVanillaTrail(boolean doVanilla){
        doVanillaTrail=doVanilla;
    }
    public void clear(){
        trails.clear();
        particleTrails.clear();
        doVanillaTrail=true;
    }
    public void readyAnimation(){
        readyTrails= (ArrayList<TkkTrail>) trails.clone();
        readyParticleTrails= (ArrayList<ParticleTrail>) particleTrails.clone();
        readyDoVanillaTrail=doVanillaTrail;
        clear();
    }

}
