package rocks.ethanol.ethanolmod.injection.mixins;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rocks.ethanol.ethanolmod.screen.MainScreen;

@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen extends Screen {

    protected MixinGameMenuScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addEthanolModButton(final CallbackInfo info) {
        this.addDrawableChild(MainScreen.createButton((GameMenuScreen) (Object) this));
    }

}
