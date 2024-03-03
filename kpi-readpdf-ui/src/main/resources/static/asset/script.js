//jQuery time
var current_fs, next_fs, previous_fs; //fieldsets
var left, opacity, scale; //fieldset properties which we will animate
var animating; //flag to prevent quick multi-click glitches
const path = window.location.protocol + '//' + window.location.host + window.location.pathname

$(document).ready(function () {
		let token = getParam("p");
		document.getElementById("token").value = token;
	}
)

$("#sendotp").click(function(){
	current_fs = $(this).parent();
	next_fs = $(this).parent().next();
	next_fs.show();
	current_fs.hide();

	const data = { token: document.getElementById("token").value };
	fetch(path+ "sendOtp", {
		method: 'POST', // or 'PUT'
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(data),
	})
	.then(response => response.json())
	.then(data => {
		document.getElementById("token").value = data.data.token
	})
	.catch((error) => {
		console.error('Error:', error);
	});

});

$(".previous").click(function(){
	current_fs = $(this).parent();
	previous_fs = $(this).parent().prev();

	//de-activate current step on progressbar
	$("#progressbar li").eq($("fieldset").index(current_fs)).removeClass("active");

	//show the previous fieldset
	previous_fs.show();
	current_fs.hide();
});

$("#submit").click(function() {
	const data = {
		token: document.getElementById("token").value,
		otp: document.getElementById("otp").value
	};
	fetch(path + "pdf", {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(data),
	})
	.then(response => {
		if (response.ok) {
			return response.blob().then(function(myBlob) {
				var objectURL = URL.createObjectURL(myBlob);
				location.assign(objectURL);
			});
		} else {
			return response.json().then(function(jsonError) {
				console.error('Error:', jsonError);
				showError(jsonError.msg)
			});
		}
	})
	.catch((error) => {
		console.error('Err:', error);
	});
})

function showError(msg) {
	let tag = document.getElementById("error")
	tag.style.display = ""
	tag.innerText = msg
}


function getParam(param) {
	let url_string = window.location.href
	let url = new URL(url_string);
	return url.searchParams.get(param);
}


