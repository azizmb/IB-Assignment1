package controllers

import scala.xml._
import java.net._

import play._
import play.mvc._

import play.cache.Cache
import play.data.validation._

object Application extends Controller {
        
	val urlActor = new UrlParseActor
	val reportActor = new ReportActor

	urlActor.start
	reportActor.start

	def processURL(@Required url:String) = {
		if(validation.hasErrors) {
			flash.error("You left the URL field blank!")
		} else {
			var results = (urlActor !? url)
			results match {
				case e: org.xml.sax.SAXParseException => flash.error("There was an error in parsing the page. Probably malformed HTML")
				case e: Exception => flash.error("Error: "+e.getMessage)
				case e: List[List[String]] => reportActor ! results
			}
		}
		Action(index)
	}
    
	def index() = {
		var results = (reportActor !? GetReport)	
		Template('report -> results)
	}
    
}


import scala.actors._
import scala.collection.immutable
import java.util.Date

class UrlParseActor extends Actor { 
	private var visited = immutable.Map.empty[String, Long]
	def act = {
		loop {
			react {
				case url: String =>
					try {
						val prev_visit = visited.getOrElse(url, 0: Long)
						val current_time = new Date().getTime()
						val elapsed = current_time - prev_visit

						if (elapsed <= 5000) {
							Thread.sleep(5000-elapsed)
						}					
					
						var results = List[List[String]]()
						var decoded_url = URLDecoder.decode(url, "utf-8")
						var html = parse(decoded_url)
						List("h1", "h2", "h3", "h4", "h5", "h6").foreach {
							(heading)=>
								html \\ heading foreach { 
									(n) =>
										var i = List(heading, (n).text)
										results ::= i		
							}
						}
						visited += (url -> new Date().getTime())
						reply(results.filter(h => countVowels(h(1))>=4).sort((e1, e2) => (countVowels(e1(1)) > countVowels(e2(1)))))
					} catch {			
						case e => reply (e)
					}
			}
		}
	}

	def parse(sUrl:String):Elem = {
		import java.net._
	
		var url = new URL(sUrl)
		var connect = url.openConnection
		XML.load(connect.getInputStream)
	}

	def countVowels(text:String):Int = {
		var count = 0
		text.toLowerCase().foreach {
			c=> 
				if (c=='a' || c=='e' || c=='i' || c=='o' || c=='u') {
					count+=1
				}
		}
		count
	}
} 

case object GetReport {}

class ReportActor extends Actor {
	private var report = List[List[String]]()

	def act = {
		while (true) {
			receive {
				case new_report: List[List[String]] => report = new_report
				
				case GetReport => reply(report)
			}
		}
	}
}

