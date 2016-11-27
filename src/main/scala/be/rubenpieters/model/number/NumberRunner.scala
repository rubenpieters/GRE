package be.rubenpieters.model.number

import cats._
import cats.implicits._
import be.rubenpieters.model._
import be.rubenpieters.util.ImmutableRng
import cats.data.State

/**
  * Created by ruben on 26/11/2016.
  */
object NumberRunner extends App {
  val initialRng = ImmutableRng.scrambled(1)

  type NGI = Unit
  type NGS = ImmutableRng
  type NGO = Int
  val numberGameRun: NGI => State[NGS, Either[NGO, SubGame[NGI, NGS, NGO]]] = _ => for {
    toGetNumbers <- (1 to 5).toList.traverseU(_ => ImmutableRng.nextLong() map math.abs)
    assemblingNumbers <- (1 to 10).toList.traverseU(_ => ImmutableRng.nextInt(50) map math.abs)
    assemblyGame1 = createNumberAssemblyGame(1, toGetNumbers.head, assemblingNumbers)
    assemblyGame2 = createNumberAssemblyGame(2, toGetNumbers.head, assemblingNumbers)
    game1Result <- SubGameRunner.run(assemblyGame1)
    game2Result <- SubGameRunner.run(assemblyGame2)
  } yield Left(-1): Either[NGO, SubGame[NGI, NGS, NGO]]

  val numberGame = SubGame[NGI, NGS, NGO]("number_game", (), numberGameRun)


  type AGI = (Long, List[Int])
  type AGS = ImmutableRng
  type AGO = (List[Int], List[Int] => Long)
  val assemblyGameRun: AGI => State[AGS, Either[AGO, SubGame[AGI, AGS, AGO]]] = {
    case ((toGetNumber, assemblingNumbers)) =>
      State.pure {
        Left((List[Int](), (a: List[Int]) => 0L))
      }
  }
  def createNumberAssemblyGame(id: Int, toGetNumber: Long, assemblingNumbers: List[Int]) =
    SubGame[AGI, AGS, AGO](
      s"assembly_game_$id", (toGetNumber, assemblingNumbers), assemblyGameRun
    )


  println(SubGameRunner.run(numberGame).run(initialRng).value)
}
