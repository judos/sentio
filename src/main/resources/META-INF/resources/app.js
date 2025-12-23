function form2Json(formId) {
	const form = new FormData(document.getElementById(formId));
	const json = {};
	for (const [key, value] of form.entries()) {
		const parts = key.split(".");
		let obj = json;
		while (parts.length > 1) {
			const part = parts.shift();
			obj = obj[part] = obj[part] || {};
		}
		obj[parts[0]] = value;
	}
	return JSON.stringify(json);
}

function logout() {
	setConfig('jwt', null);
	window.location.href = '/login';
}

function changeBgAlpha(element, alpha) {
	const bg = window.getComputedStyle(element).backgroundColor;
	const match = bg.match(/rgba?\((\d+),\s*(\d+),\s*(\d+)(?:,\s*([\d.]+))?\)/);
	const r = match[1], g = match[2], b = match[3];
	element.style.backgroundColor = `rgba(${r}, ${g}, ${b}, ${alpha})`;
}

/**
 * @param url string
 * @param init ?RequestInit
 * @returns Promise<any>
 */
async function fetchJson(url, init) {
	if (!init.headers) {
		init.headers = {};
	}
	if (!init.headers['Content-Type']) {
		init.headers['Content-Type'] = 'application/json';
	}

	return new Promise((resolve, reject) => {
		fetch(url, init).then(async (response) => {
			try {
				if (!response.ok) {
					const data = await response.json();
					console.warn('Business error:', data, response);
					return reject(new FetchJsonError(response.status || 0, data.key,
						data.message ?? data.details, data.details));
				}
				const data = await response.text();
				const json = data ? JSON.parse(data) : {};
				resolve(json);
			} catch (err) {
				console.error(err);
				if (err instanceof SyntaxError) {
					reject(new FetchJsonError(0, 'unknown_error',
						'Invalid JSON response for http/' + response.status + ': ' + err.message, err));
				} else {
					reject(new FetchJsonError(0, 'unknown_error',
						err?.message || 'Unknown error', err));
				}
			}
		}).catch((err) => {
			console.error('Network error:', err);
			reject(new FetchJsonError(0, 'network_error',
				err?.message || 'Network error', err));
		});
	});
}

/**
 * @typedef {Object} JsonFetchError
 * @property {number} status
 * @property {string} key
 * @property {string} message
 * @property {any} details
 */
class FetchJsonError {
	/** @type {number} */
	status;
	/** @type {string} */
	key;
	/** @type {string} */
	message;
	/** @type {any} */
	details;

	/**
	 * @param {number} status
	 * @param {string} key
	 * @param {string} message
	 * @param {any} details
	 */
	constructor(status, key, message, details) {
		this.status = status;
		this.key = key;
		this.message = message;
		this.details = details;
	}
}
