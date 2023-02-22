package mipt.utils

import mipt.utils.Homeworks.{TaskDef, TaskNotDone}

trait TaskSyntax:
  extension (cs: StringContext)
    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    def task(refs: Any*): TaskDef = xs => {
      val message = cs.s(refs: _*).stripMargin
      throw TaskNotDone(xs.mkString("."), message)
    }

object Homeworks extends TaskSyntax:
  final case class TaskNotDone(num: String, text: String) extends RuntimeException(s"выполните задание $num : \n $text")

  trait TaskDef {
    def applySeq(num: Seq[Int]): Nothing
    def apply(num: Int*): Nothing = applySeq(num)
  }
