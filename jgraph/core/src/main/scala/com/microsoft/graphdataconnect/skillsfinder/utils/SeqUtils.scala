package com.microsoft.graphdataconnect.skillsfinder.utils

import scala.collection.mutable

// TODO move to `common` module
object SeqUtils {

  implicit class SeqImplicits[T](val input: Seq[T]) {

    def nullToNil: Seq[T] = {
      if (input == null) Nil else input
    }

  }

  implicit class ListImplicits[T](val input: List[T]) {

    def nullToNil: List[T] = {
      if (input == null) Nil else input
    }
  }

  def groupByOrdered[A, K](seq: Seq[A])(f: A => K): Seq[(K, Seq[A])] = {
    val holder = mutable.LinkedHashMap.empty[K, Seq[A]].withDefault(_ => new mutable.ArrayBuffer[A])
    seq.foreach { x =>
      val k = f(x)
      holder(k) = holder(k) :+ x
    }
    holder.toSeq
  }

}
