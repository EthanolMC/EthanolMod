package rocks.ethanol.ethanolmod.injection.mixins;

import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rocks.ethanol.ethanolmod.screen.MainScreen;

@Mixin(PauseScreen.class)
public abstract class MixinGameMenuScreen extends Screen {

    protected MixinGameMenuScreen(final Component ignored) {
        super(ignored);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addEthanolModButton(final CallbackInfo info) {
        this.addRenderableWidget(MainScreen.createButton((PauseScreen) (Object) this));
    }

}
