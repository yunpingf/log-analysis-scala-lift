package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._
import common._
import http._
import js.jquery.JQueryArtifacts
import sitemap._
import Loc._
import mapper._
import com.myproject.analysis.model._
import com.myproject.analysis.snippet._
import net.liftmodules.JQueryModule
import net.liftweb.mongodb.MongoDB
import com.mongodb.ServerAddress
import net.liftweb.mongodb.DefaultMongoIdentifier
import com.mongodb.Mongo

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor =
        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
          Props.get("db.url") openOr
            "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
          Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    //Schemifier.schemify(true, Schemifier.infoF _, User)

    //uncomment the above lines, and use mongoDB as database to save meta data
    val server = new ServerAddress("127.0.0.1", 27017)
    //configure the database MongoDB uses
    MongoDB.defineDb(DefaultMongoIdentifier, new Mongo(server), "mydb")

    // where to search snippet
    LiftRules.addToPackages("com.myproject.analysis")

    // Build SiteMap
    /*def sitemap = SiteMap(
      Menu.i("Screen") / "screen",
      Menu.i("Home") / "index" >> User.AddUserMenusAfter, // the simple way to declare a menu

      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
	       "Static Content")))*/
    val myLoc = Loc("HomePage", "index" :: Nil, "Home Page")
    val myMenu = Menu(myLoc)

    val allMenus = myMenu :: Nil
    val mySiteMap = SiteMap(allMenus: _*)
    LiftRules.setSiteMap(mySiteMap)

    /*def sitemapMutators = User.sitemapMutator

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))*/

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    LiftRules.jsArtifacts = JQueryArtifacts
    JQueryModule.InitParam.JQuery = JQueryModule.JQuery172
    JQueryModule.init()

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
    //val displayRawData = java.lang.Class.forName(Props.get("displayrawdata").get.toString()).newInstance.asInstanceOf[DispatchRawData]
    LiftRules.snippetDispatch.append(
      Map("import_data" -> ImportData,
        "import_homework" -> ImportHomework,
        "top_videos" -> TopVideos,
        "video_by_student" -> VideoByStudent,
        "video_pause" -> VideoPause,
        "video_move" -> VideoMove,
        "video_info" -> VideoInfo))

    LiftRules.ajaxPostTimeout = 50000 //m

  }
}