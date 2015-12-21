package org.uqbar.lacar.ui.impl.jface.bindings

import java.beans.PropertyDescriptor
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory
import org.eclipse.core.databinding.observable.value.AbstractObservableValue
import org.eclipse.core.databinding.observable.value.IObservableValue
import org.eclipse.core.databinding.observable.value.ValueDiff
import org.eclipse.core.databinding.observable.Realm
import org.eclipse.core.internal.databinding.beans.JavaBeanObservableValue
import org.eclipse.core.internal.databinding.observable.masterdetail.DetailObservableValue
import org.uqbar.arena.isolation.IsolationLevelEvents
import org.uqbar.aop.transaction.ObjectTransactionManager
import org.uqbar.apo.APOConfig

trait ObservableEvents {
  val isolationKey = "apo.poo.isolationLevel";
  var objectTransactionImpl = ObjectTransactionManager.getTransaction()
  val isolationLevelEvents = IsolationLevelEvents.valueOf(APOConfig.getProperty(isolationKey).value)

}

trait TransactionalObservableValue extends AbstractObservableValue with ObservableEvents {
  override def fireValueChange(diff: ValueDiff) {
    if (this.isolationLevelEvents.check(this.objectTransactionImpl)) {
      super.fireValueChange(diff);
    }
  }
}


class DetailTransacionalObservableValue(outerObservableValue: IObservableValue, factory: IObservableFactory, detailType: Any)
  extends DetailObservableValue(outerObservableValue, factory, detailType) with TransactionalObservableValue

class JavaBeanTransacionalObservableValue(realm: Realm, any: Any, descriptor: PropertyDescriptor)
  extends JavaBeanObservableValue(realm, any, descriptor) with TransactionalObservableValue

