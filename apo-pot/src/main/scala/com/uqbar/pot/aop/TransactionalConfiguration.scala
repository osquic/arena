package com.uqbar.pot.aop

import com.uqbar.apo.pointcut.AnnotationPointCut
import com.uqbar.apo.pointcut.FieldPointCut
import javassist.CtClass
import com.uqbar.apo.pointcut.PointCut
import com.uqbar.apo.Advice
import com.uqbar.apo.Configuration
import org.uqbar.commons.utils.Transactional
import com.uqbar.aop.transaction.ObjectTransactionManager
import com.uqbar.apo.pointcut.MethodPointCut
import com.uqbar.apo.pointcut.ClassPointCut
import com.uqbar.apo.APODSL._

/**
 *
 * @author nny
 */
trait TransactionalConfiguration extends Configuration {

  val transactionInterceptor = new TransactionFieldInterceptor()

  override def createAdvices(): List[Advice] = {
    super.createAdvices().::(new Advice(new PointCut with AnnotationPointCut with FieldPointCut {
      hasAnnotation(classOf[Transactional].getName())
    })
      .addInterceptor(transactionInterceptor))
  }

}

class TransactionalConfigurationImpl extends TransactionalConfiguration
