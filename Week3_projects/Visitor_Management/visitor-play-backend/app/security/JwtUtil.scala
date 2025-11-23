package security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import java.util.Date

object JwtUtil {
  private val secretKey = "pranjal-jwt-key"
  private val algorithm = Algorithm.HMAC256(secretKey)
  private val issuer = "play-name"

  def generateToken(userId: String, expirationMillis: Long = 3600000): String = {
    val now = System.currentTimeMillis()
    JWT.create()
      .withIssuer(issuer)
      .withSubject(userId)
      .withIssuedAt(new Date(now))
      .withExpiresAt(new Date(now + expirationMillis))
      .sign(algorithm)
  }

  def validateToken(token: String): Option[String] = {
    try {
      val verifier = JWT.require(algorithm).withIssuer(issuer).build()
      val decoded = verifier.verify(token)
      Some(decoded.getSubject)
    } catch {
      case _: JWTVerificationException => None
    }
  }
}
