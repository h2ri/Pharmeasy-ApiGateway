package controllers

import java.util.{Date, Properties}

import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import play.api._
import play.api.mvc._

import scala.util.Random
//
//object Application extends Controller {
//
//  def index = Action {
//    Ok(views.html.index())
//  }
//
//
//}

import com.google.inject.Inject
import com.ning.http.client.AsyncHttpClientConfig
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import play.api.libs.json.JsPath
import play.api.libs.json.Json.toJson
import play.api.libs.json.Reads
import play.api.libs.json.Reads._
import play.api.libs.json.Reads.StringReads
import play.api.libs.json.Reads.functorReads
import play.api.libs.ws.WSClient
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.Play.current

import play.api.libs.ws.ning._
import play.api.libs.ws._

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._


object Application extends Controller {


  def f[T] (v: T) = v
  // Shows the login screen and empties the session:


  def index = Action { request =>

//     val authRequest = WS.url("http://139.59.16.77:9001/resources")
//        .withHeaders("Authorization" -> request.headers.get("Authorization").map( s =>
//          s
//        ).getOrElse("test"))
//        .get
//
//        authRequest.map(s =>
//          if(s.status == 401){
//          println("Unauthorized")
//          }
//          else{
//            //Write logic to populate the Kafka queue
//            println(s.body + " " + f(s.status) )
//
//          }
//        )
    val events = 10
    val topic = "sms"
    //val brokers = "127.0.0.1:9092"
    val brokers = "52.74.2.115:9092"
    val rnd = new Random()
    val props = new Properties()
    props.put("metadata.broker.list", brokers)
    props.put("serializer.class", "kafka.serializer.StringEncoder")
    //props.put("partitioner.class", "com.colobu.kafka.SimplePartitioner")
    props.put("producer.type", "async")
    //props.put("request.required.acks", "1")
    println("Here")
    val config = new ProducerConfig(props)
    val producer = new Producer[String, String](config)
    val t = System.currentTimeMillis()
    for (nEvents <- Range(0, events)) {
//      val runtime = new Date().getTime();
//      val ip = "192.168.2." + rnd.nextInt(255);
//      val msg = runtime + "," + nEvents + ",www.example.com," + ip;
        val msg = "Message : " + rnd.nextInt(255);
      val data = new KeyedMessage[String, String](topic,  msg);

        producer.send(data);
    }


    Ok(views.html.index()).withNewSession
  }

  // Handles the username-password sent as JSON:
  def login = Action(parse.json) { request =>

    // Creates a reader for the JSON - turns it into a LoginRequest
    implicit val loginRequest: Reads[LoginRequest] = Json.reads[LoginRequest]

    /*
     * Call validate and if ok we return valid=true and put username in session
     */
    request.body.validate[LoginRequest] match {
      case s: JsSuccess[LoginRequest] if (s.get.authenticate) => {
        Ok(toJson(Map("valid" -> true))).withSession("user" -> s.get.username)
      }
      // Not valid
      case _ => Ok(toJson(Map("valid" -> false)))
    }
  }

  def welcome = Action { implicit request =>
    request.session.get("user").map {
      user =>
      {
        Ok(views.html.welcome(user))
      }
    }.getOrElse(Redirect(routes.Application.index()))
  }
}

case class LoginRequest(username: String, password: String) {

  // Simple username-password map in place of a database:
  //val validUsers = Map("sysadmin" -> "password1", "root" -> "god")
  val request = WS.url("https://api.github.com/repos/playframework/Play20/commits")
    .get

  val validUsers = Map("sysadmin" -> "password1", "root" -> "god")

  def authenticate = validUsers.exists(_ == (username, password))
}