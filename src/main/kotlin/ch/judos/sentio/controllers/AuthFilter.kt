package ch.judos.sentio.controllers

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession


// @WebFilter("/*")
// class AuthFilter : Filter {
//
// 	override fun doFilter(
// 			request: ServletRequest,
// 			response: ServletResponse,
// 			chain: FilterChain
// 	) {
// 		val req = request as HttpServletRequest
// 		val res = response as HttpServletResponse
//
// 		val path = req.requestURI
//
// 		// Allow login page itself without check
// 		if (path == "/login") {
// 			chain.doFilter(request, response)
// 			return
// 		}
//
// 		val session: HttpSession? = req.getSession(false)
// 		val loggedIn = session?.getAttribute("userId") != null
//
// 		if (!loggedIn) {
// 			res.sendRedirect("/login")
// 			return
// 		}
//
// 		chain.doFilter(request, response)
// 	}
// }
