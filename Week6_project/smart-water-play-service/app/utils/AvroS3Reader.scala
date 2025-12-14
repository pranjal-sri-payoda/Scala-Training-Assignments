package utils

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import org.apache.avro.file.DataFileReader
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import play.api.libs.json._
import play.api.Configuration

import javax.inject.Inject
import java.io.{File, FileOutputStream}
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

class AvroS3Reader @Inject()(config: Configuration) {

  private val accessKey = config.get[String]("aws.accessKey")
  private val secretKey = config.get[String]("aws.secretKey")
  private val region = config.get[String]("aws.region")

  private val creds = new BasicAWSCredentials(accessKey, secretKey)

  private val s3: AmazonS3 = AmazonS3ClientBuilder.standard()
    .withRegion(region)
    .withCredentials(new AWSStaticCredentialsProvider(creds))
    .build()

  /** Convert AVRO values to proper JSON */
  private def toJsValue(v: Any): JsValue = v match {
    case null           => JsNull
    case s: String      => JsString(s)
    case i: Int         => JsNumber(i)
    case l: Long        => JsNumber(l)
    case d: Double      => JsNumber(d)
    case f: Float       => JsNumber(BigDecimal.decimal(f))
    case b: Boolean     => JsBoolean(b)
    case other          => JsString(other.toString) // fallback
  }

  /** Reads all AVRO files under an S3 "folder" and converts them to JSON */
  def readAvroFolder(path: String): Seq[JsValue] = {

    // Convert s3a:// → s3://
    val uri = new java.net.URI(path.replace("s3a://", "s3://"))
    val bucket = uri.getHost
    val prefix = uri.getPath.stripPrefix("/")

    val summaries = s3.listObjects(bucket, prefix).getObjectSummaries.asScala.toList

    val result = ListBuffer[JsValue]()

    summaries.foreach { summary =>
      if (summary.getKey.endsWith(".avro")) {

        val s3Obj = s3.getObject(bucket, summary.getKey)
        val stream = s3Obj.getObjectContent

        // Temp file for AVRO
        val tempFile = File.createTempFile("avro_temp_", ".avro")
        val output = new FileOutputStream(tempFile)

        // FIX: Correct buffer initialization
        val buffer = new Array[Byte](1024 * 1024) // 1 MB buffer
        var read = stream.read(buffer)

        while (read != -1) {
          output.write(buffer, 0, read)
          read = stream.read(buffer)
        }

        output.close()
        stream.close()

        // Read AVRO records
        val datumReader = new GenericDatumReader[GenericRecord]()
        val avroReader = DataFileReader.openReader(tempFile, datumReader)

        while (avroReader.hasNext) {
          val rec = avroReader.next()

          val fields: Seq[(String, Json.JsValueWrapper)] =
            rec.getSchema.getFields.asScala.toSeq.map { f =>
              val raw = rec.get(f.name)
              val jsValue = toJsValue(raw)
              f.name -> Json.toJsFieldJsValueWrapper(jsValue)
            }

          val json = Json.obj(fields: _*)
          result += json
        }

        avroReader.close()
        tempFile.delete()
      }
    }

    result.toSeq
  }
}
