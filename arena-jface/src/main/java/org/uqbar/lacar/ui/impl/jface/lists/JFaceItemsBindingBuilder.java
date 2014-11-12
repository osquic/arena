package org.uqbar.lacar.ui.impl.jface.lists;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.uqbar.arena.bindings.Transformer;
import org.uqbar.lacar.ui.impl.jface.bindings.JFaceObservableFactory;
import org.uqbar.lacar.ui.model.BindingBuilder;

/**
 * Base class for creating bindings for the items of a structured viewer.
 * 
 * @author npasserini
 */
public class JFaceItemsBindingBuilder implements BindingBuilder {
	private StructuredViewer viewer;
	private IObservableList itemsObservableList;

	public JFaceItemsBindingBuilder(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	// ********************************************************
	// ** Configuration
	// ********************************************************

	/**
	 * This operation is currently not supported for this kind of binding.
	 */
	@Override
	public <M, V> BindingBuilder adaptWith(Transformer<M, V> transformer) {
		throw new UnsupportedOperationException("Applying transformers to item bindings is currently not supported.");
	}
	
	@Override
	public <M, V> BindingBuilder modelToView(org.apache.commons.collections15.Transformer<M, V> transformer) {
		throw new UnsupportedOperationException("Applying transformers to item bindings is currently not supported.");
	}
	
	@Override
	public <M, V> BindingBuilder viewToModel(org.apache.commons.collections15.Transformer<V, M> transformer) {
		throw new UnsupportedOperationException("Applying transformers to item bindings is currently not supported.");
	}

	@Override
	public void observeProperty(Object modelObject, String propertyName) {
		this.itemsObservableList = JFaceObservableFactory.observeList(modelObject, propertyName);
		this.viewer.setContentProvider(new ObservableListContentProvider());
	}

	@Override
	public void observeErrors() {
		throw new UnsupportedOperationException("Binding errors to item bindings is not implemented.");
	}

	// ********************************************************
	// ** Building
	// ********************************************************

	@Override
	public void build() {
		this.viewer.setInput(this.itemsObservableList);
	}

	// ********************************************************
	// ** Accessors
	// ********************************************************

	protected StructuredViewer getViewer() {
		return viewer;
	}
}