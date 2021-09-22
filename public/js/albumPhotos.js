jQuery(document).ready(function($) {
	$(window).load(function () {
		$("#photosLoader").slideUp(300, function() {
			$("#photosContainer").animate({opacity:'1.0'}, 300, function() {
				$("#photosContainer > li > ul").animate({'margin-left':'0'}, 300);
			});
		});

		$("#orderAutoSaveToggle").on("click", function(event) {
			event.preventDefault();
			if (!$(this).hasClass("active")) {
				$(this).addClass("active");
			} else {
				$(this).removeClass("active");
				$(this).blur();
			}
		});

		var saveOrder = function($photosContainer, $orderSaveBtn, $saveOrderInfo) {
			$photosContainer.sortable( "disable" );

			var url = $("#photosContainer").attr("data-save-order-url");
			var order = $photosContainer.sortable( "toArray" );
			// id:order pairs (id must be "photo_"+number)
			var orderObject = {};
			for (var i = 0; i < order.length; ++i) {
				orderObject[order[i].slice( 6 )] = i+1;
			}
			var orderEncoded = JSON.stringify(orderObject);

			var $loaderEl = $('<span class="circleLoader glyphicon glyphicon-cog glyphicon-cog-animate" title="Probíhá požadavek..."></span>');
			$orderSaveBtn.after($loaderEl);
			$saveOrderInfo.html('<span class="circleLoader glyphicon glyphicon-cog glyphicon-cog-animate" title="Probíhá požadavek..."></span>');
			$.ajax({
				type: "POST",
				url: url,
				data:{serializedOrder:orderEncoded},
				success: function(response){
					$photosContainer.sortable( "enable" );
					var json = $.my.checkResponse(response);
					if (isSet(json.status) && json.status) {
						// here update lightbox if needed (blueimp does it automatically)
						window.onbeforeunload = null;
						$orderSaveBtn.removeClass('btn-success');
						// info
						$.my.notify(json.msg, "success");
						$saveOrderInfo.html(json.msg).addClass("ajaxSaveOrderInfoOK");
					} else {
						$.my.notify(json.msg, "error");
						$saveOrderInfo.html(json.msg).addClass("ajaxSaveOrderInfoError");
					}
					$loaderEl.remove();
				},
				error: function() {
					$loaderEl.remove();
					$saveOrderInfo.html('Došlo k chybě při komunikaci se serverem.').addClass("ajaxSaveOrderInfoError");
					$.my.notify("Nepodařilo se spojit se serverem, zkuste to za chvíli znovu.", "fatalError");
					$photosContainer.sortable( "refreshPositions" );
					$photosContainer.sortable( "enable" );
				}
			});
		};

		$( "#photosContainer" ).sortable({
			placeholder: "photo placeholder",
			update : function () {
				$orderSaveBtn = $('#orderSaveBtn');
				if ($("#orderAutoSaveToggle").hasClass("active")) {
					saveOrder($( "#photosContainer" ), $orderSaveBtn, $('span.ajaxSaveOrderInfo'));
				} else {
					$orderSaveBtn.addClass("btn-success");
					window.onbeforeunload = function() {
						return "Aktuální pořadí fotek nebylo uloženo. Chcete přesto pokračovat?";
					};
				}
			}
		});

		$('#orderSaveBtn').on("click", function(event) {
			event.preventDefault();
			$(this).blur();
			saveOrder($( "#photosContainer" ), $(this), $('span.ajaxSaveOrderInfo'));
		});
	});


	$("#photosContainer ul.photoMenu li a[data-make=action], #photosContainer ul.photoMenu li.show a span").tooltip({placement:'right'});

	var updatePhotoDescription = function(describeBtn, action, photoLi, url, basePath, callback) {
		$describeBtn = $(describeBtn);
		var textareaWithLabel = '<label for="photoDescriptionTextarea">Popisek:</label><br><textarea name="description" id="photoDescriptionTextarea" data-photodescription-id="new" class="description">' + $describeBtn.attr('data-description') + '</textarea>';
		var $content = $('<div class="describePhotoDialog"><form><fieldset>' + textareaWithLabel + '</fieldset></form><img src="' + $.my.extractCssUrl($describeBtn.parent().parent().parent().css('background-image')) + '" alt="' + $(photoLi).attr("data-photo-name") + '" /></div>');
		var descriptionDialog = BootstrapDialog.show({
			title: 'Úprava popisu fotky',
			message: $content,
			cssClass: 'photoDescriptionDialog',
			buttons: [{
				label: 'Uložit',
				cssClass: 'btn-primary',
				action: function(dialog){
					var description = $('#photoDescriptionTextarea').val();
					dialog.setMessage($('<div class="dialogLoader"><span>Ukládám...</span><span class="circleLoaderContainer"><span class="circleLoader largerLoader glyphicon glyphicon-cog glyphicon-cog-animate" title="Probíhá požadavek..."></span></span></div>'));
					dialog.enableButtons(false);
					$.ajax({
						type: "POST",
						url: url,
						data:{description:description},
						success: function(response){
							dialog.close();
							var json = $.my.checkResponse(response);
							if (isSet(json.status) && json.status) {
								if (json.description !== undefined) {
									$describeBtn.attr("data-description", json.description);
									if (json.description !== undefined) {
										$(photoLi).children('ul').children('li.show').children('a').attr("title", json.description);
									} else {
										$(photoLi).children('ul').children('li.show').children('a').removeAttr("title");
									}
								}
								$.my.notify(json.msg, "success");
								if (isSet(callback)) {
									if (json.description !== undefined) {
										callback(json.description);
									} else {
										callback('');
									}
								}
							} else {
								$.my.notify(json.msg, "error");
							}
						},
						error: function() {
							dialog.close();
							$.my.notify("Nepodařilo se spojit se serverem, zkuste to za chvíli znovu.", "fatalError");
						}
					});
				}
			},{
				label: 'Storno',
				action: function (dialog) {
					dialog.close();
				}
			}]
		});
		window.dialogs["description"] = descriptionDialog;

		descriptionDialog.onShown(function(event) {
			$('div.describePhotoDialog > form > fieldset > textarea:first').focus();
		});
	};



	// on "photo menu action item" click
	$("#photosContainer ul li a[data-make=action]").click( function(e) {
		e.preventDefault();
		var that = this;
		var action = $(that).parent().attr("class");
		var photoLi = $(that).parent().parent().parent()/*.parent()*/;
		var url = $(that).attr("href");
		var basePath = window.basePath;

		if (action === "rotateLeft" || action === "rotateRight") {

			$.ajax({
				type: "POST",
				url: url,
				success: function(response){
					var json = $.my.checkResponse(response);

					// refresh photo&thumb
					if (json.status == "ok") {
						$(that).closest('ul').children('li.show').children('a').attr("href", json.photo_path );
						$(that).parent().parent().parent().css("background-image", "url('" + json.thumb_path + "')");
						$.my.notify(json.msg, "success");
					} else {
						$.my.notify(json.msg, "error");
					}
				},
				error: function() {
					$.my.notify("Nepodařilo se spojit se serverem, zkuste to za chvíli znovu.", "fatalError");
				}
			});

		} else if (action === "delete") {

			$.my.confirmDialogWC(
				'confirmPhoto',
				'warning',
				'Potvrzení',
				'Opravdu chcete fotografii smazat?<img src="' + $.my.extractCssUrl($(that).parent().parent().parent().css('background-image')) + '" alt="' + $(photoLi).attr("data-photo-name") + '" style="margin:10px 20px; box-shadow:0px 0px 10px 3px #f0ad4e;" />',
				'storno',
				function() {
					window.dialogs["confirmPhoto"].setMessage($('<p style="text-align:center;"><span>Probíhá mazání</span><br/><br/><span class="circleLoader largerLoader glyphicon glyphicon-cog glyphicon-cog-animate" title="Probíhá požadavek..."></span></p>'));
					$.ajax({
						type: "POST",
						url: url,
						success: function(response){
							window.dialogs["confirmPhoto"].close();
							var json = $.my.checkResponse(response);
							if (isSet(json.status) && json.status) {
								// remove list item id (it would be good to remove that element totally, but then it wouldn't be so easy to make such a nice animation)
								$(photoLi).removeAttr("id").attr("deleted", "deleted");
								$(that).parent().parent().parent().css("background-image", "none").html('<span class="deleted">Fotka byla smazána.</span>').delay(2000).fadeOut(600);
								$.my.notify(json.msg, "success");
							} else {
								$.my.notify(json.msg, "error");
							}
							// here update lightbox if needed (bluimp does it automatically)
							// update info
							$("#photosCount").text($("#photosContainer > li[data-photo-id][deleted!=\"deleted\"]").size());
						},
						error: function() {
							window.dialogs["confirmPhoto"].close();
							$.my.notify("Nepodařilo se spojit se serverem, zkuste to za chvíli znovu.", "fatalError");
						}
					});
				}
			);

		} else if (action === "describe") {
			blueimpGalleryControls.disable();
			updatePhotoDescription(that, action, photoLi, url, basePath, function() {
				blueimpGalleryControls.enable();
			});
		}

	});



	var blueimpGalleryControls = {
		disable: function() {
			var gallery = $('#blueimp-gallery').data('gallery');
			if (isSet(gallery) && isSet(gallery.options)) {
				var options = gallery.options;
				// Toggle the controls on pressing the Return key:
				options.toggleControlsOnReturn = false;
				// Toggle the automatic slideshow interval on pressing the Space key:
				options.toggleSlideshowOnSpace = false;
				// Navigate the gallery by pressing left and right on the keyboard:
				options.enableKeyboardNavigation = false;
			}
		},
		enable: function() {
			var gallery = $('#blueimp-gallery').data('gallery');
			if (isSet(gallery) && isSet(gallery.options)) {
				var options = gallery.options;
				// Toggle the controls on pressing the Return key:
				options.toggleControlsOnReturn = true;
				// Toggle the automatic slideshow interval on pressing the Space key:
				options.toggleSlideshowOnSpace = true;
				// Navigate the gallery by pressing left and right on the keyboard:
				options.enableKeyboardNavigation = true;
			}
		}
	};

	var photoDescriptionEdit = {
		init: function(photoID, descriptionElement, slide) {
			var photo = $('#photo_'+photoID);
			$(descriptionElement).css('cursor', 'pointer');
			if ($(descriptionElement).html() == '') {
				$(descriptionElement).html('<span class="glyphicon glyphicon-pencil" title="Přidat popis"></span>');
			} else {
				$(descriptionElement).attr({ 'title': 'Upravit popis' });
			}
			$(descriptionElement).off('click').on('click', function() {
				blueimpGalleryControls.disable();
				var that = photo.find('.photoMenu .describe > a');
				var action = $(that).parent().attr("class");
				var photoLi = photo;
				var url = $(that).attr("href");
				var basePath = window.basePath;
				updatePhotoDescription(that, action, photoLi, url, basePath, function(newDescription) {
					$(descriptionElement).text(newDescription);
					if ($(descriptionElement).html() == '') {
						$(descriptionElement).html('<span class="glyphicon glyphicon-pencil" title="Přidat popis"></span>');
					} else {
						$(descriptionElement).attr({ 'title': 'Upravit popis' });
					}
					$(slide).children('img.slide-content').attr('title', newDescription);

					blueimpGalleryControls.enable();
				});
			});
		}
	};



	$('#albumPhotosButtonsContainer').on('click', 'a[data-albumPhotos-delete]', function(event){
		event.preventDefault();
		var location = $(this).attr("data-href");
		$.my.confirmDialogWC(
			'confirm',
			'danger',
			'Potvrzení',
			'Opravdu chcete všechny fotky v albu <strong>'+$(this).attr("data-name")+'</strong> smazat?',
			'storno',
			function(){
				window.location.replace(location);
			}
		);
	});



	$('#blueimp-gallery')
		.data({
			preloadRange: 8,
			continuous: false,
			// Toggle the controls on pressing the Return key:
			toggleControlsOnReturn: true,
			// Toggle the automatic slideshow interval on pressing the Space key:
			toggleSlideshowOnSpace: true,
			// Navigate the gallery by pressing left and right on the keyboard:
			enableKeyboardNavigation: true
		})
		.toggleClass('blueimp-gallery-controls', true);

//        $('body').on('focusin', '.commentsContainer form, .modal-dialog', function(){
//            blueimpGalleryControls.disable();
//        });
//        $('body').on('focusout', '.commentsContainer form, .modal-dialog', function(){
//            blueimpGalleryControls.enable();
//        });
//
//        $('body').on('mouseenter', '.bootstrap-dialog', function(){
//            blueimpGalleryControls.disable();
//        });
//        $('body').on('mouseleave', '.bootstrap-dialog', function(){
//            blueimpGalleryControls.enable();
//        });

	$('#blueimp-gallery').on('open', function() {
		// when the user opens the gallery, we don't need the "remove" animation and this also fix problems with wrong description
		$('#photosContainer li[deleted]').remove();
	});
	$('#blueimp-gallery').on('slide', function (event, index, slide) {
			index++;
			var photo = $('#photosContainer li[data-photo-id]:nth-child(' + index + ')');
			if (photo.length) {
				photoDescriptionEdit.init(photo.attr('data-photo-id'), '#blueimp-gallery > .title', slide);
			}
		})
		.on('close', function (event) {
			$('#blueimp-gallery .commentsContainer').html();
		});
});