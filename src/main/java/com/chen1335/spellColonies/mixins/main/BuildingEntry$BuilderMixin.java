package com.chen1335.spellColonies.mixins.main;

import com.chen1335.spellColonies.buildings.modules.SCBuildingModules;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = BuildingEntry.Builder.class,remap = false)
public class BuildingEntry$BuilderMixin {
    @Shadow
    private ResourceLocation registryName;

    @Shadow
    private List<BuildingEntry.ModuleProducer<?, ?>> buildingModuleProducers;

    @Inject(method = "createBuildingEntry", at = @At("HEAD"))
    private void onCreateBuildingEntry(CallbackInfoReturnable<BuildingEntry> cir) {
        if (
                registryName.equals(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, ModBuildings.GUARD_TOWER_ID)) ||
                        registryName.equals(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, ModBuildings.BARRACKS_TOWER_ID))
        ) {
            buildingModuleProducers.add(SCBuildingModules.MAGICIAN_TOWER_WORK);
        }
    }
}
