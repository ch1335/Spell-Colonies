package com.chen1335.spellColonies.mixins;

import com.chen1335.spellColonies.common.events.DefineSpellTypeEvent;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;

public interface IAbstractSpellMixin {
    void sc$defineType(DefineSpellTypeEvent.SpellType spellType);

    DefineSpellTypeEvent.SpellType sc$getType();

    default boolean sc$isType(DefineSpellTypeEvent.SpellType spellType) {
        return sc$getType() == spellType;
    }

    static IAbstractSpellMixin cast(AbstractSpell abstractSpell) {
        return (IAbstractSpellMixin) abstractSpell;
    }
}
