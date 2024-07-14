package com.github.sidit77.bettermipmaps.mixin;

import com.github.sidit77.bettermipmaps.BetterMipmaps;
import com.mojang.serialization.Codec;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(OptionInstance.class)
public class OptionInstanceMixin {


    @SuppressWarnings("rawtypes")
    @Mutable
    @Shadow @Final private OptionInstance.ValueSet values;

    @SuppressWarnings("rawtypes")
    @Mutable
    @Shadow @Final private Codec codec;

    @SuppressWarnings("rawtypes")
    @Inject(
        method = "<init>(Ljava/lang/String;Lnet/minecraft/client/OptionInstance$TooltipSupplier;Lnet/minecraft/client/OptionInstance$CaptionBasedToString;Lnet/minecraft/client/OptionInstance$ValueSet;Lcom/mojang/serialization/Codec;Ljava/lang/Object;Ljava/util/function/Consumer;)V",
        at = @At("RETURN")
    )
    public void print_key(
            String string,
            OptionInstance.TooltipSupplier tooltipSupplier,
            OptionInstance.CaptionBasedToString captionBasedToString,
            OptionInstance.ValueSet valueSet,
            Codec codec,
            Object object,
            Consumer consumer,
            CallbackInfo ci)
    {
        if(string.equals("options.mipmapLevels")) {
            this.values = new OptionInstance.IntRange(0, BetterMipmaps.MAX_MIPMAP_LEVEL);
            this.codec = this.values.codec();
        }

    }

}
