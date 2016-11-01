package be.rubenpieters.model.rps

import simulacrum._
import shapeless._

/**
  * Created by ruben on 31/10/2016.
  */
@typeclass trait RpsRules[A] {
  def beats[B : RpsRules](a: A, b: B): RpsWinState
}

object RpsRules extends RpsRules0 {
  implicit val cnilRpsRules: RpsRules[CNil] =
    new RpsRules[CNil] {
      override def beats[B: RpsRules](a: CNil, b: B): RpsWinState = Loss
    }

  implicit def coproductConsRpsRules[L, R <: Coproduct](implicit
                                                        lR: RpsRules[L],
                                                        rR: RpsRules[R]
                                                       ): RpsRules[L :+: R] =
    new RpsRules[L :+: R] {
      override def beats[B: RpsRules](a: :+:[L, R], b: B): RpsWinState =
        a.eliminate(lR.beats(_, b), rR.beats(_, b))
    }
}

trait RpsRules0 {
  implicit def genericSwearWordFinder[T, G](implicit
                                            gen: Generic.Aux[T, G],
                                            rr: Lazy[RpsRules[G]]
                                           ): RpsRules[T] =
    new RpsRules[T] {
      override def beats[B: RpsRules](a: T, b: B): RpsWinState = rr.value.beats(gen.to(a), b)
    }
}