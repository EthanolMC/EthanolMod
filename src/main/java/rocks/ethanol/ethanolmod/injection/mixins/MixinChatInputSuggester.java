package rocks.ethanol.ethanolmod.injection.mixins;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.commands.SharedSuggestionProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestions.class)
public abstract class MixinChatInputSuggester implements MinecraftWrapper {

    @Shadow
    private ParseResults<SharedSuggestionProvider> currentParse;

    @Shadow
    @Final
    EditBox input;

    @Shadow
    boolean keepSuggestions;

    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    private CommandSuggestions.SuggestionsList suggestions;

    @Shadow
    protected abstract void showSuggestions(boolean hasNewLine);

    @Inject(method = "updateCommandInfo", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false), cancellable = true)
    public final void suggestClientCommands(final CallbackInfo info) {
        if (mc.hasSingleplayerServer()) {
            return;
        }
        final EthanolMod ethanolMod = EthanolMod.getInstance();
        final CommandDispatcher<SharedSuggestionProvider> commandDispatcher = ethanolMod.getCommandDispatcher();
        if (commandDispatcher == null) return;
        final String prefix = ethanolMod.getConfiguration().getCommandPrefix();
        final int length = prefix.length();
        final String typed = this.input.getValue();
        if (typed.startsWith(prefix) && mc.screen instanceof ChatScreen) {
            final StringReader reader = new StringReader(typed.substring(length));
            if (this.currentParse == null) {
                this.currentParse = commandDispatcher.parse(reader, ethanolMod.getCommandSource());
            }

            final int cursor = this.input.getCursorPosition();
            if (cursor >= length && (this.suggestions == null || !this.keepSuggestions)) {
                this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.currentParse, cursor);
                this.pendingSuggestions.thenRun(() -> {
                    if (this.pendingSuggestions.isDone()) this.showSuggestions(false);
                });
            }
            info.cancel();
        }
    }

}
