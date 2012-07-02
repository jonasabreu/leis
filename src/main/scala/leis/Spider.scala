package leis

import java.io.File
import net.vidageek.crawler.config.CrawlerConfigurationBuilder
import net.vidageek.crawler.{ Url, Status, PageVisitor, PageCrawler, Page }
import java.text.SimpleDateFormat
import java.util.Date
import java.io.PrintWriter
import net.vidageek.crawler.component.Downloader
import net.vidageek.crawler.component.WebDownloader
import net.vidageek.crawler.component.LinkNormalizer
import net.vidageek.crawler.component.DefaultLinkNormalizer

object Spider {

  def main(args : Array[String]) {
    val cfg = new CrawlerConfigurationBuilder("http://www4.planalto.gov.br/legislacao").
      withMaxParallelRequests(20).withRequestDelay(500).withLinkNormalizer(Normalizer()).build
    val crawler = new PageCrawler(cfg)
    crawler.crawl(Visitor())
  }

}

case class Visitor() extends PageVisitor {

  val directory = new File(System.getProperty("user.home"), "leis/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date))
  directory.mkdirs

  implicit def addAsFileName(page : Page) = new {
    def asFileName = {
      var url = page.getUrl
      if (url.contains("#")) url = url.substring(0, url.indexOf("#"))
      url.replaceAll("[^A-Za-z0-9\\.\\-]", "_")
    }
  }

  def visit(page : Page) : Unit = {
    println("Visitando " + page.getUrl)
    if (page.getUrl.toLowerCase.contains("ccivil_03")) {
      val writer = new PrintWriter(new File(directory, page.asFileName))
      try {
        writer.print(page.getContent)
      } finally {
        writer.close
      }
    }
  }

  def onError(errorUrl : Url, statusError : Status) : Unit = {
    println("error: %s at %s".format(statusError, errorUrl))
  }

  def followUrl(url : Url) : Boolean = {
    isDomainAllowed(url)
  }

  private def isDomainAllowed(url : Url) : Boolean = {
    List("http://www4.planalto.gov.br", "http://www.planalto.gov.br").
      foldLeft(false)((acc, domain) => url.link.startsWith(domain) || acc)
  }

  private def isLawPage(url : Url) : Boolean = {
    url.link.toLowerCase.contains("ccivil_03")
  }

}

case class Normalizer() extends LinkNormalizer {

  val normalizer = new DefaultLinkNormalizer("http://www4.planalto.gov.br/legislacao")

  def normalize(url : String) : String = {
    val toReturn = normalizer.normalize(url)
    if (toReturn.contains("#"))
      toReturn.substring(0, toReturn.indexOf("#"))
    else
      toReturn
  }

}