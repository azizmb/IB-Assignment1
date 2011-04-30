package controllers

import java.net._

import scala.xml._

import play._
import play.mvc._

import play.cache.Cache

object Application extends Controller {
        
	def index(url:String) = {
		while (Cache.get(url)!=null){}
			
		var results = List[List[String]]()
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
			Cache.add(url, "5s")
			results = results.filter(h => countVowels(h(1))>=4).sort((e1, e2) => (countVowels(e1(1)) > countVowels(e2(1))))
			Cache.add("report", results)
		} catch {
			case e => flash.error("Error in page: "+e.getMessage);
		}

		Template('report -> results)
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
			var url = new URL(sUrl)
			var connect = url.openConnection
			XML.load(connect.getInputStream)
		}
    
}
