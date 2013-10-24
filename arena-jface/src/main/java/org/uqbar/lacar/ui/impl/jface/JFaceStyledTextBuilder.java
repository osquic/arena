package org.uqbar.lacar.ui.impl.jface;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.uqbar.arena.widgets.style.Style;
import org.uqbar.lacar.ui.impl.jface.bindings.JFaceBindingBuilder;
import org.uqbar.lacar.ui.model.BindingBuilder;
import org.uqbar.ui.jface.controller.KeyWordText;

/**
 * 
 * @author nnydjesus
 */
public class JFaceStyledTextBuilder extends JFaceSkinnableControlBuilder<KeyWordText> {

	public JFaceStyledTextBuilder(JFaceContainer container, Map<String[], Style> configuration) {
		super(container, new KeyWordText(container.getJFaceComposite(), configuration));
	}

	@Override
	public BindingBuilder observeValue() {
		return new JFaceBindingBuilder(this, new KeyWordTextObservableValue(this.getWidget(), SWT.Modify));
	}
}
