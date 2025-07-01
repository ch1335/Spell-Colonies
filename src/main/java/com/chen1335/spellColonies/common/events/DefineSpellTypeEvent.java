package com.chen1335.spellColonies.common.events;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

/**
 * 用于定义一个法术的类型（伤害，辅助，治疗）
 */
public class DefineSpellTypeEvent extends Event implements IModBusEvent {

    private final AbstractSpell spell;
    private final ResourceLocation spellId;

    public DefineSpellTypeEvent(AbstractSpell spell, ResourceLocation spellId) {
        this.spell = spell;
        this.spellId = spellId;
    }

    public ResourceLocation getSpellId() {
        return spellId;
    }

    public AbstractSpell getSpell() {
        return spell;
    }


    public enum SpellType {
        DAMAGE,
        ASSISTANT,
        HEAL_SELF,
        HEAL_OTHER
    }
}
