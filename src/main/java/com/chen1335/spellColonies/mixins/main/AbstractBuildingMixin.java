package com.chen1335.spellColonies.mixins.main;

import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import io.redspace.ironsspellbooks.api.item.ISpellbook;
import io.redspace.ironsspellbooks.item.Scroll;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = AbstractBuilding.class,remap = false)
public class AbstractBuildingMixin {
    @Inject(method = "buildingRequiresCertainAmountOfItem", at = @At("RETURN"), cancellable = true)
    private void buildingRequiresCertainAmountOfItem(ItemStack stack, List<ItemStorage> localAlreadyKept, boolean inventory, JobEntry jobEntry, CallbackInfoReturnable<Integer> cir) {
        if (stack.getItem() instanceof Scroll || stack.getItem() instanceof ISpellbook) {
            cir.setReturnValue(0);
        }
    }
}
