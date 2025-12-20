function form2Json(formId) {
	const form = document.getElementById(formId);
	return JSON.stringify(Object.fromEntries(new FormData(form)));
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
 * @param input RequestInfo | URL
 * @param init ?RequestInit
 * @returns Promise<any>
 */
async function fetchJson(input, init) {
	return new Promise((resolve, reject) => {
		fetch(input, init).then(async (response) => {
			if (!response.ok) {
				const data = await response.json().catch((e) => {
					return {
						key: 'json_parse_error',
						message: e.message,
						details: e
					};
				});
				console.warn('Business error:', data, response);
				return reject(new FetchJsonError(response.status || 0, data.key,
					data.message, data.details));
			}
			const data = await response.text();
			const json = data ? JSON.parse(data) : {};
			resolve(json);
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
