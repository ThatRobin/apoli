package io.github.apace100.apoli;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ApoliMixinPlugin implements IMixinConfigPlugin {

    private boolean isConnectorLoaded = false;
    private boolean isAppleskinLoaded = false;

    @Override
    public void onLoad(String mixinPackage) {
        isConnectorLoaded = FabricLoader.getInstance().isModLoaded("connector");
        isAppleskinLoaded = FabricLoader.getInstance().isModLoaded("appleskin");
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains(".mixin.ElytraFeatureRendererMixin") || mixinClassName.contains(".mixin.HeldItemRendererMixin") || mixinClassName.contains(".mixin.InGameHudMixin")) {
            return !isConnectorLoaded;
        }
        if (mixinClassName.contains(".mixin.integration.connector")) {
            return isConnectorLoaded;
        }
        if (mixinClassName.contains(".mixin.integration.appleskin")) {
            return isAppleskinLoaded;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

}
