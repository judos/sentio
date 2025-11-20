

function form2Json(formId) {
	const form = document.getElementById(formId);
	return JSON.stringify(Object.fromEntries(new FormData(form)));
}
