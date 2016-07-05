package com.github.jhpoelen

import java.net.URL
import java.util.zip.GZIPInputStream

import scala.io.Source

case class Taxon(id: String, name: String, rank: String, commonNames: List[Option[String]] = List(),
                 pathNames: List[String], path: List[Option[String]], pathIds: List[Option[String]],
                 externalUrl: String, thumbnailUrl: Option[String] = None) {
  def withThumbnail(thumbnailUrl: Option[String]) = {
    copy(thumbnailUrl = thumbnailUrl)
  }
}

case class Tables(species: String, taxa: String, stocks: String)

trait DB {
  def tables: Tables

  def host: String

  def idPrefix: String

  def idToUrl(id: String): String = {
    s"http://$host/summary/${id}"
  }

  def imageNameToUrl(picName: String): Option[String] = {
    val imageName = """(.*)(\.)(\w+)$""".r
    picName match {
      case imageName(name, _, ext) => {
        Some(s"http://$host/images/thumbnails/$ext/tn_$name.$ext")
      }
      case _ => None
    }
  }

  def toExternalId(id: String): String = {
    if (id.length > 0) {
      s"$idPrefix:$id"
    } else {
      ""
    }
  }

}

object FishBase extends DB {
  override def tables = Tables(species = "species", taxa = "taxa", stocks = "stocks")


  override def host = "fishbase.org"

  override def idPrefix = "FB"
}

object SeaLifeBase extends DB {
  override def tables = Tables(species = "sealifebase_species", taxa = "sealifebase_taxa", stocks = "sealifebase_stocks")

  override def host = "sealifebase.org"

  override def idPrefix = s"SLB"

}

object FishbaseTaxonCache extends App {

  val baseUrl = "https://github.com/jhpoelen/fishbase_archiver/releases/download/v0.1.0/"
  val ext = ".tsv.gz"

  // read stocks, extract species ids
  def toStream(tableArchive: String) = {
    val urlString: String = toUrl(tableArchive)
    Console.err.println(s"[$urlString] opening...")
    val lines = Source.fromInputStream(new GZIPInputStream(new URL(urlString).openStream()), "UTF-8")
      .getLines()
      .map(_.split("\t"))
    Console.err.println(s"[$urlString] opened.")
    lines
  }

  def toUrl(tableName: String): String = {
    s"$baseUrl$tableName$ext"
  }

  val sealifebase = Tables(species = "sealifebase_species", taxa = "sealifebase_taxa", stocks = "sealifebase_stocks")

  val dbs = List(FishBase, SeaLifeBase)

  val taxonMapWithThumbnails = dbs.foldLeft(Map[String, Taxon]())((db_agg, db) => {
    val stocks = toStream(db.tables.stocks)

    val stocksHeader = stocks.next()
    val specCodes = stocks.flatMap(line => stocksHeader.zip(line).toMap.filterKeys("SpecCode" == _).values)
    val specCodesDistinct = specCodes.toSeq.distinct.toSet


    val taxa = toStream(db.tables.taxa)
    val taxaHeader = taxa.next()
    val taxonMap = taxa.foldLeft(db_agg)((agg, line) => {
      val taxonLine = taxaHeader.zip(line).toMap
      taxonLine.get("SpecCode") match {
        case Some(specCode) => if (specCodesDistinct.contains(specCode)) {
          val rankNames = List("Class", "Order", "Family", "SubFamily", "Genus", "Species")
          val aTaxon = Taxon(id = db.toExternalId(specCode),
            name = List(taxonLine("Genus"), taxonLine("Species")).mkString(" "),
            rank = "Species",
            pathNames = rankNames,
            pathIds = List(None, None, Some("FamCode"), None, Some("GenCode"), Some("SpecCode")).map {
              case Some(pathId) => Some(db.toExternalId(taxonLine.getOrElse(pathId, "")))
              case None => None
            },
            path = rankNames.map(rankName => {
              taxonLine.get(rankName) match {
                case Some(taxonName) => {
                  if (taxonName == "null") {
                    None
                  } else {
                    Some(taxonName)
                  }
                }
                case _ => None
              }
            }),
            externalUrl = db.idToUrl(specCode))
          agg ++ Map(specCode -> aTaxon)
        } else {
          agg
        }
        case None => agg
      }
    })

    val species = toStream(db.tables.species)
    val speciesHeader = species.next()
    species.foldLeft(taxonMap)((agg, line) => {
      val speciesLine = speciesHeader.zip(line).toMap
      (speciesLine.get("SpecCode"), speciesLine.get("PicPreferredName")) match {
        case (Some(specCode), Some(picName)) => {
          agg.get(specCode) match {
            case Some(taxon) => {
              val thumbnailUrl = db.imageNameToUrl(picName)
              agg ++ Map(specCode -> taxon.withThumbnail(thumbnailUrl))
            }
            case None => agg
          }
        }
        case _ => agg
      }
    })
  })

  val taxonCacheHeader: List[String] = List("id", "name", "rank", "commonNames", "path", "pathIds", "pathNames", "externalUrl", "thumbnailUrl")
  println(taxonCacheHeader.mkString("\t"))

  taxonMapWithThumbnails.values.map { taxon =>
    List(taxon.id, taxon.name, taxon.rank, taxon.commonNames.map(_.getOrElse("")).mkString("|"),
      taxon.path.map(_.getOrElse("")).mkString("|"), taxon.pathIds.map(_.getOrElse("")).mkString("|"),
      taxon.pathNames.mkString("|"),
      taxon.externalUrl, taxon.thumbnailUrl.getOrElse(""))
  }.foreach(taxonList => println(taxonList.mkString("\t")))

}
