package ch.judos.sentio.controllers

import ch.judos.sentio.entities.QWebsiteMonitorData
import ch.judos.sentio.entities.WebsiteMonitorData
import ch.judos.sentio.services.ImageService
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage


@Path("/api/website-monitor-data")
class WebsiteMonitorDataResource(
		val query: JPAQueryFactory,
		val entityManager: EntityManager,
		var imageService: ImageService,
) {
	
	val qData = QWebsiteMonitorData.websiteMonitorData
	
	@GET
	@Path("/{id}/{monitorKey}")
	fun generateImage(id: Long, monitorKey: String, days: Int): Response {
		val data: List<WebsiteMonitorData> = query.selectFrom(qData).where(
			qData.website.id.eq(id)
				.and(qData.monitor.eq(monitorKey))
		).orderBy(qData.datetime.desc()).limit(24).fetch()
		// TODO: aggregate data and generate image
		return imageService.image2Response(image())
	}
	
	fun image(): BufferedImage {
		val image = BufferedImage(300, 100, BufferedImage.TYPE_INT_RGB)
		val g2d = image.createGraphics()
		
		// Fill background
		g2d.color = Color.WHITE
		g2d.fillRect(0, 0, 300, 100)
		
		// Draw text
		g2d.color = Color.RED
		g2d.font = Font("Arial", Font.BOLD, 24)
		g2d.drawString("Hello Quarkus!", 50, 60)
		g2d.dispose()
		return image
	}
}
