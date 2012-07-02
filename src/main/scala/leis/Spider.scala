package leis

import java.io.File

import net.vidageek.crawler.config.CrawlerConfigurationBuilder
import net.vidageek.crawler.{Url, Status, PageVisitor, PageCrawler, Page}

object Spider {

  def main(args : Array[String]) {
    val cfg = new CrawlerConfigurationBuilder("http://www4.planalto.gov.br/legislacao").
      withMaxParallelRequests(20).withRequestDelay(1000).build
    val crawler = new PageCrawler(cfg)
    crawler.crawl(Visitor())
  }

}

case class Visitor() extends PageVisitor {

  val directory = new File(System.getProperty("user.home"))

  def visit(page : Page) : Unit = {
    println("Visitando " + page.getUrl)
    if (page.getUrl.toLowerCase.contains("ccivil_3")) {

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

}