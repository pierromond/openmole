package org.openmole.plugin.domain

import org.openmole.core.expansion._
import org.openmole.core.workflow.domain._
import org.openmole.tool.types._
import cats.implicits._

package object bounds {

  implicit def tupleOfStringToBounds[T: FromString: Manifest]: BoundedFromContextDomain[(String, String), T] = domain ⇒ {
    val minCode = FromContext.codeToFromContext[T](domain._1)
    val maxCode = FromContext.codeToFromContext[T](domain._2)
    Domain((minCode, maxCode))
  }

  implicit def tupleIsBounds[T]: BoundedDomain[(T, T), T] = domain ⇒ Domain(domain)
  implicit def iterableOfTuplesIsBounds[T: Manifest]: BoundedDomain[Iterable[(T, T)], Array[T]] = domain ⇒ Domain((domain.unzip._1.toArray, domain.unzip._2.toArray))
  implicit def arrayOfTuplesIsBounds[T: Manifest]: BoundedDomain[Array[(T, T)], Array[T]] = domain ⇒ Domain((domain.unzip._1, domain.unzip._2))

  implicit def iterableOfStringTuplesIsBounds[T: FromString: Manifest]: BoundedFromContextDomain[Iterable[(String, String)], Array[T]] = domain ⇒ {
    def min = domain.unzip._1.toVector.traverse(v ⇒ FromContext.codeToFromContext[T](v): FromContext[T]).map(_.toArray)
    def max = domain.unzip._2.toVector.traverse(v ⇒ FromContext.codeToFromContext[T](v): FromContext[T]).map(_.toArray)
    Domain((min, max))
  }

  implicit def arrayOfStringTuplesIsBounds[T: FromString: Manifest]: BoundedFromContextDomain[Array[(String, String)], Array[T]] = domain ⇒ {
    def min = domain.unzip._1.toVector.traverse(v ⇒ FromContext.codeToFromContext[T](v): FromContext[T]).map(_.toArray)
    def max = domain.unzip._2.toVector.traverse(v ⇒ FromContext.codeToFromContext[T](v): FromContext[T]).map(_.toArray)
    Domain((min, max))
  }

}
