package com.chen1335.spellColonies.mixins.main;

import com.chen1335.spellColonies.client.animation.CitizenAnimationFactory;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.registries.DataAttachmentRegistry;
import io.redspace.ironsspellbooks.spells.fire.BurningDashSpell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(AbstractEntityCitizen.class)
public abstract class AbstractEntityCitizenMixin extends LivingEntity implements IAnimatedPlayer, IMagicEntity {
    @Unique
    private final Map<ResourceLocation, IAnimation> modAnimationData = new HashMap<>();
    @Unique
    private final AnimationStack animationStack = createAnimationStack();
    @Unique
    private final AnimationApplier animationApplier = new AnimationApplier(animationStack);

    protected AbstractEntityCitizenMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }


    @Unique
    private AnimationStack createAnimationStack() {
        AnimationStack stack = new AnimationStack();
        CitizenAnimationFactory.ANIMATION_DATA_FACTORY.prepareAnimations((AbstractEntityCitizen) (Object) this, stack, modAnimationData);
        return stack;
    }

    @Override
    public AnimationStack getAnimationStack() {
        return animationStack;
    }

    @Override
    public AnimationApplier playerAnimator_getAnimation() {
        return animationApplier;
    }

    @Override
    public @Nullable IAnimation playerAnimator_getAnimation(@NotNull ResourceLocation id) {
        return modAnimationData.get(id);
    }

    @Override
    public @Nullable IAnimation playerAnimator_setAnimation(@NotNull ResourceLocation id, @Nullable IAnimation animation) {
        if (animation == null) {
            return modAnimationData.remove(id);
        } else {
            return modAnimationData.put(id, animation);
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        animationStack.tick();
    }

    @Override
    public MagicData getMagicData() {
        if (!this.hasData(DataAttachmentRegistry.MAGIC_DATA.get())) {
            this.setData(DataAttachmentRegistry.MAGIC_DATA.get(), new MagicData(true));
        }
        return this.getData(DataAttachmentRegistry.MAGIC_DATA.get());
    }

    @Override
    public void setSyncedSpellData(SyncedSpellData syncedSpellData) {
        if (!level().isClientSide) {
            return;
        }

        getMagicData().setSyncedData(syncedSpellData);
    }

    @Override
    public boolean isCasting() {
        return getMagicData().isCasting();
    }

    @Override
    public void initiateCastSpell(AbstractSpell spell, int spellLevel) {

    }

    @Override
    public void cancelCast() {

    }

    @Override
    public void castComplete() {

    }

    @Override
    public void notifyDangerousProjectile(Projectile projectile) {

    }

    @Override
    public boolean setTeleportLocationBehindTarget(int distance) {
        return false;
    }

    @Override
    public void setBurningDashDirectionData() {
        getMagicData().setAdditionalCastData(new BurningDashSpell.BurningDashDirectionOverrideCastData());
    }

    @Override
    public boolean isDrinkingPotion() {
        return false;
    }

    @Override
    public boolean getHasUsedSingleAttack() {
        return false;
    }

    @Override
    public void setHasUsedSingleAttack(boolean bool) {

    }

    @Override
    public void startDrinkingPotion() {

    }
}
