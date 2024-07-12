package rocks.ethanol.ethanolmod.injection.mixins;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public abstract class MixinChatInputSuggester implements MinecraftWrapper {

    @Shadow
    private ParseResults<CommandSource> parse;

    @Shadow
    @Final
    TextFieldWidget textField;

    @Shadow
    boolean completingSuggestions;

    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    private ChatInputSuggestor.SuggestionWindow window;

    @Shadow
    protected abstract void showCommandSuggestions();

    @Inject(method = "refresh", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public final void suggestClientCommands(final CallbackInfo info, final String string, final StringReader reader) {
        final EthanolMod ethanolMod = EthanolMod.getInstance();
        final CommandDispatcher<CommandSource> commandDispatcher = ethanolMod.getCommandDispatcher();
        if (commandDispatcher == null) return;
        final String prefix = ethanolMod.getConfiguration().getCommandPrefix();
        final int length = prefix.length();
        if (reader.canRead(length) && reader.getString().startsWith(prefix, reader.getCursor()) && this.mc.currentScreen instanceof ChatScreen) {
            reader.setCursor(reader.getCursor() + length);
            if (this.parse == null) {
                this.parse = commandDispatcher.parse(reader, ethanolMod.getCommandSource());
            }

            final int cursor = this.textField.getCursor();
            if (cursor >= length && (this.window == null || !this.completingSuggestions)) {
                this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.parse, cursor);
                this.pendingSuggestions.thenRun(() -> {
                    if (this.pendingSuggestions.isDone()) this.showCommandSuggestions();
                });
            }
            info.cancel();
        }
    }

}
