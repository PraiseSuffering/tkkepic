package tkk.epic.mixin;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import tkk.epic.event.HitStunAndKnockbackPostEvent;
import tkk.epic.event.HitStunAndKnockbackPreEvent;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.HurtableEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.projectile.ProjectilePatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageType;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.eventlistener.DealtDamageEvent;
import yesman.epicfight.world.entity.eventlistener.HurtEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.gamerule.EpicFightGamerules;

public class HandlerEntityEvents {
    public static void hurtEvent(LivingHurtEvent event) {
        EpicFightDamageSource epicFightDamageSource = null;
        Entity trueSource = event.getSource().getEntity();

        if (trueSource != null) {
            LivingEntityPatch<?> attackerEntityPatch = EpicFightCapabilities.getEntityPatch(trueSource, LivingEntityPatch.class);
            float baseDamage = event.getAmount();

            if (event.getSource() instanceof EpicFightDamageSource instance) {
                epicFightDamageSource = instance;
            } else if (event.getSource().isIndirect() && event.getSource().getDirectEntity() != null) {
                ProjectilePatch<?> projectileCap = EpicFightCapabilities.getEntityPatch(event.getSource().getDirectEntity(), ProjectilePatch.class);

                if (projectileCap != null) {
                    epicFightDamageSource = projectileCap.getEpicFightDamageSource(event.getSource());
                }
            } else if (attackerEntityPatch != null) {
                epicFightDamageSource = attackerEntityPatch.getEpicFightDamageSource();
                baseDamage = attackerEntityPatch.getModifiedBaseDamage(baseDamage);
            }

            if (epicFightDamageSource != null && !epicFightDamageSource.is(EpicFightDamageType.PARTIAL_DAMAGE)) {
                LivingEntity hitEntity = event.getEntity();

                if (attackerEntityPatch instanceof ServerPlayerPatch playerpatch) {
                    DealtDamageEvent.Hurt dealDamageHurt = new DealtDamageEvent.Hurt(playerpatch, hitEntity, epicFightDamageSource, event);
                    playerpatch.getEventListener().triggerEvents(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_HURT, dealDamageHurt);
                }

                float totalDamage = epicFightDamageSource.getDamageModifier().getTotalValue(baseDamage);

                if (trueSource instanceof LivingEntity livingEntity && epicFightDamageSource.getExtraDamages() != null) {
                    for (ExtraDamageInstance extraDamage : epicFightDamageSource.getExtraDamages()) {
                        totalDamage += extraDamage.get(livingEntity, epicFightDamageSource.getHurtItem(), hitEntity, baseDamage);
                    }
                }

                HurtableEntityPatch<?> hitHurtableEntityPatch = EpicFightCapabilities.getEntityPatch(hitEntity, HurtableEntityPatch.class);
                LivingEntityPatch<?> hitLivingEntityPatch = EpicFightCapabilities.getEntityPatch(hitEntity, LivingEntityPatch.class);
                ServerPlayerPatch hitPlayerPatch = EpicFightCapabilities.getEntityPatch(hitEntity, ServerPlayerPatch.class);

                if (hitPlayerPatch != null) {
                    HurtEvent.Post hurtEvent = new HurtEvent.Post(hitPlayerPatch, epicFightDamageSource, totalDamage);
                    hitPlayerPatch.getEventListener().triggerEvents(PlayerEventListener.EventType.HURT_EVENT_POST, hurtEvent);
                    totalDamage = hurtEvent.getAmount();
                }

                event.setAmount(totalDamage);

                if (epicFightDamageSource.is(EpicFightDamageType.EXECUTION)) {
                    float amount = event.getAmount();
                    event.setAmount(2147483647F);

                    if (hitLivingEntityPatch != null) {
                        int executionResistance = hitLivingEntityPatch.getExecutionResistance();

                        if (executionResistance > 0) {
                            hitLivingEntityPatch.setExecutionResistance(executionResistance - 1);
                            event.setAmount(amount);
                        }
                    }
                }

                if (event.getAmount() > 0.0F) {
                    if (hitHurtableEntityPatch != null) {
                        StunType stunType = epicFightDamageSource.getStunType();
                        float impact = epicFightDamageSource.getImpact();
                        float stunTime = 0.0F;
                        float knockBackAmount = 0.0F;
                        float stunShield = hitHurtableEntityPatch.getStunShield();
                        //tkk
                        HitStunAndKnockbackPreEvent event1=new HitStunAndKnockbackPreEvent(event.getEntity(),trueSource,epicFightDamageSource,stunType,impact);
                        MinecraftForge.EVENT_BUS.post(event1);
                        stunType=event1.stunType;
                        impact=event1.impact;
                        //tkk

                        if (stunShield > impact) {
                            if (stunType == StunType.SHORT || stunType == StunType.LONG) {
                                stunType = StunType.NONE;
                            }
                        }

                        hitHurtableEntityPatch.setStunShield(stunShield - impact);

                        switch (stunType) {
                            case SHORT:
                                // Solution by Cyber2049(github): Fix stun immunity
                                stunType = StunType.NONE;

                                if (!hitEntity.hasEffect(EpicFightMobEffects.STUN_IMMUNITY.get()) && (hitHurtableEntityPatch.getStunShield() == 0.0F)) {
                                    float totalStunTime = (0.25F + impact * 0.1F) * (1.0F - hitHurtableEntityPatch.getStunReduction());

                                    if (totalStunTime >= 0.075F) {
                                        stunTime = totalStunTime - 0.1F;
                                        boolean isLongStun = totalStunTime >= 0.83F;
                                        stunTime = isLongStun ? 0.83F : stunTime;
                                        stunType = isLongStun ? StunType.LONG : StunType.SHORT;
                                        knockBackAmount = Math.min(isLongStun ? impact * 0.05F : totalStunTime, 2.0F);
                                    }

                                    stunTime *= 1.0F - hitEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                                }
                                break;
                            case LONG:
                                stunType = hitEntity.hasEffect(EpicFightMobEffects.STUN_IMMUNITY.get()) ? StunType.NONE : StunType.LONG;
                                knockBackAmount = Math.min(impact * 0.05F, 5.0F);
                                stunTime = 0.83F;
                                break;
                            case HOLD:
                                stunType = StunType.SHORT;
                                stunTime = impact * 0.25F;
                                break;
                            case KNOCKDOWN:
                                stunType = hitEntity.hasEffect(EpicFightMobEffects.STUN_IMMUNITY.get()) ? StunType.NONE : StunType.KNOCKDOWN;
                                knockBackAmount = Math.min(impact * 0.05F, 5.0F);
                                stunTime = 2.0F;
                                break;
                            case NEUTRALIZE:
                                stunType = StunType.NEUTRALIZE;
                                hitHurtableEntityPatch.playSound(EpicFightSounds.NEUTRALIZE_MOBS.get(), 3.0F, 0.0F, 0.1F);
                                EpicFightParticles.AIR_BURST.get().spawnParticleWithArgument(((ServerLevel)hitEntity.level()), hitEntity, event.getSource().getDirectEntity());
                                knockBackAmount = 0.0F;
                                stunTime = 2.0F;
                            default:
                                break;
                        }

                        //tkk
                        HitStunAndKnockbackPostEvent event2=new HitStunAndKnockbackPostEvent(event.getEntity(),trueSource,epicFightDamageSource,stunType,stunTime,knockBackAmount);
                        MinecraftForge.EVENT_BUS.post(event2);
                        stunType=event2.stunType;
                        stunTime=event2.stunTime;
                        knockBackAmount=event2.knockBackAmount;
                        //tkk

                        Vec3 sourcePosition = epicFightDamageSource.getInitialPosition();
                        hitHurtableEntityPatch.setStunReductionOnHit(stunType);
                        boolean stunApplied = hitHurtableEntityPatch.applyStun(stunType, stunTime);

                        if (sourcePosition != null) {
                            if (!(hitEntity instanceof Player) && stunApplied) {
                                hitEntity.lookAt(EntityAnchorArgument.Anchor.FEET, sourcePosition);
                            }

                            if (knockBackAmount > 0.0F) {
                                knockBackAmount *= 40.0F / hitHurtableEntityPatch.getWeight();

                                hitHurtableEntityPatch.knockBackEntity(sourcePosition, knockBackAmount);
                            }
                        }
                    }
                }
            }
        } else {
            if (event.getSource().is(DamageTypes.FALL) && event.getAmount() > 1.0F && event.getEntity().level().getGameRules().getBoolean(EpicFightGamerules.HAS_FALL_ANIMATION)) {
                LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(event.getEntity(), LivingEntityPatch.class);

                if (entitypatch != null && !entitypatch.getEntityState().inaction()) {
                    StaticAnimation fallAnimation = entitypatch.getAnimator().getLivingAnimation(LivingMotions.LANDING_RECOVERY, entitypatch.getHitAnimation(StunType.FALL));

                    if (fallAnimation != null) {
                        entitypatch.playAnimationSynchronized(fallAnimation, 0);
                    }
                }
            }
        }
    }
}
