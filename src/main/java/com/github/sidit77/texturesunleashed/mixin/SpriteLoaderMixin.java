package com.github.sidit77.texturesunleashed.mixin;

import com.github.sidit77.texturesunleashed.TexturesUnleashed;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Mixin(SpriteLoader.class)
abstract class SpriteLoaderMixin {

    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();

    @Accessor
    abstract ResourceLocation getLocation();

    @ModifyArg(
        method = "method_47659",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/SpriteLoader;stitch(Ljava/util/List;ILjava/util/concurrent/Executor;)Lnet/minecraft/client/renderer/texture/SpriteLoader$Preparations;"),
        index = 0)
    private List<SpriteContents> upscaleSprites(List<SpriteContents> list, int level, Executor executor) {
        if(TexturesUnleashed.UPSCALE_WHITELIST.contains(getLocation())) {
            int targetLevel = Math.min(list
                            .stream()
                            .mapToInt(s -> Math.min(Integer.lowestOneBit(s.width()), Integer.lowestOneBit(s.height())))
                            .max()
                            .orElse(1),
                    1 << level);

            LOGGER.info("{}: Max Level {}", getLocation(), targetLevel);

            list = list.stream().map(s -> {
                int factor = 0;
                while (Math.max(s.width(), s.height()) << factor < TexturesUnleashed.MAX_UPSCALE_RESOLUTION &&
                        Math.min(Integer.lowestOneBit(s.width()), Integer.lowestOneBit(s.height())) << factor < targetLevel) {
                    factor++;
                }
                if(factor > 0) {
                    LOGGER.info("Upscaling {} ({}x{}) by a factor of {}", s.name(), s.width(), s.height(), factor);
                    s = upscale(s, factor);
                }
                return s;
            }).collect(Collectors.toList());
        }
        return list;
    }

    @Unique
    private SpriteContents upscale(SpriteContents original, int factor) {
        NativeImage in = original.originalImage;
        NativeImage out = new NativeImage(in.format(), in.getWidth() << factor, in.getHeight() << factor, false);
        for(int x = 0; x < in.getWidth(); x++) {
            for(int y = 0; y < in.getHeight(); y++) {
                out.fillRect(x << factor, y << factor, 1 << factor, 1 << factor, in.getPixelRGBA(x, y));
            }
        }
        return new SpriteContents(original.name(), new FrameSize(original.width() << factor, original.height() << factor), out, original.metadata());
    }

}
