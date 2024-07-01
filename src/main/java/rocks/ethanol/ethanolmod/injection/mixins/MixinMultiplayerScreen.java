package rocks.ethanol.ethanolmod.injection.mixins;

import rocks.ethanol.ethanolmod.config.ConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {

    protected MixinMultiplayerScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addEthanolModButton(final CallbackInfo info) {
        this.addDrawableChild(ConfigScreen.createButton((MultiplayerScreen) (Object) this));
    }

}
