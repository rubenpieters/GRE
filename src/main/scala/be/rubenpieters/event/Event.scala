package be.rubenpieters.event

/**
  * Created by ruben on 2/01/17.
  */
sealed trait TimeEvent[T, V]

case class Event[T: Ordering, V](time: T, value: V) extends TimeEvent[T, V]
case class Gap[T: Ordering, V](firstTime: T, secondTime: T) extends TimeEvent[T, V]

object TimeEvent {
  implicit def timeEventOrdering[T: Ordering, V]: Ordering[TimeEvent[T, V]] = new Ordering[TimeEvent[T, V]] {
    override def compare(x: TimeEvent[T, V], y: TimeEvent[T, V]): Int = (x, y) match {
      case (Event(tx, vx), Event(ty, vy)) => Ordering[T].compare(tx, ty)
      case (Event(tx, vx), Gap(ty, vy)) => -1
      case (Gap(tx, vx), Event(ty, vy)) => 1
      case (Gap(tx, vx), Gap(ty, vy)) => ???
    }
  }
}

object Gap {
  implicit def gapOrdering[T: Ordering, V]: Ordering[Gap[T, V]] = new Ordering[Gap[T, V]] {
    override def compare(x: Gap[T, V], y: Gap[T, V]): Int = Ordering[T].compare(x.firstTime, y.firstTime)
  }
}

object Event {
  implicit def eventOrdering[T: Ordering, V]: Ordering[Event[T, V]] = new Ordering[Event[T, V]] {
    override def compare(x: Event[T, V], y: Event[T, V]): Int = Ordering[T].compare(x.time, y.time)
  }

  def apply[T: Ordering, V](value: V, timeFromValue: V => T): Event[T, V] = Event(timeFromValue(value), value)

  def addGaps[T: Ordering, V](list: List[Event[T, V]]): List[TimeEvent[T, V]] = {
    list.sorted.sliding(2).flatMap{ list =>
      if (list.size == 2) {
        List(list.head, Gap[T, V](list.head.time, list(1).time))
      } else {
        ???
      }
    }.toList :+ list.last
  }

  def stringifyEvents[T: Ordering, V](list: List[Event[T, V]], stringifyV: V => String): List[String] = {
    list.sorted.map(e => stringifyV(e.value))
  }

  def stringifyTimeEvents[T: Ordering, V](list: List[TimeEvent[T, V]], stringifyTE: TimeEvent[T, V] => String): List[String] = {
    list.sorted.map(e => stringifyTE(e))
  }
}

object Test extends App {
  val list = List(
    Event(1, "a")
    ,Event(10, "b")
    ,Event(100, "c")
  )

  println(Event.stringifyEvents(list, (x: String) => x.capitalize))
  println(Event.addGaps(list))
}