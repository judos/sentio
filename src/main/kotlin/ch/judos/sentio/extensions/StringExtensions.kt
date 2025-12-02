package ch.judos.sentio.extensions

import java.net.URLEncoder


fun String.urlencode(): String {
	return URLEncoder.encode(this, Charsets.UTF_8)
}
