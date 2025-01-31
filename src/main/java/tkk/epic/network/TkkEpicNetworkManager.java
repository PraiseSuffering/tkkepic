package tkk.epic.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import tkk.epic.TkkEpic;

public class TkkEpicNetworkManager {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(TkkEpic.MODID, "network_manager"), () -> "1", "1"::equals, "1"::equals);

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToClient(MSG message, PacketDistributor.PacketTarget packetTarget) {
        INSTANCE.send(packetTarget, message);
    }
    public static <MSG> void sendNearby(Level level, Vec3 pos, int range, MSG msg) {
        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.x(), pos.y(), pos.z(), range, level.dimension())), msg);
    }
    public static <MSG> void sendToAll(MSG message) {
        sendToClient(message, PacketDistributor.ALL.noArg());
    }

    public static <MSG> void sendToAllPlayerTrackingThisEntity(MSG message, Entity entity) {
        sendToClient(message, PacketDistributor.TRACKING_ENTITY.with(() -> entity));
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        sendToClient(message, PacketDistributor.PLAYER.with(() -> player));
    }

    public static <MSG> void sendToAllPlayerTrackingThisEntityWithSelf(MSG message, ServerPlayer entity) {
        sendToPlayer(message, entity);
        sendToClient(message, PacketDistributor.TRACKING_ENTITY.with(() -> entity));
    }

    public static void registerPackets() {
        int id = 0;
        INSTANCE.registerMessage(id++, CPTkkSpellPress.class, CPTkkSpellPress::toBytes, CPTkkSpellPress::fromBytes, CPTkkSpellPress::handle);
        INSTANCE.registerMessage(id++, CPTkkSpellUp.class, CPTkkSpellUp::toBytes, CPTkkSpellUp::fromBytes, CPTkkSpellUp::handle);
        INSTANCE.registerMessage(id++, SPTkkSpellUpdata.class, SPTkkSpellUpdata::toBytes, SPTkkSpellUpdata::fromBytes, SPTkkSpellUpdata::handle);
        INSTANCE.registerMessage(id++, SPEpicAddTrailUpdata.class, SPEpicAddTrailUpdata::toBytes, SPEpicAddTrailUpdata::fromBytes, SPEpicAddTrailUpdata::handle);
        INSTANCE.registerMessage(id++, SPEpicAddAttackSpeedUpdata.class, SPEpicAddAttackSpeedUpdata::toBytes, SPEpicAddAttackSpeedUpdata::fromBytes, SPEpicAddAttackSpeedUpdata::handle);
        INSTANCE.registerMessage(id++, SPEpicAddCoordUpdata.class, SPEpicAddCoordUpdata::toBytes, SPEpicAddCoordUpdata::fromBytes, SPEpicAddCoordUpdata::handle);
        INSTANCE.registerMessage(id++, SPEpicCustomStatesSpectrumUpdata.class, SPEpicCustomStatesSpectrumUpdata::toBytes, SPEpicCustomStatesSpectrumUpdata::fromBytes, SPEpicCustomStatesSpectrumUpdata::handle);
        INSTANCE.registerMessage(id++, SPCustomMotionAnimationSync.class, SPCustomMotionAnimationSync::toBytes, SPCustomMotionAnimationSync::fromBytes, SPCustomMotionAnimationSync::handle);
        INSTANCE.registerMessage(id++, SPShouldBlockMoving.class, SPShouldBlockMoving::toBytes, SPShouldBlockMoving::fromBytes, SPShouldBlockMoving::handle);
        INSTANCE.registerMessage(id++, SPSpawnParticle.class, SPSpawnParticle::toBytes, SPSpawnParticle::fromBytes, SPSpawnParticle::handle);

    }
}
