package anchorman.pdf

import java.io._

import anchorman.core._
import javax.xml.transform._
import javax.xml.transform.sax._
import javax.xml.transform.stream._
import org.apache.fop.apps._

object PdfWriter {
  // Step 1: Construct a FopFactory (reuse if you plan to render multiple documents!)
  val fopFactory = new FopFactoryBuilder(new File(".").toURI).build()

  def write(doc: Document, file: File) = {
    // Step 2: Set up output stream.
    // Note: Using BufferedOutputStream for performance reasons (helpful with FileOutputStreams).
    val out = new BufferedOutputStream(new FileOutputStream(file))

    try {
      // Step 3: Construct fop with desired output format
      val fop = fopFactory.newFop("application/pdf", out)

      // Step 4: Setup JAXP using identity transformer
      val transformer = TransformerFactory.newInstance().newTransformer()

      // Step 5: Setup input and output for XSLT transformation
      val src = new StreamSource(new ByteArrayInputStream(FopWriter.write(doc)))

      // Resulting SAX events (the generated FO) must be piped through to FOP
      val res = new SAXResult(fop.getDefaultHandler)

      // Step 6: Start XSLT transformation and FOP processing
      transformer.transform(src, res)
    } finally {
      //Clean-up
      out.close()
    }
  }
}
