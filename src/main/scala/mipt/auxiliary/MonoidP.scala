package mipt.auxiliary

trait MonoidP[A]:
  def empty: A
  def combine(a: A, b: A): A

object MonoidP:
  def apply[A](using MonoidP[A]): MonoidP[A] = summon[MonoidP[A]]

  given MonoidP[Int] = new MonoidP[Int]:
    def empty: Int = 0
    def combine(a: Int, b: Int): Int = a + b

  given MonoidP[String] = new MonoidP[String]:
    def empty: String = ""
    def combine(a: String, b: String): String = a ++ b
