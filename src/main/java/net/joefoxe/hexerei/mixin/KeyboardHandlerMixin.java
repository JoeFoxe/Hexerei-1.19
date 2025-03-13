package net.joefoxe.hexerei.mixin;

import net.joefoxe.hexerei.data.books.BookWritableTextBox;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.minecraft.client.KeyboardHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Inject(method = "charTyped", at = @At("HEAD"))
    private void modifyScreen(long windowPointer, int codePoint, int modifiers, CallbackInfo ci) {
        if (PageDrawing.focusedWritableTextBox != null) {
            BookWritableTextBox writable = PageDrawing.focusedWritableTextBox.getRight();
            BookOfShadowsAltarTile altarTile = PageDrawing.focusedWritableTextBox.getLeft();
            writable.client.pageEdit.insertText(Character.toString(codePoint));
            writable.client.clearDisplayCache(altarTile.currentBook.getUUID());
        }
    }

    @ModifyVariable(method = "keyPress", at = @At("STORE"), ordinal = 1)
    private boolean modifyFlag4(boolean original) {
        if (PageDrawing.focusedWritableTextBox != null)
            return false;
        return original;
    }
    @Inject(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;onKeyInput(IIII)V"), cancellable = true) private void beforeClientHooksOnKeyInput(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (PageDrawing.focusedWritableTextBox != null) {
            BookWritableTextBox writable = PageDrawing.focusedWritableTextBox.getRight();
            BookOfShadowsAltarTile altarTile = PageDrawing.focusedWritableTextBox.getLeft();
            if (action == 1 || action == 2) {
                if (writable.client.keyPressed(key))
                    writable.client.clearDisplayCache(altarTile.currentBook.getUUID());

            }
            ci.cancel();
        }
    }
}