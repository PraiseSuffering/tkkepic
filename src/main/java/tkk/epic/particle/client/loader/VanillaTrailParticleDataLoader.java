package tkk.epic.particle.client.loader;

import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import tkk.epic.TkkEpic;
import tkk.epic.particle.client.VanillaTrailParticle;
import yesman.epicfight.api.client.model.ItemSkin;
import yesman.epicfight.main.EpicFightMod;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class VanillaTrailParticleDataLoader extends SimpleJsonResourceReloadListener {
    public static final VanillaTrailParticleDataLoader INSTANCE = new VanillaTrailParticleDataLoader();
    public static final Map<ResourceLocation, VanillaParticleTrailData> PARTICLE_DATA = Maps.newHashMap();

    public VanillaTrailParticleDataLoader() {
        super((new GsonBuilder()).create(), "tkk_vanilla_trail_particle_data");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManager, ProfilerFiller profileFiller) {
        for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
            ResourceLocation rl = entry.getKey();
            String pathString = rl.getPath();
            ResourceLocation registryName = new ResourceLocation(rl.getNamespace(), pathString);

            VanillaParticleTrailData vanillaParticleTrailData=VanillaParticleTrailData.deserialize(entry.getValue());
            if(vanillaParticleTrailData!=null){
                PARTICLE_DATA.put(registryName,vanillaParticleTrailData);
            }
        }
    }
    public static class VanillaParticleTrailData{
        public final ResourceLocation particleId;
        public final String particleArgs;
        public final float spaceBetween;
        public final float speed;
        public final double dist;
        public final int count;


        public VanillaParticleTrailData(ResourceLocation particleId, String particleArgs, float spaceBetween, float speed, double dist, int count) {
            this.particleId = particleId;
            this.particleArgs = particleArgs;
            this.spaceBetween = spaceBetween;
            this.speed = speed;
            this.dist = dist;
            this.count = count;
        }
        public static VanillaParticleTrailData deserialize(JsonElement element){
            JsonObject jsonObj = element.getAsJsonObject();

            ResourceLocation particleId;
            String particleArgs;
            float spaceBetween;
            float speed;
            double dist;
            int count;



            if (jsonObj.has("particleId")) {
                particleId=new ResourceLocation(GsonHelper.getAsString(jsonObj, "particleId"));
            }else {
                TkkEpic.LOGGER.warn("[tkk_vanilla_trail_particle_data] not have particleId ");
                return null;
            }
            if (jsonObj.has("particleArgs")) {
                particleArgs=GsonHelper.getAsString(jsonObj, "particleArgs");
            }else {
                TkkEpic.LOGGER.warn("[tkk_vanilla_trail_particle_data] not have particleArgs ");
                return null;
            }
            if (jsonObj.has("spaceBetween")) {
                spaceBetween=GsonHelper.getAsFloat(jsonObj, "spaceBetween");
            }else {
                TkkEpic.LOGGER.warn("[tkk_vanilla_trail_particle_data] not have spaceBetween ");
                return null;
            }
            if (jsonObj.has("speed")) {
                speed=GsonHelper.getAsFloat(jsonObj, "speed");
            }else {
                TkkEpic.LOGGER.warn("[tkk_vanilla_trail_particle_data] not have speed ");
                return null;
            }
            if (jsonObj.has("dist")) {
                dist=GsonHelper.getAsDouble(jsonObj, "dist");
            }else {
                TkkEpic.LOGGER.warn("[tkk_vanilla_trail_particle_data] not have dist ");
                return null;
            }
            if (jsonObj.has("count")) {
                count=GsonHelper.getAsInt(jsonObj, "count");
            }else {
                TkkEpic.LOGGER.warn("[tkk_vanilla_trail_particle_data] not have count ");
                return null;
            }
            return new VanillaParticleTrailData(particleId,particleArgs,spaceBetween,speed,dist,count);
        }
    }
}
