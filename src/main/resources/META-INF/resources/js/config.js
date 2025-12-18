function setConfig(key, value) {
	const cookieName = `sentio_${key}`;
	if (value === null) {
		document.cookie = `${cookieName}=; Max-Age=0; Path=/; SameSite=Strict`;
	} else {
		document.cookie = `${cookieName}=${encodeURIComponent(value)}; Path=/; SameSite=Strict`;
	}
}

function getConfig(key) {
	const cookieName = `sentio_${key}=`;
	const cookies = document.cookie.split(';');
	for (let c of cookies) {
		c = c.trim();
		if (c.startsWith(cookieName)) {
			return decodeURIComponent(c.substring(cookieName.length));
		}
	}
	return null;
}
