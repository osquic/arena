package org.uqbar.lacar.ui.impl.jface.bindings;

import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.databinding.BindingException;
import org.eclipse.core.databinding.beans.IBeanObservable;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.ObservableSet;
import org.eclipse.core.internal.databinding.beans.ListenerSupport;
import org.eclipse.core.runtime.Assert;
import org.uqbar.arena.ArenaException;
import org.uqbar.lacar.ui.model.bindings.collections.ObservableContainer;

/**
 * 
 * COPIADA DE JFACE JavaBeansObservableSet
 * Necesitamos interceptar el acceso a la property para convertir entre
 * colecciones de scala y java, pero los malditos hicieron todo private.
 * 
 * Ademas le agregamos soporte para {@link ObservableContainer}
 * 
 * @author jfernandes
 */
public class ArenaJavaBeanObservableSet extends ObservableSet implements IBeanObservable {
	// agregado
	private ScalaJavaConverter converter = new ScalaJavaListConverter();
	private final Object object;
	private boolean updating = false;
	private PropertyDescriptor descriptor;
	private ListenerSupport listenerSupport;

	public ArenaJavaBeanObservableSet(Realm realm, Object object,
			PropertyDescriptor descriptor, Class elementType) {
		this(realm, object, descriptor, elementType, true);
	}

	public ArenaJavaBeanObservableSet(Realm realm, Object object,
			PropertyDescriptor descriptor, Class elementType,
			boolean attachListeners) {
		super(realm, new HashSet(), elementType);
		this.object = object;
		this.descriptor = descriptor;
		if (attachListeners) {
			PropertyChangeListener listener = new PropertyChangeListener() {
				public void propertyChange(java.beans.PropertyChangeEvent event) {
					if (!updating) {
						getRealm().exec(new Runnable() {
							public void run() {
								Set newElements = new HashSet(Arrays.asList(getValues()));
								Set addedElements = new HashSet(newElements);
								Set removedElements = new HashSet(wrappedSet);
								// remove all new elements from old elements to
								// compute
								// the removed elements
								removedElements.removeAll(newElements);
								addedElements.removeAll(wrappedSet);
								wrappedSet = newElements;
								fireSetChange(Diffs.createSetDiff(addedElements, removedElements));
							}
						});
					}
				}
			};
			this.listenerSupport = new ListenerSupport(listener, descriptor.getName());
			listenerSupport.hookListener(this.object);
		}

		wrappedSet.addAll(Arrays.asList(getValues()));
	}

	private Object primGetValues() {
		try {
			Method readMethod = descriptor.getReadMethod();
			if (!readMethod.isAccessible()) {
				readMethod.setAccessible(true);
			}
			// agregado
			return converter.convertScalaCollectionToJavaIfNeeded(readMethod.invoke(object, new Object[0]));
		} catch (IllegalArgumentException e) {
			throw new ArenaException("Error while accessing property '" + descriptor + "'", e);
		} catch (IllegalAccessException e) {
			throw new ArenaException("Error while accessing property '" + descriptor + "'", e);
		} catch (InvocationTargetException e) {
			throw new ArenaException("Error while accessing property '" + descriptor + "'", e);
		}
	}

	private Object[] getValues() {
		Object[] values = null;

		Object result = primGetValues();
		if (descriptor.getPropertyType().isArray())
			values = (Object[]) result;
		else {
			// TODO add jUnit for POJO (var. SettableValue) collections
			Collection list = (Collection) result;
			if (list != null)
				values = list.toArray();
		}
		if (values == null)
			values = new Object[0];
		return values;
	}

	private void setValues() {
		if (descriptor.getPropertyType().isArray()) {
			Class componentType = descriptor.getPropertyType()
					.getComponentType();
			Object[] newArray = (Object[]) Array.newInstance(componentType,
					wrappedSet.size());
			wrappedSet.toArray(newArray);
			primSetValues(newArray);
		} else {
			// assume that it is a java.util.Set
			primSetValues(new HashSet(wrappedSet));
		}
	}

	public boolean add(Object o) {
		getterCalled();
		updating = true;
		try {
			boolean added = wrappedSet.add(o);
			if (added) {
				setValues();
				fireSetChange(Diffs.createSetDiff(Collections.singleton(o),
						Collections.EMPTY_SET));
			}
			return added;
		} finally {
			updating = false;
		}
	}

	public boolean remove(Object o) {
		getterCalled();
		updating = true;
		try {
			boolean removed = wrappedSet.remove(o);
			if (removed) {
				setValues();
				fireSetChange(Diffs.createSetDiff(Collections.EMPTY_SET,
						Collections.singleton(o)));
			}
			return removed;
		} finally {
			updating = false;
		}
	}

	public boolean addAll(Collection c) {
		getterCalled();
		updating = true;
		try {
			Set additions = new HashSet();
			for (Iterator iterator = c.iterator(); iterator.hasNext();) {
				Object element = iterator.next();
				if (wrappedSet.add(element))
					additions.add(element);
			}
			boolean changed = !additions.isEmpty();
			if (changed) {
				setValues();
				fireSetChange(Diffs.createSetDiff(additions,
						Collections.EMPTY_SET));
			}
			return changed;
		} finally {
			updating = false;
		}
	}

	public boolean removeAll(Collection c) {
		getterCalled();
		updating = true;
		try {
			Set removals = new HashSet();
			for (Iterator iterator = c.iterator(); iterator.hasNext();) {
				Object element = iterator.next();
				if (wrappedSet.remove(element))
					removals.add(element);
			}
			boolean changed = !removals.isEmpty();
			if (changed) {
				setValues();
				fireSetChange(Diffs.createSetDiff(Collections.EMPTY_SET,
						removals));
			}
			return changed;
		} finally {
			updating = false;
		}
	}

	public boolean retainAll(Collection c) {
		getterCalled();
		updating = true;
		try {
			Set removals = new HashSet();
			for (Iterator iterator = wrappedSet.iterator(); iterator.hasNext();) {
				Object element = iterator.next();
				if (!c.contains(element)) {
					iterator.remove();
					removals.add(element);
				}
			}
			boolean changed = !removals.isEmpty();
			if (changed) {
				setValues();
				fireSetChange(Diffs.createSetDiff(Collections.EMPTY_SET,
						removals));
			}
			return changed;
		} finally {
			updating = false;
		}
	}

	public void clear() {
		getterCalled();
		if (wrappedSet.isEmpty())
			return;

		updating = true;
		try {
			Set removals = new HashSet(wrappedSet);
			wrappedSet.clear();
			setValues();
			fireSetChange(Diffs.createSetDiff(Collections.EMPTY_SET, removals));
		} finally {
			updating = false;
		}
	}

	private void primSetValues(Object newValue) {
		Exception ex = null;
		try {
			Method writeMethod = descriptor.getWriteMethod();
			if (!writeMethod.isAccessible()) {
				writeMethod.setAccessible(true);
			}
			writeMethod.invoke(object, new Object[] { newValue });
			return;
		} catch (IllegalArgumentException e) {
			ex = e;
		} catch (IllegalAccessException e) {
			ex = e;
		} catch (InvocationTargetException e) {
			ex = e;
		}
		throw new BindingException("Could not write collection values", ex); //$NON-NLS-1$
	}

	public Object getObserved() {
		return object;
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return descriptor;
	}

	public synchronized void dispose() {
		if (listenerSupport != null) {
			listenerSupport.dispose();
			listenerSupport = null;
		}

		super.dispose();
	}
}
