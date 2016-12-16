package be.rubenpieters.model

import be.rubenpieters.model.Game.UserCreateCombination
import cats.data.State

import scala.annotation.tailrec

/**
  * Created by ruben on 16/12/2016.
  */
object Game {
  //def create[I, S, O]: I => State[S, O]

  type UserInput = (List[NumOps], List[(Long, Boolean)], Long)
  type UserCreateCombination = (List[(Long, Boolean)], Long) => List[NumOps]

  def sampleUserCombFunc: UserCreateCombination = (_, _) => List(Add(0), Add(1))

  def handleTurns(userInputs: List[(UserInput, UserCreateCombination)]): List[List[NumOps]] = {
    userInputs.map { case ((ops, nums, toGetNum), userFunc) =>
      val numsAfterPreviousTakes = numsAvailable(ops, nums.map(_._1))
      ops ++ userFunc(numsAfterPreviousTakes, toGetNum)
    }
  }

  def numsAvailable(opList: List[NumOps], numList: List[Long]): List[(Long, Boolean)] = {
    val indices = opList.map(_.index)
    numList.zipWithIndex.map{ case (x, i) => (x, indices.contains(i))}
  }

  def executeNumOps(ops: List[NumOps], nums: List[Long]) = {
    ops.foldLeft(0L){ case (num, op) => op match {
      case Add(i) => num + nums(i)
      case Subtract(i) => num - nums(i)
      case Multiply(i) => num * nums(i)
    }}
  }

}

sealed trait NumOps {
  def index: Int
}

case class Add(index: Int) extends NumOps
case class Subtract(index: Int) extends NumOps
case class Multiply(index: Int) extends NumOps

class NumOpsError(message: String) extends Exception

object AdHoc extends App {
  val players = List[UserCreateCombination](
    Game.sampleUserCombFunc,
    Game.sampleUserCombFunc
  )

  val inputNums = List[Long](1, 2, 3, 4, 5)
  val toGetNums = List[Long](10, 20)

  val preparedInputNums = inputNums.map(x => (x, false))
  val gameResult = Game.handleTurns(players.map(x => ((List[NumOps](), preparedInputNums, 10L), x)))

  println(gameResult)
  println(gameResult.map(ops => Game.executeNumOps(ops, inputNums)))
}