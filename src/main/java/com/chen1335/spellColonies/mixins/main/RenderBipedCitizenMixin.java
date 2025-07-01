package com.chen1335.spellColonies.mixins.main;

import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.core.client.render.RenderBipedCitizen;
import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBipedCitizen.class)
public class RenderBipedCitizenMixin {
    @Inject(method = "render(Lcom/minecolonies/api/entity/citizen/AbstractEntityCitizen;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("RETURN"))
    private void render(AbstractEntityCitizen citizen, float limbSwing, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, CallbackInfo ci) {
        SpellRenderingHelper.renderSpellHelper(ClientMagicData.getSyncedSpellData(citizen), citizen, poseStack, bufferSource, partialTick);

    }
}
