package net.dries007.tfc.util.registry;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * An abstraction around an underlying {@link RegistryHolder} which allows implementing lightweight, type-specific holder
 * objects, while avoiding having to type the double generic everywhere. This also provides convenience for type-specific
 * holders that use an underlying {@code Type<T>} object, for example:
 * {@snippet :
 * record Id<T extends BlockEntity>(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder)
 *     implements RegistryHolder<BlockEntityType<?>, BlockEntityType<T>> {}
 * }
 * This then makes it easy to register objects with minimal type declarations, while still having access to both the specific type,
 * and the underlying holder (and thus registry type).
 *
 * @param <R> The type of the registry, also used to retrieve the {@link net.minecraft.core.Holder<R>}
 * @param <T> The type of the object, also used to retrieve the {@link java.util.function.Supplier<T>}
 */
public interface RegistryHolder<R, T extends R> extends IdHolder<T>
{
    /**
     * Typically provided as a {@code DeferredHolder} field on a record.
     */
    @Override
    DeferredHolder<R, T> holder();

    @Override
    default T get()
    {
        return holder().get();
    }

    default ResourceLocation getId()
    {
        return holder().getId();
    }
}
