package be.rubenpieters.model.number

import cats._
import cats.implicits._
import be.rubenpieters.model.{Idle, SubGame}
import be.rubenpieters.util.ImmutableRng
import cats.data.State

/**
  * Created by ruben on 26/11/2016.
  */
object NumberRunner extends App {
  val initialRng = ImmutableRng.scrambled(1)
  val numberGameRun: State[ImmutableRng, Int] = for {
    toGetNumbers <- (1 to 5).toList.traverseU(_ => ImmutableRng.nextLong() map math.abs)
    assemblingNumbers <- (1 to 10).toList.traverseU(_ => ImmutableRng.nextInt(50) map math.abs)
    assemblyGame1 = createNumberAssemblyGame(1, toGetNumbers.head, assemblingNumbers)
    assemblyGame2 = createNumberAssemblyGame(2, toGetNumbers.head, assemblingNumbers)
  } yield -1

  val numberGame = SubGame[Unit, ImmutableRng, Int]("number_game", (), initialRng, _ => numberGameRun, Idle)


  val assemblyGameRun: (Long, List[Int]) => State[Unit, (List[Int], List[Int] => Long)] =
    (toGetNumber, assemblingNumbers) =>
      State.pure{
        (List[Int](), (a: List[Int]) => 0L)
      }
  def createNumberAssemblyGame(id: Int, toGetNumber: Long, assemblingNumbers: List[Int]) =
    SubGame[(Long, List[Int]), Unit, (List[Int], List[Int] => Long)](
      s"assembly_game_$id", (toGetNumber, assemblingNumbers), (), assemblyGameRun.tupled, Idle
    )

}
