jQuery(document).ready(function($) {
	var flashes = $.parseJSON($('body').attr('data-flashes'));
	$.each(flashes, function(i, flash) {
		if (isSet(flash.type)) {
			$.my.notify(flash.message, flash.type);
		} else {
			$.my.notify(flash.message);
		}
	});

	/** refresh session request */
	var refreshUrl = $('body').attr('data-refreshurl');
	if (isSet(refreshUrl)) {
		var refreshSession = function () {
			$.post(refreshUrl, function (data) {
				console.log('platnost session byla prodloužena');
			});
			$.post(refreshUrl, function (data) {
				var json = $.my.checkResponse(data);
				if (isSet(json.refresh) && json.refresh) {
					location.reload();
				} else {
					console.log('platnost session byla prodloužena');
				}
			});
		};

		// refresh every 10 minutes
		setInterval(refreshSession, 600000);
	}

	window.initLightbox = function() {
		$('.article').not('.enabled').each(function (index, article) {
			var articleID = $(article).attr('data-id');
			$(article).find('a').has('img').each(function (i, a) {
				var $a = $(a);
				if ($a.hasClass('imgLink') && !$a.hasClass('imgLightbox')) return;
				if (!isSet($a.attr('title'))) {
					var $img = $(this).children('img');
					$a.attr('title', $img.attr('alt'));
				}
				$a.attr('data-gallery', 'article-' + articleID);
			});
		});
	}
	window.removeLightbox = function() {
		$('.article.enabled [data-gallery]').removeAttr('data-gallery');
	}
	window.initLightbox();



	// show photo automatically
	$(window).bind("load", function() {
		setTimeout(function(){
			$('.photosContainer .showThisPhoto:first').click();
		}, 500);
	});



	//if ($( window ).width() >= 1200 ) {
	//	// animace top
	//	$.extend($.expr[':'], {
	//		transparent: function (elem, i, attr) {
	//			return ( $(elem).css("opacity") === "0" );
	//		},
	//		opaque: function (elem, i, attr) {
	//			return ( $(elem).css("opacity") === "1" );
	//		}
	//	});
	//	var $an = $('#animace');
	//	$an.find("img").css("visibility", "visible");
	//	$an.find("img.hidden").css("opacity", "0.0");
	//	$an.find("img").removeClass('hidden');
	//	if ($an.find("img").length > 0) {
	//		var changePhoto = function () {
	//			$an.find("img:opaque").css({"z-index": "2"}).fadeTo(1500, 0.0);
	//			// selector pro vybrani prvniho nasledujiciho sourozence
	//			if ($an.find("img:opaque ~ img:transparent:first").length) {
	//				$an.find("img:opaque ~ img:transparent:first").css({"z-index": "1"}).fadeTo(1000, 1.0);
	//			} else {
	//				// vybrani prvniho sourozence
	//				$an.find("img:transparent:first").css({"z-index": "1"}).fadeTo(1000, 1.0);
	//			}
	//		}
	//		window.setInterval(function () {
	//			changePhoto();
	//		}, 6000);
	//	}
	//}
});