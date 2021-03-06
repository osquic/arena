package org.uqbar.lacar.ui.model;

import org.uqbar.arena.bindings.ValueTransformer;

/**
 * Colabora en la construcción de un binding.
 * 
 * @author npasserini
 */
public interface BindingBuilder {

	// ********************************************************
	// ** Configuracion
	// ********************************************************

	/**
	 * Observes a property of a known object.
	 * 
	 * @param model The owner of the property to be observed.
	 * @param propertyName The name of the property to observe.
	 */
	public void observeProperty(Object model, String propertyName);

	/**
	 * Observers the container, analysing if any control in this container has an error.
	 */
	public void observeErrors();
	
	/**
	 * Sets an adapter strategy based on a {@link ValueTransformer}. As the transformer is bidirectional, it can
	 * transform the values from the model to the value needed in the view and viceversa.
	 * 
	 * THIS IS A SHORTCUT METHOD WHEN YOU NEED TO TRANSFORM IN BOTH WAYS.
	 * OTHERWISE USE {@link #modelToView(org.apache.commons.collections15.Transformer)} and
	 * {@link #viewToModel(org.apache.commons.collections15.Transformer)}
	 * 
	 * @param transformer The strategy
	 * @return this.
	 */
	public <M,V>BindingBuilder adaptWith(ValueTransformer<M, V> transformer);
	
	/**
	 * Configures the given "one-way" transformer to transform values coming from the VIEW
	 * that will be set into the model.
	 * @return the builder
	 */
	public <M,V>BindingBuilder viewToModel(org.apache.commons.collections15.Transformer<V, M> transformer);
	
	/**
	 * Configures the given "one-way" transformer to transform values coming from the MODEL
	 * that will be set into the VIEW.
	 * @return the builder
	 */
	public <M,V>BindingBuilder modelToView(org.apache.commons.collections15.Transformer<M, V> transformer);

	// ********************************************************
	// ** Build
	// ********************************************************

	/**
	 * Indicates that configuration of this builder has ended and gives place to the work it needs to do to
	 * finalize de construction.
	 */
	public void build();

}
