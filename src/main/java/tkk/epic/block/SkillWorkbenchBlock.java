package tkk.epic.block;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import tkk.epic.TkkEpic;
import tkk.epic.block.entity.SkillWorkbenchBlockEntity;
import tkk.epic.block.entity.TkkEpicBlockEntitys;


public class SkillWorkbenchBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public SkillWorkbenchBlock() {
        super(BlockBehaviour.Properties.copy((BlockBehaviour) Blocks.ANVIL).sound(SoundType.ANVIL).strength(5.0F, 10.0F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_58126_) {
        return this.defaultBlockState().setValue(FACING, p_58126_.getHorizontalDirection().getOpposite());
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58150_) {
        p_58150_.add(FACING);
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof SkillWorkbenchBlockEntity) {
                ((SkillWorkbenchBlockEntity) blockEntity).drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide()) {return InteractionResult.sidedSuccess(pLevel.isClientSide());}
        BlockEntity entity = pLevel.getBlockEntity(pPos);
        if(!(entity instanceof SkillWorkbenchBlockEntity)) {
            throw new IllegalStateException("Our Container provider is missing!");
        }
        if(pPlayer.isCrouching()){
            if(((SkillWorkbenchBlockEntity) entity).shiftUse(pPlayer)){
                if(pLevel instanceof ServerLevel){
                    try {
                        ParticleType a= ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation("minecraft:totem_of_undying"));
                        ParticleOptions ipd=a.getDeserializer().fromCommand(a,new StringReader(""));
                        ((ServerLevel) pLevel).sendParticles(ipd,pPos.getX()+0.5,pPos.getY()+0.5,pPos.getZ()+0.5,30,0,0,0,0.6);

                    } catch (CommandSyntaxException e) {
                    }
                }
            }
        }else {
            NetworkHooks.openScreen(((ServerPlayer)pPlayer), (SkillWorkbenchBlockEntity)entity, pPos);
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new SkillWorkbenchBlockEntity(p_153215_,p_153216_);
    }
}
