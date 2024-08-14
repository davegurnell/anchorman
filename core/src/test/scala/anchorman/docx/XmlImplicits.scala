package anchorman.docx

import java.io.StringWriter
import org.scalactic.Equality
import scala.xml._

object XmlImplicits {
  implicit val nodeSeqEquality: Equality[NodeSeq] =
    new Equality[NodeSeq] {
      val regex = "[ \\t\\r\\n]+".r

      def areEqual(a: NodeSeq, b: Any): Boolean = {
        b match {
          case b: NodeSeq =>
            val aWriter = new StringWriter
            val bWriter = new StringWriter
            XML.write(
              aWriter,
              Group(a),
              "utf-8",
              false,
              null,
              MinimizeMode.Default
            )
            XML.write(
              bWriter,
              Group(b),
              "utf-8",
              false,
              null,
              MinimizeMode.Default
            )
            val aString = aWriter.toString.replaceAll("\\s", "")
            val bString = bWriter.toString.replaceAll("\\s", "")
            // println(aString)
            // println(bString)
            aString == bString
          case _ => false
        }
      }
    }

}
