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
