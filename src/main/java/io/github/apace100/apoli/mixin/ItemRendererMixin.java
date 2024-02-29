package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ModifyItemRenderPower;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Shadow @Final private ItemModels models;

    @Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
    private void getModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        List<ModifyItemRenderPower> powers = PowerHolderComponent.getPowers(entity, ModifyItemRenderPower.class);
        for (ModifyItemRenderPower power : powers) {
            if (power.doesApply(stack)) {
                BakedModel model = this.models.getModelManager().getModel(power.getModelIdentifier());

                if(model != null && model != this.models.getModelManager().getMissingModel()) {
                    cir.setReturnValue(model);
                    break;
                }
            }
        }

    }

}
