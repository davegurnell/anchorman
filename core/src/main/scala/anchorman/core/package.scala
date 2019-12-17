package anchorman

import cats.MonadError

package object core {
  type MonadThrowable[F[_]] = MonadError[F, Throwable]
}
