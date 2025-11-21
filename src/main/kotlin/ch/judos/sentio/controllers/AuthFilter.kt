package ch.judos.sentio.controllers



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
