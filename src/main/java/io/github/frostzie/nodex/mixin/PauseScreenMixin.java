package io.github.frostzie.nodex.mixin;

import io.github.frostzie.nodex.bootstrap.UiBootstrap;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin() {
        super(null);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        //TODO: Re-add Config
        PauseScreen self = (PauseScreen) (Object) this;

        Button button = Button.builder(Component.literal("IDE"), b ->
                        UiBootstrap.INSTANCE.showAndFocusWindow())
                .bounds(self.width / 2 + 72, self.height / 4 - 16, 30, 20).build();

        this.addRenderableWidget(button);
    }
}