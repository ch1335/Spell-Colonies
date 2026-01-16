package com.chen1335.spellColonies.ai.workers.guard;

import com.chen1335.spellColonies.ai.jobs.JobMagician;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.core.colony.buildings.AbstractBuildingGuards;
import com.minecolonies.core.entity.ai.workers.guard.AbstractEntityAIGuard;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import io.redspace.ironsspellbooks.item.Scroll;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntityAIMagician extends AbstractEntityAIGuard<JobMagician, AbstractBuildingGuards> {
    public MagicianCombatAI magicianCombatAI;

    public EntityAIMagician(@NotNull JobMagician job) {
        super(job);
        magicianCombatAI = new MagicianCombatAI((EntityCitizen) worker, getStateAI());

    }

    public void setAttackTarget(LivingEntity livingEntity) {
        magicianCombatAI.setAttackTarget(livingEntity);
    }

    @Override
    public void tick() {
        if (magicianCombatAI.getCurrentTarget() != worker.getTarget()) {
            worker.setTarget(magicianCombatAI.getCurrentTarget());
        }
        super.tick();
    }

    @Override
    protected @NotNull List<ItemStorage> itemsNiceToHave() {
        List<ItemStorage> itemStorages = new ArrayList<>();
        for (int i = 0; i < this.getInventory().getSlots(); i++) {
            ItemStack itemStack = this.getInventory().getStackInSlot(i);
            if (itemStack.getItem() instanceof Scroll) {
                itemStorages.add(new ItemStorage(itemStack, itemStack.getCount(), true));
            }
        }
        return itemStorages;
    }
}
