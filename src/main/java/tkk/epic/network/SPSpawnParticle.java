package tkk.epic.network;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.function.Supplier;

public class SPSpawnParticle {

    public ArrayList<Particle> particles=new ArrayList<>();
    public SPSpawnParticle() {

    }
    public SPSpawnParticle(FriendlyByteBuf buf) {
        int size=buf.readInt();
        for(int i=0;i<size;i++){
            particles.add(fromBytesParticle(buf));
        }
    }
    public void addParticle(String id, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
        this.particles.add(new Particle(id,x,y,z,xSpeed,ySpeed,zSpeed));
    }
    public static SPSpawnParticle fromBytes(FriendlyByteBuf buf) {
        SPSpawnParticle msg = new SPSpawnParticle(buf);
        return msg;
    }

    public static void toBytes(SPSpawnParticle msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.particles.size());
        for(Particle p:msg.particles){
            toBytesParticle(p,buf);
        }
    }

    public static void handle(SPSpawnParticle msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Level world = mc.player.level();
            for(Particle p:msg.particles){
                ParticleOptions ipd=p.getIParticleData();
                //TkkEpic.LOGGER.log(Level.ERROR,"tkk spawnHandle "+ipd);
                if(ipd==null){continue;}
                world.addParticle(ipd,p.x,p.y,p.z,p.xSpeed,p.ySpeed,p.zSpeed);

            }

        });

        ctx.get().setPacketHandled(true);
    }
    public static Particle fromBytesParticle(FriendlyByteBuf buf) {
        Particle msg = new Particle(buf.readUtf(),buf.readDouble(),buf.readDouble(),buf.readDouble(),buf.readDouble(),buf.readDouble(),buf.readDouble());
        return msg;
    }

    public static void toBytesParticle(Particle msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.id);
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeDouble(msg.xSpeed);
        buf.writeDouble(msg.ySpeed);
        buf.writeDouble(msg.zSpeed);
    }

    private static class Particle{
        public final String id;
        public final double x;
        public final double y;
        public final double z;
        public final double xSpeed;
        public final double ySpeed;
        public final double zSpeed;

        private Particle(String id, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
            this.xSpeed = xSpeed;
            this.ySpeed = ySpeed;
            this.zSpeed = zSpeed;
        }
        public final ParticleOptions getIParticleData() {
            ParticleType a= ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(id));
            if (a==null){
                return null;
            }
            try {
                ParticleOptions ipd=a.getDeserializer().fromCommand(a,new StringReader(""));
                return ipd;
            } catch (CommandSyntaxException e) {
                return null;
            }
        }

    }
}
