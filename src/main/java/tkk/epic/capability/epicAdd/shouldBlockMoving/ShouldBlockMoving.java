package tkk.epic.capability.epicAdd.shouldBlockMoving;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tkk.epic.network.SPShouldBlockMoving;
import tkk.epic.network.TkkEpicNetworkManager;

public class ShouldBlockMoving {
    public final LivingEntity entity;
    public Boolean shouldBlockMoving=null;
    public ShouldBlockMoving(LivingEntity entity){
        this.entity=entity;
    }
    public void setShouldBlockMoving(Boolean blockMoving){
        this.shouldBlockMoving=blockMoving;
        if (entity instanceof ServerPlayer){
            SPShouldBlockMoving packet=new SPShouldBlockMoving(entity.getId(),this);
            TkkEpicNetworkManager.sendToPlayer(packet, (ServerPlayer) entity);
        }
    }
}
