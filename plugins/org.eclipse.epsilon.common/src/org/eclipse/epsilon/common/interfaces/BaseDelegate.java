package org.eclipse.epsilon.common.interfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import org.eclipse.epsilon.common.util.CollectionUtil;

/**
 * Convenience interface for classes which may have a parent ("base")
 * in order to delegate failed operations to (for example, variable lookups).
 * Please note that such a strategy requires synchronisation or immutability
 * in concurrent use cases. For example, if a collection in the parent is
 * modified when a lookup is being delegated by a child, this can lead to
 * {@link java.util.ConcurrentModificationException}. To mitigate this,
 * a synchronized version of delegate methods are provided.
 * 
 * @author Sina Madani
 */
@SuppressWarnings("unchecked")
public interface BaseDelegate<T extends BaseDelegate<T>> {
	
	static enum MergeMode {
		MERGE_INTO_BASE,
		INHERIT_FROM_BASE,
		//COPY_BOTH
	}

	T getBase();
	
	void merge(MergeMode mode);
	
	default T getFrom(MergeMode mode) {
		return mode.equals(MergeMode.MERGE_INTO_BASE) ? (T) this : getBase();
	}
	
	default T getTo(MergeMode mode) {
		return mode.equals(MergeMode.INHERIT_FROM_BASE) ? (T) this : getBase();
	}
	
	default <C> void mergeCollectionsUnique(Function<T, Collection<C>> colPropertyGetter, Function<Collection<C>, ? extends Collection<C>> targetCol, MergeMode mode) {
		T base = getBase();
		if (base != null) {
			Collection<C>
				from = colPropertyGetter.apply(getFrom(mode)),
				to = colPropertyGetter.apply(getTo(mode));
			
			to = targetCol.apply(CollectionUtil.mergeCollectionsUnique(from, to, ArrayList::new));
		}
	}

	default <R> R delegateLookup(Function<T, R> method) {
		R value = method.apply((T)this);
		if (value != null)
			return value;
		
		T base = getBase();
		if (base != null)
			value = method.apply(base);
		
		return value;
	}
	
	@SafeVarargs
	static <T, R> R delegateLookup(Function<T, R> method, T... instances) {
		for (T instance : instances) {
			if (instance == null) continue;
			R current = method.apply(instance);
			if (current != null)
				return current;
		}
		return null;
	}
	
	//Concurrency support utils
	
	default <R> R delegateLookup(Function<T, R> method, boolean syncOnBase) {
		return delegateLookup(method, syncOnBase ? t -> t : null);
	}
	
	default <R> R delegateLookup(Function<T, R> method, Function<T, ? extends Object> lockProperty) {
		T base = getBase();
		return delegateLookup(method, (T)this, base, lockProperty != null && base != null ? lockProperty.apply(base) : null);
	}
	
	static <T, R> R delegateLookup(Function<T, R> method, T thisObj, T baseObj, Object lock) {
		R current = method.apply(thisObj);
		if (current != null)
			return current;
		
		else if (baseObj != null) {
			if (lock != null) {
				synchronized(lock) {
					return method.apply(baseObj);
				}
			}
			return method.apply(baseObj);
		}
		
		return null;
	}
}