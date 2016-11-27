package be.rubenpieters.model

import cats.Monad
import cats.data._
import cats._
import cats.implicits._

/**
  * Created by ruben on 26/11/2016.
  */
object SubGameRunner {
  def run[I, S, O](subGame: SubGame[I, S, O]): State[S, O] = {
    chainTillEnd(subGame.operation(subGame.input))
  }

  def chainTillEnd[I, S, O](op: State[S, Either[O, SubGame[I, S, O]]]): State[S, O] = {
    for {
      result <- op
      nextResult <- result match {
        case Left(output) => State.pure[S, O](output)
        case Right(subGame) => chainTillEnd(subGame.operation(subGame.input))
      }
    } yield nextResult
  }


//    def sequenceUntil[M[_]: Monad, A](condition: A => Boolean, monad: M[A]): M[A] = {
//      for {
//        ma <- monad
//        result <- if (condition(ma)) {
//          monad
//        } else {
//
//        }
//      } yield result
//    }
}
