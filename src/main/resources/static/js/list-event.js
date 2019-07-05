window.onload = function() {
	populateEvents();
	configureInfiniteScroll();
	configureInfiniteScrollMobile();
	setFilter();
};

const url = "/events/rest/list";
var page = 0;
var filter = 0;
var name = "";

function populateEvents() {
	$.getJSON(url + "?name=" + name + "&label=" + filter + "&page=" + page,
			function(data) {
				$('#load').hide();
				if (data.length > 0) {
					$.each(data, function(key, val) {

						var item = $("#item-event-default").html();

						item = item.replace(/{ID}/g, val.id);
						item = item.replace(/{NAME}/g, val.name);
						item = item.replace(/{IMG}/g, "/events/uploads/"
								+ val.img);
						item = item.replace(/{AMOUNT}/g,
								currencyFormat(val.amount));
						item = item.replace(/{DATE}/g, moment(val.createAt)
								.locale('es').format('LL'));
						item = item.replace(/{LABEL}/g, val.label.name);
						item = item.replace(/{COLOR}/g, val.label.color);

						$("#list-events").append(item);

					});
				} else {
					page = -1;
					$('#nomore').show();
				}
			});
}

function configureInfiniteScroll() {
	var win = $(window);
	win.on({
	    'touchmove': function(e) { 
	    	alert("Scroll touch");
	    	initPopulate();
	    }});
	
	win.scroll(function() {
		alert("Scroll normal");
		initPopulate();
	});
}

function configureInfiniteScrollMobile() {
	$(document).on("scrollstop", function(e) {

		/* active page */
		var activePage = $.mobile.pageContainer.pagecontainer("getActivePage"),

		/* window's scrollTop() */
		scrolled = $(window).scrollTop(),

		/* viewport */
		screenHeight = $.mobile.getScreenHeight(),

		/* content div height within active page */
		contentHeight = $(".ui-content", activePage).outerHeight(),

		/* header's height within active page (remove -1 for unfixed) */
		header = $(".ui-header", activePage).outerHeight() - 1,

		/* footer's height within active page (remove -1 for unfixed) */
		footer = $(".ui-footer", activePage).outerHeight() - 1,

		/* total height to scroll */
		scrollEnd = contentHeight - screenHeight + header + footer;

		/*
		 * if total scrolled value is equal or greater than total height of
		 * content div (total scroll) and active page is the target page (pageX
		 * not any other page) call addMore() function
		 */
		if (activePage[0].id == "pageX" && scrolled >= scrollEnd) {
			alert("Scroll special");
			initPopulate();
		}
	});
}

function initPopulate() {
	var win = $(window);
	alert(""+$(document).height() +" - "+win.height() +" - "+win.scrollTop());
	if ($(document).height() - win.height() == win.scrollTop()) {
		if (page >= 0) {
			$('#load').show();
			page++;
			populateEvents();
		}
	}
}

function search() {
	var input = document.getElementById("search");
	name = input.value;
	resetList();
}

function setFilter() {
	$("select#filter").change(function() {
		window.filter = $(this).children("option:selected").val();
		resetList();
	});
}

function resetList() {
	$('#nomore').hide();
	page = 0;
	$("#list-events").empty();
	populateEvents();
}

function currencyFormat(num) {
	return num.toFixed(2).replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,');
}
