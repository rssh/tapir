package sttp.tapir.server.tests

import cats.effect.IO
import sttp.capabilities.WebSockets
import sttp.capabilities.fs2.Fs2Streams
import sttp.client3.SttpBackend
import sttp.monad.MonadError
import sttp.tapir.tests.Test

/** All server tests in default configurations, except for streaming (which require a streams object) and web socket ones (which need to be
  * subclassed). If a custom configuration is needed, exclude the tests here, and add separately.
  */
class AllServerTests[F[_], ROUTE](
    createServerTest: CreateServerTest[F, Any, ROUTE],
    serverInterpreter: TestServerInterpreter[F, Any, ROUTE],
    backend: SttpBackend[IO, Fs2Streams[IO] with WebSockets],
    security: Boolean = true,
    basic: Boolean = true,
    contentNegotiation: Boolean = true,
    file: Boolean = true,
    mapping: Boolean = true,
    metrics: Boolean = true,
    multipart: Boolean = true,
    oneOf: Boolean = true,
    reject: Boolean = true,
    staticContent: Boolean = true,
    validation: Boolean = true,
    oneOfBody: Boolean = true
)(implicit
    m: MonadError[F]
) {
  def tests(): List[Test] =
    (if (security) new ServerSecurityTests(createServerTest).tests() else Nil) ++
      (if (basic) new ServerBasicTests(createServerTest, serverInterpreter).tests() else Nil) ++
      (if (contentNegotiation) new ServerContentNegotiationTests(createServerTest).tests() else Nil) ++
      (if (file) new ServerFileTests(createServerTest).tests() else Nil) ++
      (if (mapping) new ServerMappingTests(createServerTest).tests() else Nil) ++
      (if (metrics) new ServerMetricsTest(createServerTest).tests() else Nil) ++
      (if (multipart) new ServerMultipartTests(createServerTest).tests() else Nil) ++
      (if (oneOf) new ServerOneOfTests(createServerTest).tests() else Nil) ++
      (if (reject) new ServerRejectTests(createServerTest, serverInterpreter).tests() else Nil) ++
      (if (staticContent) new ServerStaticContentTests(serverInterpreter, backend).tests() else Nil) ++
      (if (validation) new ServerValidationTests(createServerTest).tests() else Nil) ++
      (if (oneOfBody) new ServerOneOfBodyTests(createServerTest).tests() else Nil)
}
