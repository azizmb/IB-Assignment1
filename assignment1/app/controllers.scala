package controllers

import scala.xml._
import java.net._

import play._
import play.mvc._

import play.cache.Cache
import play.data.validation._

object Application extends Controller {
        
	val urlActor = new UrlParseActor
	urlActor.start

	def processURL(@Required url:String) = {
		if(validation.hasErrors) {
			flash += "error" -> "You left the URL field blank!"
		} else {
			var results = (urlActor !? url)
			print (results)
		}
		Action(index)
	}
    
	def index() = {
		var results = Cache.get("report")
		var url = Cache.get("report_url")
		if (results.isEmpty){
			Template('report -> null)
		} else {
			Template('report -> results.toList(0), 'url -> url.toList(0))
		}
		
	}
    
}

import scala.actors._
class UrlParseActor extends Actor { 
	def act = {
		loop {
			react {  // Like receive, but uses thread polling for efficiency.
				case url: String => 
					print (url)
					var results = List[List[String]]()
					var devoded_url = URLDecoder.decode(url, "utf-8")
					var html = parse(devoded_url)
					List("h1", "h2", "h3", "h4", "h5", "h6").foreach {
						(heading)=>
							html \\ heading foreach { 
								(n) =>
									var i = List(heading, (n).text)
									results ::= i		
						}
					}
					results = results.filter(h => countVowels(h(1))>=4).sort((e1, e2) => (countVowels(e1(1)) > countVowels(e2(1))))
					reply(results)
				
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
		text.toLowerCase().foreach { c=> 
			if (c=='a' || c=='e' || c=='i' || c=='o' || c=='u') {
						count+=1
			}
		}
		return count
	}
} 

