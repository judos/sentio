popupTemplate = "popup-template-";
popupId = "popup-id";
popupIdBack = "popup-id-backdrop";
popupContent = "popup-content";
popups = [];
popupIsShowing = false;
popupIsDisposing = false;
popupHideTimeout = 0;

window.addEventListener('DOMContentLoaded', function () {
	if (localStorage.getItem('popup') != null) {
		const popup = JSON.parse(localStorage.getItem('popup'))
		popupQueueText(popup);
		localStorage.removeItem('popup');
	}
});

function popupQueueText({text, showMs = 3000, danger = false}) {
	popups.push({
		type: "default", text: text, showMs: showMs, blendIn: true, backdrop: false,
		danger: danger
	});
	setTimeout(showPopup, 1);
}

function popupQueueSave(text, onConfirm) {
	popupHide();
	popups.push({
		type: "confirm", text: text, showMs: 0, blendIn: false, backdrop: true,
		danger: false, buttonText: 'Save', title: 'Save', onConfirm: onConfirm
	});
	setTimeout(showPopup, 1);
}

function popupQueueDelete(text, onConfirm) {
	popupHide();
	popups.push({
		type: "confirm", text: text, showMs: 0, blendIn: false, backdrop: true,
		danger: true, buttonText: 'Delete', title: 'Delete', onConfirm: onConfirm
	});
	setTimeout(showPopup, 1);
}

function showPopup() {
	if (popups.length === 0) return;
	if (popupIsShowing) return;
	popupIsShowing = true;
	const popupData = popups.shift();

	let backdrop = null;
	if (popupData.backdrop) backdrop = popupCreateBackdrop();

	let popup = popupCreate(popupData);

	setTimeout(function () {
		popup.classList.remove('hide');
		if (backdrop != null) {
			backdrop.classList.remove('hide');
		}
	}, 1);
	if (popupData.showMs !== 0) {
		popupHideTimeout = setTimeout(popupHide, popupData.showMs);
	}
}

function popupHide() {
	clearTimeout(popupHideTimeout);
	if (!popupIsShowing) return;
	if (popupIsDisposing) return;
	popupIsDisposing = true;
	const popup = document.getElementById(popupId);
	popup.classList.add('hide');
	const backdrop = document.getElementById(popupIdBack);
	if (backdrop != null) backdrop.classList.add('hide');

	setTimeout(function () {
		popup.remove();
		if (backdrop != null) backdrop.remove();
		popupIsDisposing = false;
		popupIsShowing = false;
		showPopup(); // show next if available
	}, 520); // Zeit für Ausblend-Animation
}

function popupCreate(popupData) {
	const original = document.getElementById(popupTemplate + popupData.type);
	let popup;
	if (original != null) {
		popup = original.cloneNode(true);
		const contentElement = popup.querySelector('#' + popupContent);
		contentElement.textContent = popupData.text;
		const btn = popup.querySelector('#popup-btn');
		if (popupData.danger) {
			btn.classList.add('danger-btn');
		}
		btn.textContent = popupData.buttonText;
		btn.addEventListener('click', function () {
			popupHide();
			if (popupData.onConfirm != null) {
				popupData.onConfirm();
			}
		});
		const title = popup.querySelector('#popup-title');
		title.textContent = popupData.title;
	} else {
		popup = document.createElement('div');
		popup.textContent = popupData.text;
	}
	popup.id = popupId;
	popup.classList.add("popup");
	popup.classList.add(popupData.type);
	if (popupData.danger) {
		popup.classList.add('danger');
	}
	if (popupData.blendIn) {
		popup.classList.add('hide');
	}
	document.body.appendChild(popup);
	return popup;
}

function popupCreateBackdrop() {
	let backdrop = document.createElement('div');
	backdrop.id = popupIdBack;
	backdrop.classList.add("backdrop");
	backdrop.classList.add('hide');
	backdrop.addEventListener('click', popupHide);
	document.body.appendChild(backdrop);
	return backdrop;
}
