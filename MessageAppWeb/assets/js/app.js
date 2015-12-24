var URL_PREFIX = "http://message-list.appspot.com";
var SCROLL_THRESHOLD = 600;
var LIMIT = 50;

var url = URL_PREFIX + "/messages?limit=" + LIMIT;
var pageToken = null;
var messagesHolder = document.getElementById("messagesHolder");
var loadingPanel = document.getElementById("loading-panel");
var cardTemplate = '<div class="card">'+
	'<img class="author-avatar" src="{0}">'+
	'<div class="author-holder">'+
	'<div class="author-name">{1}</div>'+
	'<div class="message-date">{2}</div>'+
	'</div>'+
	'<div class="message-content">{3}</div>'+
	'</div>';

	var loading = false;

function Message(msg) {
	this.author = {};
	this.author.name = msg.author.name;
	this.author.photoUrl = msg.author.photoUrl;
	this.content = msg.content;
	this.updated = msg.updated;
}

function handleMessages(responseText) {	
	var response = JSON.parse(responseText);
	pageToken = response.pageToken;

	for(var i = 0; i < response.messages.length; i++) {
		var msg = new Message(response.messages[i]);
		var messageElm = document.createElement("DIV");
		messageElm.setAttribute("class", "card-holder");
		messageElm.innerHTML = cardTemplate.replace("{0}", URL_PREFIX + msg.author.photoUrl)
									.replace("{1}", msg.author.name)
									.replace("{2}", msg.updated)
									.replace("{3}", msg.content);		
		messagesHolder.appendChild(messageElm);
	}
}

function makeAjaxCall(url, callback){
	var request = new XMLHttpRequest();
	request.open("GET", url, true);
	loading = true;
	loadingPanel.style.display = "block";	
	request.send(null);			

	request.onreadystatechange = function() {
	    if (request.readyState === 4) {
	      	if (request.status === 200) {	      		
			 	callback(request.responseText);
			 	loading = false;
			 	loadingPanel.style.display = "none";
	      	} else {
	       		console.log(request.status);
	       		loading = false;
	       		loadingPanel.style.display = "none";
	      	}
	    }
	}
}

function _generateUrl() {
	if(pageToken) {
		return url + "&pageToken=" + pageToken;
	} else {
		return url;
	}
}

window.onload = function() {
	window.addEventListener("scroll", function(e){				
		if (document.body.scrollHeight - (document.body.scrollTop + window.innerHeight) < SCROLL_THRESHOLD) {
			if(!loading)
				makeAjaxCall(_generateUrl(), handleMessages);
	    }
	});	
	makeAjaxCall(_generateUrl(), handleMessages);
}