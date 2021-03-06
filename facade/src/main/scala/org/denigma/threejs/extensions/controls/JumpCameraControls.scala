package org.denigma.threejs.extensions.controls

import org.denigma.threejs.extensions.animations.{Animation, Scheduler}
import org.denigma.threejs.{Vector3, Scene, Camera}
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.{MouseEvent}
import scala.concurrent.duration
import scala.concurrent.duration.Duration
import scala.concurrent.duration.MILLISECONDS
import scala.language.postfixOps

/**
 * Camera that changes its center when a click is done on objects
 * @param camera
 * @param element
 * @param scene
 * @param width
 * @param height
 * @param center
 */
class JumpCameraControls(val camera:Camera,
                         val element:HTMLElement,
                         val scene:Scene,
                          val width:Double, val height:Double,
                         center:Vector3 = new Vector3())
  extends HoverControls(camera,element,center) with IntersectionControls
{

  implicit val scheduler = new Scheduler().start()

  override def onMouseMove(event:MouseEvent) = {
    this.onCursorMove(event.clientX,event.clientY,width,height)
    rotateOnMove(event)
  }

  def moveTo(position:Vector3) = {
    val start = center.clone()
    val dp = new Vector3().subVectors(position,center)
    dom.console.info(dp)

    new Animation(Duration(1,duration.SECONDS)) (  p =>{

        val m = dp.clone().multiplyScalar(p)
        val cur = start.clone().add(m)
        //dom.console.info(cur)
        center.copy(cur)
    }).go(scheduler)
    //center.copy(position)
  }

  override def onMouseDown(event:MouseEvent) = {
    this.intersections.headOption match {
      case Some(obj)=> obj.`object`.position match {
        case p if p.equals(center)=> super.onMouseDown(event)
        case p=>    moveTo(p)
      }
      case None=>super.onMouseDown(event)
    }

  }

}
