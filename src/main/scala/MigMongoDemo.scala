import com.synhaptein.migmongo.MigmongoEngine
import com.synhaptein.migmongo.commands.ChangeGroup
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.{MongoDriver, MongoConnection, DefaultDB}
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType.{Descending, Ascending}
import reactivemongo.bson.BSONDocument
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.Try

case class Migmongo(db: DefaultDB) extends MigmongoEngine {
  changeGroups(
    MigrationMyApp("myApp")
  )
}

case class MigrationMyApp(group: String) extends ChangeGroup {
  changeSet("ChangeSet-1", "author1") { db =>
    List(
      db[BSONCollection]("table1").insert(BSONDocument("name" -> "John Doe")),
      //db[BSONCollection]("table3").insert(BSONDocument("name" -> "John Doe")),
      db[BSONCollection]("table2").insert(BSONDocument("name" -> "John Doe"))
    )
  }

  // Will be fire-and-forget
//  changeSet("ChangeSet-2", "author2") { db =>
//    List(
//      db[BSONCollection]("table1").update(
//        selector = BSONDocument(),
//        update = BSONDocument("$set" -> BSONDocument("price" -> 180)),
//        multi = true)
//    )
//  }
}



object MigMongoDemo extends App {

  val uri = "mongodb://dyk:mongo@localhost/profile"
  val driver = new MongoDriver
  val connection: Try[MongoConnection] =
    MongoConnection.parseURI(uri).map { parsedUri =>
      driver.connection(parsedUri)
    }
  val db: DefaultDB = connection.get.db("profile")
  Migmongo(db).process()


}
