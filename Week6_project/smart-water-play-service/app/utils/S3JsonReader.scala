package utils

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import play.api.Configuration
import play.api.libs.json._
import javax.inject.Inject

import scala.jdk.CollectionConverters._

class S3JsonReader @Inject()(config: Configuration) {

  private val accessKey = config.get[String]("aws.accessKey")
  private val secretKey = config.get[String]("aws.secretKey")
  private val region    = config.get[String]("aws.region")

  private val creds = new BasicAWSCredentials(accessKey, secretKey)

  private val s3: AmazonS3 =
    AmazonS3ClientBuilder
      .standard()
      .withRegion(region)
      .withCredentials(new AWSStaticCredentialsProvider(creds))
      .build()

  def readRecentReadings(path: String, limit: Int): Seq[JsValue] = {

    val uri = new java.net.URI(path.replace("s3a://", "s3://"))
    val bucket = uri.getHost
    val prefix = uri.getPath.stripPrefix("/")

    val objects = s3
      .listObjects(bucket, prefix)
      .getObjectSummaries
      .asScala
      .toList
      .sortBy(_.getLastModified.getTime)
      .reverse
      .take(limit)

    objects.flatMap { obj =>
      val stream = s3.getObject(bucket, obj.getKey).getObjectContent
      val json   = Json.parse(stream)
      stream.close()
      Some(json)
    }
  }
}
