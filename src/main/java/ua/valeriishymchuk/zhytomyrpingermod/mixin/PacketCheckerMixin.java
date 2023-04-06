package ua.valeriishymchuk.zhytomyrpingermod.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ua.valeriishymchuk.zhytomyrpingermod.channel.DelayChannel;

@Mixin(ClientConnection.class)
public class PacketCheckerMixin {

    @Inject(method = "channelActive", at = @At("HEAD"))
    private void onActive(ChannelHandlerContext context, CallbackInfo ci) {
        context.pipeline().addFirst(new DelayChannel());
    }

}
