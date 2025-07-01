package com.chen1335.spellColonies.mixins.main;

import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.core.client.render.CitizenArmorLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.util.InternalUtil;

@Mixin(CitizenArmorLayer.class)
public abstract class CitizenArmorLayerMixin<T extends AbstractEntityCitizen, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends HumanoidArmorLayer<T, M, A> {


    @Shadow
    protected abstract void renderArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, T citizen, EquipmentSlot equipmentSlot, int light, A armor, ICitizenDataView citizenDataView);

    public CitizenArmorLayerMixin(RenderLayerParent<T, M> renderer, A innerModel, A outerModel, ModelManager modelManager) {
        super(renderer, innerModel, outerModel, modelManager);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILcom/minecolonies/api/entity/citizen/AbstractEntityCitizen;FFFFFF)V", at = @At(value = "INVOKE", target = "Lcom/minecolonies/core/client/render/CitizenArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/minecolonies/api/entity/citizen/AbstractEntityCitizen;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;Lcom/minecolonies/api/colony/ICitizenDataView;)V", ordinal = 0), cancellable = true)
    private void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int light, @NotNull T citizen, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headRotY, float headRotX, CallbackInfo ci) {
        this.sc$renderArmor(poseStack, bufferSource, light, citizen, limbSwing, limbSwingAmount, partialTicks, ageInTicks, headRotY, headRotX, EquipmentSlot.CHEST);
        this.sc$renderArmor(poseStack, bufferSource, light, citizen, limbSwing, limbSwingAmount, partialTicks, ageInTicks, headRotY, headRotX, EquipmentSlot.LEGS);
        this.sc$renderArmor(poseStack, bufferSource, light, citizen, limbSwing, limbSwingAmount, partialTicks, ageInTicks, headRotY, headRotX, EquipmentSlot.FEET);
        this.sc$renderArmor(poseStack, bufferSource, light, citizen, limbSwing, limbSwingAmount, partialTicks, ageInTicks, headRotY, headRotX, EquipmentSlot.HEAD);
        ci.cancel();
    }

    private void sc$renderArmor(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int light, @NotNull T citizen, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headRotY, float headRotX, EquipmentSlot slot) {
        if (!InternalUtil.tryRenderGeoArmorPiece(poseStack, bufferSource, citizen, citizen.getItemBySlot(slot), slot, this.getParentModel(), this.getArmorModel(slot), partialTicks, light, limbSwing, limbSwingAmount, ageInTicks, headRotY, headRotX, this::setPartVisibility)) {
            this.renderArmorPiece(poseStack, bufferSource, citizen, slot, light, this.getArmorModel(slot), citizen.getCitizenDataView());
        }
    }
}
