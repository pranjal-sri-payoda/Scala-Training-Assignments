package config

case class AppConfig(
                      billingMonth: String,
                      lakePath: String,
                      accessKey: String,
                      secretKey: String,
                      mysqlUrl: String,
                      mysqlUser: String,
                      mysqlPass: String
                    )

object AppConfig {

  import com.typesafe.config.ConfigFactory

  def load(): AppConfig = {
    val c = ConfigFactory.load()
    AppConfig(
      billingMonth = c.getString("billing.month"),
      lakePath     = c.getString("s3.lake-path"),
      accessKey    = c.getString("s3.access-key"),
      secretKey    = c.getString("s3.secret-key"),
      mysqlUrl     = c.getString("mysql.url"),
      mysqlUser    = c.getString("mysql.username"),
      mysqlPass    = c.getString("mysql.password")
    )
  }
}
