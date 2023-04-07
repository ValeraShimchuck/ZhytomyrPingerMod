package ua.valeriishymchuk.zhytomyrpingermod.mixin;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ua.valeriishymchuk.zhytomyrpingermod.channel.DelayChannel;
import ua.valeriishymchuk.zhytomyrpingermod.widget.ValueOptionSlider;

import java.lang.reflect.Method;

@Mixin(MultiplayerScreen.class)
public class MPGuiMixin {

    private static final int MIN = 0;
    private static final int MAX = 1000;

    @Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        addDrawableChild0(new ValueOptionSlider(
                50,
                10,
                100,
                20,
                Text.of("ping"),
                getRawValueFromPing(DelayChannel.getDelay()),
                value -> Text.of("ping: " + (getPingFromRawValue(value))),
                value -> DelayChannel.setDelay(getPingFromRawValue(value) / 2)));
    }

    private int getPingFromRawValue(double value) {
        return (int) (((MAX - MIN) * value) + MIN);
    }

    private double getRawValueFromPing(int value) {
        return ((double) value - MIN) / (MAX - MIN);
    }

    private MultiplayerScreen getYourself() {
        return (MultiplayerScreen) (Object) this;
    }

    @SuppressWarnings("unchecked")
    private <T extends Element & Drawable & Selectable> T addDrawableChild0(T drawableElement) {
        try {
            Class<Screen> screenClass = Screen.class;
            Method addDrawableChildMethod = screenClass.getDeclaredMethod("method_37063", Element.class);
            addDrawableChildMethod.setAccessible(true);
            return (T) addDrawableChildMethod.invoke(this, drawableElement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
