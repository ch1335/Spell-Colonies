package com.chen1335.spellColonies.mixins.main;

import com.minecolonies.api.client.render.modeltype.CitizenModel;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CitizenModel.class)
public abstract class CitizenModelMixin extends HumanoidModel<AbstractEntityCitizen> {
    @Unique
    private final SetableSupplier<AnimationProcessor> emoteSupplier = new SetableSupplier<>();

    public CitizenModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setupAnim(Lcom/minecolonies/api/entity/citizen/AbstractEntityCitizen;FFFFF)V", at = @At(value = "HEAD"))
    private void setDefaultBeforeRender(AbstractEntityCitizen citizen, float f1, float f2, float f3, float f4, float f5, CallbackInfo ci) {
        setDefaultPivot();
    }

    @Inject(method = "setupAnim(Lcom/minecolonies/api/entity/citizen/AbstractEntityCitizen;FFFFF)V", at = @At("RETURN"))
    private void updateAnim(AbstractEntityCitizen citizen, float f1, float f2, float f3, float f4, float f5, CallbackInfo ci) {
        IAnimatedPlayer animatedPlayer = (IAnimatedPlayer) citizen;
        AnimationApplier animationApplier = animatedPlayer.playerAnimator_getAnimation();

        if (animationApplier.isActive()) {
            float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
            animationApplier.setTickDelta(partialTick);
            emoteSupplier.set(animationApplier);
            animationApplier.updatePart("head", this.head);
            this.hat.copyFrom(this.head);
            animationApplier.updatePart("leftArm", this.leftArm);
            animationApplier.updatePart("rightArm", this.rightArm);
            animationApplier.updatePart("leftLeg", this.leftLeg);
            animationApplier.updatePart("rightLeg", this.rightLeg);
            animationApplier.updatePart("torso", this.body);
        }
    }

    @Unique
    private void setDefaultPivot() {
        this.leftLeg.setPos(1.9F, 12.0F, 0.0F);
        this.rightLeg.setPos(-1.9F, 12.0F, 0.0F);
        this.head.setPos(0.0F, 0.0F, 0.0F);
        this.rightArm.z = 0.0F;
        this.rightArm.x = -5.0F;
        this.leftArm.z = 0.0F;
        this.leftArm.x = 5.0F;
        this.body.xRot = 0.0F;
        this.rightLeg.z = 0.1F;
        this.leftLeg.z = 0.1F;
        this.rightLeg.y = 12.0F;
        this.leftLeg.y = 12.0F;
        this.head.y = 0.0F;
        this.head.zRot = 0f;
        this.body.y = 0.0F;
        this.body.x = 0f;
        this.body.z = 0f;
        this.body.yRot = 0;
        this.body.zRot = 0;

        this.head.xScale = ModelPart.DEFAULT_SCALE;
        this.head.yScale = ModelPart.DEFAULT_SCALE;
        this.head.zScale = ModelPart.DEFAULT_SCALE;
        this.body.xScale = ModelPart.DEFAULT_SCALE;
        this.body.yScale = ModelPart.DEFAULT_SCALE;
        this.body.zScale = ModelPart.DEFAULT_SCALE;
        this.rightArm.xScale = ModelPart.DEFAULT_SCALE;
        this.rightArm.yScale = ModelPart.DEFAULT_SCALE;
        this.rightArm.zScale = ModelPart.DEFAULT_SCALE;
        this.leftArm.xScale = ModelPart.DEFAULT_SCALE;
        this.leftArm.yScale = ModelPart.DEFAULT_SCALE;
        this.leftArm.zScale = ModelPart.DEFAULT_SCALE;
        this.rightLeg.xScale = ModelPart.DEFAULT_SCALE;
        this.rightLeg.yScale = ModelPart.DEFAULT_SCALE;
        this.rightLeg.zScale = ModelPart.DEFAULT_SCALE;
        this.leftLeg.xScale = ModelPart.DEFAULT_SCALE;
        this.leftLeg.yScale = ModelPart.DEFAULT_SCALE;
        this.leftLeg.zScale = ModelPart.DEFAULT_SCALE;
    }
}
