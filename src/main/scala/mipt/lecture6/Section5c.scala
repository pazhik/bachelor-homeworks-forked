package mipt.lecture6





object Section5c extends App {


  object CatsReaderT {

    import cats.data.Kleisli
    import cats.syntax.all._


    //type Id[A] = A
    //type Reader[A, B] = Kleisli[Id, A ,B]

    // A => F[B]
    //type ReaderT[F[_], A, B] = Kleisli[F, A, B]



    ///





    private val ipsocket = Seq(1, 1, 2, 3, 5, 8, 13, 21) 
    private val database = Seq(1 / 1.0, 1 / 1.0, 1 / 2.0, 1 / 6.0, 1 / 24.0, 1 / 120.0, 1 / 720.0, 1 / 5040.0) 


    case class IpConfig(connectionData: Seq[Int] = ipsocket)

    case class DbConfig(connectionData: Seq[Double] = database)


    trait IpSocket extends (Int => Int)

    trait DataBase extends (Int => Double)


    object Ip {
      val fromConfig: Kleisli[Option, IpConfig, IpSocket] =
        Kleisli(
          (ipConfig: IpConfig) =>
            (
              new IpSocket {
                private val data = ipConfig.connectionData.zipWithIndex.map(_.swap).toMap

                override def apply(index: Int): Int =
                  data(index)
              }
            ).some
        )
    }

    object Db {
      val fromConfig: Kleisli[Option, DbConfig, DataBase] =
        Kleisli(
          (dbConfig: DbConfig) =>
            (
              new DataBase {
                private val data = dbConfig.connectionData.zipWithIndex.map(_.swap).toMap

                override def apply(index: Int): Double =
                  data(index)
              }                  
            ).some
        )
    }


    case class AppConfig(dbConfig: DbConfig, ipConfig: IpConfig)

    case class Application(database: DataBase, ipSocket: IpSocket)



    val applicationConstructor: Kleisli[Option, AppConfig, Application] =
      for {
        db <- Db.fromConfig.local[AppConfig](_.dbConfig)
        ip <- Ip.fromConfig.local[AppConfig](_.ipConfig)
      } yield Application(db, ip)


  }


  import CatsReaderT._


  
  val constructed = applicationConstructor.run(
    AppConfig(DbConfig(), IpConfig())
  )
  

  constructed.map(_.ipSocket(4)).tapEach(println)
  constructed.map(_.database(3)).tapEach(println)

}
