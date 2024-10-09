package io.github.apace100.apoli.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ModifyBreakSpeedPower;
import io.github.apace100.apoli.power.ModifyHarvestPower;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {


    @Inject(method = "calcBlockBreakingDelta", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getBlockBreakingSpeed(Lnet/minecraft/block/BlockState;)F"))
    private void apoli$modifyEffectiveTool(BlockState state, PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir, @Local(ordinal=0) LocalIntRef i) {
        boolean canHarvest = PowerHolderComponent.getPowers(player, ModifyHarvestPower.class)
            .stream()
            .filter(mhp -> mhp.doesApply(pos))
            .max(ModifyHarvestPower::compareTo)
            .map(ModifyHarvestPower::isHarvestAllowed)
            .orElse(player.canHarvest(state));

        i.set(canHarvest ? 30 : 100);
    }

    @ModifyExpressionValue(method = "calcBlockBreakingDelta", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getHardness(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"))
    private float apoli$modifyBlockHardness(float original, BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {

        List<Modifier> hardnessModifiers = PowerHolderComponent.getPowers(player, ModifyBreakSpeedPower.class)
            .stream()
            .filter(p -> p.doesApply(pos))
            .flatMap(p -> p.getHardnessModifiers().stream())
            .toList();

        return (float) Math.max(ModifierUtil.applyModifiers(player, hardnessModifiers, original), -1.0F);

    }

    @ModifyReturnValue(method = "calcBlockBreakingDelta", at = @At("RETURN"))
    private float apoli$modifyBlockBreakSpeed(float original, BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        return PowerHolderComponent.modify(player, ModifyBreakSpeedPower.class, original, mbsp -> mbsp.doesApply(pos));
    }

}
