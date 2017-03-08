/*
 * Copyright 2016 codecentric AG
 */

import java.util.concurrent.Executors
import org.scalatest.{ AsyncWordSpec, BeforeAndAfterAll, Matchers }
import scala.concurrent.duration.SECONDS

final class BackToFuturesSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {
  import BackToFutures._

  private implicit val scheduler = Executors.newSingleThreadScheduledExecutor()

  "areaDiffs" should {
    "return a future that completes after one seconcd" in {
      val expected =
        Vector(
          0.21460183660255172,
          0.8584073464102069,
          5.365045915063792,
          21.46018366025517,
          536.5045915063793
        )
      areaDiffs(Vector(1.0, 2.0, 5.0, 10.0, 50.0)).map(_ shouldBe expected)
    }
  }

  override protected def afterAll() = {
    scheduler.shutdown()
    scheduler.awaitTermination(42, SECONDS)
    super.afterAll()
  }
}
