package controllers

import scala.xml._
import java.net._

import play._
import play.mvc._

import play.cache.Cache
import play.data.validation._

object Application extends Controller {
        
    def processURL(@Required url:String) = {
		if(validation.hasErrors) {
			flash += "error" -> "You left the URL field blank!"
		} else {
			while (Cache.get("url")!=None){print ("limbo")}
						
			var results = List[List[String]]()
			if (url != null) {
				try {
					var html = parse(URLDecoder.decode(url, "utf-8"))
					List("h1", "h2", "h3", "h4", "h5", "h6").foreach {
					(heading)=>
						html \\ heading foreach { 
								(n) =>
									var i = List(heading, (n).text)
									results ::= i
									
						}
					}
					Cache.add("url", url, "5s")
					results = results.filter(h => countVowels(h(1))>=4).sort((e1, e2) => (countVowels(e1(1)) > countVowels(e2(1))))
					Cache.set("report", results)
				} catch {
					case e: org.xml.sax.SAXParseException => flash.error("There was an error in parsing the page");
					case e => flash.error("Error: "+e.getMessage);
				}
			}
		}
		Action(index)
    }
    
	def index() = {
		var results = Cache.get("report")
		if (results.isEmpty){
			Template('report -> results)
		} else {
			Template('report -> results.toList(0))
		}
		
	}
		
	def countVowels(text:String):Int = {
			var count = 0
			text.toLowerCase().foreach { c=> 
				if (c=='a' || c=='e' || c=='i' || c=='o' || c=='u') {
							count+=1
				}
			}
			return count
		}
	
	def parse(sUrl:String):Elem = {
			import java.net._
			
			var url = new URL(sUrl)
			var connect = url.openConnection
			XML.load(connect.getInputStream)
		}
    
}
