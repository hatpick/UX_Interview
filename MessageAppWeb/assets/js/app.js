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

function timeAgo(time) {
    switch (typeof time) {
        case 'number':
            break;
        case 'string':
            time = +new Date(time);
            break;
        case 'object':
            if (time.constructor === Date) time = time.getTime();
            break;
        default:
            time = +new Date();
    }
    var time_formats = [
        [60, 'seconds', 1],
        [120, '1 minute ago', '1 minute from now'],
        [3600, 'minutes', 60],
        [7200, '1 hour ago', '1 hour from now'],
        [86400, 'hours', 3600],
        [172800, 'Yesterday', 'Tomorrow'],
        [604800, 'days', 86400],
        [1209600, 'Last week', 'Next week'],
        [2419200, 'weeks', 604800],
        [4838400, 'Last month', 'Next month'],
        [29030400, 'months', 2419200],
        [58060800, 'Last year', 'Next year'],
        [2903040000, 'years', 29030400],
        [5806080000, 'Last century', 'Next century'],
        [58060800000, 'centuries', 2903040000]
    ];
    var seconds = (+new Date() - time) / 1000,
        token = 'ago',
        list_choice = 1;

    if (seconds == 0) {
        return 'Just now'
    }
    if (seconds < 0) {
        seconds = Math.abs(seconds);
        token = 'from now';
        list_choice = 2;
    }
    var i = 0,
        format;
    while (format = time_formats[i++])
        if (seconds < format[0]) {
            if (typeof format[2] == 'string')
                return format[list_choice];
            else
                return Math.floor(seconds / format[2]) + ' ' + format[1] + ' ' + token;
        }
    return time;
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
									.replace("{2}", timeAgo(msg.updated))
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