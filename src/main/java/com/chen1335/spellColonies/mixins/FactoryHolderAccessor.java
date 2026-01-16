package com.chen1335.spellColonies.mixins;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public interface FactoryHolderAccessor {
    List<Function<Objects, Objects>> getFactories();
}
