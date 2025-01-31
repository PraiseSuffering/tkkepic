package tkk.epic.particle.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import tkk.epic.TkkEpic;
import tkk.epic.capability.epicAdd.EpicAddCapability;
import tkk.epic.capability.epicAdd.EpicAddCapabilityProvider;
import tkk.epic.capability.epicAdd.trail.ParticleTrail;
import tkk.epic.capability.epicAdd.trail.TkkCustomTrail;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.client.model.ItemSkin;
import yesman.epicfight.api.client.model.ItemSkins;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.CubicBezierCurve;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.particle.EpicFightParticleRenderTypes;
import yesman.epicfight.client.particle.TrailParticle;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class TkkTrailParticle extends TextureSheetParticle {
    private final ResourceLocation render_type=new ResourceLocation("");
    protected final Joint joint;
    protected final ParticleTrail trailInfo;
    protected final StaticAnimation animation;
    protected final LivingEntityPatch<?> entitypatch;
    protected final List<TkkTrailParticle.TrailEdge> invisibleTrailEdges;
    protected final List<TkkTrailParticle.TrailEdge> visibleTrailEdges;
    protected boolean animationEnd;
    protected float startEdgeCorrection = 0.0F;
    protected final ItemSkin itemSkin;
    protected final Vec3 start;
    protected final Vec3 end;


    protected TkkTrailParticle(ClientLevel level, LivingEntityPatch<?> entitypatch, Joint joint, StaticAnimation animation, ParticleTrail trailInfo, SpriteSet spriteSet,ItemSkin itemSkin) {
        super(level, 0, 0, 0);
        this.itemSkin=itemSkin;
        this.joint = joint;
        this.entitypatch = entitypatch;
        this.animation = animation;
        this.invisibleTrailEdges = Lists.newLinkedList();
        this.visibleTrailEdges = Lists.newLinkedList();
        this.hasPhysics = false;
        this.trailInfo = trailInfo;
        if(trailInfo.scalePos){
           if(itemSkin!=null){
               this.start=itemSkin.trailInfo().start.multiply(trailInfo.start);
               this.end=itemSkin.trailInfo().start.multiply(trailInfo.end);
           }else{
               this.start=new Vec3(0,0,0);
               this.end=new Vec3(0,0,0);
           }
        }else{
            this.start=trailInfo.start;
            this.end=trailInfo.end;
        }
        Vec3 entityPos = entitypatch.getOriginal().position();
        this.move(entityPos.x, entityPos.y + entitypatch.getOriginal().getEyeHeight(), entityPos.z);

        float size = (float)Math.max(this.start.length(), this.end.length()) * 2.0F;
        this.setSize(size, size);
        this.setSpriteFromAge(spriteSet);

        Pose prevPose = this.entitypatch.getAnimator().getPose(0.0F);
        Pose middlePose = this.entitypatch.getAnimator().getPose(0.5F);
        Pose currentPose = this.entitypatch.getAnimator().getPose(1.0F);
        Vec3 posOld = this.entitypatch.getOriginal().getPosition(0.0F);
        Vec3 posMid = this.entitypatch.getOriginal().getPosition(0.5F);
        Vec3 posCur = this.entitypatch.getOriginal().getPosition(1.0F);

        OpenMatrix4f prvmodelTf = OpenMatrix4f.createTranslation((float)posOld.x, (float)posOld.y, (float)posOld.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(this.entitypatch.getModelMatrix(0.0F)));
        OpenMatrix4f middleModelTf = OpenMatrix4f.createTranslation((float)posMid.x, (float)posMid.y, (float)posMid.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(this.entitypatch.getModelMatrix(0.5F)));
        OpenMatrix4f curModelTf = OpenMatrix4f.createTranslation((float)posCur.x, (float)posCur.y, (float)posCur.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(this.entitypatch.getModelMatrix(1.0F)));

        OpenMatrix4f prevJointTf = this.entitypatch.getArmature().getBindedTransformFor(prevPose, this.joint).mulFront(prvmodelTf);
        OpenMatrix4f middleJointTf = this.entitypatch.getArmature().getBindedTransformFor(middlePose, this.joint).mulFront(middleModelTf);
        OpenMatrix4f currentJointTf = this.entitypatch.getArmature().getBindedTransformFor(currentPose, this.joint).mulFront(curModelTf);

        Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, this.start);
        Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, this.end);
        Vec3 middleStartPos = OpenMatrix4f.transform(middleJointTf, this.start);
        Vec3 middleEndPos = OpenMatrix4f.transform(middleJointTf, this.end);
        Vec3 currentStartPos = OpenMatrix4f.transform(currentJointTf, this.start);
        Vec3 currentEndPos = OpenMatrix4f.transform(currentJointTf, this.end);

        this.invisibleTrailEdges.add(new TkkTrailParticle.TrailEdge(prevStartPos, prevEndPos, this.trailInfo.trailLifetime));
        this.invisibleTrailEdges.add(new TkkTrailParticle.TrailEdge(middleStartPos, middleEndPos, this.trailInfo.trailLifetime));
        this.invisibleTrailEdges.add(new TkkTrailParticle.TrailEdge(currentStartPos, currentEndPos, this.trailInfo.trailLifetime));
        /*
        this.rCol = Math.max(this.trailInfo.rCol, 0.0F);
        this.gCol = Math.max(this.trailInfo.gCol, 0.0F);
        this.bCol = Math.max(this.trailInfo.bCol, 0.0F);

        if (this.trailInfo.texturePath != null) {
            TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
            AbstractTexture abstracttexture = texturemanager.getTexture(this.trailInfo.texturePath);

            RenderSystem.bindTexture(abstracttexture.getId());
            RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        }

         */
    }

    @Deprecated /** This constructor is only for {@link ModelPreviewer} **/
    protected TkkTrailParticle(Armature armature, LivingEntityPatch<?> entitypatch, Joint joint, StaticAnimation animation, ParticleTrail trailInfo,ItemSkin itemSkin) {
        super(null, 0, 0, 0);
        this.itemSkin=itemSkin;
        this.entitypatch = entitypatch;
        this.joint = joint;
        this.animation = animation;
        this.invisibleTrailEdges = Lists.newLinkedList();
        this.visibleTrailEdges = Lists.newLinkedList();
        this.hasPhysics = false;
        this.trailInfo = trailInfo;
        if(trailInfo.scalePos){
            if(itemSkin!=null){
                this.start=itemSkin.trailInfo().start.multiply(trailInfo.start);
                this.end=itemSkin.trailInfo().start.multiply(trailInfo.end);
            }else{
                this.start=new Vec3(0,0,0);
                this.end=new Vec3(0,0,0);
            }
        }else{
            this.start=trailInfo.start;
            this.end=trailInfo.end;
        }

        float size = (float)Math.max(this.start.length(), this.trailInfo.end.length()) * 2.0F;
        this.setSize(size, size);

        Pose prevPose = this.entitypatch.getAnimator().getPose(0.0F);
        Pose middlePose = this.entitypatch.getAnimator().getPose(0.5F);
        Pose currentPose = this.entitypatch.getAnimator().getPose(1.0F);

        OpenMatrix4f prevJointTf = armature.getBindedTransformFor(prevPose, this.joint);
        OpenMatrix4f middleJointTf = armature.getBindedTransformFor(middlePose, this.joint);
        OpenMatrix4f currentJointTf = armature.getBindedTransformFor(currentPose, this.joint);

        Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, this.start);
        Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, this.end);
        Vec3 middleStartPos = OpenMatrix4f.transform(middleJointTf, this.start);
        Vec3 middleEndPos = OpenMatrix4f.transform(middleJointTf, this.end);
        Vec3 currentStartPos = OpenMatrix4f.transform(currentJointTf, this.start);
        Vec3 currentEndPos = OpenMatrix4f.transform(currentJointTf, this.end);

        this.invisibleTrailEdges.add(new TkkTrailParticle.TrailEdge(prevStartPos, prevEndPos, this.trailInfo.trailLifetime));
        this.invisibleTrailEdges.add(new TkkTrailParticle.TrailEdge(middleStartPos, middleEndPos, this.trailInfo.trailLifetime));
        this.invisibleTrailEdges.add(new TkkTrailParticle.TrailEdge(currentStartPos, currentEndPos, this.trailInfo.trailLifetime));
        /*
        this.rCol = Math.max(this.trailInfo.rCol, 0.0F);
        this.gCol = Math.max(this.trailInfo.gCol, 0.0F);
        this.bCol = Math.max(this.trailInfo.bCol, 0.0F);

        if (this.trailInfo.texturePath != null) {
            TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
            AbstractTexture abstracttexture = texturemanager.getTexture(this.trailInfo.texturePath);

            RenderSystem.bindTexture(abstracttexture.getId());
            RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        }

         */
    }

    public final boolean whileEnd(double start,double end,double now){
        if(start<end){
            return now<end;
        }else if(start>end){
            return now>end;
        }
        return false;
    }
    public final double getWhileAdd(double start,double end){
        //double temp=Math.abs(start-end);
        double a=(start>end)?-1:1;
        //return a*temp;
        return a;
    }
    public final ArrayList<Vec3> getLinePos(Vec3 start,Vec3 end,double r){
        double distance = Math.sqrt(
                Math.pow(end.x - start.x, 2) +
                Math.pow(end.y - start.y, 2) +
                Math.pow(end.z - start.z, 2)
        );
        double numPoints = Math.floor(distance / r);
        double step = r / distance;
        ArrayList<Vec3> points = new ArrayList();
        for (var i = 0; i <= numPoints; i++) {
            var t = i * step;
            var x = start.x + t * (end.x - start.x);
            var y = start.y + t * (end.y - start.y);
            var z = start.z + t * (end.z - start.z);
            points.add(new Vec3(x, y, z));
        }
        points.add(end);

        return points;
    }




    @Override
    public void tick() {
        AnimationPlayer animPlayer = this.entitypatch.getAnimator().getPlayerFor(this.animation);
        //spawnParticle
        Level world=entitypatch.getOriginal().level();
        ParticleOptions iParticleData;
        try {
            iParticleData=trailInfo.getIParticleData();
        } catch (Exception e) {
            TkkEpic.getInstance().broadcast("Error getIParticleData:"+e);
            return;
        }
        /*
        for(TkkTrailParticle.TrailEdge trailEdge:this.visibleTrailEdges){
            Vec3 start=trailEdge.start;
            Vec3 end=trailEdge.end;
            double x,y,z;
            x=start.x;
            do{
                y=start.y;
                do{
                    z=start.z;
                    do{
                        for(int i=0;i<trailInfo.count;i++) {
                            Vec3 pos = new Vec3(x,y,z);
                            double a1=random.nextGaussian()*trailInfo.dist;
                            double a2=random.nextGaussian()*trailInfo.dist;
                            double a3=random.nextGaussian()*trailInfo.dist;
                            //world.addParticle(iParticleData, pos.x+a1, pos.y+a2, pos.z+a3, random.nextGaussian() * trailInfo.speed, random.nextGaussian() * trailInfo.speed, random.nextGaussian() * trailInfo.speed);
                        }

                        z+=getWhileAdd(start.z,end.z)*trailInfo.spaceBetween;
                    }while (whileEnd(start.z,end.z,z));

                    y+=getWhileAdd(start.y,end.y)*trailInfo.spaceBetween;
                }while (whileEnd(start.y,end.y,y));

                x+=getWhileAdd(start.x,end.x)*trailInfo.spaceBetween;
            }while (whileEnd(start.x,end.x,x));


        }


         */

        if(!this.visibleTrailEdges.isEmpty()) {
            float partialTick = 0;
            //PoseStack poseStack = new PoseStack();
            int light = this.getLightColor(0);
            //this.setupPoseStack(poseStack, camera, partialTick);
            //Matrix4f matrix4f = poseStack.last().pose();
            int edges = this.visibleTrailEdges.size() - 1;
            boolean startFade = this.visibleTrailEdges.get(0).lifetime == 1;
            boolean endFade = this.visibleTrailEdges.get(edges).lifetime == this.trailInfo.trailLifetime;
            float startEdge = (startFade ? this.trailInfo.interpolateCount * 2 * partialTick : 0.0F) + this.startEdgeCorrection;
            float endEdge = endFade ? Math.min(edges - (this.trailInfo.interpolateCount * 2) * (1.0F - partialTick), edges - 1) : edges - 1;
            float interval = 1.0F / (endEdge - startEdge);
            float fading = 1.0F;

            if (this.animationEnd) {
                if (TrailInfo.isValidTime(this.trailInfo.fadeTime)) {
                    fading = ((float) this.lifetime / (float) this.trailInfo.trailLifetime);
                } else {
                    fading = Mth.clamp((this.lifetime + (1.0F - partialTick)) / this.trailInfo.trailLifetime, 0.0F, 1.0F);
                }
            }

            float partialStartEdge = interval * (startEdge % 1.0F);
            float from = -partialStartEdge;
            float to = -partialStartEdge + interval;

            for (int i = (int) (startEdge); i < (int) endEdge + 1; i++) {
                TrailEdge e1 = this.visibleTrailEdges.get(i);
                TrailEdge e2 = this.visibleTrailEdges.get(i + 1);
                Vector4f pos1 = new Vector4f((float) e1.start.x, (float) e1.start.y, (float) e1.start.z, 1.0F);
                Vector4f pos2 = new Vector4f((float) e1.end.x, (float) e1.end.y, (float) e1.end.z, 1.0F);
                Vector4f pos3 = new Vector4f((float) e2.end.x, (float) e2.end.y, (float) e2.end.z, 1.0F);
                Vector4f pos4 = new Vector4f((float) e2.start.x, (float) e2.start.y, (float) e2.start.z, 1.0F);

                //pos1.mul(matrix4f);
                //pos2.mul(matrix4f);
                //pos3.mul(matrix4f);
                //pos4.mul(matrix4f);

                float alphaFrom = Mth.clamp(from, 0.0F, 1.0F);
                float alphaTo = Mth.clamp(to, 0.0F, 1.0F);

                ArrayList<Vec3> line1 = getLinePos(new Vec3(pos1.x, pos1.y, pos1.z), new Vec3(pos4.x, pos4.y, pos4.z), trailInfo.spaceBetween);
                ArrayList<Vec3> line2 = getLinePos(new Vec3(pos2.x, pos2.y, pos2.z), new Vec3(pos3.x, pos3.y, pos3.z), trailInfo.spaceBetween);
                /*
                for (Vec3 pos : line1) {
                    double a1 = random.nextGaussian() * trailInfo.dist;
                    double a2 = random.nextGaussian() * trailInfo.dist;
                    double a3 = random.nextGaussian() * trailInfo.dist;
                    world.addParticle(iParticleData, pos.x + a1, pos.y + a2, pos.z + a3, random.nextGaussian() * trailInfo.speed, random.nextGaussian() * trailInfo.speed, random.nextGaussian() * trailInfo.speed);

                }
                for (Vec3 pos : line2) {
                    double a1 = random.nextGaussian() * trailInfo.dist;
                    double a2 = random.nextGaussian() * trailInfo.dist;
                    double a3 = random.nextGaussian() * trailInfo.dist;
                    world.addParticle(iParticleData, pos.x + a1, pos.y + a2, pos.z + a3, random.nextGaussian() * trailInfo.speed, random.nextGaussian() * trailInfo.speed, random.nextGaussian() * trailInfo.speed);

                }

                 */
                ArrayList<Vec3> max;
                ArrayList<Vec3> min;
                if(line1.size()<line2.size()){
                    max=line2;
                    min=line1;
                }else{
                    max=line1;
                    min=line2;
                }

                int step= (int) max.size()/min.size();
                int add=max.size()%min.size();
                int addStart= max.size()/min.size()-add;

                int j=0;
                int steped=0;
                int istep=0;
                for(int k=0;k<max.size();k++){
                    boolean needAdd=steped>=addStart;
                    ArrayList<Vec3> line = getLinePos(max.get(k),min.get(j), trailInfo.spaceBetween);
                    for (Vec3 pos : line) {
                        for(int l=0;l<trailInfo.count;l++) {
                            double a1 = random.nextGaussian() * trailInfo.dist;
                            double a2 = random.nextGaussian() * trailInfo.dist;
                            double a3 = random.nextGaussian() * trailInfo.dist;
                            world.addParticle(iParticleData, pos.x + a1, pos.y + a2, pos.z + a3, random.nextGaussian() * trailInfo.speed, random.nextGaussian() * trailInfo.speed, random.nextGaussian() * trailInfo.speed);
                        }
                    }
                    istep+=1;
                    if(needAdd){
                        if(istep>=step+1){
                            istep=0;
                            steped+=1;
                            j+=1;
                        }
                    }else{
                        if(istep>=step){
                            istep=0;
                            steped+=1;
                            j+=1;
                        }
                    }

                }

                /*
                vertexConsumer.vertex(pos1.x(), pos1.y(), pos1.z()).uv(from, 1.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaFrom * fading).uv2(light).endVertex();
                vertexConsumer.vertex(pos2.x(), pos2.y(), pos2.z()).uv(from, 0.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaFrom * fading).uv2(light).endVertex();
                vertexConsumer.vertex(pos3.x(), pos3.y(), pos3.z()).uv(to, 0.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaTo * fading).uv2(light).endVertex();
                vertexConsumer.vertex(pos4.x(), pos4.y(), pos4.z()).uv(to, 1.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaTo * fading).uv2(light).endVertex();


                 */
                from += interval;
                to += interval;
            }
        }
        this.visibleTrailEdges.removeIf(v -> !v.isAlive());
        if (this.animationEnd) {
            if (this.lifetime-- == 0) {
                this.remove();
            }
        } else {
            if (!this.entitypatch.getOriginal().isAlive() || this.animation != animPlayer.getAnimation().getRealAnimation() || animPlayer.getElapsedTime() > this.trailInfo.endTime) {
                this.animationEnd = true;
                this.lifetime = this.trailInfo.trailLifetime;
            }
        }

        if (TrailInfo.isValidTime(this.trailInfo.fadeTime) && this.trailInfo.endTime < animPlayer.getElapsedTime()) {
            return;
        }

        double xd = Math.pow(this.entitypatch.getOriginal().getX() - this.entitypatch.getOriginal().xo, 2);
        double yd = Math.pow(this.entitypatch.getOriginal().getY() - this.entitypatch.getOriginal().yo, 2);
        double zd = Math.pow(this.entitypatch.getOriginal().getZ() - this.entitypatch.getOriginal().zo, 2);
        float move = (float)Math.sqrt(xd + yd + zd) * 2.0F;
        this.setSize(this.bbWidth + move, this.bbHeight + move);

        boolean isTrailInvisible = animPlayer.getAnimation().isLinkAnimation() || animPlayer.getElapsedTime() <= this.trailInfo.startTime;
        boolean isFirstTrail = this.visibleTrailEdges.isEmpty();
        boolean needCorrection = (!isTrailInvisible && isFirstTrail);

        if (needCorrection) {
            float startCorrection = Math.max((this.trailInfo.startTime - animPlayer.getPrevElapsedTime()) / (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()), 0.0F);
            this.startEdgeCorrection = this.trailInfo.interpolateCount * 2 * startCorrection;
        }

        ParticleTrail trailInfo = this.trailInfo;
        Pose prevPose = this.entitypatch.getAnimator().getPose(0.0F);
        Pose middlePose = this.entitypatch.getAnimator().getPose(0.5F);
        Pose currentPose = this.entitypatch.getAnimator().getPose(1.0F);
        Vec3 posOld = this.entitypatch.getOriginal().getPosition(0.0F);
        Vec3 posMid = this.entitypatch.getOriginal().getPosition(0.5F);
        Vec3 posCur = this.entitypatch.getOriginal().getPosition(1.0F);

        OpenMatrix4f prvmodelTf = OpenMatrix4f.createTranslation((float)posOld.x, (float)posOld.y, (float)posOld.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(this.entitypatch.getModelMatrix(0.0F)));
        OpenMatrix4f middleModelTf = OpenMatrix4f.createTranslation((float)posMid.x, (float)posMid.y, (float)posMid.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(this.entitypatch.getModelMatrix(0.5F)));
        OpenMatrix4f curModelTf = OpenMatrix4f.createTranslation((float)posCur.x, (float)posCur.y, (float)posCur.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(this.entitypatch.getModelMatrix(1.0F)));

        OpenMatrix4f prevJointTf = this.entitypatch.getArmature().getBindedTransformFor(prevPose, this.joint).mulFront(prvmodelTf);
        OpenMatrix4f middleJointTf = this.entitypatch.getArmature().getBindedTransformFor(middlePose, this.joint).mulFront(middleModelTf);
        OpenMatrix4f currentJointTf = this.entitypatch.getArmature().getBindedTransformFor(currentPose, this.joint).mulFront(curModelTf);
        Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, this.start);
        Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, this.end);
        Vec3 middleStartPos = OpenMatrix4f.transform(middleJointTf, this.start);
        Vec3 middleEndPos = OpenMatrix4f.transform(middleJointTf, this.end);
        Vec3 currentStartPos = OpenMatrix4f.transform(currentJointTf, this.start);
        Vec3 currentEndPos = OpenMatrix4f.transform(currentJointTf, this.end);

        List<Vec3> finalStartPositions;
        List<Vec3> finalEndPositions;
        boolean visibleTrail;

        if (isTrailInvisible) {
            finalStartPositions = Lists.newArrayList();
            finalEndPositions = Lists.newArrayList();
            finalStartPositions.add(prevStartPos);
            finalStartPositions.add(middleStartPos);
            finalEndPositions.add(prevEndPos);
            finalEndPositions.add(middleEndPos);

            this.invisibleTrailEdges.clear();
            visibleTrail = false;
        } else {
            List<Vec3> startPosList = Lists.newArrayList();
            List<Vec3> endPosList = Lists.newArrayList();
            TkkTrailParticle.TrailEdge edge1;
            TkkTrailParticle.TrailEdge edge2;

            if (isFirstTrail) {
                int lastIdx = this.invisibleTrailEdges.size() - 1;
                edge1 = this.invisibleTrailEdges.get(lastIdx);
                edge2 = new TkkTrailParticle.TrailEdge(prevStartPos, prevEndPos, -1);
            } else {
                edge1 = this.visibleTrailEdges.get(this.visibleTrailEdges.size() - (this.trailInfo.interpolateCount / 2 + 1));
                edge2 = this.visibleTrailEdges.get(this.visibleTrailEdges.size() - 1);
                edge2.lifetime++;
            }

            startPosList.add(edge1.start);
            endPosList.add(edge1.end);
            startPosList.add(edge2.start);
            endPosList.add(edge2.end);
            startPosList.add(middleStartPos);
            endPosList.add(middleEndPos);
            startPosList.add(currentStartPos);
            endPosList.add(currentEndPos);

            finalStartPositions = CubicBezierCurve.getBezierInterpolatedPoints(startPosList, 1, 3, this.trailInfo.interpolateCount);
            finalEndPositions = CubicBezierCurve.getBezierInterpolatedPoints(endPosList, 1, 3, this.trailInfo.interpolateCount);

            if (!isFirstTrail) {
                finalStartPositions.remove(0);
                finalEndPositions.remove(0);
            }

            visibleTrail = true;
        }

        this.makeTrailEdges(finalStartPositions, finalEndPositions, visibleTrail ? this.visibleTrailEdges : this.invisibleTrailEdges);
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        /*
        if (this.visibleTrailEdges.isEmpty()) {
            return;
        }

        PoseStack poseStack = new PoseStack();
        int light = this.getLightColor(partialTick);
        this.setupPoseStack(poseStack, camera, partialTick);
        Matrix4f matrix4f = poseStack.last().pose();
        int edges = this.visibleTrailEdges.size() - 1;
        boolean startFade = this.visibleTrailEdges.get(0).lifetime == 1;
        boolean endFade = this.visibleTrailEdges.get(edges).lifetime == this.trailInfo.trailLifetime;
        float startEdge = (startFade ? this.trailInfo.interpolateCount * 2 * partialTick : 0.0F) + this.startEdgeCorrection;
        float endEdge = endFade ? Math.min(edges - (this.trailInfo.interpolateCount * 2) * (1.0F - partialTick), edges - 1) : edges - 1;
        float interval = 1.0F / (endEdge - startEdge);
        float fading = 1.0F;

        if (this.animationEnd) {
            if (TrailInfo.isValidTime(this.trailInfo.fadeTime)) {
                fading = ((float)this.lifetime / (float)this.trailInfo.trailLifetime);
            } else {
                fading = Mth.clamp((this.lifetime + (1.0F - partialTick)) / this.trailInfo.trailLifetime, 0.0F, 1.0F);
            }
        }

        float partialStartEdge = interval * (startEdge % 1.0F);
        float from = -partialStartEdge;
        float to = -partialStartEdge + interval;

        for (int i = (int)(startEdge); i < (int)endEdge + 1; i++) {
            TkkTrailParticle.TrailEdge e1 = this.visibleTrailEdges.get(i);
            TkkTrailParticle.TrailEdge e2 = this.visibleTrailEdges.get(i + 1);
            Vector4f pos1 = new Vector4f((float)e1.start.x, (float)e1.start.y, (float)e1.start.z, 1.0F);
            Vector4f pos2 = new Vector4f((float)e1.end.x, (float)e1.end.y, (float)e1.end.z, 1.0F);
            Vector4f pos3 = new Vector4f((float)e2.end.x, (float)e2.end.y, (float)e2.end.z, 1.0F);
            Vector4f pos4 = new Vector4f((float)e2.start.x, (float)e2.start.y, (float)e2.start.z, 1.0F);

            pos1.mul(matrix4f);
            pos2.mul(matrix4f);
            pos3.mul(matrix4f);
            pos4.mul(matrix4f);

            float alphaFrom = Mth.clamp(from, 0.0F, 1.0F);
            float alphaTo = Mth.clamp(to, 0.0F, 1.0F);

            vertexConsumer.vertex(pos1.x(), pos1.y(), pos1.z()).uv(from, 1.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaFrom * fading).uv2(light).endVertex();
            vertexConsumer.vertex(pos2.x(), pos2.y(), pos2.z()).uv(from, 0.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaFrom * fading).uv2(light).endVertex();
            vertexConsumer.vertex(pos3.x(), pos3.y(), pos3.z()).uv(to, 0.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaTo * fading).uv2(light).endVertex();
            vertexConsumer.vertex(pos4.x(), pos4.y(), pos4.z()).uv(to, 1.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaTo * fading).uv2(light).endVertex();

            from += interval;
            to += interval;
        }

         */
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return EpicFightParticleRenderTypes.TRAIL_PROVIDER.apply(this.render_type);
    }

    protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
        Vec3 vec3 = camera.getPosition();
        float x = (float)-vec3.x();
        float y = (float)-vec3.y();
        float z = (float)-vec3.z();

        poseStack.translate(x, y, z);
    }

    protected void makeTrailEdges(List<Vec3> startPositions, List<Vec3> endPositions, List<TkkTrailParticle.TrailEdge> dest) {
        for (int i = 0; i < startPositions.size(); i++) {
            dest.add(new TkkTrailParticle.TrailEdge(startPositions.get(i), endPositions.get(i), this.trailInfo.trailLifetime));
        }
    }



    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSeta;

        public Provider(SpriteSet spriteSet) {
            this.spriteSeta = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            int eid = (int)Double.doubleToRawLongBits(x);
            int animid = (int)Double.doubleToRawLongBits(z);
            int jointId = (int)Double.doubleToRawLongBits(xSpeed);
            int idx = (int)Double.doubleToRawLongBits(ySpeed);
            Entity entity = level.getEntity(eid);
            if (entity != null) {
                LivingEntityPatch<?> entitypatch = (LivingEntityPatch) EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
                StaticAnimation animation = AnimationManager.getInstance().byId(animid);
                TkkCustomTrail tkkCustomTrail=entity.getCapability(EpicAddCapabilityProvider.EPIC_ADD_CAPABILITY).orElse(null).getTkkCustomTrail();
                ParticleTrail tkkTrail = tkkCustomTrail.readyParticleTrails.get(idx);


                InteractionHand hand=tkkTrail.hand? InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND;
                ItemSkin itemSkin=null;
                if (hand != null) {
                    ItemStack stack = ((LivingEntity) entitypatch.getOriginal()).getItemInHand(hand);
                    itemSkin = ItemSkins.getItemSkin(stack.getItem());
                }


                if (entitypatch != null && animation != null) {
                    return new TkkTrailParticle(level, entitypatch, entitypatch.getArmature().searchJointById(jointId), animation, tkkTrail, this.spriteSeta,itemSkin);
                }
            }

            return null;
        }
    }
    @OnlyIn(Dist.CLIENT)
    public static class TrailEdge {
        public final Vec3 start;
        public final Vec3 end;
        public int lifetime;

        public TrailEdge(Vec3 start, Vec3 end, int lifetime) {
            this.start = start;
            this.end = end;
            this.lifetime = lifetime;
        }

        public boolean isAlive() {
            return --this.lifetime > 0;
        }
    }
}
