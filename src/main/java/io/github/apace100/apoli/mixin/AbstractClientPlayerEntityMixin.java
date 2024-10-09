package io.github.apace100.apoli.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.mojang.authlib.GameProfile;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ModifyFovPower;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {

    private AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @ModifyReturnValue(method = "getFovMultiplier", at = @At(value = "RETURN", ordinal = 0))
    private float apoli$modifySpyglassFov(float original) {
        return PowerHolderComponent.modify(this, ModifyFovPower.class, original);
    }

    @ModifyReturnValue(method = "getFovMultiplier", at = @At(value = "RETURN", ordinal = 1))
    private float apoli$modifyFov(float original, @Local float end) {
        float delta = MinecraftClient.getInstance().options.getFovEffectScale().getValue().floatValue();
        float start = 1.0F;

        List<ModifyFovPower> mfps = PowerHolderComponent.getPowers(this, ModifyFovPower.class);
        boolean affectedByFovEffectScale = mfps.isEmpty() || mfps
            .stream()
            .anyMatch(ModifyFovPower::isAffectedByFovEffectScale);

        float newEnd = PowerHolderComponent.modify(this, ModifyFovPower.class, end);
        float newDelta = affectedByFovEffectScale ? delta : start;

        return MathHelper.lerp(newDelta, start, newEnd);

    }

}
